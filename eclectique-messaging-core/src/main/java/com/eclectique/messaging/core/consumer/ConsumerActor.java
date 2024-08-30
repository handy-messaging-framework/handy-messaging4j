/**
 * MIT License
 * <p>
 * Copyright (c) 2024 Aron Sajan Philip
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
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
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.eclectique.messaging.core.configuration.Profile;
import com.eclectique.messaging.interfaces.Consumer;
import com.eclectique.messaging.interfaces.EnqueueMessage;
import com.eclectique.messaging.interfaces.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Actor class to consume messages from the message queue
 */
public class ConsumerActor extends AbstractActor {

    /**
     * Message to poll data from the message queue
     */
    final static class PollData {
        int msgLimit;

        public PollData(int messageLimit) {
            this.msgLimit = messageLimit;
        }
    }

    ;

    /**
     * Message to interrupt polling. Sent by the ConsumerInterruptActor
     */
    final static class InterruptPolling {
        UUID sessionId;

        public InterruptPolling(UUID sessionId) {
            this.sessionId = sessionId;
        }

        @Override
        public int hashCode() {
            return sessionId.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof InterruptPolling) {
                InterruptPolling objX = (InterruptPolling) obj;
                return objX.sessionId.equals(this.sessionId);
            }
            return false;
        }
    }

    ;

    /**
     * Message to request subscription from a MessageChannelSubscriberActor
     */
    final static class SubscriptionRequest {
        ActorRef subscriberActor;

        public SubscriptionRequest(ActorRef subscriberActor) {
            this.subscriberActor = subscriberActor;
        }

        @Override
        public int hashCode() {
            return this.subscriberActor.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof SubscriptionRequest) {
                SubscriptionRequest subscriptionX = (SubscriptionRequest) obj;
                return subscriptionX.hashCode() == this.hashCode();
            }
            return false;
        }
    }

    /**
     * Message to indicate that data is available for consumption
     */
    final static class DataAvailable {
        List<Message> messageCollection;
        UUID sessionId;

        public DataAvailable(UUID sessionId, List<Message> messageCollection) {
            this.messageCollection = messageCollection;
            this.sessionId = sessionId;
        }
    }

    /**
     * Message to indicate that the consumer has been initialized
     */
    final class ConsumerInitialized {
    }

    /**
     * Message to acknowledge subscription request
     */
    final static class SubscriptionAck {
    }

    private Optional<ActorRef> subscriberActor;
    private Optional<UUID> currentSessionId;
    private List<Message> messageCollection;
    private Consumer consumer;
    private int maxMessagesPerBatch;
    private int dispatchLimit;
    private long maxPollIntervalMillis;
    private String channelId;
    private Logger LOGGER = LoggerFactory.getLogger(ConsumerActor.class);
    private ActorRef rootActor;

    /**
     * Constructor to initialize the consumer actor
     * @param profile The profile to use for consuming messages
     * @param rootActor The MessageChannelRootActor reference
     * @param queueName The name of the queue
     * @param messageTypeClass The class of the message type
     * @param channelId The channel ID
     * @param maxMessagesPerBatch The maximum number of messages to poll in a batch
     * @param maxPollIntervalMs The maximum poll interval in milliseconds
     */
    public ConsumerActor(Profile profile,
                         ActorRef rootActor,
                         String queueName,
                         String messageTypeClass,
                         String channelId,
                         int maxMessagesPerBatch,
                         long maxPollIntervalMs) {
        this.messageCollection = new ArrayList<>();
        this.rootActor = rootActor;
        this.consumer = new MessageConsumerBuilder()
                .setProfile(profile)
                .setQueueName(queueName)
                .setConsumerActor(this.self())
                .setMessageTypeClass(messageTypeClass)
                .build();
        this.channelId = channelId;
        this.maxMessagesPerBatch = maxMessagesPerBatch;
        this.maxPollIntervalMillis = maxPollIntervalMs;
        rootActor.tell(new ConsumerInitialized(), self());
    }

    public static Props getActorProperties(Profile profile,
                                           ActorRef rootActor,
                                           String queueName,
                                           String messageTypeClass,
                                           String channelId,
                                           int maxMessagePerBatch,
                                           long maxPollIntervalMs) {
        return Props.create(ConsumerActor.class,
                profile,
                rootActor,
                queueName,
                messageTypeClass,
                channelId,
                maxMessagePerBatch,
                maxPollIntervalMs);
    }

    @Override
    public Receive createReceive() {
        return new ReceiveBuilder().match(PollData.class, args -> {
                    LOGGER.info(String.format("CONSUMER-ACTOR %s received poll request from %s", this.self(), this.sender()));
                    LOGGER.info(String.format("Buffered messages now - %s", this.messageCollection.size()));
                    this.currentSessionId = Optional.of(UUID.randomUUID());
                    this.dispatchLimit = args.msgLimit;
                    if (this.messageCollection.size() < this.dispatchLimit) {
                        ActorRef interruptionActor = this.context().actorOf(ConsumerInterruptActor
                                        .getActorProperties(this.currentSessionId.get()),
                                String.format("INTERRUPTION-ACTOR-%s@%s", this.currentSessionId.get(), this.channelId));
                        interruptionActor.tell(new ConsumerInterruptActor.StartTimer(this.maxPollIntervalMillis),
                                this.self());
                        this.onPollData();
                    } else {
                        LOGGER.info(String.format("%s already had buffered data hence not polling", this.self()));
                        this.flushBufferedData();
                    }
                })
                .match(InterruptPolling.class, args -> {
                    this.sender().tell(PoisonPill.getInstance(), this.self());
                    UUID interruptionSessionId = args.sessionId;
                    LOGGER.info(String.format("Buffered messages now - %s", this.messageCollection.size()));
                    if (this.currentSessionId.isPresent() && this.currentSessionId.get().equals(interruptionSessionId)) {
                        this.flushBufferedData();
                    } else {
                        LOGGER.info(String.format("Stale interrupt received for session ID %s. Ignoring",
                                interruptionSessionId.toString()));
                    }
                })
                .match(SubscriptionRequest.class, args -> {
                    this.subscriberActor = Optional.of(args.subscriberActor);
                    args.subscriberActor.tell(new SubscriptionAck(), this.self());
                })
                .match(EnqueueMessage.class, args -> {
                    this.onMessageRecord(args.getMessage());
                }).build();
    }


    /**
     * Handler method for EnqueueMessage message. Buffers the data to a buffer memory
     * @param message The message to buffer
     */
    void onMessageRecord(Message message) {
        LOGGER.info(String.format("%s Buffering data", this.self()));
        this.messageCollection.add(message);
        if (messageCollection.size() >= this.maxMessagesPerBatch) {
            this.flushBufferedData();
        }
    }

    /**
     * Flush buffered data to the subscriber. Will get invoked when the buffer is full or when the consumer is
     * interrupted following a timeout
     */
    synchronized private void flushBufferedData() {
        if (currentSessionId.isPresent()) {
            this.consumer.stopPolling();
            UUID sessionId = this.currentSessionId.get();
            this.currentSessionId = Optional.empty();
            this.subscriberActor.ifPresent(subscriber -> {
                int dispatchSize = (this.messageCollection.size() > this.dispatchLimit) ? this.dispatchLimit : this.messageCollection.size();
                LOGGER.info(String.format("%s flushing %s messages to subscriber", this.self(), dispatchSize));
                List<Message> dispatchMessages = new ArrayList<>(this.messageCollection.subList(0, dispatchSize));
                this.messageCollection.removeAll(dispatchMessages);
                subscriber.tell(new DataAvailable(sessionId, dispatchMessages), this.self());
            });
        }
    }

    /**
     * Handler for PollData message. Polls data from the message queue
     */
    private void onPollData() {
        if (this.messageCollection.size() >= this.maxMessagesPerBatch) {
            this.flushBufferedData();
        } else {
            this.consumer.startPolling();
        }
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        LOGGER.info(String.format("CONSUMER-ACTOR %s started", this.self()));
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        LOGGER.info(String.format("CONSUMER-ACTOR %s shut down", this.self()));
    }
}
