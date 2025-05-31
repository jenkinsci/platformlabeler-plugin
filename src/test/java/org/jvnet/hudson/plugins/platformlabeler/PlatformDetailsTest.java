/*
 * The MIT License
 *
 * Copyright (C) 2019-2022 Mark Waite
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
import static org.hamcrest.Matchers.nullValue;

import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlatformDetailsTest {

    private String name;
    private String arch;
    private String version;
    private String windowsFeatureUpdate;
    private String osName;
    private PlatformDetails details;

    @BeforeEach
    void randomizeData() {
        name = randomName();
        arch = randomArch();
        version = randomVersion();
        windowsFeatureUpdate = randomWindowsFeatureUpdate();
        osName = name;
        details = new PlatformDetails(name, arch, version, windowsFeatureUpdate, osName);
    }

    @Test
    void testGetName() {
        assertThat(details.getName(), is(name));
    }

    @Test
    @Deprecated
    void testGetNameFewerDetails() {
        PlatformDetails fewerDetails = new PlatformDetails(name, arch, version);
        assertThat(fewerDetails.getName(), is(name));
    }

    @Test
    void testGetArchitecture() {
        assertThat(details.getArchitecture(), is(arch));
    }

    @Test
    @Deprecated
    void testGetArchitectureFewerDetails() {
        PlatformDetails fewerDetails = new PlatformDetails(name, arch, version, "ignored-windows-feature-update");
        assertThat(fewerDetails.getArchitecture(), is(arch));
    }

    @Test
    void testGetVersion() {
        assertThat(details.getVersion(), is(version));
    }

    @Test
    @Deprecated
    void testGetVersionFewerDetails() {
        PlatformDetails fewerDetails = new PlatformDetails(name, arch, version);
        assertThat(fewerDetails.getVersion(), is(version));
    }

    @Test
    void testGetArchitectureNameVersion() {
        assertThat(details.getArchitectureNameVersion(), is(arch + "-" + name + "-" + version));
    }

    @Test
    void testGetArchitectureName() {
        assertThat(details.getArchitectureName(), is(arch + "-" + name));
    }

    @Test
    void testGetNameVersion() {
        assertThat(details.getNameVersion(), is(name + "-" + version));
    }

    @Test
    void testGetWindowsFeatureUpdate() {
        assertThat(details.getWindowsFeatureUpdate(), is(windowsFeatureUpdate));
    }

    @Test
    void testGetWindowsFeatureUpdateNull() {
        PlatformDetails nullInDetails = new PlatformDetails(name, arch, version, null, osName);
        assertThat(nullInDetails.getWindowsFeatureUpdate(), is(nullValue()));
    }

    @Test
    void testGetWindowsFeatureUpdateEmptyString() {
        PlatformDetails nullInDetails = new PlatformDetails(name, arch, version, "", osName);
        assertThat(nullInDetails.getWindowsFeatureUpdate(), is(nullValue()));
    }

    @Test
    @Deprecated
    void testDetailsWithoutFeatureUpdate() {
        PlatformDetails detailsWithoutFeatureUpdate = new PlatformDetails(name, arch, version);
        assertThat(detailsWithoutFeatureUpdate.getWindowsFeatureUpdate(), is(nullValue()));
    }

    @Test
    void testGetOsName() {
        assertThat(details.getOsName(), is(osName));
    }

    @Test
    void testGetOsNameNull() {
        PlatformDetails nullInDetails = new PlatformDetails(name, arch, version, "non-empty", null);
        assertThat(nullInDetails.getOsName(), is(nullValue()));
    }

    @Test
    void testGetOsNameEmptyString() {
        PlatformDetails nullInDetails = new PlatformDetails(name, arch, version, null, "");
        assertThat(nullInDetails.getOsName(), is(nullValue()));
    }

    @Test
    @Deprecated
    void testDetailsWithoutOsName() {
        PlatformDetails detailsWithoutOsName = new PlatformDetails(name, arch, version, null);
        assertThat(detailsWithoutOsName.getOsName(), is(nullValue()));
    }

    private static final Random random = new Random();

    private static final String[] names = {
        "Windows 10", "alpine", "centos", "debian", "fedora", "freebsd", "macos", "raspbian", "ubuntu"
    };

    private static final String[] versions = {
        "3.19.7", "3.20.6", "3.21.3", "3.22.0", "8.10", "9.5", "10", "11", "12", "15.6", "22.04", "24.04", "41", "42",
    };

    private static final String[] windowsFeatureUpdates = {
        "1809", "1903", "1909", "2003", "2009", "2103", "2109", "2203"
    };

    private static String randomName() {
        return names[random.nextInt(names.length)];
    }

    private static String randomArch() {
        return "amd64";
    }

    private static String randomVersion() {
        return versions[random.nextInt(versions.length)];
    }

    private static String randomWindowsFeatureUpdate() {
        return windowsFeatureUpdates[random.nextInt(windowsFeatureUpdates.length)];
    }
}
