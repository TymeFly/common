package com.github.tymefly.common.document;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.github.tymefly.common.base.validate.Preconditions;
import com.github.tymefly.common.document.key.DocumentKey;

/**
 * Walk through the Document tree to find a field and perform some action on it. Walker instances are thread-safe
 * and may be shared by Documents.
 *
 * @implNote Rather than walking through the {@link Structure} elements inside the {@link DocumentImpl} the walker
 * will call top methods in the nested {@link Document} objects. This is because Decorators for child documents may
 * add additional specialist functionality.
 * @param <T>       Type of the returned value
 */
@ThreadSafe
class Walker<T> {
    /**
     * Builders for {@link Inserter} objects
     * @param <T>       Type of the returned value
     */
    static class Builder<T> {
        private BiFunction<Document, DocumentKey, T> function;
        private BiFunction<DocumentImpl, WalkerKey, T> found;
        private Supplier<T> notFound;

        /**
         * Sets the function that will be used to walk one step towards the ultimate child document
         * @param function  a Function that is used to walk one step towards the ultimate child document
         * @return          a fluent interface
         */
        @Nonnull
        Builder<T> toWalk(@Nonnull BiFunction<Document, DocumentKey, T> function) {
            this.function = function;

            return this;
        }

        /**
         * Sets a function that can be executed on the ultimate child document to read the data
         * @param found     a function that can be executed on the ultimate child document to read the data
         * @return          a fluent interface
         */
        @Nonnull
        Builder<T> whenFound(@Nonnull BiFunction<DocumentImpl, WalkerKey, T> found) {
            this.found = found;

            return this;
        }

        /**
         * Sets a function that can be used to obtain the value that is returned if a required child
         * document is missing
         * @param notFound  a function that can be used to obtain the value that is returned if a required child
         *                      document is missing
         * @return          a fluent interface
         */
        @Nonnull
        Builder<T> whenNotFound(@Nonnull Supplier<T> notFound) {
            this.notFound = notFound;

            return this;
        }

        @Nonnull
        Walker<T> build() {
            return new Walker<>(this);
        }
    }


    private final BiFunction<Document, DocumentKey, T> function;
    private final BiFunction<DocumentImpl, WalkerKey, T> found;
    private final Supplier<T> notFound;


    private Walker(@Nonnull Builder<T> builder) {
        this.function = Preconditions.checkSet(builder.function, "Walk Function");
        this.found = Preconditions.checkSet(builder.found, "Found Function");
        this.notFound = Preconditions.checkSet(builder.notFound, "NotFound Function");
    }


    @Nullable
    T walk(@Nonnull AbstractDocument<?> document, @Nonnull DocumentKey key) {
        T result;
        WalkerKey walkerKey = WalkerKey.from(key);

        if (walkerKey.hasChildren()) {
            Structure structure = document.getStructure();
            Object child = WalkerHelper.get(Object.class, structure, walkerKey);

            if (child == null) {
                result = notFound.get();
            } else {
                Document childDoc = WalkerHelper.cast(Document.class, walkerKey, child);

                result = function.apply(childDoc, walkerKey.shift());
            }
        } else {
            result = found.apply(document.getImpl(), walkerKey);
        }

        return result;
    }
}
