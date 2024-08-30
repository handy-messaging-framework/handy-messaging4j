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

package com.eclectique.messaging.core.consumer;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.eclectique.messaging.core.consumer.dispatcher.MessageChannelDispatcherActor;
import com.eclectique.messaging.interfaces.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * MessageChannelSubscriberActor - Actor class that subscribes to a message channel and receives messages from the
 * publisher actor. The subscriber actor is responsible for receiving messages from the publisher actor and
 * dispatching the messages to the dispatcher actor.
 */
public class MessageChannelSubscriberActor extends AbstractActor {

    /**
     * Initialize - Message class to initialize the subscriber actor
     */
    public static final class Initialize{}

    /**
     * Initialized - Message class to indicate that the subscriber actor has been initialized
     */
    public static final class Initialized{}


    private enum InitializationModules {
        PUBLISHER,
        DISPATCHER
    }

    private Logger LOGGER = LoggerFactory.getLogger(MessageChannelSubscriberActor.class);
    private String channelId;
    private ActorRef publisherActor;
    private ActorRef rootActor;
    private ActorRef dispatcherActor;


    @Override
    public Receive createReceive() {
        return new ReceiveBuilder().match(Initialize.class, args -> {
            this.onInitialize();
        }).match(ConsumerActor.DataAvailable.class, args->{
            this.onDataReceived(args.sessionId, args.messageCollection);
        }).match(ConsumerActor.SubscriptionAck.class, args->{
            this.onPublisherAck();
        }).match(MessageChannelDispatcherActor.SubscriberRegistered.class, args -> {
            this.onDispatcherAck();
        }).match(MessageChannelDispatcherActor.DispatchCompleted.class, args->{
            this.publisherActor.tell(new ConsumerActor.PollData(3), this.self());
        }).build();
    }


    private Set<InitializationModules> pendingInitializationSteps;

    /**
     * Constructor for the MessageChannelSubscriberActor
     * @param channelId - ChannelID for uniquely identifying the consumer channel
     * @param publisherActor - Publisher Actor
     * @param rootActor - Root Actor
     * @param dispatcherActor - Dispatcher Actor
     */

    public MessageChannelSubscriberActor(String channelId,
                                         ActorRef publisherActor,
                                         ActorRef rootActor,
                                         ActorRef dispatcherActor){
        this.channelId = channelId;
        this.publisherActor = publisherActor;
        this.rootActor = rootActor;
        this.dispatcherActor = dispatcherActor;
        this.pendingInitializationSteps = new HashSet<>(){{
            add(InitializationModules.PUBLISHER);
            add(InitializationModules.DISPATCHER);
        }};

    }

    /**
     * onDataReceived - Method to handle the data received from the publisher actor
     * @param sessionId - Session ID
     * @param messages - List of messages
     */
    private void onDataReceived(UUID sessionId, List<Message> messages){

        LOGGER.info(String.format("SUBSCRIBER-ACTOR %s received %s messages", this.self(), messages.size()));
        if(messages.size()>0) {
            this.dispatcherActor.tell(new MessageChannelDispatcherActor.DispatchMessages(sessionId, messages),
                    this.self());
        } else {
            this.publisherActor.tell(new ConsumerActor.PollData(3), this.self());
        }

    }

    public static final Props getActorProperties(String channelId, ActorRef rootActor, ActorRef publisherActor, ActorRef dispatcherActor){
        return Props.create(MessageChannelSubscriberActor.class, channelId, publisherActor, rootActor, dispatcherActor);
    }

    /**
     * onInitialize - Handler for the Initialize message
     */
    private void onInitialize(){
        this.publisherActor.tell(new ConsumerActor.SubscriptionRequest(this.self()), this.self());
        this.dispatcherActor.tell(new MessageChannelDispatcherActor.RegisterSubscriber(this.self()), this.self());
    }

    /**
     * onDispatcherAck - Handler for the Dispatcher Ack message
     */
    private void onDispatcherAck(){
        LOGGER.info(String.format("SUBSCRIBER-ACTOR - %s Subscriber registered with DISPATCHER-ACTOR %s", this.self(), this.dispatcherActor));
        this.pendingInitializationSteps.remove(InitializationModules.DISPATCHER);
        if(this.pendingInitializationSteps.isEmpty()){
            this.rootActor.tell(new Initialized(), this.self());
        }
    }

    /**
     * onPublisherAck - Handler for the Publisher Ack message
     */
    private void onPublisherAck(){
        LOGGER.info(String.format("SUBSCRIBER-ACTOR - %s Subscription registered with publisher %s", this.self(), this.publisherActor));
        this.pendingInitializationSteps.remove(InitializationModules.PUBLISHER);
        if(this.pendingInitializationSteps.isEmpty()) {
            this.rootActor.tell(new Initialized(), this.self());
        }
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        LOGGER.info(String.format("SUBSCRIBER-ACTOR - %s started", this.self()));
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        LOGGER.info(String.format("SUBSCRIBER-ACTOR - %s shutdown", this.self()));
    }
}
