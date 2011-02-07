package org.nate.internal.dom4j.cssselectors.internal;

import java.util.Set;

import org.dom4j.Branch;

import se.fishtank.css.selectors.NodeSelectorException;
/**
 * Simple port of Christer Sandberg's CSS selectors to Dom4j (https://github.com/chrsan/css-selectors)
 */
public interface NodeTraversalChecker {
	Set<Branch> check(Set<Branch> nodes) throws NodeSelectorException;
}
