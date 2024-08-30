/**
 * MIT License
 * <p>
 * Copyright (c) 2024 Aron Sajan Philip
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.handy.messaging.kafkaconnector;

public class Constants {

    public static final String QUEUE_NAME = "queue.name";
    public static final String KAFKA_AUTO_COMMIT_PROP = "enable.auto.commit";
    public static final String KAFKA_KEY_DESERIALIZER_PROP = "key.deserializer";
    public static final String KAFKA_VALUE_DESERIALIZER_PROP = "value.deserializer";
    public static final String KAFKA_KEY_SERIALIZER_PROP = "key.serializer";
    public static final String KAFKA_VALUE_SERIALIZER_PROP = "value.serializer";
    public static final String CONSUMER_ACTOR = "consumer.actor";
    public static final String MESSAGE_TYPE_CLASS = "message.type.class";
    public static final String KAFKA_BOOTSTRAP_SERVERS = "bootstrap.servers";

}
