package com.github.tymefly.common.base.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.tymefly.common.base.io.FailedIoException;

/**
 * A helper class that will read attributes from the manifest files in this application
 */
public class ManifestReader {
    /**
     * Attempt to read an {@code attribute} from the manifest files. If it can not be found then a
     * {@literal null} is returned. If the {@code attribute} is defined in more than one manifest file
     * then this class does not guarantee which of the associated values is returned.
     * Values are converted to the required {@code type} using the rules described in {@link Convert}
     * @param attribute         Name of the attribute
     * @param type              Required type of the attribute
     * @param <V>               Required type of the attribute
     * @return                  The value of the desired attribute or
     *                          {@literal null} if the attribute could not be found or is empty
     * @throws FailedIoException if the manifest files could not be read
     * @throws IllegalArgumentException if the value could not be converted to the desired {@code type}
     */
    @Nullable
    public <V> V read(@Nonnull String attribute, @Nonnull Class<V> type)
            throws FailedIoException, IllegalArgumentException {
        return Convert.to(read(attribute), type);
    }


    /**
     * Attempt to read an {@code attribute} from the manifest files. If it can not be found then a
     * {@code defaultValue} is returned. If the {@code attribute} is defined in more than one manifest file
     * then this class does not guarantee which of the associated values is returned
     * Values are converted to the required type using the rules described in {@link Convert}
     * @param attribute         Name of the attribute
     * @param defaultValue      Default value that is returned if the attribute can not be found
     * @param <V>               Required type of the attribute
     * @return                  The value of the desired attribute or
     *                          {@code defaultValue} if the attribute could not be found or is empty
     * @throws FailedIoException if the manifest files could not be read
     * @throws IllegalArgumentException if the value could not be converted to the desired {@code type}
     */
    @Nonnull
    public <V> V read(@Nonnull String attribute, @Nonnull V defaultValue)
            throws FailedIoException, IllegalArgumentException {
        @SuppressWarnings("unchecked")
        Class<V> type = (Class<V>) defaultValue.getClass();
        V value = read(attribute, type);

        return (value == null ? defaultValue : value);
    }


    @Nullable
    private String read(@Nonnull String attribute) {
        String found = null;

        try {
            Enumeration<URL> resources = getClass().getClassLoader()
                    .getResources(JarFile.MANIFEST_NAME);

            while ((found == null) && resources.hasMoreElements()) {
                found = read(resources.nextElement(), attribute);
            }
        } catch (IOException e) {
            throw new FailedIoException("Failed to read attribute " + attribute, e);
        }

        return found;
    }


    @Nullable
    private String read(@Nonnull URL url, @Nonnull String attribute) throws FailedIoException {
        String found;

        try (
            InputStream stream = url.openStream()
        ) {
            Manifest manifest = new Manifest(stream);
            Attributes attr = manifest.getMainAttributes();

            found = attr.getValue(attribute);
        } catch (IOException e) {
            throw new FailedIoException("Failed to read manifest file " + url.toExternalForm(), e);
        }

        if ((found != null) && found.isEmpty()) {
            found = null;
        }

        return found;
    }
}
