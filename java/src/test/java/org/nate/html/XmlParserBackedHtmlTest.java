package org.nate.html;

import static java.util.Arrays.asList;
import static org.nate.testutil.XmlFragmentAssert.assertXmlFragmentsEqual;

import java.util.ArrayList;

import org.junit.Test;


public class XmlParserBackedHtmlTest {

	@Test
	public void shouldRemoveOnlyChildWhenReplacingItWithEmptyList() throws Exception {
		XmlParserBackedHtml div = XmlParserBackedHtml.fromFragment("<div><span/></div>");
		Html span = div.selectNodes("span").get(0);
		span.replaceWith(new ArrayList<Html>());
		assertXmlFragmentsEqual("<div/>", div.toHtml());
	}
	
	@Test
	public void shouldRemoveChildOfManyWhenReplacingItWithEmptyList() throws Exception {
		XmlParserBackedHtml div = XmlParserBackedHtml.fromFragment("<div><p/><span/><section/></div>");
		Html span = div.selectNodes("span").get(0);
		span.replaceWith(new ArrayList<Html>());
		assertXmlFragmentsEqual("<div><p/><section/></div>", div.toHtml());
	}
	
	@Test
	public void shouldReplaceNodeWithNewNodes() throws Exception {
		XmlParserBackedHtml div = XmlParserBackedHtml.fromFragment("<div><p/><span/><section/></div>");
		Html span = div.selectNodes("span").get(0);
		Html newSpan1 = span.cloneFragment();
		Html newSpan2 = span.cloneFragment();
		newSpan1.setTextContent("Apple");
		newSpan2.setTextContent("Banana");
		span.replaceWith(asList(newSpan1, newSpan2));
		assertXmlFragmentsEqual("<div><p/><span>Apple</span><span>Banana</span><section/></div>", div.toHtml());
		
	}
}
