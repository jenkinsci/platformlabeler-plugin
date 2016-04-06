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
import static org.mockito.Mockito.*;

/**
 *
 * @author robertc
 */
public class PlatformLabelerTest extends HudsonTestCase {

    public void testLookupCached() throws Exception {
        Set<String> expected = new HashSet<String>();
        expected.add("foo");
        expected.add("bar");
        Set<Label> expectedLabels = new HashSet<Label>();
        for ( String l : expected ) {
            expectedLabels.add(hudson.getLabel(l));
        }

        PlatformDetailsTask detailsTask = mock(PlatformDetailsTask.class);
        when(detailsTask.call()).thenReturn(expected);
        PlatformDetailsTaskFactory detailsTaskFactory = mock(PlatformDetailsTaskFactory.class);
        when(detailsTaskFactory.newInstance()).thenReturn(detailsTask);

        NodeLabelCache cache = new NodeLabelCache(detailsTaskFactory);
        cache.cacheLabels(hudson);

        Collection<? extends Label> labels = new PlatformLabeler().findLabels(hudson);
        assertEquals(labels, expectedLabels);
    }

}
