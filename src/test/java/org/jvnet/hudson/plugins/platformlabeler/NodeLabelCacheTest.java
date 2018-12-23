package org.jvnet.hudson.plugins.platformlabeler;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import hudson.model.Computer;
import hudson.model.TaskListener;
import hudson.model.labels.LabelAtom;
import java.util.Set;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

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
}
