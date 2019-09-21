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
import hudson.Extension;
import hudson.model.Computer;
import hudson.model.Node;
import hudson.model.TaskListener;
import hudson.model.labels.LabelAtom;
import hudson.remoting.VirtualChannel;
import hudson.slaves.ComputerListener;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jenkins.model.Jenkins;

/** A cache of Node labels for the LabelFinder in our package. */
@Extension
public class NodeLabelCache extends ComputerListener {

  /** The labels computed for nodes - accessible package wide. */
  static transient WeakHashMap<Node, Collection<LabelAtom>> nodeLabels =
      new WeakHashMap<Node, Collection<LabelAtom>>();
  /** Logging of issues. */
  private static final transient Logger LOGGER =
      Logger.getLogger("org.jvnet.hudson.plugins.platformlabeler");

  /**
   * When a computer comes online, probe it for its platform labels.
   *
   * @param computer agent whose labels will be cached
   * @param ignored TaskListener that is ignored
   * @throws java.io.IOException on IO error
   * @throws java.lang.InterruptedException on thread interrupt
   */
  @Override
  public final void onOnline(final Computer computer, final TaskListener ignored)
      throws IOException, InterruptedException {
    cacheLabels(computer);
    refreshModel(computer);
  }

  /**
   * Caches the labels for the computer against its node.
   *
   * @param computer node whose labels will be cached
   * @throws IOException on I/O error
   * @throws InterruptedException on thread interruption
   */
  final void cacheLabels(final Computer computer) throws IOException, InterruptedException {
    /* Cache the labels for the node */
    nodeLabels.put(computer.getNode(), requestNodeLabels(computer));
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
        node.getAssignedLabels();
      }
    }
  }

  /**
   * Return collection of labels for computer.
   *
   * @param computer agent whose labels are returned
   * @return collection of labels for computer
   * @throws IOException on I/O error
   * @throws InterruptedException on thread interruption
   */
  @NonNull
  private Collection<LabelAtom> requestNodeLabels(final Computer computer)
      throws IOException, InterruptedException {
    final VirtualChannel channel = computer.getChannel();
    if (null == channel) {
      // Cannot obtain details from an unconnected node. While we should
      // never ask for such details, its possible that we may attempt to
      // ask while the computer was asynchronously disconnecting.
      throw new IOException("No virtual channel available");
    }
    final Collection<LabelAtom> result = new HashSet<>();
    final Jenkins jenkins = Jenkins.getInstanceOrNull();
    try {
      final Set<String> labels = channel.call(new PlatformDetailsTask());
      labels.forEach(
          (label) -> {
            result.add(jenkins.getLabelAtom(label));
          });
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Failed to read labels", e);
      throw e;
    }
    return result;
  }
}
