package com.github.tymefly.common.document.key;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link FlatDocumentKey}
 */
public class FlatDocumentKeyTest {
    private enum Key implements FlatDocumentKey {
        ONE, TWO, THREE, A_B, A_B_CHILD
    }

    /**
     * Unit test {@link FlatDocumentKey#externalise()}
     */
    @Test
    public void test_externalise() {
        Assert.assertEquals("ONE", "one", Key.ONE.externalise());
        Assert.assertEquals("TWO", "two", Key.TWO.externalise());
        Assert.assertEquals("THREE", "three", Key.THREE.externalise());
        Assert.assertEquals("A_B", "a_b", Key.A_B.externalise());
        Assert.assertEquals("A_B_CHILD", "a_b_child", Key.A_B_CHILD.externalise());
    }
}