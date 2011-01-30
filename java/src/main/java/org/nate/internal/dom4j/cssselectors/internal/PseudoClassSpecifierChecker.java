package org.nate.internal.dom4j.cssselectors.internal;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.dom4j.Branch;
import org.dom4j.Node;
import org.nate.internal.dom4j.cssselectors.DOMHelper;

import se.fishtank.css.selectors.NodeSelectorException;
import se.fishtank.css.selectors.specifier.PseudoClassSpecifier;
import se.fishtank.css.util.Assert;
/**
 * Simple port of Christer Sandberg's CSS selectors to Dom4j (https://github.com/chrsan/css-selectors)
 */
public class PseudoClassSpecifierChecker implements NodeTraversalChecker {

	private final PseudoClassSpecifier specifier;
    
    /** The set of nodes to check. */
    private Set<Branch> nodes;
    
    /** The result of the checks. */
    private Set<Branch> result;

	public PseudoClassSpecifierChecker(PseudoClassSpecifier specifier) {
		Assert.notNull(specifier, "specifier is null!");
        this.specifier = specifier;
	}

	@Override
	public Set<Branch> check(Set<Branch> nodes) throws NodeSelectorException {
        Assert.notNull(nodes, "nodes is null!");
        this.nodes = nodes;
        result = new LinkedHashSet<Branch>();
        String value = specifier.getValue();
        if ("empty".equals(value)) {
            getEmptyElements();
        } else if ("first-child".equals(value)) {
            getFirstChildElements();
        } else if ("first-of-type".equals(value)) {
            getFirstOfType();
        } else if ("last-child".equals(value)) {
            getLastChildElements();
        } else if ("last-of-type".equals(value)) {
            getLastOfType();
        } else if ("only-child".equals(value)) {
            getOnlyChildElements();
        } else if ("only-of-type".equals(value)) {
            getOnlyOfTypeElements();
        } else {
            throw new NodeSelectorException("Unknown pseudo class: " + value);
        }
        
        return result;
	}
	

    /**
     * Get {@code :empty} elements.
     * 
     * @see <a href="http://www.w3.org/TR/css3-selectors/#empty-pseudo"><code>:empty</code> pseudo-class</a>
     */
    private void getEmptyElements() {
        for (Branch node : nodes) {
        	if (!(node instanceof Branch)) {
        		continue;
        	}
        	Iterator<Node> childrenIter = ((Branch) node).nodeIterator();
            boolean empty = true;
            while (childrenIter.hasNext()) {
                Node n = childrenIter.next();
                if (n.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    empty = false;
                    break;
                } else if (n.getNodeType() == org.w3c.dom.Node.TEXT_NODE) {
                    // TODO: Should we trim the text and see if it's length 0?
                    String value = n.getText();
                    if (value.length() > 0) {
                        empty = false;
                        break;
                    }
                }
            }
            
            if (empty) {
                result.add(node);
            }
        }
    }
    
    /**
     * Get {@code :first-child} elements.
     * 
     * @see <a href="http://www.w3.org/TR/css3-selectors/#first-child-pseudo"><code>:first-child</code> pseudo-class</a>
     */
    private void getFirstChildElements() {
        for (Branch node : nodes) {
            if (DOMHelper.getPreviousSiblingElement(node) == null) {
                result.add(node);
            }
        }
    }
    
    /**
     * Get {@code :first-of-type} elements.
     * 
     * @see <a href="http://www.w3.org/TR/css3-selectors/#first-of-type-pseudo"><code>:first-of-type</code> pseudo-class</a>
     */
    private void getFirstOfType() {
        for (Branch node : nodes) {
        	Branch n = DOMHelper.getPreviousSiblingElement(node);
            while (n != null) {
                if (n.getName().equals(node.getName())) {
                    break;
                }
                
                n = DOMHelper.getPreviousSiblingElement(n);
            }
            
            if (n == null) {
                result.add(node);
            }
        }
    }

    /**
     * Get {@code :last-child} elements.
     * 
     * @see <a href="http://www.w3.org/TR/css3-selectors/#last-child-pseudo"><code>:last-child</code> pseudo-class</a>
     */
    private void getLastChildElements() {
        for (Branch node : nodes) {
            if (DOMHelper.getNextSiblingElement(node) == null) {
                result.add(node);
            }
        }
    }
    
    /**
     * Get {@code :last-of-type} elements.
     * 
     * @see <a href="http://www.w3.org/TR/css3-selectors/#last-of-type-pseudo"><code>:last-of-type</code> pseudo-class</a>
     */
    private void getLastOfType() {
        for (Branch node : nodes) {
        	Branch n = DOMHelper.getNextSiblingElement(node);
            while (n != null) {
                if (n.getName().equals(node.getName())) {
                    break;
                }
                
                n = DOMHelper.getNextSiblingElement(n);
            }
            
            if (n == null) {
                result.add(node);
            }
        }
    }
    
    /**
     * Get {@code :only-child} elements.
     * 
     * @see <a href="http://www.w3.org/TR/css3-selectors/#only-child-pseudo"><code>:only-child</code> pseudo-class</a>
     */
    private void getOnlyChildElements() {
        for (Branch node : nodes) {
            if (DOMHelper.getPreviousSiblingElement(node) == null &&
                    DOMHelper.getNextSiblingElement(node) == null) {
                result.add(node);
            }
        }
    }
    
    /**
     * Get {@code :only-of-type} elements.
     * 
     * @see <a href="http://www.w3.org/TR/css3-selectors/#only-of-type-pseudo"><code>:only-of-type</code> pseudo-class</a>
     */
    private void getOnlyOfTypeElements() {
        for (Branch node : nodes) {
        	Branch n = DOMHelper.getPreviousSiblingElement(node);
            while (n != null) {
                if (n.getName().equals(node.getName())) {
                    break;
                }
                
                n = DOMHelper.getPreviousSiblingElement(n);
            }
            
            if (n == null) {
                n = DOMHelper.getNextSiblingElement(node);
                while (n != null) {
                    if (n.getName().equals(node.getName())) {
                        break;
                    }
                    
                    n = DOMHelper.getNextSiblingElement(n);
                }
                
                if (n == null) {
                    result.add(node);
                }
            }
        }
    }


}
