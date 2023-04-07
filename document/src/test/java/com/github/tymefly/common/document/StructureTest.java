package com.github.tymefly.common.document;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link Structure}
 */
public class StructureTest {
    private Structure structure;
    private Structure empty;
    private Structure same;
    private Structure zeroBigDecimal;
    private Structure zeroString;
    private Structure differentVals;
    private Structure differentKeys;


    @Before
    public void setUp() {
        structure = new Structure();
        empty = new Structure();
        same = new Structure();
        zeroBigDecimal = new Structure();
        zeroString = new Structure();
        differentVals = new Structure();
        differentKeys = new Structure();

        structure.put("Key", "value");
        structure.put("Zero", 0);

        same.put("Key", "value");
        same.put("Zero", 0);

        zeroBigDecimal.put("Key", "value");
        zeroBigDecimal.put("Zero", BigDecimal.ZERO);

        zeroString.put("Key", "value");
        zeroString.put("Zero", "000.000");

        differentVals.put("Key", "xxxxx");
        differentVals.put("Zero", "zero");

        differentKeys.put("x", "value");
        differentKeys.put("y", 0);
    }

    /**
     * Unit test {@link Structure#add}
     */
    @Test
    public void test_add() {
        structure.add("One", "overwrite me")
                 .add("One", 1)
                 .add("Two", 2)
                 .add("Three", null);

        Assert.assertEquals("Unexpected size", 5, structure.size());
        Assert.assertEquals("'Key' is wrong", "value", structure.get("Key"));
        Assert.assertEquals("'Zero' is wrong", 0, structure.get("Zero"));
        Assert.assertEquals("'One' is wrong", 1, structure.get("One"));
        Assert.assertEquals("'Two' is wrong", 2, structure.get("Two"));
        Assert.assertNull("'Three' is wrong", structure.get("Three"));
        Assert.assertNull("'other' is wrong", structure.get("other"));
    }

    /**
     * Unit test {@link Structure#equals}
     */
    @Test
    public void test_equals() {
        Assert.assertNotEquals("compare to null", null, structure);
        Assert.assertNotEquals("compare to String", "Hello", structure);
        Assert.assertNotEquals("compare to empty", structure, empty);
        Assert.assertEquals("compare to same", structure, same);
        Assert.assertNotEquals("compare to zeroBigDecimal", structure, zeroBigDecimal);
        Assert.assertNotEquals("compare to zeroString", structure, zeroString);
        Assert.assertNotEquals("compare to differentVals", structure, differentVals);
        Assert.assertNotEquals("compare to differentKeys", structure, differentKeys);
    }

    /**
     * Unit test {@link Structure#hashCode}
     */
    @Test
    public void test_hashCode() {
        Assert.assertEquals("mutating hash code", structure.hashCode(), structure.hashCode());
        Assert.assertEquals("'Same' should have equal hashcode", structure.hashCode(), same.hashCode());

        Assert.assertNotEquals("'zeroString' should have different hashcode",
                structure.hashCode(),
                zeroString.hashCode());
        Assert.assertNotEquals("'empty' should have different hashcode",
                structure.hashCode(),
                empty.hashCode());
        Assert.assertNotEquals("'differentVals' should have different hashcode",
                structure.hashCode(),
                differentVals.hashCode());
        Assert.assertNotEquals("'differentKeys' should have different hashcode",
                structure.hashCode(),
                differentKeys.hashCode());
    }
}