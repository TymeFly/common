package com.github.tymefly.common.document.visitor.util;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.github.tymefly.common.document.ReadableDocument;

/**
 * Visitor that will merge two documents into a new one.
 * If a fields exists in both documents then the value in {@literal this} document will take precedence
 * The generated Document will not have any decorators. If decorators are required they can be added afterwards as
 * <pre>{@code
 * Document required = Document.factory(sourceDocument.accept(new Merge(otherDocument)))
 *         // Add decorators here
 *     .build();
 * }</pre>
 */
@NotThreadSafe
public non-sealed class Merge extends AbstractMergeVisitor {
    /**
     * Constructor
     * @param other     Other document to merge
     */
    public Merge(@Nonnull ReadableDocument other) {
        super(other.accept(new Copy()));
    }
}
