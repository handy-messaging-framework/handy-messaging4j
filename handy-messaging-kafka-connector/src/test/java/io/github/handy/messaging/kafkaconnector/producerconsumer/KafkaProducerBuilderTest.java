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

package io.github.handy.messaging.kafkaconnector.producerconsumer;

import io.github.handy.messaging.interfaces.Producer;
import io.github.handy.messaging.kafkaconnector.Constants;
import io.github.handy.messaging.kafkaconnector.producersystem.KafkaProducerBuilder;
import io.github.handy.messaging.kafkaconnector.producersystem.KafkaProducerSystem;
import org.apache.kafka.common.KafkaException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import java.util.HashMap;
import java.util.Map;

public class KafkaProducerBuilderTest {

    private static KafkaContainer emulator;
    private String queueName = "testQueue";

    @BeforeClass
    public static void setup() {
        emulator =  new KafkaContainer(DockerImageName.parse("apache/kafka-native:3.8.0"));
        emulator.start();
    }

    @AfterClass
    public static void teardown() {
        emulator.stop();
    }

    private Map<String, Object> getProducerProperties(){
        Map<String, Object> producerProps = new HashMap<>(){{
            put(Constants.KAFKA_BOOTSTRAP_SERVERS, emulator.getBootstrapServers());
            put(Constants.QUEUE_NAME, queueName);
        }};
        return producerProps;
    }

    @Test
    public void builderBuildsKafkaProducerSystemTest(){
        KafkaProducerBuilder producerBuilder = new KafkaProducerBuilder();
        producerBuilder.setProducerProperties(getProducerProperties());
        Producer producer = producerBuilder.build();
        Assert.assertEquals(producer.getClass(), KafkaProducerSystem.class);
    }

    @Test(expected = KafkaException.class)
    public void builderWithoutProperParamsFailsTest(){
        KafkaProducerBuilder producerBuilder = new KafkaProducerBuilder();
        Map<String, Object> props = getProducerProperties();
        props.remove(Constants.KAFKA_BOOTSTRAP_SERVERS);
        producerBuilder.setProducerProperties(props);
        producerBuilder.build();
    }
}
