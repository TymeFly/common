package com.github.tymefly.common.document;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link VisitorKeyImpl}
 */
public class KeyImplTest {
    /**
     * Unit test {@link VisitorKeyImpl}
     */
    @Test
    public void test_append_root() {
        VisitorKeyImpl key = new VisitorKeyImpl(null, "root");

        Assert.assertEquals("simpleKey", "root", key.simpleKey());
        Assert.assertEquals("index", -1, key.getIndex());
        Assert.assertEquals("element", "root", key.element());
        Assert.assertEquals("simpleKeyPath", "root", key.simpleKeyPath());
        Assert.assertEquals("fullPath", "root", key.fullPath());
        Assert.assertEquals("documentKey", "root", key.documentKey().externalise());
    }


    /**
     * Unit test {@link VisitorKeyImpl}
     */
    @Test
    public void test_append_index() {
        VisitorKeyImpl root = new VisitorKeyImpl(null, "root");
        VisitorKeyImpl key = new VisitorKeyImpl(root, 3);

        Assert.assertEquals("simpleKey", "root", key.simpleKey());
        Assert.assertEquals("index", 3, key.getIndex());
        Assert.assertEquals("element", "root[3]", key.element());
        Assert.assertEquals("simpleKeyPath", "root", key.simpleKeyPath());
        Assert.assertEquals("fullPath", "root[3]", key.fullPath());
        Assert.assertEquals("documentKey", "root[3]", key.documentKey().externalise());
    }

    /**
     * Unit test {@link VisitorKeyImpl}
     */
    @Test
    public void test_updateIndex() {
        VisitorKeyImpl root = new VisitorKeyImpl(null, "root");
        VisitorKeyImpl child1 = new VisitorKeyImpl(root, 3);
        VisitorKeyImpl child2 = new VisitorKeyImpl(root, 99);

        Assert.assertEquals("with index: simpleKey", "root", child2.simpleKey());
        Assert.assertEquals("with index: index", 99, child2.getIndex());
        Assert.assertEquals("with index: element", "root[99]", child2.element());
        Assert.assertEquals("with index: simpleKeyPath", "root", child2.simpleKeyPath());
        Assert.assertEquals("with index: fullPath", "root[99]", child2.fullPath());
        Assert.assertEquals("with index: documentKey", "root[99]", child2.documentKey().externalise());

        Assert.assertEquals("removed index: simpleKey", "root", root.simpleKey());
        Assert.assertEquals("removed index: index", -1, root.getIndex());
        Assert.assertEquals("removed index: element", "root", root.element());
        Assert.assertEquals("removed index: simpleKeyPath", "root", root.simpleKeyPath());
        Assert.assertEquals("removed index: fullPath", "root", root.fullPath());
        Assert.assertEquals("removed index: documentKey", "root", root.documentKey().externalise());
    }


    /**
     * Unit test {@link VisitorKeyImpl}
     */
    @Test
    public void test_append_child() {
        VisitorKeyImpl root = new VisitorKeyImpl(null, "root");
        VisitorKeyImpl key = new VisitorKeyImpl(root, "child");

        Assert.assertEquals("simpleKey", "child", key.simpleKey());
        Assert.assertEquals("index", -1, key.getIndex());
        Assert.assertEquals("element", "child", key.element());
        Assert.assertEquals("simpleKeyPath", "root.child", key.simpleKeyPath());
        Assert.assertEquals("fullPath", "root.child", key.fullPath());
        Assert.assertEquals("documentKey", "root.child", key.documentKey().externalise());
    }


    /**
     * Unit test {@link VisitorKeyImpl}
     */
    @Test
    public void test_truncate_HappyPath() {
        VisitorKeyImpl root = new VisitorKeyImpl(null, "root");
        VisitorKeyImpl root0 = new VisitorKeyImpl(root, 0);
        VisitorKeyImpl child = new VisitorKeyImpl(root0, "child");
        VisitorKeyImpl child1 = new VisitorKeyImpl(child, 1);

        Assert.assertEquals("child 1: simpleKey", "child", child1.simpleKey());
        Assert.assertEquals("child 1: index", 1, child1.getIndex());
        Assert.assertEquals("child 1: element", "child[1]", child1.element());
        Assert.assertEquals("child 1: simpleKeyPath", "root[0].child", child1.simpleKeyPath());
        Assert.assertEquals("child 1: fullPath", "root[0].child[1]", child1.fullPath());
        Assert.assertEquals("child 1: docKey", "root[0].child[1]", child1.documentKey().externalise());

        VisitorKeyImpl child2 = new VisitorKeyImpl(child, 2);

        Assert.assertEquals("child 2: simpleKey", "child", child2.simpleKey());
        Assert.assertEquals("child 2: index", 2, child2.getIndex());
        Assert.assertEquals("child 2: element", "child[2]", child2.element());
        Assert.assertEquals("child 2: simpleKeyPath", "root[0].child", child2.simpleKeyPath());
        Assert.assertEquals("child 2: fullPath", "root[0].child[2]", child2.fullPath());
        Assert.assertEquals("child 2: docKey", "root[0].child[2]", child2.documentKey().externalise());

        Assert.assertEquals("remove index: simpleKey", "child", child.simpleKey());
        Assert.assertEquals("remove index: index", -1, child.getIndex());
        Assert.assertEquals("remove index: element", "child", child.element());
        Assert.assertEquals("remove index: fullPath", "root[0].child", child.fullPath());
        Assert.assertEquals("remove index: docKey", "root[0].child", child.documentKey().externalise());

        Assert.assertEquals("remove child 2: simpleKey", "root", root0.simpleKey());
        Assert.assertEquals("remove child 2: index", 0, root0.getIndex());
        Assert.assertEquals("remove child 2: element", "root[0]", root0.element());
        Assert.assertEquals("remove child 2: simpleKeyPath", "root", root0.simpleKeyPath());
        Assert.assertEquals("remove child 2: fullPath", "root[0]", root0.fullPath());
        Assert.assertEquals("remove child 2: docKey", "root[0]", root0.documentKey().externalise());

        VisitorKeyImpl other = new VisitorKeyImpl(root0, "other");

        Assert.assertEquals("other: simpleKey", "other", other.simpleKey());
        Assert.assertEquals("other: index", -1, other.getIndex());
        Assert.assertEquals("other: element", "other", other.element());
        Assert.assertEquals("other: fullPath", "root[0].other", other.fullPath());
        Assert.assertEquals("other: docKey", "root[0].other", other.documentKey().externalise());
    }
}