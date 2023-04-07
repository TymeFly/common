package com.github.tymefly.common.base.config;

import java.util.stream.Collectors;

import javax.annotation.Nonnull;

/**
 * Implementation of a Config object that is backed by the JVM Properties
 * @param <S>       Type of the ConfigSettings
 * @see System#getProperties()
 */
public class PropertyConfig<S extends ConfigSetting> extends AbstractConfig<S> {
    /**
     * Constructor
     */
    public PropertyConfig() {
        super(System.getProperties()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                    e -> e.getKey().toString(),
                    e -> e.getValue().toString())));
    }

    
    @Nonnull
    @Override
    protected String source() {
        return "System Properties";
    }
}
