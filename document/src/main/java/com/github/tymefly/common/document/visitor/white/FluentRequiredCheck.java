package com.github.tymefly.common.document.visitor.white;

import javax.annotation.Nonnull;

/**
 * Part of the {@link WhiteList.Builder} fluent interface. This is responsible for defining checks on
 * individual keys within the Document that are required to be in the Document under test.
 * @see WhiteList.Builder#require()
 */
public interface FluentRequiredCheck extends FluentCheck {
    /**
     * If called as part of the fluid interface, the next check will accept {@literal null} as valid value.
     * If this method is not called then {@literal null} will not be considered a valid value.
     * @return  A fluent interface
     */
    @Nonnull
    FluentCheck allowNull();
}
