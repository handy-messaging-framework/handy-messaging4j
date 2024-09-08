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

import io.github.handy.messaging.interfaces.Message;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/**
 * Class representing a simple message
 */

public class SimpleMessage implements Message {

    private final String HEADER_SCHEMA = "io.github.handy.messaging.types.simplemessage.SimpleMessageProto";
    private final String VERSION = "1.1.0";

    private SimpleMessageType.SimpleProtoMessage.Builder protoBuilder;

    private SimpleMessageType.SimpleProtoMessage messageProtoBase;

    public SimpleMessage(){
        protoBuilder = SimpleMessageType.SimpleProtoMessage.newBuilder();
    }


    /**
     * Function to get the transaction group id
     * @return Optional - Transaction group id
     */
    @Override
    public Optional<String> getTransactionGroupId() {
        return (!this.messageProtoBase.getTransactionGroupId().isEmpty())?
                Optional.of(this.messageProtoBase.getTransactionGroupId()):Optional.empty();
    }

    /**
     * Function to get the version of the message
     * @return String - Version of the message
     */
    @Override
    public String getVersion() {
        return this.messageProtoBase.getVersion();
    }

    /**
     * Function to get the message id
     * @return String - Message id
     */
    @Override
    public String getId() {
        return this.messageProtoBase.getMessageId();
    }

    /**
     * Function to get the header schema
     * @return String - Header schema
     */
    @Override
    public String getHeaderSchema(){
        return this.messageProtoBase.getHeaderSchema();
    }

    /**
     * Function to get the date timestamp
     * @return Date - Date timestamp
     */
    public Date getDateTimestamp() {
        return Date.from(Instant.ofEpochMilli(this.messageProtoBase.getDatetimeEpochMs()));
    }

    /**
     * Function to get the sender
     * @return String - Sender
     */
    public String getSender() {
        return this.messageProtoBase.getSender();
    }

    /**
     * Function to get the payload
     * @return byte[] - Payload
     */
    public byte[] getPayload() {
        return this.messageProtoBase.getPayload().toByteArray();
    }

    /**
     * Function to get the content schema
     * @return String - Content schema
     */
    public String getContentSchema() {
        return this.messageProtoBase.getContentSchema();
    }

    /**
     * Function to set the Sender of the message
     * @param sender - Sender of the message
     * @return SimpleMessage - Instance of the simple message
     */
    public SimpleMessage setSender(String sender) {
        this.protoBuilder.setSender(sender);
        return this;
    }

    /**
     * Function to set the date time of the message
     * @param dateTime - Optional Date time of the message. If not present, current time is set
     * @return SimpleMessage - Instance of the simple message
     */
    public SimpleMessage setDateTime(Optional<Date> dateTime){
        if(dateTime.isPresent()) {
            this.protoBuilder.setDatetimeEpochMs(dateTime.get().toInstant().toEpochMilli());
        } else {
            this.protoBuilder.setDatetimeEpochMs(Instant.now().toEpochMilli());
        }
        return this;
    }

    /**
     * Function to set the content schema
     * @param contentSchema - Content schema
     * @return SimpleMessage - Instance of the simple message
     */
    public SimpleMessage setContentSchema(String contentSchema) {
        this.protoBuilder.setContentSchema(contentSchema);
        return this;
    }

    /**
     * Function to set the message id
     * @param messageId - Message id
     * @return SimpleMessage - Instance of the simple message
     */
    public SimpleMessage setMessageId(String messageId) {
        this.protoBuilder.setMessageId(messageId);
        return this;
    }

    /**
     * Function to set the payload
     * @param payload - Byte Serialized Payload
     * @return SimpleMessage - Instance of the simple message
     */
    public SimpleMessage setPayload(byte[] payload) {
        this.protoBuilder.setPayload(ByteString.copyFrom(payload));
        return this;
    }

    /**
     * Function to set the transaction group id in case message ordering is required
     * @param transactionGroupId - Transaction group id
     * @return SimpleMessage - Instance of the simple message
     */
    public SimpleMessage setTransactionGroupId(String transactionGroupId){
        this.protoBuilder.setTransactionGroupId(transactionGroupId);
        return this;
    }

    /**
     * Function to build the message. Function copies the data from the builder to the proto message
     */
    @Override
    public void buildMessage(){
        this.protoBuilder.setHeaderSchema(this.HEADER_SCHEMA);
        this.protoBuilder.setVersion(this.VERSION);
        this.messageProtoBase = this.protoBuilder.build();
    }

    /**
     * Function to deserialize the message. The byte serialized data is conerted to the proto message
     * @param serializedDataArr - Serialized data array
     */
    @Override
    public void deserialize(byte[] serializedDataArr) {
        try {
            this.messageProtoBase = SimpleMessageType.SimpleProtoMessage.parseFrom(serializedDataArr);
        } catch(InvalidProtocolBufferException protoBufException){
            throw new RuntimeException(protoBufException.getMessage());
        }
    }

    /**
     * Function to serialize the message. The proto message is serialized to byte array
     * @return byte[] - Serialized data array
     */
    @Override
    public byte[] serialize() {
        return this.messageProtoBase.toByteArray();
    }
}
