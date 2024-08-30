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

import org.apache.kafka.common.TopicPartition;

/**
 * SeekInfo is a class that holds the information required to seek to a specific offset in a Kafka topic - partition
 */
public class SeekInfo {
    private TopicPartition topicPartition;

    private long offset;

    public SeekInfo(String topic, int partition, long offset){
        this.topicPartition = new TopicPartition(topic, partition);
        this.offset = offset;
    }

    /**
     * Get the Partition of a Kafka topic
     * @return TopicPartition object
     */
    public TopicPartition getTopicPartition() {
        return topicPartition;
    }

    /**
     * Get the offset of a Kafka topic-partition
     * @return Offset
     */
    public long getOffset() {
        return offset;
    }
}
