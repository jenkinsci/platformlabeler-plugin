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
    @NonNull
    private final String distributorId;

    @NonNull
    private final String release;

    private static final Logger LOGGER = Logger.getLogger(LsbRelease.class.getName());

    /** Extract distributor ID and release for current platform. */
    public LsbRelease() {
        Map<String, String> newProps = new HashMap<>();
        try {
            Process process = new ProcessBuilder("lsb_release", "-a").start();
            try (InputStream stream = process.getInputStream()) {
                readLsbReleaseOutput(stream, newProps);
            }
        } catch (IOException e) {
            LOGGER.log(Level.FINEST, "lsb_release execution failed", e);
        }
        this.distributorId = newProps.getOrDefault("Distributor ID", PlatformDetailsTask.UNKNOWN_VALUE_STRING);
        String guessedRelease = newProps.getOrDefault("Release", PlatformDetailsTask.UNKNOWN_VALUE_STRING);
        if (this.distributorId.equals("Debian")) {
            /* Check apt-cache policy in case the Debian distribution is testing or unstable. */
            String aptCacheRelease = readAptCachePolicy(guessedRelease);
            if (guessedRelease.equals("n/a") || !aptCacheRelease.equals(PlatformDetailsTask.UNKNOWN_VALUE_STRING)) {
                guessedRelease = aptCacheRelease;
            }
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
        this.distributorId = newProps.getOrDefault("Distributor ID", PlatformDetailsTask.UNKNOWN_VALUE_STRING);
        String guessedRelease = newProps.getOrDefault("Release", PlatformDetailsTask.UNKNOWN_VALUE_STRING);
        if (this.distributorId.equals("Debian")) {
            /* Check apt-cache policy in case the Debian distribution is testing or unstable. */
            String aptCacheRelease = readAptCachePolicy(lsbReleaseFile);
            if (guessedRelease.equals("n/a") || !aptCacheRelease.equals(PlatformDetailsTask.UNKNOWN_VALUE_STRING)) {
                guessedRelease = aptCacheRelease;
            }
        }
        this.release = guessedRelease;
    }

    private void readLsbReleaseOutput(InputStream inputStream, Map<String, String> newProps) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
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
    private String readAptCachePolicy(@NonNull File testData) {
        String unstableDir = File.separator + "unstable" + File.separator;
        String testingDir = File.separator + "testing" + File.separator;
        if (testData.getPath().contains(unstableDir)) {
            return "unstable";
        }
        if (testData.getPath().contains(testingDir)) {
            return "testing";
        }
        return PlatformDetailsTask.UNKNOWN_VALUE_STRING;
    }

    /*
     * Return Debian codename "unstable" or "testing" if apt-cache
     * policy indicates the distribution is one of those two
     * codenames, otherwise return UNKNOWN_VALUE_STRING.
     *
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
    private String readAptCachePolicy(@NonNull String defaultValue) {
        String value = defaultValue;
        try {
            Process process = new ProcessBuilder("apt-cache", "policy", "base-files").start();
            try (InputStream stream = process.getInputStream()) {
                value = readReleaseFromAptCachePolicyOutput(stream);
            }
        } catch (IOException e) {
            LOGGER.log(Level.FINEST, "apt-cache execution failed", e);
        }
        return value;
    }

    /* Identifying strings in apt-cache policy output for the unstable
     * or testing distributions.
     */
    private static final String APT_CACHE_POLICY_SID = " sid/";
    private static final String APT_CACHE_POLICY_TESTING = " testing/";
    private static final String APT_CACHE_POLICY_UNSTABLE = " unstable/";
    private List<String> aptCacheIdentifiers =
            Arrays.asList(APT_CACHE_POLICY_SID, APT_CACHE_POLICY_TESTING, APT_CACHE_POLICY_UNSTABLE);

    /*
     * If apt-cache output contains an aptCacheIdentifier codename,
     * return the matching distribution.  Otherwise return
     * UNKNOWN_VALUE_STRING.
     */
    private String readReleaseFromAptCachePolicyOutput(InputStream inputStream) throws IOException {
        List<String> results;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            results = reader.lines()
                    .filter(line -> aptCacheIdentifiers.stream().anyMatch(line::contains))
                    .collect(Collectors.toList());
        }
        if (results.isEmpty()) {
            LOGGER.log(Level.FINEST, "empty apt-cache policy, not testing, sid, or unstable");
            return PlatformDetailsTask.UNKNOWN_VALUE_STRING;
        } else if (results.get(0).contains(APT_CACHE_POLICY_TESTING)) {
            LOGGER.log(Level.FINEST, "apt-cache policy is testing");
            return "testing";
        } else if (results.get(0).contains(APT_CACHE_POLICY_UNSTABLE)) {
            LOGGER.log(Level.FINEST, "apt-cache policy is unstable");
            return "unstable";
        } else if (results.get(0).contains(APT_CACHE_POLICY_SID)) {
            LOGGER.log(Level.FINEST, "apt-cache policy is sid");
            return "unstable";
        }
        LOGGER.log(Level.FINEST, "unexpected non-empty apt-cache policy, not testing, sid, or unstable");
        return PlatformDetailsTask.UNKNOWN_VALUE_STRING;
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
