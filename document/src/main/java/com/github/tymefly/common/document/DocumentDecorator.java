package com.github.tymefly.common.document;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.tymefly.common.document.decorator.UnmodifiableDocument;
import com.github.tymefly.common.document.key.DocumentKey;
import com.github.tymefly.common.document.visitor.DocumentVisitor;

/**
 * An abstract class that is implemented by call classes that need to customise the actions of a Document.
 * This class implements all methods described by {@link Document} by delegating to a wrapped AbstractDocument
 * instance. Implementing classes need only override the methods that they need to change.
 * @implNote There no guarantee the {@link DocumentKey} instances that are passed to this class are immutable.
 * Consequently, implementations should not store the keys for later use.
 * @param <D>   The type of this DocumentDecorator. It is strongly recommended that this is a {@link Document}
 *              so that it can easily interact with other DocumentDecorator types, however this isn't a requirement.
 *              Other types could be substituted should they need to extend the Document API
 */
public abstract non-sealed class DocumentDecorator<D extends CommonDocument> extends AbstractDocument<D> {
    private final AbstractDocument<?> wrapped;

    /**
     * Constructor
     * @param wrapped   Wrapped AbstractDocument
     */
    protected DocumentDecorator(@Nonnull AbstractDocument<?> wrapped) {
        this.wrapped = wrapped;
    }


    @Override
    @Nonnull
    protected AbstractDocument<?> getWrapped() {
        return wrapped;
    }


    @Override
    @Nonnull
    DocumentImpl getImpl() {
        return getWrapped().getImpl();
    }


    @Override
    @Nonnull
    Structure getStructure() {
        return getWrapped().getStructure();
    }

    @Override
    public boolean wraps(@Nonnull Class<? extends DocumentDecorator<?>> type) {
        return (this.getClass().isAssignableFrom(type) || getWrapped().wraps(type));
    }

    @Nonnull
    @Override
    public List<Class<? extends DocumentDecorator<?>>> wraps() {
        @SuppressWarnings("unchecked")
        Class<? extends DocumentDecorator<?>> type = (Class<? extends DocumentDecorator<?>>) this.getClass();
        List<Class<? extends DocumentDecorator<?>>> result = getWrapped().wraps();

        result.add(type);

        return result;
    }

    @Override
    public boolean canMutate() {
        return !wraps(UnmodifiableDocument.class);
    }

    @Nonnull
    @Override
    public ReadableDocument unmodifiable() {
        return (canMutate() ? new UnmodifiableDocument(this) : this);
    }

    @Nonnull
    @Override
    public D addString(@Nonnull DocumentKey key, @Nullable String value) {
        getWrapped().addString(key, value);

        return (D) this;
    }

    @Nonnull
    @Override
    public D addStrings(@Nonnull DocumentKey key, String... values) {
        getWrapped().addStrings(key, values);

        return (D) this;
    }

    @Nonnull
    @Override
    public D addStrings(@Nonnull DocumentKey key, @Nonnull Collection<String> values) {
        getWrapped().addStrings(key, values);

        return (D) this;
    }

    @Nonnull
    @Override
    public D appendString(@Nonnull DocumentKey key, @Nullable String value) {
        getWrapped().appendString(key, value);

        return (D) this;
    }

    @Nonnull
    @Override
    public D addNumber(@Nonnull DocumentKey key, @Nullable Number value) {
        getWrapped().addNumber(key, value);

        return (D) this;
    }

    @Nonnull
    @Override
    public D addNumbers(@Nonnull DocumentKey key, Number... values) {
        getWrapped().addNumbers(key, values);

        return (D) this;
    }

    @Nonnull
    @Override
    public D addNumbers(@Nonnull DocumentKey key, @Nonnull Collection<Number> values) {
        getWrapped().addNumbers(key, values);

        return (D) this;
    }

    @Nonnull
    @Override
    public D appendNumber(@Nonnull DocumentKey key, @Nullable Number value) {
        getWrapped().appendNumber(key, value);

        return (D) this;
    }

    @Nonnull
    @Override
    public D addBoolean(@Nonnull DocumentKey key, @Nullable Boolean value) {
        getWrapped().addBoolean(key, value);

        return (D) this;
    }

    @Nonnull
    @Override
    public D addBooleans(@Nonnull DocumentKey key, Boolean... values) {
        getWrapped().addBooleans(key, values);

        return (D) this;
    }

    @Nonnull
    @Override
    public D addBooleans(@Nonnull DocumentKey key, @Nonnull Collection<Boolean> values) {
        getWrapped().addBooleans(key, values);

        return (D) this;
    }

    @Nonnull
    @Override
    public D appendBoolean(@Nonnull DocumentKey key, @Nullable Boolean value) {
        getWrapped().appendBoolean(key, value);

        return (D) this;
    }

    @Nonnull
    @Override
    public D addEnum(@Nonnull DocumentKey key, @Nullable Enum<?> value) {
        getWrapped().addEnum(key, value);

        return (D) this;
    }

    @Nonnull
    @Override
    public <E extends Enum<E>> D addEnums(@Nonnull DocumentKey key, E... values) {
        getWrapped().addEnums(key, values);

        return (D) this;
    }

    @Nonnull
    @Override
    public <E extends Enum<E>> D addEnums(@Nonnull DocumentKey key, @Nonnull Collection<E> values) {
        getWrapped().addEnums(key, values);

        return (D) this;
    }

    @Nonnull
    @Override
    public <E extends Enum<E>> D appendEnum(@Nonnull DocumentKey key, @Nullable E value) {
        getWrapped().appendEnum(key, value);

        return (D) this;
    }

    @Nonnull
    @Override
    public D addDocument(@Nonnull DocumentKey key, @Nullable CommonDocument value) {
        getWrapped().addDocument(key, value);

        return (D) this;
    }

    @Nonnull
    @Override
    public D addDocuments(@Nonnull DocumentKey key, CommonDocument... values) {
        getWrapped().addDocuments(key, values);

        return (D) this;
    }

    @Nonnull
    @Override
    public D addDocuments(@Nonnull DocumentKey key, @Nonnull Collection<? extends CommonDocument> values) {
        getWrapped().addDocuments(key, values);

        return (D) this;
    }

    @Nonnull
    @Override
    public D appendDocument(@Nonnull DocumentKey key, @Nullable CommonDocument value) {
        getWrapped().appendDocument(key, value);

        return (D) this;
    }

    @Nonnull
    @Override
    public D remove(@Nonnull DocumentKey key) {
        getWrapped().remove(key);

        return (D) this;
    }


    @Nonnull
    @Override
    public <T> T get(@Nonnull DocumentKey key, @Nonnull Class<T> type) {
        return getWrapped().get(key, type);
    }

    @Nonnull
    @Override
    public <T> T getOrDefault(@Nonnull DocumentKey key, @Nonnull T defaultValue) {
        return getWrapped().getOrDefault(key, defaultValue);
    }

    @Nonnull
    @Override
    public <T> T get(@Nonnull DocumentKey key, @Nonnull Function<String, T> fromString) {
        return getWrapped().get(key, fromString);
    }

    @Nullable
    @Override
    public <T> T getOptional(@Nonnull DocumentKey key, @Nonnull Class<T> type) {
        return getWrapped().getOptional(key, type);
    }

    @Nonnull
    @Override
    public <T> List<? extends T> getAll(@Nonnull DocumentKey key, @Nonnull Class<T> type) {
        return getWrapped().getAll(key, type);
    }


    @Override
    public boolean isEmpty() {
        return getWrapped().isEmpty();
    }

    @Override
    public boolean contains(@Nonnull DocumentKey key) {
        return getWrapped().contains(key);
    }

    @Override
    public boolean hasValue(@Nonnull DocumentKey key) {
        return getWrapped().hasValue(key);
    }

    @Override
    public boolean isSequence(@Nonnull DocumentKey key) {
        return getWrapped().isSequence(key);
    }


    @Nonnull
    @Override
    public <T> T accept(@Nonnull DocumentVisitor<T> visitor) {
        return getWrapped().accept(visitor);
    }


    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Document ? getWrapped().equals(obj) : false);
    }

    @Override
    public int hashCode() {
        return getWrapped().hashCode();
    }

    @Override
    public String toString() {
        return getWrapped().toString();
    }
}
