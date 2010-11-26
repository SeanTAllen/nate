package org.nate.internal.transformer;

import org.nate.Engine;
import org.nate.encoder.NateNode;

public class EngineInjector implements NateTransformer {

	private final Engine engine;

	public EngineInjector(Engine engine) {
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
