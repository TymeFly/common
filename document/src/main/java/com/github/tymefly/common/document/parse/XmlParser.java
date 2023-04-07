package com.github.tymefly.common.document.parse;

import java.io.InputStream;

import javax.annotation.Nonnull;

import com.github.tymefly.common.base.io.FailedIoException;
import com.github.tymefly.common.document.DocumentException;
import com.github.tymefly.common.document.WritableDocument;


/**
 * Read some XML into a Document. Note that:
 * <ul>
 *  <li>The root element of the XML is the target document</li>
 *  <li>Attributes are stored by name as strings</li>
 *  <li>Empty XML elements are ignored</li>
 *  <li>XML elements that only contain text are stored in the parent document as string sequences</li>
 *  <li>XML elements that have data (child elements or attributes) are stored in the parent document
 *      as Document sequences</li>
 * </ul>
 */
public class XmlParser implements DocumentParser {
    @Override
    public void load(@Nonnull WritableDocument<?> target,
                     @Nonnull InputStream source) throws FailedIoException, DocumentException {
        new XmlHandler(target)
            .load(source);
    }
}