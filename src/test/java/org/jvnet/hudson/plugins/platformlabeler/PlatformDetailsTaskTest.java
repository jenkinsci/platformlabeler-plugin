package org.jvnet.hudson.plugins.platformlabeler;

import java.io.File;
import java.util.Set;
import static org.hamcrest.Matchers.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class PlatformDetailsTaskTest {

    public PlatformDetailsTaskTest() {
    }

    @Test
    public void testCall() throws Exception {
        PlatformDetailsTask platformDetailsTask = new PlatformDetailsTask();
        Set<String> details = platformDetailsTask.call();
        if (isWindows()) {
            assertThat(details, hasItems("windows"));
        } else {
            assertThat(details, not(hasItems("windows")));
        }
        String osName = System.getProperty("os.name", "os.name.is.unknown");
        if (osName.toLowerCase().startsWith("linux")) {
            assertThat(details, not(hasItems("linux")));
            assertThat(details, not(hasItems("Linux")));
            assertThat(details, anyOf(hasItems("Debian"), hasItems("CentOS"), hasItems("Ubuntu")));
        }
    }

    private boolean isWindows() {
        return File.pathSeparatorChar == ';';
    }
}
