package com.github.tymefly.common.document.visitor.serializer.xml;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.transform.Transformer;

/**
 * Document Visitor that will generate a single line of XML
 */
@NotThreadSafe
public non-sealed class XmlSerializer extends AbstractXmlSerializer implements AutoCloseable {
    @Override
    void format(@Nonnull Transformer transformer) {
    }
}
