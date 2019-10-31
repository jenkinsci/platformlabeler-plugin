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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assumptions.*;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class WindowsReleaseTest {

  private final WindowsRelease windowsRelease = new WindowsRelease();
  private final WindowsRelease windowsReleaseFile;

  public WindowsReleaseTest() throws IOException {
    File dataFile = new File(getClass().getResource("windows/10.0/reg-query").getFile());
    windowsReleaseFile = new WindowsRelease(dataFile);
  }

  @Test
  public void testReleaseFile() {
    assertThat(windowsReleaseFile.release(), is("1903"));
  }

  @Test
  public void testReleaseNotWindows() {
    assumeFalse(isWindows());
    assertThat(windowsRelease.release(), is(PlatformDetailsTask.UNKNOWN_WINDOWS_VALUE_STRING));
  }

  @Test
  public void testRelease() {
    assumeTrue(isWindows());
    assertThat(windowsRelease.release(), matchesPattern("[12][0-9][0-9][0-9]"));
  }

  @Test
  public void testDistributorId() {
    assertEquals(windowsRelease.distributorId(), "Microsoft");
    assertEquals(windowsReleaseFile.distributorId(), "Microsoft");
  }

  private static boolean isWindows() {
    return File.pathSeparatorChar == ';';
  }
}
