/*
 * The MIT License
 *
 * Copyright (C) 2019 Mark Waite
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jvnet.hudson.plugins.platformlabeler;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;

import hudson.Functions;
import java.io.File;
import java.net.URL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WindowsReleaseTest {

    private final WindowsRelease windowsRelease = new WindowsRelease();
    private WindowsRelease windowsReleaseFile;

    @BeforeEach
    void setUp() throws Exception {
        URL resource = getClass().getResource("windows/10.0.1903/reg-query");
        File dataFile = new File(resource.toURI());
        windowsReleaseFile = new WindowsRelease(dataFile);
    }

    @Test
    void testReleaseFromFile() {
        assertThat(windowsReleaseFile.release(), is("1903"));
    }

    @Test
    void testRelease() {
        if (Functions.isWindows()) {
            assertThat(windowsRelease.release(), matchesPattern("[12][0-9][0-9][0-9]"));
        } else {
            String expected = PlatformDetailsTask.UNKNOWN_WINDOWS_VALUE_STRING;
            assertThat(windowsRelease.release(), is(expected));
        }
    }

    @Test
    void testDistributorId() {
        assertThat(windowsRelease.distributorId(), is("Microsoft"));
    }

    @Test
    void testDistributorIdFromFile() {
        assertThat(windowsReleaseFile.distributorId(), is("Microsoft"));
    }
}
