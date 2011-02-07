package org.nate.internal.transformer;

import org.nate.encoder.NateNode;

public class TextInjector implements NateTransformer {

	private final String text;

	public TextInjector(String text) {
		this.text = text;
	}

	@Override
	public void transform(NateNode node) {
		node.setTextContent(text);
	}

	@Override
	public void setAttribute(String attributeName, NateNode node) {
		node.setAttribute(attributeName, text);
	}

}
