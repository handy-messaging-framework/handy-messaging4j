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

package com.eclectique.messaging.core.consumer;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.TestKit;
import com.eclectique.messaging.core.consumer.dispatcher.MessageChannelDispatcherActor;
import com.eclectique.messaging.interfaces.Message;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;


public class MessageChannelSubscriberActorTest {

    static ActorSystem system = ActorSystem.create();
    TestKit publisherActorProbe = new TestKit(system);
    TestKit rootActorProbe = new TestKit(system);
    TestKit dispatcherActorProbe = new TestKit(system);

    ActorRef subscriberActor;

    public MessageChannelSubscriberActorTest(){
        this.subscriberActor = system.actorOf(MessageChannelSubscriberActor.getActorProperties(getChannelId(),
                this.rootActorProbe.testActor(),
                this.publisherActorProbe.testActor(),
                this.dispatcherActorProbe.testActor()));
    }

    private String getChannelId(){
        return "CHANNEL-PROFILE1-TEST";
    }


    @Test
    public void initializationTest(){
        subscriberActor.tell(new MessageChannelSubscriberActor.Initialize(), rootActorProbe.testActor());
        publisherActorProbe.expectMsg(new ConsumerActor.SubscriptionRequest(subscriberActor));
        dispatcherActorProbe.expectMsg(new MessageChannelDispatcherActor.RegisterSubscriber(subscriberActor));
    }

    @Test
    public void subscriberRegistrationCompleteTest(){
        subscriberActor.tell(new ConsumerActor.SubscriptionAck(), publisherActorProbe.testActor());
        subscriberActor.tell(new MessageChannelDispatcherActor.SubscriberRegistered(), this.dispatcherActorProbe.testActor());
        this.rootActorProbe.expectMsgClass(MessageChannelSubscriberActor.Initialized.class);
    }

    @Test
    public void emptyDataReceivedTest(){
        ConsumerActor.DataAvailable dataAvailable = new ConsumerActor.DataAvailable(UUID.randomUUID(), new ArrayList<>());
        subscriberActor.tell(dataAvailable, this.publisherActorProbe.testActor());
        this.publisherActorProbe.expectMsgClass(ConsumerActor.PollData.class);
    }

    @Test
    public void nonEmptyDataReceivedTest(){

        ConsumerActor.DataAvailable dataAvailable = new ConsumerActor.DataAvailable(UUID.randomUUID(), new ArrayList<>(){{
            add(getDummyMessage());
        }});
        subscriberActor.tell(dataAvailable, this.publisherActorProbe.testActor());
        this.dispatcherActorProbe.expectMsgClass(MessageChannelDispatcherActor.DispatchMessages.class);
    }

    private Message getDummyMessage(){
        return new Message() {
            @Override
            public Optional<String> getTransactionGroupId() {
                return Optional.empty();
            }

            @Override
            public String getVersion() {
                return "";
            }

            @Override
            public String getHeaderSchema() {
                return "";
            }

            @Override
            public String getId() {
                return "";
            }

            @Override
            public void buildMessage() {

            }

            @Override
            public void deserialize(byte[] serializedDataArr) {

            }

            @Override
            public byte[] serialize() {
                return new byte[0];
            }
        };
    }

}
