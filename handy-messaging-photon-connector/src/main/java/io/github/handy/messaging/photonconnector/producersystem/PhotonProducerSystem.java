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

package io.github.handy.messaging.photonconnector.producersystem;

import io.github.handy.messaging.interfaces.Message;
import io.github.handy.messaging.interfaces.Producer;
import io.github.handy.messaging.photon.clients.PhotonProducer;
import io.github.handy.messaging.photon.types.responses.CommandExecutionStatus;
import io.github.handy.messaging.photon.types.responses.CommandResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The PhotonProducerSystem is the connector wrapping a PhotonProducer. It is responsible for sending messages
 * to the Photon Messaging Queue
 */
public class PhotonProducerSystem extends Producer {

    private String messagingServiceInstance;
    private PhotonProducer producer;
    private Logger LOGGER = LoggerFactory.getLogger(PhotonProducerSystem.class);

    /**
     * Constructor for initializing the Photon Producer instance
     * @param producerBuilder ProducerBuilder instance
     */
    public PhotonProducerSystem(PhotonProducerBuilder producerBuilder){
        super(producerBuilder);
        this.messagingServiceInstance = producerBuilder.getPhotonMessagingInstance();
        this.producer = new PhotonProducer(this.messagingServiceInstance);
        LOGGER.info("Photon Producer System initialized");
    }

    /**
     * Sends a message to the Photon Messaging Queue
     * @param message Message to be sent
     */
    @Override
    public void sendMessage(Message message) {
        CommandResponse response = producer.sendMessage(this.getQueueName(), message);
        if(response.getCommandExecutionStatus()== CommandExecutionStatus.FAILED){
            throw new RuntimeException(String.format(" Failed to send message to (%s, %s)", this.messagingServiceInstance, this,getQueueName()));
        }
    }

    @Override
    public void sendMessage(String key, Message message) {
        LOGGER.warn(String.format("Photon Messaging Producer does not need key. Ignoring the key %s", key));
        sendMessage(message);
    }

    /**
     * Closes the Photon Producer Instance
     */
    @Override
    public void close() {}
}
