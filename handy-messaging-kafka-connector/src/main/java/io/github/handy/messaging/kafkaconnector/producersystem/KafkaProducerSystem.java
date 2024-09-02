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

package io.github.handy.messaging.kafkaconnector.producersystem;

import io.github.handy.messaging.interfaces.Message;
import io.github.handy.messaging.interfaces.Producer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class KafkaProducerSystem extends Producer {

    KafkaProducer kafkaProducer;


    public KafkaProducerSystem(KafkaProducerBuilder builder){
        super(builder);
        this.kafkaProducer = builder.getKafkaProducer();
    }

    @Override
    public void sendMessage(Message message) {
        this.sendMessage(Optional.empty(), message);
    }

    /**
     * Sends a message to the Kafka Queue
     * @param key Key for the message
     * @param message Message to be sent
     */
    @Override
    public void sendMessage(String key, Message message) {
        this.sendMessage(Optional.of(key), message);
    }

    private void sendMessage(Optional<String> key, Message message){
        try {
            ProducerRecord record;
            if(key.isPresent()){
                record = new ProducerRecord(this.getQueueName(), key.get(), message.serialize());
            } else {
                record = new ProducerRecord(this.getQueueName(), message.serialize());
            }
            Future<RecordMetadata> sendHandle = this.kafkaProducer.send(record);
            sendHandle.get();
        } catch (InterruptedException interruptEx){
            System.out.println("Kafka Producer Exception - "+interruptEx.getMessage());
        } catch (ExecutionException execEx){
            System.out.println("Kafka Producer Exception - "+execEx.getMessage());
        }
    }

    @Override
    public void close() {
        this.kafkaProducer.close();
    }

}
