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

package io.github.handy.messaging.core.consumer;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.TestKit;
import org.junit.Test;

import java.util.UUID;

public class ConsumerInterruptActorTest {
    static ActorSystem system = ActorSystem.create();

    TestKit testProbe = new TestKit(system);

    private ActorRef getProbeActor(){
        return testProbe.testActor();
    }

    @Test
    public void verifyInterruptionGeneratedTest(){
        UUID sessionId = UUID.randomUUID();
        ActorRef consumerInterruptActor = system.actorOf(ConsumerInterruptActor.getActorProperties(sessionId));
        consumerInterruptActor.tell(new ConsumerInterruptActor.StartTimer(5), getProbeActor());
        ConsumerActor.InterruptPolling interruptPollingSignal = new ConsumerActor.InterruptPolling(sessionId);
        testProbe.expectMsg(interruptPollingSignal);
    }
}
