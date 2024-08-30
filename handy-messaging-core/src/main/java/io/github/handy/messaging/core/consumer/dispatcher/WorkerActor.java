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

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import io.github.handy.messaging.interfaces.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Represents a worker actor that processes messages
 */
public class WorkerActor extends AbstractActor {

    /**
     * Message class - To process a task
     */
    public final static class ProcessTask{
        WorkerTask task;
        public ProcessTask(WorkerTask task){
            this.task = task;
        }
    }

    /**
     * Message class - To indicate a task has been completed
     */
    public static class TaskCompleted {
        WorkerTask task;
        public TaskCompleted(WorkerTask task){
            this.task = task;
        }
    }

    private Logger LOGGER = LoggerFactory.getLogger(WorkerActor.class);
    private UUID workerId;
    private MessageHandler messageHandler;


    @Override
    public Receive createReceive() {
        return new ReceiveBuilder().match(ProcessTask.class, args->{
           try {
               this.processTask(args.task);
           } catch (Exception e){
               LOGGER.error(String.format("Error processing task %s. Details - %s", args.task.getTaskId(), e.getMessage()));
           } finally {
               this.sender().tell(new WorkerActor.TaskCompleted(args.task), this.self());
           }
        }).build();
    }

    /**
     * Process the task
     * @param task The task to be processed
     */
    private void processTask(WorkerTask task){
        this.messageHandler.handleMessage(task.getMessage());
    }

    /**
     * Constructor for WorkerActor
     * @param workerId The worker id
     * @param messageHandler The message handler to process the message
     */
    public WorkerActor(UUID workerId, MessageHandler messageHandler){
        this.workerId = workerId;
        this.messageHandler = messageHandler;
    }

    public static Props getActorProperties(UUID workerId, MessageHandler messagehandler){
        return Props.create(WorkerActor.class, workerId, messagehandler)
                .withDispatcher("worker-dispatcher");
    }


    @Override
    public void preStart() throws Exception {
        super.preStart();
        LOGGER.info(String.format("WORKER-ACTOR %s STARTED", this.self()));
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        LOGGER.info(String.format("WORKER-ACTOR %s STOPPED", this.self()));
    }
}
