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

package io.github.handy.messaging.photonconnector.consumersystem;

import io.github.handy.messaging.interfaces.Consumer;
import io.github.handy.messaging.interfaces.Message;
import io.github.handy.messaging.memcell.clients.MemcellMessagingConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * PhotonConsumerSystem is the connector wrapping a Photon Consumer.
 * It is responsible for pulling messages from the Phtoton Messaging Queue
 */
public class PhotonConsumerSystem extends Consumer {

    private ExecutorService pollThreadManager;
    private MemcellMessagingConsumer consumer;
    private Future pollHandle;
    private boolean interruptedFlag;
    private String consumerId, messageType;
    private Logger LOGGER = LoggerFactory.getLogger(PhotonConsumerSystem.class);

    /**
     * Constructor for PhotonConsumerSystem
     * @param consumerBuilder ConsumerBuilder instance
     */
    public PhotonConsumerSystem(PhotonConsumerBuilder consumerBuilder) {
        super(consumerBuilder.getConsumerActor());
        this.messageType = consumerBuilder.getMessageTypeClass();
        this.pollThreadManager = Executors.newFixedThreadPool(1);
        try {
            this.consumerId = String.format("consumer_%s_%s", consumerBuilder.getApplicationId(),
                    consumerBuilder.getQueueName());
            this.consumer = new MemcellMessagingConsumer(consumerBuilder.getPhotonMessagingInstance(),
                    consumerBuilder.getQueueName(),
                    consumerId,
                    Class.forName(this.getMessageTypeClass()));
        } catch (ClassNotFoundException ex){
            throw new RuntimeException(ex.getMessage());
        }
        LOGGER.info(String.format("Photon consumer for queue %s initialized", consumerBuilder.getQueueName()));
    }

    /**
     * Method to start polling messages from the Photon Messaging Queue
     */
    @Override
    public void startPolling() {
        this.interruptedFlag = false;
        this.pollHandle = this.pollThreadManager.submit(()->{
            while(!this.interruptedFlag){
               List<Message> collectedMessages = this.consumer.readMessages(50);
              collectedMessages.forEach(this::onMessageReceived);
            }
        });
    }

    /**
     * Method to stop polling messages from the Photon Messaging Queue
     */
    @Override
    public void stopPolling() {
        this.interruptedFlag = true;
        try {
            this.pollHandle.get();
        } catch (Exception ex){
            throw new RuntimeException(String.format("Photon consumer interruption failed. Details - %s", ex.getMessage()));
        }
    }

    /**
     * Method to get the message type class
     * @return String
     */
    @Override
    protected String getMessageTypeClass() {
        return this.messageType;
    }
}
