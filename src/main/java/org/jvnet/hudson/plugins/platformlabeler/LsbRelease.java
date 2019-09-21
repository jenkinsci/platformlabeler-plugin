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
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/** Linux standard base release class. Provides distributor ID and release. */
public class LsbRelease {
  @NonNull private final String distributorId;
  @NonNull private final String release;

  /** Extract distributor ID and release for current platform. */
  public LsbRelease() {
    Map<String, String> newProps = new HashMap<>();
    try {
      Process process = new ProcessBuilder("lsb_release", "-a").start();
      try (BufferedReader reader =
          new BufferedReader(
              new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
        reader
            .lines()
            .map(line -> line.split(":", 2))
            .forEach(parts -> newProps.put(parts[0], parts[1].trim()));
      }
    } catch (IOException e) {
      // IGNORE
    }
    String id = newProps.get("Distributor ID");
    this.distributorId = id != null ? id : PlatformDetailsTask.UNKNOWN_VALUE_STRING;
    String rel = newProps.get("Release");
    this.release = rel != null ? rel : PlatformDetailsTask.UNKNOWN_VALUE_STRING;
  }

  /**
   * Return the Linux distributor ID for this agent.
   *
   * @return Linux distributor ID for this agent
   */
  @NonNull
  public String distributorId() {
    return distributorId;
  }

  /**
   * Return the Linux release for this agent.
   *
   * @return Linux release for this agent
   */
  @NonNull
  public String release() {
    return release;
  }
}
