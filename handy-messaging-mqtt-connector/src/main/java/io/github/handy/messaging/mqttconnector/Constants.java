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

package io.github.handy.messaging.mqttconnector;

/**
 * Constants class contains the constants used in the MQTT connector
 */
public class Constants {
    public static final String QUEUE_NAME = "queue.name";
    public static final String CONNECTION_HOST = "mqtt.broker.host";
    public static final String CONNECTION_PORT = "mqtt.broker.port";
    public static final String QOS = "mqtt.qos";
    public static final String AUTOMATIC_RECONNECT_FLAG = "mqtt.automatic_reconnect.flag";
    public static final String USERNAME = "mqtt.broker.username";
    public static final String PASSWORD = "mqtt.broker.password";
    public static final String CLEAN_SESSION_FLAG = "mqtt.clean_session.flag";
    public static final String CONNECTION_TIMEOUT = "connection.timeout.sec";
    public static final String CONSUMER_ACTOR = "consumer.actor";
    public static final String MESSAGE_TYPE_CLASS = "message.type.class";
}
