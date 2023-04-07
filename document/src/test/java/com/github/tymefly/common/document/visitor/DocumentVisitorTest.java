package com.github.tymefly.common.document.visitor;

import com.github.tymefly.common.document.Document;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link DocumentVisitor}
 */
public class DocumentVisitorTest {
    private DocumentVisitor<String> visitor;

    @Before
    public void setUp() {
        visitor = mock(DocumentVisitor.class);

        when(visitor.initialise(any(VisitorContext.class))).thenReturn(visitor);
        when(visitor.nullValue(any(VisitorKey.class))).thenReturn(visitor);
        when(visitor.stringValue(any(VisitorKey.class), anyString())).thenReturn(visitor);
        when(visitor.numericValue(any(VisitorKey.class), any(Number.class))).thenReturn(visitor);
        when(visitor.booleanValue(any(VisitorKey.class), anyBoolean())).thenReturn(visitor);
        when(visitor.enumValue(any(VisitorKey.class), any(Enum.class))).thenReturn(visitor);
        when(visitor.beginChild(any(VisitorKey.class))).thenReturn(visitor);
        when(visitor.endChild(any(VisitorKey.class))).thenReturn(visitor);
        when(visitor.beginSequence(any(VisitorKey.class), any(Class.class), anyInt())).thenReturn(visitor);
        when(visitor.endSequence(any(VisitorKey.class))).thenReturn(visitor);
        when(visitor.isComplete()).thenReturn(false);
        when(visitor.process()).thenReturn("Done");
    }

    /**
     * Unit test {@link DocumentVisitor#initialise}
     */
    @Test
    public void test_Initialise() {
        ArgumentCaptor<VisitorContext> contextArg = ArgumentCaptor.forClass(VisitorContext.class);

        Document doc = Document.newInstance();
        String result = doc.accept(visitor);

        Assert.assertEquals("Unexpected result", "Done", result);

        verify(visitor).initialise(contextArg.capture());

        Assert.assertSame("Unexpected reader", doc, contextArg.getValue().reader());
    }
}