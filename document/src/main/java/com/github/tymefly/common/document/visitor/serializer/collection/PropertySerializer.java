package com.github.tymefly.common.document.visitor.serializer.collection;

import java.util.Properties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.github.tymefly.common.document.visitor.DocumentVisitor;
import com.github.tymefly.common.document.visitor.VisitorKey;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Serialise the contents of a Document into a {@link Properties} object
 * @see MapSerializer
 */
@NotThreadSafe
public class PropertySerializer implements DocumentVisitor<Properties> {
    private final Properties map;


    /** Constructor */
    public PropertySerializer() {
        this.map = new Properties();
    }

    @Nonnull
    @Override
    public PropertySerializer nullValue(@Nonnull VisitorKey key) {
        return store(key, null);
    }

    @Nonnull
    @Override
    public PropertySerializer stringValue(@Nonnull VisitorKey key, @Nonnull String value) {
        return store(key, value);
    }

    @Nonnull
    @Override
    public PropertySerializer numericValue(@Nonnull VisitorKey key, @Nonnull Number value) {
        return store(key, value);
    }

    @Nonnull
    @Override
    public PropertySerializer booleanValue(@Nonnull VisitorKey key, boolean value) {
        return store(key, value);
    }

    @Nonnull
    @Override
    public PropertySerializer enumValue(@Nonnull VisitorKey key, @Nonnull Enum<?> value) {
        return store(key, value);
    }

    @Nonnull
    @Override
    public PropertySerializer beginChild(@Nonnull VisitorKey key) {
        return this;
    }

    @Nonnull
    @Override
    public PropertySerializer endChild(@Nonnull VisitorKey key) {
        return this;
    }

    @Nonnull
    @Override
    public PropertySerializer beginSequence(@Nonnull VisitorKey key, @Nonnull Class<?> type, int size) {
        return this;
    }

    @Nonnull
    @Override
    public PropertySerializer endSequence(@Nonnull VisitorKey key) {
        return this;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP",
        justification = "The purpose of this class is to generate the properties - this class should be discarded")
    @Nonnull
    @Override
    public Properties process() {
        return map;
    }

    @Nonnull
    private PropertySerializer store(@Nonnull VisitorKey name, @Nullable Object value) {
        map.put(name.fullPath(), (value == null ? "" : value.toString()));

        return this;
    }
}
