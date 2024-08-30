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

package com.eclectique.messaging.core.consumer;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * ConsumerInterruptActor is an actor that is responsible for interrupting the ConsumerActor
 * when the polling time exceeds the maximum polling time.
 */
public class ConsumerInterruptActor extends AbstractActor {

    UUID sessionId;
    private Logger LOGGER = LoggerFactory.getLogger(ConsumerInterruptActor.class);

    /**
     * StartTimer is a message that is sent to the ConsumerInterruptActor to start a timer
     * that will interrupt the ConsumerActor after the specified time.
     */
    public static class StartTimer{
        long timerDelayMillis;
        public StartTimer(long timerDelayMillis){
            this.timerDelayMillis = timerDelayMillis;
        }
    };

    /**
     * Constructor for the ConsumerInterruptActor
     * @param sessionId The session id of the consumer
     */
    public ConsumerInterruptActor(UUID sessionId){
        this.sessionId = sessionId;
    }

    public static Props getActorProperties(UUID sessionId){
        return Props.create(ConsumerInterruptActor.class, sessionId);
    }

    @Override
    public Receive createReceive() {
        return new ReceiveBuilder().match(StartTimer.class, args->{
            LOGGER.info(String.format("Wake up after %s ms", args.timerDelayMillis));
            Thread.sleep(args.timerDelayMillis);
            this.sender().tell(new ConsumerActor.InterruptPolling(this.sessionId), this.self());
        }).build();
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        LOGGER.info(String.format("CONSUMER-INTERRUPT-ACTOR %s started", this.self()));
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        LOGGER.info(String.format("CONSUMER-INTERRUPT-ACTOR %s stopped", this.self()));
    }
}
