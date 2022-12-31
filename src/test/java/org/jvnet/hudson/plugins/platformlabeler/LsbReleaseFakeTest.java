package org.jvnet.hudson.plugins.platformlabeler;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;

public class LsbReleaseFakeTest {
    private String fakeDistributorId = "megasupercorp";
    private String fakeRelease = "1.2.3";
    private LsbRelease fakeLsbRelease = new LsbRelease(fakeDistributorId, fakeRelease);

    @Test
    void matchingFakeDistributorId() {
        assertThat(fakeLsbRelease.distributorId(), is(fakeDistributorId));
    }

    @Test
    void matchingFakeRelease() {
        assertThat(fakeLsbRelease.release(), is(fakeRelease));
    }
}
