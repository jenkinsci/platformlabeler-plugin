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

import hudson.model.Label;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.jvnet.hudson.test.HudsonTestCase;

/**
 *
 * @author robertc
 */
public class PlatformLabelerTest extends HudsonTestCase {

    public void testLookupCached() {
        Set<Label> expected = new HashSet<Label>();
        expected.add(hudson.getLabel("foo"));
        expected.add(hudson.getLabel("bar"));
        NodeLabelCache.nodeLabels.put(hudson, expected);
        Collection<Label> labels = new PlatformLabeler().findLabels(hudson);
        assertEquals(expected, labels);
    }

    public void testLookupUncached() throws Exception {
        /* remove the hudson node from the cache */
        if (NodeLabelCache.nodeLabels.containsKey(hudson)) {
            NodeLabelCache.nodeLabels.remove(hudson);
        }
        Collection<Label> labels = new PlatformLabeler().findLabels(hudson);
        assertEquals(0, labels.size());
    }
}
