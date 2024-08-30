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

import com.eclectique.messaging.interfaces.Producer;
import com.eclectique.messaging.interfaces.ProducerBuilder;
import com.eclectique.messaging.mqttconnector.ClientBuilderBase;
import com.eclectique.messaging.mqttconnector.Constants;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import java.util.Map;

/**
 * MqttProducerBuilder is the builder class for the MqttProducerSystem instance
 */
public class MqttProducerBuilder extends ClientBuilderBase implements ProducerBuilder  {

    private Map<String, Object> producerProps;

    /**
     * Set the producer properties
     * @param props - Map of producer properties
     * @return ProducerBuilder
     */
    @Override
    public ProducerBuilder setProducerProperties(Map<String, Object> props) {
        this.producerProps = props;
        return this;
    }

    /**
     * Get the queue name
     * @return String representing the queue name
     */
    @Override
    public String getQueueName() {
        return this.producerProps.get(Constants.QUEUE_NAME).toString();
    }

    /**
     * Build the producer instance
     * @return Producer instance
     */
    @Override
    public Producer build() {
        return new MqttProducerSystem(this);
    }

    /**
     * Get the MQTT connection options
     * @return
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
     * Get the QoS level
     * @return int representing the QoS level
     */
    public int getQos(){
        return (int) this.producerProps.getOrDefault(Constants.QOS, 0);
    }

    /**
     * Get the client ID
     * @return String representing the client ID
     */
    public String getClientId(){
        return super.clientId;
    }

    /**
     * Get the MQTT publisher client
     * @return IMqttClient instance
     * @throws MqttException
     */
    public IMqttClient getPublisher() throws MqttException {
        return new MqttClient(super.getConnectionString(), this.clientId);
    }

    /**
     * Get the client properties
     * @return Map of client properties
     */
    @Override
    protected Map<String, Object> getClientProperties() {
        return this.producerProps;
    }
}
