package com.github.tymefly.common.document.key;

import java.util.function.Predicate;

import javax.annotation.Nonnull;

import com.github.tymefly.common.document.DocumentException;

/**
 * Generated Document key with helpful {@link #toString()} implementation
 */
class ConstructedKey implements DocumentKey {
    private static final Predicate<String> VALIDATE = FULL_PATH_PATTERN.asMatchPredicate();
    private final String external;

    /**
     * Constructor
     * @param external              external form of the key
     * @throws DocumentException    if {@code external} is not a valid {@link #FULL_PATH_PATTERN}
     */
    ConstructedKey(@Nonnull String external) throws DocumentException {
        if (!VALIDATE.test(external)) {
            throw new DocumentException("Malformed key '%s'", external);
        }

        this.external = external;
    }


    @Nonnull
    @Override
    public String externalise() {
        return external;
    }


    @Override
    public String toString() {
        return "ConstructedKey{" + external + '}';
    }
}
