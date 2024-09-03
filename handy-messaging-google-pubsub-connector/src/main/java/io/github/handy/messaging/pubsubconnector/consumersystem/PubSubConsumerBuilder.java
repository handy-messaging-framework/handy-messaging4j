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

package io.github.handy.messaging.pubsubconnector.consumersystem;

import akka.actor.ActorRef;
import io.github.handy.messaging.interfaces.Consumer;
import io.github.handy.messaging.interfaces.ConsumerBuilder;
import io.github.handy.messaging.pubsubconnector.Constants;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.pubsub.v1.ProjectSubscriptionName;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.Map;

/**
 * Builder class for PubSub Consumer
 */
public class PubSubConsumerBuilder implements ConsumerBuilder {

    private Map<String, Object> props;
    private boolean emulatorFlag;
    private String subscriptionName;
    private CredentialsProvider credentialsProvider;
    private TransportChannelProvider transportChannelProvider;

    /**
     * Set the consumer properties
     * @param properties - Consumer properties Map
     * @return - ConsumerBuilder
     */
    @Override
    public ConsumerBuilder setConsumerProperties(Map<String, Object> properties) {
       this.props = properties;
       return this;
    }

    /**
     * Get the queue name
     * @return - Queue name
     */
    @Override
    public String getQueueName() {
        return this.props.get(Constants.QUEUE_NAME).toString();
    }

    /**
     * Get the message type class
     * @return - Message type class
     */
    @Override
    public String getMessageTypeClass() {
        return this.props.get(Constants.MESSAGE_TYPE_CLASS).toString();
    }

    /**
     * Get the consumer actor
     * @return - Consumer actor
     */
    @Override
    public ActorRef getConsumerActor() {
        return (ActorRef) this.props.get(Constants.CONSUMER_ACTOR);
    }

    /**
     * Check if the connection is to an emulator
     * @return - Boolean flag indicating if the connection is to an emulator
     */
    public boolean isEmulatorFlag() {
        return this.emulatorFlag;
    }

    /**
     * Get the subscription name
     * @return - Subscription name
     */
    public String getSubscriptionName() {
        return this.subscriptionName;
    }

    /**
     * Get the credentials provider for PubSub
     * @return - Credentials provider
     */
    public CredentialsProvider getCredentialsProvider() {
        return this.credentialsProvider;
    }

    /**
     * Get the transport channel provider for PubSub
     * @return - Transport channel provider
     */
    public TransportChannelProvider getTransportChannelProvider() {
        return this.transportChannelProvider;
    }

    /**
     * Build the PubSub Consumer
     * @return - PubSub Consumer
     */
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
