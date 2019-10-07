package org.jvnet.hudson.plugins.platformlabeler;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/** Stores configuration about labels to generate */
public class LabelConfig extends AbstractDescribableImpl<LabelConfig> {

  private boolean architecture = true;
  private boolean name = true;
  private boolean version = true;
  private boolean architectureName = true;
  private boolean nameVersion = true;
  private boolean architectureNameVersion = true;

  @DataBoundConstructor
  public LabelConfig() {}

  public boolean isArchitecture() {
    return architecture;
  }

  @DataBoundSetter
  public void setArchitecture(boolean arch) {
    this.architecture = arch;
  }

  public boolean isName() {
    return name;
  }

  @DataBoundSetter
  public void setName(boolean name) {
    this.name = name;
  }

  public boolean isVersion() {
    return version;
  }

  @DataBoundSetter
  public void setVersion(boolean version) {
    this.version = version;
  }

  public boolean isArchitectureName() {
    return architectureName;
  }

  @DataBoundSetter
  public void setArchitectureName(boolean archName) {
    this.architectureName = archName;
  }

  public boolean isNameVersion() {
    return nameVersion;
  }

  @DataBoundSetter
  public void setNameVersion(boolean nameVersion) {
    this.nameVersion = nameVersion;
  }

  public boolean isArchitectureNameVersion() {
    return architectureNameVersion;
  }

  @DataBoundSetter
  public void setArchitectureNameVersion(boolean archNameVersion) {
    this.architectureNameVersion = archNameVersion;
  }

  @Extension
  @Symbol("platformlabelerconfig")
  public static class DescriptorImpl extends Descriptor<LabelConfig> {}
}
