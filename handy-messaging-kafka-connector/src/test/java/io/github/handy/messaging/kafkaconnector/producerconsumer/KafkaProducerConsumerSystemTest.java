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

import akka.actor.ActorSystem;
import akka.testkit.TestKit;
import io.github.handy.messaging.interfaces.Consumer;
import io.github.handy.messaging.interfaces.EnqueueMessage;
import io.github.handy.messaging.interfaces.Message;
import io.github.handy.messaging.interfaces.Producer;
import io.github.handy.messaging.kafkaconnector.Constants;
import io.github.handy.messaging.kafkaconnector.consumersystem.KafkaConsumerBuilder;
import io.github.handy.messaging.kafkaconnector.producersystem.KafkaProducerBuilder;
import io.github.handy.messaging.types.simplemessage.SimpleMessage;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import scala.concurrent.duration.FiniteDuration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import java.util.concurrent.TimeUnit;

public class KafkaProducerConsumerSystemTest {

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
        consumerProperties.put(Constants.MESSAGE_TYPE_CLASS, "io.github.handy.messaging.types.simplemessage.SimpleMessage");
        consumerProperties.put("auto.offset.reset", "earliest");
        return consumerProperties;
    }

    private Map<String, Object> getProducerProperties(){
        Map<String, Object> producerProps = new HashMap<>(){{
            put(Constants.KAFKA_BOOTSTRAP_SERVERS, emulator.getBootstrapServers());
            put(Constants.QUEUE_NAME, queueName);
        }};
       return producerProps;
    }

    @Test
    public void producerConsumerTest() {
        Consumer consumer =  new KafkaConsumerBuilder().setConsumerProperties(getConsumerProperties()).build();
        consumer.startPolling();
        Producer producer = new KafkaProducerBuilder().setProducerProperties(getProducerProperties()).build();
        producer.sendMessage(getMessage());
        consumerProbe.expectMsgClass(FiniteDuration.create(5, TimeUnit.SECONDS), EnqueueMessage.class);
       consumer.stopPolling();

    }


    private Message getMessage(){
        SimpleMessage contentMsg = new SimpleMessage();
        contentMsg.setContentSchema(String.class.toString());
        contentMsg.setDateTime(Optional.of(Date.from(Instant.now())));
        contentMsg.setMessageId("msg-1");
        contentMsg.setPayload("Hello, this is a sample message".getBytes());
        contentMsg.setSender("app-1");
        contentMsg.setTransactionGroupId("transaction1");
        contentMsg.buildMessage();
        return contentMsg;
    }
}
