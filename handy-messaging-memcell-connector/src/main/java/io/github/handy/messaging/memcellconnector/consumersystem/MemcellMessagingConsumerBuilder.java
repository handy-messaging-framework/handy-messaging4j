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

package io.github.handy.messaging.memcellconnector.consumersystem;

import akka.actor.ActorRef;
import io.github.handy.messaging.interfaces.Consumer;
import io.github.handy.messaging.interfaces.ConsumerBuilder;
import io.github.handy.messaging.memcellconnector.Constants;

import java.util.Map;

/**
 * Builder class for Memcell Messaging Consumer
 */
public class MemcellMessagingConsumerBuilder implements ConsumerBuilder {

    Map<String, Object> consumerProps;

    /**
     * Set the consumer properties
     * @param properties - Map of properties
     * @return - ConsumerBuilder
     */
    @Override
    public ConsumerBuilder setConsumerProperties(Map<String, Object> properties) {
        this.consumerProps = properties;
        return this;
    }

   /**
     * Get the queue name
     * @return - String representing the queue name
     */
    @Override
    public String getQueueName() {
        return this.consumerProps.get(Constants.QUEUE_NAME).toString();
    }

    /**
     * Get the message type class
     * @return - String representing the message type class
     */
    @Override
    public String getMessageTypeClass() {
        return this.consumerProps.get(Constants.MESSAGE_TYPE_CLASS).toString();
    }

    /**
     * Get the name of the Memcell messaging instance
     * @return - String representing the Memcell messaging instance
     */
    public String getMemcellMessagingInstance(){
        return this.consumerProps.get(Constants.MESSAGE_SERVICE).toString();
    }

    /**
     * Get the application id
     * @return - String representing the application id
     */
    public String getApplicationId(){
        return this.consumerProps.get(Constants.APPLICATION_ID).toString();
    }

    /**
     * Get the consumer actor
     * @return - ActorRef representing the consumer actor
     */
    @Override
    public ActorRef getConsumerActor() {
        return (ActorRef) this.consumerProps.get(Constants.CONSUMER_ACTOR);
    }

    /**
     * Build the consumer
     * @return - Consumer
     */
    @Override
    public Consumer build() {
        return new MemcellMessagingConsumerSystem(this);
    }
}
