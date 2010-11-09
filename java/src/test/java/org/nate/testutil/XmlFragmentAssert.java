package org.nate.testutil;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

public class XmlFragmentAssert {
	
	private XmlFragmentAssert() {
	}
	
	public static void assertXmlFragmentsEqual(String expected, String actual) throws Exception {
		// Wrap in fake roots in case the xml has multiple roots, otherwise you get a parser exception
		assertXMLEqual("Unexpected xml: " + actual, wrapInFakeRoot(expected), wrapInFakeRoot(actual));
	}
	
	private static String wrapInFakeRoot(String fragment) {
		return "<fragment>" + fragment.trim() + "</fragment>";
	}

}
