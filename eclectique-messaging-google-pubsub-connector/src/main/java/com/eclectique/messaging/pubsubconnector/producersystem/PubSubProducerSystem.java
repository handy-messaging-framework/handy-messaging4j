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

package com.eclectique.messaging.pubsubconnector.producersystem;

import com.eclectique.messaging.interfaces.Message;
import com.eclectique.messaging.interfaces.Producer;
import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PubSubProducerSystem extends Producer {

    Publisher publisher;
    private Logger LOGGER = LoggerFactory.getLogger(PubSubProducerSystem.class);
    public PubSubProducerSystem(PubSubProducerBuilder builder) {
        super(builder);
        this.publisher = builder.getPublisher();
    }

    @Override
    public void sendMessage(Message message) {
        try{
            PubsubMessage pubSubMsg = PubsubMessage
                    .newBuilder()
                    .setData(ByteString.copyFrom(message.serialize()))
                    .build();
            ApiFuture publishHandle = publisher.publish(pubSubMsg);
            publishHandle.get();
        } catch(Exception ex){
            LOGGER.error(String.format("Exception encountered while publishing message - %s", ex.getMessage()));
        }
    }

    @Override
    public void close() {
        this.publisher.shutdown();
    }
}
