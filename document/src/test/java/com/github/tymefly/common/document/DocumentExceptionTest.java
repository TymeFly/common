package com.github.tymefly.common.document;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link DocumentException}
 */
public class DocumentExceptionTest {
    /**
     * Unit test {@link DocumentException#DocumentException(String)}
     */
    @Test
    public void test_SimpleConstructor() {
        DocumentException actual = new DocumentException("Hello");

        Assert.assertEquals("Unexpected Message", "Hello", actual.getMessage());
        Assert.assertEquals("Unexpected cause", null, actual.getCause());
    }


    /**
     * Unit test {@link DocumentException#DocumentException(String)}
     */
    @Test
    public void test_FormattedConstructor() {
        DocumentException actual = new DocumentException("Hello %s : %d", "World", 2);

        Assert.assertEquals("Unexpected Message", "Hello World : 2", actual.getMessage());
        Assert.assertEquals("Unexpected cause", null, actual.getCause());
    }


    /**
     * Unit test {@link DocumentException#DocumentException(String)}
     */
    @Test
    public void test_WrappedConstructor() {
        IOException cause = new IOException();
        DocumentException actual = new DocumentException("Hello", cause);

        Assert.assertEquals("Unexpected Message", "Hello", actual.getMessage());
        Assert.assertEquals("Unexpected cause", cause, actual.getCause());
    }
}