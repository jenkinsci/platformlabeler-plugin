package org.jvnet.hudson.plugins.platformlabeler;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class PlatformDetailsTaskStaticStringTest {

  private final String name;
  private final String arch;
  private final String version;
  private final String expectedName;
  private final String expectedArch;
  private final String expectedVersion;

  public PlatformDetailsTaskStaticStringTest(String name, String arch, String version) {
    this.name = name;
    this.arch = arch;
    this.version = version;
    this.expectedName = computeExpectedName(name);
    this.expectedArch = computeExpectedArch(name, arch);
    this.expectedVersion = computeVersion(version);
  }

  /**
   * Generate test parameters for cases which can be tested with static string conversions. Linux
   * platform labeling can't be tested with static string conversions. Refer to other tests for
   * Linux platform labeling tests. Expected values are computed in the test contructor.
   *
   * @return parameter values to be tested
   */
  @Parameters(name = "{0}-{1}-{2}")
  public static Collection<Object[]> generateTestParameters() {
    Collection<Object[]> data =
        Arrays.asList(
            new Object[][] {
              /** General cases for operating system names in platformlabeler-1.1 */
              {"mac", "amd64", "11.0"}, // macOS
              {"Solaris", "amd64", "11.3"}, // Solaris
              {"Solaris", "sparc", "11.3"}, // Solaris
              {"SunOS", "sparc", "4.1.4"}, // SunOS
              /** Special Windows version cases using version names in platformlabeler-1.1 */
              {"Windows 2000", "amd64", "5.0"}, // Win2000
              {"Windows 2000", "x86", "5.0"}, // Win2000
              {"Windows 2003", "amd64", "5.2"}, // Win2003
              {"Windows 2003", "x86", "5.2"}, // Win2003
              {"Windows NT", "amd64", "4.0"}, // WinNT
              {"Windows NT", "x86", "4.0"}, // WinNT
              {"Windows XP", "amd64", "5.1"}, // WinXP
              {"Windows XP", "x86", "5.1"}, // WinXP
              /** General Windows version cases using version numbers in platformlabeler-1.1 */
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
      return data;
    }

    /* Check this platform is in the test data */
    String myArch = System.getProperty("os.arch");
    String myVersion = System.getProperty("os.version");
    for (Object[] testData : data) {
      if (testData[0].equals(myName)
          && testData[1].equals(myArch)
          && testData[2].equals(myVersion)) {
        return data;
      }
    }

    /* Add data for this platform, it is not already in the data and is not linux */
    Object[] myTestData = {myName, myArch, myVersion};
    List<Object[]> augmentedData = new ArrayList<>();
    augmentedData.add(myTestData);
    augmentedData.addAll(data);
    return augmentedData;
  }

  @Test
  public void testComputeLabels() throws Exception {
    PlatformDetailsTask details = new PlatformDetailsTask();
    PlatformDetails result = details.computeLabels(arch, name, version);
    assertThat(result.getArchitecture(), equalTo(expectedArch));
    assertThat(result.getName(), equalTo(expectedName));
    assertThat(result.getVersion(), equalTo(expectedVersion));
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
    return name.toLowerCase();
  }

  private String computeVersion(String version) {
    if (!name.startsWith("Windows")) {
      return version;
    }
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
    return version;
  }

  private static boolean isWindows() {
    return File.pathSeparatorChar == ';';
  }
}
