/*
 * The MIT License
 *
 * Copyright (C) 2009 Robert Collins
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

import static org.junit.Assert.*;

import hudson.model.labels.LabelAtom;
import java.util.Collection;
import java.util.HashSet;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

/** @author robertc */
public class PlatformLabelerTest {

  @Rule public final JenkinsRule j = new JenkinsRule();

  @Test
  public void testLookupCached() {
    Collection<LabelAtom> expected = new HashSet<>();
    expected.add(j.jenkins.getLabelAtom("foo"));
    expected.add(j.jenkins.getLabelAtom("bar"));
    NodeLabelCache.nodeLabels.put(j.jenkins, expected);
    Collection labels = new PlatformLabeler().findLabels(j.jenkins);
    assertEquals(expected, labels);
  }

  @Test
  public void testLookupUncached() throws Exception {
    /* remove the Jenkins node from the cache */
    if (NodeLabelCache.nodeLabels.containsKey(j.jenkins)) {
      NodeLabelCache.nodeLabels.remove(j.jenkins);
    }
    Collection labels = new PlatformLabeler().findLabels(j.jenkins);
    assertEquals(0, labels.size());
  }
}
