package com.github.tymefly.common.base.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link Enums}
 */
public class EnumsTest {

    enum TestEnum {
        ONE, TWO, THREE, THREE_B
    }


    /**
     * Unit test {@link Enums#toEnum}
     */
    @Test
    public void test_toEnum() {
        Assert.assertEquals("basic", TestEnum.ONE, Enums.toEnum(TestEnum.class, "ONE"));
        Assert.assertEquals("change case", TestEnum.TWO, Enums.toEnum(TestEnum.class, "two"));
        Assert.assertEquals("Trim", TestEnum.THREE, Enums.toEnum(TestEnum.class, "  THREE  "));
        Assert.assertEquals("Underscores", TestEnum.THREE_B, Enums.toEnum(TestEnum.class, "THREE  B"));
        Assert.assertEquals("All", TestEnum.THREE_B, Enums.toEnum(TestEnum.class, "  Three  B  "));
    }


    /**
     * Unit test {@link Enums#toEnum}
     */
    @Test
    public void test_toEnum_EmptyName() {
        Exception actual = Assert.assertThrows(IllegalArgumentException.class,
                () -> Enums.toEnum(TestEnum.class, ""));

        Assert.assertEquals("Unexpected message", "Invalid constant in TestEnum ''", actual.getMessage());
    }


    /**
     * Unit test {@link Enums#toEnum}
     */
    @Test
    public void test_toEnum_InvalidName() {
        Exception actual = Assert.assertThrows(IllegalArgumentException.class,
                () ->  Enums.toEnum(TestEnum.class, "FOUR"));

        Assert.assertEquals("Unexpected message", "Invalid constant in TestEnum 'FOUR'", actual.getMessage());
    }


    /**
     * Unit test {@link Enums#safeToEnum(Class, String)}
     */
    @Test
    public void test_safeToEnum() {
        Assert.assertEquals("basic", TestEnum.ONE, Enums.safeToEnum(TestEnum.class, "ONE"));
        Assert.assertEquals("change case", TestEnum.TWO, Enums.safeToEnum(TestEnum.class, "two"));
        Assert.assertEquals("Trim", TestEnum.THREE, Enums.safeToEnum(TestEnum.class, "  THREE  "));
        Assert.assertEquals("Underscores", TestEnum.THREE_B, Enums.safeToEnum(TestEnum.class, "THREE  B"));
        Assert.assertEquals("All", TestEnum.THREE_B, Enums.safeToEnum(TestEnum.class, "  Three  B  "));
        Assert.assertNull("Empty String", Enums.safeToEnum(TestEnum.class, ""));
        Assert.assertNull("FOUR", Enums.safeToEnum(TestEnum.class, "FOUR"));
    }


    /**
     * Unit test {@link Enums#toEnums}
     */
    @Test
    public void test_toEnums() {
        Set<TestEnum> expected = new HashSet<>() {{
            add(TestEnum.ONE);
            add(TestEnum.TWO);
        }};

        Assert.assertEquals("basic",
            expected,
            Enums.toEnums(TestEnum.class,
                              Arrays.asList("ONE", "TWO", "ONE")));
    }
}