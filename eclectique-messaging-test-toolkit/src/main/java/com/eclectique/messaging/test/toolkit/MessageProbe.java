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
import akka.util.Timeout;
import com.eclectique.messaging.interfaces.Message;
import com.eclectique.messaging.interfaces.MessageField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static akka.pattern.Patterns.ask;

/**
 * Message Probe class gets a list of messages collected from a queue. This class contains various directives
 * that can be run on the data collected from the queue.
 */
public class MessageProbe {

    ActorRef messageBufferActor;

    Logger LOGGER = LoggerFactory.getLogger(MessageProbe.class);

    List<Message> analysisMessages;
    private MessageProbe(List<Message> collectedMessages){
        this.analysisMessages = collectedMessages;
    }

    /**
     * Function compares and filters all message instances fetched from the queue with a target one that matches certain field criteria
     * @param matchTargetMessage The target message instance object to which each messages collected in the queue gets compared against with
     * @param matchFields Those fields in the `matchTargetMessage` that needs to be compared with the ones collected from the queue
     * @return List of messages from the queue that matches with the `matchFields` of the `matchTargetMessage`
     */
    public List<Message> filterMessages(Message matchTargetMessage, MessageField... matchFields){
        return this.analysisMessages.stream().filter(enqueuedMessage -> {
            for(MessageField messageField: matchFields){
                boolean matchFlag = messageField.compare(matchTargetMessage, enqueuedMessage);
                if(!matchFlag){
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
    }

    /**
     * Returns true if there are any messages collected from the queue that matches with certain target fields of a target message
     * @param matchTargetMessage The target message object to which each message collected from the queue gets compared against with
     * @param matchFields Those fields in the `matchTargetMessage` that needs to be compared with the ones collected from the queue
     * @return True if there is at least one message that matches with the `matchTargetMessage` on the `matchFields`
     */
    public boolean hasAnyMessages(Message matchTargetMessage, MessageField... matchFields){
        return !this.filterMessages(matchTargetMessage, matchFields).isEmpty();
    }

    /**
     * Function checks if there is at least a single message in the queue.
     * @return Returns true if there is atleast one message in the queue. Otherwise returns false
     */
    public boolean hasAnyMessages(){
        return !this.analysisMessages.isEmpty();
    }

    /**
     * Builder class for the Message Probe instance
     */
     static class MessageProbeBuilder {

        ActorRef messageBufferActor;
        Logger LOGGER = LoggerFactory.getLogger(MessageProbeBuilder.class);
        public MessageProbeBuilder(ActorRef messageBufferActor){
            this.messageBufferActor = messageBufferActor;
        }

        public MessageProbe startNewAnalysis(long dataCollectionDelayMs){
            LOGGER.info(String.format("Message probe builder waiting for data collection timeout of %s ms to expire", dataCollectionDelayMs));
            List<Message> collectedMessages = new ArrayList<>();
            try {
                Timeout timeout = new Timeout(Duration.create(60, "seconds"));
                Thread.sleep(dataCollectionDelayMs);
                collectedMessages = (List)Await.result(ask(messageBufferActor, new MessageRetentionBufferActor.ReleaseMessages(), 60000), timeout.duration());
            } catch (Exception ex){
                throw new RuntimeException("Message probe builder could not complete data collection timeout");
            }
            return new MessageProbe(collectedMessages);
        }
    }
}
