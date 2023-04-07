package com.github.tymefly.common.document.visitor.util;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import com.github.tymefly.common.document.Document;
import com.github.tymefly.common.document.key.LayeredDocumentKey;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link PathSet}
 */
public class PathSetTest {
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
     * Unit test {@link PathSet}
     */
    @Test
    public void test_Empty() {
        Set<String> expected = Collections.emptySet();
        Set<String> actual = Document.newInstance()
                .accept(new PathSet());

        Assert.assertEquals("Unexpected Keys", expected, actual);
    }


    /**
     * Unit test {@link PathSet}
     */
    @Test
    public void test_SingleString() {
        Set<String> expected = Set.of("x");
        Set<String> actual = Document.newInstance()
                .addString(Key.X, "x")
                .accept(new PathSet());

        Assert.assertEquals("Unexpected Keys", expected, actual);
    }

    /**
     * Unit test {@link PathSet}
     */
    @Test
    public void test_Null() {
        Set<String> expected = Set.of("x");
        Set<String> actual = Document.newInstance()
                .addString(Key.X, null)
                .accept(new PathSet());

        Assert.assertEquals("Unexpected Keys", expected, actual);
    }


    /**
     * Unit test {@link PathSet}
     */
    @Test
    public void test_SingleNumber() {
        Set<String> expected = Set.of("x");
        Set<String> actual = Document.newInstance()
                .addNumber(Key.X, 123)
                .accept(new PathSet());

        Assert.assertEquals("Unexpected Keys", expected, actual);
    }


    /**
     * Unit test {@link PathSet}
     */
    @Test
    public void test_Nested() {
        Set<String> expected = Set.of("nested.string");
        Set<String> actual = Document.newInstance()
                .addString(Key.NESTED_STRING, "Hello")
                .accept(new PathSet());

        Assert.assertEquals("Unexpected Keys", expected, actual);
    }


    /**
     * Unit test {@link PathSet}
     */
    @Test
    public void test_Array() {
        Set<String> expected = Set.of("array[0]");
        Set<String> actual = Document.newInstance()
                .addStrings(Key.ARRAY, "Hello")
                .accept(new PathSet());

        Assert.assertEquals("Unexpected Keys", expected, actual);
    }


    /**
     * Unit test {@link PathSet}
     */
    @Test
    public void test_NestedArray() {
        Set<String> expected = Set.of("x.array[0]");
        Set<String> actual = Document.newInstance()
                .addDocument(Key.X, Document.newInstance().addStrings(Key.ARRAY, "Hello"))
                .accept(new PathSet());

        Assert.assertEquals("Unexpected Keys", expected, actual);
    }


    /**
     * Unit test {@link PathSet}
     */
    @Test
    public void test_MultipleElements() {
        Set<String> expected = Set.of("x", "y", "z",
                                      "array[0]", "array[1]", "array[2]",
                                      "nested.docs[0].array[0]",
                                            "nested.docs[0].array[1]",
                                            "nested.docs[0].array[2]",
                                            "nested.docs[0].array[3]",
                                      "nested.docs[1].array[0]",
                                            "nested.docs[1].array[1]",
                                            "nested.docs[1].array[2]",
                                            "nested.docs[1].x",
                                      "root");
        Set<String> actual = doc.accept(new PathSet());

        Assert.assertEquals("Unexpected Keys", expected, actual);
    }

    /**
     * Unit test {@link PathSet}
     */
    @Test
    public void test_NoNulls() {
        Set<String> expected = Set.of("x", "y",
                                      "array[0]", "array[1]", "array[2]",
                                      "nested.docs[0].array[0]",
                                            "nested.docs[0].array[1]",
                                            "nested.docs[0].array[3]",
                                      "nested.docs[1].array[0]",
                                            "nested.docs[1].array[1]",
                                            "nested.docs[1].array[2]",
                                            "nested.docs[1].x",
                                      "root");
        Set<String> actual = doc.accept(new PathSet(VisitorOptions.RECURSIVE));

        Assert.assertEquals("Unexpected Keys", expected, actual);
    }


    /**
     * Unit test {@link PathSet}
     */
    @Test
    public void test_TopLevelOnly_WithNulls() {
        Set<String> expected = Set.of("x", "y", "z",
                                      "array[0]", "array[1]", "array[2]",
                                      "root");
        Set<String> actual = doc.accept(new PathSet(VisitorOptions.INCLUDE_NULL));

        Assert.assertEquals("Unexpected Keys", expected, actual);
    }

    /**
     * Unit test {@link PathSet}
     */
    @Test
    public void test_TopLevelOnly_NoNulls() {
        Set<String> expected = Set.of("x", "y",
                                      "array[0]", "array[1]", "array[2]",
                                      "root");
        Set<String> actual = doc.accept(new PathSet(EnumSet.noneOf(VisitorOptions.class)));

        Assert.assertEquals("Unexpected Keys", expected, actual);
    }


    /**
     * Unit test {@link PathSet}
     */
    @Test
    public void test_WithSequenceNames() {
        Set<String> expected = Set.of("x", "y", "z",
                                      "array", "array[0]", "array[1]", "array[2]",
                                      "nested.docs",
                                            "nested.docs[0].array",
                                            "nested.docs[0].array[0]",
                                            "nested.docs[0].array[1]",
                                            "nested.docs[0].array[2]",
                                            "nested.docs[0].array[3]",
                                      "nested.docs[1].array[0]",
                                            "nested.docs[1].array",
                                            "nested.docs[1].array[1]",
                                            "nested.docs[1].array[2]",
                                            "nested.docs[1].x",
                                      "root");
        EnumSet<VisitorOptions> options = EnumSet.of(VisitorOptions.RECURSIVE,
                                                     VisitorOptions.INCLUDE_NULL,
                                                     VisitorOptions.INCLUDE_SEQUENCE_NAMES);
        Set<String> actual = doc.accept(new PathSet(options));

        Assert.assertEquals("Unexpected Keys", expected, actual);
    }

    /**
     * Unit test {@link PathSet}
     */
    @Test
    public void test_WithChildNames() {
        Set<String> expected = Set.of("x", "y", "z",
                                      "array[0]", "array[1]", "array[2]",
                                      "nested.docs[0].array[0]",
                                            "nested.docs[0].array[1]",
                                            "nested.docs[0].array[2]",
                                            "nested.docs[0].array[3]",
                                      "nested",
                                      "nested.docs[0]",
                                      "nested.docs[1]",
                                      "nested.docs[1].array[0]",
                                            "nested.docs[1].array[1]",
                                            "nested.docs[1].array[2]",
                                            "nested.docs[1].x",
                                      "root");
        EnumSet<VisitorOptions> options = EnumSet.of(VisitorOptions.RECURSIVE,
                                                     VisitorOptions.INCLUDE_NULL,
                                                     VisitorOptions.INCLUDE_CHILD_NAMES);
        Set<String> actual = doc.accept(new PathSet(options));

        Assert.assertEquals("Unexpected Keys", expected, actual);
    }


    /**
     * Unit test {@link PathSet}
     */
    @Test
    public void test_Defined_Children() {
        Set<String> expected = Set.of("x", "y",
                                      "array",
                                      "array[0]", "array[1]", "array[2]",
                                      "nested",
                                      "root");
        EnumSet<VisitorOptions> options = EnumSet.of(VisitorOptions.INCLUDE_SEQUENCE_NAMES,
                                                     VisitorOptions.INCLUDE_CHILD_NAMES);
        Set<String> actual = doc.accept(new PathSet(options));

        Assert.assertEquals("Unexpected Keys", expected, actual);
    }

    /**
     * Unit test {@link PathSet}
     */
    @Test
    public void test_Everything() {
        Set<String> expected = Set.of("x", "y", "z",
                                      "array",
                                      "array[0]", "array[1]", "array[2]",
                                      "nested.docs[0].array[0]",
                                            "nested.docs[0].array[1]",
                                            "nested.docs[0].array[2]",
                                            "nested.docs[0].array[3]",
                                      "nested",
                                      "nested.docs",
                                      "nested.docs[0]",
                                      "nested.docs[1]",
                                      "nested.docs[0].array",
                                      "nested.docs[1].array",
                                            "nested.docs[1].array[0]",
                                            "nested.docs[1].array[1]",
                                            "nested.docs[1].array[2]",
                                            "nested.docs[1].x",
                                      "root");
        Set<String> actual = doc.accept(new PathSet(EnumSet.allOf(VisitorOptions.class)));

        Assert.assertEquals("Unexpected Keys", expected, actual);
    }
}