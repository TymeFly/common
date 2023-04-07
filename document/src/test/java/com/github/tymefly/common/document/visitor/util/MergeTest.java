package com.github.tymefly.common.document.visitor.util;

import java.util.Collections;
import java.util.List;

import com.github.tymefly.common.document.Document;
import com.github.tymefly.common.document.key.FlatDocumentKey;
import com.github.tymefly.common.document.key.LayeredDocumentKey;
import com.github.tymefly.common.document.visitor.serializer.json.JsonSerializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link Merge}
 */
public class MergeTest {
    private enum Value {
        A, B, C
    }

    private enum Key implements LayeredDocumentKey {
        ROOT_CHILD_STRING,
        ROOT_CHILD_NUMBER,
        ROOT_CHILD_BOOLEAN,
        ROOT_CHILD_ENUM,
        ROOT_STRINGS,
        ROOT_NUMBERS,
        ROOT_BOOLEANS,
        ROOT_ENUMS,
        ROOT_DOCS,
        STRING,
        NUMBER,
        EXTRA
    }

    private enum Nested implements FlatDocumentKey {
        ONE,
        TWO,
        THREE,
        DATA
    }


    private Document source;


    @Before
    public void setUp() {
        source = Document.newInstance()
                .addString(Key.ROOT_CHILD_STRING, "String")
                .addNumber(Key.ROOT_CHILD_NUMBER, 123)
                .addBoolean(Key.ROOT_CHILD_BOOLEAN, true)
                .addEnum(Key.ROOT_CHILD_ENUM, Value.A)
                .addStrings(Key.ROOT_STRINGS, "One", null, "Three")
                .addNumbers(Key.ROOT_NUMBERS, 1, null, 3.4)
                .addBooleans(Key.ROOT_BOOLEANS, true, null, false)
                .addEnums(Key.ROOT_ENUMS, Value.B, null, Value.C)
                .addDocuments(Key.ROOT_DOCS,
                        Document.newInstance().addString(Nested.ONE, "One"),
                        Document.newInstance().addString(Nested.TWO, null)
                                .addStrings(Nested.DATA, "x", "y", "z"),
                        Document.newInstance().addString(Nested.THREE, "Three")
                                .addStrings(Nested.DATA, (List<String>) null));
    }

    /**
     * Unit test {@link JsonSerializer}
     */
    @Test
    public void test_Merge_AllTypes_WithEmpty() {
        Document actual = source.accept(new Merge(Document.empty()));

        Assert.assertNotSame("Same instance returned", source, actual);
        Assert.assertEquals("Unexpected Merge", source, actual);
    }

    /**
     * Unit test {@link JsonSerializer}
     */
    @Test
    public void test_Merge_AllTypes_WithSelf() {
        Document actual = source.accept(new Merge(source));

        Assert.assertNotSame("Same instance returned", source, actual);
        Assert.assertEquals("Unexpected Merge", source, actual);
    }


    /**
     * Unit test {@link JsonSerializer}
     */
    @Test
    public void test_Merge_MutuallyExclusive() {
        Document left = Document.newInstance()
                .addString(Key.STRING, "Hello");
        Document right = Document.newInstance()
                .addNumber(Key.NUMBER, 12);
        Document expected = Document.newInstance()
                .addString(Key.STRING, "Hello")
                .addNumber(Key.NUMBER, 12);
        Document actual = right.accept(new Merge(left));

        Assert.assertEquals("Unexpected Merge", expected, actual);
    }

    /**
     * Unit test {@link JsonSerializer}
     */
    @Test
    public void test_Merge_KeyClash() {
        Document left = Document.newInstance()
                .addString(Key.STRING, "Hello")
                .addString(Key.EXTRA, "left");
        Document right = Document.newInstance()
                .addNumber(Key.NUMBER, 12)
                .addString(Key.EXTRA, "right");
        Document leftOverRight = Document.newInstance()
                .addString(Key.STRING, "Hello")
                .addNumber(Key.NUMBER, 12)
                .addString(Key.EXTRA, "left");
        Document rightOverLeft = Document.newInstance()
                .addString(Key.STRING, "Hello")
                .addNumber(Key.NUMBER, 12)
                .addString(Key.EXTRA, "right");

        Assert.assertEquals("Unexpected Merge - left has precedence", leftOverRight, left.accept(new Merge(right)));
        Assert.assertEquals("Unexpected Merge - right has precedence", rightOverLeft, right.accept(new Merge(left)));

        Assert.assertEquals("Unexpected left Wrappers", Collections.emptyList(), left.accept(new Merge(right)).wraps());
        Assert.assertEquals("Unexpected right Wrappers", Collections.emptyList(), right.accept(new Merge(left)).wraps());
    }
}