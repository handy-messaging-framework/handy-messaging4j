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
import com.eclectique.messaging.core.configuration.Profile;
import com.eclectique.messaging.core.Constants;
import  com.eclectique.messaging.interfaces.Consumer;

/**
 * Builder class for creating a MessageConsumer instance
 */
public class MessageConsumerBuilder {

    private String queueName;
    private Profile profile;
    private ActorRef consumerActor;
    private String messageTypeClass;

    /**
     * Set the queue name
     * @param queueName - Name of the queue
     * @return - MessageConsumerBuilder
     */
    public MessageConsumerBuilder setQueueName(String queueName){
        this.queueName = queueName;
        return this;
    }

    /**
     * Set the profile
     * @param profile - Profile instance
     * @return - MessageConsumerBuilder
     */
    public MessageConsumerBuilder setProfile(Profile profile){
        this.profile = profile;
        return this;
    }

    /**
     * Set the consumer actor
     * @param consumerActor - ActorRef instance
     * @return - MessageConsumerBuilder
     */
    public MessageConsumerBuilder setConsumerActor(ActorRef consumerActor){
        this.consumerActor = consumerActor;
        return this;
    }

    /**
     * Set the message type class
     * @param messageTypeClass - Fully qualified class name of the message type
     * @return - MessageConsumerBuilder
     */
    public MessageConsumerBuilder setMessageTypeClass(String messageTypeClass){
        this.messageTypeClass = messageTypeClass;
        return this;
    }

    /**
     * Build the consumer instance
     * @return - Consumer instance
     */
    public Consumer build(){
        this.profile.getConsumerProperties().getProps().put(Constants.QUEUE_NAME, this.queueName);
        this.profile.getConsumerProperties().getProps().put(Constants.CONSUMER_ACTOR, this.consumerActor);
        this.profile.getConsumerProperties().getProps().put(Constants.MESSAGE_TYPE_CLASS, this.messageTypeClass);
        try {
            return ConsumerBuilderFactory.getConsumerBuilder(profile.getSystem(),
                    profile.getConsumerProperties().getProps())
                    .build();
        } catch (Exception ex){
            throw new RuntimeException(ex.getMessage());
        }
    }
}
