package org.jvnet.hudson.plugins.platformlabeler;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Computer;
import hudson.model.Node;
import hudson.model.TaskListener;
import hudson.model.TopLevelItem;
import hudson.model.labels.LabelAtom;
import hudson.remoting.Callable;
import hudson.remoting.VirtualChannel;
import hudson.slaves.NodeDescriptor;
import hudson.slaves.NodeProperty;
import hudson.slaves.NodePropertyDescriptor;
import hudson.slaves.RetentionStrategy;
import hudson.util.ClockDifference;
import hudson.util.DescribableList;
import hudson.util.LogTaskListener;
import hudson.util.RingBufferLogHandler;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.interceptor.RequirePOST;

/**
 * Test NodeLabelCache in a few potential error cases.
 *
 * @author Mark Waite
 */
public class NodeLabelCacheTest {

    @Rule
    public JenkinsRule r = new JenkinsRule();

    public NodeLabelCacheTest() {
        /* Intentionally empty constructor */
    }

    private Computer computer;
    private Set<LabelAtom> labelsBefore;
    private NodeLabelCache nodeLabelCache;
    private PlatformDetails localDetails;

    @Before
    public void setUp() throws IOException {
        computer = r.jenkins.toComputer();
        labelsBefore = computer.getNode().getAssignedLabels();
        assertThat(labelsBefore, is(not(empty())));
        nodeLabelCache = new NodeLabelCache();
        PlatformDetailsTask task = new PlatformDetailsTask();
        localDetails = task.computeLabels(
                System.getProperty("os.arch", PlatformDetailsTask.UNKNOWN_VALUE_STRING),
                System.getProperty("os.name", PlatformDetailsTask.UNKNOWN_VALUE_STRING),
                System.getProperty("os.version", PlatformDetailsTask.UNKNOWN_VALUE_STRING));
    }

    @After
    public void tearDown() {
        Set<LabelAtom> labelsAfter = computer.getNode().getAssignedLabels();
        assertThat(labelsBefore, everyItem(in(labelsAfter)));
    }

    @Test
    public void testCacheAndRefreshModel() throws Exception {
        nodeLabelCache.cacheAndRefreshModel(computer, computer.getChannel());
    }

    @Test
    public void testCacheLabels() throws Exception {
        nodeLabelCache.cacheLabels(computer, computer.getChannel());
    }

    @Test
    public void testRefreshModel() {
        nodeLabelCache.refreshModel(computer);
    }

    @Test
    public void testRefreshModelNullComputer() {
        nodeLabelCache.refreshModel(null);
    }

    @Test
    public void testRefreshModelNullingComputer() {
        Computer nullingComputer = new NullingComputer(computer.getNode());
        nodeLabelCache.refreshModel(nullingComputer);
    }

    @Test(expected = IOException.class)
    public void testCacheLabelsNullingComputer() throws Exception {
        Computer nullingComputer = new NullingComputer(computer.getNode());
        nodeLabelCache.cacheLabels(nullingComputer, nullingComputer.getChannel());
    }

    @Test
    public void testOnConfigurationChange() {
        nodeLabelCache.onConfigurationChange();
    }

    @Test
    public void testRequestComputerPlatformDetails() throws Exception {
        PlatformDetails platformDetails =
                nodeLabelCache.requestComputerPlatformDetails(computer, computer.getChannel());
        assertThat(platformDetails.getArchitecture(), is(localDetails.getArchitecture()));
        assertThat(platformDetails.getName(), is(localDetails.getName()));
        assertThat(platformDetails.getVersion(), is(localDetails.getVersion()));
        assertThat(platformDetails.getWindowsFeatureUpdate(), is(localDetails.getWindowsFeatureUpdate()));
    }

    @Test
    public void testGetLabelsForNode() throws IOException {
        Collection<LabelAtom> labels = nodeLabelCache.getLabelsForNode(computer.getNode());
        PlatformDetailsTask task = new PlatformDetailsTask();
        for (LabelAtom labelAtom : labels) {
            assertThat(
                    labelAtom.getName(),
                    anyOf(
                            is(localDetails.getArchitecture()),
                            is(localDetails.getArchitectureName()),
                            is(localDetails.getArchitectureNameVersion()),
                            is(localDetails.getName()),
                            is(localDetails.getNameVersion()),
                            is(localDetails.getVersion()),
                            is(localDetails.getWindowsFeatureUpdate())));
        }
    }

    @Test
    public void testGetLabelsForNode_IsNull() throws Exception {
        Node nullingNode = new NullingNode();
        Collection<LabelAtom> labels = nodeLabelCache.getLabelsForNode(nullingNode);
        assertThat(labels, is(empty()));
    }

    @Test(expected = IOException.class)
    public void testRequestComputerPlatformDetails_ChannelThrows() throws Exception {
        Computer throwingComputer = new NullingComputer(computer.getNode(), new IOException("Oops"));
        nodeLabelCache.requestComputerPlatformDetails(throwingComputer, throwingComputer.getChannel());
    }

    @Test(expected = IOException.class)
    public void testRequestComputerPlatformDetails_ChannelThrowsOnNullComputer() throws Exception {
        nodeLabelCache.requestComputerPlatformDetails(null, computer.getChannel());
    }

    @Test(expected = IOException.class)
    public void testRequestComputerPlatformDetails_ChannelThrowsOnNullChannel() throws Exception {
        nodeLabelCache.requestComputerPlatformDetails(computer, null);
    }

    @Test
    public void testPreOnline_ChannelLogsDetailCollectionIgnoredOnInternalException() throws Exception {
        // Setup a recorder for agent log
        RingBufferLogHandler agentLogHandler = new RingBufferLogHandler(10);
        Logger agentLogger = Logger.getLogger(NodeLabelCacheTest.class.getName());
        agentLogger.addHandler(agentLogHandler);
        TaskListener agentListener = new LogTaskListener(agentLogger, Level.INFO);

        nodeLabelCache.preOnline(computer, null, new FilePath(new File(".")), agentListener);

        assertThat(
                agentLogHandler.getView().get(0).getMessage(),
                startsWith("Ignored platform detail collection failure for 'unnamed agent' during preOnline phase."));
    }

    @Test
    public void testPreOnline_ChannelLogsDetailCollectionIgnoredOnInternalExceptionForNullComputer() throws Exception {
        // Setup a recorder for agent log
        RingBufferLogHandler agentLogHandler = new RingBufferLogHandler(10);
        Logger agentLogger = Logger.getLogger(NodeLabelCacheTest.class.getName());
        agentLogger.addHandler(agentLogHandler);
        TaskListener agentListener = new LogTaskListener(agentLogger, Level.INFO);

        nodeLabelCache.preOnline(null, null, new FilePath(new File(".")), agentListener);

        assertThat(
                agentLogHandler.getView().get(0).getMessage(),
                startsWith("Ignored platform detail collection failure for 'unnamed agent' during preOnline phase."));
    }

    @Test
    public void testPreOnline_ChannelLogsDetailCollectionIgnoredOnInternalExceptionForComputer() throws Exception {
        // Setup a recorder for agent log
        RingBufferLogHandler agentLogHandler = new RingBufferLogHandler(10);
        Logger agentLogger = Logger.getLogger(NodeLabelCacheTest.class.getName());
        agentLogger.addHandler(agentLogHandler);
        TaskListener agentListener = new LogTaskListener(agentLogger, Level.INFO);

        Computer minimal = new MinimalComputer(computer.getNode());
        String name = minimal.getName();
        nodeLabelCache.preOnline(minimal, null, new FilePath(new File(".")), agentListener);

        assertThat(
                agentLogHandler.getView().get(0).getMessage(),
                startsWith("Ignored platform detail collection failure for '" + name + "' during preOnline phase."));
    }

    /** A minimal Computer class for preOnline test. */
    private class MinimalComputer extends Computer {
        public MinimalComputer(Node node) {
            super(node);
        }

        @Override
        public Node getNode() {
            return super.getNode();
        }

        @Override
        public String getName() {
            return "computer-test";
        }

        @Override
        public VirtualChannel getChannel() {
            return null;
        }

        @Override
        public Charset getDefaultCharset() {
            throw new UnsupportedOperationException("Unsupported");
        }

        @Override
        public List<LogRecord> getLogRecords() throws IOException, InterruptedException {
            throw new UnsupportedOperationException("Unsupported");
        }

        @Override
        @RequirePOST
        public void doLaunchSlaveAgent(StaplerRequest sr, StaplerResponse sr1) throws IOException, ServletException {
            throw new UnsupportedOperationException("Unsupported");
        }

        @Override
        protected Future<?> _connect(boolean bln) {
            throw new UnsupportedOperationException("Unsupported");
        }

        @Override
        public Boolean isUnix() {
            throw new UnsupportedOperationException("Unsupported");
        }

        @Override
        public boolean isConnecting() {
            throw new UnsupportedOperationException("Unsupported");
        }

        @Override
        public RetentionStrategy<?> getRetentionStrategy() {
            throw new UnsupportedOperationException("Unsupported");
        }
    }

    /** Class that intentionally returns nulls for test purposes. */
    private class NullingComputer extends Computer {

        private final IOException exceptionToThrow;

        public NullingComputer(Node node) {
            super(node);
            exceptionToThrow = null;
        }

        public NullingComputer(Node node, IOException throwThisException) {
            super(node);
            exceptionToThrow = throwThisException;
        }

        @Override
        public Node getNode() {
            /* Intentionally return null to test null node handling */
            return null;
        }

        @Override
        public VirtualChannel getChannel() {
            if (exceptionToThrow != null) {
                return new ThrowingChannel(exceptionToThrow);
            }
            /* Intentionally return null to test null channel handling */
            return null;
        }

        @Override
        public Charset getDefaultCharset() {
            throw new UnsupportedOperationException("Unsupported");
        }

        @Override
        public List<LogRecord> getLogRecords() throws IOException, InterruptedException {
            throw new UnsupportedOperationException("Unsupported");
        }

        @Override
        @RequirePOST
        public void doLaunchSlaveAgent(StaplerRequest sr, StaplerResponse sr1) throws IOException, ServletException {
            throw new UnsupportedOperationException("Unsupported");
        }

        @Override
        protected Future<?> _connect(boolean bln) {
            throw new UnsupportedOperationException("Unsupported");
        }

        @Override
        public Boolean isUnix() {
            throw new UnsupportedOperationException("Unsupported");
        }

        @Override
        public boolean isConnecting() {
            throw new UnsupportedOperationException("Unsupported");
        }

        @Override
        public RetentionStrategy<?> getRetentionStrategy() {
            throw new UnsupportedOperationException("Unsupported");
        }
    }

    private class ThrowingChannel implements VirtualChannel {
        private final IOException exceptionToThrow;

        public ThrowingChannel(IOException exceptionToThrow) {
            this.exceptionToThrow = exceptionToThrow;
        }

        @Override
        public <V, T extends Throwable> V call(Callable<V, T> callable) throws IOException {
            if (exceptionToThrow != null) {
                throw exceptionToThrow;
            }
            return null;
        }

        @Override
        public <V, T extends Throwable> hudson.remoting.Future<V> callAsync(Callable<V, T> callable) {
            return null;
        }

        @Override
        public <T> T export(java.lang.Class<T> type, T instance) {
            return null;
        }

        @Override
        public void join() {
            throw new UnsupportedOperationException("Unsupported");
        }

        @Override
        public void join(long timeout) {
            throw new UnsupportedOperationException("Unsupported");
        }

        @Override
        public void syncLocalIO() {
            throw new UnsupportedOperationException("Unsupported");
        }

        @Override
        public void close() {
            throw new UnsupportedOperationException("Unsupported");
        }
    }

    private class NullingNode extends Node {
        @Override
        public Callable<ClockDifference, IOException> getClockDifferenceCallable() {
            throw new UnsupportedOperationException("Unsupported");
        }

        @Override
        public NodeDescriptor getDescriptor() {
            throw new UnsupportedOperationException("Unsupported");
        }

        @Override
        public DescribableList<NodeProperty<?>, NodePropertyDescriptor> getNodeProperties() {
            throw new UnsupportedOperationException("Unsupported");
        }

        @Override
        public FilePath getRootPath() {
            throw new UnsupportedOperationException("Unsupported");
        }

        @Override
        public FilePath getWorkspaceFor(TopLevelItem item) {
            throw new UnsupportedOperationException("Unsupported");
        }

        @Override
        public String getLabelString() {
            throw new UnsupportedOperationException("Unsupported");
        }

        @Override
        public Computer createComputer() {
            throw new UnsupportedOperationException("Unsupported");
        }

        @Override
        public Node.Mode getMode() {
            throw new UnsupportedOperationException("Unsupported");
        }

        @Override
        public int getNumExecutors() {
            throw new UnsupportedOperationException("Unsupported");
        }

        @Override
        public Launcher createLauncher(TaskListener listener) {
            throw new UnsupportedOperationException("Unsupported");
        }

        @Override
        public String getNodeDescription() {
            throw new UnsupportedOperationException("Unsupported");
        }

        @Override
        @Deprecated
        public void setNodeName(String name) {
            throw new UnsupportedOperationException("Unsupported");
        }

        @Override
        public String getNodeName() {
            throw new UnsupportedOperationException("Unsupported");
        }
    }
}
