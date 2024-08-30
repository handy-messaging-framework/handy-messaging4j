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

package com.eclectique.messaging.pubsubconnector.consumersystem;

import akka.actor.ActorRef;
import com.eclectique.messaging.interfaces.Consumer;
import com.eclectique.messaging.interfaces.ConsumerBuilder;
import com.eclectique.messaging.pubsubconnector.Constants;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.pubsub.v1.ProjectSubscriptionName;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Map;

public class PubSubConsumerBuilder implements ConsumerBuilder {

    private Map<String, Object> props;

    private boolean emulatorFlag;

    private String subscriptionName;


    private CredentialsProvider credentialsProvider;

    private TransportChannelProvider transportChannelProvider;


    @Override
    public ConsumerBuilder setConsumerProperties(Map<String, Object> properties) {
       this.props = properties;
       return this;
    }

    @Override
    public String getQueueName() {
        return this.props.get(Constants.QUEUE_NAME).toString();
    }

    @Override
    public String getMessageTypeClass() {
        return this.props.get(Constants.MESSAGE_TYPE_CLASS).toString();
    }

    @Override
    public ActorRef getConsumerActor() {
        return (ActorRef) this.props.get(Constants.CONSUMER_ACTOR);
    }

    public boolean isEmulatorFlag() {
        return this.emulatorFlag;
    }

    public String getSubscriptionName() {
        return this.subscriptionName;
    }

    public CredentialsProvider getCredentialsProvider() {
        return this.credentialsProvider;
    }

    public TransportChannelProvider getTransportChannelProvider() {
        return this.transportChannelProvider;
    }

    @Override
    public Consumer build() {
        String projectId = this.props.get(Constants.PROJECTID).toString();
        String subscriptionId = this.props.get(Constants.SUBSCRIPTIONID).toString();
        this.emulatorFlag = (boolean) this.props.get(Constants.EMULATOR_EXECUTION_FLAG);
        this.subscriptionName = ProjectSubscriptionName.format(projectId, subscriptionId);
        if(emulatorFlag){
            String hostname = this.props.get(Constants.HOSTNAME).toString();
            int port = (int) this.props.get(Constants.PORT);
            ManagedChannel managedChannel = ManagedChannelBuilder.forAddress(hostname, port).usePlaintext().build();
            this.transportChannelProvider = FixedTransportChannelProvider.create(GrpcTransportChannel.create(managedChannel));
            this.credentialsProvider = NoCredentialsProvider.create();
        }

        return new PubSubConsumerSystem(this);
    }
}
