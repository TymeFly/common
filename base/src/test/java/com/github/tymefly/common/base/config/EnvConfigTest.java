package com.github.tymefly.common.base.config;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link EnvConfig}
 */
public class EnvConfigTest {
    private enum TestKey implements ConfigSetting {
        DEFINED("JUNIT_DEFINED", "???"),
        DEFAULTED("JUNIT_DEFAULTED", "MyDefault"),
        UNDEFINED("JUNIT_UNDEFINED", null);


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


    private EnvConfig<TestKey> config;


    @Before
    public void setUp() {
        Map<String, String> environment = Map.of("JUNIT_DEFINED", "setValue");

        config = new EnvConfig<>(environment);
    }

    /**
     * Unit test {@link EnvConfig#contains(ConfigSetting)}
     */
    @Test
    public void test_contains() {
        Assert.assertTrue("Contains DEFINED", config.contains(TestKey.DEFINED));
        Assert.assertFalse("Contains DEFAULTED", config.contains(TestKey.DEFAULTED));
        Assert.assertFalse("Contains UNDEFINED", config.contains(TestKey.UNDEFINED));
    }


    /**
     * Unit test {@link EnvConfig#read(ConfigSetting, Class)}}
     */
    @Test
    public void test_read() {
        Assert.assertEquals("Read DEFINED", "setValue", config.read(TestKey.DEFINED, String.class));
        Assert.assertEquals("Read DEFAULTED", "MyDefault", config.read(TestKey.DEFAULTED, String.class));

        Exception actual = Assert.assertThrows("Read UNDEFINED",
                IllegalStateException.class,
                () -> config.read(TestKey.UNDEFINED, String.class));

        Assert.assertEquals("Unexpected message",
                "Setting JUNIT_UNDEFINED was not defined in System Environment Variables",
                actual.getMessage());
    }
}