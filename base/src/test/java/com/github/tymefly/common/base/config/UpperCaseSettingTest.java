package com.github.tymefly.common.base.config;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link UpperCaseSetting}
 */
public class UpperCaseSettingTest {
    private enum Key implements UpperCaseSetting {
        UPPER,
        lower,
        Mixed_Case
    }


    /**
     * Unit test {@link UpperCaseSetting#getKey}
     */
    @Test
    public void test_GetKey() {
        Assert.assertEquals("UPPER", "UPPER", Key.UPPER.getKey());
        Assert.assertEquals("lower", "LOWER", Key.lower.getKey());
        Assert.assertEquals("Mixed_Case", "MIXED_CASE", Key.Mixed_Case.getKey());
    }
}