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

package com.eclectique.messaging.core.consumer;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.eclectique.messaging.core.configuration.Profile;
import com.eclectique.messaging.core.configuration.ProfileHelper;
import com.eclectique.messaging.interfaces.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * The MessageConsumingSystem class is a singleton class that is responsible for setting up the message consuming system
 * in the application. It is responsible for creating the Actor System and setting up the message consuming actors
 * in the system.
 */
public class MessageConsumingSystem {

    private ActorSystem messageConsumerSystem;
    private static Optional<MessageConsumingSystem> consumingSystemInstance = Optional.empty();
    private Logger LOGGER = LoggerFactory.getLogger(MessageConsumingSystem.class);

    /**
     * The getInstance method is responsible for returning the singleton instance of the MessageConsumingSystem class.
     * @return MessageConsumingSystem
     */
    public static MessageConsumingSystem getInstance(){
        if(consumingSystemInstance.isEmpty()){
            consumingSystemInstance = Optional.of(new MessageConsumingSystem());
        }
        return consumingSystemInstance.get();
    }

    private MessageConsumingSystem(){
        LOGGER.info("Initialized Messaging Consuming System");
        this.messageConsumerSystem = ActorSystem.create("MessageConsumingSystem");
    }

    /**
     * The setupConsumer method is responsible for setting up the message consuming actor in the system
     * @param profileName String - The name of the profile to be used for setting up the consuming actor
     * @param queueName String - The name of the queue to be used for setting up the consuming actor
     * @param messageTypeClass String - The class name of the message type to be used for setting up the consuming actor
     * @param messageHandler MessageHandler - The message handler that gets invoked when a message is received
     */
    public void setupConsumer(String profileName, String queueName,String messageTypeClass, MessageHandler messageHandler){
       this.setupConsumer(ProfileHelper.getProfile(profileName), queueName, messageTypeClass, messageHandler);
    }

    /**
     * The setupConsumer method is responsible for setting up the message consuming actor in the system
     * @param profile Profile - The profile to be used for setting up the consuming actor
     * @param queueName String - The name of the queue to be used for setting up the consuming actor
     * @param messageTypeClass String - The class name of the message type to be used for setting up the consuming actor
     * @param messageHandler MessageHandler - The message handler that gets invoked when a message is received
     */
    public void setupConsumer(Profile profile, String queueName,String messageTypeClass, MessageHandler messageHandler){

        try {
            ActorRef consumerChannelRoot = MessageChannelRootActorFactory.getMessageChannelRootActor(profile,
                    queueName,
                    messageTypeClass,
                    messageHandler,
                    this.messageConsumerSystem).get();
            consumerChannelRoot.tell(new MessageChannelRootActor.StartConsumerChannel(), ActorRef.noSender());
        } catch (InterruptedException | ExecutionException ex){
            throw new RuntimeException(ex.getMessage());
        }
    }



}
