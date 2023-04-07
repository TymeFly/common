package com.github.tymefly.common.document.decorator;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.tymefly.common.document.AbstractDocument;
import com.github.tymefly.common.document.CommonDocument;
import com.github.tymefly.common.document.Document;
import com.github.tymefly.common.document.DocumentDecorator;
import com.github.tymefly.common.document.ReadableDocument;
import com.github.tymefly.common.document.key.DocumentKey;
import com.github.tymefly.common.document.visitor.DocumentVisitor;

/**
 * A {@link DocumentDecorator} that provides synchronization access to its data
 */
public class SynchronizedDocument extends DocumentDecorator<Document> implements Document {
    /**
     * Constructor
     * @param wrapped Wrapped AbstractDocument
     */
    public SynchronizedDocument(@Nonnull AbstractDocument<?> wrapped) {
        super(wrapped);
    }

    @Nonnull
    @Override
    public synchronized ReadableDocument unmodifiable() {
        return super.unmodifiable();
    }

    @Nonnull
    @Override
    public synchronized Document addString(@Nonnull DocumentKey key, @Nullable String value) {
        return super.addString(key, value);
    }

    @Nonnull
    @Override
    public synchronized Document addStrings(@Nonnull DocumentKey key, String... values) {
        return super.addStrings(key, values);
    }

    @Nonnull
    @Override
    public synchronized Document addStrings(@Nonnull DocumentKey key, @Nonnull Collection<String> values) {
        return super.addStrings(key, values);
    }

    @Nonnull
    @Override
    public synchronized Document appendString(@Nonnull DocumentKey key, @Nullable String value) {
        return super.appendString(key, value);
    }

    @Nonnull
    @Override
    public synchronized Document addNumber(@Nonnull DocumentKey key, @Nullable Number value) {
        return super.addNumber(key, value);
    }

    @Nonnull
    @Override
    public synchronized Document addNumbers(@Nonnull DocumentKey key, Number... values) {
        return super.addNumbers(key, values);
    }

    @Nonnull
    @Override
    public synchronized Document addNumbers(@Nonnull DocumentKey key, @Nonnull Collection<Number> values) {
        return super.addNumbers(key, values);
    }

    @Nonnull
    @Override
    public synchronized Document appendNumber(@Nonnull DocumentKey key, @Nullable Number value) {
        return super.appendNumber(key, value);
    }

    @Nonnull
    @Override
    public synchronized Document addBoolean(@Nonnull DocumentKey key, @Nullable Boolean value) {
        return super.addBoolean(key, value);
    }

    @Nonnull
    @Override
    public synchronized Document addBooleans(@Nonnull DocumentKey key, Boolean... values) {
        return super.addBooleans(key, values);
    }

    @Nonnull
    @Override
    public synchronized Document addBooleans(@Nonnull DocumentKey key, @Nonnull Collection<Boolean> values) {
        return super.addBooleans(key, values);
    }

    @Nonnull
    @Override
    public synchronized Document appendBoolean(@Nonnull DocumentKey key, @Nullable Boolean value) {
        return super.appendBoolean(key, value);
    }

    @Nonnull
    @Override
    public synchronized Document addEnum(@Nonnull DocumentKey key, @Nullable Enum<?> value) {
        return super.addEnum(key, value);
    }

    @SafeVarargs
    @Nonnull
    @Override
    public final synchronized <E extends Enum<E>> Document addEnums(@Nonnull DocumentKey key, E... values) {
        return super.addEnums(key, values);
    }

    @Nonnull
    @Override
    public synchronized <E extends Enum<E>> Document addEnums(@Nonnull DocumentKey key, @Nonnull Collection<E> values) {
        return super.addEnums(key, values);
    }

    @Nonnull
    @Override
    public synchronized <E extends Enum<E>> Document appendEnum(@Nonnull DocumentKey key, @Nullable E value) {
        return super.appendEnum(key, value);
    }

    @Nonnull
    @Override
    public synchronized Document addDocument(@Nonnull DocumentKey key, @Nullable CommonDocument value) {
        return super.addDocument(key, value);
    }

    @Nonnull
    @Override
    public synchronized Document addDocuments(@Nonnull DocumentKey key, CommonDocument... values) {
        return super.addDocuments(key, values);
    }

    @Nonnull
    @Override
    public synchronized Document addDocuments(@Nonnull DocumentKey key,
                                              @Nonnull Collection<? extends CommonDocument> values) {
        return super.addDocuments(key, values);
    }

    @Nonnull
    @Override
    public synchronized Document appendDocument(@Nonnull DocumentKey key, @Nullable CommonDocument value) {
        return super.appendDocument(key, value);
    }

    @Nonnull
    @Override
    public synchronized Document remove(@Nonnull DocumentKey key) {
        return super.remove(key);
    }

    @Nonnull
    @Override
    public synchronized <T> T get(@Nonnull DocumentKey key, @Nonnull Class<T> type) {
        return super.get(key, type);
    }

    @Nonnull
    @Override
    public synchronized <T> T getOrDefault(@Nonnull DocumentKey key, @Nonnull T defaultValue) {
        return super.getOrDefault(key, defaultValue);
    }

    @Nonnull
    @Override
    public synchronized <T> T get(@Nonnull DocumentKey key, @Nonnull Function<String, T> fromString) {
        return super.get(key, fromString);
    }

    @Nullable
    @Override
    public synchronized <T> T getOptional(@Nonnull DocumentKey key, @Nonnull Class<T> type) {
        return super.getOptional(key, type);
    }

    @Nonnull
    @Override
    public synchronized <T> List<? extends T> getAll(@Nonnull DocumentKey key, @Nonnull Class<T> type) {
        return super.getAll(key, type);
    }

    @Override
    public synchronized boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    public synchronized boolean contains(@Nonnull DocumentKey key) {
        return super.contains(key);
    }

    @Override
    public synchronized boolean hasValue(@Nonnull DocumentKey key) {
        return super.hasValue(key);
    }

    @Override
    public synchronized boolean isSequence(@Nonnull DocumentKey key) {
        return super.isSequence(key);
    }

    @Nonnull
    @Override
    public synchronized <T> T accept(@Nonnull DocumentVisitor<T> visitor) {
        return super.accept(visitor);
    }

    @Override
    public synchronized boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public synchronized int hashCode() {
        return super.hashCode();
    }
}
