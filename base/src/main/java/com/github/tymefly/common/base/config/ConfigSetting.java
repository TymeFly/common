package com.github.tymefly.common.base.config;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Interface that defines the contract of a {@link Config} setting.
 */
@FunctionalInterface
public interface ConfigSetting {
    /**
     * Returns the key used to look up the setting in the Config object
     * @return the key used to look up the setting in the Config object
     */
    @Nonnull
    String getKey();


    /**
     * Returns the value that is return if the setting is not defined. {@literal null} indicates that
     * there is no default, in which case {@link Config#read(ConfigSetting, Class)} will throw an
     * {@link IllegalStateException}
     * @return the default value for this setting
     */
    @Nullable
    default String getDefaultValue() {
        return null;
    }
}
