package com.github.tymefly.common.document.visitor.white;

import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import com.github.tymefly.common.document.key.DocumentKey;

/**
 * Test that backs {@link WhiteList.Builder#forRegEx(DocumentKey, String)}
 */
class RegExItem extends WhiteItem {
    private final Pattern regEx;

    RegExItem(@Nonnull String regEx, boolean allowNull) {
        super(allowNull);

        this.regEx = Pattern.compile("^" + regEx + "$");
    }


    @Override
    boolean isValid(@Nonnull Object testValue) {
        return regEx.matcher(testValue.toString()).matches();
    }
}
