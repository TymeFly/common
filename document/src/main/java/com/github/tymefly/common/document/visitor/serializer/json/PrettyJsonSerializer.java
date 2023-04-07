package com.github.tymefly.common.document.visitor.serializer.json;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.google.gson.GsonBuilder;

/**
 * Document Visitor that will generate a Json string with new lines and indents
 */
@NotThreadSafe
public non-sealed class PrettyJsonSerializer extends AbstractJsonSerializer {
    /** Constructor */
    public PrettyJsonSerializer() {
        this(null);
    }


    private PrettyJsonSerializer(@Nullable AbstractJsonSerializer parent) {
        super(parent);
    }


    @Override
    AbstractJsonSerializer construct() {
        return new PrettyJsonSerializer(this);
    }

    @Override
    @Nonnull
    GsonBuilder configureBuilder(@Nonnull GsonBuilder builder) {
        return builder.setPrettyPrinting();
    }
}
