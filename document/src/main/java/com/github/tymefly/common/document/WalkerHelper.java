package com.github.tymefly.common.document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Utility functions used by classes that handle {@link WalkerKey} objects
 */
class WalkerHelper {
    /** Hide Utility class constructor */
    private WalkerHelper() {
    }


    /**
     * Ensure that {@code data} is of the required {@code type} with-out applying any conversions
     * @param type      Required type of the data
     * @param key       Walker key
     * @param data      Value to be tested
     * @param <E>       Required type of the data
     * @return          the {@code data}
     * @throws DocumentException is the value is not of the required type
     */
    @Nullable
    @SuppressWarnings("unchecked")
    static <E> E cast(@Nonnull Class<E> type, @Nonnull WalkerKey key, @Nullable Object data) throws DocumentException {
        E result;

        if (data == null) {
            result = null;
        } else if (type.isAssignableFrom(data.getClass())) {
            result = (E) data;
        } else {
            throw new DocumentException("Data at '%s' is of type %s, but %s was expected",
                    key.elementPath(), data.getClass().getSimpleName(), type.getSimpleName());
        }

        return result;
    }

    /**
     * Ensure that {@code data} is a sequence of the required {@code type} with-out any type conversions
     * @param <E>       Required type of the sequence
     * @param type      Required type of the sequence
     * @param key       Walker key
     * @param data      Value to be tested
     * @return          the {@code data}
     * @throws DocumentException is the value is not a Sequence of the required type or is {@literal null}
     */
    @Nonnull
    private static <E> Sequence<E> castSequence(@Nonnull Class<E> type,
                                                @Nonnull WalkerKey key,
                                                @Nonnull Object data) throws DocumentException {
        Sequence<?> sequence = cast(Sequence.class, key, data);

        return castSequence(type, key, sequence);
    }


    @SuppressWarnings("unchecked")
    @Nonnull
    private static <E> Sequence<E> castSequence(@Nonnull Class<E> type,
                                                @Nonnull WalkerKey key,
                                                @Nonnull Sequence<?> sequence) {
        if (!type.isAssignableFrom(sequence.getType())) {
            throw new DocumentException("Sequence at '%s' is of type %s, but %s was expected",
                    key.simplePath(), sequence.getType().getSimpleName(), type.getSimpleName());
        }

        return (Sequence<E>) sequence;
    }


    /**
     * Extract a field from the {@code key.simpleKey()} of the {@code structure}.
     * This may contain a nested {@link Sequence}
     * @param type          Required type of the data
     * @param structure     Structure to take the data from
     * @param key           Walker key that specific the index in the structure
     * @param <E>           Required type of the data
     * @return              Data from the {@code structure}, or {@literal null} if the value is not present
     */
    @Nullable
    static <E> E get(@Nonnull Class<E> type, @Nonnull Structure structure, @Nonnull WalkerKey key) {
        String simple = key.simpleKey();
        Object value;

        if (key.hasIndex()) {
            Object raw = structure.get(simple);
            Sequence<E> children = (raw == null ? null : castSequence(type, key, raw));

            value = (children == null ? null : children.get(key.index()));
        } else {
            value = structure.get(simple);
        }

        return cast(type, key, value);
    }


    /**
     * Get a {@link Sequence} of {@code type} from {@code key.simpleKey()} of the {@code structure}, or
     * create one if it does not exist.
     * <br>
     * Reading an empty sequence is a special case; this method can change the type of an existing structure
     * so that it matches the required type. This allows parsers to create empty structures in the document
     * with-out forcing the type of the data
     * @param structure     structure to read
     * @param key           key to the data
     * @param type          type of the structure
     * @param <T>           type of the structure
     * @return              A sequence that is in the structure
     * @throws DocumentException is the value is not a Sequence of the required type or is {@literal null}
     */
    @Nonnull
    static <T> Sequence<T> getSequence(@Nonnull Structure structure,
                                       @Nonnull WalkerKey key,
                                       @Nonnull Class<T> type) throws DocumentException {
        Sequence<T> sequence = null;
        String name = key.simpleKey();
        Object data = structure.get(name);

        if (data != null) {
            Sequence<?> found = cast(Sequence.class, key, data);

            if (!found.isEmpty()) {
                sequence = castSequence(type, key, found);
            }
        } else if (structure.containsKey(name)) {
            throw new DocumentException("Data at '%s' is null, but Sequence was expected", key.simplePath());
        } else {
            // Do nothing - no sequence found so create one
        }

        if (sequence == null) {
            sequence = Sequence.of(type, key.index());

            structure.put(name, sequence);
        }

        return sequence;
    }
}
