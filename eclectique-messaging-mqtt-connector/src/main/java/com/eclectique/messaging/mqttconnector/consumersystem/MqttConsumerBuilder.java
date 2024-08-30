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

import akka.actor.ActorRef;
import com.eclectique.messaging.interfaces.Consumer;
import com.eclectique.messaging.interfaces.ConsumerBuilder;
import com.eclectique.messaging.mqttconnector.ClientBuilderBase;
import com.eclectique.messaging.mqttconnector.Constants;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Map;

/**
 * Builder class for Mqtt Consumer
 */
public class MqttConsumerBuilder extends ClientBuilderBase implements ConsumerBuilder {

    Map<String, Object> consumerProps;

    /**
     * Set the consumer properties
     * @param properties - Map of properties
     * @return - ConsumerBuilder
     */
    @Override
    public ConsumerBuilder setConsumerProperties(Map<String, Object> properties) {
        this.consumerProps = properties;
        return this;
    }

    /**
     * Get the queue name
     * @return - String representing the queue name
     */
    @Override
    public String getQueueName() {
        return this.consumerProps.get(Constants.QUEUE_NAME).toString();
    }

    /**
     * Get the message type class
     * @return - String representing the message type class
     */
    @Override
    public String getMessageTypeClass() {
        return this.consumerProps.get(Constants.MESSAGE_TYPE_CLASS).toString();
    }

    /**
     * Get the consumer actor
     * @return - ActorRef representing the consumer actor
     */
    @Override
    public ActorRef getConsumerActor() {
        return (ActorRef) this.consumerProps.get(Constants.CONSUMER_ACTOR);
    }

    /**
     * Build the consumer
     * @return - Consumer instance
     */
    @Override
    public Consumer build() {
        return new MqttConsumerSystem(this);
    }

    /**
     * Get the consumer properties
     * @return - Map of properties
     */
    @Override
    protected Map<String, Object> getClientProperties() {
        return this.consumerProps;
    }

    /**
     * Get the MQTT client instance
     * @return - IMqttClient instance
     * @throws MqttException - Exception thrown if there is an error in creating the client
     */
    public IMqttClient getConsumer() throws MqttException {
        MqttClient client = new MqttClient(super.getConnectionString(),this.clientId);
        return client;
    }

    /**
     * Get the MQTT connection options
     * @return - MqttConnectOptions instance
     */
    public MqttConnectOptions getConnectOptions(){
        MqttConnectOptions connectOptions = new MqttConnectOptions();
        super.populateConnectionProperty(Constants.CONNECTION_TIMEOUT, connectOptions);
        super.populateConnectionProperty(Constants.AUTOMATIC_RECONNECT_FLAG, connectOptions);
        super.populateConnectionProperty(Constants.CLEAN_SESSION_FLAG, connectOptions);
        super.populateConnectionProperty(Constants.USERNAME, connectOptions);
        super.populateConnectionProperty(Constants.PASSWORD, connectOptions);
        return connectOptions;
    }

    /**
     * Get the client id
     * @return - String representing the client id
     */
    public String getClientId(){
        return super.clientId;
    }
}
