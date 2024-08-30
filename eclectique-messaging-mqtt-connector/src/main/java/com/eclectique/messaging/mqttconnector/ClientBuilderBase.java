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


package com.eclectique.messaging.mqttconnector;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

/**
 * ClientBuilderBase is the base class for building a MQTT client. This class is responsible for setting up the
 * connection properties for the client.
 */
public abstract class ClientBuilderBase {

    protected String clientId;
    private Logger LOGGER = LoggerFactory.getLogger(ClientBuilderBase.class);

    /**
     * Constructor for ClientBuilderBase
     */
    public ClientBuilderBase(){
        this.clientId = UUID.randomUUID().toString();
    }

    /**
     * Method to populate the connection properties for the client
     * @param connectionPropertyKey - Key for the connection property
     * @param connectionSettings - Connection settings for the client
     */
    protected void populateConnectionProperty(String connectionPropertyKey, MqttConnectOptions connectionSettings){
        if(this.getClientProperties().containsKey(connectionPropertyKey)){
            Object propertyValue = this.getClientProperties().get(connectionPropertyKey);
            switch(connectionPropertyKey){
                case Constants.AUTOMATIC_RECONNECT_FLAG:
                    connectionSettings.setAutomaticReconnect((boolean) propertyValue);
                    break;
                case Constants.USERNAME:
                    connectionSettings.setUserName(propertyValue.toString());
                    break;
                case Constants.PASSWORD:
                    connectionSettings.setPassword((propertyValue.toString().toCharArray()));
                    break;
                case Constants.CLEAN_SESSION_FLAG:
                    connectionSettings.setCleanSession((boolean) propertyValue);
                    break;
                case Constants.CONNECTION_TIMEOUT:
                    connectionSettings.setConnectionTimeout((int)propertyValue);
                    break;
                default:
                    LOGGER.error("Mqtt Client - Unknown property provided. Ignoring");
            }
        }
    }

    /**
     * Method to get the connection string for the client
     * @return - Connection string for the client
     */
    protected String getConnectionString(){
        String hostname = this.getClientProperties().get(Constants.CONNECTION_HOST).toString();
        int port = (int) this.getClientProperties().get(Constants.CONNECTION_PORT);
        return String.format("tcp://%s:%s", hostname, port);
    }


    /**
     * Method to get the client properties
     * @return - Client properties
     */
    abstract protected Map<String, Object> getClientProperties();
}
