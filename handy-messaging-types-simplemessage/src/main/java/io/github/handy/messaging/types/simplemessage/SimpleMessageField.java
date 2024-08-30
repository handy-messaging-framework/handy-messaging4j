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

package io.github.handy.messaging.types.simplemessage;

import io.github.handy.messaging.interfaces.MessageField;
import java.util.Comparator;

/**
 * Enum to define the fields in the SimpleMessage class
 */
public enum SimpleMessageField implements MessageField {

    VERSION("getVersion", new StringFieldComparator()),
    ID("getId", new StringFieldComparator()),
    HEADER_SCHEMA("getHeaderSchema", new StringFieldComparator()),
    DATETIMESTAMP("getDateTimestamp", new DateTimeFieldComparator()),
    SENDER("getSender", new StringFieldComparator()),
    PAYLOAD("getPayload", new ByteArrayFieldComparator()),
    CONTENT_SCHEMA("getContentSchema", new StringFieldComparator());

    String getterMethodName;
    Comparator fieldComparator;


    SimpleMessageField(String getterMethodName, Comparator fieldComparator){
        this.getterMethodName = getterMethodName;
        this.fieldComparator = fieldComparator;
    }

    @Override
    public String getFieldGetterMethod() {
        return this.getterMethodName;
    }

    @Override
    public Comparator getFieldComparator() {
        return this.fieldComparator;
    }
}
