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

import com.eclectique.messaging.interfaces.Producer;
import com.eclectique.messaging.interfaces.ProducerBuilder;
import com.eclectique.messaging.pubsubconnector.Constants;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.TopicName;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Map;

public class PubSubProducerBuilder implements ProducerBuilder {

    private Map<String, Object> producerProps;
    private String topic;
    private String projectId;

    private boolean emulatorFlag = false;

    private Publisher publisher;
    @Override
    public ProducerBuilder setProducerProperties(Map<String, Object> props) {
        this.producerProps = props;
        return this;
    }

    @Override
    public String getQueueName() {
        return this.topic;
    }

    public String getProjectId() {
        return projectId;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    @Override
    public Producer build() {
        try {
            this.topic = producerProps.get(Constants.QUEUE_NAME).toString();
            this.projectId = this.producerProps.get(Constants.PROJECTID).toString();
            this.emulatorFlag = (boolean) this.producerProps.get(Constants.EMULATOR_EXECUTION_FLAG);
            TopicName topicObj =  TopicName.of(this.projectId, this.topic);
            if (emulatorFlag) {
                String hostname = this.producerProps.get(Constants.HOSTNAME).toString();
                int port = (int) this.producerProps.get(Constants.PORT);
                ManagedChannel channel = ManagedChannelBuilder.forAddress(hostname, port).usePlaintext().build();
                TransportChannelProvider channelProvider = FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel));
                CredentialsProvider credentialsProvider = NoCredentialsProvider.create();
                this.publisher = Publisher.newBuilder(topicObj)
                        .setChannelProvider(channelProvider)
                        .setCredentialsProvider(credentialsProvider)
                        .build();
            } else {
                this.publisher = Publisher.newBuilder(topicObj).build();
            }
            return new PubSubProducerSystem(this);
        } catch (Exception ex){
            throw new RuntimeException(String.format("PubSub Producer Build Error. Details - %s", ex.getMessage()));
        }
    }
}
