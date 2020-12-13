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

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlatformDetailsTest {

  private String name;
  private String arch;
  private String version;
  private String windowsFeatureUpdate;
  private PlatformDetails details;

  public PlatformDetailsTest() {}

  @BeforeEach
  void randomizeData() {
    name = randomName();
    arch = randomArch();
    version = randomVersion();
    windowsFeatureUpdate = randomWindowsFeatureUpdate();
    details = new PlatformDetails(name, arch, version, windowsFeatureUpdate);
  }

  @Test
  void testGetName() {
    assertThat(details.getName(), is(name));
  }

  @Test
  void testGetArchitecture() {
    assertThat(details.getArchitecture(), is(arch));
  }

  @Test
  void testGetVersion() {
    assertThat(details.getVersion(), is(version));
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
    PlatformDetails nullInDetails = new PlatformDetails(name, arch, version, null);
    assertThat(nullInDetails.getWindowsFeatureUpdate(), is(nullValue()));
  }

  private final Random random = new Random();
  private final String[] names = {
    "Windows 10", "alpine", "centos", "debian", "fedora", "raspbian", "freebsd", "macos", "ubuntu"
  };
  private final String[] versions = {
    "7.7.1908",
    "8.2.2004",
    "9.11",
    "10.0",
    "11.3",
    "12.2",
    "12.1",
    "12.2",
    "14.04",
    "16.04",
    "18.04",
    "20.04",
    "26",
    "27",
    "28",
    "29",
    "30",
  };
  private final String[] windowsFeatureUpdates = {
    "1703", "1709", "1803", "1809", "1903", "1909", "2003"
  };

  private String randomName() {
    return names[random.nextInt(names.length)];
  }

  private String randomArch() {
    return "amd64";
  }

  private String randomVersion() {
    return versions[random.nextInt(versions.length)];
  }

  private String randomWindowsFeatureUpdate() {
    return windowsFeatureUpdates[random.nextInt(windowsFeatureUpdates.length)];
  }
}
