package org.jvnet.hudson.plugins.platformlabeler;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import hudson.model.Computer;
import hudson.model.Node;
import hudson.model.TaskListener;
import hudson.model.labels.LabelAtom;
import hudson.remoting.VirtualChannel;
import hudson.slaves.RetentionStrategy;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.logging.LogRecord;
import javax.servlet.ServletException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * Test NodeLabelCache in a few potential error cases.
 *
 * @author Mark Waite
 */
public class NodeLabelCacheTest {

  @Rule public JenkinsRule r = new JenkinsRule();

  public NodeLabelCacheTest() {}

  private Computer computer;
  private Set<LabelAtom> labelsBefore;
  private NodeLabelCache nodeLabelCache;

  @Before
  public void setUp() {
    computer = r.jenkins.toComputer();
    labelsBefore = computer.getNode().getAssignedLabels();
    assertThat(labelsBefore, is(not(empty())));
    nodeLabelCache = new NodeLabelCache();
  }

  @After
  public void tearDown() {
    Set<LabelAtom> labelsAfter = computer.getNode().getAssignedLabels();
    assertThat(labelsBefore, everyItem(in(labelsAfter)));
  }

  @Test
  public void testOnOnline() throws Exception {
    nodeLabelCache.onOnline(computer, TaskListener.NULL);
  }

  @Test
  public void testCacheLabels() throws Exception {
    nodeLabelCache.cacheLabels(computer);
  }

  @Test
  public void testRefreshModel() {
    nodeLabelCache.refreshModel(computer);
  }

  @Test
  public void testRefreshModelNullComputer() {
    nodeLabelCache.refreshModel(null);
  }

  @Test
  public void testRefreshModelNullingComputer() {
    Computer nullingComputer = new NullingComputer(computer.getNode());
    nodeLabelCache.refreshModel(nullingComputer);
  }

  @Test(expected = IOException.class)
  public void testCacheLabelsNullingComputer() throws Exception {
    Computer nullingComputer = new NullingComputer(computer.getNode());
    nodeLabelCache.cacheLabels(nullingComputer);
  }

  @Test
  public void testGetLabelsForNode_IsNull() throws Exception {
    Node nullingNode = new NullingNode();
    Collection<LabelAtom> labels = nodeLabelCache.getLabelsForNode(nullingNode);
    assertThat(labels, is(empty()));
  }

  /** Class that intentionally returns nulls for test purposes. */
  private class NullingComputer extends Computer {

    public NullingComputer(Node node) {
      super(node);
    }

    @Override
    public Node getNode() {
      /* Intentionally return null to test null node handling */
      return null;
    }

    @Override
    public VirtualChannel getChannel() {
      /* Intentionally return null to test null channel handling */
      return null;
    }

    @Override
    public Charset getDefaultCharset() {
      throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    public List<LogRecord> getLogRecords() throws IOException, InterruptedException {
      throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    public void doLaunchSlaveAgent(StaplerRequest sr, StaplerResponse sr1)
        throws IOException, ServletException {
      throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    protected Future<?> _connect(boolean bln) {
      throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    public Boolean isUnix() {
      throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    public boolean isConnecting() {
      throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    public RetentionStrategy getRetentionStrategy() {
      throw new UnsupportedOperationException("Unsupported");
    }
  }

  private class NullingNode extends Node {
    public hudson.remoting.Callable<hudson.util.ClockDifference, IOException>
        getClockDifferenceCallable() {
      throw new UnsupportedOperationException("Unsupported");
    }

    public hudson.slaves.NodeDescriptor getDescriptor() {
      throw new UnsupportedOperationException("Unsupported");
    }

    public hudson.util.DescribableList<
            hudson.slaves.NodeProperty<?>, hudson.slaves.NodePropertyDescriptor>
        getNodeProperties() {
      throw new UnsupportedOperationException("Unsupported");
    }

    public hudson.FilePath getRootPath() {
      throw new UnsupportedOperationException("Unsupported");
    }

    public hudson.FilePath getWorkspaceFor(hudson.model.TopLevelItem item) {
      throw new UnsupportedOperationException("Unsupported");
    }

    public String getLabelString() {
      throw new UnsupportedOperationException("Unsupported");
    }

    public Computer createComputer() {
      throw new UnsupportedOperationException("Unsupported");
    }

    public Node.Mode getMode() {
      throw new UnsupportedOperationException("Unsupported");
    }

    public int getNumExecutors() {
      throw new UnsupportedOperationException("Unsupported");
    }

    public hudson.Launcher createLauncher(TaskListener listener) {
      throw new UnsupportedOperationException("Unsupported");
    }

    public String getNodeDescription() {
      throw new UnsupportedOperationException("Unsupported");
    }

    @Deprecated
    public void setNodeName(String name) {
      throw new UnsupportedOperationException("Unsupported");
    }

    public String getNodeName() {
      throw new UnsupportedOperationException("Unsupported");
    }
  }
}
