package org.jvnet.hudson.plugins.platformlabeler;

import hudson.Extension;
import hudson.model.Node;
import hudson.slaves.NodeProperty;
import hudson.slaves.NodePropertyDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/** Allows to configure which labels should be generate for the node. */
public class PlatformLabelerNodeProperty extends NodeProperty<Node> {

    private LabelConfig labelConfig;

    @DataBoundConstructor
    public PlatformLabelerNodeProperty() {
        /* Intentionally empty constructor */
    }

    public LabelConfig getLabelConfig() {
        /* Return a defensive copy so that caller cannot modify state of this object */
        return new LabelConfig(labelConfig);
    }

    @DataBoundSetter
    public void setLabelConfig(LabelConfig labelConfig) {
        /* Use a defensive copy so that labelConfig is not modifiable outside this object */
        this.labelConfig = new LabelConfig(labelConfig);
    }

    @Extension
    public static class DescriptorImpl extends NodePropertyDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.Automatic_PlatformLabels();
        }
    }
}
