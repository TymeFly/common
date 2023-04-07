package com.github.tymefly.common.document.parse;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.github.tymefly.common.base.io.FailedIoException;
import com.github.tymefly.common.base.validate.Preconditions;
import com.github.tymefly.common.document.Document;
import com.github.tymefly.common.document.DocumentException;
import com.github.tymefly.common.document.WritableDocument;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Read Json formatted data with in the limits of the Document interface.
 * <ul>
 *  <li>Multi-dimensional arrays are not supported</li>
 *  <li>Arrays can not contain primitive types and structures</li>
 *  <li>Arrays of mixed types are converted to arrays of Strings</li>
 * </ul>
 */
public class JsonParser implements DocumentParser {
    private enum ElementType {
        None, Null, String, Number, Boolean, Array, Object, Mixed
    }


    @Override
    public void load(@Nonnull WritableDocument<?> target,
                     @Nonnull InputStream source) throws FailedIoException, DocumentException {
        try (
            Reader in = new InputStreamReader(source, StandardCharsets.UTF_8)
        ) {
            JsonObject root = new Gson().fromJson(in, JsonObject.class);

            populate(target, root);
        } catch (DocumentException e) {
            throw e;
        } catch (IOException e) {
            throw new FailedIoException("Failed to load Json", e);
        } catch (Exception e) {
            throw new DocumentException("Failed to parse Json", e);
        }
    }


    @Nonnull
    private <D extends WritableDocument<?>> D populate(@Nonnull D parent, @Nonnull JsonObject child) {
        for (var entry : child.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            if (value instanceof JsonNull) {
                parent.addString(() -> key, null);
            } else if (value instanceof JsonPrimitive) {
                populate(parent, key, (JsonPrimitive) value);
            } else if (value instanceof JsonObject) {
                parent.addDocument(() -> key, populate(Document.newInstance(), (JsonObject) value));
            } else if (value instanceof JsonArray) {
                populate(parent, key, (JsonArray) value);
            } else {                                    // Should not happen
                throw new DocumentException("Key '%s' has unexpected type '%s'",
                            key, value.getClass().getSimpleName());
            }
        }

        return parent;
    }


    private <D extends WritableDocument<?>> void populate(@Nonnull D parent,
                                                          @Nonnull String key,
                                                          @Nonnull JsonPrimitive value) {
        if (value.isString()) {
            parent.addString(() -> key, value.getAsString());
        } else if (value.isNumber()) {
            parent.addNumber(() -> key, value.getAsBigDecimal());
        } else if (value.isBoolean()) {
            parent.addBoolean(() -> key, value.getAsBoolean());
        } else {                                        // Should not happen
            throw new DocumentException("Unexpected key '%s' has unexpected type '%s'", key, value);
        }
    }


    private <D extends WritableDocument<?>> void populate(@Nonnull D parent,
                                                          @Nonnull String key,
                                                          @Nonnull JsonArray values) {
        ElementType arrayType = determineType(values);

        if (arrayType == ElementType.None) {
            // Empty array, so create an array of Strings to preserve the structure
            parent.addStrings(() -> key, Collections.emptyList());
        } else if (arrayType == ElementType.Mixed) {
            parent.addStrings(() -> key, asList(String.class, values));     // Mixed Type, so use strings
        } else if (arrayType == ElementType.String) {
            parent.addStrings(() -> key, asList(String.class, values));
        } else if (arrayType == ElementType.Number) {
            parent.addNumbers(() -> key, asList(Number.class, values));
        } else if (arrayType == ElementType.Boolean) {
            parent.addBooleans(() -> key, asList(Boolean.class, values));
        } else if (arrayType == ElementType.Object) {
            parent.addDocuments(() -> key, populate(values));
        } else {                            // Block arrays of arrays
            throw new DocumentException("Unexpected key '%s' is an array of unexpected type '%s'", key, arrayType);
        }
    }


    @Nonnull
    private List<Document> populate(@Nonnull JsonArray values) {
        List<Document> children = new ArrayList<>(values.size());

        for (var value : values) {
            Document child = populate(Document.newInstance(), value.getAsJsonObject());

            children.add(child);
        }

        return children;
    }


    /**
     * Returns the type of the values in the list
     */
    @Nonnull
    private ElementType determineType(@Nonnull JsonArray values) {
        ElementType arrayType = ElementType.None;

        for (var test : values) {
            ElementType elementType = determineType(test);

            if (elementType == ElementType.Null) {
                // do nothing - do not use a 'null' value in the decision
            } else if (arrayType == ElementType.None) {
                arrayType = elementType;
            } else if (arrayType == elementType) {
                // do nothing - type has not changed
            } else {
                arrayType = ElementType.Mixed;
            }
        }

        return arrayType;
    }


    @Nonnull
    private ElementType determineType(@Nonnull JsonElement element) {
        ElementType type;

        if (element.isJsonNull()) {
            type = ElementType.Null;
        } else if (element.isJsonObject()) {
            type = ElementType.Object;
        } else if (element.isJsonArray()) {
            type = ElementType.Array;
        } else if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();

            if (primitive.isString()) {
                type = ElementType.String;
            } else  if (primitive.isNumber()) {
                type = ElementType.Number;
            } else if (primitive.isBoolean()) {
                type = ElementType.Boolean;
            } else {
                type = null;
            }
        } else {
            type = null;
        }

        return Preconditions.checkNotNull(type, "Unexpected Json type for %s", element);
    }


    @Nonnull
    private <T> List<T> asList(@Nonnull Class<T> type, @Nonnull JsonArray values) {
        List<T> result = new ArrayList<>(values.size());

        for (var value : values) {
            T element;

            if (value.isJsonNull()) {
                element = null;
            } else if (type == String.class) {
                element = type.cast(value.getAsString());
            } else if (type == Number.class) {
                element = type.cast(value.getAsBigDecimal());
            } else if (type == Boolean.class) {
                element = type.cast(value.getAsBoolean());
            } else {                    // Should not happen
                throw new DocumentException("Unexpected type %s", type.getSimpleName());
            }

            result.add(element);
        }

        return result;
    }
}
