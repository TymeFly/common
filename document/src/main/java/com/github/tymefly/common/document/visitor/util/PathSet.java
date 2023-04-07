package com.github.tymefly.common.document.visitor.util;

import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.github.tymefly.common.document.visitor.VisitorKey;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A visitor that calculates the externalised form of the keys that are stored in a Document.
 * @see KeySet
 * @see EntrySet
 */
@NotThreadSafe
public non-sealed class PathSet extends AbstractKeyVisitor<Set<String>> {
    private final Set<String> results = new LinkedHashSet<>();

    /**
     * A constructor for an {@link PathSet} visitor which will return all the keys in the Document.
     * This includes fields that map to {@literal null} and fields that are in child Documents.
     * It does not include the sequences names or child Document names.
     */
    public PathSet() {
        this(VisitorOptions.RECURSIVE, VisitorOptions.INCLUDE_NULL);
    }

    /**
     * A customised version of the {@link PathSet} Visitor
     * @param option        One of the options that determine which keys to retrieve
     */
    public PathSet(@Nonnull VisitorOptions option) {
        this(EnumSet.of(option));
    }

    /**
     * A customised version of the {@link PathSet} Visitor
     * @param option        First option that determines which keys to retrieve
     * @param options       additional options
     */
    public PathSet(@Nonnull VisitorOptions option, VisitorOptions... options) {
        this(EnumSet.of(option, options));
    }

    /**
     * A customised version of the {@link PathSet} Visitor
     * @param options       Options that determine which keys to retrieve
     */
    public PathSet(@Nonnull EnumSet<VisitorOptions> options) {
        super(options);
    }


    @Override
    void report(@Nonnull VisitorKey key, @Nullable Object value) {
        results.add(key.fullPath());
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP",
        justification = "The purpose of this class is to generate the set - this class should be discarded")
    @Nonnull
    @Override
    public Set<String> process() {
        return results;
    }
}
