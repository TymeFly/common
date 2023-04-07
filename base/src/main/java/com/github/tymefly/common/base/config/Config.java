package com.github.tymefly.common.base.config;

import javax.annotation.Nonnull;

import com.github.tymefly.common.base.utils.Convert;


/**
 * Configuration Reader
 * @param <S>       Type of configuration Setting
 */
interface Config<S extends ConfigSetting> {
    /**
     * Returns {@code true} only if the setting has been defined in backing Config object. If {@literal false}
     * is returned then {@link #read(ConfigSetting, Class)} will attempt to return the default value
     * @param setting       Setting to examine
     * @return {@code true} only if the setting has been defined in backing Config object.
     */
    boolean contains(@Nonnull S setting);

    /**
     * Read a single setting from the Config object. If the setting is not defined then the default value will be
     * returned. Values are converted to the required {@code type} using the rules described in
     * {@link Convert}
     * @param setting       Setting to read
     * @param type          Type of the return value
     * @param <V>           Type of the return value
     * @return If the setting is defined in the backing object then return it; else return the default value
     * @throws IllegalStateException if the setting is not defined and there is no default
     * @throws ConfigException the value can not be represented in the required {@code type}
     */
    @Nonnull
    <V> V read(@Nonnull S setting, @Nonnull Class<V> type) throws ConfigException, IllegalStateException;
}
