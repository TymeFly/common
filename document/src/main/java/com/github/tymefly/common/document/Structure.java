package com.github.tymefly.common.document;

import java.util.LinkedHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * Holds the data in a Document.
 * There is little special functionality over a Map, but it makes the code much more readable
 */
class Structure extends LinkedHashMap<String, Object> {
    /**
     * A fluent alternative to {@link #put(Object, Object)}
     * @param key   key with which the specified value is to be associated
     * @param value to be associated with the specified key
     * @return a fluent interface
     */
    @Nonnull
    Structure add(@Nonnull String key, @Nullable Object value) {
        put(key, value);

        return this;
    }
}
