package com.github.tymefly.common.document;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import com.github.tymefly.common.document.key.DocumentKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link Walker}
 */
public class WalkerTest {
    private BiFunction<Document, DocumentKey, String> toWalk;
    private BiFunction<DocumentImpl, WalkerKey, String> whenFound;
    private Supplier<String> whenNotFound;

    private AbstractDocument document;

    private Walker<String> walker;


    @Before
    public void setUp() {
        toWalk = mock(BiFunction.class);
        whenFound = mock(BiFunction.class);
        whenNotFound = mock(Supplier.class);

        document = (AbstractDocument) Document.newInstance()
                .addString(() -> "a.b.c", "data")
                .addString(() -> "key", "value")
                .unmodifiable();

        when(toWalk.apply(any(Document.class), any(DocumentKey.class)))
                .thenReturn("continue");
        when(whenFound.apply(any(DocumentImpl.class), any(WalkerKey.class)))
                .thenReturn("found");
        when(whenNotFound.get())
                .thenReturn("NOT_FOUND");

        walker = new Walker.Builder<String>()
            .toWalk(toWalk)
            .whenFound(whenFound)
            .whenNotFound(whenNotFound)
            .build();
    }

    /**
     * Unit test {@link Walker}
     */
    @Test
    public void test_FoundChild() {
        ArgumentCaptor<Document> child = ArgumentCaptor.forClass(Document.class);
        ArgumentCaptor<DocumentKey> key = ArgumentCaptor.forClass(DocumentKey.class);

        String result = walker.walk(document, () -> "a.b.c");

        Assert.assertEquals("Unexpected result", "continue", result);

        verify(toWalk).apply(child.capture(), key.capture());
        verify(whenFound, never()).apply(any(DocumentImpl.class), any(WalkerKey.class));
        verify(whenNotFound, never()).get();

        Assert.assertEquals("Unexpected child",
                Document.newInstance()
                    .addString(() -> "b.c", "data")
                    .unmodifiable(),
                child.getValue());
        Assert.assertEquals("Unexpected key", "b.c", key.getValue().externalise());
    }


    /**
     * Unit test {@link Walker}
     */
    @Test
    public void test_MissingChild()  {
        String result = walker.walk(document, () -> "x.y.z");

        Assert.assertEquals("Unexpected result", "NOT_FOUND", result);

        verify(toWalk, never()).apply(any(DocumentImpl.class), any(WalkerKey.class));
        verify(whenFound, never()).apply(any(DocumentImpl.class), any(WalkerKey.class));
        verify(whenNotFound).get();
    }


    /**
     * Unit test {@link Walker}
     */
    @Test
    public void test_FoundUltimateChild() {
        ArgumentCaptor<DocumentImpl> child = ArgumentCaptor.forClass(DocumentImpl.class);
        ArgumentCaptor<WalkerKey> key = ArgumentCaptor.forClass(WalkerKey.class);

        String result = walker.walk(document, () -> "key");

        Assert.assertEquals("Unexpected result", "found", result);

        verify(toWalk, never()).apply(any(DocumentImpl.class), any(WalkerKey.class));
        verify(whenFound).apply(child.capture(), key.capture());
        verify(whenNotFound, never()).get();

        Assert.assertEquals("Unexpected child", document, child.getValue());
        Assert.assertEquals("Unexpected key", "key", key.getValue().externalise());
    }
}