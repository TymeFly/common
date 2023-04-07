package com.github.tymefly.common.document.key;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link LayeredDocumentKey}
 */
public class LayeredDocumentKeyTest {
    private enum Key implements LayeredDocumentKey {
        ONE, TWO, THREE, A_B, A_B_CHILD
    }

    /**
     * Unit test {@link LayeredDocumentKey#externalise()}
     */
    @Test
    public void test_externalise() {
        Assert.assertEquals("ONE", "one", Key.ONE.externalise());
        Assert.assertEquals("TWO", "two", Key.TWO.externalise());
        Assert.assertEquals("THREE", "three", Key.THREE.externalise());
        Assert.assertEquals("A_B", "a.b", Key.A_B.externalise());
        Assert.assertEquals("A_B_CHILD", "a.b.child", Key.A_B_CHILD.externalise());
    }
}