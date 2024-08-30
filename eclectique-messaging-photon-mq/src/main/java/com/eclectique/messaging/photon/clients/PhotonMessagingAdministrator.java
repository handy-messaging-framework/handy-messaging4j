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

package com.eclectique.messaging.photon.clients;


import com.eclectique.messaging.photon.messagingservice.CommandCapsuleBroker;
import com.eclectique.messaging.photon.messagingservice.PhotonMessagingRegistry;
import com.eclectique.messaging.photon.types.commands.RegisterQueueCommand;
import com.eclectique.messaging.photon.types.responses.CommandResponse;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * PhotonMessagingAdministrator is a client class that provides administrative functions for PhotonMQ
 */
public class PhotonMessagingAdministrator {

    /**
     * Function to register a new messaging service
     * @param serviceId - ID of the messaging service
     */
    public void registerMessagingService(String serviceId){
        PhotonMessagingRegistry.registerNewMessagingService(serviceId);
    }

    /**
     * Function to register a new messaging queue
     * @param queueName - Name of the queue
     * @param serviceId - ID of the messaging service
     * @return CommandResponse
     */
    public CommandResponse registerMessagingQueue(String queueName, String serviceId){

        RegisterQueueCommand command = new RegisterQueueCommand(queueName);
        try{
        FutureTask<CommandResponse> responseFuture = new CommandCapsuleBroker<CommandResponse>()
                .sendCommandCapsule(command, serviceId);
            return responseFuture.get();
        } catch (InterruptedException | ExecutionException ex){
            throw new RuntimeException(ex.getMessage());
        }
    }

    /**
     * Function to tear down a messaging service
     * @param serviceId - ID of the messaging service
     */
    public void tearDownMessagingService(String serviceId){
        PhotonMessagingRegistry.tearDownMessagingService(serviceId);
    }

    /**
     * Function to shut down the messaging system
     */
    public void shutdownMessagingSystem(){
        try {
            PhotonMessagingRegistry.tearDownSystem().get();
        } catch (InterruptedException | ExecutionException ex){
            throw new RuntimeException(ex.getMessage());
        }
    }
}
