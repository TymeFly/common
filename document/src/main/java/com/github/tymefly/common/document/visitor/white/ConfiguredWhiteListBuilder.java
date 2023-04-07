package com.github.tymefly.common.document.visitor.white;

import javax.annotation.Nonnull;

/**
 * Part of the {@link WhiteList.Builder} fluent interface. This is the final step in a creating a
 * WhiteList Visitor.
 */
public interface ConfiguredWhiteListBuilder {
    /**
     * Returns a fully configured WhiteList visitor
     * @return a fully configured WhiteList visitor
     */
    @Nonnull
    WhiteList build();
}
