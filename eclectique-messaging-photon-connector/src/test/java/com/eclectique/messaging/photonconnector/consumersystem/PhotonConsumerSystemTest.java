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

package com.eclectique.messaging.photonconnector.consumersystem;

import akka.actor.ActorSystem;
import akka.testkit.TestKit;
import com.eclectique.messaging.interfaces.Consumer;
import com.eclectique.messaging.interfaces.EnqueueMessage;
import com.eclectique.messaging.interfaces.Message;
import com.eclectique.messaging.photon.clients.PhotonMessagingAdministrator;
import com.eclectique.messaging.photon.clients.PhotonProducer;
import com.eclectique.messaging.photonconnector.Constants;
import com.eclectique.messaging.types.simplemessage.SimpleMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PhotonConsumerSystemTest {

    private ActorSystem testSystem = ActorSystem.create();
    private TestKit consumerProbe = new TestKit(testSystem);
    private Consumer consumer;
    private static final String TEST_QUEUE = "test_queue";
    private static final String TEST_SERVICE = "test_service";
    private static final String TEST_APP = "test_app";

    @Before
    public void setup(){
        PhotonMessagingAdministrator adm = new PhotonMessagingAdministrator();
        adm.registerMessagingService(TEST_SERVICE);
        adm.registerMessagingQueue(TEST_QUEUE, TEST_SERVICE);
    }

    @After
    public void teardown(){
        PhotonMessagingAdministrator adm = new PhotonMessagingAdministrator();
        adm.tearDownMessagingService("test_service");
    }

    private Map<String, Object> getConsumerProperties(){
        return new HashMap<>(){{
            put(Constants.CONSUMER_ACTOR, consumerProbe.testActor());
            put(Constants.QUEUE_NAME, TEST_QUEUE);
            put(Constants.MESSAGE_SERVICE, TEST_SERVICE);
            put(Constants.MESSAGE_TYPE_CLASS, "com.eclectique.messaging.types.simplemessage.SimpleMessage");
            put(Constants.APPLICATION_ID, TEST_APP);
        }};
    }

    @Test
    public void consumerConsumesMessageTest(){
        consumer = new PhotonConsumerBuilder().setConsumerProperties(getConsumerProperties()).build();
        consumer.startPolling();
        PhotonProducer producer = new PhotonProducer(TEST_SERVICE);
        producer.sendMessage(TEST_QUEUE, getMessage());
        consumerProbe.expectMsgClass(EnqueueMessage.class);
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
