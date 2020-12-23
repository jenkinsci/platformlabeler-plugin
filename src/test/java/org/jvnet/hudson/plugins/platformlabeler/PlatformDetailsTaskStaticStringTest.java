package org.jvnet.hudson.plugins.platformlabeler;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class PlatformDetailsTaskStaticStringTest {

    /**
     * Generate test parameters for cases which can be tested with static string conversions. Linux
     * platform labeling can't be tested with static string conversions. Refer to other tests for
     * Linux platform labeling tests.
     *
     * @return parameter values to be tested
     */
    public static Stream<Object[]> generateTestParameters() {
        Collection<Object[]> data =
                Arrays.asList(
                        new Object[][] {
                            /** General cases for operating system names in platformlabeler-1.1 */
                            {"mac", "amd64", "11.0"}, // macOS
                            {"Solaris", "amd64", "11.3"}, // Solaris
                            {"Solaris", "sparc", "11.3"}, // Solaris
                            {"SunOS", "sparc", "4.1.4"}, // SunOS
                            /**
                             * Special Windows version cases using version names in
                             * platformlabeler-1.1
                             */
                            {"Windows 2000", "amd64", "5.0"}, // Win2000
                            {"Windows 2000", "x86", "5.0"}, // Win2000
                            {"Windows 2003", "amd64", "5.2"}, // Win2003
                            {"Windows 2003", "x86", "5.2"}, // Win2003
                            {"Windows NT", "amd64", "4.0"}, // WinNT
                            {"Windows NT", "x86", "4.0"}, // WinNT
                            {"Windows XP", "amd64", "5.1"}, // WinXP
                            {"Windows XP", "x86", "5.1"}, // WinXP
                            /**
                             * General Windows version cases using version numbers in
                             * platformlabeler-1.1
                             */
                            {"Windows 10", "amd64", "10.0"}, // Win10
                            {"Windows 10", "x86", "10.0"}, // Win10
                            {"Windows 2008R2", "amd64", "6.1"}, // Win2008R2
                            {"Windows 7", "amd64", "6.1"}, // Win7
                            {"Windows 7", "x86", "6.1"}, // Win7
                            {"Windows Server 2012 R2", "amd64", "6.3"}, // Win2012R2
                            {"Windows Vista64", "amd64", "6.0.6001"}, // WinVista
                            {"Windows Vista", "amd64", "6.0.6000"}, // WinVista
                            {"Windows Vista", "x86", "6.0.6000"}, // WinVista
                            /** General case for operating systems unknown to platformlabeler-1.1 */
                            {"FreeBSD", "amd64", "10.3-STABLE"}, // FreeBSD
                        });

        /* Don't add data for this platform if linux - linux decodes the distribution as a label */
        String myName = System.getProperty("os.name");
        if (myName.equalsIgnoreCase("linux")) {
            return data.stream();
        }

        /* Check this platform is in the test data */
        String myArch = System.getProperty("os.arch");
        String myVersion = System.getProperty("os.version");
        for (Object[] testData : data) {
            if (testData[0].equals(myName)
                    && testData[1].equals(myArch)
                    && testData[2].equals(myVersion)) {
                return data.stream();
            }
        }

        /* Add data for this platform, it is not already in the data and is not linux */
        Object[] myTestData = {myName, myArch, myVersion};
        List<Object[]> augmentedData = new ArrayList<>();
        augmentedData.add(myTestData);
        augmentedData.addAll(data);
        return augmentedData.stream();
    }

    @ParameterizedTest(name = "file: {0}, expected: '{1}', {2}, {3}")
    @MethodSource("generateTestParameters")
    @DisplayName("Compute labels from static strings")
    void testComputeLabels(String name, String arch, String version) throws Exception {
        String expectedName = computeExpectedName(name);
        String expectedArch = computeExpectedArch(name, arch);
        String expectedVersion = computeVersion(name, version);
        PlatformDetailsTask details = new PlatformDetailsTask();
        PlatformDetails result = details.computeLabels(arch, name, version);
        assertThat(result.getArchitecture(), is(expectedArch));
        assertThat(result.getName(), is(expectedName));
        assertThat(result.getVersion(), is(expectedVersion));
    }

    private static String computeExpectedArch(String name, String arch) {
        if (!isWindows() || !name.startsWith("Windows")) {
            return arch;
        }
        final String env1 = System.getenv("PROCESSOR_ARCHITECTURE");
        final String env2 = System.getenv("PROCESSOR_ARCHITEW6432");
        if ("amd64".equalsIgnoreCase(env1) || "amd64".equalsIgnoreCase(env2)) {
            arch = "amd64";
        }
        return arch;
    }

    private String computeExpectedName(String name) {
        if (name.startsWith("Windows")) {
            return "windows";
        }
        // Handle cases like "Mac OS X" in the same way as the validation code
        if (name.startsWith("Mac")) {
            return "mac";
        }
        return name.toLowerCase(Locale.ENGLISH);
    }

    private String computeVersion(String name, String version) {
        if (name.startsWith("Windows")) {
            switch (version) {
                case "4.0":
                    version = "nt4";
                    break;
                case "5.0":
                    version = "2000";
                    break;
                case "5.1":
                    version = "xp";
                    break;
                case "5.2":
                    version = "2003";
                    break;
                default:
                    break;
            }
        } else if (name.startsWith("FreeBSD")) {
            return getFreeBsdVersion(version);
        }
        return version;
    }

    private String getFreeBsdVersion(String version) {
        /* If no freebsd-version command, return the provided default value */
        File freebsdVersionCommand = new File("/bin/freebsd-version");
        if (!freebsdVersionCommand.exists()) {
            return version;
        }
        /* Compute the expected version number value */
        try {
            Process p = Runtime.getRuntime().exec("/bin/freebsd-version -u");
            p.waitFor();
            try (BufferedReader b =
                    new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"))) {
                String line = b.readLine();
                if (line != null) {
                    version = line;
                }
            }
        } catch (IOException | InterruptedException e) {
            /* Return version instead of throwing an exception */
        }
        return version;
    }

    private static boolean isWindows() {
        return File.pathSeparatorChar == ';';
    }
}
