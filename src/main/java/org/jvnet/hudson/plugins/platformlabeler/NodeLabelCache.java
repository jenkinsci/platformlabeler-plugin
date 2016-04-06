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
import hudson.model.Node;
import hudson.model.TaskListener;
import hudson.model.labels.LabelAtom;
import hudson.remoting.VirtualChannel;
import hudson.slaves.ComputerListener;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jenkins.model.Jenkins;

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
    private static transient WeakHashMap<Node, Set<LabelAtom>> nodeLabels = new WeakHashMap<Node, Set<LabelAtom>>();
    /**
     * Logging of issues
     */
    private static final transient Logger logger = Logger.getLogger("org.jvnet.hudson.plugins.platformlabeler");

    private final transient PlatformDetailsTaskFactory platformDetailsTaskClassFactory;

    public NodeLabelCache() {
        this.platformDetailsTaskClassFactory = new PlatformDetailsTaskFactory();
    }

    /**
     * For testing.
     */
    protected NodeLabelCache( PlatformDetailsTaskFactory factory ) {
        platformDetailsTaskClassFactory = factory;
    }

    /**
     * When a computer comes online, probe it for its platform labels.
     */
    @Override
    public void onOnline(Computer computer, TaskListener listener) throws IOException, InterruptedException {
        synchronized( NodeLabelCache.class ) {
            cacheLabels(computer.getNode());
        }
        refreshModel(computer.getNode());
    }

    public static synchronized Set<LabelAtom> getNodeLabels( Node node ) {
        Set<LabelAtom> ret = nodeLabels.get(node);
        if ( ret != null ) return ret;

        try {
            NodeLabelCache cache = new NodeLabelCache();
            ret = cache.requestNodeLabels(node.getChannel());
            nodeLabels.put(node, ret);
        }
        catch ( IOException ex ) {
            // exception already logged in requestNodeLabels
        }
        catch ( InterruptedException ex ) {} // shrug
        return ret;
    }

    /**
     * Caches the labels for the computer against its node.
     */
    protected void cacheLabels(Node node) throws IOException, InterruptedException {
        /* Cache the labels for the node */
        nodeLabels.put(node, requestNodeLabels(node.getChannel()));
    }

    /**
     * Update Hudson's model so that labels for this computer are up to date.
     */
    private void refreshModel(final Node node) {
        node.getAssignedLabels();
    }

    private Set<LabelAtom> requestNodeLabels(VirtualChannel channel) throws IOException, InterruptedException {
        if (null == channel) {
            // Cannot obtain details from an unconnected node. While we should
            // never ask for such details, its possible that we may attempt to
            // ask while the computer was asynchronously disconnecting.
            throw new IOException("No virtual channel available");
        }
        final Set<LabelAtom> result = new HashSet<LabelAtom>();
        final Jenkins jenkins = Jenkins.getInstance();
        try {
            final Set<String> labels = channel.call(platformDetailsTaskClassFactory.newInstance());
            for (String label : labels) {
                result.add(jenkins.getLabelAtom(label));
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to read labels", e);
            throw e;
        }
        return result;
    }
}
