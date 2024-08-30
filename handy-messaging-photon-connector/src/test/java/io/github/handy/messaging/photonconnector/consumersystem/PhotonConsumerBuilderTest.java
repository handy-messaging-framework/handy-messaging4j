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

package io.github.handy.messaging.photonconnector.consumersystem;

import akka.actor.ActorSystem;
import akka.testkit.TestKit;
import io.github.handy.messaging.photon.clients.PhotonMessagingAdministrator;
import io.github.handy.messaging.photonconnector.Constants;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;

public class PhotonConsumerBuilderTest {

    ActorSystem testSystem = ActorSystem.create();
    TestKit consumerProbe = new TestKit(testSystem);

   @BeforeClass
   public static void setup(){
       PhotonMessagingAdministrator adm = new PhotonMessagingAdministrator();
       adm.registerMessagingService("test_service");
       adm.registerMessagingQueue("test_queue", "test_service");
   }

   @AfterClass
   public static void teardown(){
       PhotonMessagingAdministrator adm = new PhotonMessagingAdministrator();
       adm.tearDownMessagingService("test_service");
   }

   private Map<String, Object> getConsumerProperties(){
       return new HashMap<>(){{
           put(Constants.CONSUMER_ACTOR, consumerProbe.testActor());
           put(Constants.QUEUE_NAME, "test_queue");
           put(Constants.MESSAGE_SERVICE, "test_service");
           put(Constants.MESSAGE_TYPE_CLASS, "io.github.handy.messaging.types.simplemessage.SimpleMessage");
           put(Constants.APPLICATION_ID, "test_app");
       }};
   }

   @Test
   public void buildPhotonConsumerInstanceTest(){
       PhotonConsumerBuilder consumerBuilder = new PhotonConsumerBuilder();
       consumerBuilder.setConsumerProperties(getConsumerProperties());
       Assert.assertEquals(consumerBuilder.build().getClass(), PhotonConsumerSystem.class);
   }

   @Test(expected = NullPointerException.class)
    public void buildPhotonConsumerInstanceWithoutRequiredPropsTest(){
        PhotonConsumerBuilder consumerBuilder = new PhotonConsumerBuilder();
        Map<String, Object> props = getConsumerProperties();
        props.remove(Constants.QUEUE_NAME);
        consumerBuilder.setConsumerProperties(props);
        consumerBuilder.build();
    }
}
