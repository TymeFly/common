package com.github.tymefly.common.document.key;

import com.github.tymefly.common.document.DocumentException;
import junit.framework.TestCase;
import org.junit.Assert;

/**
 * Unit test {@link ConstructedKey}
 */
public class ConstructedKeyTest extends TestCase {
    private final ConstructedKey first = new ConstructedKey("a");
    private final ConstructedKey second = new ConstructedKey("a.b");
    private final ConstructedKey third = new ConstructedKey("a.b[1]");
    private final ConstructedKey fourth = new ConstructedKey("a[2].b[1]");


    public void test_InvalidKey() {
        Assert.assertThrows(DocumentException.class, () -> new ConstructedKey(""));
    }


    /**
     * Unit test {@link ConstructedKey#externalise()}
     */
    public void test_externalise() {
        Assert.assertEquals("first failed externalise()", "a", first.externalise());
        Assert.assertEquals("second failed externalise()", "a.b", second.externalise());
        Assert.assertEquals("third failed externalise()", "a.b[1]", third.externalise());
        Assert.assertEquals("fourth failed externalise()", "a[2].b[1]", fourth.externalise());
    }


    /**
     * Unit test {@link ConstructedKey#toString()}
     */
    public void test_toString() {
        Assert.assertTrue("first.toString() has wrong key", first.toString().contains("a"));
        Assert.assertTrue("second failed externalise()",second.toString().contains("a.b"));
        Assert.assertTrue("third failed externalise()", third.toString().contains("a.b[1]"));
        Assert.assertTrue("fourth failed externalise()",fourth.toString().contains("a[2].b[1]"));
    }
}