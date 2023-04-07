package com.github.tymefly.common.document.visitor.white;

import java.io.Serial;

import javax.annotation.Nonnull;

import com.github.tymefly.common.document.key.DocumentKey;

/**
 * Thrown when the {@link WhiteList} visitor notices that the data in a document isn't valid
 */
public class WhiteListException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 0x1L;

    private final DocumentKey badKey;

    WhiteListException(@Nonnull DocumentKey badKey) {
        super("Failed to validate key " + badKey.externalise());

        this.badKey = badKey;
    }

    /**
     * Returns the first DocumentKey that caused the WhiteList to fail.
     * @return the first DocumentKey that caused the WhiteList to fail
     */
    @Nonnull
    public DocumentKey getBadKey() {
        return badKey;
    }
}
