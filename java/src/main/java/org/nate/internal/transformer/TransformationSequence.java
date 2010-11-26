package org.nate.internal.transformer;

import java.util.ArrayList;
import java.util.List;

import org.nate.encoder.NateNode;

@SuppressWarnings("unchecked")
public class TransformationSequence implements NateTransformer {

	private final Iterable sequence;

	public TransformationSequence(Iterable sequence) {
		this.sequence = sequence;
	}

	@Override
	public void transform(NateNode node) {
		List<NateNode> newNodes = new ArrayList<NateNode>();
		for (Object value : sequence) {
			NateTransformer transformer = NateTransformers.from(value);
			NateNode newNode = node.copy();
			transformer.transform(newNode);
			newNodes.add(newNode);
		}
		node.replaceWith(newNodes);
	}

	@Override
	public void setAttribute(String attributeName, NateNode node) {
		throw new IllegalArgumentException("Illegal attempt (not yet implemented) to inject list into attribute " + attributeName);
	}

}
