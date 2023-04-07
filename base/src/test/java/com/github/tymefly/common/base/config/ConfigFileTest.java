package com.github.tymefly.common.base.config;

import java.io.File;
import java.net.URL;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link ConfigFile}
 */
public class ConfigFileTest {
    private enum TestKey implements ConfigSetting {
        A("a.a", "Default"),
        B("a.b", null),
        C("a.c", "999"),
        HELLO("Hello", null),
        EMPTY("empty", null),
        UNDEFINED("undefined", null);


        private final String key;
        private final String defaultValue;

        TestKey(@Nonnull String key, @Nullable String defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        @Nonnull
        @Override
        public String getKey() {
            return key;
        }

        @Nullable
        @Override
        public String getDefaultValue() {
            return defaultValue;
        }
    }


    private enum Enabled { ON, OFF };


    private ConfigFile<TestKey> config;


    @Before
    public void setUp() throws Exception {
        URL resource = getClass().getResource("/config/config.txt");
        File source = new File(resource.toURI());

        config = new ConfigFile<>(source);
    }

    /**
     * Unit test {@link ConfigFile#contains}
     */
    @Test
    public void test_contains() {
        Assert.assertFalse("Unexpected Key A", config.contains(TestKey.A));
        Assert.assertTrue("Expected Key B", config.contains(TestKey.B));
        Assert.assertTrue("Expected Key C", config.contains(TestKey.C));
        Assert.assertTrue("Expected Key HELLO", config.contains(TestKey.HELLO));
        Assert.assertTrue("Expected Key EMPTY", config.contains(TestKey.EMPTY));
        Assert.assertFalse("Unexpected Key UNDEFINED", config.contains(TestKey.UNDEFINED));
    }

    /**
     * Unit test {@link ConfigFile#read(ConfigSetting, Class)}
     */
    @Test
    public void test_get_String() {
        Assert.assertEquals("Read Key A", "Default", config.read(TestKey.A, String.class));
        Assert.assertEquals("Read Key B", "On", config.read(TestKey.B, String.class));
        Assert.assertEquals("Read Key C", "0", config.read(TestKey.C, String.class));
        Assert.assertEquals("Read Key HELLO", "World", config.read(TestKey.HELLO, String.class));
        Assert.assertEquals("Read Key EMPTY", "", config.read(TestKey.EMPTY, String.class));
        Assert.assertThrows("Read Key UNDEFINED", IllegalStateException.class, () -> config.read(TestKey.UNDEFINED, String.class));
    }

    /**
     * Unit test {@link ConfigFile#read(ConfigSetting, Class)}
     */
    @Test
    public void test_get_Int() {
        Assert.assertThrows("Read Key A", ConfigException.class, () -> config.read(TestKey.A, Integer.class));
        Assert.assertThrows("Read Key B", ConfigException.class, () -> config.read(TestKey.B, Integer.class));
        Assert.assertEquals("Expected Key C", Integer.valueOf(0), config.read(TestKey.C, Integer.class));
        Assert.assertThrows("Read Key HELLO", ConfigException.class, () -> config.read(TestKey.HELLO, Integer.class));
        Assert.assertThrows("Read Key EMPTY", ConfigException.class, () -> config.read(TestKey.EMPTY, Integer.class));
        Assert.assertThrows("Read Key UNDEFINED", IllegalStateException.class, () -> config.read(TestKey.UNDEFINED, Integer.class));
    }

    /**
     * Unit test {@link ConfigFile#read(ConfigSetting, Class)}
     */
    @Test
    public void test_get_Boolean() {
        Assert.assertThrows("Read Key A", ConfigException.class, () -> config.read(TestKey.A, Boolean.class));
        Assert.assertTrue("Read Key B", config.read(TestKey.B, Boolean.class));
        Assert.assertFalse("Read Key C", config.read(TestKey.C, Boolean.class));
        Assert.assertThrows("Read Key HELLO", ConfigException.class, () -> config.read(TestKey.HELLO, Boolean.class));
        Assert.assertThrows("Read Key EMPTY", ConfigException.class, () -> config.read(TestKey.EMPTY, Boolean.class));
        Assert.assertThrows("Read Key UNDEFINED", IllegalStateException.class, () -> config.read(TestKey.UNDEFINED, Boolean.class));
    }

    /**
     * Unit test {@link ConfigFile#read(ConfigSetting, Class)}
     */
    @Test
    public void test_get_Enum() {
        Assert.assertThrows("Read Key A", ConfigException.class, () -> config.read(TestKey.A, Enabled.class));
        Assert.assertEquals("Read Key B", Enabled.ON, config.read(TestKey.B, Enabled.class));
        Assert.assertThrows("Read Key C", ConfigException.class, () -> config.read(TestKey.C, Enabled.class));
        Assert.assertThrows("Read Key HELLO", ConfigException.class, () -> config.read(TestKey.HELLO, Enabled.class));
        Assert.assertThrows("Read Key EMPTY", ConfigException.class, () -> config.read(TestKey.EMPTY, Enabled.class));
        Assert.assertThrows("Read Key UNDEFINED", IllegalStateException.class, () -> config.read(TestKey.UNDEFINED, Enabled.class));
    }
}