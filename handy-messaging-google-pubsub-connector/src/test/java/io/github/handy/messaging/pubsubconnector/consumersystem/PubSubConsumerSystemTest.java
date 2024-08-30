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
import akka.actor.ActorSystem;
import akka.testkit.TestKit;
import io.github.handy.messaging.interfaces.EnqueueMessage;
import io.github.handy.messaging.interfaces.Message;
import io.github.handy.messaging.pubsubconnector.Constants;
import io.github.handy.messaging.types.simplemessage.SimpleMessage;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.*;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.PushConfig;
import com.google.pubsub.v1.SubscriptionName;
import com.google.pubsub.v1.TopicName;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.Before;
import org.junit.Test;
import org.testcontainers.containers.PubSubEmulatorContainer;
import org.testcontainers.utility.DockerImageName;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PubSubConsumerSystemTest {

    private PubSubEmulatorContainer emulator;
    ActorSystem testSystem = ActorSystem.create();
    TestKit consumerActorProbe = new TestKit(testSystem);

    private String topicName = "TestTopic";
    private String projectId = "SampleProject";
    private String subscriptionId = "TestSubscription";
    private ActorRef consumerActor = consumerActorProbe.testActor();

    private Publisher publisher;

    private Map<String, Object> getConsumerProperties(){
        Map<String, Object> props = new HashMap<>(){{
            put(Constants.QUEUE_NAME, topicName);
            put(Constants.CONSUMER_ACTOR, consumerActor);
            put(Constants.HOSTNAME,  emulator.getEmulatorEndpoint().split(":")[0]);
            put(Constants.PORT, Integer.parseInt(emulator.getEmulatorEndpoint().split(":")[1]));
            put(Constants.EMULATOR_EXECUTION_FLAG, true);
            put(Constants.SUBSCRIPTIONID, subscriptionId);
            put(Constants.PROJECTID, projectId);
            put(Constants.MESSAGE_TYPE_CLASS, "io.github.handy.messaging.types.simplemessage.SimpleMessage");

        }};

        return props;
    }

    private void createSubscription(
            String subscriptionId,
            String topicId,
            TransportChannelProvider channelProvider,
            NoCredentialsProvider credentialsProvider
    ) throws IOException {
        SubscriptionAdminSettings subscriptionAdminSettings = SubscriptionAdminSettings
                .newBuilder()
                .setTransportChannelProvider(channelProvider)
                .setCredentialsProvider(credentialsProvider)
                .build();
        SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create(subscriptionAdminSettings);
        SubscriptionName subscriptionName = SubscriptionName.of(projectId, subscriptionId);
        subscriptionAdminClient.createSubscription(
                subscriptionName,
                TopicName.of(projectId, topicId),
                PushConfig.getDefaultInstance(),
                10
        );
    }


    private void createTopic(
            String topicId,
            TransportChannelProvider channelProvider,
            NoCredentialsProvider credentialsProvider
    ) throws IOException {
        TopicAdminSettings topicAdminSettings = TopicAdminSettings
                .newBuilder()
                .setTransportChannelProvider(channelProvider)
                .setCredentialsProvider(credentialsProvider)
                .build();
        try (TopicAdminClient topicAdminClient = TopicAdminClient.create(topicAdminSettings)) {
            TopicName topicName = TopicName.of(projectId, topicId);
            topicAdminClient.createTopic(topicName);
        }
    }

    private Message getMessage(){
        SimpleMessage contentMsg = new SimpleMessage();
        contentMsg.setContentSchema(String.class.toString());
        contentMsg.setDateTime(Optional.of(Date.from(Instant.now())));
        contentMsg.setMessageId("msg-1");
        contentMsg.setPayload("Hello, this is a sample message".getBytes());
        contentMsg.setSender("app-1");
        contentMsg.setTransactionGroupId("transaction1");
        contentMsg.buildMessage();
        return contentMsg;
    }


    @Before
    public void setup() throws IOException {
        this.emulator = new PubSubEmulatorContainer(DockerImageName.parse("gcr.io/google.com/cloudsdktool/google-cloud-cli:441.0.0-emulators"));
        this.emulator.start();
        String hostport = emulator.getEmulatorEndpoint();
        ManagedChannel channel = ManagedChannelBuilder.forTarget(hostport).usePlaintext().build();
        TransportChannelProvider channelProvider = FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel));
        NoCredentialsProvider credentialsProvider = NoCredentialsProvider.create();
        createTopic(topicName, channelProvider, credentialsProvider);
        createSubscription(subscriptionId,topicName, channelProvider, credentialsProvider);
        this.publisher =  Publisher
                .newBuilder(TopicName.of(projectId, topicName))
                .setChannelProvider(channelProvider)
                .setCredentialsProvider(credentialsProvider)
                .build();
    }

    private PubSubConsumerSystem getConsumerSystem(){
        PubSubConsumerBuilder builder = new PubSubConsumerBuilder();
        builder.setConsumerProperties(getConsumerProperties());
        return(PubSubConsumerSystem) builder.build();

    }

    @Test
    public void verifyMessagesReceivedEnqueuedTest(){
        PubSubConsumerSystem consumerSystem = getConsumerSystem();
        consumerSystem.startPolling();
        PubsubMessage pubSubMsg = PubsubMessage
                .newBuilder()
                .setData(ByteString.copyFrom(getMessage().serialize()))
                .build();
        this.publisher.publish(pubSubMsg);
        consumerActorProbe.expectMsgClass(EnqueueMessage.class);
    }
}
