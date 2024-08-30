
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

import akka.actor.Actor;
import akka.actor.ActorRef;
import com.eclectique.messaging.mqttconnector.consumersystem.MqttConsumerBuilder;
import org.junit.Assert;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;

public class MqttConsumerBuilderTest {

    private class Helper {

        private static ActorRef getConsumerActor(){
            return Actor.noSender();
        }
        private static Map<String, Object> generateConsumerProperties(){
            return new HashMap<String, Object>(){{
                put(Constants.QUEUE_NAME, "abc/def");
                put(Constants.CONSUMER_ACTOR, getConsumerActor());
                put(Constants.CONNECTION_HOST, "sample.host");
                put(Constants.CONNECTION_PORT, 1883);
                put(Constants.MESSAGE_TYPE_CLASS, "com.eclectique.plaintext");
            }};
        }
    }

    @Test
    public void testVerifyQueueNameFromConsumerProperties(){
        MqttConsumerBuilder consumerBuilder = new MqttConsumerBuilder();
        consumerBuilder.setConsumerProperties(Helper.generateConsumerProperties());
        Assert.assertEquals(consumerBuilder.getQueueName(), "abc/def");
    }

    @Test
    public void testVerifyConnectionURL(){
        MqttConsumerBuilder consumerBuilder = new MqttConsumerBuilder();
        consumerBuilder.setConsumerProperties(Helper.generateConsumerProperties());
        Assert.assertEquals(consumerBuilder.getConnectionString(), "tcp://sample.host:1883");
    }

    @Test
    public void testVerifyMessageType(){
        MqttConsumerBuilder consumerBuilder = new MqttConsumerBuilder();
        consumerBuilder.setConsumerProperties(Helper.generateConsumerProperties());
        Assert.assertEquals(consumerBuilder.getMessageTypeClass(), "com.eclectique.plaintext");
    }
}
