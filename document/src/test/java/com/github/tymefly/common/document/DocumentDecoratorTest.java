package com.github.tymefly.common.document;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;

import com.github.tymefly.common.document.decorator.UnmodifiableDocument;
import com.github.tymefly.common.document.key.DocumentKey;
import com.github.tymefly.common.document.visitor.DocumentVisitor;
import com.github.tymefly.common.document.visitor.util.KeySet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Unit test for {@link DocumentDecorator}
 */
public class DocumentDecoratorTest {
    private static class DecoratorType extends DocumentDecorator<DecoratorType> {
        protected DecoratorType(@Nonnull AbstractDocument<?> wrapped) {
            super(wrapped);
        }
    }


    private final DocumentKey KEY = () -> "key";

    private enum Type {
        A,
        B
    }

    private DocumentDecorator<?> decorator;
    private AbstractDocument<?> backing;


    @Before
    public void setUp() {
        backing = mock(DocumentDecorator.class);
        decorator = new DecoratorType(backing);
    }

    /**
     * Unit test {@link DocumentDecorator#getWrapped}
     */
    @Test
    public void test_getWrapped() {
        Assert.assertSame("Unexpected backing", backing, decorator.getWrapped());
    }

    /**
     * Unit test {@link DocumentDecorator#getImpl}
     */
    @Test
    public void test_getImpl() {
        decorator.getImpl();

        verify(backing).getImpl();
    }

    /**
     * Unit test {@link DocumentDecorator#getStructure}
     */
    @Test
    public void test_getStructure() {
        decorator.getStructure();

        verify(backing).getStructure();
    }

    /**
     * Unit test {@link DocumentDecorator#unmodifiable}
     */
    @Test
    public void test_unmodifiable_ofRaw() {
        ReadableDocument view = decorator.unmodifiable();

        Assert.assertTrue("Expected Unmodifiable", view instanceof UnmodifiableDocument);
        Assert.assertTrue("Keep additional decorators", view.wraps(DecoratorType.class));
    }

    /**
     * Unit test {@link DocumentDecorator#unmodifiable}
     */
    @Test
    public void test_unmodifiable_OfUnmodifiable() {
        AbstractDocument<?> base = mock(DocumentDecorator.class);
        AbstractDocument<?> unmodifiable = new UnmodifiableDocument(base);
        DecoratorType outer = new DecoratorType(unmodifiable);

        ReadableDocument view = outer.unmodifiable();

        Assert.assertSame("Don't wrap unmodifiable", view, outer);
    }


    /**
     * Unit test {@link DocumentDecorator#addString(DocumentKey, String)}
     */
    @Test
    public void test_addString() {
        decorator.addString(KEY, "data");

        verify(backing).addString(KEY, "data");
    }

    /**
     * Unit test {@link DocumentDecorator#addStrings(DocumentKey, String...)}
     */
    @Test
    public void test_addStrings_Array() {
        decorator.addStrings(KEY, "Hello", null, "World");

        verify(backing).addStrings(KEY, "Hello", null, "World");
    }

    /**
     * Unit test {@link DocumentDecorator#addStrings(DocumentKey, Collection)}
     */
    @Test
    public void test_addStrings_Collection() {
        List<String> data = Arrays.asList("Hello", null, "World");

        decorator.addStrings(KEY, data);

        verify(backing).addStrings(KEY, data);
    }

    /**
     * Unit test {@link DocumentDecorator#appendString(DocumentKey, String)}
     */
    @Test
    public void test_appendString() {
        decorator.appendString(KEY, "Hello");

        verify(backing).appendString(KEY, "Hello");
    }


    /**
     * Unit test {@link DocumentDecorator#addNumber(DocumentKey, Number)}
     */
    @Test
    public void test_addNumber() {
        decorator.addNumber(KEY, 123);

        verify(backing).addNumber(KEY, 123);
    }

    /**
     * Unit test {@link DocumentDecorator#addNumbers(DocumentKey, Number...)}
     */
    @Test
    public void test_addNumbers_Array() {
        decorator.addNumbers(KEY, 1, -2, null);

        verify(backing).addNumbers(KEY, 1, -2, null);
    }

    /**
     * Unit test {@link DocumentDecorator#addNumbers(DocumentKey, Collection)}
     */
    @Test
    public void test_addNumbers_Collection() {
        List<Number> data = Arrays.asList(1, -2, null);

        decorator.addNumbers(KEY, data);

        verify(backing).addNumbers(KEY, data);
    }

    /**
     * Unit test {@link DocumentDecorator#appendNumber(DocumentKey, Number)}
     */
    @Test
    public void test_appendNumber() {
        decorator.appendNumber(KEY, 123);

        verify(backing).appendNumber(KEY, 123);
    }


    /**
     * Unit test {@link DocumentDecorator#addBoolean(DocumentKey, Boolean)}
     */
    @Test
    public void test_addBoolean() {
        decorator.addBoolean(KEY, true);

        verify(backing).addBoolean(KEY, true);
    }

    /**
     * Unit test {@link DocumentDecorator#addBooleans(DocumentKey, Boolean...)}
     */
    @Test
    public void test_addBooleans_Array() {
        decorator.addBooleans(KEY, false, null, true);

        verify(backing).addBooleans(KEY, false, null, true);
    }

    /**
     * Unit test {@link DocumentDecorator#addBooleans(DocumentKey, Collection)}
     */
    @Test
    public void test_addBooleans_Collection() {
        List<Boolean> data = Arrays.asList(false, null, true);

        decorator.addBooleans(KEY, data);

        verify(backing).addBooleans(KEY, data);
    }

    /**
     * Unit test {@link DocumentDecorator#appendBoolean(DocumentKey, Boolean)}
     */
    @Test
    public void test_appendBoolean() {
        decorator.appendBoolean(KEY, true);

        verify(backing).appendBoolean(KEY, true);
    }


    /**
     * Unit test {@link DocumentDecorator#addEnum(DocumentKey, Enum)}
     */
    @Test
    public void test_AddEnum() {
        decorator.addEnum(KEY, Type.A);

        verify(backing).addEnum(KEY, Type.A);
    }

    /**
     * Unit test {@link DocumentDecorator#addEnums(DocumentKey, Enum[])}
     */
    @Test
    public void test_addEnums_Array() {
        decorator.addEnums(KEY, Type.A, null, Type.B);

        verify(backing).addEnums(KEY, Type.A, null, Type.B);
    }

    /**
     * Unit test {@link DocumentDecorator#addEnums(DocumentKey, Collection)}
     */
    @Test
    public void test_addEnums_Collection() {
        List<Type> data = Arrays.asList(Type.A, null, Type.B);

        decorator.addEnums(KEY, data);

        verify(backing).addEnums(KEY, data);
    }

    /**
     * Unit test {@link DocumentDecorator#appendEnum(DocumentKey, Enum)}
     */
    @Test
    public void test_appendEnum() {
        decorator.appendEnum(KEY, Type.A);

        verify(backing).appendEnum(KEY, Type.A);
    }


    /**
     * Unit test {@link DocumentDecorator#addDocument(DocumentKey, CommonDocument)}
     */
    @Test
    public void test_addDocument() {
        decorator.addDocument(KEY, Document.empty());

        verify(backing).addDocument(KEY, Document.empty());
    }

    /**
     * Unit test {@link DocumentDecorator#addDocuments(DocumentKey, CommonDocument...)}
     */
    @Test
    public void test_addDocuments_Array() {
        decorator.addDocuments(KEY, null, Document.empty(), null);

        verify(backing).addDocuments(KEY, null, Document.empty(), null);
    }

    /**
     * Unit test {@link DocumentDecorator#addDocuments(DocumentKey, Collection)}
     */
    @Test
    public void test_addDocuments_Collection() {
        List<ReadableDocument> data = Arrays.asList(null, Document.empty(), null);

        decorator.addDocuments(KEY, data);

        verify(backing).addDocuments(KEY, data);
    }

    /**
     * Unit test {@link DocumentDecorator#appendDocument(DocumentKey, CommonDocument)}
     */
    @Test
    public void test_appendDocument() {
        decorator.appendDocument(KEY, Document.empty());

        verify(backing).appendDocument(KEY, Document.empty());
    }


    /**
     * Unit test {@link DocumentDecorator#remove}
     */
    @Test
    public void test_remove() {
        decorator.remove(KEY);

        verify(backing).remove(KEY);
    }

    /**
     * Unit test {@link DocumentDecorator#get(DocumentKey, Class)}
     */
    @Test
    public void test_get() {
        decorator.get(KEY, Type.class);

        verify(backing).get(KEY, Type.class);
    }

    /**
     * Unit test {@link DocumentDecorator#getOrDefault(DocumentKey, Object)}
     */
    @Test
    public void test_get_withDefault() {
        decorator.getOrDefault(KEY, Type.B);

        verify(backing).getOrDefault(KEY, Type.B);
    }

    /**
     * Unit test {@link DocumentDecorator#get(DocumentKey, Function)}
     */
    @Test
    public void test_get_conversion() {
        decorator.get(KEY, Function.identity());

        verify(backing).get(KEY, Function.identity());
    }

    /**
     * Unit test {@link DocumentDecorator#getOptional(DocumentKey, Class)}
     */
    @Test
    public void test_getOptional() {
        decorator.getOptional(KEY, Type.class);

        verify(backing).getOptional(KEY, Type.class);
    }

    /**
     * Unit test {@link DocumentDecorator#getAll(DocumentKey, Class)}
     */
    @Test
    public void test_getAll() {
        decorator.getAll(KEY, Type.class);

        verify(backing).getAll(KEY, Type.class);
    }

    /**
     * Unit test {@link DocumentDecorator#isEmpty()}
     */
    @Test
    public void test_isEmpty() {
        decorator.isEmpty();

        verify(backing).isEmpty();
    }

    /**
     * Unit test {@link DocumentDecorator#contains(DocumentKey)}
     */
    @Test
    public void test_contains() {
        decorator.contains(KEY);

        verify(backing).contains(KEY);
    }

    /**
     * Unit test {@link DocumentDecorator#hasValue(DocumentKey)}
     */
    @Test
    public void test_hasValue() {
        decorator.hasValue(KEY);

        verify(backing).hasValue(KEY);
    }

    /**
     * Unit test {@link DocumentDecorator#isSequence(DocumentKey)}
     */
    @Test
    public void test_isSequence() {
        decorator.isSequence(KEY);

        verify(backing).isSequence(KEY);
    }

    /**
     * Unit test {@link DocumentDecorator#accept(DocumentVisitor)}
     */
    @Test
    public void test_accept() {
        KeySet visitor = new KeySet();

        decorator.accept(visitor);

        verify(backing).accept(visitor);
    }
}