package com.github.tymefly.common.document.parse;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;

import javax.annotation.Nonnull;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.github.tymefly.common.base.io.FailedIoException;
import com.github.tymefly.common.document.CommonDocument;
import com.github.tymefly.common.document.Document;
import com.github.tymefly.common.document.DocumentException;
import com.github.tymefly.common.document.WritableDocument;
import com.github.tymefly.common.document.key.DocumentKey;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Class used to parse XML into a Document.
 * This is package-protected to so that implementation details do not become part of the public
 * API of {@link XmlParser}.
 * @see <a href="https://github.com/tetsujin1979/EnumParser">Git Hub</a>
 */
class XmlHandler extends DefaultHandler {
    private static final CommonDocument EMPTY = Document.empty();

    private final Deque<WritableDocument<?>> docs;
    private final WritableDocument<?> target;

    private String characters;

    XmlHandler(@Nonnull WritableDocument<?> target) {
        this.docs = new ArrayDeque<>();
        this.target = target;
    }


    void load(@Nonnull InputStream source) throws FailedIoException, DocumentException {
        try {
            SAXParser parser = SAXParserFactory.newInstance()
                .newSAXParser();

            parser.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            parser.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            parser.parse(source, this);
        } catch (IOException e) {
            throw new FailedIoException("Failed to read XML", e);
        } catch (RuntimeException | ParserConfigurationException | SAXException e) {
            throw new DocumentException("Failed to parse XML", e);
        }
    }


    @Override
    public void startElement(@Nonnull String namespaceURI,
                             @Nonnull String lName,
                             @Nonnull String qName,
                             @Nonnull Attributes attributes) {
        WritableDocument<?> child = docs.isEmpty() ? target : Document.newInstance();
        int index = attributes.getLength();

        while (index-- != 0) {
            String name = attributes.getLocalName(index);
            String value = attributes.getValue(index);

            child.addString(() -> name, value);
        }

        docs.push(child);
    }


    @Override
    public void endElement(@Nonnull String namespaceURI, @Nonnull String sName, @Nonnull String qName) {
        WritableDocument<?> doc = docs.pop();
        WritableDocument<?> parent = (docs.isEmpty() ? target : docs.peek());
        DocumentKey key = () -> qName;

        if (!characters.isEmpty()) {
            parent.appendString(key, characters);
        }

        if (!docs.isEmpty() && !doc.equals(EMPTY)) {
            parent.appendDocument(key, doc);
        }
    }


    @Override
    public void characters(char[] buf, int offset, int len) {
        this.characters = new String(buf, offset, len).trim();
    }
}
