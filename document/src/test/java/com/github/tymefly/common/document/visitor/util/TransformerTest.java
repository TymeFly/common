package com.github.tymefly.common.document.visitor.util;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.github.tymefly.common.document.Document;
import com.github.tymefly.common.document.key.DocumentKey;
import com.github.tymefly.common.document.key.LayeredDocumentKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link Transformer}
 */
public class TransformerTest {
    private enum Key implements LayeredDocumentKey {
        STR,
        STR2,
        NUM,
        BOOL,
        ENUM,
        NULL,
        PARENT,
        PARENT_CHILD1,
        PARENT_CHILD2,
        PARENT_NULL,
        ARRAY_DOCS,
        ARRAY_NULLS,
        ARRAY_BOOLS,
        EMPTY_DOC,
        EMPTY_SEQUENCE,
        REMAP
    }

    private enum Type {
        ONE, TWO, THREE
    }

    private Document source;


    @Before
    public void setUp() {
        source = Document.newInstance()
            .addDocument(Key.EMPTY_DOC, Document.empty())
            .addStrings(Key.EMPTY_SEQUENCE)
            .addString(Key.STR, "Hello")
            .addString(Key.STR2, "World")
            .addNumber(Key.NUM, 123)
            .addBoolean(Key.BOOL, true)
            .addEnum(Key.ENUM, Type.ONE)
            .addString(Key.NULL, null)
            .addString(Key.PARENT_CHILD1, "foo")
            .addString(Key.PARENT_CHILD2, "bar")
            .addString(Key.PARENT_NULL, null)
            .addDocuments(Key.ARRAY_DOCS,
                Document.empty(),
                Document.newInstance().addString(Key.STR, "XXX"),
                Document.empty(),
                null)
            .addNumbers(Key.ARRAY_NULLS, null, null, null)
            .addBooleans(Key.ARRAY_BOOLS, true, null, false);
    }

    /**
     * Unit test {@link Transformer.Builder#remove(DocumentKey)} to remove items
     */
    @Test
    public void test_remove_SingleFields() {
        Document expected = Document.newInstance()
            .addDocument(Key.EMPTY_DOC, Document.empty())
            .addStrings(Key.EMPTY_SEQUENCE)
            .addString(Key.STR, "Hello")
            .addNumber(Key.NUM, 123)
            .addBoolean(Key.BOOL, true)
            .addEnum(Key.ENUM, Type.ONE)
            .addString(Key.NULL, null)
            .addString(Key.PARENT_CHILD1, "foo")
            .addString(Key.PARENT_CHILD2, "bar")
            .addString(Key.PARENT_NULL, null)
            .addDocuments(Key.ARRAY_DOCS,
                Document.empty(),
                Document.newInstance().addString(Key.STR, "XXX"),
                Document.empty(),
                null)
            .addNumbers(Key.ARRAY_NULLS, null, null, null)
            .addBooleans(Key.ARRAY_BOOLS, true, null, false);
        Transformer transformer = new Transformer.Builder()
            .remove(Key.STR2)
            .build();
        Document actual = source.accept(transformer);

        Assert.assertEquals("Unexpected Transformed Document", expected, actual);
    }

    /**
     * Unit test {@link Transformer.Builder#remove(DocumentKey)} to remove items
     */
    @Test
    public void test_remove_MultipleFields() {
        Document expected = Document.newInstance()
            .addStrings(Key.EMPTY_SEQUENCE)
            .addString(Key.STR2, "World")
            .addDocument(Key.EMPTY_DOC, Document.empty())
            .addNumber(Key.NUM, 123)
            .addBoolean(Key.BOOL, true)
            .addString(Key.NULL, null)
            .addString(Key.PARENT_CHILD1, "foo")
            .addString(Key.PARENT_NULL, null)
            .addDocuments(Key.ARRAY_DOCS,
                Document.empty(),
                Document.newInstance().addString(Key.STR, "XXX"),
                Document.empty(),
                null)
            .addNumbers(Key.ARRAY_NULLS, null, null, null)
            .addBooleans(Key.ARRAY_BOOLS, true, null, false);
        Transformer transformer = new Transformer.Builder()
            .remove(Key.STR)
            .remove(Key.ENUM)
            .remove(Key.PARENT_CHILD2)
            .build();
        Document actual = source.accept(transformer);

        Assert.assertEquals("Unexpected Transformed Document", expected, actual);
    }

    /**
     * Unit test {@link Transformer.Builder#remove(DocumentKey)} to remove items
     */
    @Test
    public void test_remove_childDocument() {
        Document expected = Document.newInstance()
            .addDocument(Key.EMPTY_DOC, Document.empty())
            .addStrings(Key.EMPTY_SEQUENCE)
            .addString(Key.STR, "Hello")
            .addString(Key.STR2, "World")
            .addNumber(Key.NUM, 123)
            .addBoolean(Key.BOOL, true)
            .addEnum(Key.ENUM, Type.ONE)
            .addString(Key.NULL, null)
            .addDocuments(Key.ARRAY_DOCS,
                Document.empty(),
                Document.newInstance().addString(Key.STR, "XXX"),
                Document.empty(),
                null)
            .addNumbers(Key.ARRAY_NULLS, null, null, null)
            .addBooleans(Key.ARRAY_BOOLS, true, null, false);
        Transformer transformer = new Transformer.Builder()
            .remove(Key.PARENT)
            .build();
        Document actual = source.accept(transformer);

        Assert.assertEquals("Unexpected Transformed Document", expected, actual);
    }

    /**
     * Unit test {@link Transformer.Builder#remove(DocumentKey)} to remove items
     */
    @Test
    public void test_remove_fromArray() {
        Document expected = Document.newInstance()
            .addDocument(Key.EMPTY_DOC, Document.empty())
            .addStrings(Key.EMPTY_SEQUENCE)
            .addString(Key.STR, "Hello")
            .addString(Key.STR2, "World")
            .addNumber(Key.NUM, 123)
            .addBoolean(Key.BOOL, true)
            .addEnum(Key.ENUM, Type.ONE)
            .addString(Key.NULL, null)
            .addString(Key.PARENT_NULL, null)
            .addString(Key.PARENT_CHILD1, "foo")
            .addString(Key.PARENT_CHILD2, "bar")
            .addString(Key.PARENT_NULL, null)
            .addNumbers(Key.ARRAY_NULLS, null, null, null)
            .addBooleans(Key.ARRAY_BOOLS, true, null, false);
        Transformer transformer = new Transformer.Builder()
            .remove(Key.ARRAY_DOCS)
            .build();
        Document actual = source.accept(transformer);

        Assert.assertEquals("Unexpected Transformed Document", expected, actual);
    }

    /**
     * Unit test {@link Transformer.Builder#removeNullFields()} to remove null fields
     */
    @Test
    public void test_removeNullFields() {
        Document expected = Document.newInstance()
            .addDocument(Key.EMPTY_DOC, Document.empty())
            .addStrings(Key.EMPTY_SEQUENCE)
            .addString(Key.STR, "Hello")
            .addString(Key.STR2, "World")
            .addNumber(Key.NUM, 123)
            .addBoolean(Key.BOOL, true)
            .addEnum(Key.ENUM, Type.ONE)
            .addString(Key.PARENT_CHILD1, "foo")
            .addString(Key.PARENT_CHILD2, "bar")
            .addDocuments(Key.ARRAY_DOCS,
                Document.empty(),
                Document.newInstance().addString(Key.STR, "XXX"),
                Document.empty())
            .addNumbers(Key.ARRAY_NULLS)
            .addBooleans(Key.ARRAY_BOOLS, true, null, false);
        Transformer transformer = new Transformer.Builder()
            .removeNullFields()
            .build();
        Document actual = source.accept(transformer);

        Assert.assertEquals("Unexpected Transformed Document", expected, actual);
    }

    /**
     * Unit test {@link Transformer.Builder#remap(DocumentKey, Function)} to rename specific fields
     */
    @Test
    public void test_Remap_fields() {
        Document expected = Document.newInstance()
            .addDocument(DocumentKey.from(Key.REMAP, Key.EMPTY_DOC), Document.empty())
            .addStrings(Key.EMPTY_SEQUENCE)
            .addString(DocumentKey.from(Key.REMAP, Key.STR), "Hello")
            .addString(Key.STR2, "World")
            .addNumber(DocumentKey.from(Key.REMAP, Key.NUM), 123)
            .addBoolean(DocumentKey.from(Key.REMAP, Key.BOOL), true)
            .addEnum(DocumentKey.from(Key.REMAP, Key.ENUM), Type.ONE)
            .addString(DocumentKey.from(Key.REMAP, Key.NULL), null)
            .addString(Key.PARENT_CHILD1, "foo")
            .addString(Key.PARENT_CHILD2, "bar")
            .addString(Key.PARENT_NULL, null)
            .addDocuments(Key.ARRAY_DOCS,
                Document.empty(),
                Document.newInstance().addString(Key.STR, "XXX"),
                Document.empty(),
                null)
            .addNumbers(Key.ARRAY_NULLS, null, null, null)
            .addBooleans(Key.ARRAY_BOOLS, true, null, false);
        Function<DocumentKey, DocumentKey> remapping = k -> DocumentKey.from(Key.REMAP, k);
        Transformer transformer = new Transformer.Builder()
            .remap(Key.STR, remapping)
            .remap(Key.NUM, remapping)
            .remap(Key.BOOL, remapping)
            .remap(Key.ENUM, remapping)
            .remap(Key.NULL, remapping)
            .remap(Key.EMPTY_DOC, remapping)
            .build();
        Document actual = source.accept(transformer);

        Assert.assertEquals("Unexpected Transformed Document", expected, actual);
    }

    /**
     * Unit test {@link Transformer.Builder#remap(DocumentKey, Function)} to remap a sequence of fields
     */
    @Test
    public void test_Remap_Sequence() {
        Document expected = Document.newInstance()
            .addDocument(Key.EMPTY_DOC, Document.empty())
            .addStrings(Key.EMPTY_SEQUENCE)
            .addString(Key.STR, "Hello")
            .addString(Key.STR2, "World")
            .addNumber(Key.NUM, 123)
            .addBoolean(Key.BOOL, true)
            .addEnum(Key.ENUM, Type.ONE)
            .addString(Key.NULL, null)
            .addString(Key.PARENT_CHILD1, "foo")
            .addString(Key.PARENT_CHILD2, "bar")
            .addString(Key.PARENT_NULL, null)
            .addDocuments(Key.ARRAY_DOCS,
                Document.empty(),
                Document.newInstance().addString(Key.STR, "XXX"),
                Document.empty(),
                null)
            .addNumbers(Key.ARRAY_NULLS, null, null, null)
            .addBooleans(DocumentKey.from(Key.REMAP, Key.ARRAY_BOOLS), true, null, false);
        Function<DocumentKey, DocumentKey> remapping = k -> DocumentKey.from(Key.REMAP, k);
        Transformer transformer = new Transformer.Builder()
            .remap(Key.ARRAY_BOOLS, remapping)
            .build();
        Document actual = source.accept(transformer);

        Assert.assertEquals("Unexpected Transformed Document", expected, actual);
    }

    /**
     * Unit test {@link Transformer.Builder#remap(DocumentKey, Function)} to remap all fields in a child document
     */
    @Test
    public void test_Remap_ChildDocument() {
        Document expected = Document.newInstance()
            .addDocument(Key.EMPTY_DOC, Document.empty())
            .addStrings(Key.EMPTY_SEQUENCE)
            .addString(Key.STR, "Hello")
            .addString(Key.STR2, "World")
            .addNumber(Key.NUM, 123)
            .addBoolean(Key.BOOL, true)
            .addEnum(Key.ENUM, Type.ONE)
            .addString(Key.NULL, null)
            .addString(DocumentKey.from(Key.REMAP, Key.PARENT_CHILD1), "foo")
            .addString(DocumentKey.from(Key.REMAP, Key.PARENT_CHILD2), "bar")
            .addString(DocumentKey.from(Key.REMAP, Key.PARENT_NULL), null)
            .addDocuments(Key.ARRAY_DOCS,
                Document.empty(),
                Document.newInstance().addString(Key.STR, "XXX"),
                Document.empty(),
                null)
            .addNumbers(Key.ARRAY_NULLS, null, null, null)
            .addBooleans(Key.ARRAY_BOOLS, true, null, false);
        Function<DocumentKey, DocumentKey> remapping = k -> DocumentKey.from(Key.REMAP, k);
        Transformer transformer = new Transformer.Builder()
            .remap(Key.PARENT, remapping)
            .build();
        Document actual = source.accept(transformer);

        Assert.assertEquals("Unexpected Transformed Document", expected, actual);
    }

    /**
     * Unit test {@link Transformer.Builder#remapStrings(BiFunction)}
     */
    @Test
    public void test_remapStrings() {
        Document expected = Document.newInstance()
            .addDocument(Key.EMPTY_DOC, Document.empty())
            .addStrings(Key.EMPTY_SEQUENCE)
            .addString(Key.STR, "~str_hello~")
            .addString(Key.STR2, "~str2_world~")
            .addNumber(Key.NUM, 123)
            .addBoolean(Key.BOOL, true)
            .addEnum(Key.ENUM, Type.ONE)
            .addString(Key.NULL, null)
            .addString(Key.PARENT_CHILD1, "~parent.child1_foo~")
            .addString(Key.PARENT_CHILD2, "~parent.child2_bar~")
            .addString(Key.PARENT_NULL, null)
            .addDocuments(Key.ARRAY_DOCS,
                Document.empty(),
                Document.newInstance().addString(Key.STR, "~array.docs[1].str_xxx~"),
                Document.empty(),
                null)
            .addNumbers(Key.ARRAY_NULLS, null, null, null)
            .addBooleans(Key.ARRAY_BOOLS, true, null, false);
        Transformer transformer = new Transformer.Builder()
            .remapStrings((k, v) -> v.toUpperCase())                    // will be undone - The order is significant
            .remapStrings((k, v) -> k.externalise() + "_" + v)
            .remapStrings((k, v) -> "~" + v.toLowerCase() + "~")
            .build();
        Document actual = source.accept(transformer);

        Assert.assertEquals("Unexpected Transformed Document", expected, actual);
    }

    /**
     * Unit test {@link Transformer.Builder#remapNumbers(BiFunction)}
     */
    @Test
    public void test_remapNumbers() {
        Document expected = Document.newInstance()
            .addDocument(Key.EMPTY_DOC, Document.empty())
            .addStrings(Key.EMPTY_SEQUENCE)
            .addString(Key.STR, "Hello")
            .addString(Key.STR2, "World")
            .addNumber(Key.NUM, 1237)
            .addBoolean(Key.BOOL, true)
            .addEnum(Key.ENUM, Type.ONE)
            .addString(Key.NULL, null)
            .addString(Key.PARENT_CHILD1, "foo")
            .addString(Key.PARENT_CHILD2, "bar")
            .addString(Key.PARENT_NULL, null)
            .addDocuments(Key.ARRAY_DOCS,
                Document.empty(),
                Document.newInstance().addString(Key.STR, "XXX"),
                Document.empty(),
                null)
            .addNumbers(Key.ARRAY_NULLS, null, null, null)
            .addBooleans(Key.ARRAY_BOOLS, true, null, false);
        Transformer transformer = new Transformer.Builder()
            .remapNumbers((k, v) -> v.intValue() * 10)
            .remapNumbers((k, v) -> v.intValue() + 7)
            .build();
        Document actual = source.accept(transformer);

        Assert.assertEquals("Unexpected Transformed Document", expected, actual);
    }

    /**
     * Unit test {@link Transformer.Builder#remapBooleans(BiFunction)}
     */
    @Test
    public void test_remapBoolean() {
        Document expected = Document.newInstance()
            .addDocument(Key.EMPTY_DOC, Document.empty())
            .addStrings(Key.EMPTY_SEQUENCE)
            .addString(Key.STR, "Hello")
            .addString(Key.STR2, "World")
            .addNumber(Key.NUM, 123)
            .addBoolean(Key.BOOL, false)
            .addEnum(Key.ENUM, Type.ONE)
            .addString(Key.NULL, null)
            .addString(Key.PARENT_CHILD1, "foo")
            .addString(Key.PARENT_CHILD2, "bar")
            .addString(Key.PARENT_NULL, null)
            .addDocuments(Key.ARRAY_DOCS,
                Document.empty(),
                Document.newInstance().addString(Key.STR, "XXX"),
                Document.empty(),
                null)
            .addNumbers(Key.ARRAY_NULLS, null, null, null)
            .addBooleans(Key.ARRAY_BOOLS, false, null, true);
        Transformer transformer = new Transformer.Builder()
            .remapBooleans((k, v) -> !v)
            .build();
        Document actual = source.accept(transformer);

        Assert.assertEquals("Unexpected Transformed Document", expected, actual);
    }

    /**
     * Unit test {@link Transformer.Builder#remapEnums(BiFunction)}
     */
    @Test
    public void test_remapEnum() {
        Document expected = Document.newInstance()
            .addDocument(Key.EMPTY_DOC, Document.empty())
            .addStrings(Key.EMPTY_SEQUENCE)
            .addString(Key.STR, "Hello")
            .addString(Key.STR2, "World")
            .addNumber(Key.NUM, 123)
            .addBoolean(Key.BOOL, true)
            .addEnum(Key.ENUM, Type.THREE)
            .addString(Key.NULL, null)
            .addString(Key.PARENT_CHILD1, "foo")
            .addString(Key.PARENT_CHILD2, "bar")
            .addString(Key.PARENT_NULL, null)
            .addDocuments(Key.ARRAY_DOCS,
                Document.empty(),
                Document.newInstance().addString(Key.STR, "XXX"),
                Document.empty(),
                null)
            .addNumbers(Key.ARRAY_NULLS, null, null, null)
            .addBooleans(Key.ARRAY_BOOLS, true, null, false);
        BiFunction<DocumentKey, Enum<?>, Enum<?>> remap = (k, v) -> v.getClass().getEnumConstants()[v.ordinal() + 1];
        Transformer transformer = new Transformer.Builder()
            .remapEnums(remap)
            .remapEnums(remap)
            .build();
        Document actual = source.accept(transformer);

        Assert.assertEquals("Unexpected Transformed Document", expected, actual);
    }
}