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

package io.github.handy.messaging.core.consumer.dispatcher;

import akka.actor.ActorRef;
import io.github.handy.messaging.interfaces.Message;

import java.util.UUID;

/**
 * Represents a task to be executed by a worker actor
 */
public class WorkerTask {

    private Message message;
    private UUID taskId;
    private ActorRef workerActor;

    /**
     * Constructor for WorkerTask
     * @param taskId The task id
     * @param msg The message to be processed
     * @param workerActor The worker actor that will process the message
     */
    public WorkerTask(UUID taskId, Message msg, ActorRef workerActor){
        this.message = msg;
        this.taskId = taskId;
        this.workerActor = workerActor;
    }

    /**
     * Get the message to be processed
     * @return The message to be processed
     */
    public Message getMessage() {
        return message;
    }

    /**
     * Get the task id
     * @return The task id
     */
    public UUID getTaskId() {
        return taskId;
    }

    /**
     * Get the worker actor that will process the message
     * @return The worker actor that will process the message
     */
    public ActorRef getWorkerActor() {
        return workerActor;
    }
}
