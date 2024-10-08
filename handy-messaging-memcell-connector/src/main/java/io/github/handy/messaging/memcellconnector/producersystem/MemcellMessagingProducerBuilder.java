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

package io.github.handy.messaging.memcellconnector.producersystem;

import io.github.handy.messaging.interfaces.Producer;
import io.github.handy.messaging.interfaces.ProducerBuilder;
import io.github.handy.messaging.memcellconnector.Constants;


import java.util.Map;

/**
 * Builder class for MemcellMessagingProducer System
 */
public class MemcellMessagingProducerBuilder implements ProducerBuilder {

    Map<String, Object> producerProps;

    /**
     * Set the producer properties
     * @param props - Properties for the producer
     * @return - ProducerBuilder
     */
    @Override
    public ProducerBuilder setProducerProperties(Map<String, Object> props) {
        this.producerProps = props;
        return this;
    }

    /**
     * Get the queue name
     * @return - String representing the queue name
     */
    @Override
    public String getQueueName() {
        return this.producerProps.get(Constants.QUEUE_NAME).toString();
    }

    /**
     * Get the message service instance
     * @return - String representing the message service instance
     */
    public String getMemcellMessagingInstance(){
        return this.producerProps.get(Constants.MESSAGE_SERVICE).toString();
    }

    /**
     * Build the producer
     * @return - Producer instance
     */
    @Override
    public Producer build() {
        return new MemcellMessagingProducerSystem(this);
    }
}
