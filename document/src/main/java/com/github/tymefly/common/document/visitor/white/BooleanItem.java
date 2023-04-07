package com.github.tymefly.common.document.visitor.white;

import javax.annotation.Nonnull;

import com.github.tymefly.common.base.utils.Convert;
import com.github.tymefly.common.document.key.DocumentKey;

/**
 * Test that backs {@link WhiteList.Builder#forBoolean(DocumentKey)}
 */
class BooleanItem extends WhiteItem {
    BooleanItem(boolean allowNull) {
        super(allowNull);
    }

    @Override
    boolean isValid(@Nonnull Object testValue) {
        return (Convert.toBoolean(testValue) != null);
    }
}
