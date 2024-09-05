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

package io.github.handy.messaging.mqttconnector;

import io.github.handy.messaging.interfaces.Message;
import io.github.handy.messaging.mqttconnector.producersystem.MqttProducerBuilder;
import io.github.handy.messaging.mqttconnector.producersystem.MqttProducerSystem;
import io.github.handy.messaging.types.simplemessage.SimpleMessage;
import org.eclipse.paho.client.mqttv3.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testcontainers.hivemq.HiveMQContainer;
import org.testcontainers.utility.DockerImageName;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class MqttProducerSystemTest {

    
    private static HiveMQContainer hiveMQContainer;
    private MqttProducerSystem producer;

    private  Map<String, Object> generateConsumerProperties(){
        return new HashMap<String, Object>(){{
            put(Constants.QUEUE_NAME, "abc/def");
            put(Constants.CONNECTION_HOST, hiveMQContainer.getHost());
            put(Constants.CONNECTION_PORT, hiveMQContainer.getMqttPort());
            put(Constants.CLEAN_SESSION_FLAG, true);
            put(Constants.MESSAGE_TYPE_CLASS, "io.github.handy.messaging.types.simplemessage.SimpleMessage");
        }};
    }

    @BeforeClass
    public static void setup(){
        hiveMQContainer = new HiveMQContainer(DockerImageName.parse("hivemq/hivemq-ce:2021.3"));
        hiveMQContainer.start();
    }

    @Before
    public void setupTest(){
        MqttProducerBuilder producerBuilder = new MqttProducerBuilder();
        producerBuilder.setProducerProperties(this.generateConsumerProperties());
        this.producer = new MqttProducerSystem(producerBuilder);
    }

    @Test
    public void verifyProducerSendsMessageTest() throws MqttException, ExecutionException, InterruptedException {
        MqttClient verifierClient = new MqttClient(String.format("tcp://%s:%s", this.hiveMQContainer.getHost(),
                this.hiveMQContainer.getMqttPort()), UUID.randomUUID().toString());

       List<Message> collectedMsgs = new ArrayList<>();
        FutureTask<Object> completionFuture = new FutureTask<>(()->{
            return new Object();
        });

        verifierClient.setCallback(new MqttCallback() {


            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
               SimpleMessage receivedMessage = new SimpleMessage();
               receivedMessage.deserialize(mqttMessage.getPayload());
                collectedMsgs.add(receivedMessage);
               completionFuture.run();

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
        verifierClient.connect();
        verifierClient.subscribe("abc/def");
        this.producer.sendMessage(this.getMessage());
        completionFuture.get();
        Assert.assertEquals(1, collectedMsgs.size());
        Message receivedMsg = collectedMsgs.get(0);
        Assert.assertEquals("io.github.handy.messaging.types.simplemessage.SimpleMessageProto", receivedMsg.getHeaderSchema());
        Assert.assertEquals("msg-1", receivedMsg.getId());
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
