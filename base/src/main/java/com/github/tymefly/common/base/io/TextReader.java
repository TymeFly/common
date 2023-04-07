package com.github.tymefly.common.base.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.github.tymefly.common.base.validate.Preconditions;

/**
 * Read text from a source and apply a set of transformations on it. This class will handle all the IO
 * and ensure that the source stream is closed.
 * The order of the transformations is given by the fluent interface.
 */
@NotThreadSafe
public class TextReader {
    private final InputStream source;
    private final List<BiFunction<Integer, String, String>> transformations;
    private final String name;

    private int lineNumber;


    private TextReader(@Nonnull InputStream source, @Nullable String name) {
        this.source = Preconditions.checkNotNull(source, "No source specified");
        this.name = (name == null ? "" : " '" + name + "'");
        this.transformations = new ArrayList<>();
    }


    /**
     * Factory method used to create a LineReader from a Path
     * @param source    path to source file
     * @return          A fluent interface
     * @throws FailedIoException if the file could not be read
     */
    @Nonnull
    public static TextReader from(@Nonnull Path source) throws FailedIoException {
        return from(source.toFile());
    }


    /**
     * Factory method used to create a LineReader from a File
     * @param source    A file on the local file system
     * @return          A fluent interface
     * @throws FailedIoException if the file could not be read
     */
    @Nonnull
    public static TextReader from(@Nonnull File source) throws FailedIoException {
        FileInputStream stream;

        try {
            stream = new FileInputStream(source);
        } catch (FileNotFoundException e) {
            throw new FailedIoException("Failed to open file " + source.getAbsolutePath(), e);
        }

        return new TextReader(stream, source.getName());
    }

    /**
     * Factory method used to create a LineReader from an InputStream
     * @param source    Stream of data to be read
     * @return          A fluent interface
     */
    @Nonnull
    public static TextReader from(@Nonnull InputStream source) {
        return new TextReader(source, null);
    }


    /**
     * Instructs this LineReader to skip blank lines
     * @return          A fluent interface
     */
    @Nonnull
    public TextReader skipBlanks() {
        return transform(s -> s.isEmpty() ? null : s);
    }


    /**
     * Instructs this LineReader to trim leading and trailing spaces from the lines
     * @return          A fluent interface
     */
    @Nonnull
    public TextReader trim() {
        return transform(String::trim);
    }


    /**
     * Instructs this LineReader to replaces each tab with a space
     * @return          A fluent interface
     */
    @Nonnull
    public TextReader expand() {
        return transform(l -> l.replace('\t', ' '));
    }


    /**
     * Instructs this LineReader to remove trailing comments
     * @param commentString     A string that defines a comment; typically either {@code //} or {@code #}
     * @return          A fluent interface
     */
    @Nonnull
    public TextReader removeComments(@Nonnull String commentString) {
        return transform((i, l) -> removeComments(l, commentString));
    }


    /**
     * Instructs this LineReader to replace a regular expression with a string
     * @param regEx     A Regular expression
     * @param value     The values that replaces the text in the regular expression
     * @return          A fluent interface
     */
    @Nonnull
    public TextReader transform(@Nonnull String regEx, @Nonnull String value) {
        return transform((i, l) -> l.replaceAll(regEx, value));
    }


    /**
     * Performs any arbitrary transformations on the line.
     * @param transformation    A transformation function that will be passed the text on the line
     * @return          A fluent interface
     */
    @Nonnull
    public TextReader transform(@Nonnull Function<String, String> transformation) {
        return transform((i, l) -> transformation.apply(l));
    }


    /**
     * Performs any arbitrary transformations on the line.
     * @param transformation    A transformation function that will be passed the line number and the text on the line
     * @return          A fluent interface
     */
    @Nonnull
    public TextReader transform(@Nonnull BiFunction<Integer, String, String> transformation) {
        transformations.add(transformation);

        return this;
    }


    /**
     * Reads the source, performs all the transformations and collects the results in a mutable list.
     * @return The contents of the source
     * @throws FailedIoException if the source could not be read
     */
    @Nonnull
    public List<String> lines() throws FailedIoException {
        List<String> results = new ArrayList<>();

        forEach((i, l) -> results.add(l));

        return results;
    }

    /**
     * Reads the source, performs all the transformations and collects the results in a String.
     * @return The contents of the source
     * @throws FailedIoException if the source could not be read
     */
    @Nonnull
    public String text() throws FailedIoException {
        StringJoiner buffer = new StringJoiner(System.lineSeparator());

        forEach((i, l) -> buffer.add(l));

        return buffer.toString();
    }


    /**
     * Reads the source, performs all the transformations and applies the {@code action} function
     * @param action    A functions that will be passed the line number and the text on the line
     * @throws FailedIoException if the source could not be read
     */
    public void forEach(@Nonnull BiConsumer<Integer, String> action) throws FailedIoException {
        Preconditions.checkState((lineNumber == 0), "This LineReader has already been processed");

        try (
            Reader stream = new InputStreamReader(source, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(stream)
        ) {
            String line = reader.readLine();

            while (line != null) {
                lineNumber++;

                for (var transformation : transformations) {
                    line = transformation.apply(lineNumber, line);

                    if (line == null) {
                        break;
                    }
                }

                if (line != null) {
                    action.accept(lineNumber, line);
                }

                line = reader.readLine();
            }
        } catch (Exception e) {
            throw new FailedIoException("Failed to load" + name + ". Error on line " + lineNumber, e);
        }
    }


    @Nonnull
    private String removeComments(@Nonnull String line, @Nonnull String commentString) {
        int index = line.indexOf(commentString);

        return (index == -1 ? line : line.substring(0, index));
    }
}
