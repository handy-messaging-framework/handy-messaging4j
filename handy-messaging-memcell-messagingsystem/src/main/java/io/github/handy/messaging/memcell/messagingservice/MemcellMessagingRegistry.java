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

package io.github.handy.messaging.memcell.messagingservice;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Terminated;
import akka.dispatch.OnComplete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.*;

/**
 * MemcellMessagingRegistry is a registry for Memcell messaging services
 */
public class MemcellMessagingRegistry {
    private final static ConcurrentMap<String, ActorRef> instanceRegistry = new ConcurrentHashMap<>();
    private static Optional<ActorSystem> messagingRegistryInstance = Optional.empty();

    private static Logger LOGGER = LoggerFactory.getLogger(MemcellMessagingRegistry.class);

    /**
     * Function to register a new messaging service
     * @param instanceName - Name of the messaging service
     */
    public static void registerNewMessagingService(String instanceName){
        ActorSystem messagingRegistry = messagingRegistryInstance.orElseGet(MemcellMessagingRegistry::initializeSystem);
        if(!instanceRegistry.containsKey(instanceName)){
            ActorRef messagingServiceActor = messagingRegistry.actorOf(MemcellMessagingService.getActorProperties(instanceName));
            instanceRegistry.put(instanceName, messagingServiceActor);
        } else {
            throw new RuntimeException(String.format("Messaging service %s already exists", instanceName));
        }
        LOGGER.info(String.format("Registered new messaging service %s", instanceName));
    }

    /**
     * Function to tear down a messaging service
     * @param instanceName - Name of the messaging service
     */
    public static void tearDownMessagingService(String instanceName){
        if(instanceRegistry.containsKey(instanceName)){
            instanceRegistry.get(instanceName).tell(PoisonPill.getInstance(), ActorRef.noSender());
            instanceRegistry.remove(instanceName);
        } else {
            throw new RuntimeException(String.format("Messaging service %s does not exist", instanceName));
        }
        LOGGER.info(String.format("Tore down messaging service %s", instanceName));
    }

    /**
     * Function to get a messaging service Actor
     * @param instanceName - Name of the messaging service
     * @return ActorRef
     */
    public static ActorRef getMessagingService(String instanceName){
        if(instanceRegistry.containsKey(instanceName)){
            return instanceRegistry.get(instanceName);
        } else {
            throw new RuntimeException(String.format("Messaging service %s does not exist", instanceName));
        }
    }

    /**
     * Function to tear down the system
     * @return Boolean Future - True if system is shut down. False otherwise
     */
    public static Future<Boolean> tearDownSystem(){
        instanceRegistry.clear();
        FutureTask<Boolean> completableFuture = new FutureTask<>(()->{
            return true;
        });
        ActorSystem messagingRegistrySystem = messagingRegistryInstance.orElseGet(MemcellMessagingRegistry::initializeSystem);
        messagingRegistrySystem.terminate().onComplete(new OnComplete<Terminated>() {
            @Override
            public void onComplete(Throwable failure, Terminated success) throws Throwable {
                completableFuture.run();
            }
        }, messagingRegistrySystem.dispatcher());
        messagingRegistryInstance = Optional.empty();
        return completableFuture;
    }

    private static ActorSystem initializeSystem(){
        messagingRegistryInstance = Optional.of(ActorSystem.create("MessagingRegistry"));
        return messagingRegistryInstance.get();
    }
}
