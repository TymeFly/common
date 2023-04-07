package com.github.tymefly.common.base.config;

import javax.annotation.Nonnull;

/**
 * An extension of a ConfigSetting with a default implementation of the {@link ConfigSetting#getKey()} method
 * that uses the lowercase {@link #name()} as the external key, with dots ({@literal .}) separating words,
 * which is typically used by property files.
 * This interface is expected to be implemented by an {@code enum} type
 * @see ConfigSetting
 * @see EnvConfig
 */
public interface LowerCaseSetting extends ConfigSetting {
    /**
     * Returns the name of this object. This is used to generate the key
     * @return the name of this object. This is used to generate the key
     */
    @Nonnull
    String name();


    /**
     * Returns the key used to look up the setting in the Config object
     * @return the key used to look up the setting in the Config object
     */
    @Nonnull
    default String getKey() {
        return name().toLowerCase().replace("_", ".");
    }
}
