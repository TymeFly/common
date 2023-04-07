package com.github.tymefly.common.document.visitor.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test {@link EntrySet.Entry}
 */
public class EntrySet_EntryTest {
    private final EntrySet.Entry first = new EntrySet.Entry(() -> "a", "b");
    private final EntrySet.Entry first_b = new EntrySet.Entry(() -> "a", "b");
    private final EntrySet.Entry second = new EntrySet.Entry(() -> "a.b", null);
    private final EntrySet.Entry third = new EntrySet.Entry(() -> "a.c", 3);
    private final EntrySet.Entry third_b = new EntrySet.Entry(() -> "a.c", 3);


    /**
     * Unit test {@link EntrySet.Entry#getKey()}
     */
    @Test
    public void test_GetFirst() {
        Assert.assertEquals("regEx", "a", first.getKey().externalise());
        Assert.assertEquals("first_b", "a", first_b.getKey().externalise());
        Assert.assertEquals("value", "a.b", second.getKey().externalise());
        Assert.assertEquals("third", "a.c", third.getKey().externalise());
        Assert.assertEquals("third_b", "a.c", third_b.getKey().externalise());
    }

    /**
     * Unit test @link {@link EntrySet.Entry#getValue()}
     */
    @Test
    public void test_GetSecond() {
        Assert.assertEquals("regEx", "b", first.getValue());
        Assert.assertEquals("first_b", "b", first_b.getValue());
        Assert.assertNull("value", second.getValue());
        Assert.assertEquals("third", 3, third.getValue());
        Assert.assertEquals("third_b", 3, third_b.getValue());
    }

    /**
     * Unit test {@link EntrySet.Entry#equals}
     */
    @Test
    public void test_equals() {
        Assert.assertFalse("regEx equals String", first.equals("Hello"));

        Assert.assertFalse("regEx equals null", first.equals(null));
        Assert.assertTrue("regEx equals regEx", first.equals(first));
        Assert.assertTrue("value equals value", second.equals(second));
        Assert.assertTrue("third equals third", third.equals(third));

        Assert.assertTrue("regEx equals first_b", first.equals(first_b));
        Assert.assertTrue("first_b equals regEx", first_b.equals(first));

        Assert.assertTrue("third equals third_b", third.equals(third_b));
        Assert.assertTrue("third_b equals third", third_b.equals(third));

        Assert.assertFalse("regEx equals value", first.equals(second));
        Assert.assertFalse("regEx equals third", first.equals(third));
        Assert.assertFalse("value equals regEx", second.equals(first));
        Assert.assertFalse("value equals third", second.equals(third));
        Assert.assertFalse("third equals regEx", third.equals(first));
        Assert.assertFalse("third equals value", third.equals(second));
    }

    /**
     * Unit test {@link EntrySet.Entry#hashCode}
     */
    @Test
    public void test_hashCode() {
        Assert.assertEquals("regEx and first_b", first.hashCode(), first_b.hashCode());
        Assert.assertEquals("third and third_b", third.hashCode(), third_b.hashCode());

        Assert.assertNotEquals("regEx and value", first.hashCode(), second.hashCode());
        Assert.assertNotEquals("regEx and third", first.hashCode(), third.hashCode());
        Assert.assertNotEquals("value and third", second.hashCode(), third.hashCode());
    }


    /**
     * Unit test {@link EntrySet.Entry#toString}
     */
    @Test
    public void test_toString() {
        Assert.assertEquals("regEx", "Entry{a => b}", first.toString());
        Assert.assertEquals("first_b", "Entry{a => b}", first_b.toString());
        Assert.assertEquals("value", "Entry{a.b => null}", second.toString());
        Assert.assertEquals("third", "Entry{a.c => 3}", third.toString());
        Assert.assertEquals("third_b", "Entry{a.c => 3}", third_b.toString());
    }
}
