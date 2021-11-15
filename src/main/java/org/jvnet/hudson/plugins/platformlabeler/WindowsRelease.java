/*
 * The MIT License
 *
 * Copyright (C) 2019 Tobias Gruetzmacher, Mark Waite
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

import edu.umd.cs.findbugs.annotations.NonNull;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/** Windows release class. Provides Windows feature update, 1803, 1903, 2009, 2103, 2109, etc. */
public class WindowsRelease implements PlatformDetailsRelease {
    @NonNull private final String release;

    /** Extract distributor ID and release for current platform. */
    public WindowsRelease() {
        Map<String, String> newProps = new HashMap<>();
        try {
            Process process =
                    new ProcessBuilder(
                                    "REG",
                                    "QUERY",
                                    "HKLM\\Software\\Microsoft\\Windows NT\\CurrentVersion",
                                    "/t",
                                    "REG_SZ",
                                    "/v",
                                    "ReleaseId")
                            .start();
            readWindowsReleaseOutput(process.getInputStream(), newProps);
        } catch (IOException ignored) {
            // IGNORE
        }
        this.release =
                newProps.getOrDefault(
                        "ReleaseId", PlatformDetailsTask.UNKNOWN_WINDOWS_VALUE_STRING);
    }

    /** Read file to assign distributor ID and release. Package protected for tests. */
    WindowsRelease(File windowsReleaseFile) throws IOException {
        Map<String, String> newProps = new HashMap<>();
        try (FileInputStream stream = new FileInputStream(windowsReleaseFile)) {
            readWindowsReleaseOutput(stream, newProps);
        }
        this.release =
                newProps.getOrDefault(
                        "ReleaseId", PlatformDetailsTask.UNKNOWN_WINDOWS_VALUE_STRING);
    }

    private void readWindowsReleaseOutput(InputStream inputStream, Map<String, String> newProps)
            throws IOException {
        try (BufferedReader reader =
                new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            reader.lines()
                    .filter(s -> s.contains("REG_SZ"))
                    .map(line -> line.split("REG_SZ", 2))
                    .forEach(parts -> newProps.put(parts[0].trim(), parts[1].trim()));
        }
    }

    /**
     * Return the Windows feature update for this agent or
     * PlatformDetailsTask.UNKNOWN_WINDOWS_VALUE_STRING.
     *
     * @return Windows feature update for this agent
     */
    @NonNull
    @Override
    public String release() {
        return release;
    }

    @NonNull
    @Override
    public String distributorId() {
        return "Microsoft";
    }
}
