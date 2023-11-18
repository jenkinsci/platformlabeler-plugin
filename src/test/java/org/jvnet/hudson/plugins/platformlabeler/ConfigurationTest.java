package org.jvnet.hudson.plugins.platformlabeler;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

import hudson.model.Computer;
import hudson.model.Label;
import hudson.model.labels.LabelAtom;
import hudson.slaves.ComputerListener;
import hudson.slaves.DumbSlave;
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
    @Rule
    public final JenkinsRule r = new JenkinsRule();

    private Computer computer;
    private NodeLabelCache nodeLabelCache;
    private PlatformDetails platformDetails;

    @Before
    public void setUp() throws IOException, InterruptedException {
        computer = r.jenkins.toComputer();
        nodeLabelCache = ComputerListener.all().get(NodeLabelCache.class);
        platformDetails = nodeLabelCache.requestComputerPlatformDetails(computer, computer.getChannel());
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
        labelConfig.setOsName(false);
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
        labelConfig.setOsName(false);
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
    public void configuredAllLabelsOnEphemeralNode() throws Exception {

        // Create and connect the agent
        DumbSlave agent = r.createSlave(Label.get("agent"));
        agent.toComputer().connect(false).get();

        PlatformLabelerNodeProperty nodeProperty = new PlatformLabelerNodeProperty();
        LabelConfig labelConfig = new LabelConfig();
        nodeProperty.setLabelConfig(labelConfig);
        agent.getNodeProperties().add(nodeProperty);

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
        expected.add(r.jenkins.getLabelAtom(platformDetails.getOsName()));

        // Static labels of the agent
        expected.add(r.jenkins.getLabelAtom("agent"));
        expected.add(r.jenkins.getLabelAtom("slave0"));

        // We don't expect to find 'built-in' on the agent
        expected.removeIf(label -> label.getName().equals("built-in"));

        Set<LabelAtom> labelsAfter = agent.getAssignedLabels();
        assertThat(labelsAfter, containsInAnyOrder(expected.toArray()));
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
        expected.add(r.jenkins.getLabelAtom(platformDetails.getOsName()));

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
        labelConfig.setOsName(false);
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
        globalLabelConfig.setOsName(false);
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
        assertThat(globalLabelConfigBefore.isArchitecture(), is(globalLabelConfigAfter.isArchitecture()));
        assertThat(globalLabelConfigBefore.isName(), is(globalLabelConfigAfter.isName()));
        assertThat(globalLabelConfigBefore.isVersion(), is(globalLabelConfigAfter.isVersion()));
        assertThat(
                globalLabelConfigBefore.isWindowsFeatureUpdate(), is(globalLabelConfigAfter.isWindowsFeatureUpdate()));
        assertThat(globalLabelConfigBefore.isOsName(), is(globalLabelConfigAfter.isOsName()));
    }
}
