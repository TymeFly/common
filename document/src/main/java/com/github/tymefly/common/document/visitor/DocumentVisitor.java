package com.github.tymefly.common.document.visitor;

import javax.annotation.Nonnull;

import com.github.tymefly.common.document.CommonDocument;
import com.github.tymefly.common.document.Document;

/**
 * Defines the contract of DocumentVisitor.
 * Visitor implementations are generally not thread safe and unless otherwise stated, DocumentVisitors are
 * single use objects.
 * @param <T>       Type of the data generated by the visitor
 * @see Document#accept(DocumentVisitor)
 */
public interface DocumentVisitor<T> {
    /**
     * Optional method to initialise the visitor with data passed from the context
     * @param context   the visitor context
     * @return          a fluent interface
     */
    default DocumentVisitor<T> initialise(@Nonnull VisitorContext context) {
        return this;
    }

    /**
     * Instructs the visitor to process a {@literal null} value
     * @param key       Accessor object that provides the key of the field in the Document associated with this value
     * @return          a fluent interface
     */
    @Nonnull
    DocumentVisitor<T> nullValue(@Nonnull VisitorKey key);

    /**
     * Instructs the visitor to process a {@link String} value
     * @param key       Accessor object that provides the key of the field in the Document associated with this value
     * @param value     a String value that is stored in the Document
     * @return          a fluent interface
     */
    @Nonnull
    DocumentVisitor<T> stringValue(@Nonnull VisitorKey key, @Nonnull String value);

    /**
     * Instructs the visitor to process a {@link Number} value
     * @param key       Accessor object that provides the key of the field in the Document associated with this value
     * @param value     a numeric value that is stored in the Document
     * @return          a fluent interface
     */
    @Nonnull
    DocumentVisitor<T> numericValue(@Nonnull VisitorKey key, @Nonnull Number value);

    /**
     * Instructs the visitor to process a {@link Boolean} value
     * @param key       Accessor object that provides the key of the field in the Document associated with this value
     * @param value     a boolean value that is stored in the Document
     * @return          a fluent interface
     */
    @Nonnull
    DocumentVisitor<T> booleanValue(@Nonnull VisitorKey key, boolean value);

    /**
     * Instructs the visitor to process a value that extends {@link Enum}
     * @param key       Accessor object that provides the key of the field in the Document associated with this value
     * @param value     an enumeration constant that is stored in the Document
     * @return          a fluent interface
     */
    @Nonnull
    DocumentVisitor<T> enumValue(@Nonnull VisitorKey key, @Nonnull Enum<?> value);

    /**
     * Instructs the visitor that the Document will start sending values in a child document
     * @param key       Accessor object that provides the key of the child document
     * @return          a fluent interface
     * @see #endChild(VisitorKey)
     */
    @Nonnull
    DocumentVisitor<T> beginChild(@Nonnull VisitorKey key);

    /**
     * Instructs the visitor that the Document has stopped processing elements in a child document
     * @param key       Accessor object that provides the key of the child document
     * @return          a fluent interface
     * @see #endChild(VisitorKey)
     */
    @Nonnull
    DocumentVisitor<T> endChild(@Nonnull VisitorKey key);

    /**
     * Instructs the visitor that the Document will start sending values in a sequence of elements. These will
     * be sent in order of their index
     * @param key       Accessor object that provides the key of the sequence
     * @param type      This will be exactly one of {@link String}, {@link Number}, {@link Boolean}, {@link Enum} or
     *                  {@link CommonDocument}. Sub-types of these values will not be passed.
     * @param size      Number of elements in the immediate child Document
     * @return          a fluent interface
     * @see #endSequence(VisitorKey)
     */
    @Nonnull
    DocumentVisitor<T> beginSequence(@Nonnull VisitorKey key, @Nonnull Class<?> type, int size);

    /**
     * Instructs the visitor that the Document has stopped processing elements in a sequence
     * @param key       Accessor object that provides the key of the sequence
     * @return          a fluent interface
     * @see #beginSequence(VisitorKey, Class, int)
     */
    @Nonnull
    DocumentVisitor<T> endSequence(@Nonnull VisitorKey key);

    /**
     * Returns {@literal true} as soon as this visitor has read all the data it requires to return the data it
     * required. The handling Document will attempt to finish processing without passing any more data to this
     * visitor. This includes skipping calls to {@link #endChild(VisitorKey)} and {@link #endSequence(VisitorKey)}
     * Most Visitors will require all the data in the document, so this method defaults to return {@literal false}.
     * @return {@literal true} as soon as this visitor has read all the data it requires.
     */
    default boolean isComplete() {
        return false;
    }

    /**
     * Returns the data generated by the visitor
     * @return the data generated by the visitor
     */
    @Nonnull
    T process();
}
