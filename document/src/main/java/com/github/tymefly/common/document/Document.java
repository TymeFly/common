package com.github.tymefly.common.document;


import javax.annotation.Nonnull;

import com.github.tymefly.common.base.utils.Convert;
import com.github.tymefly.common.document.key.DocumentKey;


/**
 * A Documents are generic DTOs that store data based on a hierarchy of {@link DocumentKey}s.
 * The following data types are supported:
 * <ul>
 *     <li>{@literal null}</li>
 *     <li>{@link String}</li>
 *     <li>{@link Boolean}</li>
 *     <li>{@link Number} and subtypes {@link Byte}, {@link Short}, {@link Integer}, {@link Long},
 *         {@link Float}, {@link Double}, {@link java.math.BigInteger}, {@link java.math.BigDecimal}</li>
 *     <li>All Subtypes of {@link Enum}</li>
 *     <li>Child documents</li>
 *     <li>zero based indexed sequences of the other supported data types. This excludes the possibility
 *          of sequences of sequences</li>
 * </ul>
 * A document will attempt to convert data between the various data types using the rules described in
 * {@link Convert}. For example an Integer can be stored in a Document but this could be
 * read back as Integer, a String, or some other type Numeric type.
 * These conversion rules do not affect {@link #equals(Object)} or {@link #hashCode()}. Two documents will
 * be not be equal if they use different types to store the same data.
 * The {@link com.github.tymefly.common.document.visitor.util.Equivalent} visitor can be used instead of
 * {@link #equals(Object)} if the client doesn't care about the data type.
 * <br>
 * Documents are generally not synchronized, so care must be taken in multi-threaded environments. If
 * Synchronization is required then build with the synchronized decorator by calling
 * {@code Document.factory().withSynchronization().build();}
 */
public interface Document extends ReadableDocument, WritableDocument<Document> {
    /**
     * Returns an immutable empty Document
     * @return an immutable empty Document
     */
    @Nonnull
    static ReadableDocument empty() {
        return DocumentFactoryImpl.empty();
    }


    /**
     * Returns a new empty Document with no special customisations.
     * @return a new empty Document with no special customisations.
     */
    @Nonnull
    static Document newInstance() {
        return DocumentFactoryImpl.create().build();
    }


    /**
     * Returns a factory that allows the client to create customised Documents. Customisations
     * can include pre-populating the data or wrapping the document in one or more decorators.
     * @return a factory that allows the client to create customised Documents.
     */
    @Nonnull
    static DocumentFactory<Document> factory() {
        return DocumentFactoryImpl.create();
    }


    /**
     * Returns a factory that allows the client to wrap an existing Document with decorators.
     * The client is responsible for ensuring that actions on the unwrapped {@code source} document do not
     * change the expected semantics of wrapped Document returned by the factory. Typically, the
     * client will use the generated document instead of the {@code source} document
     * @param source     a Document that will be wrapped in the generated document
     * @return a factory that allows the client to wrap an existing Document with decorators.
     */
    @Nonnull
    static WrappedDocumentFactory<Document> factory(@Nonnull CommonDocument source) {
        return DocumentFactoryImpl.create(source);
    }
}
