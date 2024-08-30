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

package com.eclectique.messaging.core.consumer;

import com.eclectique.messaging.interfaces.ConsumerBuilder;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
/**
 * Factory class to get the appropriate consumer builder based on the consumer system
 */
public class ConsumerBuilderFactory {

    /**
     * Get the consumer builder based on the consumer system
     * @param consumerSystem The consumer system
     * @param properties The properties required for the consumer
     * @return The consumer builder
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static ConsumerBuilder getConsumerBuilder(String consumerSystem, Map<String, Object> properties)
            throws ClassNotFoundException,
            NoSuchMethodException,
            InvocationTargetException,
            InstantiationException,
            IllegalAccessException {
        Class<?> consumerBuilderClass = Class.forName(ConsumerBuilderMap.getInstance().getConsumerBuilder(consumerSystem));
        ConsumerBuilder builder = (ConsumerBuilder) consumerBuilderClass.getConstructor().newInstance();
        builder.setConsumerProperties(properties);
        return builder;
    }
}
