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

package com.eclectique.messaging.mqttconnector;

import akka.actor.ActorRef;

import akka.actor.ActorSystem;
import akka.testkit.TestKit;
import com.eclectique.messaging.interfaces.EnqueueMessage;
import com.eclectique.messaging.interfaces.Message;
import com.eclectique.messaging.mqttconnector.consumersystem.MqttConsumerBuilder;
import com.eclectique.messaging.mqttconnector.consumersystem.MqttConsumerSystem;
import com.eclectique.messaging.types.simplemessage.SimpleMessage;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.Before;
import org.junit.Test;
import org.testcontainers.hivemq.HiveMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;
import java.time.Instant;
import java.util.*;

public class MqttConsumerSystemTest {

    MqttConsumerBuilder consumerBuilder;

    MqttConsumerSystem consumerSystem;

    @Container
    HiveMQContainer hiveMQContainer;

    static ActorSystem system = ActorSystem.create();

    TestKit testProbe = new TestKit(system);

    private  ActorRef getConsumerActor(){
        return testProbe.testActor();
    }

    private Map<String, Object> getConsumerProperties(){
        return new HashMap<String, Object>(){{
            put(Constants.QUEUE_NAME, "abc/def");
            put(Constants.CONSUMER_ACTOR, getConsumerActor());
            put(Constants.CONNECTION_HOST, hiveMQContainer.getHost());
            put(Constants.CONNECTION_PORT, hiveMQContainer.getMqttPort());
            put(Constants.MESSAGE_TYPE_CLASS, "com.eclectique.messaging.types.simplemessage.SimpleMessage");
            put(Constants.CLEAN_SESSION_FLAG, true);
            put("max.poll.duration.millis", 1000);
            put("max.messages.per.batch", 1);
        }};
    }
    @Before
    public void setup(){
        this.hiveMQContainer = new HiveMQContainer(DockerImageName.parse("hivemq/hivemq-ce:latest"));
        this.hiveMQContainer.start();
        this.consumerBuilder = new MqttConsumerBuilder();
        this.consumerBuilder.setConsumerProperties(getConsumerProperties());
        this.consumerSystem = new MqttConsumerSystem(this.consumerBuilder);

    }

    @Test
    public void testVerifyReceivedMessagesGetsEnqueued() throws MqttException {
        this.consumerSystem.startPolling();
        MqttClient publisher = new MqttClient(String.format("tcp://%s:%s", this.hiveMQContainer.getHost(), this.hiveMQContainer.getMqttPort()), UUID.randomUUID().toString());
        MqttMessage message = new MqttMessage();
        message.setPayload(getMessage().serialize());
        message.setQos(2);
        publisher.connect();
        publisher.publish("abc/def", message);
        publisher.disconnect();
        testProbe.expectMsgClass(EnqueueMessage.class);
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
