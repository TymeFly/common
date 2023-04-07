package com.github.tymefly.common.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.tymefly.common.base.utils.BigDecimals;
import com.github.tymefly.common.base.utils.Convert;
import com.github.tymefly.common.base.validate.Preconditions;
import com.github.tymefly.common.document.decorator.UnmodifiableDocument;
import com.github.tymefly.common.document.key.DocumentKey;
import com.github.tymefly.common.document.visitor.DocumentVisitor;
import com.github.tymefly.common.document.visitor.VisitorContext;
import com.github.tymefly.common.document.visitor.serializer.json.JsonSerializer;
import com.github.tymefly.common.document.visitor.util.Copy;


/**
 * The lowest level wrapped class created by {@link DocumentFactory}.
 * @implNote Internally all numbers are stored as BigDecimal
 * @implNote this class is final so that {@link #clone()} can be guaranteed to return the correct type without
 *              calling {@literal clone.super()}
 */
final class DocumentImpl extends AbstractDocument<Document> implements Document {
    /** Single implementation of VisitorContext */
    private class VisitorContextImpl implements VisitorContext {
        @Nonnull
        @Override
        public ReadableDocument reader() {
            return DocumentImpl.this;
        }
    }

    private static final Walker<Object> GET_WALKER = new Walker.Builder<>()
            .toWalk((d, k) -> d.getOptional(k, Object.class))
            .whenFound(DocumentImpl::getHelper)
            .whenNotFound(() -> null)
            .build();
    private static final Walker<Boolean> CONTAINS_WALKER = new Walker.Builder<Boolean>()
            .toWalk(ReadableDocument::contains)
            .whenFound(DocumentImpl::containsHelper)
            .whenNotFound(() -> false)
            .build();
    private static final Walker<WritableDocument<?>> REMOVE_WALKER = new Walker.Builder<WritableDocument<?>>()
            .toWalk(WritableDocument::remove)
            .whenFound(DocumentImpl::removeHelper)
            .whenNotFound(() -> null)
            .build();

    private Structure structure;
    private Function<AbstractDocument<?>, ? extends AbstractDocument<?>> constructor;
    private VisitorContext visitorContext;


    /**
     * Constructor
     * @param initialData   The initial data for this Document. This will usually be {@literal null}, as
     *                      Documents are generated without data. However, {@link #clone()} can pass a temporary
     *                      Document in which contains the underlying structure that can be used without copying.
     */
    DocumentImpl(@Nullable AbstractDocument<?> initialData) {
        structure = initialData == null ?  new Structure() : initialData.getStructure();
        constructor = DocumentImpl::new;
        visitorContext = null;
    }

    @Nonnull
    @Override
    AbstractDocument<?> getWrapped() {
        return this;
    }

    @Nonnull
    DocumentImpl getImpl() {
        return this;
    }

    @Override
    @Nonnull
    Structure getStructure() {
        return structure;
    }

    void setStructure(@Nonnull Structure structure) {
        this.structure = structure;
    }

    void setConstructor(@Nonnull Function<AbstractDocument<?>, ? extends AbstractDocument<?>> constructor) {
        this.constructor = constructor;
    }

    @Nonnull
    Function<AbstractDocument<?>, ? extends AbstractDocument<?>> getConstructor() {
        return this.constructor;
    }

    @Override
    public boolean wraps(@Nonnull Class<? extends DocumentDecorator<?>> type) {
        return false;
    }

    @Override
    @Nonnull
    public List<Class<? extends DocumentDecorator<?>>> wraps() {
        return new ArrayList<>();
    }

    @Override
    public boolean canMutate() {
        return true;
    }


    @Nonnull
    @Override
    public ReadableDocument unmodifiable() {
        return new UnmodifiableDocument(this);
    }


    @Override
    @Nonnull
    public DocumentImpl addString(@Nonnull DocumentKey key, @Nullable String value) {
        add(key, String.class, value, (d, k) -> d.addString(k, value));

        return this;
    }

    @Override
    @Nonnull
    public DocumentImpl addStrings(@Nonnull DocumentKey key, String... values) {
        add(key, String.class, values, (d, k) -> d.addStrings(k, values));

        return this;
    }

    @Override
    @Nonnull
    public DocumentImpl addStrings(@Nonnull DocumentKey key, @Nonnull Collection<String> values) {
        add(key, String.class, values, (d, k) -> d.addStrings(k, values));

        return this;
    }

    @Nonnull
    @Override
    public Document appendString(@Nonnull DocumentKey key, @Nullable String value) {
        append(key, String.class, value, (d, k) -> d.appendString(k, value));

        return this;
    }


    @Override
    @Nonnull
    public DocumentImpl addNumber(@Nonnull DocumentKey key, @Nullable Number value) {
        add(key, Number.class, value, (d, k) -> d.addNumber(k, value), BigDecimals::toBigDecimal);

        return this;
    }

    @Override
    @Nonnull
    public DocumentImpl addNumbers(@Nonnull DocumentKey key, Number... values) {
        add(key, Number.class, values, (d, k) -> d.addNumbers(k, values), BigDecimals::toBigDecimal);

        return this;
    }

    @Override
    @Nonnull
    public DocumentImpl addNumbers(@Nonnull DocumentKey key, @Nonnull Collection<Number> values) {
        add(key, Number.class, values, (d, k) -> d.addNumbers(k, values), BigDecimals::toBigDecimal);

        return this;
    }

    @Nonnull
    @Override
    public Document appendNumber(@Nonnull DocumentKey key, @Nullable Number value) {
        append(key, Number.class, value, (d, k) -> d.appendNumber(k, value), BigDecimals::toBigDecimal);

        return this;
    }


    @Override
    @Nonnull
    public DocumentImpl addBoolean(@Nonnull DocumentKey key, @Nullable Boolean value) {
        add(key, Boolean.class, value, (d, k) -> d.addBoolean(k, value));

        return this;
    }

    @Override
    @Nonnull
    public DocumentImpl addBooleans(@Nonnull DocumentKey key, Boolean... values) {
        add(key, Boolean.class, values, (d, k) -> d.addBooleans(k, values));

        return this;
    }

    @Override
    @Nonnull
    public DocumentImpl addBooleans(@Nonnull DocumentKey key, @Nonnull Collection<Boolean> values) {
        add(key, Boolean.class, values, (d, k) -> d.addBooleans(k, values));

        return this;
    }

    @Nonnull
    @Override
    public Document appendBoolean(@Nonnull DocumentKey key, @Nullable Boolean value) {
        append(key, Boolean.class, value, (d, k) -> d.appendBoolean(k, value));

        return this;
    }


    @Override
    @Nonnull
    public DocumentImpl addEnum(@Nonnull DocumentKey key, @Nullable Enum<?> value) {
        add(key, Enum.class, value, (d, k) -> d.addEnum(k, value));

        return this;
    }

    @SafeVarargs
    @Override
    @Nonnull
    public final <E extends Enum<E>> DocumentImpl addEnums(@Nonnull DocumentKey key, E... values) {
        add(key, Enum.class, values, (d, k) -> d.addEnums(k, values));

        return this;
    }

    @Override
    @Nonnull
    public <E extends Enum<E>> DocumentImpl addEnums(@Nonnull DocumentKey key, @Nonnull Collection<E> values) {
        add(key, Enum.class, values, (d, k) -> d.addEnums(k, values));

        return this;
    }

    @Nonnull
    @Override
    public <E extends Enum<E>> Document appendEnum(@Nonnull DocumentKey key, @Nullable E value) {
        append(key, Enum.class, value, (d, k) -> d.appendEnum(k, value));

        return this;
    }

    @Nonnull
    @Override
    public Document addDocument(@Nonnull DocumentKey key, @Nullable CommonDocument value) {
        add(key, CommonDocument.class, value, (d, k) -> d.addDocument(k, value), this::validateDocument);

        return this;
    }

    @Nonnull
    @Override
    public Document addDocuments(@Nonnull DocumentKey key, CommonDocument... values) {
        add(key, CommonDocument.class, values, (d, k) -> d.addDocuments(k, values), this::validateDocument);

        return this;
    }

    @Override
    @Nonnull
    public DocumentImpl addDocuments(@Nonnull DocumentKey key, @Nonnull Collection<? extends CommonDocument> values) {
        add(key, CommonDocument.class, values, (d, k) -> d.addDocuments(k, values), this::validateDocument);

        return this;
    }

    @Nonnull
    @Override
    public Document appendDocument(@Nonnull DocumentKey key, @Nullable CommonDocument value) {
        append(key, CommonDocument.class, value, (d, k) -> d.appendDocument(k, value), this::validateDocument);

        return this;
    }

    @Nullable
    private <T extends CommonDocument> T validateDocument(@Nullable T document) {
        Preconditions.checkArgument(((document == null) || (document instanceof AbstractDocument)),
                "Unexpected Document Type");

        return document;
    }


    private <T> void append(@Nonnull DocumentKey key,
                            @Nonnull Class<T> type,
                            @Nullable Object data,
                            @Nonnull BiConsumer<WritableDocument<?>, DocumentKey> walkFunction) {
        append(key, type, data, walkFunction, Function.identity());
    }

    private <T> void append(@Nonnull DocumentKey key,
                            @Nonnull Class<T> type,
                            @Nullable Object data,
                            @Nonnull BiConsumer<WritableDocument<?>, DocumentKey> walkFunction,
                            @Nonnull Function<T, T> transform) {
        new Inserter.Builder()
            .toWalk(walkFunction)
            .constructBy(constructor)
            .whenFound((d, k) -> d.appendHelper(k, type, data, transform))
            .build()
            .insert(this, key);
    }

    private <T> void appendHelper(@Nonnull WalkerKey walkerKey,
                                  @Nonnull Class<T> type,
                                  @Nullable Object value,
                                  @Nonnull Function<T, T> transform) {
        if (value != null) {
            value = transform.apply(type.cast(value));
        }

        if (walkerKey.hasIndex()) {
            throw new DocumentException("Can not append to '%s'", walkerKey.fullKey().externalise());
        } else {
            Sequence<T> sequence = WalkerHelper.getSequence(structure, walkerKey, type);
            sequence.append(type.cast(value));
        }
    }

    private <T> void add(@Nonnull DocumentKey key,
                         @Nonnull Class<T> type,
                         @Nullable Object data,
                         @Nonnull BiConsumer<WritableDocument<?>, DocumentKey> walkFunction) {
        add(key, type, data, walkFunction, Function.identity());
    }

    private <T> void add(@Nonnull DocumentKey key,
                         @Nonnull Class<T> type,
                         @Nullable Object data,
                         @Nonnull BiConsumer<WritableDocument<?>, DocumentKey> walkFunction,
                         @Nonnull Function<T, T> transform) {
        new Inserter.Builder()
            .toWalk(walkFunction)
            .constructBy(constructor)
            .whenFound((d, k) -> d.addHelper(k, type, data, transform))
            .build()
            .insert(this, key);
    }

    private <T> void addHelper(@Nonnull WalkerKey walkerKey,
                               @Nonnull Class<T> type,
                               @Nullable Object value,
                               @Nonnull Function<T, T> transform) {
        if (value == null) {
            // do nothing - null is always stored as null
        } else if (value instanceof Collection) {
            value = Sequence.of(type, transformAll(transform, (Collection<? extends T>) value));
        } else if (value.getClass().isArray()) {
            value = Sequence.of(type, transformAll(transform,  (T[]) value));
        } else {
            value = transform.apply(type.cast(value));
        }

        if (walkerKey.hasIndex()) {
            Sequence<T> sequence = WalkerHelper.getSequence(structure, walkerKey, type);

            sequence.set(walkerKey.index(), type.cast(value));
        } else {
            structure.add(walkerKey.simpleKey(), value);
        }
    }


    @Nonnull
    @Override
    public Document remove(@Nonnull DocumentKey key) {
        REMOVE_WALKER.walk(this, key);

        return this;
    }

    @Nonnull
    private WritableDocument<?> removeHelper(@Nonnull WalkerKey walkerKey) {
        String simple = walkerKey.simpleKey();

        if (walkerKey.hasIndex()) {
            int index = walkerKey.index();
            Object data = structure.get(simple);
            Sequence<?> children = WalkerHelper.cast(Sequence.class, walkerKey, data);
            boolean empty = (children == null ? false : children.remove(index));

            if (empty) {
                structure.remove(simple);
            }
        } else {
            structure.remove(simple);
        }

        return this;
    }


    @Override
    @Nonnull
    public <T> T get(@Nonnull DocumentKey key, @Nonnull Class<T> type) {
        T value = getOptional(key, type);

        return Preconditions.checkNotNull(value, "Document does not have a value for %s", key.externalise());
    }

    @Override
    @Nonnull
    public <T> T getOrDefault(@Nonnull DocumentKey key, @Nonnull T defaultValue) {
        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>)
            (defaultValue instanceof CommonDocument ? CommonDocument.class: defaultValue.getClass());
        T value = getOptional(key, type);

        return (value == null ? defaultValue : value);
    }

    @Nonnull
    @Override
    public <T> T get(@Nonnull DocumentKey key, @Nonnull Function<String, T> fromString) {
        String asString = getOptional(key, String.class);

        return fromString.apply(asString);
    }

    @Override
    @Nullable
    public <T> T getOptional(@Nonnull DocumentKey key, @Nonnull Class<T> type) {
        T result;
        Object value = GET_WALKER.walk(this, key);

        if ((value instanceof Sequence) && (type == Object.class)) { // Special case - sequence can't be seen externally
            result = (T) ((Sequence<?>) value).toList();
        } else {
            result = to(key, value, type);
        }

        return result;
    }

    @Nullable
    private Object getHelper(@Nonnull WalkerKey walkerKey) {
        return WalkerHelper.get(Object.class, structure, walkerKey);
    }


    @Override
    @Nonnull
    public <T> List<? extends T> getAll(@Nonnull DocumentKey key, @Nonnull Class<T> type) {
        Object data = getOptional(key, Object.class);
        List<T> list;

        if (data == null) {
            list = Collections.emptyList();
        } else if (data instanceof Collection) {
            list = toList(key, (Collection<?>) data, type);
        } else {
            list = List.of(to(key, data, type));
        }

        return list;
    }


    @Override
    public boolean isEmpty() {
        return structure.isEmpty();
    }

    @Override
    public boolean contains(@Nonnull DocumentKey key) {
        Boolean result = CONTAINS_WALKER.walk(this, key);

        return (result == null ? false : result);
    }

    private boolean containsHelper(@Nonnull WalkerKey walkerKey) {
        boolean contains;
        String simple = walkerKey.simpleKey();

        if (walkerKey.hasIndex()) {
            int index = walkerKey.index();
            Object data = structure.get(simple);
            Sequence<?> children = (Sequence<?>) WalkerHelper.cast(Sequence.class, walkerKey, data);

            contains = ((children != null) && (index < children.size()));
        } else {
            contains = structure.containsKey(simple);
        }

        return contains;
    }


    @Override
    public boolean hasValue(@Nonnull DocumentKey key) {
        return (getOptional(key, Object.class) != null);
    }

    @Override
    public boolean isSequence(@Nonnull DocumentKey key) {
        return (getOptional(key, Object.class) instanceof List);
    }


    @Nonnull
    public <T> T accept(@Nonnull DocumentVisitor<T> visitor) {
        if (visitorContext == null) {                       // Race conditions are not a problem -  VisitorContext
            visitorContext = new VisitorContextImpl();      // for a given Document are freely exchangeable
        }

        visitor = visitor.initialise(visitorContext);

        return accept(this, visitor, null).process();
    }

    @Nonnull
    private <T> DocumentVisitor<T> accept(@Nonnull AbstractDocument<?> target,
                                          @Nonnull DocumentVisitor<T> visitor,
                                          @Nullable VisitorKeyImpl key) {
        for (var entry: target.getStructure().entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();

            visitor = acceptValue(visitor, new VisitorKeyImpl(key, name), value);

            if (visitor.isComplete()) {
                break;
            }
        }

        return visitor;
    }

    @Nonnull
    private <T> DocumentVisitor<T> acceptValue(@Nonnull DocumentVisitor<T> visitor,
                                               @Nonnull VisitorKeyImpl key,
                                               @Nullable Object value) {
        if (value == null) {
            visitor = visitor.nullValue(key);
        } else if (value instanceof String) {
            visitor = visitor.stringValue(key, (String) value);
        } else if (value instanceof Number) {
            visitor = visitor.numericValue(key, (Number) value);
        } else if (value instanceof Boolean) {
            visitor = visitor.booleanValue(key, (boolean) value);
        } else if (value instanceof Enum<?>) {
            visitor = visitor.enumValue(key, (Enum<?>) value);
        } else if (value instanceof AbstractDocument<?> child) {
            visitor = visitor.beginChild(key);
            visitor = accept(child.getWrapped(), visitor, key);
            visitor = visitor.endChild(key);
        } else if (value instanceof Sequence<?> sequence) {
            int index = 0;

            visitor = visitor.beginSequence(key, sequence.getType(), sequence.size());

            for (var element : sequence) {
                visitor = acceptValue(visitor, new VisitorKeyImpl(key, index++), element);

                if (visitor.isComplete()) {
                    break;
                }
            }

            visitor = visitor.endSequence(key);
        } else {
            throw new DocumentException("Internal Error: unexpected type %s", value.getClass());
        }

        return visitor;
    }

    // Suspend Checkstyle rule SuperCloneCheck for 10 lines: Instead of calling super clone, we are using the
    // constructor chain which will also create all the decorators in the correct order. We don't need to worry
    // about this class from being overridden as it's final
    @Nonnull
    @Override
    public Document clone() {
        AbstractDocument<?> backing = (AbstractDocument<?>) accept(new Copy());
        AbstractDocument<?> clone = constructor.apply(backing);
        clone.getImpl().setConstructor(constructor);

        return (Document) clone;
    }

    @Override
    public boolean equals(Object other) {
        boolean equal;

        if (this == other) {
            equal = true;
        } else if (other instanceof AbstractDocument<?> o) {
            equal = structure.equals(o.getStructure());
        } else {
            equal = false;
        }

        return equal;
    }

    @Override
    public int hashCode() {
        return structure.hashCode();
    }

    @Nonnull
    public String toString() {
        return this.accept(new JsonSerializer());
    }


    /**
     * Wrapper to {@link Convert#toList(Collection, Class)} with improved exceptions
     * @param key       Key of data to convert
     * @param data      values to convert
     * @param type      Desired type
     * @param <T>       Desired type
     * @return          {@code sequence} converted to a list of {@code type}
     * @throws DocumentException if the data could not be converted
     */
    @Nonnull
    private <T> List<T> toList(@Nonnull DocumentKey key, @Nonnull Collection<?> data, @Nonnull Class<T> type) {
        List<T> result;

        try {
            result = Convert.toList(data, type);
        } catch (ClassCastException | IllegalArgumentException e) {
            throw new DocumentException("Unexpected data at '" + key.externalise() + "'", e);
        }

        return result;
    }


    /**
     * Wrapper to {@link Convert#to(Object, Class)} with improved exceptions
     * @param key       Key of data to convert
     * @param data      value to convert
     * @param type      Desired type
     * @param <T>       Desired type
     * @return          {@code data} converted to {@code type}
     * @throws DocumentException if the data could not be converted
     */
    private <T> T to(@Nonnull DocumentKey key, @Nullable Object data, @Nonnull Class<T> type) throws DocumentException {
        T result;

        try {
            result = Convert.to(data, type);
        } catch (ClassCastException | IllegalArgumentException e) {
            throw new DocumentException("Unexpected data at '" + key.externalise() + "'", e);
        }

        return result;
    }
}
