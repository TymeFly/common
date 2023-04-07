package com.github.tymefly.common.base.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link ManifestReader}
 */
public class ManifestReaderTest {
    private enum TestEnum {
        A, B
    }


    private ManifestReader reader;


    @Before
    public void setUp() {
        reader = new ManifestReader();
    }

    /**
     * Unit test {@link ManifestReader#read(String, Class)}
     */
    @Test
    public void test_read_Existing_nullable() {
        Assert.assertEquals("Read String", "Hello", reader.read("common-test-string", String.class));
        Assert.assertEquals("Read Number", 12, (int) reader.read("common-test-number", Integer.class));
        Assert.assertTrue("Read Boolean", reader.read("common-test-boolean", Boolean.class));
        Assert.assertEquals("Read Enum", TestEnum.A, reader.read("common-test-enum", TestEnum.class));
        Assert.assertNull("Read Empty", reader.read("common-test-empty", String.class));        // Empty attribute is ignored
    }


    /**
     * Unit test {@link ManifestReader#read(String, Class)}
     */
    @Test
    public void test_read_Missing_nullable() {
        Assert.assertNull("Read String",reader.read("missing-test-string", String.class));
        Assert.assertNull("Read Number", reader.read("missing-test-number", Integer.class));
        Assert.assertNull("Read Boolean", reader.read("missing-test-boolean", Boolean.class));
        Assert.assertNull("Read Enum",reader.read("missing-test-enum", TestEnum.class));
        Assert.assertNull("Read Empty", reader.read("missing-test-empty", String.class));
    }


    /**
     * Unit test {@link ManifestReader#read(String, Object)}
     */
    @Test
    public void test_read_Existing_WithDefault() {
        Assert.assertEquals("Read String", "Hello", reader.read("common-test-string","default"));
        Assert.assertEquals("Read Number", 12, (int) reader.read("common-test-number", -1));
        Assert.assertTrue("Read Boolean", reader.read("common-test-boolean", false));
        Assert.assertEquals("Read Enum", TestEnum.A, reader.read("common-test-enum", TestEnum.B));
        Assert.assertEquals("Read Empty", "???", reader.read("common-test-empty", "???"));     // Empty is converted to default
    }


    /**
     * Unit test {@link ManifestReader#read(String, Object)}
     */
    @Test
    public void test_read_Missing_WithDefault() {
        Assert.assertEquals("Read String", "default", reader.read("missing-test-string","default"));
        Assert.assertEquals("Read Number", -1, (int) reader.read("missing-test-number", -1));
        Assert.assertFalse("Read Boolean", reader.read("missing-test-boolean", false));
        Assert.assertEquals("Read Enum", TestEnum.B, reader.read("missing-test-enum", TestEnum.B));
        Assert.assertEquals("Read Empty", "???", reader.read("missing-test-empty", "???"));
    }
}