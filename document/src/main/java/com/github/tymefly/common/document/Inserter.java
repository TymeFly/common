package com.github.tymefly.common.document;

import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import com.github.tymefly.common.base.validate.Preconditions;
import com.github.tymefly.common.document.key.DocumentKey;


/**
 * Walk through the Document tree and insert some data. Missing child documents and structures will be
 * created as required.
 *
 * @implNote Rather than walking through the {@link Structure} elements inside the {@link DocumentImpl} the walker
 * will call top methods in the nested {@link Document} objects. This is because Decorators for child documents may
 * add additional specialist functionality.
 */
@ThreadSafe
class Inserter {
    /** Builders for {@link Inserter} objects */
    static class Builder {
        private BiConsumer<DocumentImpl, WalkerKey> found;
        private Function<AbstractDocument<?>, ? extends AbstractDocument<?>> constructor;
        private BiConsumer<WritableDocument<?>, DocumentKey> toWalk;


        /**
         * Sets the toWalk function that will be used to walk one step towards the ultimate child document
         * @param toWalk    a Function that is used to walk one step towards the ultimate child document
         * @return          a fluent interface
         */
        @Nonnull
        Builder toWalk(@Nonnull BiConsumer<WritableDocument<?>, DocumentKey> toWalk) {
            this.toWalk = toWalk;

            return this;
        }

        /**
         * Used to set the function that is executed on the ultimate child document to set the result
         * @param found     Function to expected on the ultimate child document
         * @return          A fluent interface
         */
        @Nonnull
        Builder whenFound(@Nonnull BiConsumer<DocumentImpl, WalkerKey> found) {
            this.found = found;

            return this;
        }

        /**
         * Used to set the function that creates missing inner document elements
         * @param constructor   Function that obtains the value returned if the document is missing element in the key.
         * @return              A fluent interface
         */
        @Nonnull
        Builder constructBy(@Nonnull Function<AbstractDocument<?>, ? extends AbstractDocument<?>> constructor) {
            this.constructor = constructor;

            return this;
        }


        @Nonnull
        Inserter build() {
            return new Inserter(this);
        }
    }


    private final BiConsumer<WritableDocument<?>, DocumentKey> toWalk;
    private final BiConsumer<DocumentImpl, WalkerKey> found;
    private final Function<AbstractDocument<?>, ? extends AbstractDocument<?>> constructor;


    private Inserter(@Nonnull Builder builder) {
        this.toWalk = Preconditions.checkSet(builder.toWalk, "Found Function");
        this.found = Preconditions.checkSet(builder.found, "Found Function");
        this.constructor = Preconditions.checkSet(builder.constructor, "Document constructor");
    }


    void insert(@Nonnull AbstractDocument<?> document, @Nonnull DocumentKey key) {
        WalkerKey walkerKey = WalkerKey.from(key);
        String name = walkerKey.simpleKey();

        if (walkerKey.hasChildren()) {
            AbstractDocument<?> child;
            Structure structure = document.getStructure();

            if (walkerKey.hasIndex()) {
                Sequence<CommonDocument> sequence =
                    WalkerHelper.getSequence(structure, walkerKey, CommonDocument.class);

                child = (AbstractDocument<?>) sequence.computeIfAbsent(walkerKey.index(), i -> constructor.apply(null));
            } else {
                Object found = structure.computeIfAbsent(name, k -> constructor.apply(null));

                child = WalkerHelper.cast(AbstractDocument.class, walkerKey, found);
            }

            toWalk.accept(child, walkerKey.shift());
        } else {
            found.accept(document.getImpl(), walkerKey);
        }
    }
}
