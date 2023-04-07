package com.github.tymefly.common.document.decorator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.tymefly.common.document.AbstractDocument;
import com.github.tymefly.common.document.CommonDocument;
import com.github.tymefly.common.document.Document;
import com.github.tymefly.common.document.DocumentDecorator;
import com.github.tymefly.common.document.key.DocumentKey;

/**
 * DocumentDecorator that stops {@literal null} values from being added.
 * If the data is a VarArg or an ordered collection removing {@literal null} values might cause the
 * indexes of elements to change.
 * <br/>
 * <b>Note:</b> This Document could contain {@literal null} values if they are added to another Document that was
 * created without this decorator and that document is inserted into this document
 */
public class NullFilterDocument extends DocumentDecorator<Document> implements Document {
    /**
     * Constructor
     * @param wrapped   The underlying object that this class decorates
     */
    public NullFilterDocument(@Nonnull AbstractDocument<?> wrapped) {
        super(wrapped);
    }

    @Nonnull
    @Override
    public Document addString(@Nonnull DocumentKey key, @Nullable String value) {
        if (value != null) {
            getWrapped().addString(key, value);
        }

        return this;
    }

    @Nonnull
    @Override
    public Document addStrings(@Nonnull DocumentKey key, String... values) {
        getWrapped().addStrings(key, filter(values));

        return this;
    }

    @Nonnull
    @Override
    public Document addStrings(@Nonnull DocumentKey key, @Nonnull Collection<String> values) {
        getWrapped().addStrings(key, filter(values));

        return this;
    }

    @Nonnull
    @Override
    public Document appendString(@Nonnull DocumentKey key, @Nullable String value) {
        if (value != null) {
            getWrapped().appendString(key, value);
        }

        return this;
    }

    @Nonnull
    @Override
    public Document addNumber(@Nonnull DocumentKey key, @Nullable Number value) {
        if (value != null) {
            getWrapped().addNumber(key, value);
        }

        return this;
    }

    @Nonnull
    @Override
    public Document addNumbers(@Nonnull DocumentKey key, Number... values) {
        getWrapped().addNumbers(key, filter(values));

        return this;
    }

    @Nonnull
    @Override
    public Document addNumbers(@Nonnull DocumentKey key, @Nonnull Collection<Number> values) {
        getWrapped().addNumbers(key, filter(values));

        return this;
    }

    @Nonnull
    @Override
    public Document appendNumber(@Nonnull DocumentKey key, @Nullable Number value) {
        if (value != null) {
            getWrapped().appendNumber(key, value);
        }

        return this;
    }

    @Nonnull
    @Override
    public Document addBoolean(@Nonnull DocumentKey key, @Nullable Boolean value) {
        if (value != null) {
            getWrapped().addBoolean(key, value);
        }

        return this;
    }

    @Nonnull
    @Override
    public Document addBooleans(@Nonnull DocumentKey key, Boolean... values) {
        getWrapped().addBooleans(key, filter(values));

        return this;
    }

    @Nonnull
    @Override
    public Document addBooleans(@Nonnull DocumentKey key, @Nonnull Collection<Boolean> values) {
        getWrapped().addBooleans(key, filter(values));

        return this;
    }

    @Nonnull
    @Override
    public Document appendBoolean(@Nonnull DocumentKey key, @Nullable Boolean value) {
        if (value != null) {
            getWrapped().appendBoolean(key, value);
        }

        return this;
    }

    @Nonnull
    @Override
    public Document addEnum(@Nonnull DocumentKey key, @Nullable Enum<?> value) {
        if (value != null) {
            getWrapped().addEnum(key, value);
        }

        return this;
    }

    @SafeVarargs
    @Nonnull
    @Override
    public final <E extends Enum<E>> Document addEnums(@Nonnull DocumentKey key, E... values) {
        getWrapped().addEnums(key, filter(values));

        return this;
    }

    @Nonnull
    @Override
    public <E extends Enum<E>> Document addEnums(@Nonnull DocumentKey key, @Nonnull Collection<E> values) {
        getWrapped().addEnums(key, filter(values));

        return this;
    }

    @Nonnull
    @Override
    public <E extends Enum<E>> Document appendEnum(@Nonnull DocumentKey key, @Nullable E value) {
        if (value != null) {
            getWrapped().appendEnum(key, value);
        }

        return this;
    }

    @Nonnull
    @Override
    public Document addDocument(@Nonnull DocumentKey key, @Nullable CommonDocument value) {
        if (value != null) {
            getWrapped().addDocument(key, value);
        }

        return this;
    }

    @Nonnull
    @Override
    public Document addDocuments(@Nonnull DocumentKey key, CommonDocument... values) {
        getWrapped().addDocuments(key, filter(values));

        return this;
    }

    @Nonnull
    @Override
    public Document addDocuments(@Nonnull DocumentKey key, @Nonnull Collection<? extends CommonDocument> values) {
        getWrapped().addDocuments(key, filter(values));

        return this;
    }

    @Nonnull
    @Override
    public Document appendDocument(@Nonnull DocumentKey key, @Nullable CommonDocument value) {
        if (value != null) {
            getWrapped().appendDocument(key, value);
        }

        return this;
    }


    @Nonnull
    private <T> List<T> filter(T[] values) {
        List<T> filtered = new ArrayList<>(values.length);

        for (T value : values) {
            if (value != null) {
                filtered.add(value);
            }
        }

        return filtered;
    }


    @Nonnull
    private <T> List<T> filter(@Nonnull Collection<T> values) {
        List<T> filtered = new ArrayList<>(values.size());

        for (T value : values) {
            if (value != null) {
                filtered.add(value);
            }
        }

        return filtered;
    }
}
