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

package io.github.handy.messaging.core.producer;

import io.github.handy.messaging.core.configuration.BootConfiguration;
import io.github.handy.messaging.core.configuration.Profile;
import io.github.handy.messaging.core.Constants;
import io.github.handy.messaging.interfaces.Message;
import io.github.handy.messaging.interfaces.Producer;

import java.util.Map;

/**
 * The MessageProducerSystem class is a simple interface to send messages to a messaging system. The class is responsible for
 * creating a producer instance based on the profile and system provided.
 */
public class MessageProducerSystem {

    private Producer producer;

    /**
     * Constructor to create a producer instance based on the profile and system provided.
     * @param profile The profile name
     * @param queueName The queue name
     */
    public MessageProducerSystem(String profile, String queueName){
       Profile producerProfile =  BootConfiguration.getConfiguration().getHandyMessagingConfiguration()
                .getProfiles()
                .stream()
                .filter(profileSystem -> profileSystem.getProfileName().equals(profile))
                .findFirst().get();

       Map<String, Object> producerProperties = producerProfile.getProducerProperties().getProps();
       producerProperties.put(Constants.QUEUE_NAME, queueName);
       try {
         this.producer = ProducerBuilderFactory
                 .getProducerBuilder(producerProfile.getSystem(), producerProperties)
                 .build();
       } catch (Exception ex){
          throw new RuntimeException(ex.getMessage());
       }
    }

    /**
     * Method to send a message to the messaging system
     * @param message The message object
     */
    public void sendMessage(Message message){
        message.buildMessage();
        this.producer.sendMessage(message);
    }

    /**
     * Method to close the producer instance
     */
    public void close(){
        this.producer.close();
    }

}
