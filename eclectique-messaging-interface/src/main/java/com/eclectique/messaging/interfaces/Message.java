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

package com.eclectique.messaging.interfaces;

import java.io.*;
import java.util.Optional;

/**
 * Interface representing a Message
 */
public interface Message extends Serializable, MessageSerializer, MessageDeserializer {


    /**
     * Gets the transaction group id if applicable
     * @return Optional String representing the transaction group id
     */
    public Optional<String> getTransactionGroupId();

    /**
     * Gets the version of the message
     * @return String representing the version of the message
     */
    public abstract String getVersion();

    /**
     * Gets the schema of the message envelope
     * @return String representing the schema of the message envelope
     */
    public String getHeaderSchema();

    /**
     * Gets the message ID
     * @return String representing the message ID
     */
    public String getId();

    /**
     * Builds the message. Performs any processing that needs to be done to compile the message in the required format
     */
    public void buildMessage();

}