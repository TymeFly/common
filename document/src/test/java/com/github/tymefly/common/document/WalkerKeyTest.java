package com.github.tymefly.common.document;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link WalkerKey}
 */
public class WalkerKeyTest {
    private WalkerKey simple = WalkerKey.from(() -> "simple");
    private WalkerKey indexed = WalkerKey.from(() -> "indexed[1]");
    private WalkerKey withChild = WalkerKey.from(() -> "root.child");
    private WalkerKey full = WalkerKey.from(() -> "root[1].mid.child[3]");
    private WalkerKey shifted = WalkerKey.from(() -> "root[1].mid.child[3]").shift();
    private WalkerKey shifted2 = WalkerKey.from(() -> "root[1].mid.child[3]").shift().shift();


    /**
     * Unit test {@link WalkerKey#fullKey()}
     */
    @Test
    public void test_fullKey() {
        Assert.assertEquals("'simple' has unexpected FullKey", "simple", simple.fullKey().externalise());
        Assert.assertEquals("'indexed' has unexpected FullKey", "indexed[1]", indexed.fullKey().externalise());
        Assert.assertEquals("'withChild' has unexpected FullKey", "root.child", withChild.fullKey().externalise());
        Assert.assertEquals("'full' has unexpected FullKey", "root[1].mid.child[3]", full.fullKey().externalise());
        Assert.assertEquals("'shifted' has unexpected FullKey", "root[1].mid.child[3]", shifted.fullKey().externalise());
        Assert.assertEquals("'shifted2' has unexpected FullKey", "root[1].mid.child[3]", shifted2.fullKey().externalise());
    }

    /**
     * Unit test {@link WalkerKey#externalise()}
     */
    @Test
    public void test_externalise() {
        Assert.assertEquals("'simple' unexpected external form", "simple", simple.externalise());
        Assert.assertEquals("'indexed' unexpected external form", "indexed[1]", indexed.externalise());
        Assert.assertEquals("'withChild' unexpected external form", "root.child", withChild.externalise());
        Assert.assertEquals("'full' unexpected external form", "root[1].mid.child[3]", full.externalise());
        Assert.assertEquals("'shifted' unexpected external form", "mid.child[3]", shifted.externalise());
        Assert.assertEquals("'shifted2' unexpected external form", "child[3]", shifted2.externalise());
    }

    /**
     * Unit test {@link WalkerKey#currentElement()}
     */
    @Test
    public void test_fFirstElement() {
        Assert.assertEquals("'simple' unexpected firstElement()", "simple", simple.currentElement());
        Assert.assertEquals("'indexed' unexpected firstElement()", "indexed[1]", indexed.currentElement());
        Assert.assertEquals("'withChild' unexpected firstElement()", "root", withChild.currentElement());
        Assert.assertEquals("'full' unexpected firstElement()", "root[1]", full.currentElement());
        Assert.assertEquals("'shifted' unexpected firstElement()", "mid", shifted.currentElement());
        Assert.assertEquals("'shifted2' unexpected firstElement()", "child[3]", shifted2.currentElement());
    }

    /**
     * Unit test {@link WalkerKey#hasChildren()}
     */
    @Test
    public void test_hasChildren() {
        Assert.assertFalse("'simple' unexpected hasChildren()", simple.hasChildren());
        Assert.assertFalse("'indexed' unexpected hasChildren()", indexed.hasChildren());
        Assert.assertTrue("'withChild' unexpected hasChildren()", withChild.hasChildren());
        Assert.assertTrue("'full' unexpected hasChildren()", full.hasChildren());
        Assert.assertTrue("'shifted' unexpected hasChildren()", shifted.hasChildren());
        Assert.assertFalse("'shifted2' unexpected hasChildren()", shifted2.hasChildren());
    }

    /**
     * Unit test {@link WalkerKey#simpleKey()}
     */
    @Test
    public void test_SimpleKey() {
        Assert.assertEquals("'simple' unexpected simpleKey()", "simple", simple.simpleKey());
        Assert.assertEquals("'indexed' unexpected simpleKey()", "indexed", indexed.simpleKey());
        Assert.assertEquals("'withChild' unexpected simpleKey()", "root", withChild.simpleKey());
        Assert.assertEquals("'full' unexpected simpleKey()", "root", full.simpleKey());
        Assert.assertEquals("'shifted' unexpected simpleKey()", "mid", shifted.simpleKey());
        Assert.assertEquals("'shifted2' unexpected simpleKey()", "child", shifted2.simpleKey());
    }

    /**
     * Unit test {@link WalkerKey#hasIndex()}
     */
    @Test
    public void test_hasIndex() {
        Assert.assertFalse("'simple' unexpected hasIndex()", simple.hasIndex());
        Assert.assertTrue("'indexed' unexpected hasIndex()", indexed.hasIndex());
        Assert.assertFalse("'withChild' unexpected hasIndex()", withChild.hasIndex());
        Assert.assertTrue("'full' unexpected hasIndex()", full.hasIndex());
        Assert.assertFalse("'shifted' unexpected hasIndex()", shifted.hasIndex());
        Assert.assertTrue("'shifted2' unexpected hasIndex()", shifted2.hasIndex());
    }

    /**
     * Unit test {@link WalkerKey#index()}
     */
    @Test
    public void test_index() {
        Assert.assertEquals("'simple' unexpected index()", -1, simple.index());
        Assert.assertEquals("'indexed' unexpected index()", 1, indexed.index());
        Assert.assertEquals("'withChild' unexpected index()", -1, withChild.index());
        Assert.assertEquals("'full' unexpected index()", 1, full.index());
        Assert.assertEquals("'shifted' unexpected index()", -1, shifted.index());
        Assert.assertEquals("'shifted2' unexpected index()", 3, shifted2.index());
    }

    /**
     * Unit test {@link WalkerKey#simplePath()}
     */
    @Test
    public void test_SimplePath() {
        Assert.assertEquals("'simple' unexpected simplePath()", "simple", simple.simplePath());
        Assert.assertEquals("'indexed' unexpected simplePath()", "indexed", indexed.simplePath());
        Assert.assertEquals("'withChild' unexpected simplePath()", "root", withChild.simplePath());
        Assert.assertEquals("'full' unexpected simplePath()", "root", full.simplePath());
        Assert.assertEquals("'shifted' unexpected simplePath()", "root[1].mid", shifted.simplePath());
        Assert.assertEquals("'shifted2' unexpected simplePath()", "root[1].mid.child", shifted2.simplePath());
    }

    /**
     * Unit test {@link WalkerKey#elementPath()}
     */
    @Test
    public void test_ElementPath() {
        Assert.assertEquals("'simple' unexpected elementPath()", "simple", simple.elementPath());
        Assert.assertEquals("'indexed' unexpected elementPath()", "indexed[1]", indexed.elementPath());
        Assert.assertEquals("'withChild' unexpected elementPath()", "root", withChild.elementPath());
        Assert.assertEquals("'full' unexpected elementPath()", "root[1]", full.elementPath());
        Assert.assertEquals("'shifted' unexpected elementPath()", "root[1].mid", shifted.elementPath());
        Assert.assertEquals("'shifted2' unexpected elementPath()", "root[1].mid.child[3]", shifted2.elementPath());
    }
}