package com.github.tymefly.common.document.visitor.serializer.json;

import java.util.List;

import com.github.tymefly.common.document.Document;
import com.github.tymefly.common.document.key.LayeredDocumentKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link JsonSerializer}
 */
public class JsonSerializerTest {
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
     * Unit test {@link JsonSerializer}
     */
    @Test
    public void test_Json() {
        String actual = source.accept(new JsonSerializer());

        Assert.assertEquals("Unexpected Json",
                "{\"root\":{\"child\":{\"string\":\"String\",\"number\":123,\"boolean\":true,\"enum\":\"A\"},\"strings\":[\"One\",null,\"Three\"],\"numbers\":[1,null,3.4],\"booleans\":[true,null,false],\"enums\":[\"B\",null,\"C\"],\"docs\":[{\"one\":\"One\"},{\"two\":null,\"data\":[\"x\",\"y\",\"z\"]},{\"three\":\"Three\",\"data\":null}]}}",
                actual);
    }
}