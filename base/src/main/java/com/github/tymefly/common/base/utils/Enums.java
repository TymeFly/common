package com.github.tymefly.common.base.utils;

import java.util.EnumSet;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.tymefly.common.base.validate.Preconditions;

/**
 * Utility functions for enumeration types.
 */
public class Enums {
    private Enums() {
    }


    /**
     * Convert an enumeration name to its enumerated value with more relaxed rules then
     * {@link Enum#valueOf(Class, String)}. The name is cleaned up and the case is ignored.
     * @param type      The type of the enumerated constant
     * @param name      case-insensitive name of a constant that is of the required {@code type}
     * @param <T>       The type of the enumerated constant
     * @return          An enumeration constant
     * @throws IllegalArgumentException if one or more names do not match one of the enumerated constants
     * @see #cleanName(String)
     */
    @Nonnull
    public static <T extends Enum<T>> T toEnum(@Nonnull Class<T> type, @Nonnull String name) {
        T result = safeToEnum(type, name);

        Preconditions.checkArgument((result != null), "Invalid constant in %s '%s'", type.getSimpleName(), name);

        return result;
    }


    /**
     * Convert a list of enumeration names to their enumerated values using the same rules as
     * {@link #toEnum(Class, String)}
     * @param type       The type of the enumerated constants
     * @param names      case-insensitive list constant names that are of the required {@code type}
     * @param <T>        The type of the enumerated constants
     * @return           A list of enumerated constants
     * @throws IllegalArgumentException if one or more names do not match one of the enumerated constants
     * @see #toEnum(Class, String)
     */
    @Nonnull
    public static <T extends Enum<T>> EnumSet<T> toEnums(@Nonnull Class<T> type,
                @Nonnull List<String> names) throws IllegalArgumentException {
        EnumSet<T> result = EnumSet.noneOf(type);

        for (String description : names) {
            T element = toEnum(type, description);

            result.add(element);
        }

        return result;
    }


    /**
     * Convert an enumeration names to their enumerated values using the same rules as
     * {@link #toEnum(Class, String)}, but return {@literal null} if the constant was not found
     * @param <T>       The type of the enumerated constant
     * @param type      The type of the enumerated constant
     * @param name      case-insensitive name of a constant that is of the required {@code type}
     * @return          An enumeration constant or {@literal null}
     * @see #toEnum(Class, String)
     */
    @Nullable
    public static <T extends Enum<T>> T safeToEnum(@Nonnull Class<T> type, @Nonnull String name) {
        T found = null;

        name = cleanName(name);

        for (var test : type.getEnumConstants()) {
            if (name.equalsIgnoreCase(test.name())) {
                found = test;
                break;
            }
        }

        return found;
    }

    
    /**
     * Clean up an enumeration constant name.
     * <ul>
     *  <li>Leading and trailing white space are ignored</li>
     *  <li>White spaces in the {@code name} are converted to an underscore</li>
     * </ul>
     * @param name      Name of an enumeration
     * @return          A cleaned up version of the enumeration name
     */
    @Nonnull
    public static String cleanName(@Nonnull String name) {
        return name.trim()
            .replaceAll(" +", "_");
    }
}
