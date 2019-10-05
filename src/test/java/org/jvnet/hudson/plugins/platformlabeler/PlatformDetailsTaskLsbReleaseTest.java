package org.jvnet.hudson.plugins.platformlabeler;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

@RunWith(Parameterized.class)
public class PlatformDetailsTaskLsbReleaseTest {

  private final String lsbReleaseFileName;
  private final String expectedName;
  private final String expectedVersion;
  private final String expectedArch;

  public PlatformDetailsTaskLsbReleaseTest(
      String lsbReleaseFileName, String expectedName, String expectedVersion, String expectedArch) {
    this.lsbReleaseFileName = lsbReleaseFileName;
    this.expectedName = expectedName;
    this.expectedVersion = expectedVersion;
    this.expectedArch = expectedArch;
  }

  /**
   * Generate test parameters for Linux os-release sample files stored as resources.
   *
   * @return parameter values to be tested
   */
  @Parameters(name = "{1}-{2}-{3}")
  public static Collection<Object[]> generateReleaseFileNames() {
    String packageName = PlatformDetailsTaskLsbReleaseTest.class.getPackage().getName();
    Reflections reflections = new Reflections(packageName, new ResourcesScanner());
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
    return data;
  }

  @Test
  public void testComputeLabelsForOsRelease() throws Exception {
    PlatformDetailsTask details = new PlatformDetailsTask();
    URL resource = getClass().getResource(lsbReleaseFileName);
    File lsbReleaseFile = new File(resource.toURI());
    assertTrue("File not found " + lsbReleaseFile, lsbReleaseFile.exists());
    LsbRelease release = new LsbRelease(lsbReleaseFile);
    HashSet<String> result = details.computeLabels("amd64", "linux", "xyzzy-abc", release);
    assertThat(
        result,
        containsInAnyOrder(
            expectedArch,
            expectedName,
            expectedVersion,
            expectedArch + "-" + expectedName,
            expectedName + "-" + expectedVersion,
            expectedArch + "-" + expectedName + "-" + expectedVersion));
  }

  private static String computeExpectedName(String filename) {
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
    if (filename.contains("oraclelinux")) {
      return "OracleServer";
    }
    if (filename.contains("rhel") || filename.contains("ubi")) {
      return "rhel";
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
