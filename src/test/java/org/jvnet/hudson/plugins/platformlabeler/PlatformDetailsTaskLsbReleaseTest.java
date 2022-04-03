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

public class PlatformDetailsTaskLsbReleaseTest {

    /**
     * Generate test parameters for Linux lsb_release-a sample files stored as resources.
     *
     * @return parameter values to be tested
     */
    public static Stream<Object[]> generateReleaseFileNames() {
        String packageName = PlatformDetailsTaskLsbReleaseTest.class.getPackage().getName();
        Reflections reflections = new Reflections(packageName, Scanners.Resources);
        Set<String> fileNames = reflections.getResources(Pattern.compile(".*lsb_release-a"));
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
    @DisplayName("Compute os-release labels")
    void testComputeLabelsForOsRelease(
            String lsbReleaseFileName,
            String expectedName,
            String expectedVersion,
            String expectedArch)
            throws Exception {
        PlatformDetailsTask details = new PlatformDetailsTask();
        URL resource = getClass().getResource(lsbReleaseFileName);
        File lsbReleaseFile = new File(resource.toURI());
        assertThat(lsbReleaseFile, is(anExistingFile()));
        LsbRelease release = new LsbRelease(lsbReleaseFile);
        File suseReleaseFile = new File(lsbReleaseFile.getParentFile(), "SuSE-release");
        if (suseReleaseFile.isFile()) {
            details.setSuseRelease(suseReleaseFile);
        }
        PlatformDetails result = details.computeLabels("amd64", "linux", "xyzzy-abc", release);
        assertThat(result.getArchitecture(), is(expectedArch));
        assertThat(result.getName(), is(expectedName));
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
            return "AlmaLinux";
        }
        if (filename.contains("amzn")) {
            if (filename.contains("amzn/2018.03")) {
                return "AmazonAMI";
            }
            return "Amazon";
        }
        if (filename.contains("alpine")) {
            return "Alpine";
        }
        if (filename.contains("centos")) {
            return "CentOS";
        }
        if (filename.contains("debian")) {
            return "Debian";
        }
        if (filename.contains("fedora")) {
            return "Fedora";
        }
        if (filename.contains("oraclelinux")) {
            return "OracleServer";
        }
        if (filename.contains("linuxmint-old")) {
            return "LinuxMint";
        }
        if (filename.contains("linuxmintd")) {
            return "Linuxmint";
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
        if (filename.contains("opensuse-leap")) {
            return "openSUSE";
        }
        if (filename.contains("opensuse-tumbleweed")) {
            return "openSUSE";
        }
        return filename.toLowerCase(Locale.ENGLISH);
    }

    private static String computeExpectedVersion(String filename) {
        File file = new File(filename);
        File parentDir = file.getParentFile();
        return parentDir.getName();
    }
}
