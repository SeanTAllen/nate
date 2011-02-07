package org.nate.internal.transformer;

import java.util.ArrayList;
import java.util.List;

import org.nate.encoder.NateNode;

public class TransformationSequence implements NateTransformer {

	private final Iterable<NateTransformer> sequence;

	@SuppressWarnings("unchecked")
	public static TransformationSequence fromObjectSequence(Iterable data) {
		List<NateTransformer> sequence = new ArrayList<NateTransformer>();
		for (Object value : data) {
			NateTransformer transformer = NateTransformers.from(value);
			sequence.add(transformer);
		}
		return new TransformationSequence(sequence);
	}

	public TransformationSequence(Iterable<NateTransformer> sequence) {
		this.sequence = sequence;
	}

	@Override
	public void transform(NateNode node) {
		List<NateNode> newNodes = new ArrayList<NateNode>();
		for (NateTransformer transformer : sequence) {
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
