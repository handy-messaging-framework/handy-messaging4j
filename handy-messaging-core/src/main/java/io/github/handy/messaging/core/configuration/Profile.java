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


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class to hold the profile configuration
 */
public class Profile implements Cloneable {

    public Profile(){}

    /**
     * Constructor to initialize the profile
     * @param builder ProfileBuilder object
     */
    private Profile(ProfileBuilder builder){
        this.profileName = builder.profileName;
        this.system = builder.system;
        this.producerProperties = builder.producerProperties;
        this.consumerProperties = builder.consumerProperties;
    }

    String profileName;
    String system;

    @JsonProperty("consumer")
    private ConsumerProperties consumerProperties;

    @JsonProperty("producer")
    private ProducerProperties producerProperties;

    /**
     * Gets the profile name
     * @return Profile name
     */
    public String getProfileName() {
        return profileName;
    }

    /**
     * Gets the system
     * @return System name
     */
    public String getSystem() {
        return system;
    }

    /**
     * Gets the consumer properties
     * @return ConsumerProperties object
     */
    public ConsumerProperties getConsumerProperties() {
        return consumerProperties;
    }

    /**
     * Gets the producer properties
     * @return ProducerProperties object
     */
    public ProducerProperties getProducerProperties() {
        return producerProperties;
    }

    /**
     * Clones the profile
     * @return Profile object
     */
    @Override
    public Profile clone(){
        Profile profile = new Profile();
        profile.profileName = new String(this.profileName);
        profile.system = new String(this.system);
        profile.consumerProperties = (this.consumerProperties!=null) ? this.consumerProperties.clone() : null;
        profile.producerProperties = (this.producerProperties!=null) ? this.producerProperties.clone() : null;
        return profile;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Profile){
            Profile anotherProfile = (Profile) obj;
            return anotherProfile.hashCode() == this.hashCode();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.profileName.hashCode();
    }

    /**
     * Builder class for Profile
     */
    public static class ProfileBuilder {
        private String profileName;
        private String system;
        private ProducerProperties producerProperties;
        private ConsumerProperties consumerProperties;

        /**
         * Sets the profile name
         * @param profileName Profile name
         * @return ProfileBuilder object
         */
        public ProfileBuilder setProfileName(String profileName) {
            this.profileName = profileName;
            return this;
        }

        /**
         * Sets the system
         * @param system System name
         * @return ProfileBuilder object
         */
        public ProfileBuilder setSystem(String system) {
            this.system = system;
            return this;
        }

        /**
         * Sets the producer properties
         * @param producerProperties ProducerProperties object
         * @return ProfileBuilder object
         */
        public ProfileBuilder setProducerProperties(ProducerProperties producerProperties) {
            this.producerProperties = producerProperties;
            return this;
        }

        /**
         * Sets the consumer properties
         * @param consumerProperties ConsumerProperties object
         * @return ProfileBuilder object
         */
        public ProfileBuilder setConsumerProperties(ConsumerProperties consumerProperties) {
            this.consumerProperties = consumerProperties;
            return this;
        }

        /**
         * Builds the profile
         * @return Profile object
         */
        public Profile buildProfile(){
            return new Profile(this);
        }

    }
}
