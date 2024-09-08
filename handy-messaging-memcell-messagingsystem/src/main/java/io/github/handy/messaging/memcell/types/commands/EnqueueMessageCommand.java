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
 * Command to enqueue a message to a Memcell Messaging queue
 */
public class EnqueueMessageCommand extends CommandCapsule {

    String queueName;
    byte[] message;

    /**
     * Constructor to create an EnqueueMessageCommand instance
     * @param queueName - Name of the queue to which the message is to be enqueued
     * @param message - Message to be enqueued
     */
    public EnqueueMessageCommand(String queueName, byte[] message){
        this.queueName = queueName;
        this.message = message;
    }

    /**
     * Get the name of the queue to which the message is to be enqueued
     * @return - Queue name
     */
    public String getQueueName() {
        return queueName;
    }

    /**
     * Get the message to be enqueued
     * @return - Message
     */
    public byte[] getMessage() {
        return message;
    }

}
