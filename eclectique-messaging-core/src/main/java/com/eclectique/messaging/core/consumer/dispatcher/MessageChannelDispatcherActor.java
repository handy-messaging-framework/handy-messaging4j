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

package com.eclectique.messaging.core.consumer.dispatcher;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.eclectique.messaging.interfaces.Message;
import com.eclectique.messaging.interfaces.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The MessageChannelDispatcherActor class is an actor that is responsible for dispatching messages to worker actors.
 */
public class MessageChannelDispatcherActor extends AbstractActor {

    /**
     * The RegisterSubscriber class is a message class that is used to register a subscriber actor with the dispatcher.
     */
    public static final class RegisterSubscriber{

        ActorRef subscriberActor;

        public RegisterSubscriber(ActorRef subscriberActor){
            this.subscriberActor = subscriberActor;
        }
        @Override
        public int hashCode(){
            return this.subscriberActor.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof MessageChannelDispatcherActor.RegisterSubscriber){
                MessageChannelDispatcherActor.RegisterSubscriber registrationX = (MessageChannelDispatcherActor.RegisterSubscriber) obj;
                return registrationX.hashCode()==this.hashCode();
            }
            return false;
        }
    }

    /**
     * The SubscriberRegistered class is a message class that is used to indicate
     * that the subscriber actor has been registered with the dispatcher.
     */
    public static final class SubscriberRegistered {}

    /**
     * The DispatchMessages class is a message class that dispatcher receives from the subscriber actor to dispatch messages to the
     * worker actors
     */
    public static final class DispatchMessages{
        List<Message> messages;
        UUID sessionId;
        public DispatchMessages(UUID sessionId, List<Message> messages){
            this.messages = messages;
            this.sessionId = sessionId;
        }
    }

    /**
     * The DispatchCompleted class is a message class that the dispatcher sends to the subscriber actor to indicate that all messages
     * have been dispatched.
     */
    public static final class DispatchCompleted {
        UUID sessionId;
        public DispatchCompleted(UUID sessionId){
            this.sessionId = sessionId;
        }
    }

    private Optional<UUID> dispatchSessionId;
    private String channelId;
    private ActorRef subscriber;
    private HashSet<UUID> taskIdSet;
    private MessageHandler messageHandler;
    private Logger LOGGER = LoggerFactory.getLogger(MessageChannelDispatcherActor.class);
    private TaskMap taskMap;

    /**
     * Constructor for the MessageChannelDispatcherActor class
     * @param channelId The channel ID of the dispatcher
     * @param messageHandler The message handler instance that is used to process messages
     */
    public MessageChannelDispatcherActor(String channelId, MessageHandler messageHandler){
        this.channelId = channelId;
        this.dispatchSessionId = Optional.empty();
        this.messageHandler = messageHandler;
        this.taskMap = new TaskMap();
    }

    @Override
    public Receive createReceive() {
        return new ReceiveBuilder().match(RegisterSubscriber.class, args -> {
            this.subscriber = args.subscriberActor;
            this.sender().tell(new SubscriberRegistered(), this.self());
        }).match(DispatchMessages.class, args->{
            this.dispatchSessionId = Optional.of(args.sessionId);
            this.onDispatchMessages(args.messages);
        }).match(WorkerActor.TaskCompleted.class, args->{
            LOGGER.info(String.format("WORKER-ACTOR %s completed processing a task", this.sender()));
            this.sender().tell(PoisonPill.getInstance(), this.self());
            if(dispatchSessionId.isPresent() && taskIdSet.contains(args.task.getTaskId())){
                this.taskIdSet.remove(args.task.getTaskId());
                Optional<String> transGrpId = args.task.getMessage().getTransactionGroupId();
                if(this.taskMap.hasAvailableTasks(transGrpId)){
                    WorkerTask task = this.taskMap.getNextTask(transGrpId);
                    LOGGER.info(String.format("Sequential dispatching of task ID: %s", task.getTaskId()));
                    dispatchTask(task);
                }
                if(this.taskIdSet.isEmpty()){
                    LOGGER.info(String.format("DISPATCHER-ACTOR %s processed all messages. Requesting SUBSCRIBER-ACTOR %s to request more", this.self(), this.subscriber));
                    UUID completedSessionId = this.dispatchSessionId.get();
                    this.dispatchSessionId = Optional.empty();
                    this.subscriber.tell(new MessageChannelDispatcherActor.DispatchCompleted(completedSessionId), this.self());
                }
            } else {
                LOGGER.warn(String.format("DISPATCHER-ACTOR %s received stale process completed message for task %s",
                        this.self(),
                        args.task.getTaskId(),
                        this.sender()));
            }
        }).build();
    }
    private void dispatchTasks(){
        for(Optional<String> transactionGrpId: this.taskMap.getAllTransactions()){
            if(transactionGrpId.equals(Optional.empty())){
                LOGGER.info(String.format("Parallel dispatching %s tasks", taskMap.getTaskCount(transactionGrpId)));
               while(this.taskMap.hasAvailableTasks(transactionGrpId)){
                   WorkerTask task = this.taskMap.getNextTask(transactionGrpId);
                   dispatchTask(task);
               }
            } else {
                WorkerTask task = this.taskMap.getNextTask(transactionGrpId);
                LOGGER.info(String.format("Sequential dispatching of task ID: %s", task.getTaskId()));
                dispatchTask(task);
            }
        }
    }

    private void dispatchTask(WorkerTask task){
        ActorRef workerActor = task.getWorkerActor();
        workerActor.tell(new WorkerActor.ProcessTask(task), this.self());
    }

    private void onDispatchMessages(List<Message> messages){
        LOGGER.info(String.format("DISPATCHER-ACTOR %s received %s messages", this.self(), messages.size()));
        List<UUID> taskIds = messages.stream().map(message -> UUID.randomUUID())
                .collect(Collectors.toList());
        taskIdSet = new HashSet<>(taskIds);
        IntStream.range(0, messages.size()).forEach(idx -> {
            Message message = messages.get(idx);
            UUID workerId = taskIds.get(idx);
            ActorRef workerActor = this.context()
                    .actorOf(WorkerActor.getActorProperties(workerId, messageHandler.getNewInstance().orElse(messageHandler)),
                            String.format("WORKER-ACTOR-%s@%s", workerId, this.channelId));
            Optional<String> transactionGroupId = message.getTransactionGroupId();
            WorkerTask task = new WorkerTask(workerId, message, workerActor);
            taskMap.addTransaction(transactionGroupId,task);
        });
        this.dispatchTasks();
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        LOGGER.info(String.format("DISPATCHER-ACTOR %s STARTED", this.self()));
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        LOGGER.info(String.format("DISPATCHER-ACTOR %s STOPPED", this.self()));
    }

    public static Props getActorProperties(String channelId, MessageHandler msgHandler){
        return Props.create(MessageChannelDispatcherActor.class, channelId, msgHandler);
    }

}
