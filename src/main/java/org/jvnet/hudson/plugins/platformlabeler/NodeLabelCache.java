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
import hudson.model.Computer;
import hudson.model.Hudson;
import hudson.model.Node;
import hudson.model.Label;
import hudson.model.TaskListener;
import hudson.remoting.VirtualChannel;
import hudson.slaves.ComputerListener;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A cache of Node labels.
 *
 * While it would be nice to have a single implementation extending both
 * FindLabels and ComputerListener, because Hudson uses subclassing its not
 * easy to do so. Instead we provide a static cache which the LabelFinder
 * in our package can read.
 */
@Extension
public class NodeLabelCache extends ComputerListener {

    /**
     * The labels computed for nodes - accessible package wide.
     */
    static transient WeakHashMap<Node, Set<Label>> nodeLabels = new WeakHashMap<Node, Set<Label>>();
    /**
     * Logging of issues
     */
    private final transient Logger logger = Logger.getLogger("org.jvnet.hudson.plugins.platformlabeler");

    /**
     * When a computer comes online, probe it for its platform labels.
     */
    @Override
    public void onOnline(Computer computer, TaskListener listener) throws IOException, InterruptedException {
        cacheLabels(computer);
        refreshModel(computer);
    }

    /**
     * Caches the labels for the computer against its node.
     */
    void cacheLabels(Computer computer) throws IOException, InterruptedException {
        /* Cache the labels for the node */
        nodeLabels.put(computer.getNode(), requestNodeLabels(computer));
    }

    /**
     * Update Hudson's model so that labels for this computer are up to date.
     */
    void refreshModel(final Computer computer) {
        computer.getNode().getAssignedLabels();
    }

    private Set<Label> requestNodeLabels(Computer computer) throws IOException, InterruptedException {
        final VirtualChannel channel = computer.getChannel();
        if (null == channel) {
            // Cannot obtain details from an unconnected node. While we should
            // never ask for such details, its possible that we may attempt to
            // ask while the computer was asynchronously disconnecting.
            throw new IOException("No virtual channel available");
        }
        final Set<Label> result = new HashSet<Label>();
        final Hudson hudson = Hudson.getInstance();
        try {
            final Set<String> labels = channel.call(new PlatformDetailsTask());
            for (String label : labels) {
                result.add(hudson.getLabel(label));
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to read labels", e);
            throw e;
        }
        return result;
    }
}
