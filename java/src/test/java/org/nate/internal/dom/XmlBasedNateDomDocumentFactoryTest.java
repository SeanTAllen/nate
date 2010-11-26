package org.nate.internal.dom;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.nate.testutil.XmlFragmentAssert.assertXmlFragmentsEqual;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.nate.encoder.NateNode;
import org.nate.internal.dom.XmlBasedNateDomDocumentFactory;
import org.w3c.dom.Node;


public class XmlBasedNateDomDocumentFactoryTest {

	@Test
	public void shouldCreateNateDomDocumentFromXml() throws Exception {
		InputStream input = inputStreamFor(
				"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" +
				"<html><body><div/></body></html>");
		NateNode doc = new XmlBasedNateDomDocumentFactory().createFromXmlDocument(input);
		assertXmlFragmentsEqual("<html><body><div/></body></html>", doc.render());
		List<Node> rootElements = doc.getRootNodes();
		assertThat(rootElements.size(), is(1));
		assertThat(rootElements.get(0).getNodeName(), is("html"));
	}

	@Test
	public void shouldCreateNateDomDocumentFromXmlFragment() throws Exception {
		InputStream input = inputStreamFor("apple<a>hello</a><b>foo</b>banana");
		NateNode doc = new XmlBasedNateDomDocumentFactory().createFromXmlDocumentFragment(input);
		assertXmlFragmentsEqual("apple<a>hello</a><b>foo</b>banana", doc.render());
		List<Node> rootNodes = doc.getRootNodes();
		assertThat(rootNodes.size(), is(4));
		assertThat(rootNodes.get(0).getNodeValue(), is("apple"));
		assertThat(rootNodes.get(1).getNodeName(), is("a"));
		assertThat(rootNodes.get(2).getNodeName(), is("b"));
		assertThat(rootNodes.get(3).getNodeValue(), is("banana"));
	}

	@Test
	public void shouldCreateNateDomDocumentFromW3cElements() throws Exception {
		NateNode document1 = createDocument("<a>apple</a>");
		NateNode document2 = createDocument("<a>banana</a>");
		List<Node> nodes = new ArrayList<Node>();
		nodes.addAll(document1.getRootNodes());
		nodes.addAll(document2.getRootNodes());
		NateNode result = new XmlBasedNateDomDocumentFactory().createFromW3cNodes(nodes);
		assertXmlFragmentsEqual("<a>apple</a><a>banana</a>", result.render());
		assertXmlFragmentsEqual("<a>apple</a><a>banana</a>", result.render());
		assertXmlFragmentsEqual("<a>apple</a>", document1.render());
		assertXmlFragmentsEqual("<a>banana</a>", document2.render());
	}
	
	private InputStream inputStreamFor(String string) {
		return new ByteArrayInputStream(string.getBytes());
	}

	private NateNode createDocument(String input) {
		return new XmlBasedNateDomDocumentFactory().createFromXmlDocument(inputStreamFor(input));
	}

}
