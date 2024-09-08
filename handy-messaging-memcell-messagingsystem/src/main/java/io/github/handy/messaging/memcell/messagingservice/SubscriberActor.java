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

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import io.github.handy.messaging.memcell.types.CompletionCallback;
import io.github.handy.messaging.memcell.types.commands.EnqueueMessageCommand;
import io.github.handy.messaging.memcell.types.commands.FetchEnqueuedMessageCommand;
import io.github.handy.messaging.memcell.types.responses.CommandExecutionStatus;
import io.github.handy.messaging.memcell.types.responses.FetchEnqueuedMessagesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * SubscriberActor is an actor that subscribes to a Memcell messaging queue. It enqueues messages
 * it fetches from the queue and wait for a consumer to collect it
 */
public class SubscriberActor extends AbstractActor {

    Logger LOGGER = LoggerFactory.getLogger(SubscriberActor.class);
    private Queue<byte[]> messageQueue;

    /**
     * Constructor to create a new SubscriberActor instance
     */
    public SubscriberActor(){
        this.messageQueue = new LinkedList<>();
    }
    @Override
    public Receive createReceive() {
        return new ReceiveBuilder().match(EnqueueMessageCommand.class, args->{
            this.onEnqueueMessage(args.getMessage());
        }).match(FetchEnqueuedMessageCommand.class, args->{
            this.onFetchEnqueuedMessages(args.getCompletionCallback());
        }).build();
    }

    /**
     * Function to enqueue a message
     * @param message - Message to enqueue
     */
    private void onEnqueueMessage(byte[] message){
        this.messageQueue.add(message);
        LOGGER.info("Message enqueued");
    }

    /**
     * Function to fetch enqueued messages
     * @param completionCallback - Callback to call after fetching messages
     */
    private void onFetchEnqueuedMessages(CompletionCallback completionCallback){
        List<byte[]> collectedMessages = new ArrayList<>();
        while(!this.messageQueue.isEmpty()){
            collectedMessages.add(this.messageQueue.remove());
        }
        FetchEnqueuedMessagesResponse response = new FetchEnqueuedMessagesResponse(CommandExecutionStatus.SUCCESS,
                collectedMessages);
        completionCallback.onCompletion(response);
    }

    public static Props getActorProperties(){
        return Props.create(SubscriberActor.class);
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        LOGGER.info("Subscriber Actor Started");
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        LOGGER.info("Subscriber Actor Stopped");
    }
}
