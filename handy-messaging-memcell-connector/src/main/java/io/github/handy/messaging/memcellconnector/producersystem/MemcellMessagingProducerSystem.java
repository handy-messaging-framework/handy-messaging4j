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

package io.github.handy.messaging.memcellconnector.producersystem;

import io.github.handy.messaging.interfaces.Message;
import io.github.handy.messaging.interfaces.Producer;
import io.github.handy.messaging.memcell.clients.MemcellMessagingProducer;
import io.github.handy.messaging.memcell.types.responses.CommandExecutionStatus;
import io.github.handy.messaging.memcell.types.responses.CommandResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The MemcellMessagingProducerSystem is the connector wrapping a MemcellMessagingProducer. It is responsible for sending messages
 * to the Memcell Messaging Queue
 */
public class MemcellMessagingProducerSystem extends Producer {

    private String messagingServiceInstance;
    private MemcellMessagingProducer producer;
    private Logger LOGGER = LoggerFactory.getLogger(MemcellMessagingProducerSystem.class);

    /**
     * Constructor for initializing the Memcell Messaging Producer instance
     * @param producerBuilder ProducerBuilder instance
     */
    public MemcellMessagingProducerSystem(MemcellMessagingProducerBuilder producerBuilder){
        super(producerBuilder);
        this.messagingServiceInstance = producerBuilder.getMemcellMessagingInstance();
        this.producer = new MemcellMessagingProducer(this.messagingServiceInstance);
        LOGGER.info("Memcell Messaging Producer System initialized");
    }

    /**
     * Sends a message to the Memcell Messaging Queue
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
        LOGGER.warn(String.format("Memcell Messaging Producer does not need key. Ignoring the key %s", key));
        sendMessage(message);
    }

    /**
     * Closes the Memcell Messaging Producer Instance
     */
    @Override
    public void close() {}
}
