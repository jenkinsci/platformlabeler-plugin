package org.jvnet.hudson.plugins.platformlabeler;

import java.io.Serializable;

/** Stores the platform details of a node */
public class PlatformDetails implements Serializable {

  private static final long serialVersionUID = 1L;

  private final String name;
  private final String architecture;
  private final String version;
  private final String architectureNameVersion;
  private final String architectureName;
  private final String nameVersion;

  public PlatformDetails(String name, String architecture, String version) {
    this.name = name;
    this.architecture = architecture;
    this.version = version;
    architectureNameVersion = architecture + "-" + name + "-" + version;
    architectureName = architecture + "-" + name;
    nameVersion = name + "-" + version;
  }

  public String getName() {
    return name;
  }

  public String getArchitecture() {
    return architecture;
  }

  public String getVersion() {
    return version;
  }

  public String getArchitectureNameVersion() {
    return architectureNameVersion;
  }

  public String getArchitectureName() {
    return architectureName;
  }

  public String getNameVersion() {
    return nameVersion;
  }
}
