package com.github.tymefly.common.base.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.github.tymefly.common.base.io.TextReader;

/**
 * Implementation of a Config object that is backed by something similar to a standard Java properties file.
 * <ul>
 *     <li>Settings are name-value pairs separated by an equals sign</li>
 *     <li>Names can not contain an equals sign but values can</li>
 *     <li>Commends are a hash sign ({@code #})</li>
 *     <li>Commends can appear at the start of a line</li>
 *     <li>Commends can appear after property setting provided they are separated from the setting
 *              by at least one space</li>
 *     <li>Tabs are converted to a single space</li>
 *     <li>Names and values will have leading and trailing spaces removed</li>
 *     <li>Blank lines are ignored</li>
 *     <li>The Backslash character ({@code \}) has no special meaning</li>
 *     <li>Files are encoded in UTF-8</li>
 * </ul>
 * @param <S>       Type of the ConfigSettings
 */
public class ConfigFile<S extends ConfigSetting> extends AbstractConfig<S> {
    private final String source;

    /**
     * Constructor
     * @param source    The properties file
     */
    public ConfigFile(@Nonnull File source) {
        super(load(source));

        this.source = source.getAbsolutePath();
    }


    @Nonnull
    private static Map<String, String> load(@Nonnull File source) {
        Map<String, String> settings = new HashMap<>();

        TextReader.from(source)
            .expand()
            .removeComments(" #")
            .trim()
            .skipBlanks()
            .forEach((i, l) -> {
                String[] elements = l.split(" *= *", 2);
                String key = elements[0];
                String value = (elements.length == 1 ? "" : elements[1]);

                settings.put(key, value);
            });

        return settings;
    }


    @Nonnull
    @Override
    protected String source() {
        return source;
    }
}
