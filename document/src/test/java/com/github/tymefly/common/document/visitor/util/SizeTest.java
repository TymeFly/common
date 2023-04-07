package com.github.tymefly.common.document.visitor.util;

import java.util.EnumSet;

import com.github.tymefly.common.document.Document;
import com.github.tymefly.common.document.key.LayeredDocumentKey;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link Size}
 */
public class SizeTest {
    private enum Key implements LayeredDocumentKey {
        ROOT,
        X,
        Y,
        Z,
        ARRAY,
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
    
    private enum Type { ELEMENT }

    /**
     * Unit test {@link Size}
     */
    @Test
    public void test_Size() {
        Assert.assertEquals("empty", 0, (int) Document.empty().accept(new Size()));
        Assert.assertEquals("single element",
                1,
                (int) Document.newInstance()
                        .addString(() -> "foo", "bar")
                        .accept(new Size()));
        Assert.assertEquals("multiple elements",
                5,
                (int) Document.newInstance()
                        .addString(() -> "String", "bar")
                        .addString(() -> "null", null)
                        .addNumber(() -> "num", 12)
                        .addBoolean(() -> "bool", true)
                        .addEnum(() -> "enum", Type.ELEMENT)
                        .accept(new Size()));
        Assert.assertEquals("sequence",
                3,
                (int) Document.newInstance()
                        .addStrings(() -> "foo", "one", "two", "Three")
                        .accept(new Size()));
        Assert.assertEquals("child Documents",
                5,
                (int) Document.newInstance()
                        .addString(() -> "root.String", "bar")
                        .addString(() -> "root.null", null)
                        .addNumber(() -> "other.num", 12)
                        .addBoolean(() -> "other.bool", true)
                        .addEnum(() -> "parent.child.enum", Type.ELEMENT)
                        .accept(new Size()));
        Assert.assertEquals("Child Documents and sequences",
                12,
                (int) Document.newInstance()
                        .addStrings(() -> "String", "1", "2", null)
                        .addString(() -> "root.null", null)
                        .addNumbers(() -> "other.num", 1, 2, 3, 4, 5)
                        .addBoolean(() -> "other.bool", true)
                        .addEnums(() -> "parent.child.enum", Type.ELEMENT, null)
                        .accept(new Size()));
    }


    /**
     * Unit test {@link Size}
     */
    @Test
    public void test_MultipleElements() {
        int actual = doc.accept(new Size());

        Assert.assertEquals("Unexpected Keys", 15, actual);
    }

    /**
     * Unit test {@link Size}
     */
    @Test
    public void test_NoNulls() {
        int actual = doc.accept(new Size(VisitorOptions.RECURSIVE));

        Assert.assertEquals("Unexpected Keys", 13, actual);
    }


    /**
     * Unit test {@link Size}
     */
    @Test
    public void test_TopLevelOnly_WithNulls() {
        int actual = doc.accept(new Size(VisitorOptions.INCLUDE_NULL));

        Assert.assertEquals("Unexpected Keys", 7, actual);
    }

    /**
     * Unit test {@link Size}
     */
    @Test
    public void test_TopLevelOnly_NoNulls() {
        int actual = doc.accept(new Size(EnumSet.noneOf(VisitorOptions.class)));

        Assert.assertEquals("Unexpected Keys", 6, actual);
    }


    /**
     * Unit test {@link Size}
     */
    @Test
    public void test_WithSequenceNames() {
        EnumSet<VisitorOptions> options = EnumSet.of(VisitorOptions.RECURSIVE,
                                                     VisitorOptions.INCLUDE_NULL,
                                                     VisitorOptions.INCLUDE_SEQUENCE_NAMES);
        int actual = doc.accept(new Size(options));

        Assert.assertEquals("Unexpected Keys", 19, actual);
    }

    /**
     * Unit test {@link Size}
     */
    @Test
    public void test_WithChildNames() {
        EnumSet<VisitorOptions> options = EnumSet.of(VisitorOptions.RECURSIVE,
                                                     VisitorOptions.INCLUDE_NULL,
                                                     VisitorOptions.INCLUDE_CHILD_NAMES);
        int actual = doc.accept(new Size(options));

        Assert.assertEquals("Unexpected Keys", 18, actual);
    }


    /**
     * Unit test {@link Size}
     */
    @Test
    public void test_Defined_Children() {
        EnumSet<VisitorOptions> options = EnumSet.of(VisitorOptions.INCLUDE_SEQUENCE_NAMES,
                                                     VisitorOptions.INCLUDE_CHILD_NAMES);
        int actual = doc.accept(new Size(options));

        Assert.assertEquals("Unexpected Keys", 8, actual);
    }

    /**
     * Unit test {@link Size}
     */
    @Test
    public void test_Everything() {
        int actual = doc.accept(new Size(EnumSet.allOf(VisitorOptions.class)));

        Assert.assertEquals("Unexpected Keys", 22, actual);
    }
}