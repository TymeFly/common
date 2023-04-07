package com.github.tymefly.common.document.visitor.white;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import com.github.tymefly.common.document.Document;
import com.github.tymefly.common.document.key.DocumentKey;
import com.github.tymefly.common.document.key.LayeredDocumentKey;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link WhiteList}
 */
public class WhileListTest {
    private enum Value { ONE, TWO, THREE }

    private enum Key implements LayeredDocumentKey {
        Root,
        Child_Unexpected,
        Child_NullValue,
        Child_Cardinal,
        Child_CardinalArray,
        Child_CardinalArray2,
        Child_Integer,
        Child_IntegerArray,
        Child_IntegerArray2,
        Child_Decimal,
        Child_DecimalArray,
        Child_DecimalArray2,
        Child_StringArray,
        Child_Boolean1,
        Child_Boolean2,
        Child_Enum1,
        Child_Enum2,
        Child_Enum3,
        Child_Array
    }

    private enum NestedKey implements LayeredDocumentKey {
        Value1,
        A_Value2,
        B_Value3
    }

    private final Document test = Document.newInstance()
        .addString(Key.Root, "abcd")
        .addString(Key.Child_NullValue, null)
        .addString(Key.Child_Unexpected, "IgnoreMe")
        .addNumber(Key.Child_Cardinal, 1)
        .addNumbers(Key.Child_CardinalArray, null, 0, 1, 2, 3.4, -5)
        .addNumbers(Key.Child_CardinalArray2, 0, 1, 2, 3.4, -5)
        .addNumber(Key.Child_Integer, -1)
        .addNumbers(Key.Child_IntegerArray, null, -3, -2, -1, 0, 1, 2, 3, 4, 5.6)
        .addNumbers(Key.Child_IntegerArray2, -3, -2, -1, 0, 1, 2, 3, 4, 5.6)
        .addNumber(Key.Child_Decimal, -1.2)
        .addNumbers(Key.Child_DecimalArray, null, -3.4, -2, -1, 0, 1, 2, 3.4)
        .addNumbers(Key.Child_DecimalArray2, -3, -2, -1, 0, 1, 2, 3.4)
        .addStrings(Key.Child_StringArray, null, "a", "_b", "abc", "abcd", "abcde", "abcdef", "abcdefg", "")
        .addBoolean(Key.Child_Boolean1, false)
        .addBoolean(Key.Child_Boolean2, true)
        .addEnum(Key.Child_Enum1, Value.ONE)
        .addString(Key.Child_Enum2, Value.TWO.toString())
        .addEnum(Key.Child_Enum3, Value.THREE);


    /**
     * Unit test {@link WhiteList}
     */
    @Test
    public void test_ExpectedString() {
        Document expected = Document.newInstance()
            .addString(Key.Root, "abcd");
        WhiteList whiteList = new WhiteList.Builder()
            .forRegEx(Key.Root, "[a-z]+")
            .build();
        Document actual = test.accept(whiteList);

        Assert.assertEquals("Failed to clean Document", expected, actual);
    }


    /**
     * Unit test {@link WhiteList}
     */
    @Test
    public void test_IncludeNull() {
        Document expected = Document.newInstance()
            .addString(Key.Child_NullValue, null);
        WhiteList whiteList = new WhiteList.Builder()
            .allowNull().forRegEx(Key.Child_NullValue, "[a-z]+")
            .build();
        Document actual = test.accept(whiteList);

        Assert.assertEquals("Failed to clean Document", expected, actual);
    }


    /**
     * Unit test {@link WhiteList}
     */
    @Test
    public void test_StringLength_Max() {
        Document expected = Document.newInstance()
                .addString(Key.Root, "abcd")
                .addNumber(Key.Child_Decimal, -1.2)
                .addBoolean(Key.Child_Boolean2, true)
                .addEnum(Key.Child_Enum1, Value.ONE)
                .addString(Key.Child_Enum2, Value.TWO.toString())
                .addString(Key.Child_NullValue, null);
        WhiteList whiteList = new WhiteList.Builder()
                .allowNull().forLength(Key.Root, 4)
                .allowNull().forLength(Key.Child_Decimal, 4)
                .allowNull().forLength(Key.Child_Boolean1, 4)
                .allowNull().forLength(Key.Child_Boolean2, 4)
                .allowNull().forLength(Key.Child_Enum1, 4)
                .allowNull().forLength(Key.Child_Enum2, 4)
                .allowNull().forLength(Key.Child_Enum3, 4)
                .allowNull().forLength(Key.Child_NullValue, 4)
                .build();
        Document actual = test.accept(whiteList);

        Assert.assertEquals("Failed to clean Document", expected, actual);
    }


    /**
     * Unit test {@link WhiteList}
     */
    @Test
    public void test_StringLength_Range() {
        Document expected = Document.newInstance()
                .addString(Key.Root, "abcd")
                .addBoolean(Key.Child_Boolean2, true)
                .addEnum(Key.Child_Enum1, Value.ONE)
                .addString(Key.Child_Enum2, Value.TWO.toString())
                .addString(Key.Child_NullValue, null);
        WhiteList whiteList = new WhiteList.Builder()
                .allowNull().forLength(Key.Root, 3, 4)
                .allowNull().forLength(Key.Child_Decimal, 1, 2)
                .allowNull().forLength(Key.Child_Boolean1, 3, 4)
                .allowNull().forLength(Key.Child_Boolean2, 3, 4)
                .allowNull().forLength(Key.Child_Enum1, 3, 4)
                .allowNull().forLength(Key.Child_Enum2, 3, 4)
                .allowNull().forLength(Key.Child_Enum3, 3, 4)
                .allowNull().forLength(Key.Child_NullValue, 4)
                .build();
        Document actual = test.accept(whiteList);

        Assert.assertEquals("Failed to clean Document", expected, actual);
    }


    /**
     * Unit test {@link WhiteList}.
     * Multiple tests on the same data
     */
    @Test
    public void test_RegRexAndStringLength() {
        Document expected = Document.newInstance()
                .addStrings(Key.Child_StringArray, null, "a", null, "abc", null, null, null, null, "");
        WhiteList whiteList = new WhiteList.Builder()
                .allowNull().forLength(Key.Child_StringArray, 3)                // Remove the long strings
                .allowNull().forRegEx(Key.Child_StringArray, "[a-z]*")          // Remove the string "_b"
                .build();
        Document actual = test.accept(whiteList);

        Assert.assertEquals("Failed to clean Document", expected, actual);
    }


    /**
     * Unit test {@link WhiteList}
     */
    @Test
    public void test_ExpectedDecimal() {
        Document expected = Document.newInstance()
                .addNumber(Key.Child_Decimal, -1.2);
        WhiteList whiteList = new WhiteList.Builder()
                .forDecimal(Key.Child_Decimal)
                .build();
        Document actual = test.accept(whiteList);

        Assert.assertEquals("Failed to clean Document", expected, actual);
    }


    /**
     * Unit test {@link WhiteList}
     */
    @Test
    public void test_ExpectedDecimal_Nullable() {
        Document expected = Document.newInstance()
                .addNumbers(Key.Child_DecimalArray, null, -3.4, -2, -1 ,0, 1, 2, 3.4);
        WhiteList whiteList = new WhiteList.Builder()
                .allowNull().forDecimal(Key.Child_DecimalArray)
                .build();
        Document actual = test.accept(whiteList);

        Assert.assertEquals("Failed to clean Document", expected, actual);         
    }


    /**
     * Unit test {@link WhiteList}
     */
    @Test
    public void test_ExpectedDecimal_Null_Hostile() {
        Document expected = Document.newInstance()
                .addNumbers(Key.Child_DecimalArray2, -3, -2, -1, 0, 1, 2, 3.4);
        WhiteList whiteList = new WhiteList.Builder()
                .forDecimal(Key.Child_DecimalArray)
                .forDecimal(Key.Child_DecimalArray2)
                .build();
        Document actual = test.accept(whiteList);

        Assert.assertEquals("Failed to clean Document", expected, actual);
    }


    /**
     * Unit test {@link WhiteList}
     */
    @Test
    public void test_ExpectedInteger() {
        Document expected = Document.newInstance()
                .addNumber(Key.Child_Integer, -1);
        WhiteList whiteList = new WhiteList.Builder()
                .forInteger(Key.Child_Integer)
                .build();
        Document actual = test.accept(whiteList);

        Assert.assertEquals("Failed to clean Document", expected, actual);
    }


    /**
     * Unit test {@link WhiteList}
     */
    @Test
    public void test_ExpectedInteger_Nullable() {
        Document expected = Document.newInstance()
                .addNumbers(Key.Child_IntegerArray, null, -3, -2, -1 ,0, 1, 2, 3, 4);
        WhiteList whiteList = new WhiteList.Builder()
                .allowNull().forInteger(Key.Child_IntegerArray)
                .build();
        Document actual = test.accept(whiteList);

        Assert.assertEquals("Failed to clean Document", expected, actual);
    }


    /**
     * Unit test {@link WhiteList}
     */
    @Test
    public void test_ExpectedInteger_Null_Hostile() {
        Document expected = Document.newInstance()
                .addNumbers(Key.Child_IntegerArray2, -3, -2, -1, 0, 1, 2, 3, 4);
        WhiteList whiteList = new WhiteList.Builder()
                .forInteger(Key.Child_IntegerArray)
                .forInteger(Key.Child_IntegerArray2)
                .build();
        Document actual = test.accept(whiteList);

        Assert.assertEquals("Failed to clean Document", expected, actual);
    }


    /**
     * Unit test {@link WhiteList}
     */
    @Test
    public void test_ExpectedCardinal() {
        Document expected = Document.newInstance()
                .addNumber(Key.Child_Cardinal, 1);
        WhiteList whiteList = new WhiteList.Builder()
                .forCardinal(Key.Child_Cardinal)
                .build();
        Document actual = test.accept(whiteList);

        Assert.assertEquals("Failed to clean Document", expected, actual);
    }


    /**
     * Unit test {@link WhiteList}
     */
    @Test
    public void test_ExpectedCardinal_Nullable() {
        Document expected = Document.newInstance()
                .addNumbers(Key.Child_CardinalArray, null, 0, 1, 2);
        WhiteList whiteList = new WhiteList.Builder()
                .allowNull().forCardinal(Key.Child_CardinalArray)
                .build();
        Document actual = test.accept(whiteList);

        Assert.assertEquals("Failed to clean Document", expected, actual);
    }


    /**
     * Unit test {@link WhiteList}
     */
    @Test
    public void test_ExpectedCardinal_Null_Hostile() {
        Document expected = Document.newInstance()
                .addNumbers(Key.Child_CardinalArray2, 0, 1, 2);
        WhiteList whiteList = new WhiteList.Builder()
                .forCardinal(Key.Child_CardinalArray)
                .forCardinal(Key.Child_CardinalArray2)
                .build();
        Document actual = test.accept(whiteList);

        Assert.assertEquals("Failed to clean Document", expected, actual);
    }

    /**
     * Unit test {@link WhiteList}
     */
    @Test
    public void test_ExpectedRange() {
        Document expected = Document.newInstance()
                .addNumber(Key.Child_Decimal, -1.2)
                .addNumber(Key.Child_NullValue, null);
        WhiteList whiteList = new WhiteList.Builder()
                .allowNull().forRange(Key.Child_Decimal, -1, 1)
                .allowNull().forRange(Key.Child_NullValue, -1, 1)
                .build();
        Document actual = test.accept(whiteList);

        Assert.assertEquals("Failed to clean Document", expected, actual);
    }


    /**
     * Unit test {@link WhiteList}
     */
    @Test
    public void test_UnexpectedRange() {
        Document expected = Document.newInstance()
                .addNumber(Key.Child_NullValue, null);
        WhiteList whiteList = new WhiteList.Builder()
                .allowNull().forRange(Key.Child_Decimal, 0, 10)
                .allowNull().forRange(Key.Child_NullValue, -1, 1)
                .build();
        Document actual = test.accept(whiteList);

        Assert.assertEquals("Failed to clean Document", expected, actual);
    }


    /**
     * Unit test {@link WhiteList}
     */
    @Test
    public void test_ExpectedBoolean() {
        Document expected = Document.newInstance()
                .addBoolean(Key.Child_Boolean1, false)
                .addBoolean(Key.Child_Boolean2, true)
                .addNumber(Key.Child_NullValue, null);
        WhiteList whiteList = new WhiteList.Builder()
                .allowNull().forBoolean(Key.Child_Boolean1)
                .allowNull().forBoolean(Key.Child_Boolean2)
                .allowNull().forBoolean(Key.Child_NullValue)
                .build();
        Document actual = test.accept(whiteList);

        Assert.assertEquals("Failed to clean Document", expected, actual);
    }

    /**
     * Unit test {@link WhiteList}
     */
    @Test
    public void test_Enum() {
        Document expected = Document.newInstance()
                .addEnum(Key.Child_NullValue, null)
                .addEnum(Key.Child_Enum1, Value.ONE)
                .addString(Key.Child_Enum2, Value.TWO.toString());
        WhiteList whiteList = new WhiteList.Builder()
                .allowNull().forEnum(Key.Child_Enum1, Value.class)
                .allowNull().forEnum(Key.Child_Enum2, Value.class)
                .allowNull().forEnum(Key.Child_Unexpected, Value.class)                     // Not a valid Enum
                .allowNull().forEnum(Key.Child_NullValue, Value.class)
                .build();
        Document actual = test.accept(whiteList);

        Assert.assertEquals("Failed to clean Document", expected, actual);
    }


    /**
     * Unit test {@link WhiteList}
     */
    @Test
    public void test_Predicate() {
        Document expected = Document.newInstance()
                .addEnum(Key.Child_NullValue, null)
                .addEnum(Key.Child_Enum1, Value.ONE)
                .addString(Key.Child_Enum2, Value.TWO.toString());
        Predicate<Object> check = (v -> v.toString().length() == 3);
        WhiteList whiteList = new WhiteList.Builder()
                .allowNull().forCheck(Key.Root, check)
                .allowNull().forCheck(Key.Child_Enum1, check)
                .allowNull().forCheck(Key.Child_Enum2, check)
                .allowNull().forCheck(Key.Child_Unexpected, check)                     // Not a valid Enum
                .allowNull().forCheck(Key.Child_NullValue, check)
                .build();
        Document actual = test.accept(whiteList);

        Assert.assertEquals("Failed to clean Document", expected, actual);
    }


    /**
     * Unit test {@link WhiteList}
     */
    @Test
    public void test_ErrorHandler() {
        Document small = Document.newInstance()
                .addString(Key.Root, "Hello");
        WhiteList whiteList = new WhiteList.Builder()
                .forRegEx(Key.Root, "[A-Za-z]+")
                .onFail(s -> { throw new RuntimeException(s.externalise()); })
                .build();

        small.accept(whiteList);                 // No test required - it's enough to know we don't throw the exception

        small.addNumber(Key.Root, 12);

        Assert.assertThrows(RuntimeException.class, () -> small.accept(whiteList));
    }


    /**
     * Unit test {@link WhiteList}
     */
    @Test
    public void test_MultipleErrorHandlers() {
        AtomicReference<String> test1 = new AtomicReference<>("not called");
        AtomicReference<String> test2 = new AtomicReference<>("not called");
        Document small = Document.newInstance()
                .addString(Key.Root, "123");
        WhiteList whiteList = new WhiteList.Builder()
                .forRegEx(Key.Root, "[A-Za-z]+")
                .onFail(s -> test1.set("1: " + s.externalise()))
                .onFail(s -> test2.set("2: " + s.externalise()))
                .build();

        small.accept(whiteList);                 // No test required - it's enough to know we don't throw the exception

        Assert.assertEquals("Results of handler1", "1: root", test1.get());
        Assert.assertEquals("Results of handler2", "2: root", test2.get());
    }


    /**
     * Unit test {@link WhiteList}
     */
    @Test
    public void test_handleExtraFields() {
        Document small = Document.newInstance()
                .addString(Key.Root, "abc");
        WhiteList whiteList = new WhiteList.Builder()
                .forRegEx(Key.Root, "[A-Za-z]+")
                .onFail(s -> { throw new RuntimeException(s.externalise()); })
                .build();

        small.accept(whiteList);                 // No test required - it's enough to know we don't throw the exception

        small.addBoolean(Key.Child_Boolean1, false);

        Assert.assertThrows(RuntimeException.class, () -> small.accept(whiteList));
    }


    /**
     * Unit test {@link WhiteList}
     */
    @Test
    public void test_hasChildren_filter() {
        Document test = Document.newInstance()
                    .addDocuments(Key.Child_Array,
                            Document.newInstance().addString(NestedKey.Value1, "1.1")
                                                  .addString(NestedKey.A_Value2, "1.2")
                                                  .addString(NestedKey.B_Value3, "1.3"),
                            Document.newInstance().addString(NestedKey.Value1, "2.1")
                                                  .addString(NestedKey.B_Value3, "2.3"),
                            null,
                            Document.newInstance().addString(NestedKey.Value1, "4.1")
                                                  .addString(NestedKey.A_Value2, "4.2")
                                                  .addString(NestedKey.B_Value3, "4.3"));
        Document expected = Document.newInstance()
                    .addDocuments(Key.Child_Array,
                            Document.newInstance().addString(NestedKey.Value1, "1.1")
                                                  .addString(NestedKey.A_Value2, "1.2")
                                                  .addString(NestedKey.B_Value3, "1.3"),
                            null,
                            null,
                            Document.newInstance().addString(NestedKey.Value1, "4.1")
                                                  .addString(NestedKey.A_Value2, "4.2")
                                                  .addString(NestedKey.B_Value3, "4.3"));
        WhiteList whiteList = new WhiteList.Builder()
                .forChildren(Key.Child_Array, NestedKey.Value1, NestedKey.A_Value2, NestedKey.B_Value3)
                .forLength(DocumentKey.from(Key.Child_Array, NestedKey.Value1), 4)
                .forLength(DocumentKey.from(Key.Child_Array, NestedKey.A_Value2), 4)
                .forLength(DocumentKey.from(Key.Child_Array, NestedKey.B_Value3), 4)
                .build();

        Document actual = test.accept(whiteList);

        Assert.assertEquals("Failed to clean Document", expected, actual);
    }


    /**
     * Unit test {@link WhiteList}.
     * Check {@link WhiteList.Builder#forChildren(DocumentKey, DocumentKey...)} after other invalid fields have
     * been removed
     */
    @Test
    public void test_hasChildrenFilter_After_LengthChecks() {
        Document test = Document.newInstance()
                    .addDocuments(Key.Child_Array,
                            Document.newInstance().addString(NestedKey.Value1, "Child1")
                                                  .addString(NestedKey.A_Value2, "bad")
                                                  .addString(NestedKey.B_Value3, "xxxxxx"),
                            Document.newInstance().addString(NestedKey.Value1, "Child2")
                                                  .addString(NestedKey.A_Value2, "Good Value")
                                                  .addString(NestedKey.B_Value3, "xxxxx"));
        Document expected = Document.newInstance()
                    .addDocuments(Key.Child_Array,
                            null,
                            Document.newInstance().addString(NestedKey.Value1, "Child2")
                                    .addString(NestedKey.A_Value2, "Good Value")
                                    .addString(NestedKey.B_Value3, "xxxxx"));
        WhiteList whiteList = new WhiteList.Builder()
                .forChildren(Key.Child_Array, NestedKey.Value1, NestedKey.A_Value2, NestedKey.B_Value3)
                .allowNull().forLength(DocumentKey.from(Key.Child_Array, NestedKey.Value1), 4, 10)
                .allowNull().forLength(DocumentKey.from(Key.Child_Array, NestedKey.A_Value2), 4, 10)
                .allowNull().forLength(DocumentKey.from(Key.Child_Array, NestedKey.B_Value3), 4, 10)
                .build();

        Document actual = test.accept(whiteList);

        Assert.assertEquals("Failed to clean Document", expected, actual);
    }



    /**
     * Unit test {@link WhiteList}
     */
    @Test
    public void test_hasChildren_CallbackOnError() {
        Set<String> badKeys = new HashSet<>();

        Document test = Document.newInstance()
                .addDocuments(Key.Child_Array,
                        Document.newInstance().addString(NestedKey.Value1, "1.1")
                                .addString(NestedKey.A_Value2, "1.2")
                                .addString(NestedKey.B_Value3, "1.3"),
                        Document.newInstance().addString(NestedKey.Value1, "2.1")
                                .addString(NestedKey.B_Value3, "2.3"),
                        Document.newInstance().addString(NestedKey.Value1, "4.1")
                                .addString(NestedKey.A_Value2, "4.2")
                                .addString(NestedKey.B_Value3, "4.3"));
        WhiteList whiteList = new WhiteList.Builder()
                .forChildren(Key.Child_Array, NestedKey.Value1, NestedKey.A_Value2, NestedKey.B_Value3)
                .allowNull().forLength(DocumentKey.from(Key.Child_Array, NestedKey.Value1), 4)
                .allowNull().forLength(DocumentKey.from(Key.Child_Array, NestedKey.A_Value2), 4)
                .allowNull().forLength(DocumentKey.from(Key.Child_Array, NestedKey.B_Value3), 4)
                .onFail(s -> badKeys.add(s.externalise()))
                .build();

        test.accept(whiteList);

        Assert.assertEquals("Unexpected failed keys", Set.of("child.array[1].a.value2"), badKeys);
    }


    /**
     * Unit test {@link WhiteList}
     */
    @Test
    public void test_hasChildren_FailOnError() {
        Document test = Document.newInstance()
                .addDocuments(Key.Child_Array,
                        Document.newInstance().addString(NestedKey.Value1, "1.1")
                                .addString(NestedKey.A_Value2, "1.2")
                                .addString(NestedKey.B_Value3, "1.3"),
                        Document.newInstance().addString(NestedKey.Value1, "2.1")
                                .addString(NestedKey.B_Value3, "2.3"),
                        Document.newInstance().addString(NestedKey.Value1, "4.1")
                                .addString(NestedKey.A_Value2, "4.2")
                                .addString(NestedKey.B_Value3, "4.3"));
        WhiteList whiteList = new WhiteList.Builder()
                .forChildren(Key.Child_Array, NestedKey.Value1, NestedKey.A_Value2, NestedKey.B_Value3)
                .allowNull().forLength(DocumentKey.from(Key.Child_Array, NestedKey.Value1), 4)
                .allowNull().forLength(DocumentKey.from(Key.Child_Array, NestedKey.A_Value2), 4)
                .allowNull().forLength(DocumentKey.from(Key.Child_Array, NestedKey.B_Value3), 4)
                .onFail()
                .build();

        Assert.assertThrows(WhiteListException.class, () -> test.accept(whiteList));
    }

    /**
     * Unit test {@link WhiteList}
     */
    @Test
    public void test_requiredFields_hasAllFields() {
        Document test = Document.newInstance()
                .addString(Key.Root, "Hello")
                .addString(Key.Child_NullValue, null)
                .addNumbers(Key.Child_IntegerArray, 1, 2, 3);
        WhiteList whiteList = new WhiteList.Builder()
                .require().forRegEx(Key.Root, ".*")
                .require().allowNull().forRegEx(Key.Child_NullValue, ".*")
                .require().forRange(Key.Child_IntegerArray, 10)
                .onFail()
                .build();
        Document result = test.accept(whiteList);

        Assert.assertEquals("Unexpected Document returned", test, result);
    }


    /**
     * Unit test {@link WhiteList}
     */
    @Test
    public void test_requiredFields_hasUnrequiredFields() {
        Document test = Document.newInstance()
                .addString(Key.Root, "Hello")
                .addString(Key.Child_NullValue, null)
                .addNumbers(Key.Child_IntegerArray, 1, 2, 3)
                .addNumber(Key.Child_Cardinal, 20)
                .addBoolean(Key.Child_Boolean1, null);
        WhiteList whiteList = new WhiteList.Builder()
                .require().forRegEx(Key.Root, ".*")
                .require().allowNull().forRegEx(Key.Child_NullValue, ".*")
                .require().forRange(Key.Child_IntegerArray, 10)
                .forCardinal(Key.Child_Cardinal)
                .allowNull().forBoolean(Key.Child_Boolean1)
                .onFail()
                .build();
        Document result = test.accept(whiteList);

        Assert.assertEquals("Unexpected Document returned", test, result);

    }

    /**
     * Unit test {@link WhiteList}
     */
    @Test
    public void test_requiredFields_missingFields() {
        Document test = Document.newInstance()
                .addString(Key.Child_NullValue, null)
                .addNumbers(Key.Child_IntegerArray, 1, 2, 3);
        WhiteList whiteList = new WhiteList.Builder()
                .require().forRegEx(Key.Root, ".*")
                .require().allowNull().forRegEx(Key.Child_NullValue, ".*")
                .require().forRange(Key.Child_IntegerArray, 10)
                .onFail()
                .build();

        Assert.assertThrows(WhiteListException.class, () -> test.accept(whiteList));
    }

    /**
     * Unit test {@link WhiteList}
     */
    @Test
    public void test_requiredFields_missingNullFields() {
        Document test = Document.newInstance()
                .addString(Key.Root, "Hello")
                .addNumbers(Key.Child_IntegerArray, 1, 2, 3);
        WhiteList whiteList = new WhiteList.Builder()
                .require().forRegEx(Key.Root, ".*")
                .require().allowNull().forRegEx(Key.Child_NullValue, ".*")
                .require().forRange(Key.Child_IntegerArray, 10)
                .onFail()
                .build();

        Assert.assertThrows(WhiteListException.class, () -> test.accept(whiteList));
    }
}