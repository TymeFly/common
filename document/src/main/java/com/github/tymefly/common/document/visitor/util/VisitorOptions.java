package com.github.tymefly.common.document.visitor.util;

/**
 * Options used to customise the {@link KeySet}, {@link PathSet} and {@link Size} Visitor objects.
 */
public enum VisitorOptions {
    /** Used to include fields from child Documents. */
    RECURSIVE,

    /** Used to include fields that are assigned {@literal null}. */
    INCLUDE_NULL,

    /** Used to include the name of child Documents. */
    INCLUDE_CHILD_NAMES,

    /** Used to include the name of sequences */
    INCLUDE_SEQUENCE_NAMES
}
