package com.github.tymefly.common.document.decorator;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.tymefly.common.document.AbstractDocument;
import com.github.tymefly.common.document.CommonDocument;
import com.github.tymefly.common.document.Document;
import com.github.tymefly.common.document.DocumentDecorator;
import com.github.tymefly.common.document.ReadableDocument;
import com.github.tymefly.common.document.key.DocumentKey;

/**
 * A {@link DocumentDecorator} that prevents clients from mutating data.
 */
public class UnmodifiableDocument extends DocumentDecorator<Document> implements Document {
    private static final UnsupportedOperationException ERROR =
            new UnsupportedOperationException("Can not mutate Document");

    /**
     * Constructor
     * @param wrapped   The underlying object that this class decorates
     */
    public UnmodifiableDocument(@Nonnull AbstractDocument<?> wrapped) {
        super(wrapped);
    }

    @Override
    public boolean canMutate() {
        return false;
    }

    @Nonnull
    @Override
    public ReadableDocument unmodifiable() {
        return this;
    }

    @Nonnull
    @Override
    public Document addString(@Nonnull DocumentKey key, @Nullable String value) {
        throw ERROR;
    }

    @Nonnull
    @Override
    public Document addStrings(@Nonnull DocumentKey key, String... values) {
        throw ERROR;
    }

    @Nonnull
    @Override
    public Document addStrings(@Nonnull DocumentKey key, @Nonnull Collection<String> values) {
        throw ERROR;
    }

    @Nonnull
    @Override
    public Document appendString(@Nonnull DocumentKey key, @Nullable String value) {
        throw ERROR;
    }

    @Nonnull
    @Override
    public Document addNumber(@Nonnull DocumentKey key, @Nullable Number value) {
        throw ERROR;
    }

    @Nonnull
    @Override
    public Document addNumbers(@Nonnull DocumentKey key, Number... values) {
        throw ERROR;
    }

    @Nonnull
    @Override
    public Document addNumbers(@Nonnull DocumentKey key, @Nonnull Collection<Number> values) {
        throw ERROR;
    }

    @Nonnull
    @Override
    public Document appendNumber(@Nonnull DocumentKey key, @Nullable Number value) {
        throw ERROR;
    }

    @Nonnull
    @Override
    public Document addBoolean(@Nonnull DocumentKey key, @Nullable Boolean value) {
        throw ERROR;
    }

    @Nonnull
    @Override
    public Document addBooleans(@Nonnull DocumentKey key, Boolean... values) {
        throw ERROR;
    }

    @Nonnull
    @Override
    public Document addBooleans(@Nonnull DocumentKey key, @Nonnull Collection<Boolean> values) {
        throw ERROR;
    }

    @Nonnull
    @Override
    public Document appendBoolean(@Nonnull DocumentKey key, @Nullable Boolean value) {
        throw ERROR;
    }

    @Nonnull
    @Override
    public Document addEnum(@Nonnull DocumentKey key, @Nullable Enum<?> value) {
        throw ERROR;
    }

    @SafeVarargs
    @Nonnull
    @Override
    public final <E extends Enum<E>> Document addEnums(@Nonnull DocumentKey key, E... values) {
        throw ERROR;
    }

    @Nonnull
    @Override
    public <E extends Enum<E>> Document addEnums(@Nonnull DocumentKey key, @Nonnull Collection<E> values) {
        throw ERROR;
    }

    @Nonnull
    @Override
    public <E extends Enum<E>> Document appendEnum(@Nonnull DocumentKey key, @Nullable E value) {
        throw ERROR;
    }

    @Nonnull
    @Override
    public Document addDocument(@Nonnull DocumentKey key, @Nullable CommonDocument value) {
        throw ERROR;
    }

    @Nonnull
    @Override
    public Document addDocuments(@Nonnull DocumentKey key, CommonDocument... values) {
        throw ERROR;

    }

    @Nonnull
    @Override
    public Document addDocuments(@Nonnull DocumentKey key, @Nonnull Collection<? extends CommonDocument> values) {
        throw ERROR;
    }

    @Nonnull
    @Override
    public Document appendDocument(@Nonnull DocumentKey key, @Nullable CommonDocument value) {
        throw ERROR;
    }

    @Nonnull
    @Override
    public Document remove(@Nonnull DocumentKey key) {
        throw ERROR;
    }
}
