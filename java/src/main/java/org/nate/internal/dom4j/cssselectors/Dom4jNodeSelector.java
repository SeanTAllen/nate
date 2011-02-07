package org.nate.internal.dom4j.cssselectors;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.Branch;
import org.nate.internal.dom4j.cssselectors.internal.AttributeSpecifierChecker;
import org.nate.internal.dom4j.cssselectors.internal.NodeTraversalChecker;
import org.nate.internal.dom4j.cssselectors.internal.PseudoClassSpecifierChecker;
import org.nate.internal.dom4j.cssselectors.internal.PseudoNthSpecifierChecker;
import org.nate.internal.dom4j.cssselectors.internal.TagChecker;

import se.fishtank.css.selectors.NodeSelector;
import se.fishtank.css.selectors.NodeSelectorException;
import se.fishtank.css.selectors.Selector;
import se.fishtank.css.selectors.Specifier;
import se.fishtank.css.selectors.scanner.Scanner;
import se.fishtank.css.selectors.scanner.ScannerException;
import se.fishtank.css.selectors.specifier.AttributeSpecifier;
import se.fishtank.css.selectors.specifier.NegationSpecifier;
import se.fishtank.css.selectors.specifier.PseudoClassSpecifier;
import se.fishtank.css.selectors.specifier.PseudoNthSpecifier;
import se.fishtank.css.util.Assert;
/**
 * Simple port of Christer Sandberg's CSS selectors to Dom4j (https://github.com/chrsan/css-selectors)
 */
public class Dom4jNodeSelector implements NodeSelector<Branch> {
	
    private Branch root;

	public Dom4jNodeSelector(Branch root) {
        Assert.notNull(root, "root is null!");
        this.root = root;
    }

	@Override
	public Branch querySelector(String selectors) throws NodeSelectorException {
        Set<Branch> result = querySelectorAll(selectors);
        if (result.isEmpty()) {
            return null;
        }
        
        return result.iterator().next();
	}

	@Override
	public Set<Branch> querySelectorAll(String selectors) throws NodeSelectorException {
        Assert.notNull(selectors, "selectors is null!");
        List<List<Selector>> groups;
        try {
            Scanner scanner = new Scanner(selectors);
            groups = scanner.scan();
        } catch (ScannerException e) {
            throw new NodeSelectorException(e);
        }

        Set<Branch> results = new LinkedHashSet<Branch>();
        for (List<Selector> parts : groups) {
            Set<Branch> result = check(parts);
            if (!result.isEmpty()) {
                results.addAll(result);
            }
        }

        return results;
	}
    private Set<Branch> check(List<Selector> parts) throws NodeSelectorException {
        Set<Branch> result = new LinkedHashSet<Branch>();
        result.add(root);
        for (Selector selector : parts) {
            NodeTraversalChecker checker = new TagChecker(selector);
            result = checker.check(result);
            if (result.isEmpty()) {
                // Bail out early.
                return result;
            }
            
            if (selector.hasSpecifiers()) {
                for (Specifier specifier : selector.getSpecifiers()) {
                    switch (specifier.getType()) {
                    case ATTRIBUTE:
                        checker = new AttributeSpecifierChecker((AttributeSpecifier) specifier);
                        break;
                    case PSEUDO:
                        if (specifier instanceof PseudoClassSpecifier) {
                            checker = new PseudoClassSpecifierChecker((PseudoClassSpecifier) specifier);
                        } else if (specifier instanceof PseudoNthSpecifier) {
                            checker = new PseudoNthSpecifierChecker((PseudoNthSpecifier) specifier);
                        }
                        
                        break;
                    case NEGATION:
                        final Set<Branch> negationNodes = checkNegationSpecifier((NegationSpecifier) specifier);
                        checker = new NodeTraversalChecker() {
                            @Override
                            public Set<Branch> check(Set<Branch> nodes) throws NodeSelectorException {
                                Set<Branch> set = new LinkedHashSet<Branch>(nodes);
                                set.removeAll(negationNodes);
                                return set;
                            }
                        };
                        
                        break;
                    }
                    
                    result = checker.check(result);
                    if (result.isEmpty()) {
                        // Bail out early.
                        return result;
                    }
                }
            }
        }
        
        return result;
    }
    private Set<Branch> checkNegationSpecifier(NegationSpecifier specifier) throws NodeSelectorException {
        List<Selector> parts = new ArrayList<Selector>(1);
        parts.add(specifier.getSelector());
        return check(parts);
    }
   
}
