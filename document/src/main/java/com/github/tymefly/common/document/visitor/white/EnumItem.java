package com.github.tymefly.common.document.visitor.white;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.github.tymefly.common.base.utils.Enums;
import com.github.tymefly.common.document.key.DocumentKey;

/**
 * Test that backs {@link WhiteList.Builder#forEnum(DocumentKey, Class)}
 * @param <E>   Type of the enumeration that is
 */
class EnumItem<E extends Enum<E>> extends WhiteItem {
    private final Set<String> names;

    EnumItem(@Nonnull Class<E> type, boolean allowNull) {
        super(allowNull);

        this.names = Arrays.stream(type.getEnumConstants())
            .map(e -> Enums.cleanName(e.toString().toLowerCase()))
            .collect(Collectors.toSet());
    }


    @Override
    boolean isValid(@Nonnull Object testValue) {
        String name = Enums.cleanName(testValue.toString())
                .toLowerCase();

        return names.contains(name);
    }
}
