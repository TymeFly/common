package com.github.tymefly.common.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link Sequence}
 */
public class SequenceTest {
    /**
     * Unit test {@link Sequence#of(Class)}
     */
    @Test
    public void test_OfNothing() {
        Sequence<Boolean> sequence = Sequence.of(Boolean.class);

        Assert.assertEquals("Unexpected Size", 0, sequence.size());
        Assert.assertTrue("Unexpected isEmpty()", sequence.isEmpty());
        Assert.assertEquals("Unexpected capacity", 8, sequence.capacity());
        Assert.assertEquals("Unexpected element 0", null, sequence.get(0));
        Assert.assertEquals("Unexpected element 1", null, sequence.get(1));
    }

    /**
     * Unit test {@link Sequence#of(Class)}
     */
    @Test
    public void test_OfSize() {
        Sequence<Boolean> sequence = Sequence.of(Boolean.class, 8);

        Assert.assertEquals("Unexpected Size", 0, sequence.size());
        Assert.assertTrue("Unexpected isEmpty()", sequence.isEmpty());
        Assert.assertEquals("Unexpected capacity", 16, sequence.capacity());
        Assert.assertEquals("Unexpected element 0", null, sequence.get(0));
        Assert.assertEquals("Unexpected element 1", null, sequence.get(1));
    }


    /**
     * Unit test {@link Sequence#of(Class, Object[])}
     */
    @Test
    public void test_OfArray() {
        Number[] backing = new Number[] { 1, 2, null, 4.5, 5 };
        Sequence<Number> sequence = Sequence.of(Number.class, backing);

        // make sure changes to the array are not reflected in the sequence
        backing[0] = 99;

        Assert.assertEquals("Unexpected Size", 5, sequence.size());
        Assert.assertFalse("Unexpected isEmpty()", sequence.isEmpty());
        Assert.assertEquals("Unexpected capacity", 5, sequence.capacity());
        Assert.assertEquals("Unexpected element 0", 1, sequence.get(0));
        Assert.assertEquals("Unexpected element 1", 2, sequence.get(1));
        Assert.assertEquals("Unexpected element 2", null, sequence.get(2));
        Assert.assertEquals("Unexpected element 3", 4.5, sequence.get(3));
        Assert.assertEquals("Unexpected element 4", 5, sequence.get(4));
        Assert.assertEquals("Unexpected element 5", null, sequence.get(5));
    }


    /**
     * Unit test {@link Sequence#of(Class, Collection)}
     */
    @Test
    public void test_Of_List() {
        List<String> backing = new ArrayList<>() {{
            add("A");
            add("B");
            add(null);
            add("D");
        }};
        Sequence<String> sequence = Sequence.of(String.class, backing);

        // make sure changes to the array are not reflected in the sequence
        backing.set(0, "Error");

        Assert.assertEquals("Unexpected Size", 4, sequence.size());
        Assert.assertFalse("Unexpected isEmpty()", sequence.isEmpty());
        Assert.assertEquals("Unexpected capacity", 4, sequence.capacity());
        Assert.assertEquals("Unexpected element 0", "A", sequence.get(0));
        Assert.assertEquals("Unexpected element 1", "B", sequence.get(1));
        Assert.assertEquals("Unexpected element 2", null, sequence.get(2));
        Assert.assertEquals("Unexpected element 3", "D", sequence.get(3));
        Assert.assertEquals("Unexpected element 4", null, sequence.get(4));
    }


    /**
     * Unit test {@link Sequence#of(Class, Collection)}
     */
    @Test
    public void test_TestOf_NullList() {
        Assert.assertNull("Expected a null sequence", Sequence.of(String.class, ((List<String>) null)));
    }


    /**
     * Unit test {@link Sequence#iterator()}
     */
    @Test
    public void test_Iterator_empty() {
        Assert.assertFalse("Unexpected iterable elements",  Sequence.of(Number.class).iterator().hasNext());
    }


    /**
     * Unit test {@link Sequence#iterator()} 
     */
    @Test
    public void test_Iterator() {
        Iterator<Number> data = Sequence.of(Number.class, 1, 2, 3, 4)
            .iterator();

        Assert.assertEquals("First element", 1, data.next());
        Assert.assertEquals("Second element", 2, data.next());
        Assert.assertEquals("Third element", 3, data.next());
        Assert.assertEquals("Fourth element", 4, data.next());
        Assert.assertFalse("Expected end of Sequence", data.hasNext());
    }


    /**
     * Unit test {@link Sequence#toList}
     */
    @Test
    public void test_ToList() {
        Sequence<Number> data = Sequence.of(Number.class, 0, 1, 2, 3);

        Assert.assertEquals("Unexpected List", List.of(0, 1, 2, 3), data.toList());

        data.set(4, 4);

        Assert.assertEquals("Failed to append List", List.of(0, 1, 2, 3, 4), data.toList());

        data.set(2, -2);

        Assert.assertEquals("Failed to mutate List", List.of(0, 1, -2, 3, 4), data.toList());
    }


    /**
     * Unit test {@link Sequence#toList}
     */
    @Test
    public void test_ToList_Empty() {
        List<Number> data = Sequence.of(Number.class).toList();

        Assert.assertEquals("Unexpected list for empty Sequence", Collections.emptyList(), data);
    }



    /**
     * Unit test {@link Sequence#remove(int)}
     */
    @Test
    public void test_remove() {
        Sequence<Number> data = Sequence.of(Number.class, 0, 1, 2, 3);

        Assert.assertEquals("Initial State", "[0, 1, 2, 3]", data.toString());

        Assert.assertFalse("Remove 4", data.remove(4));
        Assert.assertEquals("After remove 2", "[0, 1, 2, 3]", data.toString());
        Assert.assertEquals("After remove 2: size()", 4, data.size());
        Assert.assertFalse("After remove 2: isEmpty()", data.isEmpty());

        Assert.assertFalse("Remove 2", data.remove(2));
        Assert.assertEquals("After remove 2", "[0, 1, null, 3]", data.toString());
        Assert.assertEquals("After remove 2: size()", 4, data.size());
        Assert.assertFalse("After remove 2: isEmpty()", data.isEmpty());

        Assert.assertFalse("Remove 3", data.remove(3));
        Assert.assertEquals("After remove 3", "[0, 1]", data.toString());
        Assert.assertEquals("After remove 3: size()", 2, data.size());
        Assert.assertFalse("After remove 3: isEmpty()", data.isEmpty());

        Assert.assertFalse("Remove 3(again)", data.remove(3));
        Assert.assertEquals("After remove 3(again)", "[0, 1]", data.toString());
        Assert.assertEquals("After remove 3(again): size()", 2, data.size());
        Assert.assertFalse("After remove 3(again): isEmpty()", data.isEmpty());

        Assert.assertFalse("Remove 0", data.remove(0));
        Assert.assertEquals("After remove 0", "[null, 1]", data.toString());
        Assert.assertEquals("After remove 0: size()", 2, data.size());
        Assert.assertFalse("After remove 0: isEmpty()", data.isEmpty());

        Assert.assertFalse("Remove 0 (again)", data.remove(0));
        Assert.assertEquals("After remove 0 (again)", "[null, 1]", data.toString());
        Assert.assertEquals("After remove 0 (again): size()", 2, data.size());
        Assert.assertFalse("After remove 0 (again): isEmpty()", data.isEmpty());

        Assert.assertTrue("Remove 1", data.remove(1));
        Assert.assertEquals("After remove 1", "[]", data.toString());
        Assert.assertEquals("After remove 1: size()", 0, data.size());
        Assert.assertTrue("After remove 1: isEmpty()", data.isEmpty());

        Assert.assertTrue("Remove 1(again)", data.remove(1));
        Assert.assertEquals("After remove 1(again)", "[]", data.toString());
        Assert.assertEquals("After remove 1(again): size()", 0, data.size());
        Assert.assertTrue("After remove 1(again): isEmpty()", data.isEmpty());
    }


    /**
     * Unit test {@link Sequence} content when the sequence is extended and elements are overwritten
     */
    @Test
    public void test_ExtendAndOverwrite() {
        Sequence<String> sequence = Sequence.of(String.class);

        Assert.assertEquals("Before: Unexpected Size", 0, sequence.size());
        Assert.assertTrue("Before: isEmpty()", sequence.isEmpty());
        Assert.assertEquals("Before: capacity()", 8, sequence.capacity());
        Assert.assertEquals("Before: Unexpected element 0", null, sequence.get(0));
        Assert.assertEquals("Before: Unexpected element 1", null, sequence.get(1));
        Assert.assertEquals("Before: Unexpected element 8", null, sequence.get(8));
        Assert.assertEquals("Before: Unexpected element 9", null, sequence.get(9));

        sequence.set(1, "One");

        Assert.assertEquals("One: Unexpected Size", 2, sequence.size());
        Assert.assertFalse("One: isEmpty()", sequence.isEmpty());
        Assert.assertEquals("One: capacity()", 8, sequence.capacity());
        Assert.assertEquals("One: Unexpected element 0", null, sequence.get(0));
        Assert.assertEquals("One: Unexpected element 1", "One", sequence.get(1));
        Assert.assertEquals("One: Unexpected element 8", null, sequence.get(8));
        Assert.assertEquals("One: Unexpected element 9", null, sequence.get(9));

        sequence.set(8, "Eight");

        Assert.assertEquals("Eight: Unexpected Size", 9, sequence.size());
        Assert.assertFalse("Eight: isEmpty()", sequence.isEmpty());
        Assert.assertEquals("Eight: capacity()", 16, sequence.capacity());
        Assert.assertEquals("Eight: Unexpected element 0", null, sequence.get(0));
        Assert.assertEquals("Eight: Unexpected element 1", "One", sequence.get(1));
        Assert.assertEquals("Eight: Unexpected element 8", "Eight", sequence.get(8));
        Assert.assertEquals("Eight: Unexpected element 9", null, sequence.get(9));

        sequence.set(9, "Nine");

        Assert.assertEquals("Nine: Unexpected Size", 10, sequence.size());
        Assert.assertFalse("Nine: isEmpty()", sequence.isEmpty());
        Assert.assertEquals("Nine: capacity()", 16, sequence.capacity());
        Assert.assertEquals("Nine: Unexpected element 0", null, sequence.get(0));
        Assert.assertEquals("Nine: Unexpected element 1", "One", sequence.get(1));
        Assert.assertEquals("Nine: Unexpected element 8", "Eight", sequence.get(8));
        Assert.assertEquals("Nine: Unexpected element 9", "Nine", sequence.get(9));

        sequence.set(9, null);

        Assert.assertEquals("After: Unexpected Size", 10, sequence.size());
        Assert.assertFalse("After: isEmpty()", sequence.isEmpty());
        Assert.assertEquals("After: capacity()", 16, sequence.capacity());
        Assert.assertEquals("After: Unexpected element 0", null, sequence.get(0));
        Assert.assertEquals("Eight: Unexpected element 1", "One", sequence.get(1));
        Assert.assertEquals("After: Unexpected element 8", "Eight", sequence.get(8));
        Assert.assertEquals("After: Unexpected element 9", null, sequence.get(9));

        Iterator<String> iterator = sequence.iterator();

        Assert.assertEquals("iterator-0", null, iterator.next());
        Assert.assertEquals("iterator-1", "One", iterator.next());
        Assert.assertEquals("iterator-2", null, iterator.next());
        Assert.assertEquals("iterator-3", null, iterator.next());
        Assert.assertEquals("iterator-4", null, iterator.next());
        Assert.assertEquals("iterator-5", null, iterator.next());
        Assert.assertEquals("iterator-6", null, iterator.next());
        Assert.assertEquals("iterator-7", null, iterator.next());
        Assert.assertEquals("iterator-8", "Eight", iterator.next());
        Assert.assertEquals("iterator-9", null, iterator.next());
        Assert.assertFalse("Expected end of Sequence", iterator.hasNext());
    }


    /**
     * Unit test {@link Sequence#append}
     */
    @Test
    public void test_append() {
        Sequence<String> sequence = Sequence.of(String.class);

        Assert.assertEquals("Before: Unexpected Size", 0, sequence.size());
        Assert.assertTrue("Before: isEmpty()", sequence.isEmpty());

        sequence.append("First");

        Assert.assertEquals("First: Unexpected Size", 1, sequence.size());
        Assert.assertFalse("First: isEmpty()", sequence.isEmpty());

        sequence.append("Second");
        sequence.append("Third");

        Assert.assertEquals("Third: Unexpected Size", 3, sequence.size());

        sequence.append(null);

        Assert.assertEquals("null: Unexpected Size", 4, sequence.size());

        sequence.append("final");

        Assert.assertEquals("final: Unexpected Size", 5, sequence.size());

        Iterator<String> iterator = sequence.iterator();

        Assert.assertEquals("iterator-0", "First", iterator.next());
        Assert.assertEquals("iterator-1", "Second", iterator.next());
        Assert.assertEquals("iterator-2", "Third", iterator.next());
        Assert.assertEquals("iterator-3", null, iterator.next());
        Assert.assertEquals("iterator-4", "final", iterator.next());
        Assert.assertFalse("Expected end of Sequence", iterator.hasNext());
    }


    /**
     * Unit test {@link Sequence#set(int, Object)}
     */
    @Test
    public void test_set_wrongType() {
        Sequence<?> sequence = Sequence.of(String.class, "Hello");

        Exception actual = Assert.assertThrows(DocumentException.class,
                () -> ((Sequence<Number>) sequence).set(3, 123));

        Assert.assertEquals("Unexpected error", "Attempt to store Integer in an sequence of String", actual.getMessage());
    }


    /**
     * Unit test {@link Sequence#computeIfAbsent}
     */
    @Test
    public void test_computeIfAbsent() {
        Sequence<String> sequence = Sequence.of(String.class);

        Assert.assertEquals("Empty", "x:10", sequence.computeIfAbsent(10, i -> "x:" + i));
        Assert.assertEquals("Not found", "y:11", sequence.computeIfAbsent(11, i -> "y:" + i));
        Assert.assertEquals("Existing", "y:11", sequence.computeIfAbsent(11, i -> "z:" + i));
    }


    /**
     * Unit test {@link Sequence#getType}
     */
    @Test
    public void test_GetType() {
        Assert.assertEquals("Unexpected String sequence Type", String.class, Sequence.of(String.class).getType());
        Assert.assertEquals("Unexpected Number sequence Type", Number.class, Sequence.of(Number.class).getType());
    }


    /**
     * Unit test {@link Sequence#equals(Object)}
     */
    @Test
    public void test_equals() {
        Sequence<String> zero = Sequence.of(String.class);
        Sequence<String> one_a = Sequence.of(String.class, "A", "B");
        Sequence<String> one_b = Sequence.of(String.class, "A", "B");
        Sequence<String> two = Sequence.of(String.class, "A", "B", "C");
        Sequence<String> three = Sequence.of(String.class, "B", "C");
        Sequence<Number> four = Sequence.of(Number.class);

        Assert.assertFalse("Equals to null", one_a.equals(null));
        Assert.assertFalse("Equals to wrong type", one_a.equals("Hello"));
        Assert.assertTrue("Equals to Self", one_a.equals(one_a));

        Assert.assertTrue("Equals objects: a -> b", one_a.equals(one_b));
        Assert.assertTrue("Equals objects: b -> a", one_b.equals(one_a));
        Assert.assertTrue("Different Types", zero.equals(four));
        Assert.assertFalse("Different Length", one_a.equals(two));
        Assert.assertFalse("Different Content", one_a.equals(three));
    }


    /**
     * Unit test {@link Sequence#hashCode()}
     */
    @Test
    public void test_hashCode() {
        Sequence<String> zero = Sequence.of(String.class);
        Sequence<String> one_a = Sequence.of(String.class, "A", "B");
        Sequence<String> one_b = Sequence.of(String.class, "A", "B");
        Sequence<String> two = Sequence.of(String.class, "A", "B", "C");
        Sequence<String> three = Sequence.of(String.class, "B", "C");
        Sequence<Number> four = Sequence.of(Number.class);

        Assert.assertEquals("Mutable hashcode", one_a.hashCode(), one_a.hashCode());
        Assert.assertEquals("Equals objects", one_a.hashCode(), one_b.hashCode());
        Assert.assertEquals("Different Types", zero.hashCode(), four.hashCode());
        Assert.assertNotEquals("Different Length", one_a.hashCode(), two.hashCode());
        Assert.assertNotEquals("Different Content", one_a.hashCode(), three.hashCode());
    }


    /**
     * Unit test {@link Sequence#hashCode()}
     */
    @Test
    public void test_hasCodeAndEqual_EmptySpace() {
        Sequence<String> noSpace = Sequence.of(String.class, List.of("A", "B", "C"));
        Sequence<String> withSpace = Sequence.of(String.class, 20);

        withSpace.set(0, "A");
        withSpace.set(1, "B");
        withSpace.set(2, "C");

        Assert.assertEquals("test equals()", withSpace, noSpace);
        Assert.assertEquals("test hashCode()", withSpace.hashCode(), noSpace.hashCode());
    }
}