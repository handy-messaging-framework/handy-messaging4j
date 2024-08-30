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

package com.eclectique.messaging.kafkaconnector.consumersystem;

import akka.actor.ActorRef;
import com.eclectique.messaging.interfaces.Consumer;
import com.eclectique.messaging.interfaces.ConsumerBuilder;
import com.eclectique.messaging.kafkaconnector.Constants;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import java.util.Arrays;
import java.util.Map;

/**
 * KafkaConsumerBuilder class is used to build a KafkaConsumerSystem Instance
 */
public class KafkaConsumerBuilder implements ConsumerBuilder {

    private Map<String, Object> consumerProperties;
    private String queueName;
    KafkaConsumer kafkaConsumer;

    /**
     * Set the consumer properties
     * @param properties - Map of properties
     * @return - ConsumerBuilder
     */
    @Override
    public ConsumerBuilder setConsumerProperties(Map<String, Object> properties) {
        this.consumerProperties = properties;
        return this;
    }

    private void extractBuilderProperties(){
        this.queueName = this.consumerProperties.get(Constants.QUEUE_NAME).toString();

    }

    private void processConsumerProperties(){
        this.extractBuilderProperties();
        this.consumerProperties.remove(Constants.QUEUE_NAME);
        this.consumerProperties.put(Constants.KAFKA_KEY_DESERIALIZER_PROP, "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        this.consumerProperties.put(Constants.KAFKA_VALUE_DESERIALIZER_PROP, "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        this.consumerProperties.put(Constants.KAFKA_AUTO_COMMIT_PROP, false);
    }



    /**
     * Get the queue name
     * @return - Queue name
     */
    @Override
    public String getQueueName() {
        return this.queueName;
    }

    /**
     * Get the message type class
     * @return - Message type class
     */
    @Override
    public String getMessageTypeClass() {
        return this.consumerProperties.get(Constants.MESSAGE_TYPE_CLASS).toString();
    }

    /**
     * Get the consumer actor
     * @return - Consumer actor
     */
    @Override
    public ActorRef getConsumerActor() {
        return (ActorRef) this.consumerProperties.get(Constants.CONSUMER_ACTOR);
    }

    /**
     * Build the consumer
     * @return - KafkaConsumerSystem Instance
     */
    @Override
    public Consumer build() {
        this.processConsumerProperties();
        this.kafkaConsumer = new KafkaConsumer(this.consumerProperties);
        this.kafkaConsumer.subscribe(Arrays.asList(this.queueName));
        return new KafkaConsumerSystem(this);
    }
}
