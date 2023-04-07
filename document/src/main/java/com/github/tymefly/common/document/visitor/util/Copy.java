package com.github.tymefly.common.document.visitor.util;

import javax.annotation.concurrent.NotThreadSafe;

import com.github.tymefly.common.document.Document;

/**
 * Visitor that will create a deep copy of a Document.
 * The generated Document will not have any decorators. If decorators are required they can be added afterwards as
 * <pre>{@code
 * Document required = Document.factory(sourceDocument.accept(new Copy()))
 *     // Add decorators here
 *     .build();
 * }</pre>
 * @see com.github.tymefly.common.document.ReadableDocument#clone()
 */
@NotThreadSafe
public non-sealed class Copy extends AbstractMergeVisitor {
    /** Constructor */
    public Copy() {
        super(Document.newInstance());
    }
}
