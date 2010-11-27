package org.nate.internal.selector;

import static org.nate.internal.Assertions.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nate.Engine;

public class NateSelectors {
	
	private static final Pattern ATTRIBUTE_SELECTOR_PATTERN = Pattern.compile("^(.*)@@(.+)$"); 

	public static NateSelector from(Object selectorObject) {
		assertType("selector", selectorObject, String.class);
		String selectorString = ((String) selectorObject).trim();
		Matcher attributeSelectorMatcher = ATTRIBUTE_SELECTOR_PATTERN.matcher(selectorString);
		if (attributeSelectorMatcher.matches()) {
			String elementSelectorString = attributeSelectorMatcher.group(1).trim();
			String attributeSelectorString = attributeSelectorMatcher.group(2).trim();
			return AttributeSelectors.from(elementSelectorString, attributeSelectorString);
		}
		if (Engine.CONTENT_ATTRIBUTE.equals(selectorString)) {
			return new SelfSelector();
		}
		return new ElementSelector(selectorString);
	}

}
