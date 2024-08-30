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

package io.github.handy.messaging.core.consumer;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import io.github.handy.messaging.core.configuration.Profile;
import io.github.handy.messaging.interfaces.MessageHandler;

import java.util.concurrent.FutureTask;

/**
 * MessageChannelRootActorFactory - Factory class to create the root actor for the message channel. The root actor is
 * responsible for creating the subscriber actors and the dispatcher actors for the message channel.
 */
public class MessageChannelRootActorFactory {

    private FutureTask<ActorRef> initializationFuture;
    private static final String CONSUMER_ROOT_ACTOR_TEMPLATE = "CONSUMER-CHANNEL-ROOT-%s-%s";
    private ActorRef createdActor;


    private MessageChannelRootActorFactory(){
        this.initializationFuture = new FutureTask<ActorRef>(()->{
            return this.createdActor;
        });
    }

    /**
     * getMessageChannelRootActor - Method to create the root actor for the message channel
     * @param profile - Profile object for the message channel
     * @param queueName - Name of the queue
     * @param messageTypeClass - Class name of the message type
     * @param messageHandler - Message handler object
     * @param messageConsumerSystem - Actor system object
     * @return - FutureTask object for the root actor which will be completed once the root actor is ready
     */
    public static FutureTask<ActorRef> getMessageChannelRootActor(Profile profile,
                                                      String queueName,
                                                      String messageTypeClass,
                                                      MessageHandler messageHandler,
                                                      ActorSystem messageConsumerSystem){
        MessageChannelRootActorFactory rootActorFactory = new MessageChannelRootActorFactory();
        String rootActorName = String.format(CONSUMER_ROOT_ACTOR_TEMPLATE, profile.getProfileName(), queueName);
        rootActorName = rootActorName.replace('/', '_');
        rootActorName = rootActorName.replace('+', '_');
        messageConsumerSystem.actorOf(MessageChannelRootActor.getActorProperties(profile,
                queueName,
                messageTypeClass,
                messageHandler,
                rootActorFactory::onInitilizationCallback), rootActorName);

        return rootActorFactory.initializationFuture;

    }



    private void onInitilizationCallback(ActorRef initializedActor){
        this.createdActor = initializedActor;
        this.initializationFuture.run();
    }


}
