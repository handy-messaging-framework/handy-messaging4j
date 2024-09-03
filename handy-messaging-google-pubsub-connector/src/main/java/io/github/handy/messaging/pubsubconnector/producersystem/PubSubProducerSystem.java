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

package io.github.handy.messaging.pubsubconnector.producersystem;

import io.github.handy.messaging.interfaces.Message;
import io.github.handy.messaging.interfaces.Producer;
import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;

/**
 * PubSubProducerSystem is a connector wrapper around a Google PubSub producer
 */
public class PubSubProducerSystem extends Producer {

    private Publisher publisher;
    private Logger LOGGER = LoggerFactory.getLogger(PubSubProducerSystem.class);

    /**
     * Constructor for PubSubProducerSystem
     * @param builder - PubSubProducerBuilder
     */
    public PubSubProducerSystem(PubSubProducerBuilder builder) {
        super(builder);
        this.publisher = builder.getPublisher();
    }

    /**
     * Send a message to the PubSub topic
     * @param message - Message
     */
    @Override
    public void sendMessage(Message message) {
        sendMessage(Optional.empty(), message);
    }

    /**
     * Send a message to the PubSub topic with a key
     * @param key - Key
     * @param message - Message
     */
    @Override
    public void sendMessage(String key, Message message) {
        sendMessage(Optional.of(key), message);
    }

    /**
     * Send a message to the PubSub topic
     * @param key (Optional) - Key
     * @param message - Message
     */
    private void sendMessage(Optional<String> key, Message message) {
        try{
            PubsubMessage.Builder pubSubMsgBuilder = PubsubMessage
                    .newBuilder()
                    .setData(ByteString.copyFrom(message.serialize()));
            key.ifPresent(pubSubMsgBuilder::setOrderingKey);
            PubsubMessage pubSubMsg = pubSubMsgBuilder.build();
            ApiFuture publishHandle = publisher.publish(pubSubMsg);
            publishHandle.get();
        } catch(Exception ex){
            LOGGER.error(String.format("Exception encountered while publishing message - %s", ex.getMessage()));
        }
    }

    /**
     * Close the producer
     */
    @Override
    public void close() {
        this.publisher.shutdown();
    }
}
