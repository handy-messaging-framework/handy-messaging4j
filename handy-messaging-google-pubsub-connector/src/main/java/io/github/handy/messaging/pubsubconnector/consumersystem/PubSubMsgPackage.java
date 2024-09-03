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

package io.github.handy.messaging.pubsubconnector.consumersystem;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.pubsub.v1.PubsubMessage;

/**
 * PubSubMsgPackage is an encapsulation that contains a PubSub message and the AckReplyConsumer to
 * acknowledge the message
 */
public class PubSubMsgPackage {

    private PubsubMessage message;
    private AckReplyConsumer sender;

    public PubSubMsgPackage(PubsubMessage msg, AckReplyConsumer replyTo){
        this.message = msg;
        this.sender = replyTo;
    }

    /**
     * Get the PubSub message
     * @return - PubsubMessage
     */
    public PubsubMessage getMessage() {
        return message;
    }

    /**
     * Get the AckReplyConsumer
     * @return - AckReplyConsumer
     */
    public AckReplyConsumer getSender() {
        return sender;
    }
}
