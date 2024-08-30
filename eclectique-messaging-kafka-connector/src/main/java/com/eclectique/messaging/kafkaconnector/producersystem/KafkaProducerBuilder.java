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

package com.eclectique.messaging.kafkaconnector.producersystem;

import com.eclectique.messaging.interfaces.Producer;
import com.eclectique.messaging.interfaces.ProducerBuilder;
import com.eclectique.messaging.kafkaconnector.Constants;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.Map;

public class KafkaProducerBuilder implements ProducerBuilder {

    private String topicName;

    private KafkaProducer kafkaProducer;

    private Map<String, Object> producerProperties;

    public KafkaProducer getKafkaProducer(){
        return this.kafkaProducer;
    }

    @Override
    public KafkaProducerBuilder setProducerProperties(Map<String, Object> props) {
       this.producerProperties =  props;
       return this;
    }

    @Override
    public String getQueueName() {
        return this.topicName;
    }

    @Override
    public Producer build() {
        this.topicName = this.producerProperties.get(Constants.QUEUE_NAME).toString();
        this.producerProperties.remove(Constants.QUEUE_NAME);
        this.producerProperties.put(Constants.KAFKA_KEY_SERIALIZER_PROP, "org.apache.kafka.common.serialization.ByteArraySerializer");
        this.producerProperties.put(Constants.KAFKA_VALUE_SERIALIZER_PROP, "org.apache.kafka.common.serialization.ByteArraySerializer");
        this.kafkaProducer = new KafkaProducer(this.producerProperties);
        return new KafkaProducerSystem(this);
    }
}
