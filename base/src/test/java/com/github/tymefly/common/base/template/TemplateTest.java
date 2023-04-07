package com.github.tymefly.common.base.template;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;

/**
 * Unit test for {@link Template}
 */
public class TemplateTest {

    /**
     * Unit test {@link Template#load(String)}
     */
    @Test
    public void test_forText_NoReplacements() {
        String actual = new Template()
                .load("/template/simple.txt")
                .replace("\r", "");

        Assert.assertEquals("Unexpected text",
                    "Title\n" +
                    "~~~~~\n" +
                    "\n" +
                    "   Simple Text\n" +
                    "Hello",
                actual);
    }

    /**
     * Unit test {@link Template#load(String)}
     */
    @Test
    public void test_forText_Unmatched() {
        String actual = new Template()
                .load("/template/general.txt");

        Assert.assertEquals("Unexpected text", "Hello !", actual);
    }


    /**
     * Unit test {@link Template#forText(String, Object)}
     */
    @Test
    public void test_forText_General() {
        String actual = new Template()
                .forText("xxxx", "World")
                .load("/template/general.txt");

        Assert.assertEquals("Unexpected text", "Hello World!", actual);
    }


    /**
     * Unit test {@link Template#forText(String, TemporalAccessor)}
     */
    @Test
    public void test_forText_Time() {
        String actual = new Template()
                .forText("now", Instant.now())
                .load("/template/time.txt");

        Assert.assertTrue("Unexpected text: " + actual,
                actual.matches("The time is \\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2} [A-Za-z/]+!"));
    }


    /**
     * Unit test {@link Template#forText(String, TemporalAccessor)}
     */
    @Test
    public void test_forText_Multiple() {
        String expected =
                "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2} [A-Za-z/]+ : World\n" +
                "~~~~~~\n" +
                "\n" +
                " indented\n" +
                "\n" +
                "Hello World 123";

        String actual = new Template()
                .forText("now", Instant.now())
                .forText("___", "World")
                .forText("num", 123)
                .load("/template/multiple.txt")
                .replace("\r", "");

        Assert.assertTrue("Unexpected text: " + actual, actual.matches(expected));
    }

    /**
     * Unit test {@link Template#forText(String, TemporalAccessor)}
     */
    @Test
    public void test_for_MultipleDateFormats() {
        DateTimeFormatter dateFormat = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE)
            .toFormatter(Locale.getDefault(Locale.Category.FORMAT));

        String expected =
                "The time is \\d{2}:\\d{2}:\\d{2}(\\.\\d+)?. The date is \\d{4}-\\d{2}-\\d{2}\n" +
                "yyyy";

        String actual = new Template()
                .withDateFormat(ISO_LOCAL_DATE)
                .forText("time-only", ISO_LOCAL_TIME, LocalDateTime.now())
                .forText("time-date", LocalDateTime.now())
                .forText("xxxx", "yyyy")
                .load("/template/timesTwo.txt")
                .replace("\r", "");

        Assert.assertTrue("Unexpected text: " + actual, actual.matches(expected));
    }
}