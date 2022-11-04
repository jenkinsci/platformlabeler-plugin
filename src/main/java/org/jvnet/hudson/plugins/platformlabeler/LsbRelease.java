/*
 * The MIT License
 *
 * Copyright (C) 2019 Tobias Gruetzmacher
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/** Linux standard base release class. Provides distributor ID and release. */
public class LsbRelease implements PlatformDetailsRelease {
    @NonNull private final String distributorId;
    @NonNull private final String release;

    private static final Logger LOGGER = Logger.getLogger(LsbRelease.class.getName());

    /** Extract distributor ID and release for current platform. */
    public LsbRelease() {
        Map<String, String> newProps = new HashMap<>();
        try {
            Process process = new ProcessBuilder("lsb_release", "-a").start();
            readLsbReleaseOutput(process.getInputStream(), newProps);
        } catch (IOException e) {
            LOGGER.log(Level.FINEST, "lsb_release execution failed", e);
        }
        this.distributorId =
                newProps.getOrDefault("Distributor ID", PlatformDetailsTask.UNKNOWN_VALUE_STRING);
        String guessedRelease =
                newProps.getOrDefault("Release", PlatformDetailsTask.UNKNOWN_VALUE_STRING);
        if (guessedRelease.equals("n/a")) {
            guessedRelease = guessDebianRelease(guessedRelease);
        }
        this.release = guessedRelease;
    }

    /** Assign distributor ID and release. Package protected for tests. */
    LsbRelease(@NonNull String distributorId, @NonNull String release) {
        this.distributorId = distributorId;
        this.release = release;
    }

    /** Read file to assign distributor ID and release. Package protected for tests. */
    LsbRelease(@NonNull File lsbReleaseFile) throws IOException {
        Map<String, String> newProps = new HashMap<>();
        try (FileInputStream stream = new FileInputStream(lsbReleaseFile)) {
            readLsbReleaseOutput(stream, newProps);
        }
        this.distributorId =
                newProps.getOrDefault("Distributor ID", PlatformDetailsTask.UNKNOWN_VALUE_STRING);
        String guessedRelease =
                newProps.getOrDefault("Release", PlatformDetailsTask.UNKNOWN_VALUE_STRING);
        if (guessedRelease.equals("n/a")) {
            guessedRelease = guessDebianReleaseFromFile(lsbReleaseFile);
        }
        this.release = guessedRelease;
    }

    private void readLsbReleaseOutput(InputStream inputStream, Map<String, String> newProps)
            throws IOException {
        try (BufferedReader reader =
                new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            reader.lines()
                    .filter(s -> s.contains(":"))
                    .map(line -> line.split(":", 2))
                    .forEach(parts -> newProps.put(parts[0], parts[1].trim()));
        }
    }

    /*
     * In the test environment, the intended Debian version is encoded
     * in the path to the file.  If a file was provided with test
     * data, then the path of that file resolves the question whether
     * the ambiguous Debian version is testing or unstable.
     */
    private String guessDebianReleaseFromFile(@NonNull File testData) {
        String unstableDir = File.separator + "unstable" + File.separator;
        return testData.getPath().contains(unstableDir) ? "unstable" : "testing";
    }

    /*
     * Debian testing has periods during its lifecycle when the
     * lsb_release command cannot distinguish between testing and
     * unstable.  That is an accepted condition for the Debian
     * developers.
     *
     * See https://bugs.debian.org/cgi-bin/bugreport.cgi?bug=845651
     * for more details.
     *
     * When in that state, the apt-cache command can be used to
     * determine the Debian release that is running.
     *
     * The base-files package is used to query the apt-cache policy
     * because it is required for all Debian installations and because
     * it is the package that provides the /etc/os-release file that
     * is used elsewhere to determine version information in case the
     * lsb_release command is not available.
     */
    private String guessDebianRelease(@NonNull String defaultValue) {
        String value = defaultValue;
        try {
            Process process = new ProcessBuilder("apt-cache", "policy", "base-files").start();
            value = readReleaseFromAptCachePolicyOutput(process.getInputStream());
        } catch (IOException e) {
            LOGGER.log(Level.FINEST, "apt-cache execution failed", e);
        }
        return value;
    }

    /* Identifying strings in apt-cache policy output that identify
     * the unstable distribution
     */
    private List<String> unstableIdentifiers = Arrays.asList("sid", "unstable");

    /*
     * If apt-cache output contains one of the unstableIdentifiers,
     * report it as unstable.  Otherwise report it as testing.
     */
    private String readReleaseFromAptCachePolicyOutput(InputStream inputStream) throws IOException {
        String distribution = "testing";
        try (BufferedReader reader =
                new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            List<String> results =
                    reader.lines()
                            .filter(line -> unstableIdentifiers.stream().anyMatch(line::contains))
                            .collect(Collectors.toList());
            if (!results.isEmpty()) {
                distribution = "unstable";
            }
        }
        return distribution;
    }

    /**
     * Return the Linux distributor ID for this agent.
     *
     * @return Linux distributor ID for this agent
     */
    @Override
    @NonNull
    public String distributorId() {
        return distributorId;
    }

    /**
     * Return the Linux release for this agent.
     *
     * @return Linux release for this agent
     */
    @Override
    @NonNull
    public String release() {
        return release;
    }
}
