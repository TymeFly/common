package com.github.tymefly.common.document.visitor.white;

import java.util.function.Consumer;

import javax.annotation.Nonnull;

import com.github.tymefly.common.document.key.DocumentKey;

/**
 * Part of the {@link WhiteList.Builder} fluent interface. This is responsible for defining the actions
 * if the WhiteList is passed invalid data and building the final WhiteList Object
 */
public interface FluentFailure extends ConfiguredWhiteListBuilder {
    /**
     * Throw a {@link WhiteListException} if invalid values were found.
     * If a failure action is not set then the default action is to filter out invalid fields
     * @return  A fluent interface
     * @see #onFail(Consumer)
     */
    @Nonnull
    FluentFailure onFail();

    /**
     * Sets a custom action that is performed when invalid values are found. If multiple actions are set then
     * they are performed in the order they are defined.
     * If a failure action is not set then the default action is to filter out invalid fields
     * @param handler       Action that is performed if a validation fails
     * @return  A fluent interface
     * @see #onFail()
     */
    @Nonnull
    FluentFailure onFail(@Nonnull Consumer<DocumentKey> handler);

}
