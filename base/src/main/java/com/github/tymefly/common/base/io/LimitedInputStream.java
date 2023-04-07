package com.github.tymefly.common.base.io;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nonnull;

/**
 * A Decorator for InputStream classes that will limit the amount of data read.
 * There is a general safety problem that can occur if the application is needs to read data from an untrusted
 * external source. A malicious or badly implemented source might break the JVM by supplying a never ending
 * stream of data that will eventually use up all the resources available to the application.
 * This InputStream will combat this by limiting the amount of data the backing InputStream can read.
 * If too much data is read then a {@link FailedIoException} is thrown.
 * {@link #available()} will return the number of bytes available before this exception is thrown.
 * This class does not support marks, so {@link #markSupported()} will always return {@literal false}
 * @see FailedIoException
 */
public class LimitedInputStream extends InputStream {
    /** The default amount of data (in bytes) that this reader will accept */
    public static final long DEFAULT_LIMIT = 512 * 1024;

    private final InputStream backing;
    private long allowed;

    /**
     * Construct a new LimitedInputStream with a default limit
     * @param backing       the backing reader
     * @see LimitedInputStream#DEFAULT_LIMIT
     */
    public LimitedInputStream(@Nonnull InputStream backing) {
        this(backing, LimitedInputStream.DEFAULT_LIMIT);
    }

    /**
     * Construct a new LimitedInputStream with a specific data limit
     * @param backing       the backing reader
     * @param limit         data limit in bytes
     */
    public LimitedInputStream(@Nonnull InputStream backing, long limit) {
        this.backing = backing;
        this.allowed = limit;
    }


    @Override
    public int available() throws IOException {
        return (int) Math.min(allowed, backing.available());
    }


    @Override
    public int read() throws IOException {
        int data = backing.read();

        if (data >= 0) {
            allowed--;

            if (allowed < 0) {
                throw new FailedIoException("Too much data has been read");
            }
        }

        return data;
    }


    @Override
    public void close() throws IOException {
        backing.close();
    }
}
