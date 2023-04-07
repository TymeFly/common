package com.github.tymefly.common.base.io;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.Test;

public class TextReaderTest {
    /**
     * Unit test {@link TextReader#from(File)}
     */
    @Test
    public void test_from_File() {
        String fileName = TextReaderTest.class.getResource("/io/single.txt").getFile();
        File file = new File(fileName);

        List<String> actual = TextReader.from(file)
                .lines();

        Assert.assertEquals("Unexpected data read", Collections.singletonList("  Hello World"), actual);
    }

    /**
     * Unit test {@link TextReader#from(File)}
     */
    @Test
    public void test_from_File_Missing() {
        File file = new File("this/does/not/exist.??");

        Assert.assertThrows(FailedIoException.class, () -> TextReader.from(file).lines());
    }

    /**
     * Unit test {@link TextReader#from(Path)}
     */
    @Test
    public void test_from_Path() {
        String fileName = TextReaderTest.class.getResource("/io/single.txt").getFile();
        File file = new File(fileName);

        List<String> actual = TextReader.from(file.toPath())
                .lines();

        Assert.assertEquals("Unexpected data read", Collections.singletonList("  Hello World"), actual);
    }

    /**
     * Unit test {@link TextReader#from(InputStream)}
     */
    @Test
    public void test_from_Stream() {
        InputStream stream = new ByteArrayInputStream("Some Text".getBytes(StandardCharsets.UTF_8));

        List<String> actual = TextReader.from(stream)
                .lines();

        Assert.assertEquals("Unexpected data read", Collections.singletonList("Some Text"), actual);
    }


    /**
     * Unit test {@link TextReader#skipBlanks()}
     */
    @Test
    public void test_SkipBlanks() {
        String fileName = TextReaderTest.class.getResource("/io/blanks.txt").getFile();
        File file = new File(fileName);

        List<String> actual = TextReader.from(file)
                .skipBlanks()
                .lines();

        Assert.assertEquals("Unexpected data read", List.of("One", "Two", "Three"), actual);
    }

    /**
     * Unit test {@link TextReader#trim()}
     */
    @Test
    public void test_Trim() {
        String fileName = TextReaderTest.class.getResource("/io/white.txt").getFile();
        File file = new File(fileName);

        List<String> actual = TextReader.from(file)
                .trim()
                .lines();

        Assert.assertEquals("Unexpected data read",
                List.of("None", "Leading spaces", "Training spaces", "Leading Tabs", "Trailing Tabs"),
                actual);
    }

    /**
     * Unit test {@link TextReader#expand()}
     */
    @Test
    public void test_Expand() {
        String fileName = TextReaderTest.class.getResource("/io/white.txt").getFile();
        File file = new File(fileName);

        List<String> actual = TextReader.from(file)
                .expand()
                .lines();

        Assert.assertEquals("Unexpected data read",
                List.of("None", "  Leading spaces", "Training spaces  ", "   Leading Tabs", "Trailing Tabs   "),
                actual);
    }

    /**
     * Unit test {@link TextReader#removeComments(String)}
     */
    @Test
    public void test_RemoveComments() {
        String fileName = TextReaderTest.class.getResource("/io/comments.txt").getFile();
        File file = new File(fileName);

        List<String> actual = TextReader.from(file)
                .removeComments("//")
                .removeComments("#")
                .lines();

        Assert.assertEquals("Unexpected data read",
                List.of("No Comment", "", "text ", "other", "End"),
                actual);
    }


    /**
     * Unit test {@link TextReader#transform(String, String)}
     */
    @Test
    public void test_transform_RegEx() {
        String fileName = TextReaderTest.class.getResource("/io/blanks.txt").getFile();
        File file = new File(fileName);

        List<String> actual = TextReader.from(file)
                .skipBlanks()
                .transform("[A-Z]", "_")
                .lines();

        Assert.assertEquals("Unexpected data read", List.of("_ne", "_wo", "_hree"), actual);
    }


    /**
     * Unit test {@link TextReader#transform(Function)}
     */
    @Test
    public void test_Transform_Line() {
        String fileName = TextReaderTest.class.getResource("/io/transform.txt").getFile();
        File file = new File(fileName);

        List<String> actual = TextReader.from(file)
                .transform((Function<String, String>) String::toUpperCase)
                .lines();

        Assert.assertEquals("Unexpected data read",
                List.of("HELLO", "WORLD"),
                actual);
    }

    /**
     * Unit test {@link TextReader#transform(BiFunction)}
     */
    @Test
    public void test_Transform_Line_AndNumber() {
        String fileName = TextReaderTest.class.getResource("/io/transform.txt").getFile();
        File file = new File(fileName);

        List<String> actual = TextReader.from(file)
                .transform((i, l) -> "[" + i + "] " + l)
                .lines();

        Assert.assertEquals("Unexpected data read",
                List.of("[1] Hello", "[2] World"),
                actual);
    }

    /**
     * Unit test {@link TextReader#forEach(BiConsumer)}
     */
    @Test
    public void test_Transform_Fail_File() {
        String fileName = TextReaderTest.class.getResource("/io/transform.txt").getFile();
        File file = new File(fileName);
        TextReader reader = TextReader.from(file)
                .transform((i, l) -> {
                    if (i == 2) {
                        throw new RuntimeException("Got line 2!");
                    }

                    return l;
                });

        Exception actual = Assert.assertThrows(FailedIoException.class, reader::lines);

        Assert.assertEquals("Unexpected message", "Failed to load 'transform.txt'. Error on line 2", actual.getMessage());
    }

    /**
     * Unit test {@link TextReader#forEach(BiConsumer)}
     */
    @Test
    public void test_Transform_Fail_Stream() {
        InputStream stream = new ByteArrayInputStream("1 \n2 \n3 \n4".getBytes(StandardCharsets.UTF_8));
        TextReader reader = TextReader.from(stream)
                .transform((i, l) -> {
                    if (i == 3) {
                        throw new RuntimeException("Got line 3!");
                    }

                    return l;
                });

        Exception actual = Assert.assertThrows(FailedIoException.class, reader::lines);

        Assert.assertEquals("Unexpected message", "Failed to load. Error on line 3", actual.getMessage());
    }

    /**
     * Unit test {@link TextReader#text()}
     */
    @Test
    public void test_text() {
        String fileName = TextReaderTest.class.getResource("/io/blanks.txt").getFile();
        File file = new File(fileName);

        String actual = TextReader.from(file)
                .skipBlanks()
                .text()
                .replace("\r", "");

        Assert.assertEquals("Unexpected data read", "One\nTwo\nThree", actual);
    }

    /**
     * Unit test {@link TextReader#forEach(BiConsumer)}
     */
    @Test
    public void test_ForEach() {
        String fileName = TextReaderTest.class.getResource("/io/numbers.txt").getFile();
        File file = new File(fileName);
        Set<Integer> results = new TreeSet<>();

        TextReader.from(file)
                .forEach((i, l) -> results.add(Integer.parseInt(l)));

        Assert.assertEquals("Unexpected data read",
                Set.of(-500, -1, 10, 30, 100),
                results);
    }


    /**
     * Unit test {@link TextReader#forEach(BiConsumer)}
     */
    @Test
    public void test_MultipleTransformations() {
        String fileName = TextReaderTest.class.getResource("/io/multipleLines.txt").getFile();
        File file = new File(fileName);

        List<String> actual = TextReader.from(file)
                .skipBlanks()
                .removeComments("#")            // This will leave the first line blank
                .trim()
                .transform((i, l) -> l + "\t!\t")
                .removeComments("//")           // Will remove some text applied by the transformation
                .expand()
                .trim()                         // Will remove trailing \t added above (it's a leading \t when blank)
                .transform((i, l) -> "" + i + "." + l)
                .lines();

        Assert.assertEquals("Unexpected data read",
                List.of("1.!",
                        "3.leading",
                        "4.trailing space !",
                        "5.with  tabs !",
                        "6.spaces  AND  tabs !",
                        "7.trailing !"),
                actual);
    }



    /**
     * Unit test {@link TextReader#from(File)}
     */
    @Test
    public void test_empty_File() {
        String fileName = TextReaderTest.class.getResource("/io/empty.txt").getFile();
        File file = new File(fileName);

        List<String> actual = TextReader.from(file)
                .expand()
                .lines();

        Assert.assertEquals("Unexpected data read", Collections.emptyList(), actual);
    }
}
