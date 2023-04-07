package com.github.tymefly.common.document.visitor.util;

import java.math.BigDecimal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.github.tymefly.common.base.annotation.VisibleForTesting;
import com.github.tymefly.common.base.utils.BigDecimals;
import com.github.tymefly.common.base.utils.Convert;
import com.github.tymefly.common.base.utils.Enums;
import com.github.tymefly.common.document.ReadableDocument;
import com.github.tymefly.common.document.visitor.DocumentVisitor;
import com.github.tymefly.common.document.visitor.VisitorKey;

/**
 * A Visitor that compare two Documents to see if contain the same data after type conversion have been handled
 */
@NotThreadSafe
public class Equivalent implements DocumentVisitor<Boolean> {
    private final ReadableDocument other;
    private boolean equivalent = true;
    private int size = 0;

    /**
     * Constructor
     * @param other     Document that needs to be compared with the one that accepts this visitor
     */
    public Equivalent(@Nonnull ReadableDocument other) {
        this.other = other;
    }


    @Nonnull
    @Override
    public DocumentVisitor<Boolean> nullValue(@Nonnull VisitorKey key) {
        equivalent = check(key, null);
        size++;

        return this;
    }

    @Nonnull
    @Override
    public DocumentVisitor<Boolean> stringValue(@Nonnull VisitorKey key, @Nonnull String value) {
        equivalent = check(key, value);
        size++;

        return this;
    }

    @Nonnull
    @Override
    public DocumentVisitor<Boolean> numericValue(@Nonnull VisitorKey key, @Nonnull Number value) {
        equivalent = check(key, value);
        size++;

        return this;
    }

    @Nonnull
    @Override
    public DocumentVisitor<Boolean> booleanValue(@Nonnull VisitorKey key, boolean value) {
        equivalent = check(key, value);
        size++;

        return this;
    }

    @Nonnull
    @Override
    public DocumentVisitor<Boolean> enumValue(@Nonnull VisitorKey key, @Nonnull Enum<?> value) {
        equivalent = check(key, value);
        size++;

        return this;
    }

    @Nonnull
    @Override
    public DocumentVisitor<Boolean> beginChild(@Nonnull VisitorKey key) {
        return this;
    }

    @Nonnull
    @Override
    public DocumentVisitor<Boolean> endChild(@Nonnull VisitorKey key) {
        return this;
    }

    @Nonnull
    @Override
    public DocumentVisitor<Boolean> beginSequence(@Nonnull VisitorKey key, @Nonnull Class<?> type, int size) {
        int otherSize = other.getAll(key.documentKey(), Object.class).size();

        equivalent = (size == otherSize);

        return this;
    }

    @Nonnull
    @Override
    public DocumentVisitor<Boolean> endSequence(@Nonnull VisitorKey key) {
        return this;
    }

    @Override
    public boolean isComplete() {
        return !equivalent;         // Fail fast
    }

    @Nonnull
    @Override
    public Boolean process() {
        equivalent = equivalent && (other.accept(new Size()) == size);

        return equivalent;
    }


    private boolean check(@Nonnull VisitorKey key, @Nullable Object left) {
        Object right = other.getOptional(key.documentKey(), Object.class);

        return check(left, right);
    }


    @VisibleForTesting
    boolean check(@Nullable Object left, @Nullable Object right) {
        boolean same;

        if (left == null) {
            same = (right == null);
        } else if (right == null) {
            same = false;
        } else if ((left instanceof BigDecimal leftValue) && (right instanceof BigDecimal rightValue)) {
            same = (leftValue.compareTo(rightValue) == 0);
        } else if (left.getClass() == right.getClass()) {
            same = left.equals(right);
        } else if ((left instanceof Number) && (right instanceof String)) {
            same = check((Number) left, (String) right);
        } else if ((left instanceof String) && (right instanceof Number)) {
            same = check((Number) right, (String) left);
        } else if ((left instanceof Enum) && (right instanceof String)) {
            same = check(left, Enums.safeToEnum(((Enum<?>) left).getClass(), (String) right));
        } else if ((left instanceof String) && (right instanceof Enum)) {
            same = check(Enums.safeToEnum(((Enum<?>) right).getClass(), (String) left), right);
        } else if ((left instanceof Boolean) && (right instanceof String)) {
            same = check((boolean) left, (String) right);
        } else if ((left instanceof String) && (right instanceof Boolean)) {
            same = check((boolean) right, (String) left);
        } else if ((left instanceof Boolean) && (right instanceof Enum)) {
            same = check((boolean) left, right.toString());
        } else if ((left instanceof Enum) && (right instanceof Boolean)) {
            same = check((boolean) right, left.toString());
        } else if ((left instanceof Boolean) && (right instanceof Number)) {
            same = check(((boolean) left ? 1 : 0), right);
        } else if ((left instanceof Number) && (right instanceof Boolean)) {
            same = check(left, ((boolean) right ? 1: 0));
        } else if ((left instanceof Number) && (right instanceof Number)) {     // Should not happen
            BigDecimal leftValue = BigDecimals.toBigDecimal((Number) left);
            BigDecimal rightValue = BigDecimals.toBigDecimal((Number) right);

            same = check(leftValue, rightValue);
        } else {
            same = false;
        }

        return same;
    }

    private boolean check(boolean left, @Nonnull String right) {
        Boolean asBool = Convert.toBoolean(right);
        boolean same = (asBool != null) && (asBool == left);

        return same;
    }

    private boolean check(@Nonnull Number left, @Nonnull String right) {
        boolean same;

        try {
            BigDecimal rightValue = new BigDecimal(right.trim());

            same = check(left, rightValue);
        } catch (NumberFormatException e) {
            same = false;
        }

        return same;
    }
}
