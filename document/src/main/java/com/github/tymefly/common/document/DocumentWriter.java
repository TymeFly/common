package com.github.tymefly.common.document;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.tymefly.common.document.key.DocumentKey;

/**
 * Defines the contract for mutating the content of a Document
 * @param <F>       Type of the fluent interface
 */
public interface DocumentWriter<F> {
    /**
     * Add a single String value to this Document. If the Document already contains an element at the location
     * given by the {@code key} this it will be silently replaced.
     * @param key       a key that determines the location of the data in the Document
     * @param value     the string to store
     * @return          a fluent interface
     */
    @Nonnull
    F addString(@Nonnull DocumentKey key, @Nullable String value);

    /**
     * Add a sequence of String values to this Document. If the Document already contains an element at the location
     * given by the {@code key} this it will be silently replaced.
     * @param key       a key that determines the location of the data in the Document
     * @param values    the strings to store
     * @return          a fluent interface
     */
    @Nonnull
    F addStrings(@Nonnull DocumentKey key, String... values);

    /**
     * Add a sequence of String values to this Document. If the Document already contains an element at the location
     * given by the {@code key} this it will be silently replaced.
     * @param key       a key that determines the location of the data in the Document
     * @param values    the strings to store. This should be an ordered collection
     * @return          a fluent interface
     */
    @Nonnull
    F addStrings(@Nonnull DocumentKey key, @Nonnull Collection<String> values);

    /**
     * Append a new string to a sequence in this Document. If the Document does not contain a sequence at the
     * specified location then a new sequence containing just the {@code value} will be inserted
     * @param key       a key that determines the location of a sequence in the Document
     * @param value     the string to store
     * @return          a fluent interface
     */
    @Nonnull
    F appendString(@Nonnull DocumentKey key, @Nullable String value);

    /**
     * Add a single Numeric value to this Document. If the Document already contains an element at the location
     * given by the {@code key} this it will be silently replaced.
     * @param key       a key that determines the location of the data in the Document
     * @param value     the number to store
     * @return          a fluent interface
     */
    @Nonnull
    F addNumber(@Nonnull DocumentKey key, @Nullable Number value);

    /**
     * Add a sequence of Numeric values to this Document. If the Document already contains an element at the location
     * given by the {@code key} this it will be silently replaced.
     * @param key       a key that determines the location of the data in the Document
     * @param values    the numbers to store. This should be an ordered collection but do not have to be the same type
     * @return          a fluent interface
     */
    @Nonnull
    F addNumbers(@Nonnull DocumentKey key, Number... values);

    /**
     * Add a sequence of Numeric values to this Document. If the Document already contains an element at the location
     * given by the {@code key} this it will be silently replaced.
     * @param key       a key that determines the location of the data in the Document
     * @param values    the numbers to store. This should be an ordered collection but do not have to be the same type
     * @return          a fluent interface
     */
    @Nonnull
    F addNumbers(@Nonnull DocumentKey key, @Nonnull Collection<Number> values);

    /**
     * Append a new number to a sequence in this Document. If the Document does not contain a sequence at the
     * specified location then a new sequence containing just the {@code value} will be inserted
     * @param key       a key that determines the location of a sequence in the Document
     * @param value     the string to store
     * @return          a fluent interface
     */
    @Nonnull
    F appendNumber(@Nonnull DocumentKey key, @Nullable Number value);

    /**
     * Add a single Boolean value to this Document. If the Document already contains an element at the location
     * given by the {@code key} this it will be silently replaced.
     * @param key       a key that determines the location of the data in the Document
     * @param value     the boolean to store
     * @return          a fluent interface
     */
    @Nonnull
    F addBoolean(@Nonnull DocumentKey key, @Nullable Boolean value);

    /**
     * Add a sequence of Boolean values to this Document. If the Document already contains an element at the location
     * given by the {@code key} this it will be silently replaced.
     * @param key       a key that determines the location of the data in the Document
     * @param values    the booleans to store. This should be an ordered collection
     * @return          a fluent interface
     */
    @Nonnull
    F addBooleans(@Nonnull DocumentKey key, Boolean... values);

    /**
     * Add a sequence of Boolean values to this Document. If the Document already contains an element at the location
     * given by the {@code key} this it will be silently replaced.
     * @param key       a key that determines the location of the data in the Document
     * @param values    the booleans to store. This should be an ordered collection
     * @return          a fluent interface
     */
    @Nonnull
    F addBooleans(@Nonnull DocumentKey key, @Nonnull Collection<Boolean> values);

    /**
     * Append a new Boolean to a sequence in this Document. If the Document does not contain a sequence at the
     * specified location then a new sequence containing just the {@code value} will be inserted
     * @param key       a key that determines the location of a sequence in the Document
     * @param value     the string to store
     * @return          a fluent interface
     */
    @Nonnull
    F appendBoolean(@Nonnull DocumentKey key, @Nullable Boolean value);

    /**
     * Add a single enumeration constant value to this Document. If the Document already contains an element at
     * the location given by the {@code key} this it will be silently replaced.
     * @param key       a key that determines the location of the data in the Document
     * @param value     the enumeration constant to store
     * @return          a fluent interface
     */
    @Nonnull
    F addEnum(@Nonnull DocumentKey key, @Nullable Enum<?> value);

    /**
     * Add a sequence of enumeration constants to this Document. If the Document already contains an element at
     * the location given by the {@code key} this it will be silently replaced.
     * <br>
     * <b>Implementations must ensure there is no heap pollution.</b>
     * @param key       a key that determines the location of the data in the Document
     * @param values    the enumeration constants to store. This should be an ordered collection
     * @param <E>       The type of the enumeration constants
     * @return          a fluent interface
     */
    @Nonnull
    <E extends Enum<E>> F addEnums(@Nonnull DocumentKey key, E... values);

    /**
     * Add a sequence of enumeration constants to this Document. If the Document already contains an element at
     * the location given by the {@code key} this it will be silently replaced.
     * @param key       a key that determines the location of the data in the Document
     * @param values    the enumeration constants to store. This should be an ordered collection
     * @param <E>       The type of the enumeration constants
     * @return          a fluent interface
     */
    @Nonnull
    <E extends Enum<E>> F addEnums(@Nonnull DocumentKey key, @Nonnull Collection<E> values);

    /**
     * Append a new Enumeration constant to a sequence in this Document. If the Document does not contain a sequence
     * at the specified location then a new sequence containing just the {@code value} will be inserted
     * @param key       a key that determines the location of a sequence in the Document
     * @param value     the string to store
     * @param <E>       The type of the enumeration constant
     * @return          a fluent interface
     */
    @Nonnull
    <E extends Enum<E>> F appendEnum(@Nonnull DocumentKey key, @Nullable E value);

    /**
     * Add a single child Document value to this Document. If the Document already contains an element at the location
     * given by the {@code key} this it will be silently replaced.
     * @param key       a key that determines the location of the data in the Document
     * @param value     the child document to store. This must be a document created by this package
     * @return          a fluent interface
     */
    @Nonnull
    F addDocument(@Nonnull DocumentKey key, @Nullable CommonDocument value);

    /**
     * Add a sequence of child Documents to this Document. If the Document already contains an element at the location
     * given by the {@code key} this it will be silently replaced.
     * @param key       a key that determines the location of the data in the Document
     * @param values    the child Documents to store. These must be a documents created by this package
     * @return          a fluent interface
     */
    @Nonnull
    F addDocuments(@Nonnull DocumentKey key, CommonDocument... values);

    /**
     * Add a sequence of child Documents to this Document. If the Document already contains an element at the location
     * given by the {@code key} this it will be silently replaced.
     * @param key       a key that determines the location of the data in the Document
     * @param values    the child Documents to store. These must be a documents created by this package
     * @return          a fluent interface
     */
    @Nonnull
    F addDocuments(@Nonnull DocumentKey key, @Nonnull Collection<? extends CommonDocument> values);

    /**
     * Append a new Document to a sequence in this Document. If the Document does not contain a sequence at the
     * specified location then a new sequence containing just the {@code value} will be inserted
     * @param key       a key that determines the location of a sequence in the Document
     * @param value     the string to store
     * @return          a fluent interface
     */
    @Nonnull
    F appendDocument(@Nonnull DocumentKey key, @Nullable CommonDocument value);

    /**
     * Remove a single element or sequence from this Document. If the Document does not contain any data at the location
     * given by the {@code key} then this method will not do anything
     * @param key       a key that determines the location of the data in the Document that will be removed
     * @return          a fluent interface
     */
    @Nonnull
    F remove(@Nonnull DocumentKey key);
}
