package com.github.tymefly.common.document;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link WalkerHelper}
 */
public class WalkerHelperTest {
    private final WalkerKey key = WalkerKey.from(() -> "a[2].b[99].c");


    /**
     * Unit test {@link WalkerHelper#cast(Class, WalkerKey, Object)}
     */
    @Test
    public void test_cast_success() {
        Assert.assertNull("case null", WalkerHelper.cast(String.class, key, null));
        Assert.assertEquals("Same Type", "Hello", WalkerHelper.cast(String.class, key, "Hello"));
        Assert.assertEquals("Same Subclass", 12, WalkerHelper.cast(Number.class, key, 12));
    }

    /**
     * Unit test {@link WalkerHelper#cast(Class, WalkerKey, Object)}
     */
    @Test
    public void test_cast_failure() {
        DocumentException exception =
                Assert.assertThrows(DocumentException.class, () -> WalkerHelper.cast(Number.class, key, "Hello"));

        Assert.assertEquals("Unexpected message",
                "Data at 'a[2]' is of type String, but Number was expected",
                exception.getMessage());
    }


    /**
     * Unit test {@link WalkerHelper#get(Class, Structure, WalkerKey)}
     */
    @Test
    public void test_get() {
        ReadableDocument child = Document.empty();
        Sequence<String> sequence = Sequence.of(String.class, "1", "2", "3");
        Structure data = new Structure()
                .add("b", 12)
                .add("c", child)
                .add("e", sequence);

        Assert.assertNull("Missing Value", WalkerHelper.get(Number.class, data, WalkerKey.from(() -> "a[12]")));
        Assert.assertEquals("Expected Number", 12, WalkerHelper.get(Number.class, data, WalkerKey.from(() -> "b")));
        Assert.assertNull("Missing sequence", WalkerHelper.get(Document.class, data, WalkerKey.from(() -> "d[99]")));
        Assert.assertNull("Missing Indexed", WalkerHelper.get(String.class, data, WalkerKey.from(() -> "e[99]")));
        Assert.assertNull("Bad key", WalkerHelper.get(String.class, data, WalkerKey.from(() -> "f")));
        Assert.assertNull("Bad key indexed", WalkerHelper.get(String.class, data, WalkerKey.from(() -> "f[2]")));
    }


    /**
     * Unit test {@link WalkerHelper#get(Class, Structure, WalkerKey)}
     */
    @Test
    public void test_get_failure() {
        Sequence<String> sequence = Sequence.of(String.class, "1", "2", "3");
        Structure data = new Structure()
                .add("s", sequence);

        DocumentException exception =
                Assert.assertThrows(DocumentException.class,
                                    () ->  WalkerHelper.get(Number.class, data, WalkerKey.from(() -> "s")));

        Assert.assertEquals("Unexpected message",
                "Data at 's' is of type Sequence, but Number was expected",
                exception.getMessage());
    }


    /**
     * Unit test {@link WalkerHelper#getSequence(Structure, WalkerKey, Class)}
     */
    @Test
    public void test_getSequence_happyPath() {
        Sequence<Integer> raw = Sequence.of(Integer.class, 1, 2, 3);
        Structure structure = new Structure().add("a", raw);
        WalkerKey key = WalkerKey.from(() -> "a[0]");

        Sequence<Integer> actual = WalkerHelper.getSequence(structure, key, Integer.class);

        Assert.assertSame("failed to cast", raw, actual);
        Assert.assertEquals("Unexpected structure size", 1, structure.size());
    }

    /**
     * Unit test {@link WalkerHelper#getSequence(Structure, WalkerKey, Class)}
     */
    @Test
    public void test_getSequence_Null() {
        WalkerKey key = WalkerKey.from(() -> "a[17]");
        Structure structure = new Structure().add("a", null);

        Exception e = Assert.assertThrows(DocumentException.class,
                () -> WalkerHelper.getSequence(structure, key, String.class));

        Assert.assertEquals("Unexpected message", "Data at 'a' is null, but Sequence was expected", e.getMessage());
    }

    /**
     * Unit test {@link WalkerHelper#getSequence(Structure, WalkerKey, Class)}
     */
    @Test
    public void test_getSequence_Missing() {
        WalkerKey key = WalkerKey.from(() -> "a[2]");
        Structure structure = new Structure();

        Sequence<String> actual = WalkerHelper.getSequence(structure, key, String.class);
        Object expected = structure.get("a");

        Assert.assertEquals("Unexpected object", expected, actual);
        Assert.assertEquals("Unexpected sequence type", String.class, actual.getType());
        Assert.assertEquals("Unexpected sequence size", 0, actual.size());
    }

    /**
     * Unit test {@link WalkerHelper#getSequence(Structure, WalkerKey, Class)}
     */
    @Test
    public void test_getSequence_NotASequence() {
        WalkerKey key = WalkerKey.from(() -> "a.b");
        Object raw = "Hello";
        Structure structure = new Structure().add("a", raw);

        Exception e = Assert.assertThrows(DocumentException.class,
                () -> WalkerHelper.getSequence(structure, key, String.class));

        Assert.assertEquals("Unexpected message", "Data at 'a' is of type String, but Sequence was expected", e.getMessage());
    }

    /**
     * Unit test {@link WalkerHelper#getSequence(Structure, WalkerKey, Class)}
     */
    @Test
    public void test_getSequence_wrongType() {
        WalkerKey key = WalkerKey.from(() -> "a[1]");
        Object raw = Sequence.of(String.class, "Hello");
        Structure structure = new Structure().add("a", raw);

        Exception e = Assert.assertThrows(DocumentException.class,
                () -> WalkerHelper.getSequence(structure, key, Number.class));

        Assert.assertEquals("Unexpected message", "Sequence at 'a' is of type String, but Number was expected", e.getMessage());
    }

    /**
     * Unit test {@link WalkerHelper#getSequence(Structure, WalkerKey, Class)}
     */
    @Test
    public void test_getSequence_ChangeType() {
        Sequence<Integer> raw = Sequence.of(Integer.class);
        Structure structure = new Structure().add("a", raw);
        WalkerKey key = WalkerKey.from(() -> "a[0]");

        Sequence<String> actual = WalkerHelper.getSequence(structure, key, String.class);

        Assert.assertNotSame("Unexpected sequence", raw, actual);
        Assert.assertEquals("Unexpected sequence type", String.class, actual.getType());
        Assert.assertEquals("Unexpected sequence size", 0, actual.size());
        Assert.assertEquals("Unexpected structure", new Structure().add("a", Sequence.of(String.class)), structure);
        Assert.assertEquals("structure has wrong sequence type", String.class, ((Sequence<?>)structure.get("a")).getType());
    }
}