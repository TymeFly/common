package com.github.tymefly.common.document.visitor;

import javax.annotation.Nonnull;

import com.github.tymefly.common.document.key.DocumentKey;

/**
 * Key objects contain a number of methods that are used by {@link DocumentVisitor} implementations
 * to obtain the appropriate externalised form of a document key for each data element.
 */
public interface VisitorKey {
    /**
     * Returns the simple document key. This does not include any parent elements or sequence indexes
     * @return the simple document key
     * @see DocumentKey#SIMPLE_KEY
     */
    @Nonnull
    String simpleKey();

    /**
     * Returns the optional index for this key, or {@literal -1} if there is no index
     * @return the optional index for this key
     * @see DocumentKey#INDEX
     */
    int getIndex();

    /**
     * Returns the element name. This does not include parent elements, but can include optional sequence indexes
     * @return the element name
     * @see DocumentKey#ELEMENT
     */
    @Nonnull
    String element();

    /**
     * Returns the path from the root of the document up to and including the {@link #simpleKey()},
     * but does not include the final, optional, index
     * @return the full document key
     */
    @Nonnull
    String simpleKeyPath();

    /**
     * Returns the full path from the root of the document including all parent keys and indexes
     * @return the full document key
     * @see DocumentKey#FULL_PATH
     */
    @Nonnull
    String fullPath();

    /**
     * Returns the full document key from the root of the document including all parent keys and indexes.
     * Calling {@link DocumentKey#externalise()} will return a string equal to {@link #fullPath()}
     * @return the full document key.
     */
    @Nonnull
    DocumentKey documentKey();
}
