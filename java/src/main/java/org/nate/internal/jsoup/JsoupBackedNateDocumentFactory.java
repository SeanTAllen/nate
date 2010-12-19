package org.nate.internal.jsoup;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.nate.encoder.NateDocument;
import org.nate.exception.IONateException;
import org.nate.internal.util.StreamUtils;

public class JsoupBackedNateDocumentFactory {

	private static final String NATE_FRAGMENT_WRAPPER = "natewrapper";
	private static final String BEGIN_NATE_FRAGMENT_WRAPPER = "<" + NATE_FRAGMENT_WRAPPER + ">";
	private static final String END_NATE_FRAGMENT_WRAPPER = "</" + NATE_FRAGMENT_WRAPPER + ">";
	
	/**
	 *  TODO: What should we do with this???
	 */
	public static final String BASE_URI = "";

	public JsoupBackedNateDocument createFromHtmlDocument(InputStream source) {
		return new JsoupBackedNateDocument(parse(source));
	}

	public JsoupBackedNateDocument createFromHtmlDocument(String source) {
		return new JsoupBackedNateDocument(parse(source));
	}

	public JsoupBackedNateDocumentFragment createFromHtmlFragment(InputStream source) {
		return new JsoupBackedNateDocumentFragment(parseFragment(source));
	}

	public NateDocument createFromHtmlFragment(String source) {
		return new JsoupBackedNateDocumentFragment(parseFragment(source));
	}

	private Document parse(String source) {
		return Jsoup.parse(source);
	}

	private Document parse(InputStream source) {
		try {
			// TODO: Really need to sort out charsets and base uri...
			return Jsoup.parse(source, null, BASE_URI);
		} catch (IOException e) {
			throw new IONateException("Problems parsing HTML.", e);
		}
	}

	private List<Node> parseFragment(InputStream source) {
		Element pseudoRootNode =
			parse(StreamUtils.wrapInPseudoRootElement(source, NATE_FRAGMENT_WRAPPER))
				.select(NATE_FRAGMENT_WRAPPER).get(0);
		return pseudoRootNode.childNodes();
	}

	private List<Node> parseFragment(String source) {
		Element pseudoRootNode =
			parse(wrapInPseudoRootElement(source)).select(NATE_FRAGMENT_WRAPPER).get(0);
		return pseudoRootNode.childNodes();
	}

	private String wrapInPseudoRootElement(String source) {
		return BEGIN_NATE_FRAGMENT_WRAPPER + source + END_NATE_FRAGMENT_WRAPPER;
	}

}
