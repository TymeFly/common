package com.github.tymefly.common.document;

/**
 * Defines the contract for a Document that can be updated.
 * @param <D>   The type of this Document.
 */
public interface WritableDocument<D extends CommonDocument> extends CommonDocument, DocumentWriter<D> {
}
