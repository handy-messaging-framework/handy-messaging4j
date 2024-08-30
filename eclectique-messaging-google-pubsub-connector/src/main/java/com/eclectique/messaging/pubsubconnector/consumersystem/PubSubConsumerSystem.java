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

package com.eclectique.messaging.pubsubconnector.consumersystem;

import com.eclectique.messaging.interfaces.Consumer;
import com.eclectique.messaging.interfaces.Message;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.PubsubMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class PubSubConsumerSystem extends Consumer {

    private AtomicBoolean interruptionFlag;

    private Logger LOGGER = LoggerFactory.getLogger(PubSubConsumerSystem.class);

    private Subscriber subscriber;

    private String subscriptionName;
    private CredentialsProvider credentialsProvider;

    private TransportChannelProvider channelProvider;

    Future pollThreadHandle;

    private boolean emulatorFlag;

    ExecutorService pollThreadManager;

    List<PubSubMsgPackage> polledMessages;

    String messageTypeClass;

    public PubSubConsumerSystem(PubSubConsumerBuilder builder){
        super(builder.getConsumerActor());
        this.subscriptionName = builder.getSubscriptionName();
        this.credentialsProvider = builder.getCredentialsProvider();
        this.channelProvider = builder.getTransportChannelProvider();
        this.emulatorFlag = builder.isEmulatorFlag();
        this.pollThreadManager = Executors.newFixedThreadPool(1);
        polledMessages = new ArrayList<>();
        this.interruptionFlag = new AtomicBoolean(false);
        this.messageTypeClass = builder.getMessageTypeClass();


    }

    private void buildSubscriber(){
        if(this.emulatorFlag){
            this.subscriber = Subscriber.newBuilder(this.subscriptionName, this::onMessageReceipt)
                    .setCredentialsProvider(this.credentialsProvider)
                    .setChannelProvider(this.channelProvider)
                    .build();
        } else {
            this.subscriber = Subscriber.newBuilder(this.subscriptionName, this::onMessageReceipt).build();
        }
    }
    @Override
    public void startPolling() {
        LOGGER.info("Polling Started");
        this.interruptionFlag.set(false);
        this.pollThreadHandle = this.pollThreadManager.submit(()->{
            LOGGER.info("Processing Thread - "+Thread.currentThread().getName());
            while(!interruptionFlag.get()){
                List<PubSubMsgPackage> collectedMessages = pollMessages(500);
                collectedMessages.stream().map(pubSubMessage -> {
                    Message msg = this.getMessage(pubSubMessage.getMessage().getData().toByteArray());
                    return msg;
                }).forEach(message -> {
                    super.onMessageReceived(message);
                });

                collectedMessages.forEach(pubSubMsg -> {
                    try {
                        pubSubMsg.getSender().ack();
                    } catch (Exception ex){
                        LOGGER.info("ACK FAILED. Reason - "+ex.getMessage());
                    }
                });
            }
            LOGGER.info("Interrupted");
        });

    }

    private List<PubSubMsgPackage> pollMessages(long waitDurationMs){
        try {
            this.buildSubscriber();
            this.subscriber.startAsync().awaitRunning();
            Thread.sleep(waitDurationMs);
            this.subscriber.stopAsync().awaitTerminated(100, TimeUnit.MILLISECONDS);
        } catch (TimeoutException timeoutException){

        } catch (Exception ex){
            LOGGER.error(String.format("PubSub polling failure. Details - %s", ex.getMessage()));
        }
        List<PubSubMsgPackage> collectedMessages = new ArrayList<>(this.polledMessages);
        this.polledMessages.clear();
        return collectedMessages;
    }

    private void onMessageReceipt(PubsubMessage pubSubMsg, AckReplyConsumer consumer){
        LOGGER.info("----------Received Message-----------");
        this.polledMessages.add(new PubSubMsgPackage(pubSubMsg, consumer));
    }


    @Override
    public void stopPolling() {
        this.interruptionFlag.set(true);
        try {
            this.pollThreadHandle.get();
        } catch (Exception ex){
            LOGGER.info("Interrupt failed - "+ex.getMessage());
        }
        LOGGER.info("Interrupt received - "+Thread.currentThread().getName());
    }

    @Override
    protected String getMessageTypeClass() {
        return this.messageTypeClass;
    }
}
