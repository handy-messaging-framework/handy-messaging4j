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

package io.github.handy.messaging.core.producer;

import io.github.handy.messaging.interfaces.ProducerBuilder;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Factory class to get the producer builder instance based on the producer system
 */
public class ProducerBuilderFactory {

    /**
     * Get the producer builder instance based on the producer system
     * @param producerSystem The producer system
     * @param properties The producer properties
     * @return The producer builder instance
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static ProducerBuilder getProducerBuilder(String producerSystem, Map<String, Object> properties)
            throws ClassNotFoundException,
            NoSuchMethodException,
            InvocationTargetException,
            InstantiationException,
            IllegalAccessException {
        Class<?> producerBuilderClass = Class.forName(ProducerBuilderMap.getInstance()
                .getProducerBuilder(producerSystem));
        ProducerBuilder builder = (ProducerBuilder) producerBuilderClass.getConstructor().newInstance();
        builder.setProducerProperties(properties);
        return builder;
    }
}
