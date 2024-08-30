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

package com.eclectique.messaging.test.toolkit;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.TestKit;
import com.eclectique.messaging.types.simplemessage.SimpleMessage;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class MessageRetentionBufferActorTest {

    ActorSystem system = ActorSystem.create("testSystem");
    TestKit senderProbe = new TestKit(system);

    ActorRef retentionActor;

    @Before
    public void setup(){
        retentionActor = system.actorOf(MessageRetentionBufferActor.getActorProperties());
    }

    @Test
    public void testEnqueueMessage(){
        retentionActor.tell(new MessageRetentionBufferActor.EnqueueMessage(getSampleMessage()), senderProbe.testActor());
        senderProbe.expectNoMessage();
    }

    @Test
    public void testReleaseMessages(){
        retentionActor.tell(new MessageRetentionBufferActor.EnqueueMessage(getSampleMessage()), senderProbe.testActor());
        retentionActor.tell(new MessageRetentionBufferActor.ReleaseMessages(), senderProbe.testActor());
        senderProbe.expectMsgClass(ArrayList.class);
    }

    private SimpleMessage getSampleMessage(){
        SimpleMessage msg = new SimpleMessage();
        msg.setContentSchema(String.class.toString());
        msg.setPayload("Hello, this is a sample message".getBytes());
        msg.setSender("app-1");
        msg.setMessageId("msg-1");
        msg.setTransactionGroupId("transaction1");
        msg.buildMessage();
        return msg;
    }

}
