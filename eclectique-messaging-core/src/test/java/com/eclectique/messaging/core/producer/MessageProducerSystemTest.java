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

package com.eclectique.messaging.core.producer;

import com.eclectique.messaging.core.configuration.ConfigurationBootstrap;
import com.eclectique.messaging.interfaces.Message;
import com.eclectique.messaging.interfaces.Producer;
import com.eclectique.messaging.interfaces.ProducerBuilder;
import com.eclectique.messaging.types.simplemessage.SimpleMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;


import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageProducerSystemTest {


    @Mock
    ProducerBuilder mockedProducerBuilder;

    @Mock
    Producer mockedProducer;


    @AfterEach
    public void cleanup(){
       reset(mockedProducerBuilder, mockedProducer);
    }

    @Test
    public void verifyProducerSendsMessageTest() throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        new ConfigurationBootstrap();
        try(MockedStatic<ProducerBuilderFactory> mockedFactory = Mockito.mockStatic(ProducerBuilderFactory.class)){
            when(mockedProducerBuilder.build()).thenReturn(mockedProducer);
            doNothing().when(mockedProducer).sendMessage(any());
            when(ProducerBuilderFactory.getProducerBuilder(any(), any())).thenReturn(mockedProducerBuilder);
            MessageProducerSystem producerSystem = new MessageProducerSystem("profile3", "TEST-QUEUE");
            Message message = getMessage();
            producerSystem.sendMessage(message);
            verify(mockedProducer, times(1)).sendMessage(message);
        }

    }

    private Message getMessage(){
        SimpleMessage contentMsg = new SimpleMessage();
        contentMsg.setContentSchema(String.class.toString());
        contentMsg.setDateTime(Optional.of(Date.from(Instant.now())));
        contentMsg.setMessageId("msg-1");
        contentMsg.setPayload("Hello, this is a sample message".getBytes());
        contentMsg.setSender("app-1");
        contentMsg.setTransactionGroupId("transaction1");
        contentMsg.buildMessage();
        return contentMsg;
    }
}
