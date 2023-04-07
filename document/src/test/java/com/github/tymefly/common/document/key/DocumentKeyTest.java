package com.github.tymefly.common.document.key;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link DocumentKeyBuilder}
 */
public class DocumentKeyTest {
    private enum LayeredKey implements LayeredDocumentKey {
        ONE, TWO, THREE, FOUR_FIVE
    }

    private enum FlatKey implements FlatDocumentKey {
        A, B, C_D
    }


    /**
     * Unit test {@link DocumentKeyBuilder}
     */
    @Test
    public void test_SingleElement() {
        Assert.assertEquals("Simple", "one", DocumentKey.from(LayeredKey.ONE).externalise());
        Assert.assertEquals("Flat", "a", DocumentKey.from(FlatKey.A).externalise());
        Assert.assertEquals("Layered", "four.five", DocumentKey.from(LayeredKey.FOUR_FIVE).externalise());
    }


    /**
     * Unit test {@link DocumentKeyBuilder}
     */
    @Test
    public void test_TwoElements() {
        Assert.assertEquals("Flat", "a.b", DocumentKey.from(FlatKey.A, FlatKey.B).externalise());
        Assert.assertEquals("Layered", "one.two", DocumentKey.from(LayeredKey.ONE, LayeredKey.TWO).externalise());
        Assert.assertEquals("Mixed", "one.c_d", DocumentKey.from(LayeredKey.ONE, FlatKey.C_D).externalise());
    }


    /**
     * Unit test {@link DocumentKeyBuilder}
     */
    @Test
    public void test_MultipleElements() {
        Assert.assertEquals("Flat", "a.b.c_d", DocumentKey.from(FlatKey.A, FlatKey.B, FlatKey.C_D).externalise());
        Assert.assertEquals("Layered",
                "one.two.three.four.five",
                DocumentKey.from(LayeredKey.ONE, LayeredKey.TWO, LayeredKey.THREE, LayeredKey.FOUR_FIVE).externalise());
        Assert.assertEquals("Mixed",
                "one.a.four.five.c_d",
                DocumentKey.from(LayeredKey.ONE, FlatKey.A, LayeredKey.FOUR_FIVE, FlatKey.C_D).externalise());
    }
}