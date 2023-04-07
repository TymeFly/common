package com.github.tymefly.common.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;

import com.github.tymefly.common.document.key.DocumentKey;

/**
 * Base class for all Documents and their Decorators.
 * @param <D>   The type of this AbstractDocument.
 */
public abstract sealed class AbstractDocument<D extends CommonDocument>
        implements ReadableDocument, WritableDocument<D>
        permits DocumentDecorator, DocumentImpl {

    /**
     * Hide Constructor from classes outside this package.
     * As the class is sealed we know that the only classes that can call it are in this package
     */
    AbstractDocument() {
    }

    /**
     * Returns the AbstractDocument that this class wraps.
     * @return the AbstractDocument that this class wraps.
     */
    @Nonnull
    abstract AbstractDocument<?> getWrapped();


    /**
     * Used to access the Document implementation that underpins this AbstractDocument.
     * <br>
     * This should be used with care as it bypasses all the Decorators
     * @return the Document implementation that underpins this AbstractDocument
     */
    @Nonnull
    abstract DocumentImpl getImpl();


    /**
     * Used to access the Structure that underpins the Document returned by {@link #getImpl()}
     * @return the Structure that underpins the Document returned by {@link #getImpl()}
     */
    @Nonnull
    abstract Structure getStructure();


    /**
     * Helper function that extending classes can use to transform data passed to
     * {@link WritableDocument#addStrings(DocumentKey, Collection)},
     * {@link WritableDocument#addNumbers(DocumentKey, Collection)},
     * {@link WritableDocument#addBooleans(DocumentKey, Collection)},
     * {@link WritableDocument#addEnums(DocumentKey, Collection)} or
     * {@link WritableDocument#addDocuments(DocumentKey, Collection)}
     * @param transform     transformation function for a single element in the collection.
     *                          transformation functions need to be able to handle {@literal null} values
     * @param data          The elements to be transformed
     * @param <T>           The type of the data
     * @return              A new ordered Collection that preserves the order of the original {@code data}
     */
    @Nonnull
    protected <T> Collection<T> transformAll(@Nonnull Function<T, T> transform, @Nonnull Collection<? extends T> data) {
        List<T> results = new ArrayList<>(data.size());

        for (var element : data) {
            results.add(transform.apply(element));
        }

        return results;
    }


    /**
     * Helper function that extending classes can use to transform data passed to
     * {@link WritableDocument#addStrings(DocumentKey, String...)}
     * {@link WritableDocument#addNumbers(DocumentKey, Number...)}
     * {@link WritableDocument#addBooleans(DocumentKey, Boolean...)}
     * {@link WritableDocument#addEnums(DocumentKey, Enum[])} or
     * {@link WritableDocument#addDocuments(DocumentKey, CommonDocument...)}
     * @param transform     transformation function for a single element in the array
     *                          transformation functions need to be able to handle {@literal null} values
     * @param data          The elements to be transformed
     * @param <T>           The type of the data
     * @return              The original array but with the data transformed
     */
    @Nonnull
    protected <T> T[] transformAll(@Nonnull Function<T, T> transform, @Nonnull T[] data) {
        for (int i = 0; i < data.length; i++) {
            data[i] = transform.apply(data[i]);
        }

        return data;
    }

    // Suspend Checkstyle rule SuperCloneCheck for 10 lines: Instead of calling super clone, we will get
    // {@link DocumentImpl#clone} to create everything for us. DocumentImp has to be able to create the right types
    // when we produce child Documents anyway.
    @Nonnull
    @Override
    public ReadableDocument clone() {
        return getImpl().clone();
    }
}
