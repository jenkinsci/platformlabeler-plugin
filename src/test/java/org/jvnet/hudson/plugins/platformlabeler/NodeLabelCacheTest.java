package org.jvnet.hudson.plugins.platformlabeler;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import hudson.model.Computer;
import hudson.model.Node;
import hudson.model.TaskListener;
import hudson.model.labels.LabelAtom;
import hudson.remoting.VirtualChannel;
import hudson.slaves.RetentionStrategy;
import java.io.IOException;
import java.nio.charset.Charset;
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
    assertThat(labelsBefore, everyItem(isIn(labelsAfter)));
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
}
