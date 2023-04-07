package com.github.tymefly.common.document.visitor;

import javax.annotation.Nonnull;

import com.github.tymefly.common.document.DocumentReader;

/**
 * Extensible API for passing context data to a {@link DocumentVisitor}
 */
public interface VisitorContext {
    /**
     * Returns an interface that can read data from the Document that the visitor is operating on.
     * This is not guaranteed to be original document.
     * @return an interface that can read data from the Document that the visitor is operating on
     */
    @Nonnull
    DocumentReader reader();
}
