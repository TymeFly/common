package com.github.tymefly.common.document.parse;

import java.io.InputStream;

import javax.annotation.Nonnull;

import com.github.tymefly.common.base.io.FailedIoException;
import com.github.tymefly.common.base.io.TextReader;
import com.github.tymefly.common.document.DocumentException;
import com.github.tymefly.common.document.WritableDocument;

/**
 * Read name-value pairs into a Document. All values are assumed to be Strings
 */
public class PropertiesParser implements DocumentParser {
    @Override
    public void load(@Nonnull WritableDocument<?> target,
                     @Nonnull InputStream source) throws FailedIoException, DocumentException {
        TextReader.from(source)
                .expand()
                .removeComments("#")
                .trim()
                .skipBlanks()
                .forEach((i, l) -> process(target, l));
    }


    private void process(@Nonnull WritableDocument<?> target, @Nonnull String line) {
        String[] elements = line.split(" *= *", 2);
        String key = elements[0];
        String value = (elements.length == 1 ? "" : elements[1]);

        target.addString(() -> key, value);
    }
}
