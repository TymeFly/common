package com.github.tymefly.common.base.config;

import java.util.Map;

import javax.annotation.Nonnull;

import com.github.tymefly.common.base.annotation.VisibleForTesting;

/**
 * Implementation of a Config object that is backed by the System Environment variables
 * @param <S>       Type of the ConfigSettings
 * @see System#getenv()
 * @see UpperCaseSetting
 */
public class EnvConfig<S extends ConfigSetting> extends AbstractConfig<S> {
    /**
     * Application Constructor
     */
    public EnvConfig() {
        this(System.getenv());
    }


    /**
     * Unit test Constructor
     */
    @VisibleForTesting
    EnvConfig(@Nonnull Map<String, String> environment) {
        super(environment);
    }

    
    @Nonnull
    @Override
    protected String source() {
        return "System Environment Variables";
    }
}
