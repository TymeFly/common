package com.github.tymefly.common.document;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link Document}
 */
public class DocumentTest {
    /**
     * Unit test {@link Document#empty}
     */
    @Test
    public void test_Empty() {
        ReadableDocument doc1 = Document.empty();
        ReadableDocument doc2 = Document.empty();

        Assert.assertSame("Expected Same Document", doc1, doc2);
        Assert.assertTrue("Expected an empty document", doc1.isEmpty());
        Assert.assertThrows("Was able to mutate document",
            UnsupportedOperationException.class,
            () -> ((Document) doc1).addBoolean(() -> "x", true));
    }


    /**
     * Unit test {@link Document#newInstance}
     */
    @Test
    public void test_newInstance() {
        Document doc1 = Document.newInstance();
        Document doc2 = Document.newInstance();

        Assert.assertNotSame("Expected Different Documents", doc1, doc2);
        Assert.assertTrue("doc1 was not empty", doc1.isEmpty());
        Assert.assertTrue("doc2 was not empty", doc2.isEmpty());
        Assert.assertEquals("Document are not equal", doc1, doc2);

        doc1.addBoolean(() -> "x", true);

        Assert.assertNotEquals("Document should not be equal", doc1, doc2);
    }

    /**
     * Unit test {@link Document#factory}
     */
    @Test
    public void test_Factory() {
        DocumentFactory<Document> factory = Document.factory();

        Assert.assertEquals("Unexpected type", DocumentFactoryImpl.class, factory.getClass());
    }

    /**
     * Unit test {@link Document#factory(CommonDocument)}
     */
    @Test
    public void test_Wrap() {
        CommonDocument inner = new DocumentImpl(null);
        WrappedDocumentFactory<Document> factory = Document.factory(inner);

        Assert.assertEquals("Unexpected type", DocumentFactoryImpl.class, factory.getClass());
    }
}