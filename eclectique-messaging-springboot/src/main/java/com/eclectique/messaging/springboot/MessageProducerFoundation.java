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

package com.eclectique.messaging.springboot;

import com.eclectique.messaging.core.producer.MessageProducerSystem;
import com.eclectique.messaging.interfaces.Message;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class that provides the foundation for creating a message producer
 */
public abstract class MessageProducerFoundation {

    protected abstract String getQueueName();

    protected abstract String getProfile();

    private MessageProducerSystem producerSystem;

    private Logger LOGGER = LoggerFactory.getLogger(MessageProducerFoundation.class);

    /**
     * Constructor that initializes the producer system
     */
    public MessageProducerFoundation(){

        this.producerSystem = new MessageProducerSystem(getProfile(), getQueueName());
        LOGGER.info(String.format("PRODUCER - %s - %s initialized",
                this.getProfile(),
                this.getQueueName()));

    }

    /**
     * Function that acts as a wrapper to send messages to the queue
     * @param message Message to be sent
     */
    public void sendMessage(Message message){
        LOGGER.info(String.format("Sending message to queue %s using profile %s",
                this.getQueueName(),
                this.getProfile()));
        this.producerSystem.sendMessage(message);
    }

    /**
     * Function that closes the producer system
     */
    @PreDestroy
    private void clear(){
        this.producerSystem.close();
        LOGGER.info(String.format("PRODUCER - %s - %s closed",
                this.getProfile(),
                this.getQueueName()));
    }
}
