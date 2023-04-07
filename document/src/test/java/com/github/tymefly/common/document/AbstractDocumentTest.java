package com.github.tymefly.common.document;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link AbstractDocument}
 */
public class AbstractDocumentTest {
    private AbstractDocument<?> doc;

    @Before
    public void setUp() {
        doc = new DocumentImpl(null);
    }


    /**
     * Unit test {@link AbstractDocument#transformAll(Function, Object[])}
     */
    @Test
    public void test_transformAll_Array() {
        Integer[] numbers = new Integer[] { 1, 2, 3, 4 };
        Function<Integer, Integer> transform = i -> i + 1;

        Integer[] result = doc.transformAll(transform, numbers);

        Assert.assertSame("Unexpected array", numbers, result);
        Assert.assertArrayEquals("unexpected numbers", new Integer[]{ 2, 3, 4, 5}, result);
    }


    /**
     * Unit test {@link AbstractDocument#transformAll(Function, Collection)}
     */
    @Test
    public void test_transformAll_Collection() {
        List<String> colours = List.of("Red", "Green", "Blue");
        Function<String, String> transform = String::toUpperCase;

        Collection<String> result = doc.transformAll(transform, colours);

        Assert.assertNotSame("Unexpected List", colours, result);
        Assert.assertEquals("unexpected numbers", List.of("RED", "GREEN", "BLUE"), result);
    }
}