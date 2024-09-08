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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This class is a singleton class that provides a mapping of consumer systems to their respective builder classes.
 * This class is used by the ConsumerFactory to get the builder class for a particular consumer system.
 */
public class ConsumerBuilderMap {

    private Map<String, String> builderMap;
    private static Optional<ConsumerBuilderMap> instance = Optional.empty();

    private ConsumerBuilderMap(){
        this.builderMap = new HashMap<>();
        this.initializeBuilderMap();
    }

    /**
     * This method returns the singleton instance of the ConsumerBuilderMap class.
     * @return ConsumerBuilderMap instance
     */
    public static ConsumerBuilderMap getInstance(){
        if(instance.isEmpty()){
            instance = Optional.of(new ConsumerBuilderMap());
        }
        return instance.get();
    }

    private void initializeBuilderMap(){
        this.builderMap.put("kafka", "io.github.handy.messaging.kafkaconnector.consumersystem.KafkaConsumerBuilder");
        this.builderMap.put("google-pubsub", "io.github.handy.messaging.pubsubconnector.consumersystem.PubSubConsumerBuilder");
        this.builderMap.put("memcell-mq", "io.github.handy.messaging.memcellconnector.consumersystem.MemcellMessagingConsumerBuilder");
        this.builderMap.put("mqtt", "io.github.handy.messaging.mqttconnector.consumersystem.MqttConsumerBuilder");
    }

    /**
     * This method returns the builder class for a particular consumer system.
     * @param consumerSystem The consumer system for which the builder class is required
     * @return The builder class for the consumer system
     */
    public String getConsumerBuilder(String consumerSystem){
        return this.builderMap.get(consumerSystem);
    }
}
