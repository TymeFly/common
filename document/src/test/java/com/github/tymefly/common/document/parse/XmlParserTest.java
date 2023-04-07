package com.github.tymefly.common.document.parse;

import java.io.IOException;
import java.io.InputStream;

import com.github.tymefly.common.base.io.FailedIoException;
import com.github.tymefly.common.document.Document;
import com.github.tymefly.common.document.DocumentException;
import com.github.tymefly.common.document.WritableDocument;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link XmlParser}
 */
public class XmlParserTest {
    /**
     * Unit test {@link XmlParser#load(WritableDocument, InputStream)}
     */
    @Test
    public void test_HappyPath() {
        Document expected = Document.newInstance()
            .addString(() -> "source", "XML")
            .addString(() -> "book[0].export", "n")
            .addString(() -> "book[0].id", "1")
            .addString(() -> "book[0].title[0]", "Book 1")
            .addString(() -> "book[0].author[0]", "John Johnson")
            .addString(() -> "book[0].author[1]", "Robert Robertson")
            .addString(() -> "book[0].contact[0].phone[0]", "555 12345678")
            .addString(() -> "book[0].contact[0].email[0]", "example@example.com")
            .addString(() -> "book[0].contact[0].copyright", "holder")
            .addString(() -> "book[1].id", "2")
            .addString(() -> "book[1].title[0]", "Book 2");
        
        InputStream resource = getClass().getClassLoader().getResourceAsStream("xml/books.xml");
        Document result = Document.newInstance();

        new XmlParser().load(result, resource);

        Assert.assertEquals("Unexpected Document", expected, result);
    }

    /**
     * Unit test {@link XmlParser#load(WritableDocument, InputStream)}
     */
    @Test
    public void test_SingleElement() {
        Document expected = Document.newInstance()
            .addStrings(() -> "single", "element");

        InputStream resource = getClass().getClassLoader().getResourceAsStream("xml/single.xml");
        Document result = Document.newInstance();

        new XmlParser().load(result, resource);

        Assert.assertEquals("Unexpected Document", expected, result);
    }


    /**
     * Unit test {@link XmlParser#load(WritableDocument, InputStream)}
     */
    @Test
    public void test_SingleEmptyElement() {
        InputStream resource = getClass().getClassLoader().getResourceAsStream("xml/emptyElement.xml");
        Document result = Document.newInstance();

        new XmlParser().load(result, resource);

        Assert.assertEquals("Unexpected Document", Document.empty(), result);
    }


    /**
     * Unit test {@link XmlParser#load(WritableDocument, InputStream)}
     */
    @Test
    public void test_Broken() {
        InputStream resource = getClass().getClassLoader().getResourceAsStream("xml/truncated.xml");
        Document result = Document.newInstance();

        DocumentException actual =
            Assert.assertThrows(DocumentException.class, () -> new XmlParser().load(result, resource));

        Assert.assertEquals("Unexpected message", "Failed to parse XML", actual.getMessage());
    }


    /**
     * Unit test {@link XmlParser#load(WritableDocument, InputStream)}
     */
    @Test
    public void test_Invalid() throws Exception {
        InputStream resource = mock(InputStream.class);
        Document result = Document.newInstance();

        when(resource.read())
            .thenThrow(new IOException("Expected"));

        FailedIoException actual =
            Assert.assertThrows(FailedIoException.class, () -> new XmlParser().load(result, resource));

        Assert.assertEquals("Unexpected message", "Failed to read XML", actual.getMessage());
    }
}