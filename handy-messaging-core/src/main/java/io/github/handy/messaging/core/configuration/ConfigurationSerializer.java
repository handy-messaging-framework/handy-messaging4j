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

package io.github.handy.messaging.core.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Class to serialize and deserialize the configuration
 */
public class ConfigurationSerializer {
    Properties configurationProperties;

    /**
     * Deserializes the configuration
     * @param configurationFileStream InputStream of the configuration file
     * @return ConfigurationRoot object
     * @throws IOException
     */
    public static ConfigurationRoot deserialize(InputStream configurationFileStream) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        ConfigurationRoot eclecticConfig = objectMapper.readValue(configurationFileStream, ConfigurationRoot.class);
        return eclecticConfig;
    }

    /**
     * Serializes the configuration
     * @param eclectiqueConfiguration ConfigurationRoot object
     * @param outstream OutputStream to write the configuration
     * @throws IOException
     */
    public static void serialize(ConfigurationRoot eclectiqueConfiguration, OutputStream outstream) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        objectMapper.writeValue(outstream, eclectiqueConfiguration);
    }

}
