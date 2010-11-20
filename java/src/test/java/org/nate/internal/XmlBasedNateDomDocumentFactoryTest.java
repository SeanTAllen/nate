package org.nate.internal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.nate.testutil.XmlFragmentAssert.assertXmlFragmentsEqual;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.w3c.dom.Node;


public class XmlBasedNateDomDocumentFactoryTest {

	@Test
	public void shouldCreateNateDomDocumentFromXml() throws Exception {
		InputStream input = inputStreamFor(
				"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" +
				"<html><body><div/></body></html>");
		NateDomDocument doc = new XmlBasedNateDomDocumentFactory().createFromXmlDocument(input);
		assertXmlFragmentsEqual("<html><body><div/></body></html>", doc.render());
		List<? extends Node> rootNodes = doc.getRootNodes();
		assertThat(rootNodes.size(), is(1));
		assertThat(rootNodes.get(0).getNodeName(), is("html"));
	}

	@Test
	public void shouldCreateNateDomDocumentFromXmlFragment() throws Exception {
		InputStream input = inputStreamFor("<a>hello</a><b>foo</b>");
		NateDomDocument doc = new XmlBasedNateDomDocumentFactory().createFromXmlDocumentFragment(input);
		assertXmlFragmentsEqual("<a>hello</a><b>foo</b>", doc.render());
		List<? extends Node> rootNodes = doc.getRootNodes();
		assertThat(rootNodes.size(), is(2));
		assertThat(rootNodes.get(0).getNodeName(), is("a"));
		assertThat(rootNodes.get(1).getNodeName(), is("b"));
	}

	@Test
	public void shouldCreateNateDomDocumentFromNateDomNodes() throws Exception {
		NateDomDocument document1 = createDocument("<a>apple</a>");
		NateDomDocument document2 = createDocument("<a>banana</a>");
		NateDomDocument result = new XmlBasedNateDomDocumentFactory().createFromNateDomNodes(Arrays.asList(document1, document2));
		assertXmlFragmentsEqual("<a>apple</a><a>banana</a>", result.render());
		assertXmlFragmentsEqual("<a>apple</a><a>banana</a>", result.render());
		assertXmlFragmentsEqual("<a>apple</a>", document1.render());
		assertXmlFragmentsEqual("<a>banana</a>", document2.render());
	}
	
	private InputStream inputStreamFor(String string) {
		return new ByteArrayInputStream(string.getBytes());
	}
	

	private NateDomDocument createDocument(String input) {
		return new XmlBasedNateDomDocumentFactory().createFromXmlDocument(inputStreamFor(input));
	}

}
