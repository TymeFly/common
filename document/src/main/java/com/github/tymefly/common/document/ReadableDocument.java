package com.github.tymefly.common.document;

import javax.annotation.Nonnull;

import com.github.tymefly.common.document.visitor.DocumentVisitor;

/**
 * Defines the contract for a Document that can be read
 */
public interface ReadableDocument extends DocumentReader, CommonDocument, Cloneable {
    /**
     * Entry point for the visitor pattern.
     * Unless otherwise stated, visitors are single use objects. Clients are expected to create a new visitor
     * object for each call to this method
     * @param visitor       A Document visitor. Visitors can not mutate this Document
     * @param <T>           Type of data generated by the visitor
     * @return              Data generated by the visitor
     */
    @Nonnull
    <T> T accept(@Nonnull DocumentVisitor<T> visitor);

    /**
     * Returns a clone of this ReadableDocument, including all the data within the document and all the
     * decorators in the order they were originally defined
     * @return a clone of this ReadableDocument. This will be at least a Readable Document
     */
    @Nonnull
    ReadableDocument clone();
}
