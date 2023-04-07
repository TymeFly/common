package com.github.tymefly.common.document.parse;

import java.io.InputStream;

import com.github.tymefly.common.document.Document;
import com.github.tymefly.common.document.WritableDocument;
import com.github.tymefly.common.document.key.LayeredDocumentKey;
import org.junit.Assert;
import org.junit.Test;

public class PropertiesParserTest {
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
        X
    }

    private enum Nested implements LayeredDocumentKey {
        ONE,
        TWO,
        THREE,
        DATA
    }


    /**
     * Unit test {@link DocumentParser#load(WritableDocument, InputStream)}
     */
    @Test
    public void test_Load() {
        Document expected = Document.newInstance()
                .addString(Key.X, "x")
                .addString(Key.ROOT_CHILD_STRING, "String")
                .addString(Key.ROOT_CHILD_NUMBER, "123")
                .addString(Key.ROOT_CHILD_BOOLEAN, "true")
                .addString(Key.ROOT_CHILD_ENUM, "A")
                .addStrings(Key.ROOT_STRINGS, "One", "", "Three")
                .addStrings(Key.ROOT_NUMBERS, "1", "", "3.4")
                .addStrings(Key.ROOT_BOOLEANS, "true", "", "false")
                .addStrings(Key.ROOT_ENUMS, "B", "", "C")
                .addDocuments(Key.ROOT_DOCS,
                        Document.newInstance().addString(Nested.ONE, "One"),
                        Document.newInstance().addString(Nested.TWO, "")
                                .addStrings(Nested.DATA, "x", "y", "z"),
                        Document.newInstance().addString(Nested.THREE, "Three")
                                .addString(Nested.DATA, ""));
        InputStream resource = getClass().getClassLoader().getResourceAsStream("doc/data.properties");
        Document actual = Document.newInstance();

        new PropertiesParser().load(actual, resource);

        Assert.assertEquals("Unexpected Documents", expected, actual);
    }
}