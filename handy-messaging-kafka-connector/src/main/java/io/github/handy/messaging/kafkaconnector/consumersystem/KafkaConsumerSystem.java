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

package io.github.handy.messaging.kafkaconnector.consumersystem;

import io.github.handy.messaging.interfaces.Consumer;
import io.github.handy.messaging.interfaces.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * KafkaConsumerSystem is the connector wrapper for the KafkaConsumer class. It is responsible for fetching messages from
 * a Kafka topic
 */
public class KafkaConsumerSystem extends Consumer {

    private KafkaConsumer kafkaConsumer;
    private ExecutorService pollThreadManager;
    private Future pollThreadHandle;
    private boolean interruptionFlag;
    private Optional<SeekInfo> seekPointer;
    private String messageTypeClass;
    private Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerSystem.class);


    /**
     * Constructor for KafkaConsumerSystem
     * @param consumerBuilder KafkaConsumerBuilder object
     */
    public KafkaConsumerSystem(KafkaConsumerBuilder consumerBuilder){
        super(consumerBuilder.getConsumerActor());
        this.kafkaConsumer = consumerBuilder.kafkaConsumer;
        this.pollThreadManager = Executors.newFixedThreadPool(1);
        this.seekPointer = Optional.empty();
        this.messageTypeClass = consumerBuilder.getMessageTypeClass();
    }


    /**
     * Method to start polling for messages from the Kafka topic
     */
    @Override
    public void startPolling() {
        this.pollThreadHandle = this.pollThreadManager.submit(()->{
            this.interruptionFlag = false;
            if(this.seekPointer.isPresent()){
                this.kafkaConsumer.seek(this.seekPointer.get().getTopicPartition(),
                        this.seekPointer.get().getOffset());
            }
            while(!this.interruptionFlag){
                ConsumerRecords<byte[], byte[]> records = kafkaConsumer.poll(Duration.ofMillis(500));
                for(ConsumerRecord<byte[], byte[]> record: records){
                    Message message = getMessage(record.value());
                    this.seekPointer = Optional.of(new SeekInfo(record.topic(), record.partition(), record.offset()+1));
                    super.onMessageReceived(message);
                }
            }
            LOGGER.info("KAFKA CONSUMER - Interrupted polling");
            if(this.seekPointer.isPresent()) {
                LOGGER.info("KAFKA CONSUMER -  Seek Pointer found");
                try {
                    kafkaConsumer.commitSync(Collections.singletonMap(this.seekPointer.get().getTopicPartition(),
                            new OffsetAndMetadata(this.seekPointer.get().getOffset())));
                }catch (Exception ex){
                    LOGGER.info("KAFKA CONSUMER -Commit EXCEPTION - "+ex.getMessage());
                }
                LOGGER.info("KAFKA CONSUMER -Committed SUCCESSFULLY");
            } else {
                LOGGER.info("KAFKA CONSUMER - ANOMALY Seek Pointer NOT found");
            }

        });
    }

    /**
     * Method to stop polling for messages from the Kafka topic
     */
    @Override
    public void stopPolling() {
        this.interruptionFlag = true;
        try {
            this.pollThreadHandle.get();
        } catch (Exception ex){
            LOGGER.error(String.format("Kafka consumer poll thread interruption exception. Details - %s", ex.getMessage()));
        }
    }

    /**
     * Method to get the message type class
     * @return
     */
    @Override
    protected String getMessageTypeClass() {
        return this.messageTypeClass;
    }
}
