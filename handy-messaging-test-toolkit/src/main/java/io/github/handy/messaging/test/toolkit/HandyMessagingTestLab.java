/**
 * MIT License
 *
 * Copyright (c) 2024 Aron Sajan Philip
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.handy.messaging.test.toolkit;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import io.github.handy.messaging.core.configuration.ConfigurationBootstrap;
import io.github.handy.messaging.core.configuration.ConsumerProperties;
import io.github.handy.messaging.core.configuration.Profile;
import io.github.handy.messaging.core.configuration.ProfileHelper;
import io.github.handy.messaging.core.consumer.MessageConsumingSystem;
import io.github.handy.messaging.memcell.clients.MemcellMessagingAdministrator;
import io.github.handy.messaging.memcell.types.responses.CommandExecutionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class coordinating the analysis of messages within one or more queues.
 * These analysis can be verified in test cases
 */
public class HandyMessagingTestLab {

    Map<String, Map<String, ActorRef>> messageRetentionActorRegistry;

    MemcellMessagingAdministrator memcelladmin;

    ActorSystem testSystem;

    private HandyMessagingTestLab(HandyMessagingTestLabBuilder builder){
        this.messageRetentionActorRegistry = builder.messageRetentionActorRegistry;
        this.testSystem = builder.handyMessagingTestSystem;
        this.memcelladmin = builder.memcellAdmin;
    }

    /**
     * Function sets up analysis probe instance used for analysis of messages within a queue.
     * @param dataCollectionDelayMs Time in milliseconds to wait for all data to reach the queue before analysis
     * @param profile Profile associated with the queue
     * @param queue Name of the queue, whose messages need to be analyzed
     * @return Message Probe instance which contains the tools needed for detailed analysis of the messages collected in the queue
     */
   public MessageProbe getAnalysisProbe(long dataCollectionDelayMs, String profile, String queue){
       ActorRef messageRetentionActor = this.messageRetentionActorRegistry.get(profile).get(queue);
        return new MessageProbe.MessageProbeBuilder(messageRetentionActor).startNewAnalysis(dataCollectionDelayMs);
   }

    /**
     * Builder class used to build the HandyMessagingTestLab instance
     */
   public static class HandyMessagingTestLabBuilder{

       Logger LOGGER = LoggerFactory.getLogger(HandyMessagingTestLabBuilder.class);
        private List<AnalysisQueueInfo> analysisQueues;
        private MemcellMessagingAdministrator memcellAdmin;

       private ActorSystem handyMessagingTestSystem;

       Map<String, Map<String, ActorRef>> messageRetentionActorRegistry;

       Map<String, Profile> profileMap;

       public HandyMessagingTestLabBuilder(){
           this.analysisQueues = new ArrayList<>();
           this.profileMap = new HashMap<>();
           this.handyMessagingTestSystem = ActorSystem.create("HandyMessagingTestAssist");
           this.memcellAdmin = new MemcellMessagingAdministrator();
           this.messageRetentionActorRegistry = new HashMap<>();
       }

         /**
          * Function adds a listener to the queue for analysis
          * @param profile Profile associated with the queue
          * @param queueName Name of the queue
          * @param messageType Class name of the message type that gets sent to the queue
          * @return HandyMessagingTestLabBuilder instance
          */
       public HandyMessagingTestLabBuilder addListener(String profile, String queueName, String messageType){
            this.analysisQueues.add(new AnalysisQueueInfo(profile, queueName, messageType));
            return this;
       }

       private void buildProfileMap(){
           analysisQueues.forEach(queueInfo -> {
                Profile profile = ProfileHelper.getProfile(queueInfo.getProfileName());
                this.profileMap.put(queueInfo.getProfileName(), profile);
           });
       }


       private void registerQueue(String messagingInstance, String queueName){
           if(!this.memcellAdmin.registerMessagingQueue(queueName, messagingInstance).getCommandExecutionStatus().equals(CommandExecutionStatus.SUCCESS)){
               throw new RuntimeException(String.format("Registering queue % failed for messaging service %s", queueName, messagingInstance));
           }
       }

       private List<String> getMessagingInstances(){
           return this.profileMap.values().stream().map(profile -> {
               return profile.getProducerProperties().getProps().get(Constants.MEMCELL_MESSAGING_INSTANCE).toString();
           }).collect(Collectors.toList());
       }

       private void registerMessagingQueues(){
           this.analysisQueues.forEach(analysisQueueInfo -> {
               Profile profile = this.profileMap.get(analysisQueueInfo.getProfileName());
               String messagingInstance = profile.getProducerProperties().getProps().get(Constants.MEMCELL_MESSAGING_INSTANCE).toString();
               String queue = analysisQueueInfo.getQueueName();
               this.memcellAdmin.registerMessagingQueue(queue, messagingInstance);
           });
       }

       private Profile generateTestConsumerProfile(Profile fromProfile){
           ConsumerProperties messageAnalysisConsumerProps = new ConsumerProperties();
           messageAnalysisConsumerProps.setProps(new HashMap<>(){{
               put(Constants.CONSUMER_MAX_BATCH_MESSAGES, 1);
               put(Constants.CONSUMER_MAX_POLL_DURATION, 1000);
               put(Constants.MEMCELL_APPLICATION_ID, String.format("TESTCONSUMER-%s", fromProfile.getProfileName()));
               put(Constants.MEMCELL_MESSAGING_INSTANCE,  fromProfile.getProducerProperties().getProps().get(Constants.MEMCELL_MESSAGING_INSTANCE).toString());
           }
           });

           return new Profile.ProfileBuilder()
                   .setProfileName(String.format("TEST-%s", fromProfile.getProfileName()))
                   .setSystem(Constants.MEMCELL_MESSAGE_SYSTEM)
                   .setConsumerProperties(messageAnalysisConsumerProps).buildProfile();
       }

       /**
        * Function initializes the test setup
        * @return HandyMessagingTestLab instance
        */
       public HandyMessagingTestLab getTestLab(){

           LOGGER.info("Initializing test setup");
           new ConfigurationBootstrap();
           this.buildProfileMap();
           this.profileMap.values().forEach(profile -> {
               if(!profile.getSystem().equals(Constants.MEMCELL_MESSAGE_SYSTEM)){
                   throw new RuntimeException(String.format("Profile %s is not using a memcell messaging service", profile.getProfileName()));
               }
           });

           getMessagingInstances().forEach(messagingInstance -> {
               this.memcellAdmin.registerMessagingService(messagingInstance);
           });

           this.registerMessagingQueues();


           this.analysisQueues.forEach(queueInfo -> {
               Profile associatedProfile = this.profileMap.get(queueInfo.getProfileName());
               ActorRef retentionActor = this.handyMessagingTestSystem.actorOf(MessageRetentionBufferActor.getActorProperties(),
                       String.format("RETENTION-%s-%s", queueInfo.getProfileName(), queueInfo.getQueueName()));
               this.messageRetentionActorRegistry.putIfAbsent(queueInfo.getProfileName(), new HashMap<>());
               this.messageRetentionActorRegistry.get(queueInfo.getProfileName()).put(queueInfo.getQueueName(), retentionActor);
               MessageReceiver messageHandler = new MessageReceiver(retentionActor);
               MessageConsumingSystem.getInstance().setupConsumer(generateTestConsumerProfile(associatedProfile),
                       queueInfo.getQueueName(),
                       queueInfo.getMessageType(),
                       messageHandler);
           });

           return new HandyMessagingTestLab(this);

       }

   }


}
