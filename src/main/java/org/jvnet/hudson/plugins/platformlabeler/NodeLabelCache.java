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

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.Computer;
import hudson.model.Node;
import hudson.model.TaskListener;
import hudson.model.labels.LabelAtom;
import hudson.remoting.Channel;
import hudson.remoting.VirtualChannel;
import hudson.slaves.ComputerListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;

/** A cache of Node labels for the LabelFinder in our package. */
@Extension
public class NodeLabelCache extends ComputerListener {

    /** The OS properties for nodes. */
    private static transient Map<Computer, PlatformDetails> nodePlatformProperties =
            Collections.synchronizedMap(new WeakHashMap<>());
    /** The labels computed for nodes - accessible package wide. */
    static transient Map<Node, Collection<LabelAtom>> nodeLabels = new WeakHashMap<>();
    /** Logging of issues. */
    private static final transient Logger LOGGER = Logger.getLogger("org.jvnet.hudson.plugins.platformlabeler");

    /**
     * When a computer is about to come online, probe it for its platform labels.
     * Typically for ephemeral agent that connect and disconnect frequently.
     *
     * @param computer agent whose labels will be cached
     * @param channel This is the channel object to talk to the agent
     * @param root The directory where this agent stores files. The same as Node.getRootPath(), except that method returns null until the agent is connected. So this parameter is passed explicitly instead.
     * @param listener logging destination for agent that is connecting
     * @throws java.io.IOException on IO error
     * @throws java.lang.InterruptedException on thread interrupt
     */
    @Override
    public final void preOnline(final Computer computer, Channel channel, FilePath root, final TaskListener listener)
            throws IOException, InterruptedException {
        try {
            cacheAndRefreshModel(computer, channel);
            saveNodeLabel(computer.getNode());
        } catch (Exception e) {
            String name = "unnamed agent"; // built-in (and others) may not have a name during preOnline
            if (computer != null && !computer.getName().isEmpty()) {
                name = computer.getName();
            }

            listener.getLogger()
                    .println("Ignored platform detail collection failure for '" + name + "' during preOnline phase. "
                            + e);
        }
    }

    /**
     * When a computer is online, probe it for its platform labels.
     * Typically for built-in agents that are online all the time (like built-in node) ensuring they also get their labels updated.
     *
     * @param computer agent whose labels will be cached
     * @param ignored TaskListener that is ignored
     * @throws java.io.IOException on IO error
     * @throws java.lang.InterruptedException on thread interrupt
     */
    @Override
    public final void onOnline(final Computer computer, final TaskListener ignored)
            throws IOException, InterruptedException {
        // Do not query again if labels were populated during the preOnline phase
        if (nodePlatformProperties.get(computer) == null) {
            cacheAndRefreshModel(computer, computer.getChannel());
        }
    }

    public final void cacheAndRefreshModel(final Computer computer, VirtualChannel channel)
            throws IOException, InterruptedException {
        cacheLabels(computer, channel);
        refreshModel(computer);
    }

    /** When any computer has changed, update the platform labels according to the configuration. */
    @Override
    public final void onConfigurationChange() {
        LOGGER.log(Level.FINEST, "onConfigurationChange() called to refresh platform labels");
        synchronized (nodePlatformProperties) {
            nodePlatformProperties.forEach((node, labels) -> {
                refreshModel(node);
            });
        }
    }

    /**
     * Caches the labels for the computer against its node.
     *
     * @param computer node whose labels will be cached
     * @throws IOException on I/O error
     * @throws InterruptedException on thread interruption
     */
    final void cacheLabels(final Computer computer, final VirtualChannel channel)
            throws IOException, InterruptedException {
        /* Cache the labels for the node */
        nodePlatformProperties.put(computer, requestComputerPlatformDetails(computer, channel));
    }

    @SuppressFBWarnings(value = "CRLF_INJECTION_LOGS", justification = "CRLF not allowed in label display names")
    private void logUpdateNodeException(Node node, IOException e) {
        LOGGER.log(
                Level.FINE,
                String.format("Exception updating node '%s' during label refresh", node.getDisplayName()),
                e);
    }

    @SuppressFBWarnings(value = "CRLF_INJECTION_LOGS", justification = "CRLF not allowed in label display names")
    private void logUpdateNodeResult(boolean result, Node node, Set<LabelAtom> assignedLabels) {
        LOGGER.log(
                Level.FINEST,
                String.format(
                        "Update of node '%s' %s with assigned labels %s",
                        node.getDisplayName(),
                        result ? "succeeded" : "failed",
                        Arrays.toString(assignedLabels.toArray())));
    }
    /**
     * Update Jenkins' model so that labels for this computer are up to date.
     *
     * @param computer node whose labels will be cached
     */
    final void refreshModel(final Computer computer) {
        if (computer != null) {
            Node node = computer.getNode();
            if (node != null) {
                Collection<LabelAtom> labels = getLabelsForNode(node);
                nodeLabels.put(node, labels);
                node.getAssignedLabels();
            }
        }
    }

    /**
     * Save the node to ensure label will see the node updated when platform details are added (or
     * updated).
     * This will ensure a node has the same state if we were adding labels via the UI.
     * See JENKINS-72224
     *
     * @param node Node whose labels should be saved
     */
    final void saveNodeLabel(Node node) {
        if (node == null) {
            LOGGER.log(Level.FINEST, "Node is null. Unable to save labels and update node.");
            return;
        }
        Set<LabelAtom> assignedLabels = node.getAssignedLabels();
        try {
            // Save the node to ensure label will see the node updated when platform details are added (or
            // updated).
            // This will ensure a node has the same state if we were adding labels via the UI.
            // See JENKINS-72224
            boolean result = Jenkins.get().updateNode(node);
            logUpdateNodeResult(result, node, assignedLabels);
        } catch (IOException e) {
            logUpdateNodeException(node, e);
        }
    }

    /**
     * Return PlatformDetails of the computer.
     *
     * @param computer agent whose platform details are returned
     * @return PlatformDetails for computer
     * @throws IOException on I/O error
     * @throws InterruptedException on thread interruption
     */
    @NonNull
    PlatformDetails requestComputerPlatformDetails(final Computer computer, final VirtualChannel channel)
            throws IOException, InterruptedException {
        if (computer == null || channel == null) {
            // Cannot obtain details from an unconnected node. While we should
            // never ask for such details, its possible that we may attempt to
            // ask while the computer was asynchronously disconnecting.
            throw new IOException("No virtual channel available");
        }

        try {
            return channel.call(new PlatformDetailsTask());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to read labels", e);
            throw e;
        }
    }

    /**
     * Return collection of generated labels for the given node.
     *
     * @param node Node whose labels should be generated
     * @return Collection with labels
     */
    Collection<LabelAtom> getLabelsForNode(final Node node) {

        Set<LabelAtom> result = new HashSet<>();

        Computer computer = node.toComputer();

        if (computer == null) {
            return result;
        }

        PlatformDetails pp = nodePlatformProperties.get(computer);

        LabelConfig labelConfig = getLabelConfig(node);

        final Jenkins jenkins = Jenkins.get();

        if (pp == null) {
            return result;
        }

        if (labelConfig.isArchitecture()) {
            result.add(jenkins.getLabelAtom(pp.getArchitecture()));
        }

        if (labelConfig.isName()) {
            result.add(jenkins.getLabelAtom(pp.getName()));
        }

        if (labelConfig.isVersion()) {
            result.add(jenkins.getLabelAtom(pp.getVersion()));
        }

        if (labelConfig.isNameVersion()) {
            result.add(jenkins.getLabelAtom(pp.getNameVersion()));
        }

        if (labelConfig.isArchitectureName()) {
            result.add(jenkins.getLabelAtom(pp.getArchitectureName()));
        }

        if (labelConfig.isArchitectureNameVersion()) {
            result.add(jenkins.getLabelAtom(pp.getArchitectureNameVersion()));
        }

        if (labelConfig.isWindowsFeatureUpdate() && pp.getWindowsFeatureUpdate() != null) {
            result.add(jenkins.getLabelAtom(pp.getWindowsFeatureUpdate()));
        }

        if (labelConfig.isOsName() && pp.getOsName() != null) {
            result.add(jenkins.getLabelAtom(pp.getOsName()));
        }

        return result;
    }

    /**
     * Returns the labelConfig of the given node if defined or the globally configured one.
     *
     * @param node The node to check.
     * @return The labelConfig to be used for the node
     */
    private LabelConfig getLabelConfig(final Node node) {
        LabelConfig labelConfig = GlobalConfiguration.all()
                .getInstance(PlatformLabelerGlobalConfiguration.class)
                .getLabelConfig();

        PlatformLabelerNodeProperty nodeProperty = node.getNodeProperty(PlatformLabelerNodeProperty.class);

        if (nodeProperty != null) {
            labelConfig = nodeProperty.getLabelConfig();
        }
        /* Return a defensive copy so that the caller cannot modify state of this object */
        return new LabelConfig(labelConfig);
    }
}
