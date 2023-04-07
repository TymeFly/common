package com.github.tymefly.common.base.config;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link LowerCaseSetting}
 */
public class LowerCaseSettingTest {
    private enum Key implements LowerCaseSetting {
        UPPER,
        lower,
        Mixed_Case
    }


    /**
     * Unit test {@link UpperCaseSetting#getKey}
     */
    @Test
    public void test_GetKey() {
        Assert.assertEquals("UPPER", "upper", Key.UPPER.getKey());
        Assert.assertEquals("lower", "lower", Key.lower.getKey());
        Assert.assertEquals("Mixed_Case", "mixed.case", Key.Mixed_Case.getKey());
    }
}