package com.github.tymefly.common.document;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.tymefly.common.document.decorator.NullFilterDocument;
import com.github.tymefly.common.document.decorator.SynchronizedDocument;
import com.github.tymefly.common.document.decorator.UnmodifiableDocument;
import com.github.tymefly.common.document.key.DocumentKey;
import com.github.tymefly.common.document.key.LayeredDocumentKey;
import com.github.tymefly.common.document.visitor.util.PathSet;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link DocumentImpl}
 */
public class DocumentImplTest {
    private static class ExtendedDocument extends DocumentDecorator<ExtendedDocument> {
        protected ExtendedDocument(@Nonnull AbstractDocument<?> wrapped) {
            super(wrapped);
        }

        @Nonnull
        String hello() {
            return "world";
        }
    }


    private enum Type1 {
        DEFAULT,
        ZERO,
        ONE,
        TWO
    }

    private enum Key implements LayeredDocumentKey {
        ROOT,
        BOOL_VALUE,
        BOOL_LIST,
        BOOL_ARRAY,
        STRING_VALUE,
        STRING_LIST,
        STRING_ARRAY,
        NUMBER_VALUE,
        NUMBER_LIST,
        NUMBER_ARRAY,
        ENUM_VALUE,
        ENUM_LIST,
        ENUM_ARRAY,
        DOC_VALUE,
        DOC_LIST,
        DOC_ARRAY,
        EMPTY_VALUE,
        EMPTY_LIST,
        ADD,
        UNKNOWN
    }

    private final ReadableDocument empty = new DocumentImpl(null).unmodifiable();
    private final Document wrapped = Document.factory().withSynchronization().immutable().nullFilter().build();
    private final Document child = new DocumentImpl(null)
            .addString(Key.ROOT, "Child");
    private final Document defaultDoc = new DocumentImpl(null)
            .addString(Key.ROOT, "Default");
    private final Document defaultCopy = new DocumentImpl(null)
            .addString(Key.ROOT, "Default");
    private final Document similar1 = new DocumentImpl(null)
            .addString(Key.BOOL_VALUE, "true")
            .addString(Key.ADD, "2");
    private final Document similar2 = new DocumentImpl(null)
            .addBoolean(Key.BOOL_VALUE, true)
            .addNumber(Key.ADD, 2);
    private final Document sample = new DocumentImpl(null)
            .addString(Key.ROOT, "Parent")
            .addBoolean(Key.BOOL_VALUE, true)
            .addBooleans(Key.BOOL_LIST, Arrays.asList(true, null, false))
            .addBooleans(Key.BOOL_ARRAY, false, null, true)
            .addString(Key.STRING_VALUE, "Hello")
            .addStrings(Key.STRING_LIST, Arrays.asList("Zero", null, "Two"))
            .addStrings(Key.STRING_ARRAY, "0", null, "2")
            .addNumber(Key.NUMBER_VALUE, 1)
            .addNumbers(Key.NUMBER_LIST, Arrays.asList(0, null, -2.3))
            .addNumbers(Key.NUMBER_ARRAY, null, -0.1, 2e3)
            .addEnum(Key.ENUM_VALUE, Type1.ONE)
            .addEnums(Key.ENUM_LIST, Arrays.asList(Type1.ZERO, null, Type1.TWO))
            .addEnums(Key.ENUM_ARRAY, null, Type1.ONE, Type1.TWO, null)
            .addDocument(Key.DOC_VALUE, child)
            .addDocuments(Key.DOC_LIST, Arrays.asList(empty, null, defaultDoc))
            .addDocuments(Key.DOC_ARRAY, null, empty, child)
            .addString(() -> "more[3]", "3")
            .addString(() -> "more[1]", "1")
            .addString(() -> "more[0]", "0");


    /**
     * Unit test {@link DocumentImpl#unmodifiable()}
     */
    @Test
    public void test_immutable() {
        ReadableDocument view = defaultDoc.unmodifiable();

        Assert.assertEquals("before", defaultDoc, view);

        defaultDoc.addString(Key.ADD, "mutated");

        Assert.assertEquals("before", defaultDoc, view);       // Changes are passed through
        Assert.assertEquals("new value", "mutated", view.get(Key.ADD, String.class));

        // Try to force a change
        Assert.assertThrows(Exception.class, () -> ((Document) view).addString(Key.ADD, "mutate"));
    }


    /**
     * Unit test {@link DocumentImpl#empty()}
     */
    @Test
    public void test_empty() {
        Assert.assertEquals("empty test", empty, Document.empty());
    }


    /**
     * Unit test {@link DocumentImpl#get(DocumentKey, Class)}
     */
    @Test
    public void test_get_OriginalType() {
        getHelper("root", "Parent");
        getHelper("bool.value", true);
        getHelper("bool.list[0]", true);
        getHelper("bool.list[2]", false);
        getHelper("bool.array[0]", false);
        getHelper("bool.array[2]", true);
        getHelper("string.value", "Hello");
        getHelper("string.list[0]", "Zero");
        getHelper("string.list[2]", "Two");
        getHelper("string.array[0]", "0");
        getHelper("string.array[2]", "2");
        getHelper("number.value", 1L);
        getHelper("number.list[0]", 0L);
        getHelper("number.list[2]", -2.3);
        getHelper("number.array[1]", new BigDecimal("-0.1"));
        getHelper("number.array[2]", BigInteger.valueOf(2000));
        getHelper("enum.value", Type1.ONE);
        getHelper("enum.list[0]", Type1.ZERO);
        getHelper("enum.list[2]", Type1.TWO);
        getHelper("enum.array[1]", Type1.ONE);
        getHelper("enum.array[2]", Type1.TWO);

        getHelper("doc.value.root", "Child");
        getHelper("doc.list[2].root", "Default");
        getHelper("doc.array[2].root", "Child");

        getHelper("more[0]", "0");
        getHelper("more[1]", "1");
        getHelper("more[3]", "3");
    }


    /**
     * Unit test {@link DocumentImpl#get(DocumentKey, Class)}
     */
    @Test
    public void test_get_asString_primitives() {
        getHelper("root", "Parent");
        getHelper("bool.value", "true");
        getHelper("bool.list[0]", "true");
        getHelper("bool.list[2]", "false");
        getHelper("bool.array[0]", "false");
        getHelper("bool.array[2]", "true");
        getHelper("string.value", "Hello");
        getHelper("string.list[0]", "Zero");
        getHelper("string.list[2]", "Two");
        getHelper("string.array[0]", "0");
        getHelper("string.array[2]", "2");
        getHelper("number.value", "1");
        getHelper("number.list[0]", "0");
        getHelper("number.list[2]", "-2.3");
        getHelper("number.array[1]", "-0.1");
        getHelper("number.array[2]", "2000.0");
        getHelper("enum.value", "ONE");
        getHelper("enum.list[0]", "ZERO");
        getHelper("enum.list[2]", "TWO");
        getHelper("enum.array[1]", "ONE");
        getHelper("enum.array[2]", "TWO");
        getHelper("doc.value.root", "Child");
        getHelper("doc.list[2].root", "Default");
        getHelper("doc.array[2].root", "Child");
        getHelper("more[0]", "0");
        getHelper("more[1]", "1");
        getHelper("more[3]", "3");
    }


    /**
     * Unit test {@link DocumentImpl#get(DocumentKey, Class)}
     */
    @Test
    public void test_get_asNumber_primitives() {
        getHelper("string.array[0]", 0);
        getHelper("string.array[2]", 2);
        getHelper("number.value", 1);
        getHelper("number.list[0]", 0);
        getHelper("number.list[2]", -2.3);
        getHelper("number.array[1]", -0.1);
        getHelper("number.array[2]", 2000.0);
        getHelper("more[0]", 0);
        getHelper("more[1]", 1);
        getHelper("more[3]", 3);
    }


    /**
     * Unit test {@link DocumentImpl#get(DocumentKey, Class)}
     */
    @Test
    public void test_get_asString_sequences() {
        getHelper("bool.list", "[true, null, false]");
        getHelper("bool.array", "[false, null, true]");
        getHelper("string.list", "[Zero, null, Two]");
        getHelper("string.array", "[0, null, 2]");
        getHelper("number.list", "[0, null, -2.3]");
        getHelper("number.array", "[null, -0.1, 2000.0]");
        getHelper("enum.list", "[ZERO, null, TWO]");
        getHelper("enum.array", "[null, ONE, TWO, null]");
        getHelper("doc.list", "[{}, null, {\"root\":\"Default\"}]");
        getHelper("doc.array", "[null, {}, {\"root\":\"Child\"}]");
        getHelper("more", "[0, 1, null, 3]");
    }


    /**
     * Unit test {@link DocumentImpl#get(DocumentKey, Class)}
     */
    @Test
    public void test_get_asString_structure() {
        getHelper("doc.value", "{\"root\":\"Child\"}");
        getHelper("doc.array[1]", "{}");
    }

    /**
     * Unit test {@link DocumentImpl#get(DocumentKey, Class)}
     */
    @Test
    public void test_get_Object() {
        getObjectHelper("root", "Parent");
        getObjectHelper("bool.value", true);
        getObjectHelper("bool.list", Arrays.asList(true, null, false));
        getObjectHelper("bool.list[0]", true);
        getObjectHelper("bool.list[2]", false);
        getObjectHelper("bool.array", Arrays.asList(false, null, true));
        getObjectHelper("bool.array[0]", false);
        getObjectHelper("bool.array[2]", true);
        getObjectHelper("string.value", "Hello");
        getObjectHelper("string.list", Arrays.asList("Zero", null, "Two"));
        getObjectHelper("string.list[0]", "Zero");
        getObjectHelper("string.list[2]", "Two");
        getObjectHelper("string.array", Arrays.asList("0", null, "2"));
        getObjectHelper("string.array[0]", "0");
        getObjectHelper("string.array[2]", "2");
        getObjectHelper("number.value", BigDecimal.ONE);
        getObjectHelper("number.list", Arrays.asList(new BigDecimal(0), null, new BigDecimal("-2.3")));
        getObjectHelper("number.list[0]", BigDecimal.ZERO);
        getObjectHelper("number.list[2]", new BigDecimal("-2.3"));
        getObjectHelper("number.array", Arrays.asList(null, new BigDecimal("-0.1"), new BigDecimal("2000.0")));
        getObjectHelper("number.array[1]", new BigDecimal("-0.1"));
        getObjectHelper("number.array[2]", new BigDecimal("2000.0"));
        getObjectHelper("enum.value", Type1.ONE);
        getObjectHelper("enum.list", Arrays.asList(Type1.ZERO, null, Type1.TWO));
        getObjectHelper("enum.list[0]", Type1.ZERO);
        getObjectHelper("enum.list[2]", Type1.TWO);
        getObjectHelper("enum.array", Arrays.asList(null, Type1.ONE, Type1.TWO, null));
        getObjectHelper("enum.array[1]", Type1.ONE);
        getObjectHelper("enum.array[2]", Type1.TWO);
    }


    /**
     * Unit test {@link DocumentImpl#getOrDefault(DocumentKey, Object)}
     */
    @Test
    public void test_get_WithDefault() {
        getHelper("root", "???", "Parent");
        getHelper("bool.value", false, true);
        getHelper("bool.list[0]", false, true);
        getHelper("bool.list[1]", false, false);
        getHelper("bool.list[2]", true, false);
        getHelper("bool.array[0]", false, false);
        getHelper("bool.array[1]", true, true);
        getHelper("bool.array[2]", false, true);
        getHelper("string.value", "???", "Hello");
        getHelper("string.list[0]", "???", "Zero");
        getHelper("string.list[1]", "???", "???");
        getHelper("string.list[2]", "???", "Two");
        getHelper("string.array[0]", "???", "0");
        getHelper("string.array[1]", "???", "???");
        getHelper("string.array[2]", "???", "2");
        getHelper("number.value", -999L, 1L);
        getHelper("number.list[0]", -999L, 0L);
        getHelper("number.list[1]", -999, -999);
        getHelper("number.list[2]", -999.0, -2.3);
        getHelper("number.array[0]", -999, -999);
        getHelper("number.array[1]", new BigDecimal("-999"), new BigDecimal("-0.1"));
        getHelper("number.array[2]", BigInteger.valueOf(-999), BigInteger.valueOf(2000));
        getHelper("enum.value", Type1.DEFAULT, Type1.ONE);
        getHelper("enum.list[0]", Type1.DEFAULT, Type1.ZERO);
        getHelper("enum.list[1]", Type1.DEFAULT, Type1.DEFAULT);
        getHelper("enum.list[2]", Type1.DEFAULT, Type1.TWO);
        getHelper("enum.array[0]", Type1.DEFAULT, Type1.DEFAULT);
        getHelper("enum.array[1]", Type1.DEFAULT, Type1.ONE);
        getHelper("enum.array[2]", Type1.DEFAULT, Type1.TWO);

        getHelper("doc.value", defaultDoc, child);
        getHelper("doc.list[0]", empty, empty);
        getHelper("doc.list[1]", defaultDoc, defaultDoc);
        getHelper("doc.list[2]", defaultDoc, defaultDoc);
        getHelper("doc.array[0]", defaultDoc, defaultDoc);
        getHelper("doc.array[1]", defaultDoc, empty);
        getHelper("doc.array[2]", defaultDoc, child);

        getHelper("doc.list[0]", empty, wrapped);
        getHelper("doc.list[2]", wrapped, defaultDoc);
        getHelper("doc.list[99]", wrapped, wrapped);

        sample.addDocument(() -> "doc.list[2]", wrapped);
        getHelper("doc.list[2]", empty, wrapped);
    }

    /**
     * Unit test {@link DocumentImpl#getOrDefault(DocumentKey, Object)}
     */
    @Test
    public void test_get_WithDefault_asNumber() {
        getHelper("string.array[0]", (byte) 99, (byte) 0);
        getHelper("string.array[1]", (byte) 99, (byte) 99);
        getHelper("string.array[2]", (byte) 99, (byte) 2);
        getHelper("number.value", (short) 99, (short) 1);
        getHelper("number.list[0]", (float) 99, (float) 0);
        getHelper("number.list[1]", (float) 99, (float) 99);
        getHelper("number.list[2]", (float) 99, (float) -2.3);
        getHelper("number.array[0]", (double) 99, 99.0);
        getHelper("number.array[1]", (double) 99, -0.1);
        getHelper("number.array[2]", (double) 99, 2000.0);
        getHelper("more[0]", 99, 0);
        getHelper("more[1]", 99, 1);
        getHelper("more[2]", 99, 99);
        getHelper("more[3]", 99, 3);
    }


    /**
     * Unit test {@link DocumentImpl#get(DocumentKey, Function)}
     */
    @Test
    public void test_get_WithConversion() {
        Instant now = Instant.now();

        sample.addString(Key.ADD, now.toString());

        Assert.assertEquals("Failed to read time", now.toString(), sample.get(Key.ADD, String.class));
        Assert.assertEquals("Failed to convert time", now, sample.get(Key.ADD, Instant::parse));
    }


    /**
     * Unit test {@link DocumentImpl#getOptional(DocumentKey, Class)}
     */
    @Test
    public void test_getOptional_Primitive() {
        getOptionalHelper("root", "Parent", String.class);
        getOptionalHelper("bool.value", true, Boolean.class);
        getOptionalHelper("bool.list[0]", true, Boolean.class);
        getOptionalHelper("bool.list[1]", null, Boolean.class);
        getOptionalHelper("bool.list[2]", false, Boolean.class);
        getOptionalHelper("bool.array[0]", false, Boolean.class);
        getOptionalHelper("bool.array[1]", null, Boolean.class);
        getOptionalHelper("bool.array[2]", true, Boolean.class);
        getOptionalHelper("bool.array[99]", null, Boolean.class);
        getOptionalHelper("string.value", "Hello", String.class);
        getOptionalHelper("string.list[0]", "Zero", String.class);
        getOptionalHelper("string.list[1]", null, String.class);
        getOptionalHelper("string.list[2]", "Two", String.class);
        getOptionalHelper("string.array[0]", "0", String.class);
        getOptionalHelper("string.array[1]", null, String.class);
        getOptionalHelper("string.array[2]", "2", String.class);
        getOptionalHelper("string.array[99]", null, String.class);
        getOptionalHelper("number.value", 1L, Long.class);
        getOptionalHelper("number.list[0]", 0L, Long.class);
        getOptionalHelper("number.list[1]", null, Double.class);
        getOptionalHelper("number.list[2]", -2.3, Double.class);
        getOptionalHelper("number.array[0]", null, Double.class);
        getOptionalHelper("number.array[1]", new BigDecimal("-0.1"), BigDecimal.class);
        getOptionalHelper("number.array[2]", BigInteger.valueOf(2000), BigInteger.class);
        getOptionalHelper("number.array[99]", null, BigInteger.class);
        getOptionalHelper("enum.value", Type1.ONE, Type1.class);
        getOptionalHelper("enum.list[0]", Type1.ZERO, Type1.class);
        getOptionalHelper("enum.list[1]", null, Type1.class);
        getOptionalHelper("enum.list[2]", Type1.TWO, Type1.class);
        getOptionalHelper("enum.array[0]", null, Type1.class);
        getOptionalHelper("enum.array[1]", Type1.ONE, Type1.class);
        getOptionalHelper("enum.array[2]", Type1.TWO, Type1.class);
        getOptionalHelper("enum.array[99]", null, Type1.class);

        getOptionalHelper("doc.value.root", "Child", String.class);
        getOptionalHelper("doc.list[0].root", null, String.class);
        getOptionalHelper("doc.list[1].root", null, String.class);
        getOptionalHelper("doc.list[2].root", "Default", String.class);
        getOptionalHelper("doc.array[0].root", null, String.class);
        getOptionalHelper("doc.array[1].root", null, String.class);
        getOptionalHelper("doc.array[2].root", "Child", String.class);
        getOptionalHelper("doc.array[99].root", null, String.class);

        getOptionalHelper("more[0]", "0", String.class);
        getOptionalHelper("more[1]", "1", String.class);
        getOptionalHelper("more[2]", null, String.class);
        getOptionalHelper("more[3]", "3", String.class);
        getOptionalHelper("more[99]", null, String.class);

        getOptionalHelper("add", null, String.class);

        getOptionalHelper("doc.value", "{\"root\":\"Child\"}", String.class);
        getOptionalHelper("doc.list[0]", "{}", String.class);
        getOptionalHelper("doc.list[1]", null, String.class);
        getOptionalHelper("doc.list[2]", "{\"root\":\"Default\"}", String.class);
        getOptionalHelper("doc.array[0]", null, String.class);
        getOptionalHelper("doc.array[1]", "{}", String.class);
        getOptionalHelper("doc.array[2]",  "{\"root\":\"Child\"}", String.class);
        getOptionalHelper("doc.array[99]", null, String.class);
    }


    /**
     * Unit test {@link DocumentImpl#getOptional(DocumentKey, Class)}
     */
    @Test
    public void test_getOptional_OriginalType() {
        getOptionalHelper("root", "Parent", Object.class);
        getOptionalHelper("bool.value", true, Object.class);
        getOptionalHelper("bool.list[0]", true, Object.class);
        getOptionalHelper("bool.list[1]", null, Object.class);
        getOptionalHelper("bool.list[2]", false, Object.class);
        getOptionalHelper("bool.array[0]", false, Object.class);
        getOptionalHelper("bool.array[1]", null, Object.class);
        getOptionalHelper("bool.array[2]", true, Object.class);
        getOptionalHelper("bool.array[99]", null, Object.class);
        getOptionalHelper("string.value", "Hello", Object.class);
        getOptionalHelper("string.list[0]", "Zero", Object.class);
        getOptionalHelper("string.list[1]", null, Object.class);
        getOptionalHelper("string.list[2]", "Two", Object.class);
        getOptionalHelper("string.array[0]", "0", Object.class);
        getOptionalHelper("string.array[1]", null, Object.class);
        getOptionalHelper("string.array[2]", "2", Object.class);
        getOptionalHelper("string.array[99]", null, Object.class);
        getOptionalHelper("number.value", BigDecimal.ONE, Object.class);
        getOptionalHelper("number.list[0]", BigDecimal.ZERO, Object.class);
        getOptionalHelper("number.list[1]", null, Object.class);
        getOptionalHelper("number.list[2]", new BigDecimal("-2.3"), Object.class);
        getOptionalHelper("number.array[0]", null, Object.class);
        getOptionalHelper("number.array[1]", new BigDecimal("-0.1"), Object.class);
        getOptionalHelper("number.array[2]", new BigDecimal("2000.0"), Object.class);
        getOptionalHelper("number.array[99]", null, Object.class);
        getOptionalHelper("enum.value", Type1.ONE, Object.class);
        getOptionalHelper("enum.list[0]", Type1.ZERO, Object.class);
        getOptionalHelper("enum.list[1]", null, Object.class);
        getOptionalHelper("enum.list[2]", Type1.TWO, Object.class);
        getOptionalHelper("enum.array[0]", null, Object.class);
        getOptionalHelper("enum.array[1]", Type1.ONE, Object.class);
        getOptionalHelper("enum.array[2]", Type1.TWO, Object.class);
        getOptionalHelper("enum.array[99]", null, Object.class);

        getOptionalHelper("doc.value.root", "Child", Object.class);
        getOptionalHelper("doc.list[0].root", null, Object.class);
        getOptionalHelper("doc.list[1].root", null, Object.class);
        getOptionalHelper("doc.list[2].root", "Default", Object.class);
        getOptionalHelper("doc.array[0].root", null, Object.class);
        getOptionalHelper("doc.array[1].root", null, Object.class);
        getOptionalHelper("doc.array[2].root", "Child", Object.class);
        getOptionalHelper("doc.array[99].root", null, Object.class);

        getOptionalHelper("more[0]", "0", Object.class);
        getOptionalHelper("more[1]", "1", Object.class);
        getOptionalHelper("more[2]", null, Object.class);
        getOptionalHelper("more[3]", "3", Object.class);
        getOptionalHelper("more[99]", null, Object.class);

        getOptionalHelper("add", null, Object.class);

        getOptionalHelper("doc.value", child, Object.class);
        getOptionalHelper("doc.list[0]", empty, Object.class);
        getOptionalHelper("doc.list[1]", null, Object.class);
        getOptionalHelper("doc.list[2]", defaultDoc, Object.class);
        getOptionalHelper("doc.array[0]", null, Object.class);
        getOptionalHelper("doc.array[1]", empty, Object.class);
        getOptionalHelper("doc.array[2]", child, Object.class);
        getOptionalHelper("doc.array[99]", null, Object.class);
    }


    /**
     * Unit test {@link DocumentImpl#getOptional(DocumentKey, Class)}
     */
    @Test
    public void test_getOptional_Number() {
        getOptionalHelper("string.array[0]", 0L, Long.class);
        getOptionalHelper("string.array[1]", null, Long.class);
        getOptionalHelper("string.array[2]", 2L, Long.class);
        getOptionalHelper("string.array[99]", null, Long.class);
        getOptionalHelper("number.value", 1, Integer.class);
        getOptionalHelper("number.list[0]", 0, Integer.class);
        getOptionalHelper("number.list[1]", null, Integer.class);
        getOptionalHelper("number.list[2]", -2, Integer.class);
        getOptionalHelper("number.array[0]", null, BigDecimal.class);
        getOptionalHelper("number.array[1]", new BigDecimal("-0.1"), BigDecimal.class);
        getOptionalHelper("number.array[2]", BigInteger.valueOf(2000), BigInteger.class);
        getOptionalHelper("number.array[99]", null, BigInteger.class);

        getOptionalHelper("more[0]", (short) 0, Short.class);
        getOptionalHelper("more[1]", (short) 1, Short.class);
        getOptionalHelper("more[2]", null, Short.class);
        getOptionalHelper("more[3]", (short) 3, Short.class);
        getOptionalHelper("more[99]", null, Short.class);
    }


    /**
     * Unit test {@link DocumentImpl#getOptional(DocumentKey, Class)}
     */
    @Test
    public void test_getOptional_Document() {
        getOptionalHelper("doc.value", child, Document.class);
        getOptionalHelper("doc.list[0]", empty, Document.class);
        getOptionalHelper("doc.list[1]", null, Document.class);
        getOptionalHelper("doc.list[2]", defaultDoc, Document.class);
        getOptionalHelper("doc.array[0]", null, Document.class);
        getOptionalHelper("doc.array[1]", empty, Document.class);
        getOptionalHelper("doc.array[2]",  child, Document.class);
        getOptionalHelper("doc.array[99]", null, Document.class);
    }


    /**
     * Unit test {@link DocumentImpl#getAll(DocumentKey, Class)}
     */
    @Test
    public void test_getAll() {
        getAllHelper("bool.value", Arrays.asList(true), Boolean.class);
        getAllHelper("bool.list", Arrays.asList("true", null, "false"), String.class);
        getAllHelper("bool.array", Arrays.asList(false, null, true), Boolean.class);
        getAllHelper("string.value", Arrays.asList("Hello"), String.class);
        getAllHelper("string.list", Arrays.asList("Zero", null, "Two"), String.class);
        getAllHelper("string.array", Arrays.asList("0", null, "2"), String.class);
        getAllHelper("number.value", Arrays.asList(1L), Long.class);
        getAllHelper("number.list", Arrays.asList(0.0, null, -2.3), Double.class);
        getAllHelper("number.array", Arrays.asList(null, new BigDecimal("-0.1"), new BigDecimal("2000.0")), BigDecimal.class);
        getAllHelper("enum.value", Arrays.asList(Type1.ONE), Type1.class);
        getAllHelper("enum.list", Arrays.asList(Type1.ZERO, null, Type1.TWO), Type1.class);
        getAllHelper("enum.array", Arrays.asList(null, Type1.ONE, Type1.TWO, null), Type1.class);
        getAllHelper("doc.value", Arrays.asList(child), Document.class);
        getAllHelper("doc.list", Arrays.asList(empty, null, defaultDoc), CommonDocument.class);
        getAllHelper("doc.array", Arrays.asList(null, empty, child), ReadableDocument.class);

        getAllHelper("more", Arrays.asList("0", "1", null, "3"), String.class);          // Change type
        getAllHelper("add", Arrays.asList(), String.class);                              // missing items
    }


    /**
     * Unit test {@link DocumentImpl#get(DocumentKey, Class)}
     */
    @Test
    public void test_get_BadNumber() {
        Assert.assertThrows(DocumentException.class, () -> sample.get(Key.ROOT, Long.class));
    }


    /**
     * Unit test {@link DocumentImpl#get(DocumentKey, Class)}
     */
    @Test
    public void test_get_BadEnum() {
        Assert.assertThrows(DocumentException.class, () -> sample.get(Key.ROOT, Type1.class));
    }


    /**
     * Unit test {@link DocumentImpl#get(DocumentKey, Class)}
     */
    @Test
    public void test_get_null() {
        Assert.assertThrows(NullPointerException.class, () -> sample.get(() -> "more[2]", String.class));
    }


    /**
     * Unit test {@link DocumentImpl#get(DocumentKey, Class)}
     */
    @Test
    public void test_get_BadKeys() {
        Assert.assertThrows("No Key", DocumentException.class, () -> sample.get(() -> "", String.class));
        Assert.assertThrows("Just dot", DocumentException.class, () -> sample.get(() -> ".", String.class));
        Assert.assertThrows("No index", DocumentException.class, () -> sample.get(() -> "x[]", String.class));
        Assert.assertThrows("Missing ]", DocumentException.class, () -> sample.get(() -> "x[12", String.class));
        Assert.assertThrows("Negative index", DocumentException.class, () -> sample.get(() -> "x[-1]", String.class));
        Assert.assertThrows("Text in index", DocumentException.class, () -> sample.get(() -> "x[one]", String.class));
        Assert.assertThrows("Bad Character", DocumentException.class, () -> sample.get(() -> "~", String.class));
    }


    /**
     * Unit test {@link DocumentImpl#addString(DocumentKey, String)}
     */
    @Test
    public void test_badInsert() {
        Assert.assertThrows(DocumentException.class,
                () -> sample.addString(() -> Key.ROOT.externalise() + "[2]", "bad"));
    }


    /**
     * Unit test {@link DocumentImpl#get(DocumentKey, Class)}
     */
    @Test
    public void test_add_badType() {
        Assert.assertThrows(DocumentException.class, () -> sample.addString(() -> "number.list[1]", "Not a Number"));
    }


    /**
     * Unit test {@link DocumentImpl#appendString(DocumentKey, String)}
     */
    @Test
    public void test_append_String() {
        Assert.assertEquals("Append to existing sequence",
                new DocumentImpl(null)
                        .addStrings(Key.STRING_LIST, "A", "B", "C"),
                new DocumentImpl(null)
                        .addStrings(Key.STRING_LIST, "A", "B")
                        .appendString(Key.STRING_LIST, "C"));

        Assert.assertEquals("Append to missing sequence",
                new DocumentImpl(null)
                        .addStrings(Key.STRING_LIST, "new"),
                new DocumentImpl(null)
                        .appendString(Key.STRING_LIST, "new"));

        Assert.assertEquals("Append multiple times",
                new DocumentImpl(null)
                        .addStrings(Key.STRING_LIST, "1", "2", null, "4"),
                new DocumentImpl(null)
                        .appendString(Key.STRING_LIST, "1")
                        .appendString(Key.STRING_LIST, "2")
                        .appendString(Key.STRING_LIST, null)
                        .appendString(Key.STRING_LIST, "4"));
    }

    /**
     * Unit test {@link DocumentImpl#appendNumber(DocumentKey, Number)}
     */
    @Test
    public void test_append_Number() {
        Assert.assertEquals("Append to existing sequence",
                new DocumentImpl(null)
                        .addNumbers(Key.NUMBER_LIST, 1, 2, 3),
                new DocumentImpl(null)
                        .addNumbers(Key.NUMBER_LIST, 1, 2)
                        .appendNumber(Key.NUMBER_LIST, 3));

        Assert.assertEquals("Append to missing sequence",
                new DocumentImpl(null)
                        .addNumbers(Key.NUMBER_LIST, 99),
                new DocumentImpl(null)
                        .appendNumber(Key.NUMBER_LIST, 99));

        Assert.assertEquals("Append multiple times",
                new DocumentImpl(null)
                        .addNumbers(Key.NUMBER_LIST, null, 2, 3, 4),
                new DocumentImpl(null)
                        .appendNumber(Key.NUMBER_LIST, null)
                        .appendNumber(Key.NUMBER_LIST, 2)
                        .appendNumber(Key.NUMBER_LIST, 3)
                        .appendNumber(Key.NUMBER_LIST, 4));
    }

    /**
     * Unit test {@link DocumentImpl#appendBoolean(DocumentKey, Boolean)}
     */
    @Test
    public void test_append_Boolean() {
        Assert.assertEquals("Append to existing sequence",
                new DocumentImpl(null)
                        .addBooleans(Key.BOOL_LIST, true, false, true),
                new DocumentImpl(null)
                        .addBooleans(Key.BOOL_LIST, true, false)
                        .appendBoolean(Key.BOOL_LIST, true));

        Assert.assertEquals("Append to missing sequence",
                new DocumentImpl(null)
                        .addBooleans(Key.BOOL_LIST, true),
                new DocumentImpl(null)
                        .appendBoolean(Key.BOOL_LIST, true));

        Assert.assertEquals("Append multiple times",
                new DocumentImpl(null)
                        .addBooleans(Key.BOOL_LIST, true, false, null),
                new DocumentImpl(null)
                        .appendBoolean(Key.BOOL_LIST, true)
                        .appendBoolean(Key.BOOL_LIST, false)
                        .appendBoolean(Key.BOOL_LIST, null));
    }

    /**
     * Unit test {@link DocumentImpl#appendEnum(DocumentKey, Enum)}
     */
    @Test
    public void test_append_Enum() {
        Assert.assertEquals("Append to existing sequence",
                new DocumentImpl(null)
                        .addEnums(Key.ENUM_LIST, Type1.ZERO, null, Type1.TWO),
                new DocumentImpl(null)
                        .addEnums(Key.ENUM_LIST, Type1.ZERO, null)
                        .appendEnum(Key.ENUM_LIST, Type1.TWO));

        Assert.assertEquals("Append to missing sequence",
                new DocumentImpl(null)
                        .addEnums(Key.ENUM_LIST, Type1.ZERO),
                new DocumentImpl(null)
                        .appendEnum(Key.ENUM_LIST, Type1.ZERO));

        Assert.assertEquals("Append multiple times",
                new DocumentImpl(null)
                        .addEnums(Key.ENUM_LIST, Type1.ZERO, Type1.ONE, Type1.TWO),
                new DocumentImpl(null)
                        .appendEnum(Key.ENUM_LIST, Type1.ZERO)
                        .appendEnum(Key.ENUM_LIST, Type1.ONE)
                        .appendEnum(Key.ENUM_LIST, Type1.TWO));
    }

    /**
     * Unit test {@link DocumentImpl#appendDocument(DocumentKey, CommonDocument)}
     */
    @Test
    public void test_append_Document() {
        Assert.assertEquals("Append to existing sequence",
                new DocumentImpl(null)
                        .addDocuments(Key.DOC_LIST, empty, defaultDoc, sample),
                new DocumentImpl(null)
                        .addDocuments(Key.DOC_LIST, empty, defaultDoc)
                        .appendDocument(Key.DOC_LIST, sample));

        Assert.assertEquals("Append to missing sequence",
                new DocumentImpl(null)
                        .addDocuments(Key.DOC_LIST, sample),
                new DocumentImpl(null)
                        .appendDocument(Key.DOC_LIST, sample));

        Assert.assertEquals("Append multiple times",
                new DocumentImpl(null)
                        .addDocuments(Key.DOC_LIST, empty, defaultDoc, null, sample),
                new DocumentImpl(null)
                        .appendDocument(Key.DOC_LIST, empty)
                        .appendDocument(Key.DOC_LIST, defaultDoc)
                        .appendDocument(Key.DOC_LIST, null)
                        .appendDocument(Key.DOC_LIST, sample));
    }

    /**
     * Unit test {@link DocumentImpl#appendString(DocumentKey, String)} 
     */
    @Test
    public void test_append_ElementExists() {
        Assert.assertThrows("Overwrite String",
                DocumentException.class,
                () -> new DocumentImpl(null)
                        .addString(Key.ADD, "X")
                        .appendString(Key.ADD, "Y"));

        Assert.assertThrows("Overwrite null",
                DocumentException.class,
                () -> new DocumentImpl(null)
                        .addString(Key.ADD, null)
                        .appendString(Key.ADD, "Y"));
    }

    /**
     * Unit test {@link DocumentImpl#appendString(DocumentKey, String)}
     */
    @Test
    public void test_append_BadType() {
        Assert.assertThrows(DocumentException.class,
                () -> new DocumentImpl(null)
                        .addNumbers(Key.ADD, 1, 2)
                        .appendString(Key.ADD, "Three"));
    }

    /**
     * Unit test {@link DocumentImpl#appendString(DocumentKey, String)}
     */
    @Test
    public void test_append_intoSequence() {
        Assert.assertThrows(DocumentException.class,
                () -> new DocumentImpl(null)
                        .appendString(() -> "x[2]", "???"));
    }


    /**
     * Unit test {@link DocumentImpl#remove(DocumentKey)}
     */
    @Test
    public void test_remove_field() {
        Document test = new DocumentImpl(null)
            .addString(Key.ROOT, "root")
            .addBoolean(Key.ADD, true);

        Assert.assertEquals("initial state", "{\"root\":\"root\",\"add\":true}", test.toString());

        test.remove(Key.ADD);
        Assert.assertEquals("remove 'ADD'", "{\"root\":\"root\"}", test.toString());

        test.remove(Key.UNKNOWN);
        Assert.assertEquals("remove 'UNKNOWN'", "{\"root\":\"root\"}", test.toString());

        test.remove(Key.ROOT);
        Assert.assertEquals("remove 'ROOT'", "{}", test.toString());

        Assert.assertEquals("should be empty", empty, test);
    }


    /**
     * Unit test {@link DocumentImpl#remove(DocumentKey)}
     */
    @Test
    public void test_remove_inSequence() {
        Document test = new DocumentImpl(null)
                .addStrings(Key.ROOT, "0", "1", "2", "3");

        Assert.assertEquals("initial state", "{\"root\":[\"0\",\"1\",\"2\",\"3\"]}", test.toString());

        test.remove(() -> "root[4]");
        Assert.assertEquals("remove beyond array", "{\"root\":[\"0\",\"1\",\"2\",\"3\"]}", test.toString());

        test.remove(() -> "root[1]");
        Assert.assertEquals("remove 'root[1]'", "{\"root\":[\"0\",null,\"2\",\"3\"]}", test.toString());

        test.remove(() -> "root[3]");
        Assert.assertEquals("remove 'root[3]'", "{\"root\":[\"0\",null,\"2\"]}", test.toString());

        test.remove(() -> "root[2]");
        Assert.assertEquals("remove 'root[2]'", "{\"root\":[\"0\"]}", test.toString());

        test.remove(() -> "root[0]");
        Assert.assertEquals("remove 'root[0]'", "{}", test.toString());
        Assert.assertEquals("should be empty", empty, test);

        test.remove(() -> "root[5]");
        Assert.assertEquals("remove 'root[5]' (sequence doesn't exist)", "{}", test.toString());
    }


    /**
     * Unit test {@link DocumentImpl#equals(Object)}
     */
    @Test
    public void test_equals() {
        Assert.assertNotEquals("Equal To Null", defaultDoc, null);
        Assert.assertNotEquals("Equal To OtherType", defaultDoc, "hello");
        Assert.assertNotEquals("Equal To Empty", defaultDoc, empty);
        Assert.assertNotEquals("Empty Arrays change equal()", empty, new DocumentImpl(null).addStrings(Key.ADD));
        Assert.assertEquals("Equal To Self", defaultDoc, defaultDoc);
        Assert.assertEquals("Equal To Copy", defaultDoc, defaultCopy);
        Assert.assertNotEquals("Change value type", similar1, similar2);
        Assert.assertNotEquals("Different documents", similar1, sample);

        Assert.assertEquals("Equal to immutable", empty, Document.empty());

        similar1.addString(Key.BOOL_VALUE, "TRUE");
        Assert.assertNotEquals("Change case of boolean value", similar1, similar2);
    }


    /**
     * Unit test {@link DocumentImpl#equals(Object)}
     */
    @Test
    public void test_equals_emptyArray() {
        Document left = new DocumentImpl(null).addStrings(Key.ADD);
        Document right = new DocumentImpl(null);

        Assert.assertNotEquals("Empty array changes equals", left, right);
    }


    /**
     * Unit test {@link Document#hashCode()}
     */
    @Test
    public void test_hashCode() {
        Document one_a = new DocumentImpl(null)
                .addString(() -> "A", "A");
        Document one_b = new DocumentImpl(null)
                .addString(() -> "A", "A");
        Document two = new DocumentImpl(null)
                .addString(() -> "A", "B");
        Document three = new DocumentImpl(null)
                .addNumber(() -> "A", 123);
        Document four = new DocumentImpl(null)
                .addString(() -> "A.B", "A");
        Document five_a = new DocumentImpl(null)
                .addString(() -> "A[1].B", "A");
        Document five_b = new DocumentImpl(null)
                .addString(() -> "A[2].B", "A");
        Document six = new DocumentImpl(null)
                .addString(() -> "A", "A")
                .addString(() -> "B", "Hello");

        Assert.assertEquals("Equals objects", one_a.hashCode(), one_b.hashCode());
        Assert.assertNotEquals("Different value", one_a.hashCode(), two.hashCode());
        Assert.assertNotEquals("Different type", one_a.hashCode(), three.hashCode());
        Assert.assertNotEquals("Child element", one_a.hashCode(), four.hashCode());
        Assert.assertNotEquals("Child array", one_a.hashCode(), five_a.hashCode());
        Assert.assertNotEquals("Different array index", five_b.hashCode(), five_a.hashCode());
        Assert.assertNotEquals("Different size", one_a.hashCode(), six.hashCode());
    }


    /**
     * Unit test {@link DocumentImpl#isEmpty()}
     */
    @Test
    public void test_isEmpty() {
        Assert.assertTrue("empty", empty.isEmpty());
        Assert.assertFalse("sample", sample.isEmpty());
        Assert.assertFalse("empty string", new DocumentImpl(null).addString(Key.ADD, "").isEmpty());
        Assert.assertFalse("null string", new DocumentImpl(null).addString(Key.ADD, null).isEmpty());
    }


    /**
     * Unit test {@link DocumentImpl#contains(DocumentKey)}
     */
    @Test
    public void test_contains() {
        sample.addString(Key.ADD, null);

        Assert.assertTrue("Expected key", sample.contains(Key.STRING_VALUE));
        Assert.assertTrue("Expected child key", sample.contains(() -> "doc.value.root"));
        Assert.assertFalse("Unexpected key", sample.contains(Key.UNKNOWN));
        Assert.assertTrue("Expected null", sample.contains(Key.ADD));

        Assert.assertTrue("Expected Array element", sample.contains(() -> "string.list[0]"));
        Assert.assertTrue("Null Array element", sample.contains(() -> "string.list[1]"));
        Assert.assertFalse("Beyond end of Array", sample.contains(() -> "string.list[3]"));

        sample.remove(Key.STRING_VALUE);
        sample.remove(() -> "string.list[3]");                  // never existed
        sample.remove(() -> "string.list[2]");                  // actual last item

        Assert.assertFalse("Removed field", sample.contains(Key.STRING_VALUE));
        Assert.assertTrue("Array elements", sample.contains(() -> "string.list"));
        Assert.assertFalse("Remove Array element[3]", sample.contains(() -> "string.list[3]"));
        Assert.assertFalse("Remove Array element[2]", sample.contains(() -> "string.list[2]"));
        Assert.assertFalse("Previously null Array element[1]", sample.contains(() -> "string.list[1]"));
        Assert.assertTrue("Not yet removed Array element[0]", sample.contains(() -> "string.list[0]"));

        sample.remove(() -> "string.list[0]");                  // remove last item

        Assert.assertFalse("Remove Array element[0]", sample.contains(() -> "string.list[0]"));
        Assert.assertFalse("Removed array", sample.contains(() -> "string.list"));
    }

    /**
     * Unit test {@link DocumentImpl#isSequence(DocumentKey)}
     */
    @Test
    public void test_isSequence() {
        Assert.assertFalse(sample.isSequence(() ->"root"));
        Assert.assertFalse(sample.isSequence(() ->"bool.value"));
        Assert.assertFalse(sample.isSequence(() ->"bool.list[0]"));
        Assert.assertFalse(sample.isSequence(() ->"bool.list[1]"));
        Assert.assertFalse(sample.isSequence(() ->"bool.list[2]"));
        Assert.assertTrue(sample.isSequence(() ->"bool.list"));
        Assert.assertFalse(sample.isSequence(() ->"bool.array[0]"));
        Assert.assertFalse(sample.isSequence(() ->"bool.array[1]"));
        Assert.assertFalse(sample.isSequence(() ->"bool.array[2]"));
        Assert.assertTrue(sample.isSequence(() ->"bool.array"));

        Assert.assertFalse(sample.isSequence(() ->"bool.array[99]"));
        Assert.assertFalse(sample.isSequence(() ->"unknown"));
    }


    /**
     * Unit test {@link DocumentImpl#hasValue(DocumentKey)}
     */
    @Test
    public void test_hasValue() {
        sample.addString(Key.ADD, null);

        Assert.assertTrue("Expected key", sample.hasValue(Key.STRING_VALUE));
        Assert.assertTrue("Expected child key", sample.hasValue(() -> "doc.value.root"));
        Assert.assertFalse("Unexpected key", sample.hasValue(Key.UNKNOWN));
        Assert.assertFalse("Unexpected null", sample.hasValue(Key.ADD));

        Assert.assertTrue("Expected Array element", sample.hasValue(() -> "string.list[0]"));
        Assert.assertFalse("Null Array element", sample.hasValue(() -> "string.list[1]"));
        Assert.assertFalse("Beyond end of Array", sample.hasValue(() -> "string.list[3]"));
    }


    /**
     * Unit test {@link DocumentImpl#wraps(Class)}
     */
    @Test
    public void test_wraps_Class() {
        Document plain = new DocumentImpl(null);
        Document nullFilter = Document.factory().nullFilter().build();
        ReadableDocument immutable = Document.factory().immutable().build();
        ReadableDocument both = Document.factory().nullFilter().immutable().build();
        ReadableDocument all = Document.factory().withSynchronization().nullFilter().immutable().build();

        Assert.assertFalse("plain isA(nullSafe)", plain.wraps(NullFilterDocument.class));
        Assert.assertFalse("plain isA(immutable)", plain.wraps(UnmodifiableDocument.class));
        Assert.assertFalse("plain isA(sync'ed)", plain.wraps(SynchronizedDocument.class));

        Assert.assertTrue("nullFilter isA(nullSafe)", nullFilter.wraps(NullFilterDocument.class));
        Assert.assertFalse("nullFilter isA(immutable)", nullFilter.wraps(UnmodifiableDocument.class));
        Assert.assertFalse("nullFilter isA(sync'ed)", nullFilter.wraps(SynchronizedDocument.class));

        Assert.assertFalse("immutable isA(nullSafe)", immutable.wraps(NullFilterDocument.class));
        Assert.assertTrue("immutable isA(immutable)", immutable.wraps(UnmodifiableDocument.class));
        Assert.assertFalse("immutable isA(sync'ed)", immutable.wraps(SynchronizedDocument.class));

        Assert.assertTrue("both isA(nullSafe)", both.wraps(NullFilterDocument.class));
        Assert.assertTrue("both isA(immutable)", both.wraps(UnmodifiableDocument.class));
        Assert.assertFalse("both isA(sync'ed)", both.wraps(SynchronizedDocument.class));

        Assert.assertTrue("all isA(nullSafe)", all.wraps(NullFilterDocument.class));
        Assert.assertTrue("all isA(immutable)", all.wraps(UnmodifiableDocument.class));
        Assert.assertTrue("all isA(sync'ed)", all.wraps(SynchronizedDocument.class));
    }


    /**
     * Unit test {@link DocumentImpl#wraps(Class)}
     */
    @Test
    public void test_wraps_All() {
        Document plain = new DocumentImpl(null);
        Document nullFilter = Document.factory().nullFilter().build();
        ReadableDocument immutable = Document.factory().immutable().build();
        ReadableDocument both = Document.factory().nullFilter().immutable().build();
        ReadableDocument all = Document.factory().withSynchronization().nullFilter().immutable().build();

        Assert.assertEquals("plain has wrong wrappers", Collections.emptyList(), plain.wraps());
        Assert.assertEquals("nullFilter has wrong wrappers", List.of(NullFilterDocument.class), nullFilter.wraps());
        Assert.assertEquals("immutable has wrong wrappers", List.of(UnmodifiableDocument.class), immutable.wraps());
        Assert.assertEquals("both has wrong wrappers",
            List.of(NullFilterDocument.class, UnmodifiableDocument.class),
            both.wraps());
        Assert.assertEquals("all has wrong wrappers",
            List.of(SynchronizedDocument.class, NullFilterDocument.class, UnmodifiableDocument.class),
            all.wraps());
    }


    /**
     * Unit test {@link DocumentImpl#canMutate()}
     */
    @Test
    public void test_canMutate() {
        Document plain = new DocumentImpl(null);
        Document nullFilter = Document.factory().nullFilter().build();
        ReadableDocument immutable = Document.factory().immutable().build();
        ReadableDocument both = Document.factory().nullFilter().immutable().build();
        ReadableDocument all = Document.factory().withSynchronization().nullFilter().immutable().build();

        Assert.assertTrue("plain isMutable()", plain.canMutate());
        Assert.assertTrue("nullSafe isMutable()", nullFilter.canMutate());
        Assert.assertFalse("immutable isMutable()", immutable.canMutate());
        Assert.assertFalse("both isMutable()", both.canMutate());
        Assert.assertFalse("all isMutable()", all.canMutate());
    }

    /**
     * Unit test {@link DocumentImpl}
     */
    @Test
    public void test_ImmutableChild() {
        ReadableDocument empty = Document.empty();
        Document parent = new DocumentImpl(null);

        parent.addDocument(() -> "child", empty);

        Assert.assertThrows(UnsupportedOperationException.class,
                () -> parent.addString(() -> "child.value", "Hello"));
    }


    /**
     * Unit test {@link DocumentImpl}
     */
    @Test
    public void test_ImmutableChild_InArray() {
        ReadableDocument empty = Document.empty();
        Document parent = new DocumentImpl(null);

        parent.addDocuments(() -> "children", empty);

        Assert.assertThrows(UnsupportedOperationException.class,
                () -> parent.addString(() -> "children[0].value", "Hello"));
    }


    /**
     * Unit test {@link DocumentImpl}
     */
    @Test
    public void test_ReadDocument_MultipleTypes() {
        DocumentKey key = () -> "key";
        ExtendedDocument child = Document.factory()
                .as(ExtendedDocument.class, ExtendedDocument::new)
                .build();
        Document parent = new DocumentImpl(null);

        parent.addDocument(key, child);

        // Read using different types
        ExtendedDocument extended = parent.get(key, ExtendedDocument.class);
        ReadableDocument readable = parent.get(key, ReadableDocument.class);
        WritableDocument<?> writable = parent.get(key, WritableDocument.class);
        CommonDocument basic = parent.get(key, CommonDocument.class);

        Assert.assertSame("Unexpected 'extended' document", child, extended);
        Assert.assertSame("Unexpected 'readable' document", child, readable);
        Assert.assertSame("Unexpected 'writable' document", child, writable);
        Assert.assertSame("Unexpected 'basic' document", child, basic);

        Assert.assertEquals("Failed to call extended method", "world", extended.hello());

        // child is not of type 'Document'
        Assert.assertThrows(DocumentException.class, () -> parent.get(key, Document.class));
    }

    /**
     * Unit test {@link DocumentImpl}
     */
    @Test
    public void test_ReadDocument_BadType() {
        DocumentKey key = () -> "key";
        Document child = new DocumentImpl(null);
        Document parent = new DocumentImpl(null);

        parent.addDocument(key, child);

        Assert.assertThrows(DocumentException.class, () -> parent.get(key, ExtendedDocument.class));
    }


    /**
     * Unit test {@link DocumentImpl}.
     * Test that documents with different nested decorators work as expected
     */
    @Test
    public void test_WriteDocument_MixedTypes() {
        Document parent = Document.factory()
                .nullFilter()
                .build();
        Document child = new DocumentImpl(null);

        child.addString(() -> "b", null);               // Child is allowed to contain nulls
        parent.addDocument(() -> "a", child);           // parent contains a null via child
        parent.addString(() ->"a.c", null);             // should be filtered out - can not add a null via parent
        parent.addString(() -> "d", null);              // parent can not contain nulls directly
        parent.addString(() -> "e", "e");               // parent can contain non-null values
        child.addString(() -> "f", null);               // Child can have nulls loaded even if it's a child of parent
        child.addString(() -> "g", "g");                // Child can contain non-null values

        Assert.assertEquals("Unexpected keys",
                Set.of("a.b", "e", "a.f", "a.g"),
                parent.accept(new PathSet()));

        Assert.assertEquals("Unexpected value of 'a.b'", null, parent.getOptional(() -> "a.b", String.class));
        Assert.assertEquals("Unexpected value of 'e'", "e", parent.getOptional(() -> "e", String.class));
        Assert.assertEquals("Unexpected value of 'a.f'", null, parent.getOptional(() -> "a.f", String.class));
        Assert.assertEquals("Unexpected value of 'a.g'", "g", parent.getOptional(() -> "a.g", String.class));
    }

    /**
     * Unit test {@link DocumentImpl#clone()}
     */
    @Test
    public void test_Clone() {
        Document source = Document.factory()
            .addString(Key.STRING_VALUE, "String")
            .addNumber(Key.NUMBER_VALUE, 123)
            .addBoolean(Key.BOOL_VALUE, true)
            .addEnum(Key.ENUM_VALUE, Type1.ZERO)
            .addStrings(Key.STRING_LIST, "One", null, "Three")
            .addNumbers(Key.NUMBER_LIST, 1, null, 3.4)
            .addBooleans(Key.BOOL_LIST, true, null, false)
            .addEnums(Key.ENUM_LIST, Type1.ONE, null, Type1.TWO)
            .addDocuments(Key.DOC_LIST,
                    Document.newInstance().addString(Key.STRING_VALUE, "One"),
                    Document.newInstance().addString(Key.UNKNOWN, null)
                            .addStrings(Key.STRING_LIST, "x", "y", "z"),
                    Document.newInstance().addString(Key.STRING_VALUE, "Three")
                            .addStrings(Key.STRING_LIST, (List<String>) null))
            .addDocument(Key.EMPTY_VALUE, Document.empty())
            .addStrings(Key.EMPTY_LIST)
            .immutable()
            .nullFilter()                   // keep null values - Null filter applied after null were added
            .build();

        ReadableDocument actual = source.clone();

        Assert.assertNotSame("Same instance returned", source, actual);
        Assert.assertEquals("Unexpected Clone", source, actual);
        Assert.assertTrue("Failed to clone EMPTY_VALUE", actual.contains(Key.EMPTY_VALUE));
        Assert.assertTrue("Failed to clone EMPTY_LIST", actual.contains(Key.EMPTY_LIST));
        Assert.assertEquals("Unexpected Wrappers", List.of(UnmodifiableDocument.class, NullFilterDocument.class), actual.wraps());
    }



                //*** helpers ***//

    private void getHelper(@Nonnull String key, @Nonnull Object expected) {
        Object actual = sample.get(() -> key, expected.getClass());

        Assert.assertEquals("Failed to read " + key, expected, actual);
    }


    private void getHelper(@Nonnull String key, @Nonnull Object defaultValue, @Nonnull Object expected) {
        Object actual = sample.getOrDefault(() -> key, defaultValue);

        Assert.assertEquals("Failed to read " + key, expected, actual);
    }


    private void getObjectHelper(@Nonnull String key, @Nonnull Object expected) {
        Object actual = sample.get(() -> key, Object.class);

        Assert.assertEquals("Failed to read " + key, expected, actual);
    }


    private void getOptionalHelper(@Nonnull String key, @Nullable Object expected, @Nonnull Class<?> type) {
        Object actual = sample.getOptional(() -> key, type);

        Assert.assertEquals("Failed to read " + key, expected, actual);
    }

    private <E> void getAllHelper(@Nonnull String key, @Nullable List<E> expected, @Nonnull Class<E> type) {
        List<? extends E> actual = sample.getAll(() -> key, type);

        Assert.assertEquals("Failed to read " + key, expected, actual);
    }
}