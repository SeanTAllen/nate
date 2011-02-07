package org.nate.internal.dom4j;

import static org.junit.Assert.assertThat;
import static org.nate.testutil.WhiteSpaceIgnoringXmlMatcher.matchesXmlIgnoringWhiteSpace;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Test;


public class Dom4jBackedNateDocumentFactoryTest {

	@Test
	public void shouldCreateNateDomDocumentFromXml() throws Exception {
		String source = "<html><head></head><body><div>hello</div></body></html>";
		InputStream input = inputStreamFor(source);
		Dom4jBackedNateDocument doc = new Dom4jBackedNateDocumentFactory().createFromXmlDocument(input);
		assertThat(doc.render(), matchesXmlIgnoringWhiteSpace(source));
	}

	@Test
	public void shouldCreateNateDomDocumentFragmentFromXmlFragment() throws Exception {
		String source = "<div>hello</div>";
		InputStream input = inputStreamFor(source);
		Dom4jBackedDocumentFragment doc = new Dom4jBackedNateDocumentFactory().createFromXmlDocumentFragment(input);
		assertThat(doc.render(), matchesXmlIgnoringWhiteSpace(source));
	}

	private InputStream inputStreamFor(String string) {
		return new ByteArrayInputStream(string.getBytes());
	}

}
