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

package com.eclectique.messaging.photonconnector.producersystem;

import com.eclectique.messaging.interfaces.Message;
import com.eclectique.messaging.interfaces.Producer;
import com.eclectique.messaging.photon.clients.PhotonConsumer;
import com.eclectique.messaging.photon.clients.PhotonMessagingAdministrator;
import com.eclectique.messaging.photonconnector.Constants;
import com.eclectique.messaging.types.simplemessage.SimpleMessage;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.time.Instant;
import java.util.*;

public class PhotonProducerSystemTest {

    private static final String TEST_QUEUE = "test_queue";
    private static final String TEST_SERVICE = "test_service";
    private static final String TEST_APP = "test_app";
    private Producer producer;

    private Map<String, Object> getProducerProperties(){
        return new HashMap<>(){{
            put(Constants.QUEUE_NAME, "test_queue");
            put(Constants.MESSAGE_SERVICE, "test_service");
        }};
    }

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

    @Test
    public void producerSendsMessageTest(){
        try(PhotonConsumer consumer = new PhotonConsumer(TEST_SERVICE, TEST_QUEUE, TEST_APP, SimpleMessage.class)){
            producer = new PhotonProducerBuilder().setProducerProperties(getProducerProperties()).build();
            producer.sendMessage(getMessage());
            List<Message> messageList = consumer.readMessages(1000);
            Assert.assertEquals(messageList.size(), 1);
            Assert.assertEquals(messageList.get(0).getClass(), SimpleMessage.class);
            Assert.assertEquals(messageList.get(0).getId(), getMessage().getId());
            Assert.assertEquals(((SimpleMessage)messageList.get(0)).getContentSchema(), ((SimpleMessage)getMessage()).getContentSchema());
        }
    }

    @Test(expected = RuntimeException.class)
    public void producerEncountersExceptionWithWrongParams(){
        Map<String, Object> props = getProducerProperties();
        props.put(Constants.QUEUE_NAME, "wrong_queue");
       producer = new PhotonProducerBuilder().setProducerProperties(props).build();
       producer.sendMessage(getMessage());
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
