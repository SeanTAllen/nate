package org.nate.internal.dom4j.cssselectors.internal;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Branch;

import se.fishtank.css.selectors.NodeSelectorException;
import se.fishtank.css.selectors.specifier.AttributeSpecifier;
import se.fishtank.css.util.Assert;
/**
 * Simple port of Christer Sandberg's CSS selectors to Dom4j (https://github.com/chrsan/css-selectors)
 */
public class AttributeSpecifierChecker implements NodeTraversalChecker {

	private final AttributeSpecifier specifier;

	public AttributeSpecifierChecker(AttributeSpecifier specifier) {
		Assert.notNull(specifier, "specifier is null!");
		this.specifier = specifier;
	}

	@Override
	public Set<Branch> check(Set<Branch> nodes) throws NodeSelectorException {
        Assert.notNull(nodes, "nodes is null!");
        Set<Branch> result = new LinkedHashSet<Branch>();
        for (Branch node : nodes) {
        	if (!(node instanceof Element)) {
        		continue;
        	}
            List<Attribute> attributes = ((Element) node).attributes();
            
            Attribute attr = findAttribute(attributes, specifier.getName());
            if (attr == null) {
                continue;
            }
            
            // It just have to be present.
            if (specifier.getValue() == null) {
                result.add(node);
                continue;
            }
            
            String value = attr.getValue().trim();
            if (value.length() != 0) {
                String val = specifier.getValue();
                switch (specifier.getMatch()) {
                case EXACT:
                    if (value.equals(val)) {
                        result.add(node);
                    }
                    
                    break;
                case HYPHEN:
                    if (value.equals(val) || value.startsWith(val + '-')) {
                        result.add(node);
                    }
                    
                    break;
                case PREFIX:
                    if (value.startsWith(val)) {
                        result.add(node);
                    }
                    
                    break;
                case SUFFIX:
                    if (value.endsWith(val)) {
                        result.add(node);
                    }
                    
                    break;
                case CONTAINS:
                    if (value.contains(val)) {
                        result.add(node);
                    }
                    
                    break;
                case LIST:
                    for (String v : value.split("\\s+")) {
                        if (v.equals(val)) {
                            result.add(node);
                        }
                    }
                    
                    break;
                }
            }
        }
        
        return result;
	}

	private Attribute findAttribute(List<Attribute> attributes, String name) {
		for (Attribute attribute : attributes) {
			if (attribute.getName().equals(name)) {
				return attribute;
			}
		}
		return null;
	}

}
