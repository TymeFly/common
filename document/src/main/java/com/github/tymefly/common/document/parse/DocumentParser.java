package com.github.tymefly.common.document.parse;

import java.io.InputStream;

import javax.annotation.Nonnull;

import com.github.tymefly.common.base.io.FailedIoException;
import com.github.tymefly.common.document.DocumentException;
import com.github.tymefly.common.document.WritableDocument;

/**
 * Defines the contract used by objects that read data into a Document
 */
public interface DocumentParser {
    /**
     * Load data from the {@code source} stream and store it in the {@code target} Document.
     * The format of the data is defined by the implementing class
     * @param target        Document that will be mutated with data from the {@code source} stream
     * @param source        Data that will be added to the {@code target} Document
     * @throws FailedIoException       if the {@code source} could not be read
     * @throws DocumentException if the {@code source} data is malformed.
     */
    void load(@Nonnull WritableDocument<?> target,
              @Nonnull InputStream source) throws FailedIoException, DocumentException;
}
