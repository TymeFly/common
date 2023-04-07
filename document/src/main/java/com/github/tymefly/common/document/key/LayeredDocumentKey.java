package com.github.tymefly.common.document.key;

import javax.annotation.Nonnull;

/**
 * An extension of a DocumentKey with a default implementation of the {@link DocumentKey#externalise()} method
 * that uses the lowercase {@link #name()} as the external key. The key can be multilayered; the underscore character
 * indicates where the breaks between the levels are.
 * This interface is expected to be implemented by an {@link Enum} type
 * @see FlatDocumentKey
 */
public interface LayeredDocumentKey extends DocumentKey {
    /**
     * Returns the name of this object. This is used to generate the external key
     * @return the name of this object. This is used to generate the external key
     */
    @Nonnull
    String name();


    @Nonnull
    @Override
    default String externalise() {
        return name().replace('_', DocumentKey.SEPARATOR).toLowerCase();
    }
}
