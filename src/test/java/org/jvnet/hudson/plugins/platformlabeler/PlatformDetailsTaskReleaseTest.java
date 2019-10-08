package org.jvnet.hudson.plugins.platformlabeler;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Pattern;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

@RunWith(Parameterized.class)
public class PlatformDetailsTaskReleaseTest {

  private final String releaseFileName;
  private final String expectedName;
  private final String expectedVersion;
  private final String expectedArch;

  public PlatformDetailsTaskReleaseTest(
      String releaseFileName, String expectedName, String expectedVersion, String expectedArch) {
    this.releaseFileName = releaseFileName;
    this.expectedName = expectedName;
    this.expectedVersion = expectedVersion;
    this.expectedArch = expectedArch;
  }

  /**
   * Generate test parameters for Linux os-release and redhat-release sample files stored as
   * resources.
   *
   * @return parameter values to be tested
   */
  @Parameters(name = "{1}-{2}-{3}-{0}")
  public static Collection<Object[]> generateReleaseFileNames() {
    String packageName = PlatformDetailsTaskReleaseTest.class.getPackage().getName();
    Reflections reflections = new Reflections(packageName, new ResourcesScanner());
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
    return data;
  }

  @Test
  public void testComputeLabelsForRelease() throws Exception {
    PlatformDetailsTask details = new PlatformDetailsTask();
    URL resource = getClass().getResource(releaseFileName);
    File releaseFile = new File(resource.toURI());
    assertTrue("File not found " + releaseFile, releaseFile.exists());
    if (releaseFile.getName().startsWith("redhat")) {
      details.setOsReleaseFile(null);
      details.setRedhatRelease(releaseFile);
    } else {
      details.setOsReleaseFile(releaseFile);
      details.setRedhatRelease(null);
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
    if (filename.contains("amzn")) {
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
    if (filename.contains("oraclelinux")) {
      return "OracleServer";
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
    return filename.toLowerCase();
  }

  private static String computeExpectedVersion(String filename) {
    File file = new File(filename);
    File parentDir = file.getParentFile();
    return parentDir.getName();
  }
}
