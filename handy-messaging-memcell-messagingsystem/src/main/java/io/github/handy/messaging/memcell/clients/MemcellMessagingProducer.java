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

package io.github.handy.messaging.memcell.clients;

import io.github.handy.messaging.interfaces.Message;
import io.github.handy.messaging.memcell.messagingservice.CommandCapsuleBroker;
import io.github.handy.messaging.memcell.types.commands.EnqueueMessageCommand;
import io.github.handy.messaging.memcell.types.responses.CommandResponse;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Memcell Messaging Producer is a client class that sends messages to a Memcell Messaging queue
 */
public class MemcellMessagingProducer {

    String serviceInstanceId;
    public MemcellMessagingProducer(String serviceInstanceId){
        this.serviceInstanceId = serviceInstanceId;
    }

    /**
     * Function to send a message to a Memcell Messaging queue synchronously
     * @param queueName - Name of the queue
     * @param message - Message to send
     * @return CommandResponse
     */
    public CommandResponse sendMessage(String queueName, Message message){
        message.buildMessage();
        EnqueueMessageCommand enqueueCommand = new EnqueueMessageCommand(queueName, message.serialize());
        try {
            FutureTask<CommandResponse> enqueueResponse = new CommandCapsuleBroker<CommandResponse>()
                    .sendCommandCapsule(enqueueCommand, this.serviceInstanceId);
            return enqueueResponse.get();
        } catch (InterruptedException | ExecutionException ex){
            throw new RuntimeException(ex.getMessage());
        }
    }

    /**
     * Function to send a message to a Memcell Messaging queue asynchronously
     * @param queueName - Name of the queue
     * @param message - Message to send
     * @return CommandResponse future
     */
    public FutureTask<CommandResponse> asyncSendMessage(String queueName, Message message){
        message.buildMessage();
        EnqueueMessageCommand enqueueCommand = new EnqueueMessageCommand(queueName,
                message.serialize());
        return new CommandCapsuleBroker<CommandResponse>()
                .sendCommandCapsule(enqueueCommand, this.serviceInstanceId);
    }

}
