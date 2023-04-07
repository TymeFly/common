package com.github.tymefly.common.document.visitor.util;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.github.tymefly.common.document.CommonDocument;
import com.github.tymefly.common.document.Document;
import com.github.tymefly.common.document.DocumentException;
import com.github.tymefly.common.document.visitor.DocumentVisitor;
import com.github.tymefly.common.document.visitor.VisitorKey;

/**
 * Visitor that will create a deep copy of a Document
 */
@NotThreadSafe
abstract sealed class AbstractMergeVisitor implements DocumentVisitor<Document> permits Copy, Merge {
    private final Document result;
    private Class<?> type = String.class;

    /**
     * Constructor
     * @param results   Generated Document that may contain default values that this visitor can overwrite
     */
    AbstractMergeVisitor(@Nonnull Document results) {
        this.result = results;
    }

    @Nonnull
    @Override
    public DocumentVisitor<Document> nullValue(@Nonnull VisitorKey key) {
        if (type == String.class) {
            result.addString(key.documentKey(), null);
        } else if (type == Number.class) {
            result.addNumber(key.documentKey(), null);
        } else if (type == Boolean.class) {
            result.addBoolean(key.documentKey(), null);
        } else if (type == Enum.class) {
            result.addEnum(key.documentKey(), null);
        } else if (type == CommonDocument.class) {
            result.addDocument(key.documentKey(), null);
        } else {
            throw new DocumentException("INTERNAL ERROR: Unexpected type %s", type.getSimpleName());
        }

        return this;
    }

    @Nonnull
    @Override
    public DocumentVisitor<Document> stringValue(@Nonnull VisitorKey key, @Nonnull String value) {
        result.addString(key.documentKey(), value);

        return this;
    }

    @Nonnull
    @Override
    public DocumentVisitor<Document> numericValue(@Nonnull VisitorKey key, @Nonnull Number value) {
        result.addNumber(key.documentKey(), value);

        return this;
    }

    @Nonnull
    @Override
    public DocumentVisitor<Document> booleanValue(@Nonnull VisitorKey key, boolean value) {
        result.addBoolean(key.documentKey(), value);

        return this;
    }

    @Nonnull
    @Override
    public DocumentVisitor<Document> enumValue(@Nonnull VisitorKey key, @Nonnull Enum<?> value) {
        result.addEnum(key.documentKey(), value);

        return this;
    }

    @Nonnull
    @Override
    public DocumentVisitor<Document> beginChild(@Nonnull VisitorKey key) {
        result.addDocument(key.documentKey(), Document.newInstance());

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
        this.type = type;

        if (type == String.class) {
            result.addStrings(key.documentKey());
        } else if (type == Number.class) {
            result.addNumbers(key.documentKey());
        } else if (type == Boolean.class) {
            result.addBooleans(key.documentKey());
        } else if (type == Enum.class) {
            result.addEnums(key.documentKey());
        } else if (type == CommonDocument.class) {
            result.addDocuments(key.documentKey());
        } else {
            throw new DocumentException("INTERNAL ERROR: Unexpected type %s", type.getSimpleName());
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
}
