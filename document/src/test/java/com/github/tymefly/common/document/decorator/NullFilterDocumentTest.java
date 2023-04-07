package com.github.tymefly.common.document.decorator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import com.github.tymefly.common.document.AbstractDocument;
import com.github.tymefly.common.document.CommonDocument;
import com.github.tymefly.common.document.Document;
import com.github.tymefly.common.document.key.DocumentKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link NullFilterDocument}
 */
public class NullFilterDocumentTest {
    private enum Type { ONE, TWO, THREE }

    private static final DocumentKey VALUE = () -> "Value";
    private static final DocumentKey NULL = () -> "Null";

    private Document doc;


    @Before
    public void setUp() {
        AbstractDocument<?> backing = (AbstractDocument<?>) Document.newInstance();
        doc = new NullFilterDocument(backing);
    }

    /**
     * Unit test {@link NullFilterDocument#addString(DocumentKey, String)}
     */
    @Test
    public void test_addString() {
        doc.addString(VALUE, "Hello")
           .addString(NULL, null);

        Assert.assertEquals("addString()", Document.newInstance().addString(VALUE, "Hello"), doc);
    }

    /**
     * Unit test {@link NullFilterDocument#addStrings(DocumentKey, String...)}
     */
    @Test
    public void test_addStrings_Array() {
        doc.addStrings(VALUE, null, "Hello", null, "World")
           .addStrings(NULL, (String) null);

        Assert.assertEquals("addString(...)",
            Document.newInstance().addStrings(VALUE, "Hello", "World").addStrings(NULL),
            doc);
    }

    /**
     * Unit test {@link NullFilterDocument#addStrings(DocumentKey, Collection)}
     */
    @Test
    public void test_addStrings_List() {
        doc.addStrings(VALUE, Arrays.asList(null, "Hello", null, "World"))
           .addStrings(NULL, nullList(String.class));

        Assert.assertEquals("addStrings(List)",
            Document.newInstance().addStrings(VALUE, "Hello", "World").addStrings(NULL),
            doc);
    }

    /**
     * Unit test {@link NullFilterDocument#appendString(DocumentKey, String)}
     */
    @Test
    public void test_appendString() {
        doc.addStrings(VALUE, "Hello", "World")
           .appendString(VALUE, null)
           .appendString(VALUE, "?");

        Assert.assertEquals("appendString()",
            Document.newInstance().addStrings(VALUE, "Hello", "World", "?"),
            doc);
    }


    /**
     * Unit test {@link NullFilterDocument#addNumber(DocumentKey, Number)}
     */
    @Test
    public void test_addNumber() {
        doc.addNumber(VALUE, 12)
           .addNumber(NULL, null);

        Assert.assertEquals("addNumber()", Document.newInstance().addNumber(VALUE, 12), doc);
    }

    /**
     * Unit test {@link NullFilterDocument#addNumbers(DocumentKey, Number...)}
     */
    @Test
    public void test_addNumbers_Array() {
        doc.addNumbers(VALUE, 1, null, 2, null, 3)
           .addNumbers(NULL, (Long) null);

        Assert.assertEquals("addNumbers(...)",
            Document.newInstance().addNumbers(VALUE, 1, 2, 3).addNumbers(NULL),
            doc);
    }

    /**
     * Unit test {@link NullFilterDocument#addNumbers(DocumentKey, Collection)}
     */
    @Test
    public void test_addNumbers_List() {
        doc.addNumbers(VALUE, Arrays.asList(1, null, 2, null, 3))
           .addNumbers(NULL, nullList(Number.class));

        Assert.assertEquals("addNumbers(List)",
            Document.newInstance().addNumbers(VALUE, 1, 2, 3).addNumbers(NULL),
            doc);
    }

    /**
     * Unit test {@link NullFilterDocument#appendNumber(DocumentKey, Number)}
     */
    @Test
    public void test_appendNumber() {
        doc.addNumbers(VALUE, 1, 2)
           .appendNumber(VALUE, null)
           .appendNumber(VALUE, 3);

        Assert.assertEquals("appendNumber()",
            Document.newInstance().addNumbers(VALUE, 1, 2, 3),
            doc);
    }


    /**
     * Unit test {@link NullFilterDocument#addBoolean(DocumentKey, Boolean)}
     */
    @Test
    public void test_addBoolean() {
        doc.addBoolean(VALUE, true)
           .addBoolean(NULL, null);

        Assert.assertEquals("addBoolean()", Document.newInstance().addBoolean(VALUE, true), doc);
    }

    /**
     * Unit test {@link NullFilterDocument#addBooleans(DocumentKey, Boolean...)}
     */
    @Test
    public void test_addBooleans_Array() {
        doc.addBooleans(VALUE, true, false, null)
           .addBooleans(NULL, (Boolean) null);

        Assert.assertEquals("addBooleans(...)",
            Document.newInstance().addBooleans(VALUE, true, false).addBooleans(NULL),
            doc);
    }

    /**
     * Unit test {@link NullFilterDocument#addBooleans(DocumentKey, Boolean...)}
     */
    @Test
    public void test_addBooleans_List() {
        doc.addBooleans(VALUE, Arrays.asList(true, false, null))
           .addBooleans(NULL, nullList(Boolean.class));

        Assert.assertEquals("addBooleans(List)",
            Document.newInstance().addBooleans(VALUE, true, false).addBooleans(NULL),
            doc);
    }

    /**
     * Unit test {@link NullFilterDocument#appendBoolean(DocumentKey, Boolean)}
     */
    @Test
    public void test_appendBoolean() {
        doc.addBooleans(VALUE, true, true)
           .appendBoolean(VALUE, null)
           .appendBoolean(VALUE, false);

        Assert.assertEquals("appendBoolean()",
            Document.newInstance().addBooleans(VALUE, true, true, false),
            doc);
    }


    /**
     * Unit test {@link NullFilterDocument#addEnum(DocumentKey, Enum)}
     */
    @Test
    public void test_addEnum() {
        doc.addEnum(VALUE, Type.ONE)
           .addEnum(NULL, null);

        Assert.assertEquals("addEnum()", Document.newInstance().addEnum(VALUE, Type.ONE), doc);
    }

    /**
     * Unit test {@link NullFilterDocument#addEnums(DocumentKey, Enum[])}
     */
    @Test
    public void test_addEnums_Array() {
        doc.addEnums(VALUE, Type.ONE, null, null, Type.TWO)
           .addEnums(NULL, (Type) null);

        Assert.assertEquals("addEnums(...)",
            Document.newInstance().addEnums(VALUE, Type.ONE, Type.TWO).addEnums(NULL),
            doc);
    }

    /**
     * Unit test {@link NullFilterDocument#addEnums(DocumentKey, Enum[])}
     */
    @Test
    public void test_addEnums_List() {
        doc.addEnums(VALUE, Arrays.asList(Type.ONE, null, null, Type.TWO))
           .addEnums(NULL, nullList(Type.class));

        Assert.assertEquals("addEnums(List)",
            Document.newInstance().addEnums(VALUE, Type.ONE, Type.TWO).addEnums(NULL),
            doc);
    }

    /**
     * Unit test {@link NullFilterDocument#appendEnum(DocumentKey, Enum)}
     */
    @Test
    public void test_appendEnum() {
        doc.addEnums(VALUE, Type.ONE, Type.TWO)
           .appendEnum(VALUE, null)
           .appendEnum(VALUE, Type.THREE);

        Assert.assertEquals("appendEnum()",
            Document.newInstance().addEnums(VALUE, Type.ONE, Type.TWO, Type.THREE),
            doc);
    }


    /**
     * Unit test {@link NullFilterDocument#addDocument(DocumentKey, CommonDocument)}
     */
    @Test
    public void test_addDocument() {
        doc.addDocument(VALUE, Document.empty())
           .addDocument(NULL, null);

        Assert.assertEquals("addDocument()", Document.newInstance().addDocument(VALUE, Document.empty()), doc);
    }

    /**
     * Unit test {@link NullFilterDocument#addDocuments(DocumentKey, CommonDocument...)}
     */
    @Test
    public void test_addDocuments_Array() {
        doc.addDocuments(VALUE, Document.empty(), null, null, Document.empty())
           .addDocuments(NULL, (CommonDocument) null);

        Assert.assertEquals("addDocuments(...)",
            Document.newInstance().addDocuments(VALUE, Document.empty(), Document.empty()).addDocuments(NULL),
            doc);
    }

    /**
     * Unit test {@link NullFilterDocument#addDocuments(DocumentKey, CommonDocument...)}
     */
    @Test
    public void test_addDocuments_List() {
        doc.addDocuments(VALUE, Arrays.asList(Document.empty(), null, null, Document.empty()))
           .addDocuments(NULL, nullList(Document.class));

        Assert.assertEquals("addDocuments(List)",
            Document.newInstance().addDocuments(VALUE, Document.empty(), Document.empty()).addDocuments(NULL),
            doc);
    }

    /**
     * Unit test {@link NullFilterDocument#appendDocument(DocumentKey, CommonDocument)}
     */
    @Test
    public void test_appendDocument() {
        doc.addDocuments(VALUE, Document.empty(), Document.empty())
           .appendDocument(VALUE, null)
           .appendDocument(VALUE, Document.empty());

        Assert.assertEquals("appendDocument()",
            Document.newInstance().addDocuments(VALUE,  Document.empty(),  Document.empty(),  Document.empty()),
            doc);
    }

    @Nonnull
    private <T> List<T> nullList(@Nonnull Class<T> type) {
        List<T> values = new ArrayList<>();
        values.add(null);

        return values;
    }
}