package org.jvnet.hudson.plugins.platformlabeler;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LsbReleaseRealTest {
    private LsbRelease lsbRelease;

    @BeforeEach
    void initialize() {
        lsbRelease = new LsbRelease();
    }

    @Test
    void nonEmptyRealDistributorId() {
        assertThat(lsbRelease.distributorId(), is(not(emptyString())));
    }

    @Test
    void nonEmptyRealRelease() {
        assertThat(lsbRelease.release(), is(not(emptyString())));
    }
}
