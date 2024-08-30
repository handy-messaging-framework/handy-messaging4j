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

package io.github.handy.messaging.core.consumer;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import io.github.handy.messaging.core.configuration.ConfigurationConstants;
import io.github.handy.messaging.core.configuration.Profile;
import io.github.handy.messaging.core.consumer.dispatcher.MessageChannelDispatcherActor;
import io.github.handy.messaging.interfaces.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Root actor for the message channel. This actor is responsible for creating the publisher, subscriber and dispatcher actors
 * and initializing them.
 */
public class MessageChannelRootActor extends AbstractActor {

    private ActorRef publisherActor;
    private ActorRef subscriberActor;
    private ActorRef dispatcherActor;
    private String channelId;
    private Logger LOGGER = LoggerFactory.getLogger(MessageChannelRootActor.class);

    /**
     * Message to start the consumer channel
     */
    public static final class StartConsumerChannel {}



    ActorInitializationCallback initializationCompleteCallback;

    /**
     * Constructor for the root actor
     * @param profile Profile object
     * @param queueName Name of the queue
     * @param messageTypeClass Class of the message type
     * @param messageHandler Message handler
     * @param onInitializationComplete Callback to be invoked after initialization
     */
    public MessageChannelRootActor(Profile profile,
                                   String queueName,
                                   String messageTypeClass,
                                   MessageHandler messageHandler,
                                   ActorInitializationCallback onInitializationComplete){
        this.channelId = String.format("CHANNEL-%s-%s", profile.getProfileName(), queueName);
        this.channelId = this.channelId.replace('/', '_');
        this.channelId = this.channelId.replace('+', '_');
        this.initializationCompleteCallback = onInitializationComplete;

        int maxMessagesPerBatch = Integer.parseInt(profile.getConsumerProperties()
                .getProps()
                .get(ConfigurationConstants.Consumer.MAX_MSG_PER_BATCH).toString());
        long maxPollDurationMs =  Long.parseLong(profile.getConsumerProperties()
                .getProps()
                .get(ConfigurationConstants.Consumer.MAX_POLL_INTERVAL_MS).toString());

        this.publisherActor = this.context().actorOf(ConsumerActor.getActorProperties(profile,
                self(),
                queueName,
                messageTypeClass,
                this.channelId,
                maxMessagesPerBatch,
                maxPollDurationMs), String.format("CONSUMER-ACTOR@%s", this.channelId));

        this.dispatcherActor = this.context().actorOf(MessageChannelDispatcherActor.getActorProperties(this.channelId, messageHandler),
                String.format("DISPATCHER-ACTOR@%s", this.channelId));

    }


    /**
     * Handler for the StartConsumerChannel message
     */
    private void onStartConsumerChannel(){
        this.publisherActor.tell(new ConsumerActor.PollData(3), this.self());
    }

    /**
     * Handler for the ConsumerInitialized message
     */
    private void onMessagePublisherInitialized(){
        this.subscriberActor = this.context().actorOf(MessageChannelSubscriberActor.getActorProperties(this.channelId,
                this.self(),
                this.publisherActor,
                this.dispatcherActor), String.format("SUBSCRIBER-ACTOR@%s", this.channelId));
        this.subscriberActor.tell(new MessageChannelSubscriberActor.Initialize(), this.self());
    }

    /**
     * Handler for the SubscriberInitialized message
     */
    private void onSubscriberInitialized(){
        this.initializationCompleteCallback.afterInitialize(self());
    }

    @Override
    public Receive createReceive() {
        return new ReceiveBuilder().match(MessageChannelSubscriberActor.Initialized.class, args->{
            this.onSubscriberInitialized();
        }).match(StartConsumerChannel.class, args->{
            this.onStartConsumerChannel();
        }).match(ConsumerActor.ConsumerInitialized.class, args->{
            this.onMessagePublisherInitialized();
        }).build();
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        LOGGER.info(String.format("ROOT-ACTOR %s STARTED", this.self()));
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        LOGGER.info(String.format("ROOT-ACTOR %s SHUT DOWN", this.self()));
    }

    public static Props getActorProperties(Profile profile, String queueName, String messageTypeClass, MessageHandler messageHandler, ActorInitializationCallback onInitializationCallback){
        return Props.create(MessageChannelRootActor.class, profile, queueName, messageTypeClass, messageHandler, onInitializationCallback);
    }
}
