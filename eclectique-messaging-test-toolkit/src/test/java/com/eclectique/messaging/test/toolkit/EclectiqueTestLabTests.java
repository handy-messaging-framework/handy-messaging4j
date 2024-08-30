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

package com.eclectique.messaging.test.toolkit;

import com.eclectique.messaging.photon.clients.PhotonMessagingAdministrator;
import org.junit.*;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class EclectiqueTestLabTests {

    private static EclectiqueTestLab testLab;


    @BeforeClass
    public static void setup() {

        EclectiqueTestLab.EclectiqueTestLabBuilder labBuilder = new EclectiqueTestLab.EclectiqueTestLabBuilder()
                .addListener("photonTestProfile",
                        "testqueue",
                        "com.eclectique.messaging.types.simplemessage.SimpleMessage");


        testLab = labBuilder.getTestLab();
    }

    @AfterClass
    public static void tearDown() {
        PhotonMessagingAdministrator administrator = new PhotonMessagingAdministrator();
        administrator.tearDownMessagingService("validation_instance");
    }


    @Test
    public void getAnalysisProbeReturnsNonNullProbe() {
        MessageProbe probe = testLab.getAnalysisProbe(200, "photonTestProfile", "testqueue");
        assertNotNull(probe);
    }

    @Test(expected = NullPointerException.class)
    public void getAnalysisProbeReturnsNullWhenProfileDoesNotExist() {
        MessageProbe probe = testLab.getAnalysisProbe(1000, "nonexistentProfile", "testqueue");
        assertNull(probe);
    }

    @Test(expected = RuntimeException.class)
    public void getAnalysisProbeReturnsNullWhenQueueDoesNotExist() {
        MessageProbe probe = testLab.getAnalysisProbe(1000, "photonTestProfile", "nonexistentQueue");
        assertNull(probe);
    }
}