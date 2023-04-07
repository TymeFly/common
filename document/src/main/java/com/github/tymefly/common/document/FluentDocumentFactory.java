package com.github.tymefly.common.document;

import java.util.function.Function;

import javax.annotation.Nonnull;

import com.github.tymefly.common.document.decorator.NullFilterDocument;
import com.github.tymefly.common.document.decorator.SynchronizedDocument;
import com.github.tymefly.common.document.decorator.UnmodifiableDocument;

/**
 * {@link DocumentFactory} functions that can be used after the Document has been initialised.
 * @param <D>   The type of the generated Document
 */
public interface FluentDocumentFactory<D extends CommonDocument> extends DocumentWriter<FluentDocumentFactory<D>> {
    /**
     * Wrap the generated Document with a {@link UnmodifiableDocument} decorator.
     * This will cause all attempts to mutate the Document to throw a {@link UnsupportedOperationException}
     * @return              A fluent interface.
     */
    @Nonnull
    FluentDocumentFactory<? extends ReadableDocument> immutable();

    /**
     * Wrap the generated Document with a {@link NullFilterDocument} decorator.
     * This will cause setter methods that attempt to write {@code null} values into the Document to return without
     * mutating the document
     * @return              A fluent interface.
     */
    @Nonnull
    FluentDocumentFactory<Document> nullFilter();


    /**
     * Wrap the generated Document with a {@link SynchronizedDocument} decorator.
     * Adds synchronization to each method defined in {@link Document}
     * @return              A fluent interface.
     */
    @Nonnull
    FluentDocumentFactory<Document> withSynchronization();


    /**
     * Wraps the generated Document with a custom Decorator. The Decorator must extend {@link DocumentDecorator}
     * and needs to accept a wrapped AbstractDocument as the sole parameter for the {@code constructor} function
     * @param type          The type of the generated Document
     * @param constructor   constructor for factory method for the wrapped Document.
     * @param <E>           The type of the extended Document
     * @return              A fluent interface.
     */
    @Nonnull
    <E extends CommonDocument> FluentDocumentFactory<E> as(
            @Nonnull Class<? extends E> type,
            @Nonnull Function<AbstractDocument<?>, ? extends DocumentDecorator<E>> constructor);


    /**
     * Returns the Document described by this Factory
     * @return the Document described by this Factory
     */
    @Nonnull
    D build();
}
