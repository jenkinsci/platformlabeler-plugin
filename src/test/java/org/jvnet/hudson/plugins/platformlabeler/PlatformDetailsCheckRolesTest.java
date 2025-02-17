package org.jvnet.hudson.plugins.platformlabeler;

import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Collection;
import jenkins.security.Roles;
import org.jenkinsci.remoting.Role;
import org.jenkinsci.remoting.RoleChecker;
import org.jenkinsci.remoting.RoleSensitive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

/**
 * Check the role checker in PlatformDetailsTask.
 *
 * @author Mark Waite
 */
@WithJenkins
class PlatformDetailsCheckRolesTest {

    private JenkinsRule r;

    private PlatformDetailsTask platformDetailsTask;

    @BeforeEach
    void setUp(JenkinsRule r) {
        this.r = r;
        platformDetailsTask = new PlatformDetailsTask();
    }

    @Test
    void testCheckRoles() {
        RoleChecker checker = new RoleChecker() {
            @Override
            public void check(@NonNull RoleSensitive rs, @NonNull Collection<Role> roleCollection)
                    throws SecurityException {
                if (!roleCollection.contains(Roles.SLAVE)) {
                    throw new SecurityException(Roles.SLAVE + " missing");
                }
            }
        };
        platformDetailsTask.checkRoles(checker);
    }

    @Test
    void testCheckRolesThrowsSecurityException() {
        RoleChecker exceptionThrowingChecker = new RoleChecker() {
            @Override
            public void check(@NonNull RoleSensitive rs, @NonNull Collection<Role> roleCollection)
                    throws SecurityException {
                if (roleCollection.contains(Roles.SLAVE)) {
                    throw new SecurityException(Roles.SLAVE + " found, throwing intentional exception");
                }
            }
        };
        assertThrows(SecurityException.class, () -> platformDetailsTask.checkRoles(exceptionThrowingChecker));
    }
}
