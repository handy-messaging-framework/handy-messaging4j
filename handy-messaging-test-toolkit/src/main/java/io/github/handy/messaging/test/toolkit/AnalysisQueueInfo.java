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


/**
 * Class that packages the details of the queue that needs to be analyzed
 */
public class AnalysisQueueInfo {
    private String profileName;

    private String queueName;

    private String messageType;

    /**
     * Constructor for the AnalysisQueueInfo class
     * @param profileName The profile name of the queue
     * @param queue The name of the queue
     * @param messageType The class name of the message type that gets sent to the queue
     */
    public AnalysisQueueInfo(String profileName, String queue, String messageType){
        this.profileName = profileName;
        this.queueName = queue;
        this.messageType = messageType;
    }

    /**
     * Gets the profile name of the queue
     * @return Profile name
     */
    public String getProfileName() {
        return profileName;
    }

    /**
     * Gets the name of the queue to be analyzed
     * @return Queue Name
     */
    public String getQueueName() {
        return queueName;
    }

    /**
     * Gets the class name of the message type that gets sent to the queue
     * @return Class name of the message type
     */
    public String getMessageType() {
        return messageType;
    }

}

