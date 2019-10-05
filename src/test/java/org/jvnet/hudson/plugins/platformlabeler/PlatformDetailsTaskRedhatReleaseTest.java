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
public class PlatformDetailsTaskRedhatReleaseTest {

  private final String redhatReleaseFileName;
  private final String expectedName;
  private final String expectedVersion;
  private final String expectedArch;

  public PlatformDetailsTaskRedhatReleaseTest(
      String osReleaseFileName, String expectedName, String expectedVersion, String expectedArch) {
    this.redhatReleaseFileName = osReleaseFileName;
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
    String packageName = PlatformDetailsTaskRedhatReleaseTest.class.getPackage().getName();
    Reflections reflections = new Reflections(packageName, new ResourcesScanner());
    Set<String> fileNames = reflections.getResources(Pattern.compile(".*redhat-release"));
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
    URL resource = getClass().getResource(redhatReleaseFileName);
    File redhatReleaseFile = new File(resource.toURI());
    assertTrue("File not found " + redhatReleaseFile, redhatReleaseFile.exists());
    details.setRedhatRelease(redhatReleaseFile);
    String unknown = PlatformDetailsTask.UNKNOWN_VALUE_STRING;
    LsbRelease release = new LsbRelease(unknown, unknown);
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
    if (filename.contains("rhel") || filename.contains("ubi")) {
      return "Red Hat Enterprise Linux Server";
    }
    if (filename.contains("scientific")) {
      return "Scientific Linux";
    }
    return filename.toLowerCase();
  }

  private static String computeExpectedVersion(String filename) {
    File file = new File(filename);
    File parentDir = file.getParentFile();
    return parentDir.getName();
  }
}
