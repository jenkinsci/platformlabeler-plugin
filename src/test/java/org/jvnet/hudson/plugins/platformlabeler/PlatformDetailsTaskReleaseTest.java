package org.jvnet.hudson.plugins.platformlabeler;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.io.FileMatchers.*;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

public class PlatformDetailsTaskReleaseTest {

    /**
     * Generate test parameters for Linux os-release, redhat-release and SuSE-release sample files
     * stored as resources.
     *
     * @return parameter values to be tested
     */
    public static Stream<Object[]> generateReleaseFileNames() {
        String packageName = PlatformDetailsTaskReleaseTest.class.getPackage().getName();
        Reflections reflections = new Reflections(packageName, Scanners.Resources);
        Set<String> fileNames = reflections.getResources(Pattern.compile(".*-release"));
        Collection<Object[]> data = new ArrayList<>(fileNames.size());
        for (String fileName : fileNames) {
            String oneExpectedName = computeExpectedName(fileName);
            String oneExpectedVersion = computeExpectedVersion(fileName);
            String oneExpectedArch = "amd64";
            String trimmedName = fileName.split(".platformlabeler.")[1];
            Object[] oneTest = {trimmedName, oneExpectedName, oneExpectedVersion, oneExpectedArch};
            data.add(oneTest);
        }
        return data.stream();
    }

    @ParameterizedTest(name = "file: {0}, expected: '{1}', {2}, {3}")
    @MethodSource("generateReleaseFileNames")
    @DisplayName("Compute platform details from release file")
    void testComputeLabelsForRelease(
            String releaseFileName,
            String expectedName,
            String expectedVersion,
            String expectedArch)
            throws Exception {
        PlatformDetailsTask details = new PlatformDetailsTask();
        URL resource = getClass().getResource(releaseFileName);
        File releaseFile = new File(resource.toURI());
        assertThat(releaseFile, is(anExistingFile()));
        if (releaseFile.getName().startsWith("redhat")) {
            /* Replaces os-release with Red Hat additional file */
            details.setOsReleaseFile(null);
            details.setDebianVersion(null);
            details.setRedhatRelease(releaseFile);
            details.setSuseRelease(null);
        } else if (releaseFile.getName().startsWith("SuSE")) {
            /* Replaces os-release with SuSE additional file */
            details.setOsReleaseFile(null);
            details.setDebianVersion(null);
            details.setSuseRelease(releaseFile);
            details.setRedhatRelease(null);
        } else if (releaseFileName.startsWith("debian")) {
            /* Adds another file to consider in addition to os-release */
            details.setOsReleaseFile(releaseFile);
            details.setSuseRelease(null);
            details.setRedhatRelease(null);
            /* Extra file needed for Debian testing and unstable. No version in os-release */
            File debianVersionFile = new File(releaseFile.getParentFile(), "debian_version");
            details.setDebianVersion(debianVersionFile.exists() ? debianVersionFile : null);
        } else {
            details.setOsReleaseFile(releaseFile);
            details.setRedhatRelease(null);
            details.setSuseRelease(null);
        }
        String unknown = PlatformDetailsTask.UNKNOWN_VALUE_STRING;
        LsbRelease release = new LsbRelease(unknown, unknown);
        PlatformDetails result = details.computeLabels("amd64", "linux", "xyzzy-abc", release);
        assertThat(result.getName(), is(expectedName));
        assertThat(result.getArchitecture(), is(expectedArch));
        assertThat(result.getVersion(), is(expectedVersion));
        assertThat(result.getArchitectureName(), is(expectedArch + "-" + expectedName));
        assertThat(
                result.getArchitectureNameVersion(),
                is(expectedArch + "-" + expectedName + "-" + expectedVersion));
        assertThat(result.getNameVersion(), is(expectedName + "-" + expectedVersion));
    }

    private static String computeExpectedName(String filename) {
        if (filename.contains("alinux") && !filename.contains("almalinux")) {
            return "AlibabaCloud";
        }
        if (filename.contains("almalinux")) {
            return "Alma";
        }
        if (filename.contains("amzn")) {
            return "Amazon";
        }
        if (filename.contains("alpine")) {
            return "Alpine";
        }
        if (filename.contains("centos")) {
            return "CentOS";
        }
        if (filename.contains("clearlinux")) {
            return "clear-linux-os";
        }
        if (filename.contains("debian")) {
            return "Debian";
        }
        if (filename.contains("fedora")) {
            return "Fedora";
        }
        if (filename.contains("linuxmint-old")) {
            return "LinuxMint";
        }
        if (filename.contains("linuxmintd")) {
            return "Linuxmint";
        }
        if (filename.contains("opensuse")) {
            return "openSUSE";
        }
        if (filename.contains("oraclelinux")) {
            return "OracleServer";
        }
        if (filename.contains("raspbian")) {
            return "Raspbian";
        }
        if (filename.contains("rhel") || filename.contains("ubi")) {
            return "RedHatEnterprise";
        }
        if (filename.contains("ubuntu")) {
            return "Ubuntu";
        }
        if (filename.contains("scientific")) {
            return "Scientific";
        }
        if (filename.contains("sles")) {
            return "SUSE";
        }
        return filename.toLowerCase(Locale.ENGLISH);
    }

    private static String computeExpectedVersion(String filename) {
        File file = new File(filename);
        File parentDir = file.getParentFile();
        if (parentDir.getName().equals("testing") || parentDir.getName().equals("unstable")) {
            /* Debian unstable and Debian testing are indistinguishable by package definition */
            /* See https://unix.stackexchange.com/questions/464812/ for more details */
            return "bullseye";
        }
        return parentDir.getName();
    }
}
