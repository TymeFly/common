package com.github.tymefly.common.document.visitor.serializer.collection;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import com.github.tymefly.common.document.Document;
import com.github.tymefly.common.document.key.LayeredDocumentKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test {@link PropertySerializer}
 */
public class PropertySerializerTest {
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
        ROOT_DOCS,
        X
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
                .addString(Key.X, "x")
                .addString(Key.ROOT_CHILD_STRING, "String")
                .addNumber(Key.ROOT_CHILD_NUMBER, 123)
                .addBoolean(Key.ROOT_CHILD_BOOLEAN, true)
                .addEnum(Key.ROOT_CHILD_ENUM, Value.A)
                .addStrings(Key.ROOT_STRINGS, "One", null, "Three", "")
                .addNumbers(Key.ROOT_NUMBERS, 1, null, 3.4)
                .addBooleans(Key.ROOT_BOOLEANS, true, null, false)
                .addEnums(Key.ROOT_ENUMS, Value.B, null, Value.C)
                .addDocuments(Key.ROOT_DOCS,
                        Document.newInstance().addString(Nested.ONE, "One"),
                        Document.newInstance().addString(Nested.TWO, null)
                                     .addStrings(Nested.DATA, "x", "y", "z"),
                        Document.newInstance().addString(Nested.THREE, "Three")
                                     .addStrings(Nested.DATA, (String) null))
        ;
    }


    /**
     * Unit test {@link PropertySerializer}
     */
    @Test
    public void test_PropertySerializer() {
        Properties actual = source.accept(new PropertySerializer());

        Properties expected = new Properties();
        expected.put("x", "x");
        expected.put("root.child.string", "String");
        expected.put("root.child.number", "123");
        expected.put("root.child.boolean", "true");
        expected.put("root.child.enum", "A");
        expected.put("root.strings[0]", "One");
        expected.put("root.strings[1]", "");
        expected.put("root.strings[2]", "Three");
        expected.put("root.strings[3]", "");
        expected.put("root.numbers[0]", "1");
        expected.put("root.numbers[1]", "");
        expected.put("root.numbers[2]", "3.4");
        expected.put("root.booleans[0]", "true");
        expected.put("root.booleans[1]", "");
        expected.put("root.booleans[2]", "false");
        expected.put("root.enums[0]", "B");
        expected.put("root.enums[1]", "");
        expected.put("root.enums[2]", "C");
        expected.put("root.docs[0].one", "One");
        expected.put("root.docs[1].two", "");
        expected.put("root.docs[1].data[0]", "x");
        expected.put("root.docs[1].data[1]", "y");
        expected.put("root.docs[1].data[2]", "z");
        expected.put("root.docs[2].three", "Three");
        expected.put("root.docs[2].data[0]", "");

        List<String> expectedKeys = expected.keySet()
                .stream()
                .map(Object::toString)
                .sorted()
                .collect(Collectors.toList());
        List<String> actualKeys = actual.keySet()
                .stream()
                .map(Object::toString)
                .sorted()
                .collect(Collectors.toList());

        Assert.assertEquals("Unexpected Keys", expectedKeys, actualKeys);       // To help identify errors
        Assert.assertEquals("Unexpected Properties", expected, actual);
    }
}