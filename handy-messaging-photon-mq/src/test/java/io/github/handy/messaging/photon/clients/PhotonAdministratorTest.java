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


package io.github.handy.messaging.photon.clients;

import akka.actor.RepointableActorRef;
import io.github.handy.messaging.photon.messagingservice.PhotonMessagingRegistry;
import io.github.handy.messaging.photon.types.responses.CommandExecutionStatus;
import io.github.handy.messaging.photon.types.responses.CommandResponse;
import org.junit.AfterClass;
import org.junit.Assert;

import org.junit.Test;

public class PhotonAdministratorTest {

    String messagingInstance = "testPhotonMessagingInstance";

    @Test
    public void registerMessagingInstanceTest(){
        PhotonMessagingAdministrator adm = new PhotonMessagingAdministrator();
        adm.registerMessagingService(messagingInstance);
        Assert.assertEquals(RepointableActorRef.class, PhotonMessagingRegistry.getMessagingService(messagingInstance).getClass());
        adm.tearDownMessagingService(messagingInstance);
    }

    @Test(expected = RuntimeException.class)
    public void tearDownActorTest(){
        PhotonMessagingAdministrator adm = new PhotonMessagingAdministrator();
        adm.registerMessagingService(messagingInstance);
        Assert.assertEquals(RepointableActorRef.class, PhotonMessagingRegistry.getMessagingService(messagingInstance).getClass());
        adm.tearDownMessagingService(messagingInstance);
        PhotonMessagingRegistry.getMessagingService(messagingInstance);
    }

    @Test
    public void queueCreation(){
        PhotonMessagingAdministrator adm = new PhotonMessagingAdministrator();
        adm.registerMessagingService(messagingInstance);
        CommandResponse response = adm.registerMessagingQueue("testQueue", messagingInstance);
        Assert.assertEquals(response.getCommandExecutionStatus(), CommandExecutionStatus.SUCCESS);
        adm.tearDownMessagingService(messagingInstance);
    }

    @Test
    public void queueExists(){
        PhotonMessagingAdministrator adm = new PhotonMessagingAdministrator();
        adm.registerMessagingService(messagingInstance);
        CommandResponse response = adm.registerMessagingQueue("testQueue", messagingInstance);
        Assert.assertEquals(response.getCommandExecutionStatus(), CommandExecutionStatus.SUCCESS);
        response = adm.registerMessagingQueue("testQueue", messagingInstance);
        Assert.assertEquals(response.getCommandExecutionStatus(), CommandExecutionStatus.FAILED);
        adm.tearDownMessagingService(messagingInstance);
    }

    @AfterClass
    public static void tearDownSystem(){
        PhotonMessagingRegistry.tearDownSystem();
    }


}
