package com.github.tymefly.common.document.visitor.white;

import java.util.function.Predicate;

import javax.annotation.Nonnull;

import com.github.tymefly.common.document.key.DocumentKey;

/**
 * Test that backs {@link WhiteList.Builder#forCheck(DocumentKey, Predicate)}
 */
class CheckItem extends WhiteItem {
    private final Predicate<? super Object> expression;

    CheckItem(@Nonnull Predicate<? super Object> expression, boolean allowNull) {
        super(allowNull);

        this.expression = expression;
    }

    @Override
    boolean isValid(@Nonnull Object testValue) {
        return expression.test(testValue);
    }
}
