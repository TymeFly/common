package com.github.tymefly.common.document.visitor.util;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import com.github.tymefly.common.document.Document;
import com.github.tymefly.common.document.key.LayeredDocumentKey;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link EntrySet}
 */
public class EntrySetTest {
    private enum Key implements LayeredDocumentKey {
        ROOT,
        X,
        Y,
        Z,
        ARRAY,
        NESTED_STRING,
        NESTED_DOCS
    }

    private final Document doc = Document.newInstance()
                .addString(Key.X, "?")
                .addNumber(Key.Y, 123)
                .addNumber(Key.Z, null)
                .addStrings(Key.ARRAY, "x", "y", "z")
                .addDocuments(Key.NESTED_DOCS, Document.newInstance().addStrings(Key.ARRAY, "A", "B", null, "D"),
                                               Document.newInstance().addNumbers(Key.ARRAY, 1, 2, 3)
                                                                     .addString(Key.X, "X"))
                .addBoolean(Key.ROOT, true);


    /**
     * Unit test {@link EntrySet}
     */
    @Test
    public void test_Empty() {
        Map<String, Object> expected = Collections.emptyMap();
        Set<EntrySet.Entry> entries = Document.newInstance()
                .accept(new EntrySet());
        Map<String, Object> actual = asMap(entries);

        Assert.assertEquals("Unexpected Entries", expected, actual);
    }


    /**
     * Unit test {@link EntrySet}
     */
    @Test
    public void test_SingleString() {
        Map<String, Object> expected = new HashMap<>() {{
            put("x", "x");
        }};
        Set<EntrySet.Entry> entries = Document.newInstance()
                .addString(Key.X, "x")
                .accept(new EntrySet());
        Map<String, Object> actual = asMap(entries);

        Assert.assertEquals("Unexpected Entries", expected, actual);
    }

    /**
     * Unit test {@link EntrySet}
     */
    @Test
    public void test_Null() {
        Map<String, Object> expected = new HashMap<>() {{
            put("x", null);
        }};
        Set<EntrySet.Entry> entries = Document.newInstance()
                .addString(Key.X, null)
                .accept(new EntrySet());
        Map<String, Object> actual = asMap(entries);

        Assert.assertEquals("Unexpected Entries", expected, actual);
    }


    /**
     * Unit test {@link EntrySet}
     */
    @Test
    public void test_SingleNumber() {
        Map<String, Object> expected = new HashMap<>() {{
            put("x", BigDecimal.valueOf(123));
        }};
        Set<EntrySet.Entry> entries = Document.newInstance()
                .addNumber(Key.X, 123)
                .accept(new EntrySet());
        Map<String, Object> actual = asMap(entries);

        Assert.assertEquals("Unexpected Entries", expected, actual);
    }


    /**
     * Unit test {@link EntrySet}
     */
    @Test
    public void test_Nested() {
        Map<String, Object> expected = new HashMap<>() {{
            put("nested.string", "Hello");
        }};
        Set<EntrySet.Entry> entries = Document.newInstance()
                .addString(Key.NESTED_STRING, "Hello")
                .accept(new EntrySet());
        Map<String, Object> actual = asMap(entries);

        Assert.assertEquals("Unexpected Entries", expected, actual);
    }


    /**
     * Unit test {@link EntrySet}
     */
    @Test
    public void test_Array() {
        Map<String, Object> expected = new HashMap<>() {{
            put("array[0]", "Hello");
            put("array[1]", "World");
        }};
        Set<EntrySet.Entry> entries = Document.newInstance()
                .addStrings(Key.ARRAY, "Hello", "World")
                .accept(new EntrySet());
        Map<String, Object> actual = asMap(entries);

        Assert.assertEquals("Unexpected Entries", expected, actual);
    }


    /**
     * Unit test {@link EntrySet}
     */
    @Test
    public void test_NestedArray() {
        Map<String, Object> expected = new HashMap<>() {{
            put("x.array[0]", "Hello");
            put("x.array[1]", "World");
        }};
        Set<EntrySet.Entry> entries = Document.newInstance()
                .addDocument(Key.X, Document.newInstance().addStrings(Key.ARRAY, "Hello", "World"))
                .accept(new EntrySet());
        Map<String, Object> actual = asMap(entries);

        Assert.assertEquals("Unexpected Entries", expected, actual);
    }


    /**
     * Unit test {@link EntrySet}
     */
    @Test
    public void test_MultipleElements() {
        Map<String, Object> expected = new HashMap<>() {{
            put("root", true);
            put("x", "?");
            put("y", BigDecimal.valueOf(123));
            put("z", null);
            put("array[2]", "z");
            put("array[1]", "y");
            put("array[0]", "x");
            put("nested.docs[1].x", "X");
            put("nested.docs[1].array[0]", BigDecimal.valueOf(1));
            put("nested.docs[1].array[1]", BigDecimal.valueOf(2));
            put("nested.docs[1].array[2]", BigDecimal.valueOf(3));
            put("nested.docs[0].array[3]", "D");
            put("nested.docs[0].array[2]", null);
            put("nested.docs[0].array[1]", "B");
            put("nested.docs[0].array[0]", "A");
        }};
        Set<EntrySet.Entry> entries = doc.accept(new EntrySet());
        Map<String, Object> actual = asMap(entries);

        Assert.assertEquals("Unexpected Entries", expected, actual);
    }

    /**
     * Unit test {@link EntrySet}
     */
    @Test
    public void test_NoNulls() {
        Map<String, Object> expected = new HashMap<>() {{
            put("root", true);
            put("x", "?");
            put("y", BigDecimal.valueOf(123));
            put("array[2]", "z");
            put("array[1]", "y");
            put("array[0]", "x");
            put("nested.docs[1].x", "X");
            put("nested.docs[1].array[0]", BigDecimal.valueOf(1));
            put("nested.docs[1].array[1]", BigDecimal.valueOf(2));
            put("nested.docs[1].array[2]", BigDecimal.valueOf(3));
            put("nested.docs[0].array[3]", "D");
            put("nested.docs[0].array[1]", "B");
            put("nested.docs[0].array[0]", "A");
        }};
        Set<EntrySet.Entry> entries = doc.accept(new EntrySet(VisitorOptions.RECURSIVE));
        Map<String, Object> actual = asMap(entries);

        Assert.assertEquals("Unexpected Entries", expected, actual);
    }


    /**
     * Unit test {@link EntrySet}
     */
    @Test
    public void test_TopLevelOnly_WithNulls() {
        Map<String, Object> expected = new HashMap<>() {{
            put("root", true);
            put("x", "?");
            put("y", BigDecimal.valueOf(123));
            put("z", null);
            put("array[2]", "z");
            put("array[1]", "y");
            put("array[0]", "x");
        }};
        Set<EntrySet.Entry> entries = doc.accept(new EntrySet(VisitorOptions.INCLUDE_NULL));
        Map<String, Object> actual = asMap(entries);

        Assert.assertEquals("Unexpected Entries", expected, actual);
    }

    /**
     * Unit test {@link EntrySet}
     */
    @Test
    public void test_TopLevelOnly_NoNulls() {
        Map<String, Object> expected = new HashMap<>() {{
            put("root", true);
            put("x", "?");
            put("y", BigDecimal.valueOf(123));
            put("array[2]", "z");
            put("array[1]", "y");
            put("array[0]", "x");
        }};
        Set<EntrySet.Entry> entries = doc.accept(new EntrySet(EnumSet.noneOf(VisitorOptions.class)));
        Map<String, Object> actual = asMap(entries);

        Assert.assertEquals("Unexpected Entries", expected, actual);
    }

    /**
     * Unit test {@link EntrySet}
     */
    @Test
    public void test_WithSequenceNames() {
        Assert.assertThrows(IllegalArgumentException.class,
                () -> doc.accept(new EntrySet(VisitorOptions.INCLUDE_SEQUENCE_NAMES)));
    }

    /**
     * Unit test {@link EntrySet}
     */
    @Test
    public void test_WithChildNames() {
        Assert.assertThrows(IllegalArgumentException.class,
                () -> doc.accept(new EntrySet(VisitorOptions.INCLUDE_CHILD_NAMES)));
    }


    @Nonnull
    private Map<String, Object> asMap(@Nonnull Set<EntrySet.Entry> entries) {
        Map<String, Object> actual = new HashMap<>();

        for (EntrySet.Entry entry : entries) {
            actual.put(entry.getKey().externalise(), entry.getValue());
        }

        return actual;
    }
}