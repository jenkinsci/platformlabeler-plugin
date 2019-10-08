package org.jvnet.hudson.plugins.platformlabeler;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    PlatformDetails details = platformDetailsTask.call();
    if (isWindows()) {
      assertThat(details.getName(), is("windows"));
    } else {
      assertThat(details.getName(), is(not("windows")));
    }
    assertPlatformDetails(details);
  }

  private void assertPlatformDetails(PlatformDetails details) {
    String osName = System.getProperty("os.name", "os.name.is.unknown");
    if (osName.toLowerCase().startsWith("linux")) {
      String name = details.getName();
      assertThat(name, is(not("windows")));
      assertThat(name, is(not("linux")));
      assertThat(name, is(not("Linux")));
      assertThat(
          name,
          anyOf(
              is("Alpine"),
              is("Amazon"),
              is("AmazonAMI"),
              is("Debian"),
              is("CentOS"),
              is("Ubuntu")));
      // Yes, this is a dirty trick to detect the hardware architecture on some JVM's
      String expectedArch =
          System.getProperty("sun.arch.data.model", "23").equals("32") ? "x86" : "amd64";
      // Assumes tests run in JVM that matches operating system
      assertThat(details.getArchitecture(), is(expectedArch));
    }
  }

  @Test
  public void testComputeLabelsLinux32Bit() throws Exception {
    PlatformDetails details = platformDetailsTask.computeLabels("x86", "linux", "xyzzy");
    assertPlatformDetails(details);
  }

  @Test
  public void testComputeLabelsLinuxWithoutLsbRelease() throws Exception {
    assumeTrue(!isWindows() && Files.exists(Paths.get("/etc/os-release")));
    String unknown = PlatformDetailsTask.UNKNOWN_VALUE_STRING;
    LsbRelease release = new LsbRelease(unknown, unknown);
    PlatformDetails details = platformDetailsTask.computeLabels("x86", "linux", "xyzzy", release);
    assertPlatformDetails(details);
  }

  @Test
  public void testComputeLabelsLinuxWithNullLsbRelease() throws Exception {
    assumeTrue(!isWindows() && Files.exists(Paths.get("/etc/os-release")));
    LsbRelease release = null;
    PlatformDetails details = platformDetailsTask.computeLabels("x86", "linux", "xyzzy", release);
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
    String details = platformDetailsTask.computeLabels("x86", "linux", "xyzzy").getName();
    String name = platformDetailsTask.readReleaseIdentifier("ID");
    assertThat(details, is(name));
  }

  @Test
  public void readReleaseIdentifierMissingFileReturnsUnknownValue() throws Exception {
    PlatformDetails details = platformDetailsTask.computeLabels("x86", "linux", "xyzzy");
    platformDetailsTask.setOsReleaseFile(new File("/this/file/does/not/exist"));
    String name = platformDetailsTask.readReleaseIdentifier("ID");
    assertThat(name, is(PlatformDetailsTask.UNKNOWN_VALUE_STRING));
  }

  @Test
  public void readRedhatReleaseIdentifierMissingFileReturnsUnknownValue() throws Exception {
    PlatformDetails details = platformDetailsTask.computeLabels("x86", "linux", "xyzzy");
    platformDetailsTask.setRedhatRelease(new File("/this/file/does/not/exist"));
    String name = platformDetailsTask.readRedhatReleaseIdentifier("ID");
    assertThat(name, is(PlatformDetailsTask.UNKNOWN_VALUE_STRING));
  }

  @Test
  public void readRedhatReleaseIdentifierNullFileReturnsUnknownValue() throws Exception {
    PlatformDetails details = platformDetailsTask.computeLabels("x86", "linux", "xyzzy");
    platformDetailsTask.setRedhatRelease(null);
    String name = platformDetailsTask.readRedhatReleaseIdentifier("ID");
    assertThat(name, is(PlatformDetailsTask.UNKNOWN_VALUE_STRING));
  }

  @Test
  public void readRedhatReleaseIdentifierWrongFileReturnsUnknownValue() throws Exception {
    PlatformDetails details = platformDetailsTask.computeLabels("x86", "linux", "xyzzy");
    platformDetailsTask.setRedhatRelease(new File("/etc/hosts")); // Not redhat-release file
    String name = platformDetailsTask.readRedhatReleaseIdentifier("ID");
    assertThat(name, is(PlatformDetailsTask.UNKNOWN_VALUE_STRING));
  }

  @Test
  public void compareOSVersion() throws Exception {
    assumeTrue(!isWindows() && Files.exists(Paths.get("/etc/os-release")));
    PlatformDetails details = platformDetailsTask.computeLabels("x86", "linux", "xyzzy");
    String version = platformDetailsTask.readReleaseIdentifier("VERSION_ID");
    /* Check that the version string returned by readReleaseIdentifier
    is at least at the beginning of one of the detail values. Allow
    Debian 8, 9, and 10 and CentOS 7 to report their base version
    in the /etc/os-release file without reporting their incremental
    version */
    String foundValue = version;
    if (details.getVersion().startsWith(version)) {
      foundValue = details.getVersion();
    }
    /* If VERSION_ID has the unknown value then handle it as a special
      case.  Debian testing does not include a VERSION_ID value in
      the /etc/os-release file.  If there is no value for VERSION_ID,
      then confirm that the details are for Debian testing and skip
      the VERSION_ID assertion.
    */
    if (version.startsWith("unknown")) {
      assertThat(details.getName(), is("Debian"));
      assertThat(details.getVersion(), is("testing"));
    } else {
      assertThat(details.getVersion(), anyOf(is(version), is(foundValue)));
    }
  }

  private boolean isWindows() {
    return File.pathSeparatorChar == ';';
  }
}
