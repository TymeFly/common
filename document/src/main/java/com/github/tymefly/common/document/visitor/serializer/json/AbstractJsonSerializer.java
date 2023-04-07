package com.github.tymefly.common.document.visitor.serializer.json;

import java.math.BigDecimal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.tymefly.common.base.validate.Preconditions;
import com.github.tymefly.common.document.visitor.DocumentVisitor;
import com.github.tymefly.common.document.visitor.VisitorKey;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


/**
 * Base class for Json serializers
 */
abstract sealed class AbstractJsonSerializer
        implements DocumentVisitor<String>
        permits JsonSerializer, PrettyJsonSerializer {
    /**
     * Force Gson to serialize BigDecimals without an exponent.
     */
    private static class NumericWrapper extends Number {
        private final BigDecimal value;

        private NumericWrapper(double value) {
            this(BigDecimal.valueOf(value));
        }

        private NumericWrapper(@Nonnull BigDecimal value) {
            this.value = value;
        }

        @Override
        public int intValue() {
            return value.intValue();
        }

        @Override
        public long longValue() {
            return value.longValue();
        }

        @Override
        public float floatValue() {
            return value.floatValue();
        }

        @Override
        public double doubleValue() {
            return value.doubleValue();
        }

        @Override
        public String toString() {
            return value.toPlainString();
        }
    }

    private final AbstractJsonSerializer parent;
    private final JsonObject root = new JsonObject();
    private JsonArray sequence;


    AbstractJsonSerializer(@Nullable AbstractJsonSerializer parent) {
        this.parent = parent;
    }


    /**
     * Construct a child Json serializer
     * @return a child Json serializer
     */
    abstract AbstractJsonSerializer construct();


    @Nonnull
    @Override
    public DocumentVisitor<String> nullValue(@Nonnull VisitorKey key) {
        if (sequence == null) {
            root.addProperty(key.simpleKey(), (String) null);
        } else {
            sequence.add((String) null);
        }

        return this;
    }

    @Nonnull
    @Override
    public DocumentVisitor<String> stringValue(@Nonnull VisitorKey key, @Nonnull String value) {
        if (sequence == null) {
            root.addProperty(key.simpleKey(), value);
        } else {
            sequence.add(value);
        }

        return this;
    }

    @Nonnull
    @Override
    public DocumentVisitor<String> numericValue(@Nonnull VisitorKey key, @Nonnull Number value) {
        Class<? extends Number> type = value.getClass();

        if ((type == Float.class) || (type == Double.class)) {
            value = new NumericWrapper(value.doubleValue());
        } else if (value instanceof BigDecimal) {
            value = new NumericWrapper((BigDecimal) value);
        } else {
            // Do nothing - the value can be serialized in its current format
        }

        if (sequence == null) {
            root.addProperty(key.simpleKey(), value);
        } else {
            sequence.add(value);
        }

        return this;
    }

    @Nonnull
    @Override
    public DocumentVisitor<String> booleanValue(@Nonnull VisitorKey key, boolean value) {
        if (sequence == null) {
            root.addProperty(key.simpleKey(), value);
        } else {
            sequence.add(value);
        }

        return this;
    }

    @Nonnull
    @Override
    public DocumentVisitor<String> enumValue(@Nonnull VisitorKey key, @Nonnull Enum<?> value) {
        return stringValue(key, value.toString());
    }

    @Nonnull
    @Override
    public DocumentVisitor<String> beginChild(@Nonnull VisitorKey key) {
        AbstractJsonSerializer child = construct();

        if (sequence == null) {
            root.add(key.simpleKey(), child.root);
        } else {
            sequence.add(child.root);
        }

        return child;
    }

    @Nonnull
    @Override
    public DocumentVisitor<String> endChild(@Nonnull VisitorKey key) {
        return Preconditions.checkNotNull(parent, "INTERNAL ERROR: terminated child with no parent");
    }

    @Nonnull
    @Override
    public DocumentVisitor<String> beginSequence(@Nonnull VisitorKey key, @Nonnull Class<?> type, int size) {
        sequence = new JsonArray();
        root.add(key.simpleKey(), sequence);

        return this;
    }

    @Nonnull
    @Override
    public DocumentVisitor<String> endSequence(@Nonnull VisitorKey key) {
        sequence = null;

        return this;
    }

    @Nonnull
    @Override
    public String process() {
        GsonBuilder builder = new GsonBuilder().serializeNulls();
        Gson gson = configureBuilder(builder).create();
        String json = gson.toJson(root);

        return json;
    }

    @Nonnull
    GsonBuilder configureBuilder(@Nonnull GsonBuilder builder) {
        return builder;
    }
}
