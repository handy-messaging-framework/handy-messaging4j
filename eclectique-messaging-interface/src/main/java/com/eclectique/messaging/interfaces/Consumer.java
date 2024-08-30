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

package com.eclectique.messaging.interfaces;

import akka.actor.ActorRef;

/**
 * Abstract class for a consumer. A consumer is an entity that reads messages from a channel and processes them.
 */
public abstract class Consumer{

    private ActorRef consumerActor;

    /**
     * Constructor for the consumer
     * @param consumerActor - Actor reference for the consumer
     */
    public Consumer(ActorRef consumerActor){
        this.consumerActor = consumerActor;
    }

    /**
     * Function to start polling the channel for messages
     */
    abstract public void startPolling();

    /**
     * Function to deserialize the message
     * @param serializedMessage - Serialized message
     * @return - Deserialized message
     */
    public Message getMessage(byte[] serializedMessage){
        try {
            Class<?> messageTypeClass = Class.forName(this.getMessageTypeClass());
            Message messageInstance = (Message) messageTypeClass.getConstructor().newInstance();
            messageInstance.deserialize(serializedMessage);
            return messageInstance;
        } catch(Exception ex){
            throw new RuntimeException(String.format("Deserialization failed. Message type - %s. Exception details - %s",
                    this.getMessageTypeClass(),
                    ex.getMessage()));
        }
    }

    /**
     * Function to stop polling the channel for messages
     */
    abstract public void stopPolling();

    /**
     * Function to get message type class
     * @return - String representing the message type class
     */
    abstract protected String getMessageTypeClass();

    /**
     * Function to be called when a message is received
     * @param msg - Message received
     */
    protected void onMessageReceived(Message msg){
        this.consumerActor.tell(new EnqueueMessage(msg), ActorRef.noSender());
    }


}
