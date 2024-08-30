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

package io.github.handy.messaging.test.toolkit;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import io.github.handy.messaging.interfaces.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Actor class that buffers data for a queue
 */
public class MessageRetentionBufferActor extends AbstractActor {

   /**
    * Message class `EnqueueMessage` instructs the MessageRetentionBuffer
    * actor to enqueue a message to its queue
    */
   public static class EnqueueMessage {
      Message message;
      public EnqueueMessage(Message message){
         this.message = message;
      }
   }

   /**
    * Message class `ReleaseMessages` instructs the MessageRetentionBuffer
    * actor to release the messages in its queue to the caller
    */
   public static class ReleaseMessages { }

   Logger LOGGER = LoggerFactory.getLogger(MessageRetentionBufferActor.class);
   List<Message> messageCollection;

   public MessageRetentionBufferActor(){
      this.messageCollection = new ArrayList<>();
   }

   @Override
   public Receive createReceive() {
      return new ReceiveBuilder().match(EnqueueMessage.class, args -> {
         this.onEnqueueMessage(args.message);
      }).match(ReleaseMessages.class, args -> {
         sender().tell(new ArrayList<>(this.messageCollection), self());
      }).build();
   }


   private void onEnqueueMessage(Message message){
      this.messageCollection.add(message);
      LOGGER.info("Enqueued Message");
   }

    /**
     * Function returns the properties needed to create an instance of the actor
     * @return Props instance
     */
   public static Props getActorProperties(){
      return Props.create(MessageRetentionBufferActor.class);
   }

   @Override
   public void preStart() throws Exception {
      super.preStart();
      LOGGER.info("Starting Message Buffer Actor");
   }

   @Override
   public void postStop() throws Exception {
      super.postStop();
      LOGGER.info("Stopping Message Buffer Actor");
   }
}
