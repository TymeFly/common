package com.github.tymefly.common.document.visitor.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;

import com.github.tymefly.common.document.CommonDocument;
import com.github.tymefly.common.document.Document;
import com.github.tymefly.common.document.DocumentException;
import com.github.tymefly.common.document.key.DocumentKey;
import com.github.tymefly.common.document.visitor.DocumentVisitor;
import com.github.tymefly.common.document.visitor.VisitorKey;

/**
 * A visitor that creates a new document with modified keys, values or structure.
 */
@NotThreadSafe
public class Transformer implements DocumentVisitor<Document> {
    /**
     * A builder class that creates a {@link Transformer} DocumentVisitor.
     * As transformers are typically reused, but as Transformer objects can not be then normal typical usage is to
     * configure a Builder once and then create a new instances of the Transformer for each Document
     */
    @ThreadSafe
    public static class Builder {
        private final Set<String> removeKeys;                                   // Set ensures no duplicates
        private final Map<String, Function<DocumentKey, DocumentKey>> remap;
        private boolean keepNulls;
        private BiFunction<DocumentKey, String, String> stringRemap = null;
        private BiFunction<DocumentKey, Number, Number> numberRemap = null;
        private BiFunction<DocumentKey, Boolean, Boolean> booleanRemap = null;
        private BiFunction<DocumentKey, Enum<?>, Enum<?>> enumRemap = null;


        /**
         * Constructor
         */
        public Builder() {
            this.removeKeys = new HashSet<>();
            this.remap = new LinkedHashMap<>();                                 // keep order
            this.keepNulls = true;
        }


        /**
         * Instructs the Transformer to remove sections of the source document
         * @param key   the section of the document that should be removed
         * @return a fluent interface
         */
        @Nonnull
        public Builder remove(@Nonnull DocumentKey key) {
            removeKeys.add(key.externalise());

            return this;
        }

        /**
         * Instructs the Transformer to remap Document keys.
         * @param key       Document key which can refer to an individual field, a sequence or a child
         *                      Document, in which case all child items are remapped.
         * @param remap     Function to remap keys
         * @return a fluent interface
         */
        @Nonnull
        public Builder remap(@Nonnull DocumentKey key, @Nonnull Function<DocumentKey, DocumentKey> remap) {
            this.remap.put(key.externalise(), remap);

            return this;
        }

        /**
         * Instructs the Transformer to remap all the non-null string values in the document.
         * This method can be called multiple times, in which case they will be evaluated in the order they are applied
         * @param remap     Function to remap string values
         * @return a fluent interface
         */
        @Nonnull
        public Builder remapStrings(@Nonnull BiFunction<DocumentKey, String, String> remap) {
            if (stringRemap == null) {
                stringRemap = remap;
            } else {
                BiFunction<DocumentKey, String, String> original = stringRemap;

                stringRemap = (k, v) -> remap.apply(k, original.apply(k, v));
            }

            return this;
        }

        /**
         * Instructs the Transformer to remap all the non-null numeric values in the document.
         * This method can be called multiple times, in which case they will be evaluated in the order they are applied
         * @param remap     Function to remap numeric values
         * @return a fluent interface
         */
        @Nonnull
        public Builder remapNumbers(@Nonnull BiFunction<DocumentKey, Number, Number> remap) {
            if (numberRemap == null) {
                numberRemap = remap;
            } else {
                BiFunction<DocumentKey, Number, Number> original = numberRemap;

                numberRemap = (k, v) -> remap.apply(k, original.apply(k, v));
            }

            return this;
        }

        /**
         * Instructs the Transformer to remap all the non-null boolean values in the document.
         * This method can be called multiple times, in which case they will be evaluated in the order they are applied
         * @param remap     Function to remap boolean values
         * @return a fluent interface
         */
        @Nonnull
        public Builder remapBooleans(@Nonnull BiFunction<DocumentKey, Boolean, Boolean> remap) {
            if (booleanRemap == null) {
                booleanRemap = remap;
            } else {
                BiFunction<DocumentKey, Boolean, Boolean> original = booleanRemap;

                booleanRemap = (k, v) -> remap.apply(k, original.apply(k, v));
            }

            return this;
        }

        /**
         * Instructs the Transformer to remap all the non-null enumeration constants in the document.
         * This method can be called multiple times, in which case they will be evaluated in the order they are applied
         * @param remap     Function to remap enumeration values
         * @return a fluent interface
         */
        @Nonnull
        public Builder remapEnums(@Nonnull BiFunction<DocumentKey, Enum<?>, Enum<?>> remap) {
            if (enumRemap == null) {
                enumRemap = remap;
            } else {
                BiFunction<DocumentKey, Enum<?>, Enum<?>> original = enumRemap;

                enumRemap = (k, v) -> remap.apply(k, original.apply(k, v));
            }

            return this;
        }

        /**
         * Instructs the Transformer to remove fields that are {@literal null}.
         * Sequences that have trailing {@literal null} elements will be truncated, but earlier {@literal null}s
         * will be retained to maintain indexes
         * @return a fluent interface
         */
        @Nonnull
        public Builder removeNullFields() {
            keepNulls = false;

            return this;
        }

        /**
         * Returns a fully configured Transformed visitor
         * @return a fully configured Transformed visitor
         */
        @Nonnull
        public Transformer build() {
            return new Transformer(this);
        }
    }

    private final Document result;
    private final List<String> removeKeys;
    private final Map<String, Function<DocumentKey, DocumentKey>> remap;
    private final BiFunction<DocumentKey, String, String> stringRemap;
    private final BiFunction<DocumentKey, Number, Number> numberRemap;
    private final BiFunction<DocumentKey, Boolean, Boolean> booleanRemap;
    private final BiFunction<DocumentKey, Enum<?>, Enum<?>> enumRemap;
    private final boolean keepNulls;
    private Class<?> type = String.class;


    private Transformer(@Nonnull Builder builder) {
        this.result = Document.newInstance();
        this.removeKeys = new ArrayList<>(builder.removeKeys);
        this.remap = new LinkedHashMap<>(builder.remap);
        this.stringRemap = (builder.stringRemap == null ? (k, v) -> v : builder.stringRemap);
        this.numberRemap = (builder.numberRemap == null ? (k, v) -> v : builder.numberRemap);
        this.booleanRemap = (builder.booleanRemap == null ? (k, v) -> v : builder.booleanRemap);
        this.enumRemap = (builder.enumRemap == null ? (k, v) -> v : builder.enumRemap);
        this.keepNulls = builder.keepNulls;
    }

    @Nonnull
    @Override
    public DocumentVisitor<Document> nullValue(@Nonnull VisitorKey key) {
        String canonicalKey = canonicalKey(key);

        if (keepNulls && include(canonicalKey)) {
            DocumentKey documentKey = keyRemap(canonicalKey, key);

            if (type == String.class) {
                result.addString(documentKey, null);
            } else if (type == Number.class) {
                result.addNumber(documentKey, null);
            } else if (type == Boolean.class) {
                result.addBoolean(documentKey, null);
            } else if (type == Enum.class) {
                result.addEnum(documentKey, null);
            } else if (type == CommonDocument.class) {
                result.addDocument(documentKey, null);
            } else {
                throw new DocumentException("INTERNAL ERROR: Unexpected type %s", type.getSimpleName());
            }
        }

        return this;
    }

    @Nonnull
    @Override
    public DocumentVisitor<Document> stringValue(@Nonnull VisitorKey key, @Nonnull String value) {
        String canonicalKey = canonicalKey(key);

        if (include(canonicalKey)) {
            DocumentKey documentKey = keyRemap(canonicalKey, key);

            value = stringRemap.apply(documentKey, value);
            result.addString(documentKey, value);
        }

        return this;
    }

    @Nonnull
    @Override
    public DocumentVisitor<Document> numericValue(@Nonnull VisitorKey key, @Nonnull Number value) {
        String canonicalKey = canonicalKey(key);

        if (include(canonicalKey)) {
            DocumentKey documentKey = keyRemap(canonicalKey, key);

            value = numberRemap.apply(documentKey, value);
            result.addNumber(documentKey, value);
        }

        return this;
    }

    @Nonnull
    @Override
    public DocumentVisitor<Document> booleanValue(@Nonnull VisitorKey key, boolean value) {
        String canonicalKey = canonicalKey(key);

        if (include(canonicalKey)) {
            DocumentKey documentKey = keyRemap(canonicalKey, key);

            value = booleanRemap.apply(documentKey, value);
            result.addBoolean(documentKey, value);
        }

        return this;
    }

    @Nonnull
    @Override
    public DocumentVisitor<Document> enumValue(@Nonnull VisitorKey key, @Nonnull Enum<?> value) {
        String canonicalKey = canonicalKey(key);

        if (include(canonicalKey)) {
            DocumentKey documentKey = keyRemap(canonicalKey, key);

            value = enumRemap.apply(documentKey, value);
            result.addEnum(documentKey, value);
        }

        return this;
    }

    @Nonnull
    @Override
    public DocumentVisitor<Document> beginChild(@Nonnull VisitorKey key) {
        String canonicalKey = canonicalKey(key);

        if (include(canonicalKey)) {
            DocumentKey documentKey = keyRemap(canonicalKey, key);

            result.addDocument(documentKey, Document.newInstance());
        }

        return this;
    }

    @Nonnull
    @Override
    public DocumentVisitor<Document> endChild(@Nonnull VisitorKey key) {
        return this;
    }

    @Nonnull
    @Override
    public DocumentVisitor<Document> beginSequence(@Nonnull VisitorKey key, @Nonnull Class<?> type, int size) {
        String canonicalKey = canonicalKey(key);

        this.type = type;

        if (include(canonicalKey)) {
            DocumentKey documentKey = keyRemap(canonicalKey, key);

            if (type == String.class) {
                result.addStrings(documentKey);
            } else if (type == Number.class) {
                result.addNumbers(documentKey);
            } else if (type == Boolean.class) {
                result.addBooleans(documentKey);
            } else if (type == Enum.class) {
                result.addEnums(documentKey);
            } else if (type == CommonDocument.class) {
                result.addDocuments(documentKey);
            } else {
                throw new DocumentException("INTERNAL ERROR: Unexpected type %s", type.getSimpleName());
            }
        }

        return this;
    }

    @Nonnull
    @Override
    public DocumentVisitor<Document> endSequence(@Nonnull VisitorKey key) {
        this.type = String.class;

        return this;
    }

    @Nonnull
    @Override
    public Document process() {
        return result;
    }

    private boolean include(@Nonnull String canonicalKey) {
        boolean include = true;

        for (var removeKey : removeKeys) {
            if (startsWith(canonicalKey, removeKey)) {
                include = false;
                break;
            }
        }

        return include;
    }


    @Nonnull
    private DocumentKey keyRemap(@Nonnull String canonicalKey, @Nonnull VisitorKey key) {
        DocumentKey result = null;

        for (var entry : remap.entrySet()) {
            String remapKey = entry.getKey();

            if (startsWith(canonicalKey, remapKey)) {
                result = entry.getValue().apply(key.documentKey());
                break;
            }
        }

        return (result == null ? key.documentKey() : result);
    }


    // Remove array indexes from the external form of the key.
    @Nonnull
    private String canonicalKey(@Nonnull VisitorKey key) {
        return key.fullPath().replaceAll("\\[\\d+]", "");
    }


    private boolean startsWith(@Nonnull String prefix, @Nonnull String fullKey) {
        boolean startsWith;

        if (!prefix.startsWith(fullKey)) {
            startsWith = false;
        } else if (prefix.length() == fullKey.length()) {       // prefix.equals(fullKey)
            startsWith = true;
        } else {                                                // need to make sure prefix is a complete key in fullKey
            startsWith = prefix.charAt(fullKey.length()) == DocumentKey.SEPARATOR;
        }

        return startsWith;
    }
}
