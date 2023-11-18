package org.jvnet.hudson.plugins.platformlabeler;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.io.Serializable;

/** Stores the platform details of a node. */
public class PlatformDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    private final String architecture;
    private final String version;
    private final String windowsFeatureUpdate;
    private final String architectureNameVersion;
    private final String architectureName;
    private final String nameVersion;

    /**
     * Platform details constructor (deprecated).
     *
     * @param name name of operating system, as in windows, debian, ubuntu, etc.
     * @param architecture hardware architecture, as in amd64, aarch64, etc.
     * @param version version of operating system, as in 9.1, 14.04, etc.
     */
    @Deprecated
    public PlatformDetails(@NonNull String name, @NonNull String architecture, @NonNull String version) {
        this(name, architecture, version, null);
    }

    /**
     * Platform details constructor.
     *
     * @param name name of operating system, as in windows, debian, ubuntu, etc.
     * @param architecture hardware architecture, as in amd64, aarch64, etc.
     * @param version version of operating system, as in 9.1, 14.04, etc.
     * @param windowsFeatureUpdate windows feature update version string, as in 1809, 1903, 2009,
     *     2103, etc.
     */
    public PlatformDetails(
            @NonNull String name,
            @NonNull String architecture,
            @NonNull String version,
            @CheckForNull String windowsFeatureUpdate) {
        this.name = name;
        this.architecture = architecture;
        this.version = version;
        architectureNameVersion = architecture + "-" + name + "-" + version;
        architectureName = architecture + "-" + name;
        nameVersion = name + "-" + version;
        String featureUpdate = windowsFeatureUpdate;
        if (featureUpdate != null && featureUpdate.isEmpty()) {
            featureUpdate = null;
        }
        this.windowsFeatureUpdate = featureUpdate;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getArchitecture() {
        return architecture;
    }

    @NonNull
    public String getVersion() {
        return version;
    }

    @NonNull
    public String getArchitectureNameVersion() {
        return architectureNameVersion;
    }

    @NonNull
    public String getArchitectureName() {
        return architectureName;
    }

    @NonNull
    public String getNameVersion() {
        return nameVersion;
    }

    @CheckForNull
    public String getWindowsFeatureUpdate() {
        return windowsFeatureUpdate;
    }
}
