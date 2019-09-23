package org.jvnet.hudson.plugins.platformlabeler;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

public class PlatformDetailsTaskTest {

  private PlatformDetailsTask platformDetailsTask;

  @Before
  public void createPlatformDetailsTask() {
    platformDetailsTask = new PlatformDetailsTask();
  }

  @Test
  public void testCall() throws Exception {
    Set<String> details = platformDetailsTask.call();
    if (isWindows()) {
      assertThat(details, hasItems("windows"));
    } else {
      assertThat(details, not(hasItems("windows")));
    }
    assertPlatformDetails(details);
  }

  private void assertPlatformDetails(Set<String> details) {
    String osName = System.getProperty("os.name", "os.name.is.unknown");
    if (osName.toLowerCase().startsWith("linux")) {
      assertThat(details, not(hasItems("windows")));
      assertThat(details, not(hasItems("linux")));
      assertThat(details, not(hasItems("Linux")));
      assertThat(
          details,
          anyOf(
              hasItems("Alpine"),
              hasItems("Amazon"),
              hasItems("AmazonAMI"),
              hasItems("Debian"),
              hasItems("CentOS"),
              hasItems("Ubuntu")));
      // Yes, this is a dirty trick to detect the hardware architecture on some JVM's
      String expectedArch =
          System.getProperty("sun.arch.data.model", "23").equals("32") ? "x86" : "amd64";
      // Assumes tests run in JVM that matches operating system
      assertThat(details, hasItems(expectedArch));
    }
  }

  @Test
  public void testComputeLabelsLinux32Bit() throws Exception {
    Set<String> details = platformDetailsTask.computeLabels("x86", "linux", "xyzzy");
    assertPlatformDetails(details);
  }

  @Test
  public void testComputeLabelsLinuxWithoutLsbRelease() throws Exception {
    assumeTrue(!isWindows() && Files.exists(Paths.get("/etc/os-release")));
    String unknown = PlatformDetailsTask.UNKNOWN_VALUE_STRING;
    LsbRelease release = new LsbRelease(unknown, unknown);
    Set<String> details = platformDetailsTask.computeLabels("x86", "linux", "xyzzy", release);
    assertPlatformDetails(details);
  }

  @Test
  public void testComputeLabelsLinuxWithNullLsbRelease() throws Exception {
    assumeTrue(!isWindows() && Files.exists(Paths.get("/etc/os-release")));
    String unknown = PlatformDetailsTask.UNKNOWN_VALUE_STRING;
    LsbRelease release = null;
    Set<String> details = platformDetailsTask.computeLabels("x86", "linux", "xyzzy", release);
    assertPlatformDetails(details);
  }

  @Test
  public void testCheckWindows32Bit() {
    assertThat(platformDetailsTask.checkWindows32Bit("x86", "AMD64", null), is("amd64"));
  }

  @Test
  public void testCheckWindows32BitAMD64SecondArgument() {
    assertThat(platformDetailsTask.checkWindows32Bit("x86", "x86", "AMD64"), is("amd64"));
  }

  @Test
  public void compareOSName() throws Exception {
    assumeTrue(!isWindows() && Files.exists(Paths.get("/etc/os-release")));
    Set<String> details = platformDetailsTask.computeLabels("x86", "linux", "xyzzy");
    String name = platformDetailsTask.readReleaseIdentifier("ID");
    assertThat(details, hasItems(name));
  }

  @Test
  public void compareOSVersion() throws Exception {
    assumeTrue(!isWindows() && Files.exists(Paths.get("/etc/os-release")));
    Set<String> details = platformDetailsTask.computeLabels("x86", "linux", "xyzzy");
    String version = platformDetailsTask.readReleaseIdentifier("VERSION_ID");
    /* Check that the version string returned by readReleaseIdentifier
    is at least at the beginning of one of the detail values. Allow
    Debian 8, 9, and 10 and CentOS 7 to report their base version
    in the /etc/os-release file without reporting their incremental
    version */
    String foundValue = version;
    for (String detail : details) {
      if (detail.startsWith(version)) {
        foundValue = detail;
      }
    }
    /* If VERSION_ID has the unknown value then handle it as a special
      case.  Debian testing does not include a VERSION_ID value in
      the /etc/os-release file.  If there is no value for VERSION_ID,
      then confirm that the details are for Debian testing and skip
      the VERSION_ID assertion.
    */
    if (version.startsWith("unknown")) {
      assertThat(details, hasItems("Debian", "testing"));
    } else {
      assertThat(details, anyOf(hasItems(version), hasItems(foundValue)));
    }
  }

  private boolean isWindows() {
    return File.pathSeparatorChar == ';';
  }
}
