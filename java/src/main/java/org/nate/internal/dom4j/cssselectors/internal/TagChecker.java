package org.nate.internal.dom4j.cssselectors.internal;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.dom4j.Branch;
import org.dom4j.Node;
import org.nate.internal.dom4j.cssselectors.DOMHelper;

import se.fishtank.css.selectors.NodeSelectorException;
import se.fishtank.css.selectors.Selector;
import se.fishtank.css.util.Assert;
/**
 * Simple port of Christer Sandberg's CSS selectors to Dom4j (https://github.com/chrsan/css-selectors)
 */
public class TagChecker implements NodeTraversalChecker {
	   
    /** The selector to check against. */
    private final Selector selector;
    
    /** The set of nodes to check. */
    private Set<Branch> nodes;
    
    /** The result of the checks. */
    private Set<Branch> result;

	public TagChecker(Selector selector) {
        Assert.notNull(selector, "selector is null!");
        this.selector = selector;
	}

	@Override
	public Set<Branch> check(Set<Branch> nodes) throws NodeSelectorException {
        Assert.notNull(nodes, "nodes is null!");
        this.nodes = nodes;
        result = new LinkedHashSet<Branch>();
        switch (selector.getCombinator()) {
        case DESCENDANT:
            getDescentantElements();
            break;
        case CHILD:
            getChildElements();
            break;
        case ADJACENT_SIBLING:
            getAdjacentSiblingElements();
            break;
        case GENERAL_SIBLING:
            getGeneralSiblingElements();
            break;
        }
        
        return result;
    }
    
    /**
     * Get descendant elements.
     * 
     * @see <a href="http://www.w3.org/TR/css3-selectors/#descendant-combinators">Descendant combinator</a>
     * 
     * @throws NodeSelectorException If one of the nodes have an illegal type.
     */
	private void getDescentantElements() throws NodeSelectorException {
        for (Branch node : nodes) {
        	getDescentantElements(node);
        }
    }
    
    private void getDescentantElements(Branch node) {
    	String tagName = selector.getTagName();
    	Iterator<Node> nodeIterator = node.nodeIterator();
    	while (nodeIterator.hasNext()) {
    		Node child = nodeIterator.next();
    		if (child.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
    			if (tagName.equals("*") || child.getName().equals(tagName)) {
    	    		result.add((Branch) child);
    	    	}
    			getDescentantElements((Branch) child);
    		}
    	}
	}

	/**
     * Get child elements.
     * 
     * @see <a href="http://www.w3.org/TR/css3-selectors/#child-combinators">Child combinators</a>
     */
    private void getChildElements() {
    	String tag = selector.getTagName();
        for (Branch node : nodes) {
        	Iterator<Node> nodeIterator = node.nodeIterator();
        	while (nodeIterator.hasNext()) {
        		Node child = nodeIterator.next();
        		if (child.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE) {
                    continue;
                }
                if (tag.equals(child.getName()) || tag.equals(Selector.UNIVERSAL_TAG)) {
                    result.add((Branch) child);
                }
            }
        }
    }
    
    /**
     * Get adjacent sibling elements.
     * 
     * @see <a href="http://www.w3.org/TR/css3-selectors/#adjacent-sibling-combinators">Adjacent sibling combinator</a>
     */
    private void getAdjacentSiblingElements() {
    	String tag = selector.getTagName();
        for (Branch node : nodes) {
        	Branch n = DOMHelper.getNextSiblingElement(node);
            if (n != null) {
                if (tag.equals(n.getName()) || tag.equals(Selector.UNIVERSAL_TAG)) {
                    result.add(n);
                }
            }
        }
    }
    
    /**
     * Get general sibling elements.
     * 
     * @see <a href="http://www.w3.org/TR/css3-selectors/#general-sibling-combinators">General sibling combinator</a>
     */
    private void getGeneralSiblingElements() {
        for (Branch node : nodes) {
        	Branch n = DOMHelper.getNextSiblingElement(node);
            while (n != null) {
                if (selector.getTagName().equals(n.getName()) ||
                        selector.getTagName().equals(Selector.UNIVERSAL_TAG)) {
                    result.add(n);
                }
                
                n = DOMHelper.getNextSiblingElement(n);
            }
        }
    }

}
