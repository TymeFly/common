package com.github.tymefly.common.document.parse;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import com.github.tymefly.common.document.Document;
import com.github.tymefly.common.document.DocumentException;
import com.github.tymefly.common.document.ReadableDocument;
import com.github.tymefly.common.document.WritableDocument;
import com.github.tymefly.common.document.key.DocumentKey;
import com.github.tymefly.common.document.key.LayeredDocumentKey;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link JsonParser}
 */
public class JsonParserTest {
    private enum Key implements LayeredDocumentKey {
        X,
        ROOT_CHILD_STRING,
        ROOT_CHILD_NUMBER,
        ROOT_CHILD_BOOLEAN,
        ROOT_EMPTY,
        ROOT_STRINGS,
        ROOT_NUMBERS,
        ROOT_BOOLEANS,
        ROOT_DOCS
    }

    private enum Nested implements LayeredDocumentKey {
        ONE,
        TWO,
        THREE,
        DATA
    }
    
    
    /**
     * Unit test {@link JsonParser#load(WritableDocument, InputStream)}
     */
    @Test
    public void test_parse()  {
        Document expected = Document.newInstance()
                .addString(Key.X, null)
                .addString(Key.ROOT_CHILD_STRING, "String")
                .addNumber(Key.ROOT_CHILD_NUMBER, 123)
                .addBoolean(Key.ROOT_CHILD_BOOLEAN, true)
                .addStrings(Key.ROOT_EMPTY, Collections.emptyList())
                .addStrings(Key.ROOT_STRINGS, "One", null, "Three")
                .addNumbers(Key.ROOT_NUMBERS, null, 2, new BigDecimal("3.4"))
                .addBooleans(Key.ROOT_BOOLEANS, true, null, false)
                .addDocuments(Key.ROOT_DOCS,
                        Document.newInstance().addString(Nested.ONE, "One"),
                        Document.newInstance().addString(Nested.TWO, null)
                                .addStrings(Nested.DATA, "1", "x", "y", "z"),
                        Document.newInstance().addString(Nested.THREE, "Three")
                                .addStrings(Nested.DATA, (List<String>) null));
        InputStream sample = getClass().getClassLoader().getResourceAsStream("doc/data.json");
        JsonParser parser = new JsonParser();
        Document actual = Document.newInstance();

        parser.load(actual, sample);

        Assert.assertEquals("Parse failed", expected, actual);
    }


    /**
     * Unit test {@link JsonParser#load(WritableDocument, InputStream)}
     */
    @Test
    public void test_Empty() {
        ReadableDocument expected = Document.empty();
        InputStream sample = getClass().getClassLoader().getResourceAsStream("doc/empty.json");
        JsonParser parser = new JsonParser();
        Document actual = Document.newInstance();

        parser.load(actual, sample);

        Assert.assertEquals("Parse failed", expected, actual);
    }


    /**
     * Unit test {@link JsonParser#load(WritableDocument, InputStream)}
     */
    @Test
    public void test_EmptyChild() {
        ReadableDocument expected = Document.newInstance()
                .addDocument(() -> "name", Document.newInstance());
        InputStream sample = getClass().getClassLoader().getResourceAsStream("doc/emptyChild.json");
        JsonParser parser = new JsonParser();
        Document actual = Document.newInstance();

        parser.load(actual, sample);

        Assert.assertNotEquals("Expected data", Document.empty(), actual);
        Assert.assertEquals("Parse failed", expected, actual);
    }


    /**
     * Unit test {@link JsonParser#load(WritableDocument, InputStream)}
     */
    @Test
    public void test_EmptyArray() {
        ReadableDocument expected = Document.newInstance()
                .addStrings(() -> "name");
        InputStream sample = getClass().getClassLoader().getResourceAsStream("doc/emptyArray.json");
        JsonParser parser = new JsonParser();
        Document actual = Document.newInstance();

        parser.load(actual, sample);

        Assert.assertNotEquals("Expected data", Document.empty(), actual);
        Assert.assertEquals("Parse failed", expected, actual);
    }


    /**
     * Unit test {@link JsonParser#load(WritableDocument, InputStream)}
     */
    @Test
    public void test_EmptyArray_append() {
        DocumentKey sequenceKey = () -> "name";
        DocumentKey elementKey = () -> "name[2]";
        InputStream sample = getClass().getClassLoader().getResourceAsStream("doc/emptyArray.json");
        JsonParser parser = new JsonParser();
        Document actual = Document.newInstance();

        parser.load(actual, sample);

        Assert.assertTrue("Array should exist", actual.contains(sequenceKey));
        Assert.assertTrue("Should be a sequence", actual.isSequence(sequenceKey));
        Assert.assertTrue("Empty sequence is a value", actual.hasValue(sequenceKey));

        Assert.assertEquals("Read as Docs", Collections.emptyList(), actual.getAll(sequenceKey, Document.class));
        Assert.assertEquals("Read as Strings", Collections.emptyList(), actual.getAll(sequenceKey, String.class));
        Assert.assertEquals("Read as Booleans", Collections.emptyList(), actual.getAll(sequenceKey, Boolean.class));

        Assert.assertNull("Read single Doc", actual.getOptional(elementKey, Document.class));
        Assert.assertNull("Read single String", actual.getOptional(elementKey, String.class));
        Assert.assertNull("Read single Boolean",actual.getOptional(elementKey, Boolean.class));

        // Set type
        actual.addNumber(elementKey, 2);

        Assert.assertEquals("Expected data",
                Document.newInstance().addNumbers(sequenceKey, null, null, 2),
                actual);

        Assert.assertThrows(DocumentException.class, () -> actual.getOptional(elementKey, Boolean.class));
    }

    /**
     * Unit test {@link JsonParser#load(WritableDocument, InputStream)}
     */
    @Test
    public void test_mixed_array() {
        InputStream sample = getClass().getClassLoader().getResourceAsStream("doc/mixedArray.valid.json");
        JsonParser parser = new JsonParser();
        Document actual = Document.newInstance();

        parser.load(actual, sample);

        Assert.assertEquals("Expected data",                        // Numbers were converted to Strings
                Document.newInstance().addStrings(() -> "data", "One", null, "3", "4.0"),
                actual);
    }


    /**
     * Unit test {@link JsonParser#load(WritableDocument, InputStream)}
     */
    @Test
    public void test_mixed_array_nestedChild() {
        InputStream sample = getClass().getClassLoader().getResourceAsStream("doc/mixedArray.child.json");
        JsonParser parser = new JsonParser();

        Assert.assertThrows(DocumentException.class, () -> parser.load(Document.newInstance(), sample));
    }


    /**
     * Unit test {@link JsonParser#load(WritableDocument, InputStream)}
     */
    @Test
    public void test_truncated() {
        InputStream sample = getClass().getClassLoader().getResourceAsStream("doc/truncated.json");
        JsonParser parser = new JsonParser();

        Assert.assertThrows(DocumentException.class, () -> parser.load(Document.newInstance(), sample));
    }

    /**
     * Unit test {@link JsonParser#load(WritableDocument, InputStream)} to check that nested arrays are not
     * supported (Document does not have an API to read/write them)
     */
    @Test
    public void test_nested_array() {
        InputStream sample = getClass().getClassLoader().getResourceAsStream("doc/nestedArray.json");
        JsonParser parser = new JsonParser();
        DocumentException exception =
                Assert.assertThrows(DocumentException.class, () -> parser.load(Document.newInstance(), sample));

        Assert.assertEquals("Unexpected message",
                "Unexpected key 'data' is an array of unexpected type 'Array'",
                        exception.getMessage());
    }
}
