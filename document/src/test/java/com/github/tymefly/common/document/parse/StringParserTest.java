package com.github.tymefly.common.document.parse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.github.tymefly.common.document.Document;
import com.github.tymefly.common.document.DocumentException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link StringParser}
 */
public class StringParserTest {
    /**
     * Unit test {@link StringParser} for a null value
     */
    @Test
    public void test_Null() {
        byte[] raw = "key:Null".getBytes(StandardCharsets.UTF_8);
        InputStream source = new ByteArrayInputStream(raw);
        Document actual = Document.newInstance();
        Document expected = Document.newInstance()
            .addString(() -> "key", null);

        new StringParser().load(actual, source);

        Assert.assertEquals("Unexpected document generated", expected, actual);
    }


    /**
     * Unit test {@link StringParser} for a null value
     */
    @Test
    public void test_Booleans() {
        byte[] raw = "a:TRUE ; b:false".getBytes(StandardCharsets.UTF_8);
        InputStream source = new ByteArrayInputStream(raw);
        Document actual = Document.newInstance();
        Document expected = Document.newInstance()
            .addBoolean(() -> "a", true)
            .addBoolean(() -> "b", false);

        new StringParser().load(actual, source);

        Assert.assertEquals("Unexpected document generated", expected, actual);
    }


    /**
     * Unit test {@link StringParser} for a null value
     */
    @Test
    public void test_Numbers() {
        byte[] raw = " a:1; b:-2; c:3.4; d:5.6 ".getBytes(StandardCharsets.UTF_8);
        InputStream source = new ByteArrayInputStream(raw);
        Document actual = Document.newInstance();
        Document expected = Document.newInstance()
            .addNumber(() -> "a", 1)
            .addNumber(() -> "b", -2)
            .addNumber(() -> "c", 3.4)
            .addNumber(() -> "d", 5.6);

        new StringParser().load(actual, source);

        Assert.assertEquals("Unexpected document generated", expected, actual);
    }

    /**
     * Unit test {@link StringParser} for a null value
     */
    @Test
    public void test_Strings() {
        byte[] raw = " a : Foo ; b : \" Bar \" ; c : 'text' ; d : \" ; e: ; f : Hello World ".getBytes(StandardCharsets.UTF_8);
        InputStream source = new ByteArrayInputStream(raw);
        Document actual = Document.newInstance();
        Document expected = Document.newInstance()
            .addString(() -> "a", "Foo")                    // Remove leading/trailing spaces
            .addString(() -> "b", " Bar ")                  // Remove just leading/trailing spaces and double quotes
            .addString(() -> "c", "'text'")                 // Remove leading/trailing but not sing quotes
            .addString(() -> "d", "\"")                     // Unterminated double quotes, so store a double quote
            .addString(() -> "e", "")                       // Empty string (no trailing spaces)
            .addString(() -> "f", "Hello World");           // Remove leading/trailing spaces, but keep inner space


        new StringParser().load(actual, source);

        Assert.assertEquals("Unexpected document generated", expected, actual);
    }

    /**
     * Unit test {@link StringParser} for a null value
     */
    @Test
    public void test_MixedTypes() {
        byte[] raw = "a:null;b:true;c:false;d:123;e:-4.5;f:;g:\" Test \"".getBytes(StandardCharsets.UTF_8);
        InputStream source = new ByteArrayInputStream(raw);
        Document actual = Document.newInstance();
        Document expected = Document.newInstance()
            .addString(() -> "a", null)
            .addBoolean(() -> "b", true)
            .addBoolean(() -> "c", false)
            .addNumber(() -> "d", 123)
            .addNumber(() -> "e", -4.5)
            .addString(() -> "f", "")
            .addString(() -> "g", " Test ");

        new StringParser().load(actual, source);

        Assert.assertEquals("Unexpected document generated", expected, actual);
    }

    /**
     * Unit test {@link StringParser} for a null value
     */
    @Test
    public void test_Arrays() {
        byte[] raw = "a[0]:0;a[2]:2".getBytes(StandardCharsets.UTF_8);
        InputStream source = new ByteArrayInputStream(raw);
        Document actual = Document.newInstance();
        Document expected = Document.newInstance()
            .addNumbers(() -> "a", 0, null, 2);

        new StringParser().load(actual, source);

        Assert.assertEquals("Unexpected document generated", expected, actual);
    }

    /**
     * Unit test {@link StringParser} for a null value
     */
    @Test
    public void test_Nested() {
        byte[] raw = "a.b:0; a.c[1].d:1".getBytes(StandardCharsets.UTF_8);
        InputStream source = new ByteArrayInputStream(raw);
        Document actual = Document.newInstance();
        Document expected = Document.newInstance()
            .addNumber(() -> "a.b", 0)
            .addNumber(() -> "a.c[1].d", 1);

        new StringParser().load(actual, source);

        Assert.assertEquals("Unexpected document generated", expected, actual);
    }

    /**
     * Unit test {@link StringParser} for a null value
     */
    @Test
    public void test_NoKey_Value_Pair() {
        byte[] raw = "Key".getBytes(StandardCharsets.UTF_8);
        InputStream source = new ByteArrayInputStream(raw);

        Assert.assertThrows(DocumentException.class,
                () -> new StringParser().load(Document.newInstance(), source));
    }


    /**
     * Unit test {@link StringParser} for a null value
     */
    @Test
    public void test_InvalidKey() {
        byte[] raw = "@=12".getBytes(StandardCharsets.UTF_8);
        InputStream source = new ByteArrayInputStream(raw);

        Assert.assertThrows(DocumentException.class,
                () -> new StringParser().load(Document.newInstance(), source));
    }

    /**
     * Unit test {@link StringParser} for a null value
     */
    @Test
    public void test_MissingKey() {
        byte[] raw = "=12".getBytes(StandardCharsets.UTF_8);
        InputStream source = new ByteArrayInputStream(raw);

        Assert.assertThrows(DocumentException.class,
                () -> new StringParser().load(Document.newInstance(), source));
    }
}