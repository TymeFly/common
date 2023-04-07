package com.github.tymefly.common.document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.tymefly.common.document.key.DocumentKey;
import com.github.tymefly.common.document.visitor.VisitorKey;

/**
 * The only implementation of {@link VisitorKey}.
 */
class VisitorKeyImpl implements VisitorKey {
    private static final int NO_INDEX = -1;

    private final VisitorKey parent;
    private final String simpleKey;
    private final int index;

    private String element = null;
    private String simpleKeyPath = null;
    private String fullPath = null;
    private DocumentKey key = null;


    /**
     * Create a new Key for the {@code simpleKey}. This key will not have an index
     * @param parent        Optional parent Key
     * @param simpleKey     Value that will be returned by {@link #simpleKey()}
     */
    VisitorKeyImpl(@Nullable VisitorKeyImpl parent, @Nonnull String simpleKey) {
        this.parent = parent;
        this.simpleKey = simpleKey;
        this.index = NO_INDEX;
    }

    /**
     * Create a new Key for the {@code index}.
     * @param simpleKey     Key that requires an index
     * @param index         0 based index
     */
    VisitorKeyImpl(@Nonnull VisitorKeyImpl simpleKey, int index) {
        this.parent = simpleKey.parent;
        this.simpleKey = simpleKey.simpleKey();
        this.index = index;
    }


    @Nonnull
    @Override
    public String simpleKey() {
        return simpleKey;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Nonnull
    @Override
    public String element() {
        if (element == null) {
            element = (index == NO_INDEX ? simpleKey : simpleKey + '[' + index + ']');
        }

        return element;
    }

    @Nonnull
    @Override
    public String simpleKeyPath() {
        if (simpleKeyPath != null) {
            // Use cached value
        } else if (parent == null) {
            simpleKeyPath = simpleKey();
        } else {
            simpleKeyPath = parent.fullPath() + "." + simpleKey();
        }

        return simpleKeyPath;
    }

    @Nonnull
    @Override
    public String fullPath() {
        if (fullPath != null) {
            // Use cached value
        } else if (parent == null) {
            fullPath = element();
        } else {
            fullPath = parent.fullPath() + "." + element();
        }

        return fullPath;
    }

    @Nonnull
    @Override
    public DocumentKey documentKey() {
        if (key == null) {
            String external = fullPath();
            key = () -> external;
        }

        return key;
    }

    @Override
    public String toString() {
        return "KeyImpl{" + fullPath() + '}';
    }
}
