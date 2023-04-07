package com.github.tymefly.common.document;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A type safe expandable array of elements
 * @param <E>       Base type of elements in the sequence
 */
class Sequence<E> implements Iterable<E> {
    private static final int HEAD_ROOM = 8;
    private static final int SMALL_PRIME = 31;

    private final Class<E> type;
    private E[] data;
    private int size;
    private List<E> list;           // Cached value of toList() - this must be set to null when Sequence is mutated


    private Sequence(@Nonnull Class<E> type, @Nonnull E[] data, int size) {
        this.type = type;
        this.data = data;
        this.size = size;
    }


    /**
     * Create an empty sequence of elements
     * @param type      Expected type of the elements
     * @param <T>       Expected type of the elements
     * @return          An empty sequence of elements
     */
    @Nonnull
    static <T> Sequence<T> of(@Nonnull Class<T> type) {
        return of(type, 0);
    }


    /**
     * Create an empty sequence of elements
     * @param type          Expected type of the elements
     * @param expectedSize  Expected size of the sequence
     * @param <T>           Expected type of the elements
     * @return              An empty sequence of elements
     */
    @Nonnull
    static <T> Sequence<T> of(@Nonnull Class<T> type, int expectedSize) {
        // Round up to the nearest multiple of HEAD_ROOM
        expectedSize = ((expectedSize + HEAD_ROOM) / HEAD_ROOM) * HEAD_ROOM;

        // Create a non-zero sized array on the basis that we want a sequence to store data in it
        @SuppressWarnings("unchecked")
        T[] data = (T[]) Array.newInstance(type, expectedSize);

        return new Sequence<>(type, data, 0);
    }


    /**
     * Create an initialised sequence of elements
     * @param type      Base type of the elements
     * @param data      Initial values of this sequence
     * @param <T>       Base type of the elements
     * @return          An initialised sequence of elements
     */
    @Nonnull
    @SafeVarargs
    static <T> Sequence<T> of(@Nonnull Class<T> type, T... data) {
        // Don't extend the array - if we've set the data we probably know all the elements we want to store
        return new Sequence<>(type, data.clone(), data.length);
    }


    /**
     * Create an initialised sequence of elements.
     * If {@code values} are {@literal null} then {@literal null} is returned
     * @param type      Base type of the elements
     * @param data      Initial values of this sequence
     * @param <T>       Base type of the elements
     * @return          An initialised sequence of elements
     */
    @Nullable
    static <T> Sequence<T> of(@Nonnull Class<T> type, @Nullable Collection<? extends T> data) {
        // Don't extend the list - if we've set the data we probably know all the elements we want to store
        Sequence<T> result;

        if (data == null) {
            result = null;
        } else {
            @SuppressWarnings("unchecked")
            T[] backing = (T[]) data.toArray();

            result = new Sequence<>(type, backing, backing.length);
        }

        return result;
    }


    @Override
    @Nonnull
    public Iterator<E> iterator() {
        return toList().iterator();
    }


    /**
     * Return the element at {@code index} of this sequence, or {@literal null} if {@code index} is beyond the
     * size of this sequence.
     * Index is 0 based.
     * @param index     An index into this sequence
     * @return the element at {@code index} of this sequence
     */
    @Nullable
    E get(int index) {
        return (index < data.length ? data[index] : null);
    }


    E computeIfAbsent(int index, @Nonnull Function<Integer, ? extends E> mappingFunction) {
        E result = get(index);

        if (result == null) {
            result = mappingFunction.apply(index);
            set(index, result);
        }

        return result;
    }


    /**
     * Append a new value to the end of this sequence.
     * @param value     the element to append to this sequence
     */
    void append(@Nullable E value) {
        set(size, value);
    }


    /**
     * Sets the element at {@code index} of this sequence. The sequence will automatically be extended if
     * {@literal index} is beyond the current size of this sequence.
     * Index is 0 based.
     * @param index     An index into this sequence at which the {@code value} will be stored
     * @param value     the element to store in this sequence
     * @throws DocumentException if {@code value} is an unexpected type
     */
    void set(int index, @Nullable E value) throws DocumentException {
        if (index >= data.length) {
            @SuppressWarnings("unchecked")
            E[] update = (E[]) Array.newInstance(type, index + HEAD_ROOM);

            System.arraycopy(data, 0, update, 0, data.length);
            data = update;
        }

        try {
            data[index] = value;
        } catch (ArrayStoreException e) {
            throw new DocumentException("Attempt to store %s in an sequence of %s",
                    value.getClass().getSimpleName(),
                    type.getSimpleName());
        }

        size = Math.max(size, index + 1);
        list = null;
    }


    /**
     * Remove an element from the sequence. If the element is mid-sequence then the value will be set to
     * {@literal null}, if it is the last element then the sequence will be resized.
     * @param index     index of element to remove
     * @return          {@literal true} only if the sequence is empty after removal
     */
    boolean remove(int index) {
        if (index < size) {
            list = null;
            data[index] = null;

            while ((size != 0) && (data[size - 1] == null)) {
                size--;
            }
        }

        return (size == 0);
    }


    boolean isEmpty() {
        return (size == 0);
    }


    /**
     * Returns the size of this sequence. This is the index of the last element in the sequence + 1
     * @return the size of this sequence
     */
    int size() {
        return size;
    }


    /**
     * Returns the number of elements that can be stored without resizing the backing array.
     * This will always be at least {@link #size()} elements, but may be larger
     * @return the number of elements that can be stored without resizing the backing array
     */
    int capacity() {
        return data.length;
    }


    @Nonnull
    List<E> toList() {
        if (list == null) {
            list = Arrays.stream(data, 0, size)
                .collect(Collectors.toList());
        }

        return list;
    }


    @Nonnull
    Class<E> getType() {
        return type;
    }

    @Override
    public boolean equals(Object other) {
        boolean equal;

        if (this == other) {
            equal = true;
        } else if (other instanceof Sequence<?> s) {
            equal = equalsHelper(s);
        } else {
            equal = false;
        }

        return equal;
    }

    private boolean equalsHelper(@Nonnull Sequence<?> other) {
        int index = other.size;
        boolean equal = (size == index);

        while (equal && (index-- != 0)) {
            equal = Objects.equals(data[index], other.data[index]);
        }

        return equal;
    }

    @Override
    public int hashCode() {
        // We can't use Arrays.hashCode() because unused elements to make up the HEAD_ROOM would change the hashCode
        int result = 1;

        for (int index = 0; index < size; index++) {
            Object element = data[index];
            result = SMALL_PRIME * result + (element == null ? 0 : element.hashCode());
        }

        return result;
    }


    @Override
    public String toString() {
        String result;

        if (size == 0) {
            result = "[]";
        } else {
            StringBuilder builder = new StringBuilder("[");
            String separator = "";

            for (int i = 0; i < size; i++) {
                builder.append(separator)
                       .append(data[i]);
                separator = ", ";
            }

            result = builder.append(']')
                .toString();
        }

        return result;
    }
}
