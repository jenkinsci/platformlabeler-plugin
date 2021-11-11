package org.jvnet.hudson.plugins.platformlabeler;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.Extension;
import hudson.slaves.ComputerListener;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Allows to configure which labels should be generated for the node when no node specific
 * configuration is used.
 */
@Extension
public class PlatformLabelerGlobalConfiguration extends GlobalConfiguration {

    private LabelConfig labelConfig;

    /** Standard constructor. */
    @SuppressFBWarnings(
            value = "MC_OVERRIDABLE_METHOD_CALL_IN_CONSTRUCTOR",
            justification = "GlobalConfiguration does not depend on initialization of this object")
    public PlatformLabelerGlobalConfiguration() {
        load();
        if (labelConfig == null) {
            labelConfig = new LabelConfig();
        }
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        boolean result = super.configure(req, json);
        NodeLabelCache nlc = ComputerListener.all().get(NodeLabelCache.class);
        if (nlc != null) {
            nlc.onConfigurationChange();
        }
        return result;
    }

    public LabelConfig getLabelConfig() {
        /* Return a defensive copy to prevent caller changes to the returned object affecting this object */
        return new LabelConfig(labelConfig);
    }

    public void setLabelConfig(LabelConfig labelConfig) {
        /* Save a defensive copy to prevent caller changes to the returned object affecting this object */
        this.labelConfig = new LabelConfig(labelConfig);
        save();
    }
}
