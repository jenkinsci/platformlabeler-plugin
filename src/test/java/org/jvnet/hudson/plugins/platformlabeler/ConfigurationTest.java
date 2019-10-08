package org.jvnet.hudson.plugins.platformlabeler;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import hudson.model.Computer;
import hudson.model.labels.LabelAtom;
import hudson.slaves.ComputerListener;
import java.util.Set;
import jenkins.model.GlobalConfiguration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

public class ConfigurationTest {
  @Rule public final JenkinsRule r = new JenkinsRule();

  private Computer computer;
  private NodeLabelCache nodeLabelCache;

  @Before
  public void setUp() {
    computer = r.jenkins.toComputer();
    nodeLabelCache = ComputerListener.all().get(NodeLabelCache.class);
  }

  @Test
  public void configuredOnlyOneLabel() {
    PlatformLabelerNodeProperty nodeProperty = new PlatformLabelerNodeProperty();
    LabelConfig labelConfig = new LabelConfig();
    labelConfig.setArchitecture(false);
    labelConfig.setArchitectureName(false);
    labelConfig.setArchitectureNameVersion(false);
    labelConfig.setVersion(false);
    labelConfig.setNameVersion(false);
    nodeProperty.setLabelConfig(labelConfig);
    r.jenkins.getNodeProperties().add(nodeProperty);

    nodeLabelCache.onConfigurationChange();
    Set<LabelAtom> labelsAfter = computer.getNode().getAssignedLabels();
    assertThat(labelsAfter.size(), is(2));
  }

  @Test
  public void configuredTwoLabels() {
    PlatformLabelerNodeProperty nodeProperty = new PlatformLabelerNodeProperty();
    LabelConfig labelConfig = new LabelConfig();
    labelConfig.setArchitectureName(false);
    labelConfig.setArchitectureNameVersion(false);
    labelConfig.setName(false);
    labelConfig.setNameVersion(false);
    nodeProperty.setLabelConfig(labelConfig);
    r.jenkins.getNodeProperties().add(nodeProperty);

    nodeLabelCache.onConfigurationChange();
    Set<LabelAtom> labelsAfter = computer.getNode().getAssignedLabels();
    assertThat(nodeLabelCache.getLabelsForNode(computer.getNode()).size(), is(2));
    assertThat(labelsAfter.size(), is(3));
  }

  @Test
  public void configuredAllLabels() {
    PlatformLabelerNodeProperty nodeProperty = new PlatformLabelerNodeProperty();
    LabelConfig labelConfig = new LabelConfig();
    nodeProperty.setLabelConfig(labelConfig);
    r.jenkins.getNodeProperties().add(nodeProperty);

    nodeLabelCache.onConfigurationChange();
    Set<LabelAtom> labelsAfter = computer.getNode().getAssignedLabels();
    assertThat(labelsAfter.size(), is(7));
  }

  @Test
  public void nodeConfigOverridesGlobalConfig() {

    PlatformLabelerGlobalConfiguration globalConfig =
        GlobalConfiguration.all().getInstance(PlatformLabelerGlobalConfiguration.class);

    LabelConfig globalLabelConfig = new LabelConfig();

    globalLabelConfig.setVersion(false);
    globalLabelConfig.setArchitecture(false);

    globalConfig.setLabelConfig(globalLabelConfig);

    PlatformLabelerNodeProperty nodeProperty = new PlatformLabelerNodeProperty();
    LabelConfig labelConfig = new LabelConfig();
    labelConfig.setVersion(false);
    nodeProperty.setLabelConfig(labelConfig);
    r.jenkins.getNodeProperties().add(nodeProperty);

    nodeLabelCache.onConfigurationChange();
    Set<LabelAtom> labelsAfter = computer.getNode().getAssignedLabels();
    assertThat(nodeLabelCache.getLabelsForNode(computer.getNode()).size(), is(5));
    assertThat(labelsAfter.size(), is(6));
  }
}
