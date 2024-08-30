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

import com.eclectique.messaging.interfaces.MessageField;
import org.junit.Assert;
import org.junit.Test;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class FieldComparatorTest {

    @Test
    public void compareSimilarFieldsTest(){
        SimpleMessage msgX = getMessageX();
        SimpleMessage anotherMessageX = getMessageX();
        List<MessageField> fields = new ArrayList<>(){{
            add(SimpleMessageField.ID);
            add(SimpleMessageField.SENDER);
            add(SimpleMessageField.CONTENT_SCHEMA);
            add(SimpleMessageField.PAYLOAD);
            add(SimpleMessageField.DATETIMESTAMP);
            add(SimpleMessageField.HEADER_SCHEMA);
            add(SimpleMessageField.VERSION);
        }};
        for(MessageField field : fields){
            Assert.assertTrue(field.compare(msgX, anotherMessageX));
        }

    }

    @Test
    public void compareDifferentFieldsTest(){
        SimpleMessage msgX = getMessageX();
        SimpleMessage msgY =getMessageY();
        List<MessageField> fields = new ArrayList<>(){{
            add(SimpleMessageField.ID);
            add(SimpleMessageField.SENDER);
            add(SimpleMessageField.CONTENT_SCHEMA);
            add(SimpleMessageField.PAYLOAD);
            add(SimpleMessageField.DATETIMESTAMP);
        }};
        for(MessageField field : fields){
            Assert.assertFalse(field.compare(msgX, msgY));
        }
    }

    private SimpleMessage getMessageX(){
        SimpleMessage contentMsg = new SimpleMessage();
        contentMsg.setContentSchema(String.class.toString());
        contentMsg.setDateTime(Optional.of(Date.from(Instant.ofEpochMilli(1724867138000L))));
        contentMsg.setMessageId("msgX");
        contentMsg.setPayload("Hello, this is messageX".getBytes());
        contentMsg.setSender("app-1");
        contentMsg.setContentSchema("content.schemaX");
        contentMsg.setTransactionGroupId("transaction1");
        contentMsg.buildMessage();
        return contentMsg;
    }

    private SimpleMessage getMessageY(){
        SimpleMessage contentMsg = new SimpleMessage();
        contentMsg.setContentSchema(String.class.toString());
        contentMsg.setDateTime(Optional.of(Date.from(Instant.ofEpochMilli(1724867432000L))));
        contentMsg.setMessageId("msgY");
        contentMsg.setPayload("Hello, this is messageY".getBytes());
        contentMsg.setSender("app2");
        contentMsg.setContentSchema("content.schemaY");
        contentMsg.setTransactionGroupId("transaction2");
        contentMsg.buildMessage();
        return contentMsg;
    }
}
