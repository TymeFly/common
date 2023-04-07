package com.github.tymefly.common.document;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * Defines the common operations implemented all Documents
 */
public interface CommonDocument {
    /**
     * Check this document to see is a specific decorator has been applied.
     * @param type      Decorator that may wrap this document
     * @return          {@code true} only if this document has been decorated with the {@code type}
     */
    boolean wraps(@Nonnull Class<? extends DocumentDecorator<?>> type);

    /**
     * Returns a list of all the decorators applied to this Document in the order they were applied
     * @return a list of all the decorators applied to this Document in the order they were applied
     */
    @Nonnull
    List<Class<? extends DocumentDecorator<?>>> wraps();

    /**
     * Returns an unmodifiable view of this Document. Query operations on the returned ReadableDocument
     * "read through" to this Document via its decorators.
     * Attempts to modify the returned ReadableDocument result in an {@code UnsupportedOperationException}.
     * This document is unaffected by calling this method; if the Document is mutable then changes to it will
     * be reflected in the returned view. If this is not acceptable then an immutable copy of this
     * Document can be generated as:
     * <pre>{@code Document.factory()
     *     .copy(this)
     *     .immutable()
     *     .build();}</pre>
     * @return an unmodifiable view of this Document
     * @see #canMutate()
     */
    @Nonnull
    ReadableDocument unmodifiable();

    /**
     * Returns {@code true} only of this document can be mutated
     * @return {@code true} only of this document can be mutated
     */
    boolean canMutate();
}
