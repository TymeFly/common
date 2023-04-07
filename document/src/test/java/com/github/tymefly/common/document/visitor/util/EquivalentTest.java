package com.github.tymefly.common.document.visitor.util;

import java.math.BigDecimal;

import com.github.tymefly.common.document.Document;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link Equivalent}
 */
public class EquivalentTest {
    private enum Type { ONE, TWO, FALSE, ON }

    private Equivalent visitor;


    @Before
    public void setUp() {
        Document other = Document.newInstance();
        visitor = new Equivalent(other);
    }

    /**
     * Unit test {@link Equivalent}
     *
     */
    @Test
    public void test_HappyPath_Strings() {
        Document mixed = Document.newInstance()
            .addStrings(() -> "Str", "1", "2", "3")
            .addNumbers(() -> "Num", 1, 2, 3)
            .addBooleans(() -> "Bool", true, false, null)
            .addEnums(() -> "Enum", Type.ONE, Type.TWO, null, Type.FALSE);
        Document strings = Document.newInstance()
            .addStrings(() -> "Str", "1", "2", "3")
            .addStrings(() -> "Num", "1", "2", "3")
            .addStrings(() -> "Bool", "true", "false", null)
            .addStrings(() -> "Enum", "ONE", "TWO", null, "FALSE");

        Assert.assertTrue("mixed should be equivalent to its self", mixed.accept(new Equivalent(mixed)));
        Assert.assertTrue("strings should be equivalent to its self", strings.accept(new Equivalent(strings)));
        Assert.assertTrue("#1 Expected docs to be equivalent", mixed.accept(new Equivalent(strings)));
        Assert.assertTrue("#2 Expected docs to be equivalent", strings.accept(new Equivalent(mixed)));
    }

    /**
     * Unit test {@link Equivalent}
     *
     */
    @Test
    public void test_HappyPath_Enum() {
        Document mixed = Document.newInstance()
            .addStrings(() -> "Str", "ONE", "TWO", "FALSE", "ON")
            .addBooleans(() -> "Bool", true, false, null)
            .addEnums(() -> "Enum", Type.ONE, Type.TWO, null, Type.FALSE);
        Document enums = Document.newInstance()
            .addEnums(() -> "Str", Type.ONE, Type.TWO, Type.FALSE, Type.ON)
            .addEnums(() -> "Bool", Type.ON, Type.FALSE, null)
            .addEnums(() -> "Enum", Type.ONE, Type.TWO, null, Type.FALSE);

        Assert.assertTrue("mixed should be equivalent to its self", mixed.accept(new Equivalent(mixed)));
        Assert.assertTrue("enums should be equivalent to its self", enums.accept(new Equivalent(enums)));
        Assert.assertTrue("#1 Expected docs to be equivalent", mixed.accept(new Equivalent(enums)));
        Assert.assertTrue("#2 Expected docs to be equivalent", enums.accept(new Equivalent(mixed)));
    }

    /**
     * Unit test {@link Equivalent}
     *
     */
    @Test
    public void test_HappyPath_Boolean() {
        Document mixed = Document.newInstance()
            .addStrings(() -> "Str", null, "true", "1", "off", "disabled", "unset")
            .addBooleans(() -> "Bool", true, false, null)
            .addEnums(() -> "Enum", Type.ON, null, Type.FALSE);
        Document bools = Document.newInstance()
            .addBooleans(() -> "Str", null, true, true, false, false, false)
            .addBooleans(() -> "Bool", true, false, null)
            .addBooleans(() -> "Enum", true, null, false);

        Assert.assertTrue("mixed should be equivalent to its self", mixed.accept(new Equivalent(mixed)));
        Assert.assertTrue("bools should be equivalent to its self", bools.accept(new Equivalent(bools)));
        Assert.assertTrue("#1 Expected docs to be equivalent", mixed.accept(new Equivalent(bools)));
        Assert.assertTrue("#2 Expected docs to be equivalent", bools.accept(new Equivalent(mixed)));
    }

    /**
     * Unit test {@link Equivalent}
     *
     */
    @Test
    public void test_HappyPath_Number() {
        Document mixed = Document.newInstance()
            .addStrings(() -> "x.Str", "1", "2", "3")
            .addNumbers(() -> "x.Num", -1, -2, -3)
            .addBooleans(() -> "x.Bool", true, false, null);
        Document numbers = Document.newInstance()
            .addNumbers(() -> "x.Str", 1, 2, 3)
            .addNumbers(() -> "x.Num", -1, -2, -3)
            .addNumbers(() -> "x.Bool", 1, 0, null);

        Assert.assertTrue("mixed should be equivalent to its self", mixed.accept(new Equivalent(mixed)));
        Assert.assertTrue("numbers should be equivalent to its self", numbers.accept(new Equivalent(numbers)));
        Assert.assertTrue("#1 Expected docs to be equivalent", mixed.accept(new Equivalent(numbers)));
        Assert.assertTrue("#2 Expected docs to be equivalent", numbers.accept(new Equivalent(mixed)));
    }

    /**
     * Unit test {@link Equivalent}
     *
     */
    @Test
    public void test_UnhappyPath_SequenceLength() {
        Document mixed = Document.newInstance()
            .addStrings(() -> "Str", "1", "2", "3", "4")
            .addNumbers(() -> "Num", 1, 2, 3)
            .addBooleans(() -> "Bool", true, false, null)
            .addEnums(() -> "Enum", Type.ONE, Type.TWO, null, Type.FALSE);
        Document strings = Document.newInstance()
            .addNumbers(() -> "Str", 1, 2, 3)
            .addStrings(() -> "Num", "1", "2", "3")
            .addStrings(() -> "Bool", "true", "false", null)
            .addStrings(() -> "Enum", "ONE", "TWO", null, "FALSE");

        Assert.assertFalse("#1 Unexpected equivalent docs", mixed.accept(new Equivalent(strings)));
        Assert.assertFalse("#2 Unexpected equivalent docs", strings.accept(new Equivalent(mixed)));
    }


    /**
     * Unit test {@link Equivalent}
     */
    @Test
    public void test_UnhappyPath_DifferentData() {
        Document mixed = Document.newInstance()
            .addStrings(() -> "Str", "1", "2", "99")
            .addNumbers(() -> "Num", 1, 2, 3)
            .addBooleans(() -> "Bool", true, false, null)
            .addEnums(() -> "Enum", Type.ONE, Type.TWO, null, Type.FALSE);
        Document strings = Document.newInstance()
            .addNumbers(() -> "Str", 1, 2, 3)
            .addStrings(() -> "Num", "1", "2", "3")
            .addStrings(() -> "Bool", "true", "false", null)
            .addStrings(() -> "Enum", "ONE", "TWO", null, "FALSE");

        Assert.assertFalse("#1 Unexpected equivalent docs", mixed.accept(new Equivalent(strings)));
        Assert.assertFalse("#2 Unexpected equivalent docs", strings.accept(new Equivalent(mixed)));
    }


    /**
     * Unit test {@link Equivalent}
     */
    @Test
    public void test_UnhappyPath_MissingField() {
        Document mixed = Document.newInstance()
            .addString(() -> "foo", "bar")
            .addStrings(() -> "Str", "1", "2", "3")
            .addNumbers(() -> "Num", 1, 2, 3)
            .addBooleans(() -> "Bool", true, false, null)
            .addEnums(() -> "Enum", Type.ONE, Type.TWO, null, Type.FALSE);
        Document strings = Document.newInstance()
            .addNumbers(() -> "Str", 1, 2, 3)
            .addStrings(() -> "Num", "1", "2", "3")
            .addStrings(() -> "Bool", "true", "false", null)
            .addStrings(() -> "Enum", "ONE", "TWO", null, "FALSE");

        Assert.assertFalse("#1 Unexpected equivalent docs", mixed.accept(new Equivalent(strings)));
        Assert.assertFalse("#2 Unexpected equivalent docs", strings.accept(new Equivalent(mixed)));
    }


    /**
     * Unit test {@link Equivalent}
     */
    @Test
    public void test_UnhappyPath_MissingSequence() {
        Document mixed = Document.newInstance()
            .addString(() -> "foo", "bar")
            .addStrings(() -> "Str", "1", "2", "3")
            .addBooleans(() -> "Bool", true, false, null)
            .addEnums(() -> "Enum", Type.ONE, Type.TWO, null, Type.FALSE);
        Document strings = Document.newInstance()
            .addNumbers(() -> "Str", 1, 2, 3)
            .addStrings(() -> "Num", "1", "2", "3")
            .addStrings(() -> "Bool", "true", "false", null)
            .addStrings(() -> "Enum", "ONE", "TWO", null, "FALSE");

        Assert.assertFalse("#1 Unexpected equivalent docs", mixed.accept(new Equivalent(strings)));
        Assert.assertFalse("#2 Unexpected equivalent docs", strings.accept(new Equivalent(mixed)));
    }

    /**
     * Unit test {@link Equivalent#check(Object, Object)}
     */
    @Test
    public void test_check_null() {
        Assert.assertTrue("null", visitor.check(null, null));
        Assert.assertFalse("Boolean", visitor.check(false, null));
        Assert.assertFalse("String", visitor.check("Hello", null));
        Assert.assertFalse("Integer", visitor.check(123, null));
        Assert.assertFalse("BigDecimal", visitor.check(BigDecimal.TEN, null));
        Assert.assertFalse("enum", visitor.check(Type.ONE, null));
    }

    /**
     * Unit test {@link Equivalent#check(Object, Object)}
     */
    @Test
    public void test_check_String() {
        Assert.assertFalse("null", visitor.check(null, "Hello"));
        Assert.assertFalse("Boolean#1", visitor.check(false, "Hello"));
        Assert.assertTrue("Boolean#2", visitor.check(false, " false "));
        Assert.assertFalse("Boolean#3", visitor.check(false, " Enabled "));
        Assert.assertTrue("Boolean#4", visitor.check(true, " TRUE "));
        Assert.assertFalse("Boolean#5", visitor.check(true, " Disabled "));
        Assert.assertTrue("String#1", visitor.check("Hello", "Hello"));
        Assert.assertFalse("String#2", visitor.check("Hello", "hello"));
        Assert.assertFalse("String#3", visitor.check("Hello", "World"));
        Assert.assertFalse("Integer#1", visitor.check(123, "Hello"));
        Assert.assertTrue("Integer#2", visitor.check(123, "123"));
        Assert.assertTrue("Integer#3", visitor.check(123, "123.0"));
        Assert.assertFalse("Integer#4", visitor.check(123, "999"));
        Assert.assertFalse("BigDecimal", visitor.check(BigDecimal.TEN, "Hello"));
        Assert.assertTrue("BigDecimal", visitor.check(BigDecimal.TEN, " 10 "));
        Assert.assertFalse("enum#1", visitor.check(Type.ONE, "Hello"));
        Assert.assertTrue("enum#2", visitor.check(Type.ONE, " one "));
    }


    /**
     * Unit test {@link Equivalent#check(Object, Object)}
     */
    @Test
    public void test_check_Integer() {
        Assert.assertFalse("null", visitor.check(null, 10));
        Assert.assertFalse("Boolean#1", visitor.check(false, 10));
        Assert.assertTrue("Boolean#2", visitor.check(false, 0));
        Assert.assertFalse("String#1", visitor.check("Hello", 10));
        Assert.assertTrue("String#2", visitor.check("10.0", 10));
        Assert.assertFalse("Integer#1", visitor.check(123, 10));
        Assert.assertTrue("Integer#2", visitor.check(10, 10));
        Assert.assertFalse("BigDecimal#1", visitor.check(BigDecimal.ZERO, 10));
        Assert.assertTrue("BigDecimal#2", visitor.check(BigDecimal.TEN, 10));
        Assert.assertFalse("enum", visitor.check(Type.ONE, 10));
    }

    /**
     * Unit test {@link Equivalent#check(Object, Object)}
     */
    @Test
    public void test_check_BigDecimal() {
        Assert.assertFalse("null", visitor.check(null, BigDecimal.TEN));
        Assert.assertFalse("Boolean#1", visitor.check(false, BigDecimal.TEN));
        Assert.assertTrue("Boolean#2", visitor.check(false, 0));
        Assert.assertFalse("String#1", visitor.check("Hello", BigDecimal.TEN));
        Assert.assertTrue("String#2", visitor.check("10.0", BigDecimal.TEN));
        Assert.assertFalse("Integer#1", visitor.check(123, BigDecimal.TEN));
        Assert.assertTrue("Integer#2", visitor.check(10, BigDecimal.TEN));
        Assert.assertFalse("BigDecimal#1", visitor.check(BigDecimal.ZERO, BigDecimal.TEN));
        Assert.assertTrue("BigDecimal#2", visitor.check(BigDecimal.TEN, BigDecimal.TEN));
        Assert.assertFalse("enum", visitor.check(Type.ONE, BigDecimal.TEN));
    }


    /**
     * Unit test {@link Equivalent#check(Object, Object)}
     */
    @Test
    public void test_check_Enum() {
        Assert.assertFalse("null", visitor.check(null, Type.ONE));
        Assert.assertFalse("Boolean", visitor.check(false, Type.ONE));
        Assert.assertFalse("String#1", visitor.check("Hello", Type.ONE));
        Assert.assertTrue("String#2", visitor.check(" one ", Type.ONE));
        Assert.assertFalse("Integer", visitor.check(123, Type.ONE));
        Assert.assertFalse("BigDecimal", visitor.check(BigDecimal.ZERO, Type.ONE));
        Assert.assertFalse("enum#1", visitor.check(Type.ONE, Type.TWO));
        Assert.assertTrue("enum#2", visitor.check(Type.ONE, Type.ONE));
    }

    /**
     * Unit test {@link Equivalent#check(Object, Object)}
     */
    @Test
    public void test_check_Boolean() {
        Assert.assertFalse("null", visitor.check(null, true));
        Assert.assertFalse("Boolean", visitor.check(false, true));
        Assert.assertFalse("String#1", visitor.check(" hello ", true));
        Assert.assertTrue("String#2", visitor.check(" true ", true));
        Assert.assertFalse("String#3", visitor.check(" OFF ", true));
        Assert.assertTrue("String#4", visitor.check(" OFF ", false));
        Assert.assertFalse("Integer#1", visitor.check(123, true));
        Assert.assertTrue("Integer#2", visitor.check(0, false));
        Assert.assertFalse("Integer#3", visitor.check(0, true));
        Assert.assertFalse("Integer#4", visitor.check(1, false));
        Assert.assertTrue("Integer#5", visitor.check(1, true));
        Assert.assertFalse("BigDecimal", visitor.check(BigDecimal.ZERO, true));
        Assert.assertFalse("enum#1", visitor.check(Type.ONE, true));
        Assert.assertFalse("enum#2", visitor.check(Type.FALSE, true));
        Assert.assertTrue("enum#3", visitor.check(Type.FALSE, false));
        Assert.assertTrue("enum#4", visitor.check(Type.ON, true));
        Assert.assertFalse("enum#5", visitor.check(Type.ON, false));
    }
}