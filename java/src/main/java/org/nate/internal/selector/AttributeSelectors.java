package org.nate.internal.selector;

import org.nate.encoder.NateNode;
import org.nate.internal.transformer.NateTransformer;
import org.nate.internal.transformer.TransformationMap;

public final class AttributeSelectors  {

	private AttributeSelectors() {
	}

	public static NateSelector from(String elementSelectionString, String attributeName) {
		if (elementSelectionString.isEmpty()) {
			return new SimpleAttributeSelector(attributeName);
		}
		return new ElementAttributeSelector(elementSelectionString, attributeName);
	}

	private static final class SimpleAttributeSelector implements NateSelector {

		private final String attributeName;

		public SimpleAttributeSelector(String attributeName) {
			this.attributeName = attributeName;
		}

		@Override
		public void transformSelectedNodes(NateTransformer transformer, NateNode node) {
			transformer.setAttribute(attributeName, node);
		}
	}
	
	private static final class ElementAttributeSelector implements NateSelector {

		private final ElementSelector elementSelector;
		private final String attributeName;

		public ElementAttributeSelector(String elementSelectionString, String attributeName) {
			this.attributeName = attributeName;
			this.elementSelector = new ElementSelector(elementSelectionString);
		}

		@Override
		public void transformSelectedNodes(final NateTransformer transformer, NateNode node) {
			NateTransformer attributeTransformer =
				new TransformationMap(new SimpleAttributeSelector(attributeName), transformer);
			elementSelector.transformSelectedNodes(attributeTransformer, node);
		}
	}
}
