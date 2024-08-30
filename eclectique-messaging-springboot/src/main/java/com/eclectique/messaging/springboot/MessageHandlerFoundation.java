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

import com.eclectique.messaging.core.consumer.MessageConsumingSystem;
import com.eclectique.messaging.interfaces.MessageHandler;
import jakarta.annotation.PostConstruct;

/**
 * Abstract class that provides the foundation for creating a message handler
 */
public abstract class MessageHandlerFoundation implements MessageHandler {

    protected abstract String getQueueName();

    protected abstract String getProfileName();

    protected abstract String getMessageTypeClass();

    /**
     * Function that initializes the message handler. The function registers the handler
     * with the consumer assoiated with th profile and queueName
     */

    @PostConstruct
    void initializeMessageHandler(){
        MessageConsumingSystem.getInstance().setupConsumer(this.getProfileName(),
                this.getQueueName(),
                this.getMessageTypeClass(),
                this);
    }
}
