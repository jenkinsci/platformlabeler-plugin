package org.jvnet.hudson.plugins.platformlabeler;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

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
        labelConfig.setWindowsFeatureUpdate(false);
        nodeProperty.setLabelConfig(labelConfig);
        r.jenkins.getNodeProperties().add(nodeProperty);

        nodeLabelCache.onConfigurationChange();

        Collection<LabelAtom> expected = new HashSet<>();
        expected.add(r.jenkins.getSelfLabel());
        expected.add(r.jenkins.getLabelAtom(platformDetails.getName()));

        Set<LabelAtom> labelsAfter = computer.getNode().getAssignedLabels();
        assertThat(labelsAfter, is(expected));
    }

    @Test
    public void configuredTwoLabels() {
        PlatformLabelerNodeProperty nodeProperty = new PlatformLabelerNodeProperty();
        LabelConfig labelConfig = new LabelConfig();
        labelConfig.setArchitectureName(false);
        labelConfig.setArchitectureNameVersion(false);
        labelConfig.setName(false);
        labelConfig.setNameVersion(false);
        labelConfig.setWindowsFeatureUpdate(false);
        nodeProperty.setLabelConfig(labelConfig);
        r.jenkins.getNodeProperties().add(nodeProperty);

        nodeLabelCache.onConfigurationChange();

        Collection<LabelAtom> expected = new HashSet<>();
        expected.add(r.jenkins.getSelfLabel());
        expected.add(r.jenkins.getLabelAtom(platformDetails.getArchitecture()));
        expected.add(r.jenkins.getLabelAtom(platformDetails.getVersion()));

        Set<LabelAtom> labelsAfter = computer.getNode().getAssignedLabels();
        assertThat(labelsAfter, is(expected));
    }

    @Test
    public void configuredAllLabelsOnNode() {
        PlatformLabelerNodeProperty nodeProperty = new PlatformLabelerNodeProperty();
        LabelConfig labelConfig = new LabelConfig();
        nodeProperty.setLabelConfig(labelConfig);
        r.jenkins.getNodeProperties().add(nodeProperty);

        nodeLabelCache.onConfigurationChange();

        Collection<LabelAtom> expected = new HashSet<>();
        expected.add(r.jenkins.getSelfLabel());
        expected.add(r.jenkins.getLabelAtom(platformDetails.getArchitecture()));
        expected.add(r.jenkins.getLabelAtom(platformDetails.getVersion()));
        expected.add(r.jenkins.getLabelAtom(platformDetails.getName()));
        expected.add(r.jenkins.getLabelAtom(platformDetails.getNameVersion()));
        expected.add(r.jenkins.getLabelAtom(platformDetails.getArchitectureName()));
        expected.add(r.jenkins.getLabelAtom(platformDetails.getArchitectureNameVersion()));
        if (platformDetails.getWindowsFeatureUpdate() != null) {
            /* Non-windows won't have a WindowsFeatureUpdate value.
             * Windows that have not installed a feature update won't
             * have a WindowsFeatureUpdate value.
             */
            expected.add(r.jenkins.getLabelAtom(platformDetails.getWindowsFeatureUpdate()));
        }

        Set<LabelAtom> labelsAfter = computer.getNode().getAssignedLabels();
        assertThat(labelsAfter, is(expected));
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
        labelConfig.setWindowsFeatureUpdate(false);
        nodeProperty.setLabelConfig(labelConfig);
        r.jenkins.getNodeProperties().add(nodeProperty);

        nodeLabelCache.onConfigurationChange();

        Collection<LabelAtom> expected = new HashSet<>();
        expected.add(r.jenkins.getSelfLabel());
        expected.add(r.jenkins.getLabelAtom(platformDetails.getArchitecture()));
        expected.add(r.jenkins.getLabelAtom(platformDetails.getName()));
        expected.add(r.jenkins.getLabelAtom(platformDetails.getNameVersion()));
        expected.add(r.jenkins.getLabelAtom(platformDetails.getArchitectureName()));
        expected.add(r.jenkins.getLabelAtom(platformDetails.getArchitectureNameVersion()));

        Set<LabelAtom> labelsAfter = computer.getNode().getAssignedLabels();
        assertThat(labelsAfter, is(expected));
    }

    @Test
    public void globalConfigOnlyArchitecture() {

        PlatformLabelerGlobalConfiguration globalConfig =
                GlobalConfiguration.all().getInstance(PlatformLabelerGlobalConfiguration.class);

        LabelConfig globalLabelConfig = new LabelConfig();

        globalLabelConfig.setVersion(false);
        globalLabelConfig.setName(false);
        globalLabelConfig.setWindowsFeatureUpdate(false);
        globalLabelConfig.setArchitectureName(false);
        globalLabelConfig.setArchitectureNameVersion(false);
        globalLabelConfig.setNameVersion(false);

        globalConfig.setLabelConfig(globalLabelConfig);

        nodeLabelCache.onConfigurationChange();

        Collection<LabelAtom> expected = new HashSet<>();
        expected.add(r.jenkins.getSelfLabel());
        expected.add(r.jenkins.getLabelAtom(platformDetails.getArchitecture()));

        Set<LabelAtom> labelsAfter = computer.getNode().getAssignedLabels();
        assertThat(labelsAfter, is(expected));
    }

    @Test
    public void configRoundTripTest() throws Exception {
        PlatformLabelerGlobalConfiguration globalConfig =
                GlobalConfiguration.all().getInstance(PlatformLabelerGlobalConfiguration.class);
        LabelConfig globalLabelConfigBefore = globalConfig.getLabelConfig();
        r.configRoundtrip();
        LabelConfig globalLabelConfigAfter = globalConfig.getLabelConfig();
        assertThat(
                globalLabelConfigBefore.isArchitecture(),
                is(globalLabelConfigAfter.isArchitecture()));
        assertThat(globalLabelConfigBefore.isName(), is(globalLabelConfigAfter.isName()));
        assertThat(globalLabelConfigBefore.isVersion(), is(globalLabelConfigAfter.isVersion()));
        assertThat(
                globalLabelConfigBefore.isWindowsFeatureUpdate(),
                is(globalLabelConfigAfter.isWindowsFeatureUpdate()));
    }
}
