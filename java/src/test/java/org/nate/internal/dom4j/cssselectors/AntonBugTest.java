package org.nate.internal.dom4j.cssselectors;

import java.util.Set;

import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.junit.Assert;
import org.junit.Test;

/**
 * Simple port of Christer Sandberg's CSS selectors to Dom4j (https://github.com/chrsan/css-selectors)
 */
public class AntonBugTest {
    
    private final Dom4jNodeSelector nodeSelector;
    
    public AntonBugTest() throws Exception {
        SAXReader reader = new SAXReader();
        Document document = reader.read("src/test/resources/anton-bug.xml");
        nodeSelector = new Dom4jNodeSelector(document);
    }
    
    @Test
    public void checkAdjacentSiblings() throws Exception {
        Set<Branch> result = nodeSelector.querySelectorAll("token[tag^=l] + token");
        Assert.assertEquals(3, result.size());
    }
    
    @Test
    public void checkGeneralSiblings() throws Exception {
        Set<Branch> result = nodeSelector.querySelectorAll("token[tag^=l] ~ token");
        Assert.assertEquals(6, result.size());
    }
    
}
