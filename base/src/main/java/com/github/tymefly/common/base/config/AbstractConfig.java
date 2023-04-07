package com.github.tymefly.common.base.config;

import java.util.Collections;
import java.util.Map;

import javax.annotation.Nonnull;

import com.github.tymefly.common.base.utils.Convert;
import com.github.tymefly.common.base.validate.Preconditions;

/**
 * Base class of Config implementations
 * @param <S>   Type of settings
 */
abstract class AbstractConfig<S extends ConfigSetting> implements Config<S> {
    private final Map<String, String> settings;


    AbstractConfig(@Nonnull Map<String, String> settings) {
        this.settings = Collections.unmodifiableMap(settings);
    }


    /**
     * Returns Human readable description of the source of the properties
     * @return the source of the properties
     */
    @Nonnull
    protected abstract String source();


    @Override
    public boolean contains(@Nonnull S setting) {
        return settings.containsKey(setting.getKey());
    }


    @Override
    @Nonnull
    public <V> V read(@Nonnull S setting, @Nonnull Class<V> type) throws ConfigException, IllegalArgumentException {
        String key = setting.getKey();
        String raw = settings.get(key);
        V result;

        if (raw == null) {
            raw = setting.getDefaultValue();
        }

        try {
            result = Convert.to(raw, type);
        } catch (IllegalArgumentException e) {
            throw new ConfigException(setting.getKey() + " can't be read as a " + type.getSimpleName() + ": " + raw, e);
        }

        Preconditions.checkState((result != null), "Setting %s was not defined in %s", key, source());

        return result;
    }
}
