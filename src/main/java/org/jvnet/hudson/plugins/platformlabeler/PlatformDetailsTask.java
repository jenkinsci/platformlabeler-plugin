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
import java.util.HashSet;

class PlatformDetailsTask implements Callable<HashSet<String>, IOException> {

    /** Performs computation and returns the result, or throws some exception. */
    public HashSet<String> call() throws IOException {
        final String arch = System.getProperty("os.arch");
        String name = System.getProperty("os.name").toLowerCase();
        String version = System.getProperty("os.version");
        if (name.equals("solaris") || name.equals("SunOS")) {
            name = "solaris";
        } else if (name.startsWith("windows")) {
            name = "windows";
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
                    version = "2003";
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
