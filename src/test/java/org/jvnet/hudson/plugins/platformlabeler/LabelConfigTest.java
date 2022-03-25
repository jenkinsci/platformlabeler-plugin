package org.jvnet.hudson.plugins.platformlabeler;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Random;
import org.junit.Before;
import org.junit.Test;

public class LabelConfigTest {

    private final Random random = new Random();

    private LabelConfig defaultConfig;

    private LabelConfig randomConfig;
    private boolean randomIsArchitecture;
    private boolean randomIsName;
    private boolean randomIsVersion;
    private boolean randomIsArchitectureName;
    private boolean randomIsNameVersion;
    private boolean randomIsArchitectureNameVersion;

    public LabelConfigTest() {}

    @Before
    public void setUp() {
        LabelConfig randomSrcLabelConfig = new LabelConfig();

        randomIsArchitecture = random.nextBoolean();
        randomIsName = random.nextBoolean();
        randomIsVersion = random.nextBoolean();
        randomIsArchitectureName = random.nextBoolean();
        randomIsNameVersion = random.nextBoolean();
        randomIsArchitectureNameVersion = random.nextBoolean();

        randomSrcLabelConfig.setArchitecture(randomIsArchitecture);
        randomSrcLabelConfig.setName(randomIsName);
        randomSrcLabelConfig.setVersion(randomIsVersion);
        randomSrcLabelConfig.setArchitectureName(randomIsArchitectureName);
        randomSrcLabelConfig.setNameVersion(randomIsNameVersion);
        randomSrcLabelConfig.setArchitectureNameVersion(randomIsArchitectureNameVersion);

        defaultConfig = new LabelConfig();
        randomConfig = new LabelConfig(randomSrcLabelConfig);
    }

    @Test
    public void testConstructorNullArg() {
        LabelConfig nullLabelConfig = new LabelConfig(null);
        assertThat(nullLabelConfig.isArchitecture(), is(defaultConfig.isArchitecture()));
        assertThat(nullLabelConfig.isName(), is(defaultConfig.isName()));
        assertThat(nullLabelConfig.isVersion(), is(defaultConfig.isVersion()));
        assertThat(nullLabelConfig.isArchitectureName(), is(defaultConfig.isArchitectureName()));
        assertThat(nullLabelConfig.isNameVersion(), is(defaultConfig.isNameVersion()));
        assertThat(
                nullLabelConfig.isArchitectureNameVersion(),
                is(defaultConfig.isArchitectureNameVersion()));
    }

    @Test
    public void testIsArchitecture() {
        assertThat(defaultConfig.isArchitecture(), is(true));
        assertThat(randomConfig.isArchitecture(), is(randomIsArchitecture));
    }

    @Test
    public void testSetArchitecture() {
        defaultConfig.setArchitecture(!randomIsArchitecture);
        assertThat(defaultConfig.isArchitecture(), is(!randomIsArchitecture));
    }

    @Test
    public void testIsName() {
        assertThat(defaultConfig.isName(), is(true));
        assertThat(randomConfig.isName(), is(randomIsName));
    }

    @Test
    public void testSetName() {
        defaultConfig.setName(!randomIsName);
        assertThat(defaultConfig.isName(), is(!randomIsName));
    }

    @Test
    public void testIsVersion() {
        assertThat(defaultConfig.isVersion(), is(true));
        assertThat(randomConfig.isVersion(), is(randomIsVersion));
    }

    @Test
    public void testSetVersion() {
        defaultConfig.setVersion(!randomIsVersion);
        assertThat(defaultConfig.isVersion(), is(!randomIsVersion));
    }

    @Test
    public void testIsArchitectureName() {
        assertThat(defaultConfig.isArchitectureName(), is(true));
        assertThat(randomConfig.isArchitectureName(), is(randomIsArchitectureName));
    }

    @Test
    public void testSetArchitectureName() {
        defaultConfig.setArchitectureName(!randomIsArchitectureName);
        assertThat(defaultConfig.isArchitectureName(), is(!randomIsArchitectureName));
    }

    @Test
    public void testIsNameVersion() {
        assertThat(defaultConfig.isNameVersion(), is(true));
        assertThat(randomConfig.isNameVersion(), is(randomIsNameVersion));
    }

    @Test
    public void testSetNameVersion() {
        defaultConfig.setNameVersion(!randomIsNameVersion);
        assertThat(defaultConfig.isNameVersion(), is(!randomIsNameVersion));
    }

    @Test
    public void testIsArchitectureNameVersion() {
        assertThat(defaultConfig.isArchitectureNameVersion(), is(true));
        assertThat(randomConfig.isArchitectureNameVersion(), is(randomIsArchitectureNameVersion));
    }

    @Test
    public void testSetArchitectureNameVersion() {
        defaultConfig.setArchitectureNameVersion(!randomIsArchitectureNameVersion);
        assertThat(defaultConfig.isArchitectureNameVersion(), is(!randomIsArchitectureNameVersion));
    }
}
