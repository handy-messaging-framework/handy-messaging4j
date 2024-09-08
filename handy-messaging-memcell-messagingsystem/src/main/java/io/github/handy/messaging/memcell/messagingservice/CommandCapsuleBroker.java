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
import io.github.handy.messaging.memcell.types.CommandCapsule;
import io.github.handy.messaging.memcell.types.ResponseCapsule;

import java.util.Optional;
import java.util.concurrent.FutureTask;
/**
 * CommandCapsuleBroker is the broker between the memcell messaging client and the messaging service
 * @param <T> - Type of response expected
 */
public class CommandCapsuleBroker<T extends ResponseCapsule> {

    FutureTask<T> responseFuture;
    Optional<T> commandResponse = Optional.empty();

    /**
     * Function to send a command capsule to the messaging service
     * @param command - Command to send
     * @param messagingService - Name of the messaging service
     * @return FutureTask
     */
    public FutureTask<T> sendCommandCapsule(CommandCapsule command, String messagingService){
        this.responseFuture = new FutureTask<>(()->{
            return this.commandResponse.get();
        });
        command.setCompletionCallback(this::onResponseCallback);
        MemcellMessagingRegistry.getMessagingService(messagingService).tell(command, ActorRef.noSender());
        return this.responseFuture;
    }


    private void onResponseCallback(ResponseCapsule responseCapsule){
            this.commandResponse = Optional.of((T)responseCapsule);
            this.responseFuture.run();
    }
}
