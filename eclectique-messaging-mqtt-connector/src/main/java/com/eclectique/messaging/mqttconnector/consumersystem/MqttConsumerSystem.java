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

package com.eclectique.messaging.mqttconnector.consumersystem;

import com.eclectique.messaging.interfaces.Consumer;
import com.eclectique.messaging.interfaces.Message;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MqttConsumerSystem is the connector wrapping a MQTT consumer. This class is responsible for receiving messages from the MQTT broker.
 */
public class MqttConsumerSystem extends Consumer {

    private IMqttClient consumerClient;
    private String messageTypeClass;
    private Logger LOGGER = LoggerFactory.getLogger(MqttConsumerSystem.class);
    private String queueName;
    private MqttConnectOptions connectOptions;

    /**
     * Constructor for MqttConsumerSystem
     * @param consumerBuilder - Builder for the consumer
     */
    public MqttConsumerSystem(MqttConsumerBuilder consumerBuilder){
        super(consumerBuilder.getConsumerActor());
        try {
            this.consumerClient = consumerBuilder.getConsumer();
            this.queueName = consumerBuilder.getQueueName();
            this.messageTypeClass = consumerBuilder.getMessageTypeClass();
            this.connectOptions = consumerBuilder.getConnectOptions();
            this.consumerClient.setCallback(new MqttCallbackHandler(this));
            LOGGER.info("MQTT consumer initialized for topic {}", this.queueName);
        } catch (MqttException ex){
            throw new RuntimeException(ex.getMessage());
        }
    }

    /**
     * Callback for message received
     * @param msg - Message received
     */
    public void onMessage(Message msg){
        this.onMessageReceived(msg);
    }

    /**
     * Start polling for messages
     */
    @Override
    public void startPolling() {
        try {
            this.consumerClient.connect(this.connectOptions);
            this.consumerClient.subscribe(this.queueName);
        } catch (MqttException ex){
            LOGGER.error(String.format("MQTT connection error for topic %s. Details - %s",
                    this.queueName,
                    ex.getMessage()));
        }
    }

    /**
     * Stop polling for messages
     */
    @Override
    public void stopPolling() {
        try {
            this.consumerClient.disconnect();
        } catch (MqttException ex){
            LOGGER.error(String.format("MQTT disconnect error for topic %s. Details - %s",
                    this.queueName,
                    ex.getMessage()));
        }
    }

    /**
     * Get the message type class
     * @return - String representing the message type class
     */
    @Override
    protected String getMessageTypeClass() {
        return this.messageTypeClass;
    }
}
