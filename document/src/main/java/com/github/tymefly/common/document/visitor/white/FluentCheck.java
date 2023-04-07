package com.github.tymefly.common.document.visitor.white;

import java.util.function.Predicate;

import javax.annotation.Nonnull;

import com.github.tymefly.common.document.key.DocumentKey;

/**
 * Part of the {@link WhiteList.Builder} fluent interface. This is responsible for
 * defining checks on individual keys within the Document
 */
public interface FluentCheck {
    /**
     * Check the length of the string associated with {@code key}.
     * @param key       DocumentKey that will be validated
     * @param maxLength maximum number of characters the string can contain
     * @return a fluent interface
     * @see #forLength(DocumentKey, int, int)
     */
    @Nonnull
    WhiteList.Builder forLength(@Nonnull DocumentKey key, int maxLength);

    /**
     * Check the length of the string associated with {@code key}.
     * @param key       DocumentKey that will be validated
     * @param minLength minimum number of characters the string can contain
     * @param maxLength maximum number of characters the string can contain
     * @return a fluent interface
     * @see #forLength(DocumentKey, int)
     */
    @Nonnull
    WhiteList.Builder forLength(@Nonnull DocumentKey key, int minLength, int maxLength);

    /**
     * Check that the string associated with {@code key} matches a regular expression
     * @param key   DocumentKey that will be validated
     * @param regEx Regular expression that the data will be matched against
     * @return a fluent interface
     */
    @Nonnull
    WhiteList.Builder forRegEx(@Nonnull DocumentKey key, @Nonnull String regEx);

    /**
     * Check that the number associated with {@code key} is a none-negative, non-fractional number
     * @param key DocumentKey that will be validated
     * @return a fluent interface
     */
    @Nonnull
    WhiteList.Builder forCardinal(@Nonnull DocumentKey key);

    /**
     * Check that the number associated with {@code key} is a non-fractional number.
     * Negative numbers are valid
     * @param key DocumentKey that will be validated
     * @return a fluent interface
     */
    @Nonnull
    WhiteList.Builder forInteger(@Nonnull DocumentKey key);

    /**
     * Check that the number associated with {@code key} is numeric.
     * Negative and fractional numbers are considered valid
     * @param key DocumentKey that will be validated
     * @return a fluent interface
     */
    @Nonnull
    WhiteList.Builder forDecimal(@Nonnull DocumentKey key);

    /**
     * Check that the number associated with {@code key} is numeric and within
     * the range 0 to {@code maximum}. Any fractional parts of the number are ignored
     * @param key     DocumentKey that will be validated
     * @param maximum maximum value the data can take
     * @return a fluent interface
     */
    @Nonnull
    WhiteList.Builder forRange(@Nonnull DocumentKey key, long maximum);

    /**
     * Check that the number associated with {@code key} is numeric and within
     * a specific range. Any fractional parts of the number are ignored
     * @param key     DocumentKey that will be validated
     * @param minimum minimum value the data can take
     * @param maximum maximum value the data can take
     * @return a fluent interface
     */
    @Nonnull
    WhiteList.Builder forRange(@Nonnull DocumentKey key, long minimum, long maximum);

    /**
     * Check that the value associated with {@code key} is a boolean value
     * The check is case-insensitive. As well as {@code true} and {@code false} other boolean values are:
     * {@code 0}, {@code off}, {@code disabled}, {@code unset}, {@code 1}, {@code on}, {@code enabled}
     * and {@code set}
     * @param key DocumentKey that will be validated
     * @return a fluent interface
     */
    @Nonnull
    WhiteList.Builder forBoolean(@Nonnull DocumentKey key);

    /**
     * Checks that the data associated with the {@code key} is an enumeration constant.
     * The check allows some leeway in the constant name; leading and trailing spaces are ignored,
     * spaces within a name are replaced with an underscore and case is ignored.
     * @param key  DocumentKey that will be validated
     * @param type enumeration type which the data must belong to
     * @param <E>  enumeration type which the data must belong to
     * @return a fluent interface
     */
    @Nonnull
    <E extends Enum<E>> WhiteList.Builder forEnum(@Nonnull DocumentKey key, @Nonnull Class<E> type);

    /**
     * Add an adhoc validation check
     * @param key  DocumentKey that will be validated
     * @param test Test to be performed
     * @return a fluent interface
     */
    @Nonnull
    WhiteList.Builder forCheck(@Nonnull DocumentKey key, @Nonnull Predicate<? super Object> test);
}
