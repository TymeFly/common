package com.github.tymefly.common.document.visitor.serializer.collection;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.github.tymefly.common.document.visitor.DocumentVisitor;
import com.github.tymefly.common.document.visitor.VisitorKey;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Serialise the contents of a Document into key-value pairs and store them in a {@link Map}
 * @param <T> Type of the data stored in the generated map
 * @see PropertySerializer
 */
@NotThreadSafe
public class MapSerializer<T> implements DocumentVisitor<Map<String, T>> {
    private final Map<String, T> map;
    private final Function<Object, T> externalise;


    /**
     * Constructor called only by factory methods
     * @param externalise   externalisation function
     */
    private MapSerializer(@Nonnull Function<Object, T> externalise) {
        this.map = new LinkedHashMap<>();
        this.externalise = externalise;
    }


    /**
     * Returns a MapSerializer where are values are expressed as Strings
     * @return a MapSerializer where are values are expressed as Strings
     */
    @Nonnull
    public static MapSerializer<String> asString() {
        return map(Object::toString);
    }


    /**
     * Returns a MapSerializer where are values are expressed in their original type
     * @return a MapSerializer where are values are expressed in their original type
     */
    @Nonnull
    public static MapSerializer<Object> raw() {
        return map(Function.identity());
    }


    /**
     * Returns a MapSerializer where are values are expressed in a form given by the {code externalise}
     * function
     * @param externalise   function used to externalise values from the Document
     * @return a MapSerializer where are values are expressed in a form given by the {code externalise}
     * function
     * @param <T> Type of the data stored in the generated map
     */
    @Nonnull
    public static <T> MapSerializer<T> map(@Nonnull Function<Object, T> externalise) {
        return new MapSerializer<>(externalise);
    }


    @Nonnull
    @Override
    public MapSerializer<T> nullValue(@Nonnull VisitorKey key) {
        return store(key, null);
    }

    @Nonnull
    @Override
    public MapSerializer<T> stringValue(@Nonnull VisitorKey key, @Nonnull String value) {
        return store(key, value);
    }

    @Nonnull
    @Override
    public MapSerializer<T> numericValue(@Nonnull VisitorKey key, @Nonnull Number value) {
        return store(key, value);
    }

    @Nonnull
    @Override
    public MapSerializer<T> booleanValue(@Nonnull VisitorKey key, boolean value) {
        return store(key, value);
    }

    @Nonnull
    @Override
    public MapSerializer<T> enumValue(@Nonnull VisitorKey key, @Nonnull Enum<?> value) {
        return store(key, value);
    }

    @Nonnull
    @Override
    public MapSerializer<T> beginChild(@Nonnull VisitorKey key) {
        return this;
    }

    @Nonnull
    @Override
    public MapSerializer<T> endChild(@Nonnull VisitorKey key) {
        return this;
    }

    @Nonnull
    @Override
    public MapSerializer<T> beginSequence(@Nonnull VisitorKey key, @Nonnull Class<?> type, int size) {
        return this;
    }

    @Nonnull
    @Override
    public MapSerializer<T> endSequence(@Nonnull VisitorKey key) {
        return this;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP",
        justification = "The purpose of this class is to generate the map - this class should be discarded")
    @Nonnull
    @Override
    public Map<String, T> process() {
        return map;
    }

    @Nonnull
    private MapSerializer<T> store(@Nonnull VisitorKey name, @Nullable Object value) {
        map.put(name.fullPath(), (value == null ? null : externalise.apply(value)));

        return this;
    }
}
