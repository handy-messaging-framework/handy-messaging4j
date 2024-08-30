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

package com.eclectique.messaging.core.configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to hold the list of profiles
 */
public class EclectiqueConfiguration implements Cloneable{

    List<Profile> profiles;

    /**
     * Constructor to initialize the profiles
     */
    public EclectiqueConfiguration(){
        this.profiles = new ArrayList<>();
    }

    /**
     * Gets the profiles
     * @return List of profiles
     */
    public List<Profile> getProfiles() {
        return profiles;
    }

    /**
     * Sets the profiles
     * @param profiles List of profiles
     */
    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }

    /**
     * Clones the configuration
     * @return EclectiqueConfiguration object
     */
    @Override
    public EclectiqueConfiguration clone() {
        EclectiqueConfiguration configuration = new EclectiqueConfiguration();
        configuration.profiles = new ArrayList<>();
        this.profiles.forEach(profile -> {
            configuration.profiles.add(profile.clone());
        });
        return configuration;
    }
}
