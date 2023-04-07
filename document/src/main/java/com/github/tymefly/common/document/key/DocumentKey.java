package com.github.tymefly.common.document.key;

import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import com.github.tymefly.common.document.Document;

/**
 * Defines the contract for keys in a {@link Document}. Each data element in the Document
 * is referenced by a unique key. Each key has an external form, which is returned by the {@link #externalise()}
 * function.
 * Because Documents can be nested inside other Documents, a DocumentKey can reference data in the child by
 * using a {@link #SEPARATOR} in the external name. In a similar way data elements that are stored in a sequence
 * can be reference by appending a zero based {@link #INDEX} to the external name.
 */
@FunctionalInterface
public interface DocumentKey {
    /** A Regular expression that defines the external form of a simple key in the document */
    String SIMPLE_KEY = "[A-Za-z_$][A-Za-z0-9_$]*";

    /** A Regular expression that defines a single data item in index in a sequence */
    String INDEX = "\\[[0-9]+]";

    /**
     * A Regular expression that defines an element name in a single level key.
     * This is a {@link #SIMPLE_KEY} with an optional {@link #INDEX}
     */
    String ELEMENT = SIMPLE_KEY + "+(" + INDEX + ")?";

    /** A character that is used to separate elements in a multi-level key */
    char SEPARATOR = '.';

    /**
     * A Regular expression that defines the external form a DocumentKey.
     * This is one or more {@link #ELEMENT}s separated by the {@link #SEPARATOR} character.
     */
    String FULL_PATH = ELEMENT + "(\\" + SEPARATOR + ELEMENT + ")*";

    /** A pattern that matches the external form of any DocumentKey */
    Pattern FULL_PATH_PATTERN = Pattern.compile(FULL_PATH);



    /**
     * Factory method that generates a DocumentKey from a sequence of existing keys
     * @param first     The required first key in
     * @param others    Additional optional keys the generated DocumentKey
     * @return a new DocumentKey
     * @see DocumentKeyBuilder
     */
    static DocumentKey from(@Nonnull DocumentKey first, DocumentKey... others) {
        StringBuilder buffer = new StringBuilder(first.externalise());

        for (var other : others) {
            buffer.append(SEPARATOR).append(other.externalise());
        }

        return new ConstructedKey(buffer.toString());
    }


    /**
     * Returns the externalised form of this key. This must match {@link #FULL_PATH_PATTERN}
     * @return the externalised form of this key
     */
    @Nonnull
    String externalise();
}
