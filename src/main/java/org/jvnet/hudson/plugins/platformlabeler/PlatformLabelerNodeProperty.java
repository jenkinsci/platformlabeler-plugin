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
  public PlatformLabelerNodeProperty() {}

  public LabelConfig getLabelConfig() {
    return labelConfig;
  }

  @DataBoundSetter
  public void setLabelConfig(LabelConfig labelConfig) {
    this.labelConfig = labelConfig;
  }

  @Extension
  public static class DescriptorImpl extends NodePropertyDescriptor {

    @Override
    public String getDisplayName() {
      return "Automatic Platform Labels";
    }
  }
}
