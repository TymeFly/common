package com.github.tymefly.common.document;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.tymefly.common.base.io.FailedIoException;
import com.github.tymefly.common.base.io.LimitedInputStream;
import com.github.tymefly.common.document.decorator.NullFilterDocument;
import com.github.tymefly.common.document.decorator.SynchronizedDocument;
import com.github.tymefly.common.document.decorator.UnmodifiableDocument;
import com.github.tymefly.common.document.key.DocumentKey;
import com.github.tymefly.common.document.parse.DocumentParser;
import com.github.tymefly.common.document.visitor.util.Copy;

/**
 * The only Implementation of {@link DocumentFactory}.
 * @param <D> Type of the generated Document
 */
class DocumentFactoryImpl<D extends CommonDocument> implements WrappedDocumentFactory<D>, DocumentFactory<D> {
    // Lazy constructor for Empty Document
    private static class Empty {
        private static final ReadableDocument DOC = create().immutable().build();
    }


    private final Function<AbstractDocument<?>, ? extends AbstractDocument<?>> constructor;
    private final Class<? extends D> type;
    private final AbstractDocument<?> backing;


    private DocumentFactoryImpl(@Nonnull Class<? extends D> type,
                                @Nonnull Function<AbstractDocument<?>, ? extends AbstractDocument<?>> constructor,
                                @Nonnull AbstractDocument<?> backing) {
        this.type = type;
        this.backing = backing;
        this.constructor = constructor;
    }


    /** Returns a singleton Document that is guaranteed to be empty and immutable */
    @Nonnull
    static ReadableDocument empty() {
        return Empty.DOC;
    }

    @Nonnull
    static DocumentFactoryImpl<Document> create() {
        return new DocumentFactoryImpl<>(Document.class, DocumentImpl::new, new DocumentImpl(null));
    }

    @Nonnull
    static DocumentFactoryImpl<Document> create(@Nonnull CommonDocument source) {
        AbstractDocument<?> backing = (AbstractDocument<?>) source;
        Function<AbstractDocument<?>, ? extends AbstractDocument<?>> constructor = backing.getImpl().getConstructor();

        return new DocumentFactoryImpl<>(Document.class, constructor, backing);
    }


    @Override
    @Nonnull
    public DocumentFactoryImpl<? extends ReadableDocument> immutable() {
        // Since the backing document is not accessible until the factory has been completed
        // then an immutable Document is just an Unmodifiable view of the backing Document.
        return as(UnmodifiableDocument.class, UnmodifiableDocument::new);
    }

    @Override
    @Nonnull
    public DocumentFactoryImpl<Document> nullFilter() {
        return as(Document.class, NullFilterDocument::new);
    }

    @Nonnull
    @Override
    public DocumentFactoryImpl<Document> withSynchronization() {
        return as(Document.class, SynchronizedDocument::new);
    }

    @Override
    @Nonnull
    public <E extends CommonDocument> DocumentFactoryImpl<E> as(
                @Nonnull Class<? extends E> type,
                @Nonnull Function<AbstractDocument<?>, ? extends DocumentDecorator<E>> constructor) {
        Function<AbstractDocument<?>, DocumentDecorator<E>> chain = this.constructor.andThen(constructor);
        DocumentDecorator<E> wrapper = constructor.apply(this.backing);

        return new DocumentFactoryImpl<>(type, chain, wrapper);
    }


    @Override
    @Nonnull
    public FluentDocumentFactory<D> copy(@Nonnull ReadableDocument source) {
        AbstractDocument<?> copy = (AbstractDocument<?>) source.accept(new Copy());
        Structure backing = copy.getStructure();

        this.backing.getImpl().setStructure(backing);

        return this;
    }

    @Nonnull
    @Override
    public FluentDocumentFactory<D> load(@Nonnull String fileName,
                                         @Nonnull DocumentParser parser) throws FailedIoException, DocumentException {
        return load(new File(fileName), parser);
    }

    @Nonnull
    @Override
    public FluentDocumentFactory<D> load(@Nonnull File source,
                                         @Nonnull DocumentParser parser) throws FailedIoException, DocumentException {
        try (
            InputStream unbuffered = new FileInputStream(source);
            InputStream buffered = new BufferedInputStream(unbuffered)
        ) {
            return load(buffered, parser);
        } catch (Exception e) {
            throw new FailedIoException("Failed to load '" + source.getName() + "'", e);
        }
    }

    @Override
    @Nonnull
    public FluentDocumentFactory<D> load(@Nonnull InputStream source,
                                         @Nonnull DocumentParser parser) throws FailedIoException, DocumentException {
        try (
            InputStream stream = new LimitedInputStream(source)
        ) {
            parser.load(backing, stream);
        } catch (Exception e) {
            throw new FailedIoException("Failed to load Document", e);
        }

        return this;
    }

    @Override
    @Nonnull
    public FluentDocumentFactory<D> parse(@Nonnull String source,
                                          @Nonnull DocumentParser parser) throws FailedIoException, DocumentException {
        byte[] raw = source.getBytes(StandardCharsets.UTF_8);

        try (
            InputStream stream = new ByteArrayInputStream(raw)
        ) {
            load(stream, parser);
        } catch (IOException e) {
            throw new FailedIoException("Failed to open source", e);
        }

        return this;
    }


    @Nonnull
    @Override
    public FluentDocumentFactory<D> addString(@Nonnull DocumentKey key, @Nullable String value) {
        backing.addString(key, value);

        return this;
    }

    @Nonnull
    @Override
    public FluentDocumentFactory<D> addStrings(@Nonnull DocumentKey key, String... values) {
        backing.addStrings(key, values);

        return this;
    }

    @Nonnull
    @Override
    public FluentDocumentFactory<D> addStrings(@Nonnull DocumentKey key, @Nonnull Collection<String> values) {
        backing.addStrings(key, values);

        return this;
    }

    @Nonnull
    @Override
    public FluentDocumentFactory<D> appendString(@Nonnull DocumentKey key, @Nullable String value) {
        backing.appendString(key, value);

        return this;
    }

    @Nonnull
    @Override
    public FluentDocumentFactory<D> addNumber(@Nonnull DocumentKey key, @Nullable Number value) {
        backing.addNumber(key, value);

        return this;
    }

    @Nonnull
    @Override
    public FluentDocumentFactory<D> addNumbers(@Nonnull DocumentKey key, Number... values) {
        backing.addNumbers(key, values);

        return this;
    }

    @Nonnull
    @Override
    public FluentDocumentFactory<D> addNumbers(@Nonnull DocumentKey key, @Nonnull Collection<Number> values) {
        backing.addNumbers(key, values);

        return this;
    }

    @Nonnull
    @Override
    public FluentDocumentFactory<D> appendNumber(@Nonnull DocumentKey key, @Nullable Number value) {
        backing.appendNumber(key, value);

        return this;
    }

    @Nonnull
    @Override
    public FluentDocumentFactory<D> addBoolean(@Nonnull DocumentKey key, @Nullable Boolean value) {
        backing.addBoolean(key, value);

        return this;
    }

    @Nonnull
    @Override
    public FluentDocumentFactory<D> addBooleans(@Nonnull DocumentKey key, Boolean... values) {
        backing.addBooleans(key, values);

        return this;
    }

    @Nonnull
    @Override
    public FluentDocumentFactory<D> addBooleans(@Nonnull DocumentKey key, @Nonnull Collection<Boolean> values) {
        backing.addBooleans(key, values);

        return this;
    }

    @Nonnull
    @Override
    public FluentDocumentFactory<D> appendBoolean(@Nonnull DocumentKey key, @Nullable Boolean value) {
        backing.appendBoolean(key, value);

        return this;
    }

    @Nonnull
    @Override
    public FluentDocumentFactory<D> addEnum(@Nonnull DocumentKey key, @Nullable Enum<?> value) {
        backing.addEnum(key, value);

        return this;
    }

    @Nonnull
    @Override
    public <E extends Enum<E>> FluentDocumentFactory<D> addEnums(@Nonnull DocumentKey key, E... values) {
        backing.addEnums(key, values);

        return this;
    }

    @Nonnull
    @Override
    public <E extends Enum<E>> FluentDocumentFactory<D> addEnums(@Nonnull DocumentKey key,
                                                                 @Nonnull Collection<E> values) {
        backing.addEnums(key, values);

        return this;
    }

    @Nonnull
    @Override
    public <E extends Enum<E>> FluentDocumentFactory<D> appendEnum(@Nonnull DocumentKey key, @Nullable E value) {
        backing.appendEnum(key, value);

        return this;
    }

    @Nonnull
    @Override
    public FluentDocumentFactory<D> addDocument(@Nonnull DocumentKey key, @Nullable CommonDocument value) {
        backing.addDocument(key, value);

        return this;
    }

    @Nonnull
    @Override
    public FluentDocumentFactory<D> addDocuments(@Nonnull DocumentKey key, CommonDocument... values) {
        backing.addDocuments(key, values);

        return this;
    }

    @Nonnull
    @Override
    public FluentDocumentFactory<D> addDocuments(@Nonnull DocumentKey key,
                                                 @Nonnull Collection<? extends CommonDocument> values) {
        backing.addDocuments(key, values);

        return this;
    }

    @Nonnull
    @Override
    public FluentDocumentFactory<D> appendDocument(@Nonnull DocumentKey key, @Nullable CommonDocument value) {
        backing.appendDocument(key, value);

        return this;
    }

    @Nonnull
    @Override
    public FluentDocumentFactory<D> remove(@Nonnull DocumentKey key) {
        backing.remove(key);

        return this;
    }


    @Override
    @Nonnull
    public D build() {
        backing.getImpl().setConstructor(constructor);

        return type.cast(backing);
    }
}
