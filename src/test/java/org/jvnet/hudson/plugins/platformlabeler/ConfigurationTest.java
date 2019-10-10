package org.jvnet.hudson.plugins.platformlabeler;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import hudson.model.Computer;
import hudson.model.labels.LabelAtom;
import hudson.slaves.ComputerListener;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
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
  private PlatformDetails platformDetails;

  @Before
  public void setUp() throws IOException, InterruptedException {
    computer = r.jenkins.toComputer();
    nodeLabelCache = ComputerListener.all().get(NodeLabelCache.class);
    platformDetails = nodeLabelCache.requestComputerPlatformDetails(computer);
  }

  @Test
  public void configuredNameOnlyLabel() {
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

    Collection<LabelAtom> expected = new HashSet<>();
    expected.add(r.jenkins.getLabelAtom("master"));
    expected.add(r.jenkins.getLabelAtom(platformDetails.getName()));

    Set<LabelAtom> labelsAfter = computer.getNode().getAssignedLabels();
    assertEquals(expected, labelsAfter);
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

    Collection<LabelAtom> expected = new HashSet<>();
    expected.add(r.jenkins.getLabelAtom("master"));
    expected.add(r.jenkins.getLabelAtom(platformDetails.getArchitecture()));
    expected.add(r.jenkins.getLabelAtom(platformDetails.getVersion()));

    Set<LabelAtom> labelsAfter = computer.getNode().getAssignedLabels();
    assertEquals(expected, labelsAfter);
  }

  @Test
  public void configuredAllLabelsOnNode() {
    PlatformLabelerNodeProperty nodeProperty = new PlatformLabelerNodeProperty();
    LabelConfig labelConfig = new LabelConfig();
    nodeProperty.setLabelConfig(labelConfig);
    r.jenkins.getNodeProperties().add(nodeProperty);

    nodeLabelCache.onConfigurationChange();

    Collection<LabelAtom> expected = new HashSet<>();
    expected.add(r.jenkins.getLabelAtom("master"));
    expected.add(r.jenkins.getLabelAtom(platformDetails.getArchitecture()));
    expected.add(r.jenkins.getLabelAtom(platformDetails.getVersion()));
    expected.add(r.jenkins.getLabelAtom(platformDetails.getName()));
    expected.add(r.jenkins.getLabelAtom(platformDetails.getNameVersion()));
    expected.add(r.jenkins.getLabelAtom(platformDetails.getArchitectureName()));
    expected.add(r.jenkins.getLabelAtom(platformDetails.getArchitectureNameVersion()));

    Set<LabelAtom> labelsAfter = computer.getNode().getAssignedLabels();
    assertEquals(expected, labelsAfter);
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

    Collection<LabelAtom> expected = new HashSet<>();
    expected.add(r.jenkins.getLabelAtom("master"));
    expected.add(r.jenkins.getLabelAtom(platformDetails.getArchitecture()));
    expected.add(r.jenkins.getLabelAtom(platformDetails.getName()));
    expected.add(r.jenkins.getLabelAtom(platformDetails.getNameVersion()));
    expected.add(r.jenkins.getLabelAtom(platformDetails.getArchitectureName()));
    expected.add(r.jenkins.getLabelAtom(platformDetails.getArchitectureNameVersion()));

    Set<LabelAtom> labelsAfter = computer.getNode().getAssignedLabels();
    assertEquals(expected, labelsAfter);
  }

  @Test
  public void globalConfigOnlyArchitecture() {

    PlatformLabelerGlobalConfiguration globalConfig =
        GlobalConfiguration.all().getInstance(PlatformLabelerGlobalConfiguration.class);

    LabelConfig globalLabelConfig = new LabelConfig();

    globalLabelConfig.setVersion(false);
    globalLabelConfig.setName(false);
    globalLabelConfig.setArchitectureName(false);
    globalLabelConfig.setArchitectureNameVersion(false);
    globalLabelConfig.setNameVersion(false);

    globalConfig.setLabelConfig(globalLabelConfig);

    nodeLabelCache.onConfigurationChange();

    Collection<LabelAtom> expected = new HashSet<>();
    expected.add(r.jenkins.getLabelAtom("master"));
    expected.add(r.jenkins.getLabelAtom(platformDetails.getArchitecture()));

    Set<LabelAtom> labelsAfter = computer.getNode().getAssignedLabels();
    assertEquals(expected, labelsAfter);
  }
}
