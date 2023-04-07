package com.github.tymefly.common.document;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;

import com.github.tymefly.common.base.io.FailedIoException;
import com.github.tymefly.common.document.decorator.NullFilterDocument;
import com.github.tymefly.common.document.decorator.SynchronizedDocument;
import com.github.tymefly.common.document.decorator.UnmodifiableDocument;
import com.github.tymefly.common.document.key.DocumentKey;
import com.github.tymefly.common.document.parse.DocumentParser;
import com.github.tymefly.common.document.parse.JsonParser;
import com.github.tymefly.common.document.parse.PropertiesParser;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link DocumentFactoryImpl}
 */
public class DocumentFactoryImplTest {
    private static class ExtendedDocument extends DocumentDecorator<ExtendedDocument> {
        protected ExtendedDocument(@Nonnull AbstractDocument<?> wrapped) {
            super(wrapped);
        }

        @Nonnull
        String hello() {
            return "world";
        }
    }

    private enum Type {
        A, B, C, D
    }


    private static final DocumentKey KEY = () -> "Key";

    /**
     * Unit test {@link DocumentFactoryImpl#create()}
     */
    @Test
    public void test_Empty() {
        ReadableDocument doc1 = DocumentFactoryImpl.empty();
        ReadableDocument doc2 = DocumentFactoryImpl.empty();

        Assert.assertSame("New objects created", doc1, doc2);
        Assert.assertTrue("Document is not empty", doc1.isEmpty());
        Assert.assertThrows("Document was mutated",
                UnsupportedOperationException.class,
                () -> ((Document) doc1).addString(KEY, ""));
    }


    /**
     * Unit test {@link DocumentFactoryImpl#create()}
     */
    @Test
    public void test_NoDecorator() {
        Document doc = DocumentFactoryImpl.create()
                .build();

        Assert.assertTrue("Doc should be empty", doc.isEmpty());

        doc.addString(KEY, "Hello");

        Assert.assertFalse("Doc should not be empty", doc.isEmpty());
        Assert.assertEquals("Invalid Value", "Hello", doc.getOptional(KEY, String.class));
    }


    /**
     * Unit test {@link DocumentFactoryImpl#immutable()}
     */
    @Test
    public void test_immutable() {
        DocumentKey validKey = () -> "Hello";
        DocumentKey invalidKey = () -> "Foo";
        ReadableDocument doc = DocumentFactoryImpl.create()
                .parse("{ \"Hello\" : \"World\" }", new JsonParser())
                .immutable()
                .build();

        Assert.assertEquals("Expected read", "World", doc.getOptional(validKey, String.class));
        Assert.assertEquals("Unexpected read", null, doc.getOptional(invalidKey, String.class));
        Assert.assertThrows("Mutated", UnsupportedOperationException.class, () -> ((Document) doc).addBoolean(invalidKey, true));
    }


    /**
     * Unit test {@link DocumentFactoryImpl#nullFilter()}
     */
    @Test
    public void test_nullFilter() {
        Document doc = DocumentFactoryImpl.create()
                .nullFilter()
                .build();

        Assert.assertFalse("before: contains null", doc.contains(KEY));
        Assert.assertEquals("before: read", null, doc.getOptional(KEY, String.class));

        doc.addString(KEY, null);
        Assert.assertFalse("null: contains", doc.contains(KEY));
        Assert.assertEquals("null: read", null, doc.getOptional(KEY, String.class));

        doc.addString(KEY, "Foo");
        Assert.assertTrue("update: contains", doc.contains(KEY));
        Assert.assertEquals("update: read", "Foo", doc.getOptional(KEY, String.class));

        doc.addString(KEY, null);
        Assert.assertTrue("overwrite null: contains", doc.contains(KEY));
        Assert.assertEquals("overwrite null: read", "Foo", doc.getOptional(KEY, String.class));

        doc.addString(KEY, "Bar");
        Assert.assertTrue("overwrite String: contains", doc.contains(KEY));
        Assert.assertEquals("overwrite String: read", "Bar", doc.getOptional(KEY, String.class));
    }


    /**
     * Unit test {@link DocumentFactoryImpl#withSynchronization()}
     */
    @Test
    public void test_withSynchronization() {
        Document doc = DocumentFactoryImpl.create()
                .withSynchronization()
                .build();

        Assert.assertEquals("Unexpected type returned", "SynchronizedDocument", doc.getClass().getSimpleName());
    }


    /**
     * Unit test {@link DocumentFactoryImpl#copy(ReadableDocument)}
     */
    @Test
    public void test_copy() {
        Document source = DocumentFactoryImpl.create()
                .parse("{ \"Hello\" : \"World\" }", new JsonParser())
                .build();
        Document target = DocumentFactoryImpl.create()
                .copy(source)
                .build();

        Assert.assertNotSame("Expected different Documents", source, target);
        Assert.assertEquals("Different content", source, target);

        source.addString(KEY, "Data");

        Assert.assertNull("target updated from source", target.getOptional(KEY, String.class));
    }


    /**
     * Unit test {@link DocumentFactoryImpl#load(InputStream, DocumentParser)}
     */
    @Test
    public void test_load_Stream() {
        Document expected = DocumentFactoryImpl.create()
                .build()
                .addString(() -> "string", "String")
                .addString(() -> "number", "123")
                .addString(() -> "boolean", "true");
        Document actual = DocumentFactoryImpl.create()
                .load(getClass().getResourceAsStream("/doc/small.properties"), new PropertiesParser())
                .build();

        Assert.assertEquals("Failed to load document", expected, actual);
    }

    /**
     * Unit test {@link DocumentFactoryImpl#load(InputStream, DocumentParser)}
     */
    @Test
    public void test_load_Stream_Overflow() throws Exception {
        DocumentFactoryImpl<Document> factory = DocumentFactoryImpl.create();
        InputStream stream = mock(InputStream.class);

        when(stream.read()).thenReturn((int) 'a');

        Assert.assertThrows(FailedIoException.class,
            () -> factory.load(stream, new PropertiesParser()));
    }

    /**
     * Unit test {@link DocumentFactoryImpl#load(File, DocumentParser)}
     */
    @Test
    public void test_load_File() throws Exception {
        Document expected = DocumentFactoryImpl.create()
                .build()
                .addString(() -> "string", "String")
                .addString(() -> "number", "123")
                .addString(() -> "boolean", "true");
        URL resource = getClass().getResource("/doc/small.properties");
        File file = new File(resource.toURI());
        Document actual = DocumentFactoryImpl.create()
                .load(file, new PropertiesParser())
                .build();

        Assert.assertEquals("Failed to load document", expected, actual);
    }


    /**
     * Unit test {@link DocumentFactoryImpl#load(String, DocumentParser)}
     */
    @Test
    public void test_load_FileName() throws Exception {
        Document expected = DocumentFactoryImpl.create()
                .build()
                .addString(() -> "string", "String")
                .addString(() -> "number", "123")
                .addString(() -> "boolean", "true");
        URL resource = getClass().getResource("/doc/small.properties");
        File file = new File(resource.toURI());
        Document actual = DocumentFactoryImpl.create()
                .load(file.getAbsolutePath(), new PropertiesParser())
                .build();

        Assert.assertEquals("Failed to load document", expected, actual);
    }


    /**
     * Unit test {@link DocumentFactoryImpl#load(String, DocumentParser)}
     */
    @Test
    public void test_load_FileName_invalid() {
        Exception actual = Assert.assertThrows(FailedIoException.class,
            () -> DocumentFactoryImpl.create()
                .load("Does/Not/Exist.????", new PropertiesParser())
                .build());

        Assert.assertEquals("Unexpected message", "Failed to load 'Exist.????'", actual.getMessage());
    }


    /**
     * Unit test {@link DocumentFactoryImpl#parse(String, DocumentParser)}
     */
    @Test
    public void test_parse() {
        Document expected = DocumentFactoryImpl.create()
                .build()
                .addString(() -> "Hello", "World");
        Document actual = DocumentFactoryImpl.create()
                .parse("{ \"Hello\" : \"World\" }", new JsonParser())
                .build();

        Assert.assertEquals("Failed to parse document", expected, actual);
    }


    /**
     * Unit test a chain of {@link DocumentFactoryImpl} directives
     */
    @Test
    public void test_FilteredRead() {
        Document allowNull = DocumentFactoryImpl.create()
                .load(getClass().getResourceAsStream("/doc/small.json"), new JsonParser())
                .build();
        ReadableDocument skipNull = DocumentFactoryImpl.create()
                .nullFilter()
                .load(getClass().getResourceAsStream("/doc/small.json"), new JsonParser())
                .immutable()
                .build();

        Assert.assertNotEquals("failed filter on load", allowNull, skipNull);

        allowNull.remove(() -> "null");

        Assert.assertEquals("Documents should be equal", allowNull, skipNull);
    }

    /**
     * Unit test {@link DocumentFactoryImpl}
     */
    @Test
    public void test_InitialisedDocument_Strings() {
        ReadableDocument actual = DocumentFactoryImpl.create()
            .nullFilter()
            .addString(() -> "null", null)                          // should be filtered out
            .addString(() -> "remove", "me")                        // Remove this later
            .addString(() -> "string", "str")
            .addStrings(() -> "strings1", "a", "b")
            .addStrings(() -> "strings2", List.of("c", "d"))
            .appendString(() -> "strings2", "e")
            .remove(() -> "remove")
            .immutable()
            .build();
        Document expected = new DocumentImpl(null)
            .addString(() -> "string", "str")
            .addStrings(() -> "strings1", "a", "b")
            .addStrings(() -> "strings2", "c", "d", "e");

        Assert.assertFalse("Expected an immutable document", actual.canMutate());
        Assert.assertEquals("Unexpected Document", expected, actual);
        Assert.assertThrows(UnsupportedOperationException.class, () -> ((Document) actual).addString(() -> "more", "data"));
    }

    /**
     * Unit test {@link DocumentFactoryImpl}
     */
    @Test
    public void test_InitialisedDocument_Number() {
        ReadableDocument actual = DocumentFactoryImpl.create()
            .addNumber(() -> "number", 1)
            .addNumbers(() -> "numbers1", 2, 3)
            .addNumbers(() -> "numbers2", List.of(4, 5))
            .appendNumber(() -> "numbers2", 6)
            .immutable()
            .build();
        Document expected = new DocumentImpl(null)
            .addNumber(() -> "number", 1)
            .addNumbers(() -> "numbers1", 2, 3)
            .addNumbers(() -> "numbers2", 4, 5, 6);

        Assert.assertFalse("Expected an immutable document", actual.canMutate());
        Assert.assertEquals("Unexpected Document", expected, actual);
        Assert.assertThrows(UnsupportedOperationException.class, () -> ((Document) actual).addString(() -> "more", "data"));
    }

    /**
     * Unit test {@link DocumentFactoryImpl}
     */
    @Test
    public void test_InitialisedDocument_Boolean() {
        Document actual = DocumentFactoryImpl.create()
            .addBoolean(() -> "bool", true)
            .addBooleans(() -> "bool1", null, false)
            .addBooleans(() -> "bool2", Arrays.asList(false, null))
            .appendBoolean(() -> "bool2", true)
            .build();
        Document expected = new DocumentImpl(null)
            .addBoolean(() -> "bool", true)
            .addBooleans(() -> "bool1", null, false)
            .addBooleans(() -> "bool2", false, null, true);

        Assert.assertTrue("Expected an mutable document", actual.canMutate());
        Assert.assertEquals("Unexpected Document", expected, actual);

        actual.addString(() -> "more", "data");        // This is allowed - no immutable decorator
    }

    /**
     * Unit test {@link DocumentFactoryImpl}
     */
    @Test
    public void test_InitialisedDocument_Enum() {
        ReadableDocument actual = DocumentFactoryImpl.create()
            .addEnum(() -> "enum", Type.A)
            .addEnums(() -> "enum1", Type.B, null)
            .addEnums(() -> "enum2", Arrays.asList(Type.C, Type.D))
            .appendEnum(() -> "enum2", null)
            .nullFilter()                                           // Order is significant, previous nulls are allowed
            .addString(() -> "null", null)                          // but this should be filtered out
            .immutable()
            .build();
        Document expected = new DocumentImpl(null)
            .addEnum(() -> "enum", Type.A)
            .addEnums(() -> "enum1", Type.B, null)
            .addEnums(() -> "enum2", Type.C, Type.D, null);

        Assert.assertFalse("Expected an immutable document", actual.canMutate());
        Assert.assertEquals("Unexpected Document", expected, actual);
        Assert.assertThrows(UnsupportedOperationException.class, () -> ((Document) actual).addString(() -> "more", "data"));
    }

    /**
     * Unit test {@link DocumentFactoryImpl}
     */
    @Test
    public void test_InitialisedDocument_Doc() {
        ReadableDocument actual = DocumentFactoryImpl.create()
            .addDocument(() -> "doc", Document.empty())
            .addDocuments(() -> "doc1", Document.empty(), null)
            .addDocuments(() -> "doc2", Arrays.asList(null, Document.empty()))
            .appendDocument(() -> "doc2", null)
            .build();
        Document expected = new DocumentImpl(null)
            .addDocument(() -> "doc",  Document.empty())
            .addDocuments(() -> "doc1",  Document.empty(), null)
            .addDocuments(() -> "doc2", null,  Document.empty(), null);

        Assert.assertTrue("Expected an mutable document", actual.canMutate());
        Assert.assertEquals("Unexpected Document", expected, actual);

        ((Document) actual).addString(() -> "more", "data");        // This is allowed - no immutable decorator
    }



    /**
     * Unit test {@link DocumentFactoryImpl} to ensure that the constructor chain is set correctly;
     * if a document generates a child document then it needs to have the same decorators to ensure it behaves
     * the same way as its parent
     */
    @Test
    public void test_childDocuments() {
        Document parent = DocumentFactoryImpl.create()
                .nullFilter()                                             // Parent is null safe
                .build();

        parent.addString(() -> "a.b", "Hello");

        Document child = parent.get(() -> "a", Document.class);         // Child should also be null safe
        child.addString(KEY, null);

        Assert.assertFalse("null value entered", child.hasValue(KEY));

        child.addString(KEY, "World");
        Assert.assertTrue("non-null value entered", child.hasValue(KEY));
    }


    /**
     * Unit test {@link DocumentFactoryImpl} to see that a generated objects can have additional functions
     */
    @Test
    public void test_FluentDecorator() {
        String result = Document.factory()
                .as(ExtendedDocument.class, ExtendedDocument::new)
                .build()
                .addString(() -> "key", "value")
                .hello();

        Assert.assertEquals("Failed to call extended method", "world", result);
    }

    /**
     * Unit test {@link DocumentFactoryImpl} to see that the generated document has the right constructor chain.
     * We need to make sure that if it needs to generate a child document then the child will have the same
     * wrappers as its parent
     */
    @Test
    public void test_ConstructorChain() {
        ExtendedDocument doc = Document.factory()
            .nullFilter()
            .withSynchronization()
            .as(ExtendedDocument.class, ExtendedDocument::new)
            .build();
        Function<AbstractDocument<?>, ? extends AbstractDocument<?>> constructor = doc.getImpl().getConstructor();
        AbstractDocument<?> child = constructor.apply(null);

        Assert.assertTrue("Child should have null Filters", child.wraps(NullFilterDocument.class));
        Assert.assertTrue("Child should have Synchronisation", child.wraps(SynchronizedDocument.class));
        Assert.assertTrue("Child should have extensions", child.wraps(ExtendedDocument.class));
        Assert.assertFalse("Child should not be Unmodifiable", child.wraps(UnmodifiableDocument.class));
    }

    /**
     * Unit test {@link DocumentFactoryImpl} can wrap existing documents. This involves keeping existing data,
     * syncing data and maintaining the correct constructor chain.
     */
    @Test
    public void test_Wrapped_Data() {
        Document inner = Document.newInstance()
            .addString(() -> "Key", "Value");
        Document parent = Document.factory(inner)
            .build();

        Assert.assertEquals("Parent should have data", "Value", parent.getOptional(() -> "Key", String.class));
        Assert.assertEquals("Parent should not have data", null, parent.getOptional(() -> "Extra", String.class));

        inner.addString(() -> "Extra", "Data");

        Assert.assertEquals("Parent should not have data", "Data", parent.getOptional(() -> "Extra", String.class));
    }

    /**
     * Unit test {@link DocumentFactoryImpl} can wrap existing documents. This involves keeping existing data,
     * syncing data and maintaining the correct constructor chain.
     */
    @Test
    public void test_Wrapped_ConstructorChain() {
        Document inner = Document.factory()
            .nullFilter()
            .build();
        ExtendedDocument parent = Document.factory(inner)
            .withSynchronization()
            .as(ExtendedDocument.class, ExtendedDocument::new)
            .build();

        Assert.assertTrue("Parent should have null Filters", parent.wraps(NullFilterDocument.class));
        Assert.assertTrue("Parent should have Synchronisation", parent.wraps(SynchronizedDocument.class));
        Assert.assertTrue("Parent should have extensions", parent.wraps(ExtendedDocument.class));
        Assert.assertFalse("Parent should not be Unmodifiable", parent.wraps(UnmodifiableDocument.class));

        Function<AbstractDocument<?>, ? extends AbstractDocument<?>> constructor = parent.getImpl().getConstructor();
        AbstractDocument<?> child = constructor.apply(null);

        Assert.assertTrue("Child should have null Filters", child.wraps(NullFilterDocument.class));
        Assert.assertTrue("Child should have Synchronisation", child.wraps(SynchronizedDocument.class));
        Assert.assertTrue("Child should have extensions", child.wraps(ExtendedDocument.class));
        Assert.assertFalse("Child should not be Unmodifiable", child.wraps(UnmodifiableDocument.class));
    }
}