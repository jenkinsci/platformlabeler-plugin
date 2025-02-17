package org.jvnet.hudson.plugins.platformlabeler;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

class LsbReleaseFakeTest {
    private final String fakeDistributorId = "megasupercorp";
    private final String fakeRelease = "1.2.3";
    private final LsbRelease fakeLsbRelease = new LsbRelease(fakeDistributorId, fakeRelease);

    @Test
    void matchingFakeDistributorId() {
        assertThat(fakeLsbRelease.distributorId(), is(fakeDistributorId));
    }

    @Test
    void matchingFakeRelease() {
        assertThat(fakeLsbRelease.release(), is(fakeRelease));
    }
}
