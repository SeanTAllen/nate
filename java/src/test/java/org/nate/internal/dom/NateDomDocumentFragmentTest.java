package org.nate.internal.dom;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.nate.testutil.XmlFragmentAssert.assertXmlFragmentsEqual;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.junit.Test;
import org.nate.encoder.NateDocument;
import org.nate.encoder.NateNode;
import org.nate.internal.dom.XmlBasedNateDomDocumentFactory;

public class NateDomDocumentFragmentTest {

	@Test
	public void shouldFindDesiredElements() throws Exception {
		NateNode document = createDocumentFragment("apple<div><p>apple</p> hello <p>banana</p></div> ");
		List<NateNode> elements = document.find("p");
		assertThat("Unexpected size for: " + elements, elements.size(), is(2));
		assertXmlFragmentsEqual("<p>apple</p>", elements.get(0).render());
		assertXmlFragmentsEqual("<p>banana</p>", elements.get(1).render());
	}

	@Test
	public void shouldFindAnEmptyListWhenNothingMatches() throws Exception {
		NateNode document = createDocumentFragment("apple<div></div>");
		List<NateNode> elements = document.find("p");
		assertThat("Unexpected size for: " + elements, elements.size(), is(0));
	}

	@Test
	public void shouldCopyDesiredElements() throws Exception {
		String original = "apple<body><div>a<div>b</div></div>x<div>c</div></body> ";
		NateDocument document = createDocumentFragment(original);
		NateNode copy = document.copy("div");
		assertXmlFragmentsEqual("<div>a<div>b</div></div><div>b</div><div>c</div>", copy.render());
		assertXmlFragmentsEqual(original, document.render());
	}

	private NateDocument createDocumentFragment(String input) {
		return new XmlBasedNateDomDocumentFactory()
				.createFromXmlDocumentFragment(new ByteArrayInputStream(input.getBytes()));
	}

}
