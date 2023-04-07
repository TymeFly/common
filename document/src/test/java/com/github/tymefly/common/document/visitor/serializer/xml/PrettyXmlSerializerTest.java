package com.github.tymefly.common.document.visitor.serializer.xml;

import java.util.List;

import com.github.tymefly.common.document.Document;
import com.github.tymefly.common.document.key.LayeredDocumentKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link XmlSerializer}
 */
public class PrettyXmlSerializerTest {
    private enum Value {
        A, B, C
    }

    private enum Key implements LayeredDocumentKey {
        ROOT_CHILD_STRING,
        ROOT_CHILD_NUMBER,
        ROOT_CHILD_BOOLEAN,
        ROOT_CHILD_ENUM,
        ROOT_STRINGS,
        ROOT_NUMBERS,
        ROOT_BOOLEANS,
        ROOT_ENUMS,
        ROOT_DOCS
    }

    private enum Nested implements LayeredDocumentKey {
        ONE,
        TWO,
        THREE,
        DATA
    }


    private Document source;

    @Before
    public void setUp() {
        source = Document.newInstance()
                .addString(Key.ROOT_CHILD_STRING, "String")
                .addNumber(Key.ROOT_CHILD_NUMBER, 123)
                .addBoolean(Key.ROOT_CHILD_BOOLEAN, true)
                .addEnum(Key.ROOT_CHILD_ENUM, Value.A)
                .addStrings(Key.ROOT_STRINGS, "One", null, "Three")
                .addNumbers(Key.ROOT_NUMBERS, 1, null, 3.4)
                .addBooleans(Key.ROOT_BOOLEANS, true, null, false)
                .addEnums(Key.ROOT_ENUMS, Value.B, null, Value.C)
                .addDocuments(Key.ROOT_DOCS,
                        Document.newInstance().addString(Nested.ONE, "One"),
                        Document.newInstance().addString(Nested.TWO, null)
                                .addStrings(Nested.DATA, "x", "y", "z"),
                        Document.newInstance().addString(Nested.THREE, "Three")
                                .addStrings(Nested.DATA, (List<String>) null));
    }


    /**
     * Unit test {@link PrettyXmlSerializer}
     */
    @Test
    public void test_Xml() {
        String actual = source.accept(new PrettyXmlSerializer());

        Assert.assertEquals("Unexpected XML",
            """
                <root>
                    <child>
                        <string>String</string>
                        <number>123</number>
                        <boolean>true</boolean>
                        <enum>A</enum>
                    </child>
                    <strings>One</strings>
                    <strings/>
                    <strings>Three</strings>
                    <numbers>1</numbers>
                    <numbers/>
                    <numbers>3.4</numbers>
                    <booleans>true</booleans>
                    <booleans/>
                    <booleans>false</booleans>
                    <enums>B</enums>
                    <enums/>
                    <enums>C</enums>
                    <docs>
                        <one>One</one>
                    </docs>
                    <docs>
                        <two/>
                        <data>x</data>
                        <data>y</data>
                        <data>z</data>
                    </docs>
                    <docs>
                        <three>Three</three>
                        <data/>
                    </docs>
                </root>
                """,
            actual.replaceAll("\r", ""));
    }
}