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

package io.github.handy.messaging.memcell.types.commands;

import io.github.handy.messaging.memcell.types.CommandCapsule;

/**
 * Command to fetch an enqueued message from a Memcell Messaging queue
 */
public class FetchEnqueuedMessageCommand extends CommandCapsule {

    String queueName, subscriberId;

    /**
     * Constructor to create a FetchEnqueuedMessageCommand instance
     * @param queueName - Name of the queue from which the message is to be fetched
     * @param subscriberId - Subscriber ID
     */
    public FetchEnqueuedMessageCommand(String queueName,
                                       String subscriberId){
        this.queueName = queueName;
        this.subscriberId = subscriberId;
    }

    /**
     * Get the name of the subscriber from whom the message(s) are to be fetched
     * @return - Subscriber ID
     */
    public String getSubscriberId(){
        return this.subscriberId;
    }

    /**
     * Get the name of the queue from which the message(s) are to be fetched
     * @return - Queue name
     */
    public String getQueueName() {
        return queueName;
    }
}
