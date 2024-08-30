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

package com.eclectique.messaging.core.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to hold the consumer properties
 */
public class ConsumerProperties implements Cloneable{

    @JsonProperty("properties")
    Map<String, Object> props;

    /**
     * Constructor to initialize the properties
     */
    public ConsumerProperties(){
        this.props = new HashMap<String, Object>();
    }

    /**
     * Gets the properties
     * @return Map of properties
     */
    public Map<String, Object> getProps() {
        return props;
    }

    /**
     * Sets the properties
     * @param props Map of properties
     */
    public void setProps(Map<String, Object> props) {
        this.props = props;
    }

    /**
     * Clones the properties
     * @return ConsumerProperties object
     */
    @Override
    public ConsumerProperties clone() {
       ConsumerProperties consumerProperties = new ConsumerProperties();
       consumerProperties.props = new HashMap<>(this.props);
       return consumerProperties;
    }
}
