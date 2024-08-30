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
import com.eclectique.messaging.interfaces.Message;
import com.eclectique.messaging.interfaces.MessageHandler;

import java.util.Optional;

/**
 * Class designated for handling messages from a queue
 */
public class MessageReceiver implements MessageHandler {

    ActorRef messageAnalyzerActor;
    public MessageReceiver(ActorRef messageAnalyzerActor){
        this.messageAnalyzerActor = messageAnalyzerActor;
    }

    /**
     * Function gets invoked when a message gets picked by the Eclectique Consumer.
     * The consumer dispatches the message to this function
     * @param msg The deserialized message object consumer reads from the message queue
     */
    @Override
    public void handleMessage(Message msg) {
        messageAnalyzerActor.tell(new MessageRetentionBufferActor.EnqueueMessage(msg), ActorRef.noSender());
    }

    /**
     * Function decides every new message coming from the queue gets a new instance of the MessageReceiver class or not
     * @return An Optional of the new instance of the MessageReceiver
     */
    @Override
    public Optional<MessageHandler> getNewInstance() {
        return Optional.empty();
    }
}
