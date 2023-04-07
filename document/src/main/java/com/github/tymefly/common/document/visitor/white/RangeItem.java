package com.github.tymefly.common.document.visitor.white;

import javax.annotation.Nonnull;

import com.github.tymefly.common.document.key.DocumentKey;

/**
 * Test that backs {@link WhiteList.Builder#forRange(DocumentKey, long, long)}
 */
class RangeItem extends WhiteItem {
    private final long minimum;
    private final long maximum;


    RangeItem(long minimum, long maximum, boolean allowNull) {
        super(allowNull);

        this.minimum = minimum;
        this.maximum = maximum;
    }


    @Override
    boolean isValid(@Nonnull Object testValue) {
        long value;

        if (testValue instanceof Number) {
            value = ((Number) testValue).longValue();
        } else {
            value = Long.parseLong(testValue.toString());
        }

        return (value >= minimum) && (value <= maximum);
    }
}
