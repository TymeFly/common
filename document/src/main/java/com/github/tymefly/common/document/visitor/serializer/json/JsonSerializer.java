package com.github.tymefly.common.document.visitor.serializer.json;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * Document Visitor that will generate a single line of Json
 */
@NotThreadSafe
public non-sealed class JsonSerializer extends AbstractJsonSerializer {
    /** Constructor */
    public JsonSerializer() {
        this(null);
    }


    private JsonSerializer(@Nullable AbstractJsonSerializer parent) {
        super(parent);
    }


    @Override
    AbstractJsonSerializer construct() {
        return new JsonSerializer(this);
    }
}
