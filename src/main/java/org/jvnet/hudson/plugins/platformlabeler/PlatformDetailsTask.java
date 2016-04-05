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

import net.robertcollins.lsb.Release;

import hudson.remoting.Callable;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;

class PlatformDetailsTask implements Callable<HashSet<String>, IOException> {

    /** Performs computation and returns the result, or throws some exception. */
    public HashSet<String> call() throws IOException {
        String arch = System.getProperty("os.arch").toLowerCase();
        String name = System.getProperty("os.name").toLowerCase();
        String version = System.getProperty("os.version");
        if (name.equals("solaris") || name.equals("SunOS")) {
            name = "solaris";
        } else if (name.startsWith("windows")) {
            name = "windows";
            /** os.arch is really the JRE bitness, not the OS bitness. */
            if ("x86".equals(arch)) {
                final String env1 =
                    System.getenv("PROCESSOR_ARCHITECTURE").toLowerCase();
                String env2 = null;
                try {
                    env2 = System.getenv("PROCESSOR_ARCHITEW6432").toLowerCase();
                } catch (Exception e) {}

                if ("amd64".equals(env1) || "amd64".equals(env2)) {
                    arch = "amd64";
                }
            }
            if (name.startsWith("windows 9")) {
                if (version.startsWith("4.0")) {
                    version = "95";
                } else if (version.startsWith("4.9")) {
                    version = "me";
                } else {
                    assert version.startsWith("4.1");
                    version = "98";
                }
            } else {
                if (version.startsWith("4.0")) {
                    version = "nt4";
                } else if (version.startsWith("5.0")) {
                    version = "2000";
                } else if (version.startsWith("5.1")) {
                    version = "xp";
                } else if (version.startsWith("5.2")) {
                    if ("amd64".equals(arch)) {
                        // The 64-bit version of xp is based on 2003
                        version = "2003+xp";
                    } else {
                        version = "2003";
                    }
                } else if (version.startsWith("6.0.6000")) {
                    version = "vista";
                } else if (version.startsWith("6.0")) {
                    // Server 2008 is based on 6.0.6001
                    version = "vista+2008";
                } else if (version.startsWith("6.1")) {
                    if ("x86".equals(arch)) {
                        // 2008 R2 is 64-bit only.
                        version = "7";
                    } else {
                        // TODO distinguish windows 7amd64 from 2008R2?
                        version = "7+2008r2";
                    }
                }
            }
        } else if (name.startsWith("linux")) {
            String unknown_string = "unknown+check_lsb_release_installed";
            Release release = new Release();
            name = release.distributorId();
            if (null == name)
                name = unknown_string;
            version = release.release();
            if (null == version)
                version = unknown_string;

            if ("x86".equals(arch)) {
                Process p = Runtime.getRuntime().exec("/bin/uname -m");
                try {
                    p.waitFor();
                    BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line = b.readLine();
                    if (line != null) {
                        if ("x86_64".equals(line)) {
                            arch = "amd64";
                        } else {
                            arch = line;
                        }
                    }
                }
                catch (InterruptedException e) {}
            }
        } else if (name.startsWith("mac")) {
            name = "mac";
        } else {
                // Take the System.properties values verbatim.
        }
        HashSet<String> result = new HashSet<String>();
        result.add(arch);
        result.add(name);
        result.add(version);
        result.add(arch + "-" + name);
        result.add(name + "-" + version);
        result.add(arch + "-" + name + "-" + version);
        return result;
    }
}
