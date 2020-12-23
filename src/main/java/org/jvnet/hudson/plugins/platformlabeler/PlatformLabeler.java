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

import hudson.Extension;
import hudson.model.LabelFinder;
import hudson.model.Node;
import hudson.model.labels.LabelAtom;
import java.util.Collection;
import java.util.Collections;

/**
 * Obtain labels for Nodes at runtime by querying the operating system running on it. Querying is
 * done via the {@link NodeLabelCache} onOnline extension. PlatformLabeler simply answers from the
 * cache that that extension maintains.
 */
@Extension
public class PlatformLabeler extends LabelFinder {

    /**
     * Returns collection of LabelAtom for the node argument.
     *
     * @param node agent whose labels are returned
     * @return collection of LabelAtom for the node argument
     */
    @Override
    public final Collection<LabelAtom> findLabels(final Node node) {
        Collection<LabelAtom> result = NodeLabelCache.nodeLabels.get(node);
        if (null == result) /* Node that has just attached and we don't have labels yet */ {
            return Collections.emptySet();
        }
        return result;
    }
}
