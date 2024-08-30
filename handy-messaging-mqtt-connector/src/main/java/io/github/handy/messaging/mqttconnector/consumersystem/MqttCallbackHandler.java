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

package io.github.handy.messaging.mqttconnector.consumersystem;

import io.github.handy.messaging.interfaces.Message;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Callback handler for MQTT
 */
public class MqttCallbackHandler implements MqttCallback {

    Logger LOGGER = LoggerFactory.getLogger(MqttCallbackHandler.class);
    MqttConsumerSystem consumerSystem;

    /**
     * Constructor for initializing the callback handler
     * @param consumer - MqttConsumerSystem instance
     */
    public MqttCallbackHandler(MqttConsumerSystem consumer){
        this.consumerSystem = consumer;
    }

    /**
     * Method to handle connection loss
     * @param throwable - Throwable instance
     */
    @Override
    public void connectionLost(Throwable throwable) {
        LOGGER.error(String.format("MQTT connection lost. Details - %s", throwable.getMessage()));

    }

    /**
     * Method to handle message arrival
     * @param s - Topic
     * @param mqttMessage - MqttMessage instance
     * @throws Exception - Exception
     */
    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        byte[] payload = mqttMessage.getPayload();
        Message deserializedMsg = this.consumerSystem.getMessage(payload);
        this.consumerSystem.onMessage(deserializedMsg);
    }

    /**
     * Method to handle delivery completion
     * @param iMqttDeliveryToken - IMqttDeliveryToken instance
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        LOGGER.info(String.format("MQTT delivery completion statuus - %s", iMqttDeliveryToken.isComplete()));

    }
}
