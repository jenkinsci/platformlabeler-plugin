package org.jvnet.hudson.plugins.platformlabeler;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.Computer;
import hudson.node_monitors.AbstractAsyncNodeMonitorDescriptor;
import hudson.node_monitors.NodeMonitor;
import hudson.remoting.Callable;
import java.io.IOException;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

public class OsVersionNodeMonitor extends NodeMonitor {

    @DataBoundConstructor
    public OsVersionNodeMonitor() {}

    @Extension
    @Symbol("osVersion")
    public static final class DescriptorImpl extends AbstractAsyncNodeMonitorDescriptor<PlatformDetails> {

        @Override
        protected Callable<PlatformDetails, IOException> createCallable(Computer c) {
            return new PlatformDetailsTask();
        }

        @NonNull
        @Override
        public String getDisplayName() {
            return Messages.OsVersionMonitor_DisplayName();
        }

        @Override
        public boolean canTakeOffline() {
            return false;
        }
    }
}
