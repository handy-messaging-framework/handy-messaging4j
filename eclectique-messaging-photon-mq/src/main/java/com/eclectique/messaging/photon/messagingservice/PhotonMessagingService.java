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

package com.eclectique.messaging.photon.messagingservice;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.eclectique.messaging.photon.types.CompletionCallback;
import com.eclectique.messaging.photon.types.commands.*;
import com.eclectique.messaging.photon.types.responses.CommandExecutionStatus;
import com.eclectique.messaging.photon.types.responses.CommandResponse;
import com.eclectique.messaging.photon.types.responses.FetchEnqueuedMessagesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * PhotonMessagingService is the messaging service that handles message queues and subscribers
 */
public class PhotonMessagingService extends AbstractActor {

    Logger LOGGER = LoggerFactory.getLogger(PhotonMessagingService.class);

    @Override
    public Receive createReceive() {
        return new ReceiveBuilder().match(RegisterQueueCommand.class, args->{
            this.onRegisterQueue(args.getQueue(), args.getCompletionCallback());
        }).match(RegisterSubscriberCommand.class, args->{
            this.onRegisterSubscriber(args.getQueue(), args.getSubscriberId(),args.getCompletionCallback());
        }).match(UnregisterSubscriberCommand.class, args->{
            this.onUnregisterSubscriber(args.getQueue(),
                    args.getSubscriberId(),
                    args.getCompletionCallback());
        }).match(EnqueueMessageCommand.class, this::onEnqueueMessage).match(FetchEnqueuedMessageCommand.class, this::onFetchQueuedMessage)
                .build();
    }

    private Set<String> queues;
    private String messagingServiceName;

    private Map<String, Map<String, ActorRef>> subscriberRegistry;

    /**
     * Constructor to create a new PhotonMessagingService instance
     * @param messagingServiceName - Name of the messaging service
     */
    public PhotonMessagingService(String messagingServiceName){
        this.queues = new HashSet<>();
        this.subscriberRegistry = new HashMap<>();
        this.messagingServiceName = messagingServiceName;

    }

    public static Props getActorProperties(String messagingServiceName){
        return Props.create(PhotonMessagingService.class, messagingServiceName);
    }

    /**
     * Function to register a new queue
     * @param queueName - Name of the queue
     * @param completionCallback - Callback to execute after the operation
     */
    private void onRegisterQueue(String queueName, CompletionCallback completionCallback){
        LOGGER.info(String.format("MESSAGING-SERVICE %s - Register Queue Received", this.messagingServiceName));
        CommandResponse commandResponse;
        if(!this.queues.contains(queueName)) {
            this.queues.add(queueName);
            commandResponse = new CommandResponse(CommandExecutionStatus.SUCCESS);

        } else {
            commandResponse = new CommandResponse(CommandExecutionStatus.FAILED);
            completionCallback.onCompletion(commandResponse);
        }
        LOGGER.info(String.format("MESSAGING-SERVICE %s - New queue %s registered", this.messagingServiceName, queueName));
        completionCallback.onCompletion(commandResponse);
    }

    /**
     * Function to register a new subscriber
     * @param queueName - Name of the queue
     * @param subscriberId - Subscriber ID
     * @param completionCallback - Callback to execute after the operation
     */
    private void onRegisterSubscriber(String queueName, String subscriberId, CompletionCallback completionCallback){
        LOGGER.info(String.format("MESSAGING-SERVICE %s - Register Subscriber Received", this.messagingServiceName));
        CommandResponse commandResponse;
        String subscriberTemplate = "SUBSCRIBER-%s_%s";
        if(!this.queues.contains(queueName)){
            commandResponse = new CommandResponse(CommandExecutionStatus.FAILED);
        } else {
            this.subscriberRegistry.putIfAbsent(queueName, new HashMap<>());
            Map<String, ActorRef> subscriberMap = subscriberRegistry.get(queueName);
            if(!subscriberMap.containsKey(subscriberId)){
                ActorRef subscriberActor = getContext().actorOf(SubscriberActor.getActorProperties(),
                        String.format(subscriberTemplate, queueName, subscriberId));
                subscriberMap.put(subscriberId, subscriberActor);
                commandResponse = new CommandResponse(CommandExecutionStatus.SUCCESS);
            } else {
                commandResponse = new CommandResponse(CommandExecutionStatus.FAILED);
            }
        }
        completionCallback.onCompletion(commandResponse);
    }

    /**
     * Function to unregister a subscriber
     * @param queueName - Name of the queue
     * @param subscriberId - Subscriber ID
     * @param completionCallback - Callback to execute after the operation
     */
    private void onUnregisterSubscriber(String queueName, String subscriberId, CompletionCallback completionCallback){
        LOGGER.info(String.format("MESSAGING-SERVICE %s - Unregister Subscriber Received", this.messagingServiceName));
        CommandResponse commandResponse;
        if(this.subscriberRegistry.containsKey(queueName)){
            if(this.subscriberRegistry.get(queueName).containsKey(subscriberId)){
                this.subscriberRegistry.get(queueName).get(subscriberId).tell(PoisonPill.getInstance(), self());
                this.subscriberRegistry.get(queueName).remove(subscriberId);
                LOGGER.info(String.format("MESSAGING-SERVICE - Subscriber %s unregistered", subscriberId));
                commandResponse = new CommandResponse(CommandExecutionStatus.SUCCESS);

            } else {
                LOGGER.error(String.format("Subscriber %s not found", subscriberId));
                commandResponse = new CommandResponse(CommandExecutionStatus.FAILED);
            }
        } else {
            LOGGER.error(String.format("Queue %s not found", queueName));
            commandResponse = new CommandResponse(CommandExecutionStatus.FAILED);
        }
        completionCallback.onCompletion(commandResponse);
    }

    /**
     * Function to fetch a queued message
     * @param fetchEnqueuedMessageCommand - Command to fetch a queued message
     */
    private void onFetchQueuedMessage(FetchEnqueuedMessageCommand fetchEnqueuedMessageCommand){
        String queueName = fetchEnqueuedMessageCommand.getQueueName();
        String subscriberId = fetchEnqueuedMessageCommand.getSubscriberId();
        FetchEnqueuedMessagesResponse enqueuedMessagesResponse;
        if(this.subscriberRegistry.containsKey(queueName)){
            Map<String, ActorRef> subscriberMap = this.subscriberRegistry.get(queueName);
            if (subscriberMap.containsKey(subscriberId)) {
                ActorRef subscriberActor = subscriberMap.get(subscriberId);
                subscriberActor.tell(fetchEnqueuedMessageCommand, self());
            } else {
                LOGGER.error(String.format("Error - Fetch queue command. Subscriber %s not found", fetchEnqueuedMessageCommand.getSubscriberId()));
                enqueuedMessagesResponse = new FetchEnqueuedMessagesResponse(CommandExecutionStatus.FAILED,
                        new ArrayList<>());
                fetchEnqueuedMessageCommand.getCompletionCallback().onCompletion(enqueuedMessagesResponse);
            }
        } else {
            LOGGER.error(String.format("Error - Fetch queue command. Queue %s not found", fetchEnqueuedMessageCommand.getQueueName()));
            enqueuedMessagesResponse = new FetchEnqueuedMessagesResponse(CommandExecutionStatus.FAILED,
                    new ArrayList<>());
            fetchEnqueuedMessageCommand.getCompletionCallback().onCompletion(enqueuedMessagesResponse);
        }
    }

    /**
     * Function to enqueue a message
     * @param enqueueMessageCommand - Command to enqueue a message
     */
    private void onEnqueueMessage(EnqueueMessageCommand enqueueMessageCommand){
        LOGGER.info(String.format("MESSAGING-SERVICE %s - Enqeue Command Received", this.messagingServiceName));
        Optional<CommandResponse> commandResponse = Optional.empty();
        if(this.queues.contains(enqueueMessageCommand.getQueueName())){
            if(this.subscriberRegistry.containsKey(enqueueMessageCommand.getQueueName())){
                this.subscriberRegistry.get(enqueueMessageCommand.getQueueName()).values().forEach(subscriberActor -> {
                    subscriberActor.tell(enqueueMessageCommand, self());
                });
                commandResponse = Optional.of(new CommandResponse(CommandExecutionStatus.SUCCESS));
            }
        } else {
            LOGGER.error(String.format("Error - Enqueue command. Queue %s not found", enqueueMessageCommand.getQueueName()));
            commandResponse = Optional.of(new CommandResponse(CommandExecutionStatus.FAILED));
        }
        enqueueMessageCommand.getCompletionCallback().onCompletion(commandResponse.orElse(new CommandResponse(CommandExecutionStatus.SUCCESS)));
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        LOGGER.info(String.format("Photon Messaging Service %s started", this.messagingServiceName));
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        LOGGER.info(String.format("Photon Messaging Service %s stoppped", this.messagingServiceName));
    }
}
