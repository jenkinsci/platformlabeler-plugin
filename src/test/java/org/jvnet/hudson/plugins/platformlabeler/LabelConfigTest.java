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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class LabelConfigTest {

  private final LabelConfig labelConfig;

  public LabelConfigTest() {
    labelConfig = new LabelConfig();
  }

  @Test
  public void testIsArchitecture() {
    assertTrue(labelConfig.isArchitecture());
  }

  @Test
  public void testSetArchitecture() {
    labelConfig.setArchitecture(false);
    assertFalse(labelConfig.isArchitecture());
  }

  @Test
  public void testIsName() {
    assertTrue(labelConfig.isName());
  }

  @Test
  public void testSetName() {
    labelConfig.setName(false);
    assertFalse(labelConfig.isName());
  }

  @Test
  public void testIsVersion() {
    assertTrue(labelConfig.isVersion());
  }

  @Test
  public void testSetVersion() {
    labelConfig.setVersion(false);
    assertFalse(labelConfig.isVersion());
  }

  @Test
  public void testIsArchitectureName() {
    assertTrue(labelConfig.isArchitectureName());
  }

  @Test
  public void testSetArchitectureName() {
    labelConfig.setArchitectureName(false);
    assertFalse(labelConfig.isArchitectureName());
  }

  @Test
  public void testIsNameVersion() {
    assertTrue(labelConfig.isNameVersion());
  }

  @Test
  public void testSetNameVersion() {
    labelConfig.setNameVersion(false);
    assertFalse(labelConfig.isNameVersion());
  }

  @Test
  public void testIsArchitectureNameVersion() {
    assertTrue(labelConfig.isArchitectureNameVersion());
  }

  @Test
  public void testSetArchitectureNameVersion() {
    labelConfig.setArchitectureNameVersion(false);
    assertFalse(labelConfig.isArchitectureNameVersion());
  }

  @Test
  public void testIsIncludeWindowsFeatureUpdate() {
    assertTrue(labelConfig.isIncludeWindowsFeatureUpdate());
  }

  @Test
  public void testSetIncludeWindowsFeatureUpdate() {
    labelConfig.setIncludeWindowsFeatureUpdate(true);
    assertTrue(labelConfig.isIncludeWindowsFeatureUpdate());
  }
}
