package org.jvnet.hudson.plugins.platformlabeler;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PlatformDetailsTaskTest {

  private PlatformDetailsTask platformDetailsTask;

  private static final String SYSTEM_OS_ARCH =
      System.getProperty("os.arch", PlatformDetailsTask.UNKNOWN_VALUE_STRING);
  private static final String SYSTEM_OS_NAME =
      System.getProperty("os.name", PlatformDetailsTask.UNKNOWN_VALUE_STRING);
  /**
   * SPECIAL_CASE_ARCH is used to test x86 detection when running on 64 bit Intel processors. When
   * running on non-Intel processors, use the system architecture reported by Java.
   */
  private static final String SPECIAL_CASE_ARCH =
      SYSTEM_OS_ARCH.contains("amd") ? "x86" : SYSTEM_OS_ARCH;

  @BeforeEach
  public void createPlatformDetailsTask() {
    platformDetailsTask = new PlatformDetailsTask();
  }

  @Test
  @DisplayName("test remote call for platform details")
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
    String osName = SYSTEM_OS_NAME;
    assertThat(osName, is(not(PlatformDetailsTask.UNKNOWN_VALUE_STRING)));
    if (osName.toLowerCase().startsWith("linux")) {
      String name = details.getName();
      assertThat(name, is(not("windows")));
      assertThat(name, is(not("linux")));
      assertThat(name, is(not("Linux")));
      assertThat(name, is(not(PlatformDetailsTask.UNKNOWN_VALUE_STRING)));
      assertThat(
          name,
          anyOf(
              is("Alpine"),
              is("Amazon"),
              is("AmazonAMI"),
              is("CentOS"),
              is("Debian"),
              is("Raspbian"),
              is("Ubuntu")));
      // Yes, this is a dirty trick to detect the hardware architecture on some JVM's
      String expectedArch = SYSTEM_OS_ARCH;
      if (expectedArch.equals("amd64")) {
        expectedArch =
            System.getProperty("sun.arch.data.model", "23").equals("32") ? "x86" : "amd64";
      }
      // Assumes tests run in JVM that matches operating system
      assertThat(details.getArchitecture(), is(expectedArch));
    }
  }

  @Test
  @DisplayName("test 32 bit Linux label computation")
  public void testComputeLabelsLinux32Bit() throws Exception {
    PlatformDetails details =
        platformDetailsTask.computeLabels(SPECIAL_CASE_ARCH, "linux", "xyzzy");
    assertPlatformDetails(details);
  }

  @Test
  @DisplayName("test Linux32Bit stream")
  public void testLinux32BitStream() throws IOException {
    String unameOutput = "x86_64";
    InputStream stream = new ByteArrayInputStream(unameOutput.getBytes(StandardCharsets.UTF_8));
    assertThat(platformDetailsTask.checkLinux32BitStream(stream, SPECIAL_CASE_ARCH), is("amd64"));
  }

  @Test
  @DisplayName("test Linux32Bit stream ARM")
  public void testLinux32BitStreamARM() throws IOException {
    String unameOutput = "aarch64";
    InputStream stream = new ByteArrayInputStream(unameOutput.getBytes(StandardCharsets.UTF_8));
    assertThat(
        platformDetailsTask.checkLinux32BitStream(stream, SPECIAL_CASE_ARCH), is(unameOutput));
  }

  @Test
  @DisplayName("test Linux32Bit stream empty")
  public void testLinux32BitStreamEmpty() throws IOException {
    String unameOutput = "";
    String expectedArch = "Expected-Arch";
    InputStream stream = new ByteArrayInputStream(unameOutput.getBytes(StandardCharsets.UTF_8));
    assertThat(platformDetailsTask.checkLinux32BitStream(stream, expectedArch), is(expectedArch));
  }

  @Test
  @DisplayName("test Linux label computation without lsb_release")
  public void testComputeLabelsLinuxWithoutLsbRelease() throws Exception {
    assumeTrue(!isWindows() && Files.exists(Paths.get("/etc/os-release")));
    String unknown = PlatformDetailsTask.UNKNOWN_VALUE_STRING;
    LsbRelease release = new LsbRelease(unknown, unknown);
    PlatformDetails details =
        platformDetailsTask.computeLabels(SPECIAL_CASE_ARCH, "linux", "xyzzy", release);
    assertPlatformDetails(details);
  }

  @Test
  @DisplayName("test Linux label computation with null lsb_release")
  public void testComputeLabelsLinuxWithNullLsbRelease() throws Exception {
    assumeTrue(!isWindows() && Files.exists(Paths.get("/etc/os-release")));
    LsbRelease release = null;
    PlatformDetails details =
        platformDetailsTask.computeLabels(SPECIAL_CASE_ARCH, "linux", "xyzzy", release);
    assertPlatformDetails(details);
  }

  @Test
  @DisplayName("test Windows 32 bit")
  public void testCheckWindows32Bit() {
    /* Always testing this case, no SPECIAL_CASE_ARCH needed */
    assertThat(platformDetailsTask.checkWindows32Bit("x86", "AMD64", ""), is("amd64"));
  }

  @Test
  @DisplayName("test Windows 32 bit with amd64")
  public void testCheckWindows32BitAMD64SecondArgument() {
    /* Always testing this case, no SPECIAL_CASE_ARCH needed */
    assertThat(platformDetailsTask.checkWindows32Bit("x86", "x86", "AMD64"), is("amd64"));
  }

  @Test
  @DisplayName("test operating system name")
  public void compareOSName() throws Exception {
    assumeTrue(!isWindows() && Files.exists(Paths.get("/etc/os-release")));
    String computedName =
        platformDetailsTask.computeLabels(SPECIAL_CASE_ARCH, "linux", "xyzzy").getName();
    String readName = platformDetailsTask.readReleaseIdentifier("ID");
    assertThat(computedName, is(readName));
  }

  @Test
  @DisplayName("test release identifier on missing file")
  public void readReleaseIdentifierMissingFileReturnsUnknownValue() throws Exception {
    PlatformDetails details =
        platformDetailsTask.computeLabels(SPECIAL_CASE_ARCH, "linux", "xyzzy");
    platformDetailsTask.setOsReleaseFile(new File("/this/file/does/not/exist"));
    String name = platformDetailsTask.readReleaseIdentifier("ID");
    assertThat(name, is(PlatformDetailsTask.UNKNOWN_VALUE_STRING));
  }

  @Test
  @DisplayName("Read Red Hat release identifier")
  public void readRedhatReleaseIdentifierMissingFileReturnsUnknownValue() throws Exception {
    PlatformDetails details =
        platformDetailsTask.computeLabels(SPECIAL_CASE_ARCH, "linux", "xyzzy");
    platformDetailsTask.setRedhatRelease(new File("/this/file/does/not/exist"));
    String name = platformDetailsTask.readRedhatReleaseIdentifier("ID");
    assertThat(name, is(PlatformDetailsTask.UNKNOWN_VALUE_STRING));
  }

  @Test
  @DisplayName("Read Red Hat release identifier null file")
  public void readRedhatReleaseIdentifierNullFileReturnsUnknownValue() throws Exception {
    PlatformDetails details =
        platformDetailsTask.computeLabels(SPECIAL_CASE_ARCH, "linux", "xyzzy");
    platformDetailsTask.setRedhatRelease(null);
    String name = platformDetailsTask.readRedhatReleaseIdentifier("ID");
    assertThat(name, is(PlatformDetailsTask.UNKNOWN_VALUE_STRING));
  }

  @Test
  @DisplayName("Read Red Hat release identifier wrong file")
  public void readRedhatReleaseIdentifierWrongFileReturnsUnknownValue() throws Exception {
    PlatformDetails details =
        platformDetailsTask.computeLabels(SPECIAL_CASE_ARCH, "linux", "xyzzy");
    platformDetailsTask.setRedhatRelease(new File("/etc/hosts")); // Not redhat-release file
    String name = platformDetailsTask.readRedhatReleaseIdentifier("ID");
    assertThat(name, is(PlatformDetailsTask.UNKNOWN_VALUE_STRING));
  }

  @Test
  @DisplayName("Read SUSE release identifier missing file")
  public void readSuseReleaseIdentifierMissingFileReturnsUnknownValue() throws Exception {
    platformDetailsTask.setSuseRelease(new File("/this/file/does/not/exist"));
    String name = platformDetailsTask.readSuseReleaseIdentifier("ID");
    assertThat(name, is(PlatformDetailsTask.UNKNOWN_VALUE_STRING));
  }

  @Test
  @DisplayName("Read SUSE release identifier null file")
  public void readSuseReleaseIdentifierNullFileReturnsUnknownValue() throws Exception {
    platformDetailsTask.setSuseRelease(null);
    String name = platformDetailsTask.readSuseReleaseIdentifier("ID");
    assertThat(name, is(PlatformDetailsTask.UNKNOWN_VALUE_STRING));
  }

  @Test
  @DisplayName("Read SUSE release identifier wrong file")
  public void readSuseReleaseIdentifierWrongFileReturnsUnknownValue() throws Exception {
    assumeTrue(!isWindows() && Files.exists(Paths.get("/etc/hosts")));
    platformDetailsTask.setSuseRelease(new File("/etc/hosts")); // Not SuSE-release file
    String name = platformDetailsTask.readSuseReleaseIdentifier("ID");
    assertThat(name, is(PlatformDetailsTask.UNKNOWN_VALUE_STRING));
  }

  @Test
  @DisplayName("Compare operating system version")
  public void compareOSVersion() throws Exception {
    assumeTrue(!isWindows() && Files.exists(Paths.get("/etc/os-release")));
    PlatformDetails details =
        platformDetailsTask.computeLabels(SPECIAL_CASE_ARCH, "linux", "xyzzy");
    String version = platformDetailsTask.readReleaseIdentifier("VERSION_ID");
    /* Check that the version string returned by readReleaseIdentifier
     * is at least at the beginning of one of the detail values. Allow
     * Debian 8, 9, and 10 and CentOS 7 to report their base version
     * in the /etc/os-release file without reporting their incremental
     * version
     */
    String foundValue = version;
    if (details.getVersion().startsWith(version)) {
      foundValue = details.getVersion();
    }
    /* If VERSION_ID has the unknown value then handle it as a special
     * case.  Debian testing does not include a VERSION_ID value in
     * the /etc/os-release file.  If there is no value for VERSION_ID,
     * then confirm that the details are for Debian testing and skip
     * the VERSION_ID assertion.
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
