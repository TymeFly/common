package com.github.tymefly.common.document;

import javax.annotation.Nonnull;

import com.github.tymefly.common.base.validate.Preconditions;
import com.github.tymefly.common.document.key.DocumentKey;

/**
 * A DocumentKey that allows the client to inspect its external form on an {@link DocumentKey#ELEMENT} by
 * {@link DocumentKey#ELEMENT} basis
 */
class WalkerKey implements DocumentKey {
    private final DocumentKey original;

    private String external;        // external key version of the key after leading elements have been removed
    private int dot;                // Index of the dot in external before the next path element
    private String element;
    private String simple;
    private int index;

    private int offset;             // offset in original to the start of external
    private String elementPath;
    private String simplePath;


    private WalkerKey(@Nonnull DocumentKey key) {
        this.original = key;
        this.offset = 0;

        configure(validateKey(key.externalise()));
    }


    @Nonnull
    private String validateKey(@Nonnull String external) {
        Preconditions.checkNotNull(external, "Null key passed");

        if (!DocumentKey.FULL_PATH_PATTERN.matcher(external).matches()) {
            throw new DocumentException("Invalid key '%s'", external);
        }

        return external;
    }


    private void configure(@Nonnull String external) {
        this.external = external;
        this.dot = external.indexOf(DocumentKey.SEPARATOR);
        this.element = (dot == -1 ? external : external.substring(0, dot));

        if (!element.endsWith("]")) {
            this.index = -1;
            this.simple = element;
        } else {
            int index = element.indexOf("[");
            String val = element.substring(index + 1, element.length() - 1);

            this.index = Integer.parseInt(val);
            this.simple = element.substring(0, index);
        }

        elementPath = null;
        simplePath = null;
    }


    /**
     * Factory method for WalkerKeys
     * @param key   External key
     * @return      A WalkerKey
     */
    @Nonnull
    static WalkerKey from(@Nonnull DocumentKey key) {
        return (key instanceof WalkerKey ? (WalkerKey) key : new WalkerKey(key));
    }


    @Nonnull
    @Override
    public String externalise() {
        return external;
    }


    /**
     * Returns original DocumentKey used to generate this WalkerKey, including outer elements that have been removed
     * @return original DocumentKey used to generate this WalkerKey
     */
    @Nonnull
    DocumentKey fullKey() {
        return original;
    }

    /**
     * Returns the current {@link DocumentKey#ELEMENT} in this path
     * @return the current {@link DocumentKey#ELEMENT} in this path
     */
    @Nonnull
    String currentElement() {
        return element;
    }

    /**
     * Returns {@literal true} only of there are more {@link DocumentKey#ELEMENT} after the {@link #currentElement()}
     * @return {@literal true} only of there are more {@link DocumentKey#ELEMENT} after the {@link #currentElement()}
     */
    boolean hasChildren() {
        return (dot != -1);
    }

    /**
     * Returns the {@link DocumentKey#SIMPLE_KEY} of the {@link #currentElement()}
     * @return the {@link DocumentKey#SIMPLE_KEY} of the {@link #currentElement()}
     */
    @Nonnull
    String simpleKey() {
        return simple;
    }

    /**
     * Returns {@literal true} only if the {@link #currentElement()} is part of a sequence
     * @return {@literal true} only if the {@link #currentElement()} is part of a sequence
     */
    boolean hasIndex() {
        return (index != -1);
    }

    /**
     * Returns the 0 based {@link DocumentKey#INDEX} of the {@link #currentElement()}
     * @return the 0 based {@link DocumentKey#INDEX} of the {@link #currentElement()}
     */
    int index() {
        return index;
    }

    /**
     * Move the {@link #currentElement()} to the next {@link DocumentKey#ELEMENT} in the key
     * @return a fluent interface
     */
    @Nonnull
    WalkerKey shift() {
        Preconditions.checkState(hasChildren(), "No Child elements");

        offset += (dot + 1);
        configure(external.substring(dot + 1));

        return this;
    }

    /**
     * Returns the full external path from the outermost element to the current element.
     * @return the full external path from the outermost element to the current element.
     * @see #currentElement()
     */
    @Nonnull String elementPath() {
        if (elementPath == null ) {
            elementPath = original.externalise().substring(0, offset + element.length());
        }

        return elementPath;
    }

    /**
     * Returns the full external path from the outermost element to the {@link DocumentKey#SIMPLE_KEY}.
     * @return the full external path from the outermost element to the {@link DocumentKey#SIMPLE_KEY}.
     * @see #simpleKey()
     */
    @Nonnull String simplePath() {
        if (simplePath == null) {
            simplePath = original.externalise().substring(0, offset + simple.length());
        }

        return simplePath;
    }
}
