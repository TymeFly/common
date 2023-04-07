package com.github.tymefly.common.document.key;

import com.github.tymefly.common.document.DocumentException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link DocumentKeyBuilder}
 */
public class DocumentKeyBuilderTest {
    private final DocumentKey first = () -> "a";
    private final DocumentKey second = () -> "b";


    /**
     * Unit test {@link DocumentKeyBuilder#append(DocumentKey)}
     */
    @Test
    public void test_Empty() {
        Assert.assertThrows(DocumentException.class, () -> new DocumentKeyBuilder().build());
    }

    /**
     * Unit test {@link DocumentKeyBuilder#append(DocumentKey)}
     */
    @Test
    public void test_FromSingleDocumentKey() {
        Assert.assertEquals("Unexpected key", "a", new DocumentKeyBuilder().append(first).build().externalise());
    }

    /**
     * Unit test {@link DocumentKeyBuilder#append(DocumentKey)}
     */
    @Test
    public void test_FromMultipleDocumentKey() {
        Assert.assertEquals("Unexpected key",
                "a.b",
                new DocumentKeyBuilder().append(first).append(second).build().externalise());
    }

    /**
     * Unit test {@link DocumentKeyBuilder#append(DocumentKey)}
     */
    @Test
    public void test_FromSingleExternalKey() {
        Assert.assertEquals("Unexpected key",
                "hello.world",
                new DocumentKeyBuilder().append("hello").append("world").build().externalise());
    }

    /**
     * Unit test {@link DocumentKeyBuilder#append(DocumentKey)}
     */
    @Test
    public void test_withIndex() {
        Assert.assertEquals("Unexpected key",
                "a[12]",
                new DocumentKeyBuilder().append(first).append(12).build().externalise());
    }

    /**
     * Unit test {@link DocumentKeyBuilder#append(DocumentKey)}
     */
    @Test
    public void test_happyPath() {
        Assert.assertEquals("Unexpected key",
                "a[12].b.array[23]",
                new DocumentKeyBuilder()
                        .append(first)
                        .append(12)
                        .append(second)
                        .append("array")
                        .append(23)
                        .build()
                        .externalise());
    }
}