package com.github.tymefly.common.base.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link Convert}
 */
public class ConvertTest {
    private enum Enumeration { ONE, TWO, OFF };

    private static final List<String> list = List.of("false", "1", "on", "disAbled", "Set", "yes");


    /**
     * Unit test {@link Convert#to(Object, Class}
     */
    @Test
    public void test_to_String() {
        Assert.assertNull("null", Convert.to(null, String.class));
        Assert.assertEquals("boolean", "true", Convert.to(true, String.class));
        Assert.assertEquals("string", "on", Convert.to("on", String.class));
        Assert.assertEquals("byte", "0", Convert.to((byte) 0, String.class));
        Assert.assertEquals("short", "1", Convert.to((short) 1, String.class));
        Assert.assertEquals("integer", "2", Convert.to(2, String.class));
        Assert.assertEquals("long", "3", Convert.to((long) 3, String.class));
        Assert.assertEquals("float", "3.4", Convert.to(3.4f, String.class));
        Assert.assertEquals("double", "-5.6", Convert.to(-5.6, String.class));
        Assert.assertEquals("BigDecimal", "7", Convert.to(BigDecimal.valueOf(7), String.class));
        Assert.assertEquals("BigInteger", "8", Convert.to(BigInteger.valueOf(8), String.class));
        Assert.assertEquals("enum", "OFF", Convert.to(Enumeration.OFF, String.class));
        Assert.assertEquals("List", "[false, 1, on, disAbled, Set, yes]", Convert.to(list, String.class));
    }


    /**
     * Unit test {@link Convert#to(Object, Class}
     */
    @Test
    public void test_to_Boolean() {
        Assert.assertNull("null", Convert.to(null, Boolean.class));
        Assert.assertTrue("boolean", Convert.to(true, Boolean.class));
        Assert.assertFalse("string", Convert.to("DisAbleD", Boolean.class));
        Assert.assertFalse("byte", Convert.to((byte) 0, Boolean.class));
        Assert.assertTrue("short", Convert.to((short) 1, Boolean.class));
        Assert.assertThrows("integer", RuntimeException.class, () -> Convert.to(2, Boolean.class));
        Assert.assertThrows("long", RuntimeException.class, () -> Convert.to((long) 3, Boolean.class));
        Assert.assertThrows("float", RuntimeException.class, () -> Convert.to(3.4f, Boolean.class));
        Assert.assertThrows("double", RuntimeException.class, () -> Convert.to(-5.6, Boolean.class));
        Assert.assertThrows("BigDecimal", RuntimeException.class, () -> Convert.to(BigDecimal.valueOf(7), Boolean.class));
        Assert.assertThrows("BigInteger", RuntimeException.class, () -> Convert.to(BigInteger.valueOf(8), Boolean.class));
        Assert.assertFalse("enum", Convert.to(Enumeration.OFF, Boolean.class));
        Assert.assertThrows("List", RuntimeException.class, () -> Convert.to(list, Boolean.class));

        Assert.assertThrows("bad String", RuntimeException.class, () -> Convert.to("unknown", Boolean.class));
        Assert.assertThrows("bad Enum", RuntimeException.class, () -> Convert.to(Enumeration.ONE, Boolean.class));
    }


    /**
     * Unit test {@link Convert#to(Object, Class}
     */
    @Test
    public void test_to_Number_Byte() {
        Assert.assertNull("null", Convert.to(null, Byte.class));
        Assert.assertEquals("boolean", Byte.valueOf((byte) 1), Convert.to(true, Byte.class));
        Assert.assertEquals("string", Byte.valueOf((byte) 93), Convert.to("93", Byte.class));
        Assert.assertEquals("byte", Byte.valueOf((byte) 0), Convert.to((byte) 0, Byte.class));
        Assert.assertEquals("short", Byte.valueOf((byte) 1), Convert.to((short) 1, Byte.class));
        Assert.assertEquals("integer", Byte.valueOf((byte) 2), Convert.to(2, Byte.class));
        Assert.assertEquals("long", Byte.valueOf((byte) 3), Convert.to((long) 3, Byte.class));
        Assert.assertEquals("float", Byte.valueOf((byte) 3), Convert.to(3.4f, Byte.class));
        Assert.assertEquals("double", Byte.valueOf((byte) -5), Convert.to(-5.6, Byte.class));
        Assert.assertEquals("BigDecimal", Byte.valueOf((byte) 7), Convert.to(BigDecimal.valueOf(7), Byte.class));
        Assert.assertEquals("BigInteger", Byte.valueOf((byte) 8), Convert.to(BigInteger.valueOf(8), Byte.class));
        Assert.assertThrows("enum", RuntimeException.class, () -> Convert.to(Enumeration.OFF, Byte.class));
        Assert.assertThrows("List", RuntimeException.class, () -> Convert.to(list, Byte.class));

        Assert.assertThrows("text", RuntimeException.class, () -> Convert.to("text", Byte.class));
    }

    /**
     * Unit test {@link Convert#to(Object, Class}
     */
    @Test
    public void test_to_Number_Short() {
        Assert.assertNull("null", Convert.to(null, Short.class));
        Assert.assertEquals("boolean", Short.valueOf((short) 1), Convert.to(true, Short.class));
        Assert.assertEquals("string", Short.valueOf((short) 93), Convert.to("93", Short.class));
        Assert.assertEquals("byte", Short.valueOf((short) 0), Convert.to((byte) 0, Short.class));
        Assert.assertEquals("short", Short.valueOf((short) 1), Convert.to((short) 1, Short.class));
        Assert.assertEquals("integer", Short.valueOf((short) 2), Convert.to(2, Short.class));
        Assert.assertEquals("long", Short.valueOf((short) 3), Convert.to((long) 3, Short.class));
        Assert.assertEquals("float", Short.valueOf((short) 3), Convert.to(3.4f, Short.class));
        Assert.assertEquals("double", Short.valueOf((short) -5), Convert.to(-5.6, Short.class));
        Assert.assertEquals("BigDecimal", Short.valueOf((short) 7), Convert.to(BigDecimal.valueOf(7), Short.class));
        Assert.assertEquals("BigInteger", Short.valueOf((short) 8), Convert.to(BigInteger.valueOf(8), Short.class));
        Assert.assertThrows("enum", RuntimeException.class, () -> Convert.to(Enumeration.OFF, Short.class));
        Assert.assertThrows("List", RuntimeException.class, () -> Convert.to(list, Short.class));

        Assert.assertThrows("text", RuntimeException.class, () -> Convert.to("text", Short.class));
    }

    /**
     * Unit test {@link Convert#to(Object, Class}
     */
    @Test
    public void test_to_Number_Integer() {
        Assert.assertNull("null", Convert.to(null, Integer.class));
        Assert.assertEquals("boolean", 1, (int) Convert.to(true, Integer.class));
        Assert.assertEquals("string", 93, (int) Convert.to("93", Integer.class));
        Assert.assertEquals("byte", 0, (int) Convert.to((byte) 0, Integer.class));
        Assert.assertEquals("short", 1, (int) Convert.to((short) 1, Integer.class));
        Assert.assertEquals("integer", 2, (int) Convert.to(2, Integer.class));
        Assert.assertEquals("long", 3, (int) Convert.to((long) 3, Integer.class));
        Assert.assertEquals("float", 3, (int) Convert.to(3.4f, Integer.class));
        Assert.assertEquals("double", -5, (int) Convert.to(-5.6, Integer.class));
        Assert.assertEquals("BigDecimal", 7, (int) Convert.to(BigDecimal.valueOf(7), Integer.class));
        Assert.assertEquals("BigInteger", 8, (int) Convert.to(BigInteger.valueOf(8), Integer.class));
        Assert.assertThrows("enum", RuntimeException.class, () -> Convert.to(Enumeration.OFF, Integer.class));
        Assert.assertThrows("List", RuntimeException.class, () -> Convert.to(list, Integer.class));

        Assert.assertThrows("text", RuntimeException.class, () -> Convert.to("text", Integer.class));
    }

    /**
     * Unit test {@link Convert#to(Object, Class}
     */
    @Test
    public void test_to_Number_Long() {
        Assert.assertNull("null", Convert.to(null, Long.class));
        Assert.assertEquals("boolean", Long.valueOf(1), Convert.to(true, Long.class));
        Assert.assertEquals("string", Long.valueOf(93), Convert.to("93", Long.class));
        Assert.assertEquals("byte", Long.valueOf( 0), Convert.to((byte) 0, Long.class));
        Assert.assertEquals("short", Long.valueOf(1), Convert.to((short) 1, Long.class));
        Assert.assertEquals("integer", Long.valueOf(2), Convert.to(2, Long.class));
        Assert.assertEquals("long", Long.valueOf(3), Convert.to((long) 3, Long.class));
        Assert.assertEquals("float", Long.valueOf(3), Convert.to(3.4f, Long.class));
        Assert.assertEquals("double", Long.valueOf(-5), Convert.to(-5.6, Long.class));
        Assert.assertEquals("BigDecimal", Long.valueOf(7), Convert.to(BigDecimal.valueOf(7), Long.class));
        Assert.assertEquals("BigInteger", Long.valueOf(8), Convert.to(BigInteger.valueOf(8), Long.class));
        Assert.assertThrows("enum", RuntimeException.class, () -> Convert.to(Enumeration.OFF, Long.class));
        Assert.assertThrows("List", RuntimeException.class, () -> Convert.to(list, Long.class));

        Assert.assertThrows("text", RuntimeException.class, () -> Convert.to("text", Long.class));
    }

    /**
     * Unit test {@link Convert#to(Object, Class}
     */
    @Test
    public void test_to_Number_Float() {
        Assert.assertNull("null", Convert.to(null, Float.class));
        Assert.assertEquals("boolean", Float.valueOf(1f), Convert.to(true, Float.class));
        Assert.assertEquals("string", Float.valueOf(93f), Convert.to("93", Float.class));
        Assert.assertEquals("byte", Float.valueOf(0f), Convert.to((byte) 0, Float.class));
        Assert.assertEquals("short", Float.valueOf(1f), Convert.to((short) 1, Float.class));
        Assert.assertEquals("integer", Float.valueOf(2f), Convert.to(2, Float.class));
        Assert.assertEquals("long", Float.valueOf(3f), Convert.to((long) 3, Float.class));
        Assert.assertEquals("float", Float.valueOf(3.4f), Convert.to(3.4f, Float.class));
        Assert.assertEquals("double", Float.valueOf(-5.6f), Convert.to(-5.6, Float.class));
        Assert.assertEquals("BigDecimal", Float.valueOf(7f), Convert.to(BigDecimal.valueOf(7), Float.class));
        Assert.assertEquals("BigInteger", Float.valueOf(8f), Convert.to(BigInteger.valueOf(8), Float.class));
        Assert.assertThrows("enum", RuntimeException.class, () -> Convert.to(Enumeration.OFF, Float.class));
        Assert.assertThrows("List", RuntimeException.class, () -> Convert.to(list, Float.class));

        Assert.assertThrows("text", RuntimeException.class, () -> Convert.to("text", Float.class));
    }

    /**
     * Unit test {@link Convert#to(Object, Class}
     */
    @Test
    public void test_to_Number_Double() {
        Assert.assertNull("null", Convert.to(null, Double.class));
        Assert.assertEquals("boolean", Double.valueOf(1.0), Convert.to(true, Double.class));
        Assert.assertEquals("string", Double.valueOf(93.0), Convert.to("93", Double.class));
        Assert.assertEquals("byte", Double.valueOf(0.0), Convert.to((byte) 0, Double.class));
        Assert.assertEquals("short", Double.valueOf(1.0), Convert.to((short) 1, Double.class));
        Assert.assertEquals("integer", Double.valueOf(2.0), Convert.to(2, Double.class));
        Assert.assertEquals("long", Double.valueOf(3.0), Convert.to((long) 3, Double.class));
        Assert.assertEquals("float", 3.4, Convert.to(3.4f, Double.class).doubleValue(), 0.001);
        Assert.assertEquals("double", -5.6, Convert.to(-5.6, Double.class).doubleValue(), 0.001);
        Assert.assertEquals("BigDecimal", Double.valueOf(7.0), Convert.to(BigDecimal.valueOf(7), Double.class));
        Assert.assertEquals("BigInteger", Double.valueOf(8.0), Convert.to(BigInteger.valueOf(8), Double.class));
        Assert.assertThrows("enum", RuntimeException.class, () -> Convert.to(Enumeration.OFF, Double.class));
        Assert.assertThrows("List", RuntimeException.class, () -> Convert.to(list, Double.class));

        Assert.assertThrows("text", RuntimeException.class, () -> Convert.to("text", Double.class));
    }

    /**
     * Unit test {@link Convert#to(Object, Class}
     */
    @Test
    public void test_to_Number_BigDecimal() {
        Assert.assertNull("null", Convert.to(null, BigDecimal.class));
        Assert.assertEquals("boolean", new BigDecimal(1), Convert.to(true, BigDecimal.class));
        Assert.assertEquals("string", new BigDecimal(93), Convert.to("93", BigDecimal.class));
        Assert.assertEquals("byte", new BigDecimal(0), Convert.to((byte) 0, BigDecimal.class));
        Assert.assertEquals("short", new BigDecimal(1), Convert.to((short) 1, BigDecimal.class));
        Assert.assertEquals("integer", new BigDecimal(2), Convert.to(2, BigDecimal.class));
        Assert.assertEquals("long", new BigDecimal(3), Convert.to((long) 3, BigDecimal.class));
        Assert.assertEquals("float", new BigDecimal("3.4"), Convert.to(3.4f, BigDecimal.class));
        Assert.assertEquals("double", new BigDecimal("-5.6"), Convert.to(-5.6, BigDecimal.class));
        Assert.assertEquals("BigDecimal", new BigDecimal(7), Convert.to(BigDecimal.valueOf(7), BigDecimal.class));
        Assert.assertEquals("BigInteger", new BigDecimal(8), Convert.to(BigDecimal.valueOf(8), BigDecimal.class));
        Assert.assertThrows("enum",  RuntimeException.class, () -> Convert.to(Enumeration.OFF, BigDecimal.class));
        Assert.assertThrows("List",  RuntimeException.class, () -> Convert.to(list, BigDecimal.class));
    }


    /**
     * Unit test {@link Convert#to(Object, Class}
     */
    @Test
    public void test_to_Number_BigInteger() {
        Assert.assertNull("null", Convert.to(null, BigInteger.class));
        Assert.assertEquals("boolean", new BigInteger("1"), Convert.to(true, BigInteger.class));
        Assert.assertEquals("string", new BigInteger("93"), Convert.to("93", BigInteger.class));
        Assert.assertEquals("byte", new BigInteger("0"), Convert.to((byte) 0, BigInteger.class));
        Assert.assertEquals("short", new BigInteger("1"), Convert.to((short) 1, BigInteger.class));
        Assert.assertEquals("integer", new BigInteger("2"), Convert.to(2, BigInteger.class));
        Assert.assertEquals("long", new BigInteger("3"), Convert.to((long) 3, BigInteger.class));
        Assert.assertEquals("float", new BigInteger("3"), Convert.to(3.4f, BigInteger.class));
        Assert.assertEquals("double", new BigInteger("-5"), Convert.to(-5.6, BigInteger.class));
        Assert.assertEquals("BigDecimal", new BigInteger("7"), Convert.to(BigDecimal.valueOf(7), BigInteger.class));
        Assert.assertEquals("BigInteger", new BigInteger("8"), Convert.to(BigDecimal.valueOf(8), BigInteger.class));
        Assert.assertThrows("enum",  RuntimeException.class, () -> Convert.to(Enumeration.OFF, BigInteger.class));
        Assert.assertThrows("List",  RuntimeException.class, () -> Convert.to(list, BigInteger.class));
    }


    /**
     * Unit test {@link Convert#to(Object, Class}
     */
    @Test
    public void test_to_Boxed() {
        Assert.assertEquals("boolean", true, Convert.to("true", boolean.class));
        Assert.assertEquals("byte", (byte) 1, (byte) Convert.to("1", byte.class));
        Assert.assertEquals("short", (short) 2, (short) Convert.to("2", short.class));
        Assert.assertEquals("int", 3, (int) Convert.to("3", int.class));
        Assert.assertEquals("long", 4, (long) Convert.to("4", long.class));
    }


    /**
     * Unit test {@link Convert#to(Object, Class}
     */
    @Test
    public void test_to_Number_UnsupportedType() {
        Assert.assertThrows("From Number", RuntimeException.class, () -> Convert.to(1, AtomicInteger.class));
        Assert.assertThrows("From String", RuntimeException.class, () -> Convert.to("1", AtomicInteger.class));
    }


    /**
     * Unit test {@link Convert#to(Object, Class}
     */
    @Test
    public void test_to_Enum() {
        Assert.assertNull("null", Convert.to(null, Enumeration.class));
        Assert.assertThrows("boolean", RuntimeException.class, () -> Convert.to(true, Enumeration.class));
        Assert.assertEquals("string", Enumeration.ONE, Convert.to("ONE", Enumeration.class));
        Assert.assertThrows("byte", RuntimeException.class, () -> Convert.to((byte) 0, Enumeration.class));
        Assert.assertThrows("short", RuntimeException.class, () -> Convert.to((short) 1, Enumeration.class));
        Assert.assertThrows("integer", RuntimeException.class, () -> Convert.to(2, Enumeration.class));
        Assert.assertThrows("long", RuntimeException.class, () -> Convert.to((long) 3, Enumeration.class));
        Assert.assertThrows("float", RuntimeException.class, () -> Convert.to(3.4f, Enumeration.class));
        Assert.assertThrows("double", RuntimeException.class, () -> Convert.to(-5.6, Enumeration.class));
        Assert.assertThrows("BigDecimal", RuntimeException.class, () -> Convert.to(BigDecimal.valueOf(7), Enumeration.class));
        Assert.assertThrows("BigInteger", RuntimeException.class, () -> Convert.to(BigDecimal.valueOf(8), Enumeration.class));
        Assert.assertEquals("enum", Enumeration.OFF, Convert.to(Enumeration.OFF, Enumeration.class));
        Assert.assertThrows("List", RuntimeException.class, () -> Convert.to(list, Enumeration.class));
    }


    /**
     * Unit test {@link Convert#to(Object, Class}
     */
    @Test
    public void test_to_List() {
        Assert.assertNull("null", Convert.to(null, List.class));
        Assert.assertThrows("boolean", RuntimeException.class, () -> Convert.to(true, List.class));
        Assert.assertThrows("string", RuntimeException.class, () -> Convert.to("ONE", List.class));
        Assert.assertThrows("byte", RuntimeException.class, () -> Convert.to((byte) 0, List.class));
        Assert.assertThrows("short", RuntimeException.class, () -> Convert.to((short) 1, List.class));
        Assert.assertThrows("integer", RuntimeException.class, () -> Convert.to(2, List.class));
        Assert.assertThrows("long", RuntimeException.class, () -> Convert.to((long) 3, List.class));
        Assert.assertThrows("float", RuntimeException.class, () -> Convert.to(3.4f, List.class));
        Assert.assertThrows("double", RuntimeException.class, () -> Convert.to(-5.6, List.class));
        Assert.assertThrows("BigDecimal", RuntimeException.class, () -> Convert.to(BigDecimal.valueOf(7), List.class));
        Assert.assertThrows("BigInteger", RuntimeException.class, () -> Convert.to(BigDecimal.valueOf(8), List.class));
        Assert.assertThrows("enum", RuntimeException.class, () -> Convert.to(Enumeration.OFF, List.class));
        Assert.assertEquals("List", list, Convert.to(list, List.class));
    }


    /**
     * Unit test {@link Convert#toList(Collection, Class)}
     */
    @Test
    public void test_list() {
        Assert.assertEquals("Empty", List.of(), Convert.toList(List.of(), String.class));
        Assert.assertEquals("booleans",
                List.of(false, true, false, true, false, true, false, true, false, true),
                Convert.toList(
                        List.of("false", "true", "0", "1", "OFF", "on", "disabled", "enabled", "unset", "set"),
                                Boolean.class));
        Assert.assertEquals("strings",
                List.of("false", "true", "0", "1", "OFF"),
                Convert.toList(
                        List.of(false, true, 0, 1, Enumeration.OFF),
                                String.class));
        Assert.assertEquals("bytes",
                List.of((byte) 0,(byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) -6, (byte) 10, (byte) 2),
                Convert.toList(
                        List.of((byte) 0,(short) 1, 2, (long) 3, 4.5f, -6.7, BigDecimal.TEN, BigInteger.TWO),
                        Byte.class));
        Assert.assertEquals("short",
                List.of((short) 0,(short) 1, (short) 2, (short) 3, (short) 4, (short) -6, (short) 10, (short) 2),
                Convert.toList(
                        List.of((byte) 0,(short) 1, 2, 3, 4.5f, -6.7, BigDecimal.TEN, BigInteger.TWO),
                        Short.class));
        Assert.assertEquals("int",
                List.of(0, 1, 2, 3, 4, -6, 10, 2),
                Convert.toList(
                        List.of((byte) 0,(short) 1, 2, (long) 3, 4.5f, -6.7, BigDecimal.TEN, BigInteger.TWO),
                        Integer.class));
        Assert.assertEquals("long",
                List.of((long) 0, (long) 1, (long) 2, (long) 3, (long) 4, (long) -6, (long) 10, (long) 2),
                Convert.toList(
                        List.of((byte) 0,(short) 1, 2, (long) 3, 4.5f, -6.7, BigDecimal.TEN, BigInteger.TWO),
                        Long.class));
        Assert.assertEquals("float",
                List.of(0f, 1f, 2f, 3f, 4.5f, -6.7f, 10f, 2f),
                Convert.toList(
                        List.of((byte) 0,(short) 1, 2, (long) 3, 4.5f, -6.7, BigDecimal.TEN, BigInteger.TWO),
                        Float.class));
        Assert.assertEquals("double",
                List.of(0.0, 1.0, 2.0, 3.0, 4.5, -6.7, 10.0, 2.0),
                Convert.toList(
                        List.of((byte) 0,(short) 1, 2, (long) 3, 4.5f, -6.7, BigDecimal.TEN, BigInteger.TWO),
                        Double.class));
        Assert.assertEquals("BigDecimal",
                List.of(BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.valueOf(2), BigDecimal.valueOf(3),
                        BigDecimal.valueOf(4.5), BigDecimal.valueOf(-6.7), BigDecimal.TEN, BigDecimal.valueOf(2)),
                Convert.toList(
                        List.of((byte) 0,(short) 1, 2, (long) 3, 4.5f, -6.7, BigDecimal.TEN, BigInteger.TWO),
                        BigDecimal.class));
        Assert.assertEquals("BigInteger",
                List.of(BigInteger.ZERO, BigInteger.ONE, BigInteger.valueOf(2), BigInteger.valueOf(3),
                        BigInteger.valueOf(4), BigInteger.valueOf(-6), BigInteger.TEN, BigInteger.valueOf(2)),
                Convert.toList(
                        List.of((byte) 0,(short) 1, 2, (long) 3, 4.5f, -6.7, BigDecimal.TEN, BigInteger.TWO),
                        BigInteger.class));
        Assert.assertEquals("enum",
                List.of(Enumeration.OFF, Enumeration.ONE, Enumeration.TWO),
                Convert.toList(
                        List.of("OFF", Enumeration.ONE, "TWO"),
                        Enumeration.class));
    }
}