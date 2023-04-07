package com.github.tymefly.common.document;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.tymefly.common.document.key.DocumentKey;

/**
 * Defines the API to read a Document
 */
public interface DocumentReader {
    /**
     * Null safe accessor for data stored in this document.
     * @param key       a key which must be in this document
     * @param type      Type of the returned data. This must be one of:
     *                  <ul>
     *                      <li>{@link Object} - Sequences are returned as {@link List}s</li>
     *                      <li>{@link Boolean}</li>
     *                      <li>{@link String}</li>
     *                      <li>{@link Number}</li>
     *                      <li>{@link Byte}</li>
     *                      <li>{@link Short}</li>
     *                      <li>{@link Integer}</li>
     *                      <li>{@link Long}</li>
     *                      <li>{@link java.math.BigDecimal}</li>
     *                      <li>{@link java.math.BigInteger}</li>
     *                      <li>a subtype of {@link Enum}</li>
     *                      <li>{@link Document}</li>
     *                  </ul>
     * @param <T>       Type of the returned data
     * @return          the value associated with {@code key} converted to the required {@code type}.
     *                  If this is {@link Object} then the value is returned without any type conversion.
     * @throws NullPointerException if the {@code key} is not in the document or its data is {@literal null}
     * @see #hasValue(DocumentKey)
     */
    @Nonnull
    <T> T get(@Nonnull DocumentKey key, @Nonnull Class<T> type) throws NullPointerException;

    /**
     * Null safe accessor for data stored in this Document.
     * @param key           a key which may be in this document
     * @param defaultValue  Value that is returned if the key is not in the Document or its associated
     *                      value is {@literal null}.
     * @param <T>           Type of the returned data, this must be one of the types described by
     *                      {@link #get(DocumentKey, Class)}
     * @return              the value associated with {@code key}, or the {@code defaultValue}
     * @see #hasValue(DocumentKey)
     */
    @Nonnull
    <T> T getOrDefault(@Nonnull DocumentKey key, @Nonnull T defaultValue);

    /**
     * Null safe accessor for data stored in this Document.
     * @param key           a key which may be in this document
     * @param fromString    Conversion function that transforms data to any arbitrary type.
     *                      Conversion functions must be able to handle {@literal null} values
     * @param <T>           Type of the returned data
     * @return              the value associated with {@code key}, or the {@code defaultValue}
     * @see #hasValue(DocumentKey)
     */
    @Nonnull
    <T> T get(@Nonnull DocumentKey key, @Nonnull Function<String, T> fromString);

    /**
     * Accessor for data stored in this document.
     * @param key       a key which may be in this document
     * @param type      Type of the returned data.
     * @param <T>       Type of the returned data, this must be one of the types described by
     *                      {@link #get(DocumentKey, Class)}
     * @return          the value associated with {@code key} converted to the required {@code type} or
     *                  {@literal null} if the {@code key} is not in the document or the associated value
     *                  is {@literal null}. If the {@code type} is {@link Object} then the value is returned
     *                  without any type conversion.
     * @see #contains(DocumentKey)
     */
    @Nullable
    <T> T getOptional(@Nonnull DocumentKey key, @Nonnull Class<T> type);

    /**
     * Returns an immutable list all the data stored in a specific location in the document. This can be used to
     * retrieve all the values added by calls to methods like {@link Document#addStrings(DocumentKey, String...)}
     * and {@link Document#addDocuments(DocumentKey, Collection)}
     * @param key       a key which may be in this document
     * @param type      Type of the returned data.
     * @param <T>       Type of the returned data, this must be one of the types described by
     *                      {@link #get(DocumentKey, Class)}
     * @return          all the data stored in a specific location in the document. An empty list is returned if
     *                      the {@code key} is not in the document or its data is {@literal null}
     */
    @Nonnull
    <T> List<? extends T> getAll(@Nonnull DocumentKey key, @Nonnull Class<T> type);


    /**
     * Returns {@literal true} only if there is no data in this Document
     * @return {@literal true} only if there is no data in this Document
     */
    boolean isEmpty();

    /**
     * Returns {@literal true} only if the {@code key} is present in the Document.
     * {@literal true} is returned even if the associated value is {@literal null}.
     * @param key       a key which may be in this document
     * @return {@literal true} if the {@code key} is present in the Document.
     * @see #hasValue(DocumentKey)
     */
    boolean contains(@Nonnull DocumentKey key);

    /**
     * Returns {@literal true} if the {@code key} is in the Document and the value is not {@literal null}
     * is not {@literal null}
     * @param key       a key which may be in this document
     * @return {@literal true} if the {@code key} is in the Document and the value is not {@literal null}
     * @see #contains(DocumentKey)
     */
    boolean hasValue(@Nonnull DocumentKey key);

    /**
     * Returns {@literal true} if the {@code key} is in the Document and the associated value is
     * a sequence of values.
     * @param key       a key which may be in this document
     * @return {@literal true} if the {@code key} is in the Document and the associated value is
     * a sequence of values.
     * @see #getAll(DocumentKey, Class)
     */
    boolean isSequence(@Nonnull DocumentKey key);
}
