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


package io.github.handy.messaging.pubsubconnector.producersystem;

import io.github.handy.messaging.pubsubconnector.Constants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testcontainers.containers.PubSubEmulatorContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

public class PubSubProducerBuilderTest {

    private String topicName = "TestTopic";
    private String projectId = "SampleProject";

    private PubSubEmulatorContainer emulator;

    private Map<String, Object> getProducerProperties(){
        Map<String, Object> props = new HashMap<>(){{
            put(Constants.QUEUE_NAME, topicName);
            put(Constants.HOSTNAME,  emulator.getEmulatorEndpoint().split(":")[0]);
            put(Constants.PORT, Integer.parseInt(emulator.getEmulatorEndpoint().split(":")[1]));
            put(Constants.EMULATOR_EXECUTION_FLAG, true);
            put(Constants.PROJECTID, projectId);
        }};

        return props;
    }

    @Before
    public void setup(){
        this.emulator = new PubSubEmulatorContainer(DockerImageName.parse("gcr.io/google.com/cloudsdktool/google-cloud-cli:441.0.0-emulators"));
        this.emulator.start();
    }

    @Test
    public void builderBuildsPubSubProducerTest(){
        Assert.assertEquals(new PubSubProducerBuilder().setProducerProperties(getProducerProperties()).build().getClass().toString(), PubSubProducerSystem.class.toString());
    }
}
