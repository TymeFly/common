package com.github.tymefly.common.document;

import java.util.function.BiConsumer;
import java.util.function.Function;

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
 * Unit test for {@link Inserter}
 */
public class InserterTest {
    private BiConsumer<DocumentImpl, WalkerKey> whenFound;
    private Function<AbstractDocument<?>, ? extends AbstractDocument<Document>> constructor;
    private BiConsumer<WritableDocument<?>, DocumentKey> toWalk;

    private AbstractDocument<?> document;
    private AbstractDocument<?> child;

    private Inserter inserter;


    @Before
    public void setUp() {
        whenFound = mock(BiConsumer.class);
        constructor = mock(Function.class);
        toWalk = mock(BiConsumer.class);

        document = (AbstractDocument<?>) Document.newInstance();
        child = (AbstractDocument<Document>) Document.empty();

        when(constructor.apply(null))
            .then(a -> child);

        inserter = new Inserter.Builder()
            .toWalk(toWalk)
            .whenFound(whenFound)
            .constructBy(constructor)
            .build();
    }

    /**
     * Unit test {@link Walker}
     */
    @Test
    public void test_FoundChild_Document() {
        ArgumentCaptor<WritableDocument<?>> child = ArgumentCaptor.forClass(WritableDocument.class);
        ArgumentCaptor<DocumentKey> key = ArgumentCaptor.forClass(DocumentKey.class);

        document.addString(() -> "a.key", "value");

        inserter.insert(document, () -> "a.b.c");

        verify(whenFound, never()).accept(any(DocumentImpl.class), any(WalkerKey.class));
        verify(constructor, never()).apply(null);
        verify(toWalk).accept(child.capture(), key.capture());

        Assert.assertEquals("Unexpected child", Document.newInstance().addString(() -> "key", "value"), child.getValue());
        Assert.assertEquals("Unexpected key", "b.c", key.getValue().externalise());
    }

    /**
     * Unit test {@link Walker}
     */
    @Test
    public void test_NotFoundChild_Document() {
        ArgumentCaptor<WritableDocument<?>> child = ArgumentCaptor.forClass(WritableDocument.class);
        ArgumentCaptor<DocumentKey> key = ArgumentCaptor.forClass(DocumentKey.class);

        inserter.insert(document, () -> "a.b.c");

        verify(whenFound, never()).accept(any(DocumentImpl.class), any(WalkerKey.class));
        verify(constructor).apply(null);
        verify(toWalk).accept(child.capture(), key.capture());

        Assert.assertSame("Unexpected child", this.child, child.getValue());
        Assert.assertEquals("Unexpected key", "b.c", key.getValue().externalise());
    }


    /**
     * Unit test {@link Walker}
     */
    @Test
    public void test_FoundChild_Sequence() {
        ArgumentCaptor<WritableDocument<?>> child = ArgumentCaptor.forClass(WritableDocument.class);
        ArgumentCaptor<DocumentKey> key = ArgumentCaptor.forClass(DocumentKey.class);

        document.addString(() -> "a[2].key", "value");

        inserter.insert(document, () -> "a[2].b.c");

        verify(whenFound, never()).accept(any(DocumentImpl.class), any(WalkerKey.class));
        verify(constructor, never()).apply(any(AbstractDocument.class));
        verify(toWalk).accept(child.capture(), key.capture());

        Assert.assertEquals("Unexpected child", Document.newInstance().addString(() -> "key", "value"), child.getValue());
        Assert.assertEquals("Unexpected key", "b.c", key.getValue().externalise());
    }

    /**
     * Unit test {@link Walker}
     */
    @Test
    public void test_NotFoundChild_Sequence() {
        ArgumentCaptor<WritableDocument<?>> child = ArgumentCaptor.forClass(WritableDocument.class);
        ArgumentCaptor<DocumentKey> key = ArgumentCaptor.forClass(DocumentKey.class);

        inserter.insert(document, () -> "a[2].b.c");

        verify(whenFound, never()).accept(any(DocumentImpl.class), any(WalkerKey.class));
        verify(constructor).apply(null);
        verify(toWalk).accept(child.capture(), key.capture());

        Assert.assertSame("Unexpected child", this.child, child.getValue());
        Assert.assertEquals("Unexpected key", "b.c", key.getValue().externalise());
    }


    /**
     * Unit test {@link Walker}
     */
    @Test
    public void test_FoundUltimateChild() {
        ArgumentCaptor<DocumentImpl> foundChild = ArgumentCaptor.forClass(DocumentImpl.class);
        ArgumentCaptor<WalkerKey> foundKey = ArgumentCaptor.forClass(WalkerKey.class);

        inserter.insert(document, () -> "key");

        verify(whenFound).accept(foundChild.capture(), foundKey.capture());
        verify(constructor, never()).apply(any(AbstractDocument.class));
        verify(toWalk, never()).accept(any(WritableDocument.class), any(DocumentKey.class));

        Assert.assertEquals("Unexpected foundChild", document, foundChild.getValue());
        Assert.assertEquals("Unexpected foundKey", "key", foundKey.getValue().externalise());
    }
}