package com.github.tymefly.common.base.config;

import java.io.Serial;

import javax.annotation.Nonnull;


/**
 * Configuration exceptions
 */
public class ConfigException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 0x1L;

    /**
     * Constructor for a raw message
     * @param message       Human readable (raw) message
     */
    ConfigException(@Nonnull String message) {
        super(message);
    }


    /**
     * Constructor for a formatted message
     * @param message       formatted message string
     * @param args          formatting arguments
     * @see java.util.Formatter
     */
    ConfigException(@Nonnull String message, @Nonnull Object... args) {
        super(String.format(message, args));
    }


    /**
     * Constructor for a wrapped exception
     * @param message       Human readable (raw) message
     * @param cause         Wrapped exception
     */
    ConfigException(@Nonnull String message, @Nonnull Throwable cause) {
        super(message, cause);
    }
}

