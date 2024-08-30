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

package io.github.handy.messaging.photon.types.commands;

import io.github.handy.messaging.photon.types.CommandCapsule;

/**
 * Command to unregister a subscriber from a PhotonMQ queue
 */
public class UnregisterSubscriberCommand extends CommandCapsule {

    private String queue, subscriberId;

    /**
     * Constructor to create an UnregisterSubscriberCommand instance
     * @param queue - Name of the queue to unsubscribe from
     * @param subscriberId - Subscriber ID
     */
    public UnregisterSubscriberCommand(String queue, String subscriberId){
        this.queue = queue;
        this.subscriberId = subscriberId;
    }

    /**
     * Get the name of the queue to unsubscribe from
     * @return - Queue name
     */
    public String getQueue() {
        return queue;
    }

    /**
     * Get the subscriber ID
     * @return - Subscriber ID
     */
    public String getSubscriberId() {
        return subscriberId;
    }
}
