package com.github.tymefly.common.document.decorator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;

import com.github.tymefly.common.document.AbstractDocument;
import com.github.tymefly.common.document.CommonDocument;
import com.github.tymefly.common.document.Document;
import com.github.tymefly.common.document.ReadableDocument;
import com.github.tymefly.common.document.key.DocumentKey;
import com.github.tymefly.common.document.visitor.DocumentVisitor;
import com.github.tymefly.common.document.visitor.util.Copy;
import com.github.tymefly.common.document.visitor.util.Size;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link SynchronizedDocument}
 */
public class SynchronizedDocumentTest {
    private enum Type { ONE, TWO, THREE, FOUR }

    private static final DocumentKey VALUE = () -> "Value";
    private static final DocumentKey DATA = () -> "Data";
    private static final DocumentKey EMPTY = () -> "Empty";
    private static final DocumentKey NULL = () -> "Null";
    private static final DocumentKey LIST = () -> "List";

    private SynchronizedDocument doc;


    @Before
    public void setUp() {
        AbstractDocument<?> backing = (AbstractDocument<?>) Document.newInstance();
        doc = new SynchronizedDocument(backing);
    }

    /**
     * Unit test {@link SynchronizedDocument#addString(DocumentKey, String)}
     */
    @Test
    public void test_synchronisation() {
        doc.addString(VALUE, "Data");

        final int[] thread = new int[1];
        StringBuilder buffer = new StringBuilder();
        final boolean[] valid = { true };

        IntStream.rangeClosed(1, 50_000)
            .parallel()
            .forEach(i -> doc.get(VALUE, v -> {
                thread[0] = i;

                buffer.append(i);                       // Slow down thread
                buffer.reverse();

                if ((thread[0] != i)) {
                    valid[0] = false;
                }

                return v;
            }));

        Assert.assertTrue("Sync failed", valid[0]);
    }


    /**
     * Unit test {@link SynchronizedDocument#addString(DocumentKey, String)} and
     * {@link SynchronizedDocument#appendString(DocumentKey, String)}
     */
    @Test
    public void test_addString() {
        doc.addString(VALUE, "Hello")
           .addString(NULL, null)
           .appendString(LIST, "1")
           .appendString(LIST, "2");

        Assert.assertEquals("strings",
            Document.newInstance()
                    .addString(VALUE, "Hello")
                    .addString(NULL, null)
                    .addStrings(LIST, "1", "2"),
            doc);
    }

    /**
     * Unit test {@link SynchronizedDocument#addStrings(DocumentKey, String...)}
     */
    @Test
    public void test_addStrings_Array() {
        doc.addStrings(VALUE, null, "Hello", null, "World")
           .addStrings(EMPTY)
           .addStrings(NULL, (String) null);

        Assert.assertEquals("addString(...)",
            Document.newInstance()
                    .addStrings(VALUE, null, "Hello", null, "World")
                    .addStrings(EMPTY)
                    .addStrings(NULL, (String) null),
            doc);
    }

    /**
     * Unit test {@link SynchronizedDocument#addStrings(DocumentKey, Collection)}
     */
    @Test
    public void test_addStrings_List() {
        doc.addStrings(VALUE, Arrays.asList(null, "Hello", null, "World"))
           .addStrings(EMPTY, Collections.emptyList())
           .addStrings(NULL, nullList(String.class));

        Assert.assertEquals("addStrings(List)",
            Document.newInstance()
                    .addStrings(VALUE, null, "Hello", null, "World")
                    .addStrings(EMPTY)
                    .addStrings(NULL, nullList(String.class)),
            doc);
    }


    /**
     * Unit test {@link SynchronizedDocument#addNumber(DocumentKey, Number)} and
     * {@link SynchronizedDocument#appendNumber(DocumentKey, Number)}
     */
    @Test
    public void test_addNumber() {
        doc.addNumber(VALUE, 12)
           .addNumber(NULL, null)
           .appendNumber(LIST, 1)
           .appendNumber(LIST, 2);

        Assert.assertEquals("addNumber()",
            Document.newInstance()
                    .addNumber(VALUE, 12)
                    .addNumber(NULL, null)
                    .addNumbers(LIST, 1, 2),
            doc);
    }

    /**
     * Unit test {@link SynchronizedDocument#addNumbers(DocumentKey, Number...)}
     */
    @Test
    public void test_addNumbers_Array() {
        doc.addNumbers(VALUE, 1, null, 2, null, 3)
           .addNumbers(EMPTY)
           .addNumbers(NULL, (Long) null);

        Assert.assertEquals("addNumbers(...)",
            Document.newInstance()
                    .addNumbers(VALUE, 1, null, 2, null, 3)
                    .addNumbers(EMPTY)
                    .addNumbers(NULL, (Number) null),
            doc);
    }

    /**
     * Unit test {@link SynchronizedDocument#addNumbers(DocumentKey, Collection)}
     */
    @Test
    public void test_addNumbers_List() {
        doc.addNumbers(VALUE, Arrays.asList(1, null, 2, null, 3))
           .addNumbers(EMPTY, Collections.emptyList())
           .addNumbers(NULL, nullList(Number.class));

        Assert.assertEquals("addNumbers(List)",
            Document.newInstance()
                    .addNumbers(VALUE, 1, null, 2, null, 3)
                    .addNumbers(EMPTY)
                    .addNumbers(NULL, nullList(Number.class)),
            doc);
    }

    /**
     * Unit test {@link SynchronizedDocument#addBoolean(DocumentKey, Boolean)} and
     * {@link SynchronizedDocument#appendBoolean(DocumentKey, Boolean)}
     */
    @Test
    public void test_addBoolean() {
        doc.addBoolean(VALUE, true)
           .addBoolean(NULL, null)
           .appendBoolean(LIST, Boolean.TRUE)
           .appendBoolean(LIST, Boolean.FALSE);

        Assert.assertEquals("addBoolean()",
            Document.newInstance()
                    .addBoolean(VALUE, true)
                    .addBoolean(NULL, null)
                    .addBooleans(LIST, Boolean.TRUE, Boolean.FALSE),
            doc);
    }

    /**
     * Unit test {@link SynchronizedDocument#addBooleans(DocumentKey, Boolean...)}
     */
    @Test
    public void test_addBooleans_Array() {
        doc.addBooleans(VALUE, true, false, null)
           .addBooleans(EMPTY)
           .addBooleans(NULL, (Boolean) null);

        Assert.assertEquals("addBooleans(...)",
            Document.newInstance()
                    .addBooleans(VALUE, true, false, null)
                    .addBooleans(EMPTY)
                    .addBooleans(NULL, (Boolean) null),
            doc);
    }

    /**
     * Unit test {@link SynchronizedDocument#addBooleans(DocumentKey, Boolean...)}
     */
    @Test
    public void test_addBooleans_List() {
        doc.addBooleans(VALUE, Arrays.asList(true, false, null))
           .addBooleans(EMPTY, Collections.emptyList())
           .addBooleans(NULL, nullList(Boolean.class));

        Assert.assertEquals("addBooleans(List)",
            Document.newInstance()
                    .addBooleans(VALUE, true, false, null)
                    .addBooleans(EMPTY)
                    .addBooleans(NULL, nullList(Boolean.class)),
            doc);
    }

    /**
     * Unit test {@link SynchronizedDocument#addEnum(DocumentKey, Enum)} and
     *      * {@link SynchronizedDocument#appendEnum(DocumentKey, Enum)}
     */
    @Test
    public void test_addEnum() {
        doc.addEnum(VALUE, Type.ONE)
           .addEnum(NULL, null)
           .appendEnum(LIST, Type.THREE)
           .appendEnum(LIST, Type.FOUR);

        Assert.assertEquals("addEnum()",
            Document.newInstance()
                    .addEnum(VALUE, Type.ONE)
                    .addEnum(NULL, null)
                    .addEnums(LIST, Type.THREE, Type.FOUR),
            doc);
    }

    /**
     * Unit test {@link SynchronizedDocument#addEnums(DocumentKey, Enum[])}
     */
    @Test
    public void test_addEnums_Array() {
        doc.addEnums(VALUE, Type.ONE, null, null, Type.TWO)
           .addEnums(EMPTY)
           .addEnums(NULL, (Type) null);

        Assert.assertEquals("addEnums(...)",
            Document.newInstance()
                    .addEnums(VALUE, Type.ONE, null, null, Type.TWO)
                    .addEnums(EMPTY, new ArrayList<Type>())
                    .addEnums(NULL, (Type) null),
            doc);
    }

    /**
     * Unit test {@link SynchronizedDocument#addEnums(DocumentKey, Enum[])}
     */
    @Test
    public void test_addEnums_List() {
        doc.addEnums(VALUE, Arrays.asList(Type.ONE, null, null, Type.TWO))
           .addEnums(EMPTY, new ArrayList<Type>())
           .addEnums(NULL, nullList(Type.class));

        Assert.assertEquals("addEnums(List)",
            Document.newInstance()
                    .addEnums(VALUE, Type.ONE, null, null, Type.TWO)
                    .addEnums(EMPTY, new ArrayList<Type>())
                    .addEnums(NULL, nullList(Type.class)),
            doc);
    }

    /**
     * Unit test {@link SynchronizedDocument#addDocument(DocumentKey, CommonDocument)} and
     * {@link SynchronizedDocument#appendDocument(DocumentKey, CommonDocument)}
     */
    @Test
    public void test_addDocument() {
        doc.addDocument(VALUE, Document.empty())
           .addDocument(NULL, null)
           .appendDocument(LIST, null)
           .appendDocument(LIST, Document.empty());

        Assert.assertEquals("addDocument()",
            Document.newInstance()
                    .addDocument(VALUE, Document.empty())
                    .addDocument(NULL, null)
                    .addDocuments(LIST, null, Document.empty()),
            doc);
    }

    /**
     * Unit test {@link SynchronizedDocument#addDocuments(DocumentKey, CommonDocument...)}
     */
    @Test
    public void test_addDocuments_Array() {
        doc.addDocuments(VALUE, Document.empty(), null, null, Document.empty())
           .addDocuments(EMPTY)
           .addDocuments(NULL, (CommonDocument) null);

        Assert.assertEquals("addDocuments(...)",
            Document.newInstance()
                    .addDocuments(VALUE,Document.empty(), null, null, Document.empty())
                    .addDocuments(EMPTY)
                    .addDocuments(NULL, (CommonDocument) null),
            doc);
    }

    /**
     * Unit test {@link SynchronizedDocument#addDocuments(DocumentKey, CommonDocument...)}
     */
    @Test
    public void test_addDocuments_List() {
        doc.addDocuments(VALUE, Arrays.asList(Document.empty(), null, null, Document.empty()))
           .addDocuments(EMPTY, Collections.emptyList())
           .addDocuments(NULL, nullList(Document.class));

        Assert.assertEquals("addDocuments(List)",
            Document.newInstance()
                    .addDocuments(VALUE,Document.empty(), null, null, Document.empty())
                    .addDocuments(EMPTY)
                    .addDocuments(NULL, nullList(Document.class)),
            doc);
    }


    /**
     * Unit test {@link SynchronizedDocument#unmodifiable()}
     */
    @Test
    public void test_immutable() {
        doc.addString(VALUE, "Hello")
           .addNumber(DATA, 123);

        ReadableDocument view = doc.unmodifiable();

        Assert.assertEquals("expected docs to be equal", doc, view);
        Assert.assertEquals("Unexpected type", UnmodifiableDocument.class, view.getClass());
        Assert.assertEquals("Changes were passed through", doc.addString(VALUE, "other"), view);
    }


    /**
     * Unit test {@link SynchronizedDocument#remove(DocumentKey)}
     */
    @Test
    public void test_remove() {
        doc.addString(VALUE, "Hello")
           .addNumber(DATA, 123);

        Assert.assertTrue("Has VALUE", doc.contains(VALUE));

        doc.remove(VALUE);

        Assert.assertFalse("No longer had VALUE", doc.contains(VALUE));
        Assert.assertEquals("Unexpected content", Document.newInstance().addNumber(DATA, 123), doc);
    }

    /**
     * Unit test {@link SynchronizedDocument#getOrDefault(DocumentKey, Object)}
     */
    @Test
    public void test_get() {
        doc.addString(VALUE, "Hello")
           .addNumbers(DATA, 0, 1, 2, 3);

        Assert.assertEquals("get VALUE", "Hello", doc.get(VALUE, String.class));
        Assert.assertEquals("get DATA[1]", 1, (int) doc.get(() -> "Data[1]", Integer.class));
        Assert.assertThrows("Read invalid data", NullPointerException.class, () -> doc.get(NULL, Boolean.class));
    }
    /**
     * Unit test {@link SynchronizedDocument#getOrDefault(DocumentKey, Object)}
     */
    @Test
    public void test_get_Default() {
        doc.addString(VALUE, "Hello")
           .addNumbers(DATA, 0, 1, 2, 3);

        Assert.assertEquals("get VALUE", "Hello", doc.getOrDefault(VALUE, "???"));
        Assert.assertEquals("get DATA[1]", 1, (int) doc.getOrDefault(() -> "Data[1]", 10));
        Assert.assertEquals("get NULL", 99, (int) doc.getOrDefault(NULL, 99));
    }


    /**
     * Unit test {@link SynchronizedDocument#getOptional(DocumentKey, Class)}
     */
    @Test
    public void test_getOptional() {
        doc.addString(VALUE, "Hello")
           .addNumbers(DATA, 0, 1, 2, 3);

        Assert.assertEquals("get VALUE", "Hello", doc.getOptional(VALUE, String.class));
        Assert.assertEquals("get DATA[1]", Integer.valueOf(1), doc.getOptional(() -> "Data[1]", Integer.class));
        Assert.assertNull("get NULL", doc.getOptional(NULL, Boolean.class));
    }

    /**
     * Unit test {@link SynchronizedDocument#getAll(DocumentKey, Class)}
     */
    @Test
    public void test_getAll() {
        doc.addString(VALUE, "Hello")
           .addNumbers(DATA, 0, 1, 2, 3);

        Assert.assertEquals("get VALUE", Collections.singletonList("Hello"), doc.getAll(VALUE, String.class));
        Assert.assertEquals("get DATA[1]", Collections.singletonList(1), doc.getAll(() -> "Data[1]", Integer.class));
        Assert.assertEquals("get DATA", List.of(0, 1, 2, 3), doc.getAll(DATA, Integer.class));
        Assert.assertEquals("get NULL", Collections.emptyList(), doc.getAll(NULL, Boolean.class));
    }

    /**
     * Unit test {@link SynchronizedDocument#isEmpty()}
     */
    @Test
    public void test_isEmpty() {
        Assert.assertTrue("Expected empty Doc", doc.isEmpty());

        doc.addString(VALUE, "xxx");
        Assert.assertFalse("No longer empty", doc.isEmpty());

        doc.remove(VALUE);
        Assert.assertTrue("empty again", doc.isEmpty());
    }

    /**
     * Unit test {@link SynchronizedDocument#contains(DocumentKey)}
     */
    @Test
    public void test_contains() {
        doc.addString(VALUE, "Hello")
           .addNumbers(DATA, 0, 1, 2, 3);

        Assert.assertTrue("Contains VALUE", doc.contains(VALUE));
        Assert.assertTrue("Contains DATA[1]", doc.contains(() -> "Data[1]"));
        Assert.assertTrue("Contains DATA", doc.contains(DATA));
        Assert.assertFalse("Contains NULL", doc.contains(NULL));
    }

    /**
     * Unit test {@link SynchronizedDocument#hasValue(DocumentKey)}
     */
    @Test
    public void test_hasValue() {
        doc.addString(VALUE, "Hello")
           .addBoolean(NULL, null)
           .addNumbers(DATA, 0, 1, 2, 3);

        Assert.assertTrue("hasValue VALUE", doc.hasValue(VALUE));
        Assert.assertTrue("hasValue DATA[1]", doc.hasValue(() -> "Data[1]"));
        Assert.assertTrue("hasValue DATA", doc.hasValue(DATA));
        Assert.assertFalse("hasValue NULL", doc.hasValue(NULL));
        Assert.assertFalse("hasValue EMPTY", doc.hasValue(EMPTY));
    }

    /**
     * Unit test {@link SynchronizedDocument#isSequence(DocumentKey)}
     */
    @Test
    public void test_isSequence() {
        doc.addString(VALUE, "Hello")
           .addBoolean(NULL, null)
           .addNumbers(DATA, 0, 1, 2, 3);

        Assert.assertFalse("isSequence VALUE", doc.isSequence(VALUE));
        Assert.assertFalse("isSequence DATA[1]", doc.isSequence(() -> "Data[1]"));
        Assert.assertTrue("isSequence DATA", doc.hasValue(DATA));
        Assert.assertFalse("isSequence NULL", doc.hasValue(NULL));
        Assert.assertFalse("isSequence NULL", doc.hasValue(EMPTY));
    }

    /**
     * Unit test {@link SynchronizedDocument#accept(DocumentVisitor)}
     */
    @Test
    public void test_accept() {
        doc.addString(VALUE, "Hello")
           .addBoolean(NULL, null)
           .addNumbers(DATA, 0, 1, 2, 3);

        Assert.assertEquals("Unexpected count", 6, (int) doc.accept(new Size()));
    }

    /**
     * Unit test {@link SynchronizedDocument#equals(Object)}
     */
    @Test
    public void test_equals() {
        AbstractDocument<?> backing = (AbstractDocument<?>) Document.newInstance();
        SynchronizedDocument other = new SynchronizedDocument(backing);

        doc.addString(VALUE, "Hello")
           .addBoolean(NULL, null)
           .addNumbers(DATA, 0, 1, 2, 3);
        other.addString(VALUE, "Hello")
           .addBoolean(NULL, null)
           .addNumbers(DATA, 0, 1, 2, 3);

        Document copy = doc.accept(new Copy());

        Assert.assertEquals("Should be equal - same type", doc, other);
        Assert.assertEquals("Should be equal - even if decorators are different", doc, copy);
        Assert.assertNotEquals("Mutated", other, doc.remove(NULL));
    }

    /**
     * Unit test {@link SynchronizedDocument#hashCode()}
     */
    @Test
    public void test_hashCode() {
        AbstractDocument<?> backing = (AbstractDocument<?>) Document.newInstance();
        SynchronizedDocument other = new SynchronizedDocument(backing);
        Document other2 = Document.newInstance()
            .addString(VALUE, "Hello")
            .addBoolean(NULL, null)
            .addNumbers(DATA, 0, 1, 2, 3);

        doc.addString(VALUE, "Hello")
           .addBoolean(NULL, null)
           .addNumbers(DATA, 0, 1, 2, 3);
        other.addString(VALUE, "Hello")
           .addBoolean(NULL, null)
           .addNumbers(DATA, 0, 1, 2, 3);

        Document copy = doc.accept(new Copy());

        Assert.assertEquals("Should have same hash - same type", doc.hashCode(), other.hashCode());
        Assert.assertEquals("Should have same hash - even if decorators are different", doc.hashCode(), other2.hashCode());
        Assert.assertEquals("Copy should have same hash - even if decorators are different", doc.hashCode(), copy.hashCode());
        Assert.assertNotEquals("Mutated", other.hashCode(), doc.addStrings(EMPTY).hashCode());
    }



    @Nonnull
    private <T> List<T> nullList(@Nonnull Class<T> type) {
        List<T> values = new ArrayList<>();
        values.add(null);

        return values;
    }
}