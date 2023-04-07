package com.github.tymefly.common.document;

import java.io.File;
import java.io.InputStream;

import javax.annotation.Nonnull;

import com.github.tymefly.common.base.io.FailedIoException;
import com.github.tymefly.common.document.parse.DocumentParser;

/**
 * Defines a Factory that is used create Documents. Additional functionality and initial values can be configured.
 * The order that the functions in this interface are applied is important. For example, if the client wants an
 * immutable document it's probably a good idea to populate the document first.
 * If the client want to ensure that the generated document doesn't contain {@literal null} values then
 * it's probably a good idea to call {@link #nullFilter()} before populating it.
 * @param <D>       Type of Generated Document
 */
public interface DocumentFactory<D extends CommonDocument> extends FluentDocumentFactory<D> {
    /**
     * Load a document with data represented in the {@code source} String
     * @param source        data represented in a String
     * @param parser        a class that can parse the format of the data
     * @return              a fluent interface
     * @throws DocumentException if the data could not be parsed
     */
    @Nonnull
    FluentDocumentFactory<D> parse(@Nonnull String source,
                                   @Nonnull DocumentParser parser) throws DocumentException;

    /**
     * Load a document with data from the {@code source} file
     * @param fileName      path to a file on the local file system
     * @param parser        a class that can parse the format of the data
     * @return              a fluent interface
     * @throws FailedIoException if the {@code source} could not be read, or it contains more than
     *          {@link com.github.tymefly.common.base.io.LimitedInputStream#DEFAULT_LIMIT} bytes
     * @throws DocumentException if the data could not be parsed
     */
    @Nonnull
    FluentDocumentFactory<D> load(@Nonnull String fileName,
                                  @Nonnull DocumentParser parser) throws FailedIoException, DocumentException;

    /**
     * Load a document with data from the {@code source} file
     * @param source        a file on the local file system
     * @param parser        a class that can parse the format of the data
     * @return              a fluent interface
     * @throws FailedIoException if the {@code source} could not be read, or it contains more than
     *          {@link com.github.tymefly.common.base.io.LimitedInputStream#DEFAULT_LIMIT} bytes
     * @throws DocumentException if the data could not be parsed
     */
    @Nonnull
    FluentDocumentFactory<D> load(@Nonnull File source,
                                  @Nonnull DocumentParser parser) throws FailedIoException, DocumentException;

    /**
     * Load a document with data represented in the {@code source} stream
     * @param source        a data source
     * @param parser        a class that can parse the format of the data
     * @return              a fluent interface
     * @throws FailedIoException if the {@code source} could not be read, or it contains more than
     *          {@link com.github.tymefly.common.base.io.LimitedInputStream#DEFAULT_LIMIT} bytes
     * @throws DocumentException if the data could not be parsed
     */
    @Nonnull
    FluentDocumentFactory<D> load(@Nonnull InputStream source,
                                  @Nonnull DocumentParser parser) throws FailedIoException, DocumentException;

    /**
     * Populate the generated document with a copy of the data in the {@code source} document.
     * Subsequent changes to the {@code source} document will not affect the generated document and vise-versa
     * @param source    Document to copy.
     * @return a fluent interface
     */
    @Nonnull
    FluentDocumentFactory<D> copy(@Nonnull ReadableDocument source);
}
