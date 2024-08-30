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

package com.eclectique.messaging.kafkaconnector.producerconsumer;

import akka.actor.ActorSystem;
import akka.testkit.TestKit;
import com.eclectique.messaging.interfaces.Consumer;
import com.eclectique.messaging.kafkaconnector.Constants;
import com.eclectique.messaging.kafkaconnector.consumersystem.KafkaConsumerBuilder;
import com.eclectique.messaging.kafkaconnector.consumersystem.KafkaConsumerSystem;
import org.apache.kafka.common.KafkaException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import java.util.HashMap;
import java.util.Map;

public class KafkaConsumerBuilderTest {

    private static KafkaContainer emulator;
    private String queueName = "testQueue";
    ActorSystem testSystem = ActorSystem.create("testSystem");
    TestKit consumerProbe = new TestKit(testSystem);

    @BeforeClass
    public static void setup() {
        emulator =  new KafkaContainer(DockerImageName.parse("apache/kafka-native:3.8.0"));
        emulator.start();
    }

    @AfterClass
    public static void teardown() {
        emulator.stop();
    }

    private Map<String, Object> getConsumerProperties(){
        Map<String, Object> consumerProperties = new HashMap<>();
        consumerProperties.put(Constants.KAFKA_BOOTSTRAP_SERVERS, emulator.getBootstrapServers());
        consumerProperties.put(Constants.QUEUE_NAME, this.queueName);
        consumerProperties.put("group.id", "testGroup");
        consumerProperties.put(Constants.CONSUMER_ACTOR, consumerProbe.testActor());
        consumerProperties.put(Constants.MESSAGE_TYPE_CLASS, "com.eclectique.messaging.types.simplemessage.SimpleMessage");
        consumerProperties.put("auto.offset.reset", "earliest");
        return consumerProperties;
    }

    @Test
    public void builderBuildsKafkaConsumerTest(){
        KafkaConsumerBuilder consumerBuilder = new KafkaConsumerBuilder();
        consumerBuilder.setConsumerProperties(getConsumerProperties());
        Consumer consumer = consumerBuilder.build();
        Assert.assertEquals(consumer.getClass(), KafkaConsumerSystem.class);
    }


    @Test(expected=KafkaException.class)
    public void builderWithoutProperParamsFailsTest(){
        KafkaConsumerBuilder consumerBuilder = new KafkaConsumerBuilder();
        Map<String, Object> props = getConsumerProperties();
        props.remove(Constants.KAFKA_BOOTSTRAP_SERVERS);
        consumerBuilder.setConsumerProperties(props).build();
    }
}
