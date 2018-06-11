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
import java.util.HashSet;
import jenkins.security.Roles;
import net.robertcollins.lsb.Release;
import org.jenkinsci.remoting.RoleChecker;
import org.jenkinsci.remoting.RoleSensitive;

class PlatformDetailsTask implements Callable<HashSet<String>, IOException> {

  /** Required abstract method definition; we need the permission to run on a slave */
  @Override
  public void checkRoles(RoleChecker checker) throws SecurityException {
    checker.check((RoleSensitive) this, Roles.SLAVE);
  }

  /** Performs computation and returns the result, or throws some exception. */
  @Override
  public HashSet<String> call() throws IOException {
    final String arch = System.getProperty("os.arch");
    String name = System.getProperty("os.name");
    String version = System.getProperty("os.version");
    return computeLabels(arch, name, version);
  }

  private String checkWindows32Bit(String arch) {
    if (!"x86".equalsIgnoreCase(arch)) {
      return arch;
    }
    final String env1 = System.getenv("PROCESSOR_ARCHITECTURE");
    final String env2 = System.getenv("PROCESSOR_ARCHITEW6432");
    if ("amd64".equalsIgnoreCase(env1) || "amd64".equalsIgnoreCase(env2)) {
      arch = "amd64";
    }
    return arch;
  }

  private String checkLinux32Bit(String arch) {
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
            arch = "amd64";
          } else {
            arch = line;
          }
        }
      }
    } catch (IOException | InterruptedException e) {
    }
    return arch;
  }

  protected HashSet<String> computeLabels(String arch, String name, String version)
      throws IOException {
    name = name.toLowerCase();
    if (name.equals("solaris")) {
      name = "solaris";
    } else if (name.startsWith("windows")) {
      name = "windows";
      arch = checkWindows32Bit(arch);
      if (version.startsWith("4.0")) {
        version = "nt4";
      } else if (version.startsWith("5.0")) {
        version = "2000";
      } else if (version.startsWith("5.1")) {
        version = "xp";
      } else if (version.startsWith("5.2")) {
        version = "2003";
      }
    } else if (name.startsWith("linux")) {
      String unknown_string = "unknown+check_lsb_release_installed";
      Release release = new Release();
      name = release.distributorId();
      arch = checkLinux32Bit(arch);
      if (null == name) {
        name = unknown_string;
      }
      version = release.release();
      if (null == version) {
        version = unknown_string;
      }
    } else if (name.startsWith("mac")) {
      name = "mac";
    }
    HashSet<String> result = new HashSet<>();
    result.add(arch);
    result.add(name);
    result.add(version);
    result.add(arch + "-" + name);
    result.add(name + "-" + version);
    result.add(arch + "-" + name + "-" + version);
    return result;
  }

  private boolean isWindows() {
    return File.pathSeparatorChar == ';';
  }
}
