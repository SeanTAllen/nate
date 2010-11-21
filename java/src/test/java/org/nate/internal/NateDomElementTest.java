package org.nate.internal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.nate.testutil.XmlFragmentAssert.assertXmlFragmentsEqual;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.junit.Test;

public class NateDomElementTest {

	@Test
	public void shouldFindDesiredElements() throws Exception {
		NateDomElement element = elementFor("<div><p>apple</p> hello <p>banana</p></div>");
		List<NateDomElement> elements = element.find("p");
		assertThat("Unexpected size for: " + elements, elements.size(), is(2));
		assertXmlFragmentsEqual("<p>apple</p>", elements.get(0).render());
		assertXmlFragmentsEqual("<p>banana</p>", elements.get(1).render());
	}

	@Test
	public void shouldFindNoElementsWhenNoneMatch() throws Exception {
		NateDomElement element = elementFor("<div><p>apple</p> hello <p>banana</p></div>");
		List<NateDomElement> elements = element.find("p.foo");
		assertThat("Unexpected size for: " + elements, elements.size(), is(0));
	}

	private NateDomElement elementFor(String string) {
		NateDomDocument document = createDocument("<wrapper>" + string + "</wrapper>");
		return document.find("wrapper").get(0);
	}

	private NateDomDocument createDocument(String input) {
		return new XmlBasedNateDomDocumentFactory().createFromXmlDocument(new ByteArrayInputStream(input.getBytes()));
	}

}
