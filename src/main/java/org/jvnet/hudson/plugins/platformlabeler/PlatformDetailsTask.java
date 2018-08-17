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

import hudson.remoting.Callable;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import jenkins.security.Roles;
import net.robertcollins.lsb.Release;
import org.jenkinsci.remoting.RoleChecker;
import org.jenkinsci.remoting.RoleSensitive;

/** Platform details are identified and recorded in this class for labeling use. */
class PlatformDetailsTask implements Callable<HashSet<String>, IOException> {

  /**
   * Check roles are allowed. Required abstract method definition; we need the permission to run on
   * a slave
   *
   * @param checker role checker to be called to check SLAVE role
   * @throws SecurityException on a security error
   */
  @Override
  public void checkRoles(final RoleChecker checker) throws SecurityException {
    checker.check((RoleSensitive) this, Roles.SLAVE);
  }

  /**
   * Performs computation and returns the result, or throws some exception.
   *
   * @return label computation result
   * @throws IOException on I/O error
   */
  @Override
  public HashSet<String> call() throws IOException {
    final String arch = System.getProperty("os.arch");
    String name = System.getProperty("os.name");
    String version = System.getProperty("os.version");
    return computeLabels(arch, name, version);
  }

  /**
   * Return "x86" or "amd64 as Windows architecture depending on the values of known Windows
   * environment variables.
   *
   * @param arch initial guess of architecture
   * @return actual architecture, either "x86" or "amd64"
   */
  private String checkWindows32Bit(final String arch) {
    if (!"x86".equalsIgnoreCase(arch)) {
      return arch;
    }
    final String env1 = System.getenv("PROCESSOR_ARCHITECTURE");
    final String env2 = System.getenv("PROCESSOR_ARCHITEW6432");
    if ("amd64".equalsIgnoreCase(env1) || "amd64".equalsIgnoreCase(env2)) {
      return "amd64";
    }
    return arch;
  }

  /**
   * Return architecture "x86" or "amd64" based on information read from Linux system.
   *
   * @param arch initial guess of architecture
   * @return actual architecture, either "x86" or "amd64"
   */
  private String checkLinux32Bit(final String arch) {
    if (!"x86".equalsIgnoreCase(arch) || isWindows()) {
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
   * @throws IOException on I/O error
   * @return agent labels as a set of strings
   */
  protected HashSet<String> computeLabels(
      final String arch, final String name, final String version) throws IOException {
    String computedName = name.toLowerCase();
    String computedArch = arch;
    String computedVersion = version;
    if (computedName.startsWith("windows")) {
      computedName = "windows";
      computedArch = checkWindows32Bit(computedArch);
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
      String unknownString = "unknown+check_lsb_release_installed";
      Release release = new Release();
      computedName = release.distributorId();
      computedArch = checkLinux32Bit(computedArch);
      if (null == computedName) {
        computedName = unknownString;
      }
      computedVersion = release.release();
      if (null == computedVersion) {
        computedVersion = unknownString;
      }
      if (computedName.equals(unknownString)) {
        File alpineComputedVersion = new File("/etc/alpine-release");
        if (alpineComputedVersion.exists()) {
          computedName = "Alpine";
          List<String> lines =
              Files.readAllLines(alpineComputedVersion.toPath(), StandardCharsets.UTF_8);
          if (lines.size() > 0) {
            computedVersion = lines.get(0);
          }
        }
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

  /**
   * Returns true if this computer is running Microsoft Windows.
   *
   * @return true if this computer is running Microsoft Windows
   */
  private boolean isWindows() {
    return File.pathSeparatorChar == ';';
  }
}
