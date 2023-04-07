package com.github.tymefly.common.document.visitor.serializer.xml;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;

/**
 * Document Visitor that will generate an XML string with new lines and indents
 */
@NotThreadSafe
public non-sealed class PrettyXmlSerializer extends AbstractXmlSerializer implements AutoCloseable {
    @Override
    void format(@Nonnull Transformer transformer) {
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    }
}
