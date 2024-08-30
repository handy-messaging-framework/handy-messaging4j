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


package com.eclectique.messaging.mqttconnector.producersystem;

import com.eclectique.messaging.interfaces.Message;
import com.eclectique.messaging.interfaces.Producer;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * MQTTProducerSystem is a connector wrapper over MQTT Producer client.
 * This class is responsible for sending messages to the MQTT broker.
 */

public class MqttProducerSystem extends Producer {

    private IMqttClient publisherClient;
    private String publisherId;
    private int qos;
    private MqttConnectOptions connectionOptions;
    private Logger LOGGER = LoggerFactory.getLogger(MqttProducerSystem.class);

    /**
     * Constructor for MqttProducerSystem
     * @param producerBuilder - Builder for the producer
     */
    public MqttProducerSystem(MqttProducerBuilder producerBuilder){
        super(producerBuilder);
        try {
            this.publisherId = producerBuilder.getClientId();
            this.publisherClient = producerBuilder.getPublisher();
            this.qos = producerBuilder.getQos();
            this.connectionOptions = producerBuilder.getConnectOptions();
            LOGGER.info("MQTT Producer initialized for topic - {}", this.getQueueName());
        } catch (MqttException ex){
            throw new RuntimeException(ex.getMessage());
        }

    }

    /**
     * Send message to the MQTT broker
     * @param message - Message to be sent
     */
    @Override
    public void sendMessage(Message message) {
        try {
            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setPayload(message.serialize());
            mqttMessage.setQos(this.qos);
            publisherClient.connect(this.connectionOptions);
            publisherClient.publish(this.getQueueName(), mqttMessage);
            publisherClient.disconnect();
            LOGGER.info("MQTT Producer sent message to topic {}", this.getQueueName());

        } catch (MqttException ex){
            throw new RuntimeException(ex.getMessage());
        }
    }

    /**
     * Close the MQTT Producer Client
     */
    @Override
    public void close() {
        try {
            this.publisherClient.close();
            LOGGER.info("MQTT Producer for topic {} closed", this.getQueueName());
        } catch (MqttException ex){
            throw new RuntimeException(ex.getMessage());
        }
    }
}
