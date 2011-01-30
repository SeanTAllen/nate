package org.nate.internal.dom4j;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.nate.testutil.WhiteSpaceIgnoringXmlMatcher.matchesXmlIgnoringWhiteSpace;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.junit.Test;
import org.nate.encoder.NateDocument;
import org.nate.encoder.NateNode;

public class Dom4jBackedDocumentFragmentTest {

	@Test
	public void shouldFindDesiredElements() throws Exception {
		NateNode document = createDocumentFragment("apple<div><p>apple</p> hello <p>banana</p></div> ");
		List<NateNode> elements = document.find("p");
		assertThat("Unexpected size for: " + elements, elements.size(), is(2));
		assertThat(elements.get(0).render(), matchesXmlIgnoringWhiteSpace("<p>apple</p>"));
		assertThat(elements.get(1).render(), matchesXmlIgnoringWhiteSpace("<p>banana</p>"));
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
		assertThat(copy.render(), matchesXmlIgnoringWhiteSpace("<div>a<div>b</div></div><div>b</div><div>c</div>"));
		assertThat(document.render(), matchesXmlIgnoringWhiteSpace(original));
	}

	private NateDocument createDocumentFragment(String input) {
		return new Dom4jBackedNateDocumentFactory()
				.createFromXmlDocumentFragment(new ByteArrayInputStream(input.getBytes()));
	}

}
