package org.jvnet.hudson.plugins.platformlabeler;

import java.util.Collection;
import jenkins.security.Roles;
import org.jenkinsci.remoting.Role;
import org.jenkinsci.remoting.RoleChecker;
import org.jenkinsci.remoting.RoleSensitive;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

/**
 * Check the role checker in PlatformDetailsTask.
 *
 * @author Mark Waite
 */
public class PlatformDetailsCheckRolesTest {

    @Rule public JenkinsRule r = new JenkinsRule();

    private PlatformDetailsTask platformDetailsTask;

    @Before
    public void createPlatformDetailsTask() {
        platformDetailsTask = new PlatformDetailsTask();
    }

    @Test
    public void testCheckRoles() {
        RoleChecker checker =
                new RoleChecker() {
                    @Override
                    public void check(RoleSensitive rs, Collection<Role> roleCollection)
                            throws SecurityException {
                        if (!roleCollection.contains(Roles.SLAVE)) {
                            throw new SecurityException(Roles.SLAVE + " missing");
                        }
                    }
                };
        platformDetailsTask.checkRoles(checker);
    }

    @Test(expected = SecurityException.class)
    public void testCheckRolesThrowsSecurityException() {
        RoleChecker exceptionThrowingChecker =
                new RoleChecker() {
                    @Override
                    public void check(RoleSensitive rs, Collection<Role> roleCollection)
                            throws SecurityException {
                        if (roleCollection.contains(Roles.SLAVE)) {
                            throw new SecurityException(
                                    Roles.SLAVE + " found, throwing intentional exception");
                        }
                    }
                };
        platformDetailsTask.checkRoles(exceptionThrowingChecker);
    }
}
