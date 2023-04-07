package com.github.tymefly.common.document.visitor.white;

import javax.annotation.Nonnull;

/**
 * Base class for all Whitelist tests
 */
abstract class WhiteItem {
    private final boolean allowNull;

    /**
     * Constructor
      * @param allowNull        {@code true} only if {@literal null} is considered a valid value
     */
    protected WhiteItem(boolean allowNull) {
        this.allowNull = allowNull;
    }


    /**
     * The testValue implemented by the extending class
     * @param testValue value to test
     * @return          {@literal true} only if {@code testValue} is considered valid
     */
    abstract boolean isValid(@Nonnull Object testValue);


    /**
     * Called by {@link WhiteList} to validate a some data
     * @param testValue value to test
     * @return          {@literal true} only if {@code testValue} is considered valid
     */
    boolean validate(@Nonnull Object testValue) {
        boolean valid;

        try {
            valid = isValid(testValue);
        } catch (RuntimeException e) {
            valid = false;
        }

        return valid;
    }


    /**
     * Returns {@literal true} only if {@literal null} is a valid value for this test
     * @return {@literal true} only if {@literal null} is a valid value for this test
     */
    boolean allowNull() {
        return allowNull;
    }
}
