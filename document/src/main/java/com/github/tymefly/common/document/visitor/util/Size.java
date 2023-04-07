package com.github.tymefly.common.document.visitor.util;

import java.util.EnumSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.github.tymefly.common.document.visitor.VisitorKey;

/**
 * A visitor that returns the number of data elements in a Document.
 */
@NotThreadSafe
public non-sealed class Size extends AbstractKeyVisitor<Integer> {
    private int size = 0;

    /**
     * A constructor for a Size visitor that counts all the values in the Document.
     * This includes fields that map to {@literal null} and fields that are in child Documents.
     * It does not include the sequences names or child Document names.
     */
    public Size() {
        this(EnumSet.of(VisitorOptions.RECURSIVE, VisitorOptions.INCLUDE_NULL));
    }

    /**
     * A customised version of the {@link Size} Visitor
     * @param option        One of the options that determine which keys to retrieve
     */
    public Size(@Nonnull VisitorOptions option) {
        this(EnumSet.of(option));
    }


    /**
     * A customised version of the {@link Size} Visitor
     * @param options       Options that determine which keys to retrieve
     */
    public Size(@Nonnull EnumSet<VisitorOptions> options) {
        super(options);
    }


    @Override
    void report(@Nonnull VisitorKey key, @Nullable Object value) {
        size++;
    }


    @Nonnull
    @Override
    public Integer process() {
        return size;
    }
}
