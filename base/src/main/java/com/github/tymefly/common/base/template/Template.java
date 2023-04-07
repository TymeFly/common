package com.github.tymefly.common.base.template;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import com.github.tymefly.common.base.io.FailedIoException;
import com.github.tymefly.common.base.io.TextReader;
import com.github.tymefly.common.base.validate.Preconditions;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

/**
 * A simple template engine.
 * A file on the class path is loaded and all the {@code ${key}} entries in it are replaced with appropriate values.
 * Undefined entries are removed from the generated String.
 */
public class Template {
    private record Parameter (@Nonnull String regEx, @Nonnull String value) {
    }


    private static final Pattern KEY_PATTERN = Pattern.compile("[A-Za-z0-9_.-]+");
    private static final DateTimeFormatter DEFAULT_DATE_FORMAT = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE)
            .appendLiteral(" ")
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            .appendLiteral(' ')
            .appendZoneId()
            .toFormatter(Locale.getDefault(Locale.Category.FORMAT))
            .withZone(ZoneId.systemDefault());

    private final List<Parameter> parameters = new ArrayList<>();
    private DateTimeFormatter dateFormat = DEFAULT_DATE_FORMAT;


    /**
     * Set the default time-date formatter used by subsequent calls to {@link #forText(String, TemporalAccessor)}
     * @param dateFormat    Date format
     * @return              A fluent interface
     */
    @Nonnull
    public Template withDateFormat(@Nonnull DateTimeFormatter dateFormat) {
        this.dateFormat = dateFormat;

        return this;
    }


    /**
     * Informs the template engine to replace {@code ${key}} formatted tokens with a value.
     * @param key       a case-insensitive key that may appear in the template
     * @param value     The replacement value
     * @return          A fluent interface
     * @see Object#toString()
     */
    @Nonnull
    public Template forText(@Nonnull String key, @Nonnull Object value) {
        Preconditions.checkArgument(KEY_PATTERN.matcher(key).matches(), "Invalid template parameter '%s'", key);

        String regEx = "(?i)\\$\\{" + key + "\\}";

        parameters.add(new Parameter(regEx, value.toString()));

        return this;
    }


    /**
     * Informs the template engine to replace {@code ${key}} formatted tokens with a date-time value.
     * This will be formatted into ISO-8601 format and is accurate to the nearest value
     * @param key       A case-insensitive key that may appear in the template
     * @param value     The replacement date-time value
     * @return          A fluent interface
     * @see Object#toString()
     */
    @Nonnull
    public Template forText(@Nonnull String key, @Nonnull TemporalAccessor value) {
        return forText(key, dateFormat, value);
    }


    /**
     * Informs the template engine to replace {@code ${key}} formatted tokens with a date-time value.
     * This will be formatted into ISO-8601 format and is accurate to the nearest value
     * @param key           A case-insensitive key that may appear in the template
     * @param dateFormat    Formatter for the {@code value}
     * @param value         The replacement date-time value
     * @return              A fluent interface
     * @see Object#toString()
     */
    @Nonnull
    public Template forText(@Nonnull String key,
                            @Nonnull DateTimeFormatter dateFormat,
                            @Nonnull TemporalAccessor value) {
        return forText(key, dateFormat.format(value));
    }


    /**
     * Load the template from the class path and apply all the transformations in the order they were defined
     * @param fileName      Name of a file on the class path
     * @return              The content of the file with all the transformations in a single String
     * @throws FailedIoException  if the file could not be loaded
     */
    @Nonnull
    public String load(@Nonnull String fileName) throws FailedIoException {
        String content;

        try (
            InputStream in = getClass().getResourceAsStream(fileName)
        ) {
            Preconditions.checkState((in != null), "File '%s' was not found", fileName);

            TextReader reader = TextReader.from(in);

            for (var parameter : parameters) {
                String regEx = parameter.regEx();
                String value = parameter.value();

                reader = reader.transform(regEx, value);
            }

            content = reader.transform("\\$\\{.*\\}", "")       // Remove undefined RegEx replacements
                .text();
        } catch (RuntimeException | IOException e) {
            throw new FailedIoException("Failed to read file '" + fileName + "'", e);
        }

        return content;
    }
}
