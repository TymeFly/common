package com.github.tymefly.common.document.visitor.white;

import javax.annotation.Nonnull;

import com.github.tymefly.common.document.key.DocumentKey;

/**
 * Test that backs {@link WhiteList.Builder#forLength(DocumentKey, int)}
 */
class StringLength extends WhiteItem {
    private final int minLength;
    private final int maxLength;

    StringLength(int minLength, int maxLength, boolean allowNull) {
        super(allowNull);

        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    @Override
    boolean isValid(@Nonnull Object testValue) {
        int length = testValue.toString().length();

        return ((length >= minLength) && (length <= maxLength));
    }
}
