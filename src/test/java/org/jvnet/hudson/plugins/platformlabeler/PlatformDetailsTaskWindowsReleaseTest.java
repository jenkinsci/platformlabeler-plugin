package org.jvnet.hudson.plugins.platformlabeler;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.io.FileMatchers.*;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

public class PlatformDetailsTaskWindowsReleaseTest {

    /**
     * Generate test parameters for Windows feature updates using sample files stored as resources.
     *
     * @return parameter values to be tested
     */
    public static Stream<Object[]> generateWindowsReleaseFileNames() {
        String packageName = PlatformDetailsTaskWindowsReleaseTest.class.getPackage().getName();
        Reflections reflections = new Reflections(packageName, Scanners.Resources);
        Set<String> fileNames = reflections.getResources(Pattern.compile(".*reg-query"));
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

    @ParameterizedTest
    @MethodSource("generateWindowsReleaseFileNames")
    @DisplayName("Compute reg-query labels")
    void testComputeLabelsForOsRelease(
            String windowsReleaseFileName,
            String expectedName,
            String expectedVersion,
            String expectedArch)
            throws Exception {
        PlatformDetailsTask details = new PlatformDetailsTask();
        URL resource = getClass().getResource(windowsReleaseFileName);
        File windowsReleaseFile = new File(resource.toURI());
        assertThat(windowsReleaseFile, is(anExistingFile()));
        WindowsRelease release = new WindowsRelease(windowsReleaseFile);
        PlatformDetails result = details.computeLabels("amd64", "windows", "10.0", release);
        assertThat(result.getArchitecture(), is(expectedArch));
        assertThat(result.getArchitectureName(), is(expectedArch + "-" + expectedName));
        assertThat(
                result.getArchitectureNameVersion(),
                is(expectedArch + "-" + expectedName + "-" + expectedVersion));
        assertThat(result.getName(), is(expectedName));
        assertThat(result.getNameVersion(), is(expectedName + "-" + expectedVersion));
        assertThat(result.getVersion(), is(expectedVersion));
    }

    private static String computeExpectedName(String filename) {
        if (filename.contains("windows")) {
            return "windows";
        }
        return filename.toLowerCase();
    }

    private static String computeExpectedVersion(String filename) {
        File file = new File(filename);
        File parentDir = file.getParentFile();
        String expectedVersion = parentDir.getName();
        if (filename.contains("windows")) {
            expectedVersion = expectedVersion.replaceAll("[.][0-9][0-9][0-9][0-9]$", "");
        }
        return expectedVersion;
    }
}
