package org.nate.internal.transformer;

import org.nate.encoder.NateNode;
import org.nate.internal.NateDocumentBackedEngine;

public class EngineInjector implements NateTransformer {

	private final NateDocumentBackedEngine engine;

	public EngineInjector(NateDocumentBackedEngine engine) {
		this.engine = engine;
	}

	@Override
	public void transform(NateNode node) {
		node.replaceChildren(engine.getDocument());
	}

	@Override
	public void setAttribute(String attributeName, NateNode node) {
		throw new IllegalArgumentException("Illegal attempt to inject an engine into attribute " + attributeName);
	}

}
