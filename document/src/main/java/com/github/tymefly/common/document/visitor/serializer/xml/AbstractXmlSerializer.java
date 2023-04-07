package com.github.tymefly.common.document.visitor.serializer.xml;

import java.io.StringReader;
import java.io.StringWriter;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.github.tymefly.common.document.DocumentException;
import com.github.tymefly.common.document.visitor.DocumentVisitor;
import com.github.tymefly.common.document.visitor.VisitorKey;


/**
 * Base class for XML serializers
 */
abstract sealed class AbstractXmlSerializer
        implements DocumentVisitor<String>, AutoCloseable
        permits PrettyXmlSerializer, XmlSerializer  {
    private final StringWriter result;
    private final XMLStreamWriter writer;


    AbstractXmlSerializer() {
        try {
            result = new StringWriter();
            writer = XMLOutputFactory.newInstance().createXMLStreamWriter(result);

            writer.writeStartDocument();
        } catch (XMLStreamException e) {
            throw new DocumentException("Failed to create XML Serializer", e);
        }
    }

    /**
     * Allow subclasses to configure the output transformer
     * @param transformer   Transformer that needs to be configured
     */
    abstract void format(@Nonnull Transformer transformer);


    @Nonnull
    @Override
    public DocumentVisitor<String> nullValue(@Nonnull VisitorKey key) {
        try {
            writer.writeEmptyElement(key.simpleKey());
        } catch (XMLStreamException e) {
            throw new DocumentException("Failed to write " + key.fullPath(), e);
        }

        return this;
    }

    @Nonnull
    @Override
    public DocumentVisitor<String> stringValue(@Nonnull VisitorKey key, @Nonnull String value) {
        try {
            writer.writeStartElement(key.simpleKey());
            writer.writeCharacters(value);
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new DocumentException("Failed to write " + key.fullPath(), e);
        }

        return this;
    }

    @Nonnull
    @Override
    public DocumentVisitor<String> numericValue(@Nonnull VisitorKey key, @Nonnull Number value) {
        return stringValue(key, value.toString());
    }

    @Nonnull
    @Override
    public DocumentVisitor<String> booleanValue(@Nonnull VisitorKey key, boolean value) {
        return stringValue(key, Boolean.valueOf(value).toString());
    }

    @Nonnull
    @Override
    public DocumentVisitor<String> enumValue(@Nonnull VisitorKey key, @Nonnull Enum<?> value) {
        return stringValue(key, value.toString());
    }

    @Nonnull
    @Override
    public DocumentVisitor<String> beginChild(@Nonnull VisitorKey key) {
        try {
            writer.writeStartElement(key.simpleKey());
        } catch (XMLStreamException e) {
            throw new DocumentException("Failed to write " + key.fullPath(), e);
        }
        return this;
    }

    @Nonnull
    @Override
    public DocumentVisitor<String> endChild(@Nonnull VisitorKey key) {
        try {
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new DocumentException("Failed to write " + key.fullPath(), e);
        }

        return this;
    }

    @Nonnull
    @Override
    public DocumentVisitor<String> beginSequence(@Nonnull VisitorKey key, @Nonnull Class<?> type, int size) {
        return this;

    }

    @Nonnull
    @Override
    public DocumentVisitor<String> endSequence(@Nonnull VisitorKey key) {
        return this;
    }

    @Nonnull
    @Override
    public String process() {
        try {
            writer.writeEndDocument();
        } catch (XMLStreamException e) {
            throw new DocumentException("Failed to write XML", e);
        }

        return format(result.toString());
    }


    @Override
    public void close() throws Exception {
        writer.close();
    }


    @Nonnull
    private String format(@Nonnull String xml) {
        String formatted;

        try {
            StringWriter writer = new StringWriter();
            Result result = new StreamResult(writer);
            Source source = new StreamSource(new StringReader(xml));
            Transformer transformer = TransformerFactory.newInstance().newTransformer();

            format(transformer);

            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(source, result);

            formatted = writer.toString();
        } catch (TransformerException e) {
            throw new DocumentException("Failed to format XML", e);
        }

        return formatted;
    }
}
