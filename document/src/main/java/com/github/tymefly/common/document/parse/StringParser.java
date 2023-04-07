package com.github.tymefly.common.document.parse;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import com.github.tymefly.common.base.io.FailedIoException;
import com.github.tymefly.common.document.DocumentException;
import com.github.tymefly.common.document.WritableDocument;
import com.github.tymefly.common.document.key.DocumentKey;

/**
 * Read a sequence of semi-colon separated {@literal name:value} pairs into a Document.
 * This parser will try to determine the type of the data:
 * <ul>
 *     <li>the literal {@literal null} (ignoring case) is a null value</li>
 *     <li>the literal {@literal true} (ignoring case) is the boolean value {@literal true}</li>
 *     <li>the literal {@literal false} (ignoring case) is the boolean value {@literal false}</li>
 *     <li>values that are digits with an optional leading minus and contain an optional dot are numerics}</li>
 *     <li>values that start and end with double quotes are strings</li>
 *     <li>all other values, including empty values, are strings</li>
 * </ul>
 * Spaces surrounding keys and values are ignored.
 */
public class StringParser implements DocumentParser {
    private static final Pattern DELIMITER = Pattern.compile(" *; *");
    private static final String SEPARATOR = " *: *";
    private static final String ENTRY = " *" + DocumentKey.FULL_PATH + SEPARATOR + "[^;]*";
    private static final Pattern ENTRY_PATTERN = Pattern.compile(ENTRY);
    private static final String NUMERIC = "[+-]?([0-9]*[.])?[0-9]+";


    @Override
    public void load(@Nonnull WritableDocument<?> target,
                     @Nonnull InputStream source) throws FailedIoException, DocumentException {
        Scanner scanner = new Scanner(source, StandardCharsets.UTF_8)
            .useDelimiter(DELIMITER);

        while (scanner.hasNext(ENTRY_PATTERN)) {
            String entry = scanner.next(ENTRY_PATTERN);

            parse(target, entry);
        }

        if (scanner.hasNext()) {
            throw new DocumentException("Malformed data in Document input stream");
        }
    }


    private void parse(@Nonnull WritableDocument<?> target, @Nonnull String entry) {
        String[] parts = entry.trim().split(SEPARATOR, 2);      // Format of ENTRY guarantees a key and value
        DocumentKey key = () -> parts[0];
        String value = parts[1];

        if ("null".equalsIgnoreCase(value)) {
            target.addString(key, null);
        } else if ("true".equalsIgnoreCase(value)) {
            target.addBoolean(key, true);
        } else if ("false".equalsIgnoreCase(value)) {
            target.addBoolean(key, false);
        } else if (value.matches(NUMERIC)) {
            target.addNumber(key, new BigDecimal(value));
        } else if ((value.length() > 1) && value.startsWith("\"") && value.endsWith("\"")) {
            target.addString(key, value.substring(1, value.length() - 1));
        } else {
            target.addString(key, value);
        }
    }
}
