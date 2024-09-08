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

package io.github.handy.messaging.memcell.clients;

import io.github.handy.messaging.memcell.types.responses.CommandExecutionStatus;
import io.github.handy.messaging.memcell.types.responses.CommandResponse;
import io.github.handy.messaging.types.simplemessage.SimpleMessage;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class MemcellMessagingProducerConsumerTest {
    String messagingInstance = "TestInstance";

    @Test
    public void sendAndReceiveMessageQueue(){
        MemcellMessagingConsumer consumer = new MemcellMessagingConsumer(messagingInstance, "testQueue","testConsumerX", SimpleMessage.class);
        MemcellMessagingProducer producer = new MemcellMessagingProducer(messagingInstance);
        SimpleMessage msg = getMessage();
        CommandResponse response = producer.sendMessage("testQueue", msg);
        Assert.assertEquals(response.getCommandExecutionStatus(), CommandExecutionStatus.SUCCESS);
        List<SimpleMessage> msgs = consumer.readMessages(1000L);
        SimpleMessage receivedMsg = msgs.get(0);
        Assert.assertEquals(msg.getSender(), receivedMsg.getSender());
        Assert.assertEquals(msg.getId(), receivedMsg.getId());
        Assert.assertEquals(msg.getContentSchema(), receivedMsg.getContentSchema());

    }

    @Test(expected = RuntimeException.class)
    public void consumerFailNonExistingQueue(){
        MemcellMessagingConsumer consumer = new MemcellMessagingConsumer(messagingInstance, "nonExistingQueue","testConsumerX", SimpleMessage.class);
    }

    @Test(expected = RuntimeException.class)
    public void consumerFailRegisterSubscriberTwice(){
        MemcellMessagingConsumer consumerX = new MemcellMessagingConsumer(messagingInstance, "testQueue","testConsumerX", SimpleMessage.class);
        MemcellMessagingConsumer consumerXagain = new MemcellMessagingConsumer(messagingInstance, "testQueue","testConsumerX", SimpleMessage.class);
    }

    @Before
    public void setup(){
        MemcellMessagingAdministrator adm = new MemcellMessagingAdministrator();
        adm.registerMessagingService(messagingInstance);
        adm.registerMessagingQueue("testQueue", messagingInstance);
    }

    @After
    public void teardown(){
        MemcellMessagingAdministrator adm = new MemcellMessagingAdministrator();
        adm.tearDownMessagingService(messagingInstance);
    }

    private SimpleMessage getMessage(){
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
