package com.github.tymefly.common.document.decorator;

import java.util.Collections;

import com.github.tymefly.common.document.AbstractDocument;
import com.github.tymefly.common.document.Document;
import com.github.tymefly.common.document.key.DocumentKey;
import com.github.tymefly.common.document.visitor.util.Copy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link UnmodifiableDocument}
 */
public class UnmodifiableDocumentTest {
    private enum Type { ONE }

    private static final DocumentKey KEY = () -> "Key";
    private static final DocumentKey UNUSED = () -> "Unused";

    private UnmodifiableDocument doc;


    @Before
    public void setUp() {
        AbstractDocument backing = (AbstractDocument) Document.newInstance()
            .addString(KEY, "Hello")
            .addNumber(() -> "Number", 123)
            .addBoolean(() -> "Bool", true);
        doc = new UnmodifiableDocument(backing);
    }


    /**
     * Unit test {@link UnmodifiableDocument#unmodifiable()}
     */
    @Test
    public void test_immutable() {
        Assert.assertSame("No need to create a copy", doc, doc.unmodifiable());
    }


    /**
     * Unit test {@link UnmodifiableDocument} mutators
     */
    @Test
    public void test_setters() {
        Assert.assertThrows("addString", UnsupportedOperationException.class, () -> doc.addString(KEY, ""));
        Assert.assertThrows("addStrings array", UnsupportedOperationException.class, () -> doc.addStrings(KEY));
        Assert.assertThrows("addStrings list", UnsupportedOperationException.class, () -> doc.addStrings(KEY, Collections.emptyList()));
        Assert.assertThrows("appendString", UnsupportedOperationException.class, () -> doc.appendString(KEY, ""));

        Assert.assertThrows("addNumber", UnsupportedOperationException.class, () -> doc.addNumber(KEY, 0));
        Assert.assertThrows("addNumbers array", UnsupportedOperationException.class, () -> doc.addNumbers(KEY));
        Assert.assertThrows("addNumbers list", UnsupportedOperationException.class, () -> doc.addNumbers(KEY, Collections.emptyList()));
        Assert.assertThrows("appendNumber", UnsupportedOperationException.class, () -> doc.appendNumber(KEY, 0));

        Assert.assertThrows("addBoolean", UnsupportedOperationException.class, () -> doc.addBoolean(KEY, true));
        Assert.assertThrows("addBooleans array", UnsupportedOperationException.class, () -> doc.addBooleans(KEY));
        Assert.assertThrows("addBooleans list", UnsupportedOperationException.class, () -> doc.addBooleans(KEY, Collections.emptyList()));
        Assert.assertThrows("appendBoolean", UnsupportedOperationException.class, () -> doc.appendBoolean(KEY, true));

        Assert.assertThrows("addEnum", UnsupportedOperationException.class, () -> doc.addEnum(KEY, Type.ONE));
        Assert.assertThrows("addEnums array", UnsupportedOperationException.class, () -> doc.addEnums(KEY));
        Assert.assertThrows("addEnums list", UnsupportedOperationException.class, () -> doc.addEnums(KEY, Collections.<Type>emptyList()));
        Assert.assertThrows("appendEnum", UnsupportedOperationException.class, () -> doc.appendEnum(KEY, Type.ONE));

        Assert.assertThrows("addDocument", UnsupportedOperationException.class, () -> doc.addDocument(KEY, Document.empty()));
        Assert.assertThrows("addDocuments array", UnsupportedOperationException.class, () -> doc.addDocuments(KEY));
        Assert.assertThrows("addDocuments list", UnsupportedOperationException.class, () -> doc.addDocuments(KEY, Collections.emptyList()));
        Assert.assertThrows("appendDocument", UnsupportedOperationException.class, () -> doc.appendDocument(KEY, Document.empty()));
    }


    /**
     * Unit test {@link UnmodifiableDocument#remove(DocumentKey)}
     */
    @Test
    public void test_remove() {
        Assert.assertThrows(UnsupportedOperationException.class, () -> doc.remove(KEY));
    }


    /**
     * Unit test {@link UnmodifiableDocument} mutators
     */
    @Test
    public void test_getters() {
        Assert.assertEquals("get(type)", "Hello", doc.get(KEY, String.class));
        Assert.assertEquals("get(default)", "World", doc.getOrDefault(UNUSED, "World"));
        Assert.assertEquals("get(convert)", 5, (long) doc.get(KEY, String::length));
        Assert.assertEquals("getOptional()", null, doc.getOptional(UNUSED, String.class));
        Assert.assertEquals("getAll()", Collections.emptyList(), doc.getAll(UNUSED, String.class));

        Assert.assertFalse("isEmpty()", doc.isEmpty());
        Assert.assertTrue("contains()", doc.contains(KEY));
        Assert.assertTrue("hasValue()", doc.hasValue(KEY));
        Assert.assertFalse("isSequence()", doc.isSequence(KEY));
        Assert.assertEquals("accept()", doc, doc.accept(new Copy()));
    }
}