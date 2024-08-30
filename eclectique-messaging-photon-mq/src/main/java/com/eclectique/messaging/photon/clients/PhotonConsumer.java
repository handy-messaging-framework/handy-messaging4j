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

package com.eclectique.messaging.photon.clients;

import com.eclectique.messaging.interfaces.Message;
import com.eclectique.messaging.photon.messagingservice.CommandCapsuleBroker;
import com.eclectique.messaging.photon.types.commands.FetchEnqueuedMessageCommand;
import com.eclectique.messaging.photon.types.commands.RegisterSubscriberCommand;
import com.eclectique.messaging.photon.types.commands.UnregisterSubscriberCommand;
import com.eclectique.messaging.photon.types.responses.CommandExecutionStatus;
import com.eclectique.messaging.photon.types.responses.CommandResponse;
import com.eclectique.messaging.photon.types.responses.FetchEnqueuedMessagesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
/**
 * PhotonConsumer is a client class that reads messages from a PhotonMQ queue
 */
public class PhotonConsumer implements AutoCloseable {

    String serviceInstanceId, queueName, consumerId;
    Class<?> messageType;

    private Logger LOGGER = LoggerFactory.getLogger(PhotonConsumer.class);

    /**
     * Constructor to create a PhotonConsumer instance
     * @param serviceInstanceId - Instance ID of the messaging service
     * @param queueName - Name of the queue to read messages from
     * @param consumerId - Consumer ID
     * @param messageType - Type of message to read
     */
    public PhotonConsumer(String serviceInstanceId, String queueName, String consumerId, Class<?> messageType){
        this.serviceInstanceId = serviceInstanceId;
        this.consumerId = consumerId;
        this.queueName = queueName;
        this.messageType = messageType;
        try {
            RegisterSubscriberCommand registerSubscriberCommand = new RegisterSubscriberCommand(queueName, consumerId);
            CommandResponse subscriberRegisterResponse = new CommandCapsuleBroker<CommandResponse>()
                    .sendCommandCapsule(registerSubscriberCommand, serviceInstanceId)
                    .get();
            if(subscriberRegisterResponse.getCommandExecutionStatus()== CommandExecutionStatus.FAILED){
                throw new RuntimeException("Failed to register consumer");
            }
        } catch (InterruptedException | ExecutionException ex){
            throw new RuntimeException(ex.getMessage());
        }
    }

    /**
     * Function to read messages from the queue
     * @param waitDurationMillis - Duration to wait for messages
     * @param <T> - Type of message to read
     * @return List of messages read
     */
    public <T extends Message> List<T> readMessages(long waitDurationMillis){
        try {
            Thread.sleep(waitDurationMillis);
            FetchEnqueuedMessageCommand fetchEnqueuedCommand = new FetchEnqueuedMessageCommand(this.queueName, this.consumerId);
            FetchEnqueuedMessagesResponse enqueuedMessageResponse = new CommandCapsuleBroker<FetchEnqueuedMessagesResponse>()
                    .sendCommandCapsule(fetchEnqueuedCommand, this.serviceInstanceId).get();
            if(enqueuedMessageResponse.getCommandExecutionStatus()==CommandExecutionStatus.SUCCESS) {
                List<T> collectedMessages = new ArrayList<>();
                enqueuedMessageResponse.getFetchedMessages().forEach(messageByteData -> {
                    try {
                        T messageInstance = (T) this.messageType.getConstructor().newInstance();
                        messageInstance.deserialize(messageByteData);
                        collectedMessages.add(messageInstance);
                    } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                             InvocationTargetException ex){
                        throw new RuntimeException(ex.getMessage());
                    }
                });
                return collectedMessages;
            } else {
                return new ArrayList<>();
            }
        } catch (InterruptedException ex){
            throw new RuntimeException(ex.getMessage());
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Function to close the consumer
     */
    @Override
    public void close(){
        UnregisterSubscriberCommand unsubscribeCommand = new UnregisterSubscriberCommand(this.queueName,
                this.consumerId);
        try {
            CommandResponse unsubscribeResponse = new CommandCapsuleBroker<CommandResponse>()
                    .sendCommandCapsule(unsubscribeCommand, this.serviceInstanceId).get();
            if(unsubscribeResponse.getCommandExecutionStatus()==CommandExecutionStatus.FAILED){
                LOGGER.error(String.format("Unsubscribe Failed - %s", this.consumerId));
            }
        } catch (InterruptedException | ExecutionException ex){
            throw new RuntimeException(ex.getMessage());
        }
    }

}
