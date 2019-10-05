/*
 * The MIT License
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., Kohsuke Kawaguchi, Stephen Connolly
 * Copyright (C) 2009 Robert Collins
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.jvnet.hudson.plugins.platformlabeler;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.remoting.Callable;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import jenkins.security.Roles;
import org.jenkinsci.remoting.RoleChecker;
import org.jenkinsci.remoting.RoleSensitive;

/** Compute labels based on details computed on the agent. */
class PlatformDetailsTask implements Callable<HashSet<String>, IOException> {

  private static final String RELEASE = "release";
  /** Unknown field value string. Package protected for us by LsbRelease class */
  static final String UNKNOWN_VALUE_STRING = "unknown+check_lsb_release_installed";

  /**
   * Checks that required SLAVE role is allowed.
   *
   * @param checker role checker to be called to check SLAVE role
   * @throws SecurityException on a security error
   */
  @Override
  public void checkRoles(final RoleChecker checker) throws SecurityException {
    checker.check((RoleSensitive) this, Roles.SLAVE);
  }

  /**
   * Performs label computation and returns the result as a HashSet.
   *
   * @return label computation result
   * @throws IOException on I/O error
   */
  @Override
  public HashSet<String> call() throws IOException {
    final String arch = System.getProperty("os.arch", UNKNOWN_VALUE_STRING);
    final String name = System.getProperty("os.name", UNKNOWN_VALUE_STRING);
    final String version = System.getProperty("os.version", UNKNOWN_VALUE_STRING);
    return computeLabels(arch, name, version);
  }

  /**
   * Returns standardized architecture of current Windows operating system, adapted for those cases
   * where a 64 bit machine may be running a 32 bit Java virtual machine. Returns "amd64" if the
   * processor environment variables report this is an AMD 64 architecture (modern Intel and AMD
   * processors).
   *
   * @param arch architecture of the agent, as in "x86", "amd64", or "aarch64"
   * @return standardized architecture of current Windows operating system
   */
  @NonNull
  protected String checkWindows32Bit(
      @NonNull final String arch, @NonNull final String env1, @NonNull final String env2) {
    if (!"x86".equalsIgnoreCase(arch)) {
      return arch;
    }
    if ("amd64".equalsIgnoreCase(env1) || "amd64".equalsIgnoreCase(env2)) {
      return "amd64";
    }
    return arch;
  }

  /**
   * Returns standardized architecture of current Linux operating system, adapted for those cases
   * where a 64 bit machine may identify architecture in different ways. Returns "amd64" if the
   * 'uname -m' output is "x86_64".
   *
   * @param arch architecture of the agent, as in "x86", "amd64", or "aarch64"
   * @return standardized architecture of current Linux operating system
   */
  @NonNull
  private String checkLinux32Bit(@NonNull final String arch) {
    if (!"x86".equalsIgnoreCase(arch)) {
      return arch;
    }
    try {
      Process p = Runtime.getRuntime().exec("/bin/uname -m");
      p.waitFor();
      try (BufferedReader b =
          new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"))) {
        String line = b.readLine();
        if (line != null) {
          if ("x86_64".equals(line)) {
            return "amd64";
          } else {
            return line;
          }
        }
      }
    } catch (IOException | InterruptedException e) {
      /* Return arch instead of throwing an exception */
    }
    return arch;
  }

  /**
   * Compute agent labels based on seed values provided as parameters.
   *
   * @param arch architecture of the agent, as in "x86", "amd64", or "aarch64"
   * @param name name of the operating system or distribution as in "OpenBSD", "FreeBSD", "Windows",
   *     or "Linux"
   * @param version version of the operating system
   * @return agent labels as a set of strings
   * @throws IOException on I/O error
   */
  @NonNull
  protected HashSet<String> computeLabels(
      @NonNull final String arch, @NonNull final String name, @NonNull final String version)
      throws IOException {
    LsbRelease release;
    if (name.toLowerCase().startsWith("linux")) {
      release = new LsbRelease();
    } else {
      release = null;
    }
    return computeLabels(arch, name, version, release);
  }

  /**
   * Compute agent labels based on seed values provided as parameters.
   *
   * @param arch architecture of the agent, as in "x86", "amd64", or "aarch64"
   * @param name name of the operating system or distribution as in "OpenBSD", "FreeBSD", "Windows",
   *     or "Linux"
   * @param version version of the operating system
   * @param release Linux standard base release data (name, distributorId, version, etc.)
   * @return agent labels as a set of strings
   * @throws IOException on I/O error
   */
  @NonNull
  protected HashSet<String> computeLabels(
      @NonNull final String arch,
      @NonNull final String name,
      @NonNull final String version,
      @CheckForNull LsbRelease release)
      throws IOException {
    String computedName = name.toLowerCase();
    String computedArch = arch;
    String computedVersion = version;
    if (computedName.startsWith("windows")) {
      computedName = "windows";
      computedArch =
          checkWindows32Bit(
              computedArch,
              System.getenv("PROCESSOR_ARCHITECTURE"),
              System.getenv("PROCESSOR_ARCHITEW6432"));
      if (computedVersion.startsWith("4.0")) {
        computedVersion = "nt4";
      } else if (computedVersion.startsWith("5.0")) {
        computedVersion = "2000";
      } else if (computedVersion.startsWith("5.1")) {
        computedVersion = "xp";
      } else if (computedVersion.startsWith("5.2")) {
        computedVersion = "2003";
      }
    } else if (computedName.startsWith("linux")) {
      if (release == null) {
        release = new LsbRelease();
      }
      computedName = release.distributorId();
      computedArch = checkLinux32Bit(computedArch);
      computedVersion = release.release();
      /* Fallback to /etc/os-release file */
      if (computedName.equals(UNKNOWN_VALUE_STRING)) {
        computedName = readReleaseIdentifier("ID");
      }
      if (computedVersion.equals(UNKNOWN_VALUE_STRING)) {
        computedVersion = readReleaseIdentifier("VERSION_ID");
      }
      if (computedVersion.equals(UNKNOWN_VALUE_STRING)) {
        computedVersion = readReleaseIdentifier("BUILD_ID");
      }
      if (computedName.equals(UNKNOWN_VALUE_STRING)) {
        computedName = readRedhatReleaseIdentifier("ID");
      }
      if (computedVersion.equals(UNKNOWN_VALUE_STRING)) {
        computedVersion = readRedhatReleaseIdentifier("VERSION_ID");
      }
    } else if (computedName.startsWith("mac")) {
      computedName = "mac";
    }
    HashSet<String> result = new HashSet<>();
    result.add(computedArch);
    result.add(computedName);
    result.add(computedVersion);
    result.add(computedArch + "-" + computedName);
    result.add(computedName + "-" + computedVersion);
    result.add(computedArch + "-" + computedName + "-" + computedVersion);
    return result;
  }

  private static final Map<String, String> PREFERRED_LINUX_OS_NAMES = new HashMap<>();

  static {
    PREFERRED_LINUX_OS_NAMES.put("alpine", "Alpine");
    PREFERRED_LINUX_OS_NAMES.put("amzn", "Amazon");
    PREFERRED_LINUX_OS_NAMES.put("centos", "CentOS");
    PREFERRED_LINUX_OS_NAMES.put("debian", "Debian");
    PREFERRED_LINUX_OS_NAMES.put("ol", "OracleServer");
    PREFERRED_LINUX_OS_NAMES.put("opensuse", "openSUSE");
    PREFERRED_LINUX_OS_NAMES.put("ubuntu", "Ubuntu");
    PREFERRED_LINUX_OS_NAMES.put("scientific", "Scientific");
  }

  private File osRelease = new File("/etc/os-release");
  private File redhatRelease = new File("/etc/redhat-release");

  /* Package protected for use in tests */
  void setOsReleaseFile(File osRelease) {
    this.osRelease = osRelease;
  }

  void setRedhatRelease(File redhatRelease) {
    this.redhatRelease = redhatRelease;
  }

  /* Package protected for use in tests */
  @NonNull
  String readReleaseIdentifier(@NonNull String field) {
    String value = UNKNOWN_VALUE_STRING;
    try (BufferedReader br =
        new BufferedReader(Files.newBufferedReader(osRelease.toPath(), StandardCharsets.UTF_8))) {
      String line;
      while ((line = br.readLine()) != null) {
        if (line.startsWith(field + "=")) {
          String[] parts = line.split("=");
          value = parts[1].replace("\"", "").trim();
        }
      }
    } catch (IOException notFound) {
      // Ignore IOException
    }
    return PREFERRED_LINUX_OS_NAMES.getOrDefault(value, value);
  }

  /* Package protected for use in tests */
  @NonNull
  String readRedhatReleaseIdentifier(@NonNull String field) {
    String value = UNKNOWN_VALUE_STRING;
    try (BufferedReader br =
        new BufferedReader(
            Files.newBufferedReader(redhatRelease.toPath(), StandardCharsets.UTF_8))) {
      String line;
      while ((line = br.readLine()) != null) {
        if (line.contains(RELEASE)) {
          if (field.equals("ID")) {
            value = line.substring(0, line.indexOf(RELEASE)).trim();
            System.out.println(value);
          }
          if (field.equals("VERSION_ID"))
            value =
                line.substring(line.indexOf(RELEASE) + RELEASE.length(), line.indexOf("(")).trim();
        }
      }
    } catch (IOException notFound) {
      // Ignore IOException
    }
    return PREFERRED_LINUX_OS_NAMES.getOrDefault(value, value);
  }
}
