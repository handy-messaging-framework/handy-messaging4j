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

package io.github.handy.messaging.core.producer;


import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is a singleton class that holds the mapping of the producer system name to the producer builder class.
 * This class is used by the ProducerFactory to get the builder class for the producer system.
 */
public class ProducerBuilderMap {
    private Map<String, String> builderMap;
    private static Optional<ProducerBuilderMap> mapInstance = Optional.empty();

    private ProducerBuilderMap(){
        builderMap = new ConcurrentHashMap<>();
        this.initializeBuilderMap();
    }

    /**
     * This method returns the instance of the ProducerBuilderMap class.
     * @return ProducerBuilderMap
     */
    public static ProducerBuilderMap getInstance(){
        if(ProducerBuilderMap.mapInstance.isEmpty()) {
            ProducerBuilderMap.mapInstance = Optional.of(new ProducerBuilderMap());
        }
        return ProducerBuilderMap.mapInstance.get();
    }

    private void initializeBuilderMap(){
        this.builderMap.put("kafka", "io.github.handy.messaging.kafkaconnector.producersystem.KafkaProducerBuilder");
        this.builderMap.put("google-pubsub", "io.github.handy.messaging.pubsubconnector.producersystem.PubSubProducerBuilder");
        this.builderMap.put("memcell-mq", "io.github.handy.messaging.memcellconnector.producersystem.MemcellMessagingProducerBuilder");
        this.builderMap.put("mqtt", "io.github.handy.messaging.mqttconnector.producersystem.MqttProducerBuilder");
    }

    /**
     * This method returns the producer builder class for the given producer system name.
     * @param systemName - The producer system name
     * @return String - The producer builder class name
     */
    public String getProducerBuilder(String systemName){
        return this.builderMap.get(systemName);
    }
}
