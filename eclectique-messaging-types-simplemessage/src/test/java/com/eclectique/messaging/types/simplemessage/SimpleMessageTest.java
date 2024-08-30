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

package com.eclectique.messaging.types.simplemessage;

import org.junit.Assert;
import org.junit.Test;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

public class SimpleMessageTest {

    private static String EXPECTED_SERIALIZED_BASE64 = "CgUxLjAuMBI/Y29tLmVjbGVjdGlxdWUubWVzc2FnaW5nLnR5cGVzLnNpbXBsZW1lc3NhZ2UuU2ltcGxlTWVzc2FnZVByb3RvGhZjbGFzcyBqYXZhLmxhbmcuU3RyaW5nIgVhcHAtMSoFbXNnLTEyDHRyYW5zYWN0aW9uMTjQo9/QmTJCH0hlbGxvLCB0aGlzIGlzIGEgc2FtcGxlIG1lc3NhZ2U=";
    @Test
    public void serializationTest(){
        SimpleMessage msg = getMessage();
        byte[] serializedMsg = msg.serialize();
        String base64SerializedMsg = Base64.getEncoder().encodeToString(serializedMsg);
        Assert.assertEquals(EXPECTED_SERIALIZED_BASE64, base64SerializedMsg);
    }

    @Test
    public void deserializationTest(){
        SimpleMessage msg = getMessage();
        byte[] serializedMsg = msg.serialize();
        SimpleMessage deserializedMsg = new SimpleMessage();
        deserializedMsg.deserialize(serializedMsg);
        Assert.assertEquals(msg.getSender(), deserializedMsg.getSender());
        Assert.assertEquals(msg.getId(), deserializedMsg.getId());
        Assert.assertEquals(msg.getContentSchema(), deserializedMsg.getContentSchema());
        Assert.assertEquals(msg.getHeaderSchema(), deserializedMsg.getHeaderSchema());
    }

    private SimpleMessage getMessage(){
        SimpleMessage contentMsg = new SimpleMessage();
        contentMsg.setContentSchema(String.class.toString());
        contentMsg.setDateTime(Optional.of(Date.from(Instant.ofEpochMilli(1724867138000L))));
        contentMsg.setMessageId("msg-1");
        contentMsg.setPayload("Hello, this is a sample message".getBytes());
        contentMsg.setSender("app-1");
        contentMsg.setTransactionGroupId("transaction1");
        contentMsg.buildMessage();
        return contentMsg;
    }
}
