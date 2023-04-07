package com.github.tymefly.common.document.key;

import javax.annotation.Nonnull;

/**
 * A Builder for creating DocumentKey instances from other DocumentKeys or their external forms.
 * @see DocumentKey#from(DocumentKey, DocumentKey...) 
 */
public class DocumentKeyBuilder {
    private static final String SEPARATOR_STRING = "" + DocumentKey.SEPARATOR;

    private final StringBuilder buffer = new StringBuilder();
    private String separator = "";


    /**
     * Append a new key to the generated DocumentKey.
     * @param key       externalised view of the element to append
     * @return          a fluent interface
     */
    @Nonnull
    public DocumentKeyBuilder append(@Nonnull DocumentKey key) {
        return append(key.externalise());
    }


    /**
     * Append a new element to the generated DocumentKey.
     * @param externalised  externalised view of the element to append
     * @return              a fluent interface
     */
    @Nonnull
    public DocumentKeyBuilder append(@Nonnull String externalised) {
        buffer.append(separator).append(externalised);
        separator = SEPARATOR_STRING;

        return this;
    }


    /**
     * Append an index to the generated DocumentKey. The last value added should match {@link DocumentKey#SIMPLE_KEY}
     * @param index     a zero based index
     * @return          a fluent interface
     */
    @Nonnull
    public DocumentKeyBuilder append(int index) {
        buffer.append('[').append(index).append(']');

        return this;
    }


    /**
     * Returns a new DocumentKey
     * @return a new DocumentKey
     */
    @Nonnull
    public DocumentKey build() {
        return new ConstructedKey(buffer.toString());
    }
}
