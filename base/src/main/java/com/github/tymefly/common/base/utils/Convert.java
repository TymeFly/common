package com.github.tymefly.common.base.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static java.util.Map.entry;

/**
 * Value conversion functions
 */
public class Convert {
    private static final Set<String> FALSE_VALUES = Set.of("false", "0", "off", "disabled", "unset", "no");
    private static final Set<String> TRUE_VALUES = Set.of("true", "1", "on", "enabled", "set", "yes");

    private static final Map<Class<?>, Class<?>> BOXED_TYPES = Map.ofEntries(
        entry(boolean.class, Boolean.class),
        entry(byte.class, Byte.class),
        entry(short.class, Short.class),
        entry(int.class, Integer.class),
        entry(long.class, Long.class)
    );


    private Convert() {
    }


    /**
     * Convert a Collection of values via the rules defined in {@link #to(Object, Class)}.
     * The order of the values in the generated list matches the order of the supplied {@code values}
     * @param values        Values to convert, which my contain {@code null}
     * @param type          Type of elements the generated list
     * @param <T>           Type of elements the generated list
     * @return              An immutable list of converted values
     * @throws IllegalArgumentException if one of more {@code values} could not be converted
     */
    @Nonnull
    public static <T> List<T> toList(@Nonnull Collection<?> values,
                                     @Nonnull Class<T> type) throws IllegalArgumentException {
        List<T> result = values.stream()
                .map(value -> to(value, type))
                .collect(Collectors.toCollection(ArrayList::new));

        // We can't use Collectors.toUnmodifiableList() as this would fail for null elements
        return Collections.unmodifiableList(result);
    }


    /**
     * Convert a single value to another type in a flexible way. The conversion rules are:
     * <ul>
     *     <li>A {@code null} value is always converted to {@code null}</li>
     *     <li>Any type can be converted to its super type</li>
     *     <li>All values can be converted to a String using its {@link Object#toString()} method</li>
     *     <li>A value be converted to an enumeration constant by matching its {@link Object#toString()} against the
     *          constant name after converting spaces to underscores and ignoring case</li>
     *     <li>A value can be converted to a Boolean using the rules described in {@link #toBoolean(Object)}</li>
     *     <li>Booleans can be converted to Numbers using 'C'-like rules; {@literal 0} represents {@literal false} and
     *          {@literal 1} represents {@literal true}</li>
     *     <li>Strings can be converted to Numeric types using their associated parse function. </li>
     * </ul>
     * The following numeric types are supported: {@link Number}, {@link Byte}, {@link Short}, {@link Integer},
     * {@link Long}, {@link Float}, {@link Double}, {@link java.math.BigInteger}, {@link java.math.BigDecimal}
     * @param value         Value to convert
     * @param type          Type of files in the generated list
     * @param <T>           Type of files in the generated list
     * @return              A list of converted values
     * @throws IllegalArgumentException if one of more {@code values} could not be converted
     */
    @Nullable
    public static <T> T to(@Nullable Object value, @Nonnull Class<T> type) throws IllegalArgumentException {
        Object result;

        type = (Class<T>) BOXED_TYPES.getOrDefault(type, type);

        try {
            if (value == null) {
                result = null;
            } else if (type.isAssignableFrom(value.getClass())) {
                result = value;
            } else if (type == String.class) {
                result = value instanceof BigDecimal number ? number.toPlainString() : value.toString();
            } else if ((type == Boolean.class) || (type == boolean.class)) {
                result = toBoolean(value);
            } else if (Enum.class.isAssignableFrom(type)) {
                result = toEnum(value, (Class<Enum>) type);
            } else if (Number.class.isAssignableFrom(type)) {
                result = toNumber(value, (Class<? extends Number>) type);
            } else {
                result = null;
            }
        } catch (RuntimeException e) {
            result = null;
        }

        if ((value != null) && (result == null)) {
            throw new IllegalArgumentException("Can not convert '" + value + "' " +
                    "from " + value.getClass().getSimpleName() + " to " + type.getSimpleName());
        }

        return type.cast(result);
    }


    /**
     * Convert a {@code value} to its Boolean equivalent. This is done by calling {@link Object#toString()}
     * and comparing (case-insensitive) to one of the following values:
     * <ul>
     *  <li><b>false:</b>
     *      {@literal false}, {@literal 0}, {@literal off}, {@literal no}, {@literal disabled} or {@literal unset}</li>
     *  <li><b>true:</b>
     *      {@literal true}, {@literal 1}, {@literal on}, {literal yes}, {@literal enabled} or {@literal set}</li>
     * </ul>
     * @param value     Value to convert to a Boolean
     * @return          {@literal true} if the value is one of the true values, {@literal false} if the value is one
     *                  of the true values or {@literal null} if it is neither.
     */
    @Nullable
    public static Boolean toBoolean(@Nonnull Object value) {
        Boolean result;

        value = value.toString().trim().toLowerCase();

        if (FALSE_VALUES.contains(value)) {
            result = Boolean.FALSE;
        } else if (TRUE_VALUES.contains(value)) {
            result = Boolean.TRUE;
        } else {
            result = null;
        }

        return result;
    }


    @Nullable
    private static <E extends Enum<E>> E toEnum(@Nonnull Object value, @Nonnull Class<E> type) {
        E result;

        try {
            result = Enums.toEnum(type, value.toString());
        } catch (RuntimeException e) {
            result = null;
        }

        return result;
    }


    @Nullable
    private static <N extends Number> N toNumber(@Nonnull Object value, @Nonnull Class<N> type) {
        N result;

        if (value instanceof String) {
            result = toNumber((String) value, type);
        } else if (value instanceof Number) {
            result = toNumber((Number) value, type);
        } else if (value instanceof Boolean) {
            result = toNumber((Boolean) value, type);
        } else {
            result = null;
        }

        return result;
    }


    @Nullable
    private static <N extends Number> N toNumber(@Nonnull String value, @Nonnull Class<N> type)
            throws ClassCastException, NumberFormatException {
        Object result;

        value = value.trim();

        if (type == Byte.class) {
            result = Byte.parseByte(value);
        } else if (type == Short.class) {
            result = Short.parseShort(value);
        } else if (type == Integer.class) {
            result = Integer.parseInt(value);
        } else if (type == Long.class) {
            result = Long.parseLong(value);
        } else if (type == Float.class) {
            result = Float.parseFloat(value);
        } else if (type == Double.class) {
            result = Double.parseDouble(value);
        } else if (type == BigDecimal.class) {
            result = new BigDecimal(value);
        } else if (type == BigInteger.class) {
            result = new BigInteger(value);
        } else {
            result = null;
        }

        return type.cast(result);
    }


    @Nullable
    private static <N extends Number> N toNumber(@Nonnull Number value, @Nonnull Class<N> type)
            throws ClassCastException, NumberFormatException {
        Object result;

        if (type == value.getClass()) {
            result = value;
        } else if (type == Byte.class) {
            result = value.byteValue();
        } else if (type == Short.class) {
            result = value.shortValue();
        } else if (type == Integer.class) {
            result = value.intValue();
        } else if (type == Long.class) {
            result = value.longValue();
        } else if (type == Float.class) {
            result = value.floatValue();
        } else if (type == Double.class) {
            result = value.doubleValue();
        } else if (type == BigDecimal.class) {
            result = new BigDecimal(value.toString());
        } else if (type == BigInteger.class) {
            result = toNumber(value, BigDecimal.class).toBigInteger();
        } else {
            result = null;
        }

        return type.cast(result);
    }


    @Nullable
    private static <N extends Number> N toNumber(@Nonnull Boolean value,
                                                 @Nonnull Class<N> type)
            throws ClassCastException, NumberFormatException {
        return toNumber((value ? 1 : 0), type);
    }
}
