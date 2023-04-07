package com.github.tymefly.common.document.visitor.util;

import java.util.EnumSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.github.tymefly.common.document.visitor.DocumentVisitor;
import com.github.tymefly.common.document.visitor.VisitorKey;

/**
 * Base class used by classes that return keys based on {@link VisitorOptions}
 * @param <T>       Type of data returned by the implementing visitor
 */
@NotThreadSafe
abstract sealed class AbstractKeyVisitor<T>
        implements DocumentVisitor<T>
        permits EntrySet, KeySet, PathSet, Size {
    private final Set<VisitorOptions> options;
    private int level;
    private boolean reportNested;

    AbstractKeyVisitor(@Nonnull EnumSet<VisitorOptions> options) {
        this.options = options;
        this.level = 0;
        this.reportNested = true;
    }


    abstract void report(@Nonnull VisitorKey key, @Nullable Object value);


    @Nonnull
    @Override
    public AbstractKeyVisitor<T> nullValue(@Nonnull VisitorKey key) {
        if (reportNested && options.contains(VisitorOptions.INCLUDE_NULL)) {
            report(key, null);
        }

        return this;
    }

    @Nonnull
    @Override
    public AbstractKeyVisitor<T> stringValue(@Nonnull VisitorKey key, @Nonnull String value) {
        if (reportNested) {
            report(key, value);
        }

        return this;
    }

    @Nonnull
    @Override
    public AbstractKeyVisitor<T> numericValue(@Nonnull VisitorKey key, @Nonnull Number value) {
        if (reportNested) {
            report(key, value);
        }

        return this;
    }

    @Nonnull
    @Override
    public AbstractKeyVisitor<T> booleanValue(@Nonnull VisitorKey key, boolean value) {
        if (reportNested) {
            report(key, value);
        }

        return this;
    }

    @Nonnull
    @Override
    public AbstractKeyVisitor<T> enumValue(@Nonnull VisitorKey key, @Nonnull Enum<?> value) {
        if (reportNested) {
            report(key, value);
        }

        return this;
    }

    @Nonnull
    @Override
    public AbstractKeyVisitor<T> beginChild(@Nonnull VisitorKey key) {
        if (reportNested && options.contains(VisitorOptions.INCLUDE_CHILD_NAMES)) {
            report(key, null);
        }

        level++;
        reportNested = (options.contains(VisitorOptions.RECURSIVE));

        return this;
    }

    @Nonnull
    @Override
    public AbstractKeyVisitor<T> endChild(@Nonnull VisitorKey key) {
        level--;
        reportNested |= (level == 0);

        return this;
    }

    @Nonnull
    @Override
    public AbstractKeyVisitor<T> beginSequence(@Nonnull VisitorKey key, @Nonnull Class<?> type, int size) {
        if (reportNested && options.contains(VisitorOptions.INCLUDE_SEQUENCE_NAMES)) {
            report(key, null);
        }

        return this;
    }

    @Nonnull
    @Override
    public AbstractKeyVisitor<T> endSequence(@Nonnull VisitorKey key) {
        return this;
    }
}
