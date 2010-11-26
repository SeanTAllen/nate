package org.nate.internal.transformer;

import java.util.Map;
import java.util.Set;

import org.nate.encoder.NateNode;
import org.nate.internal.selector.NateSelector;
import org.nate.internal.selector.NateSelectors;

@SuppressWarnings("unchecked")
public class TransformationMap implements NateTransformer {

	private final Map map;

	public TransformationMap(Map map) {
		this.map = map;
	}

	@Override
	public void transform(NateNode node) {
		Set<Map.Entry> entrySet = map.entrySet();
		for (Map.Entry entry : entrySet) {
			Object key = entry.getKey();
			Object value = entry.getValue();
			NateSelector selector = NateSelectors.from(key);
			NateTransformer transformer = NateTransformers.from(value);
			selector.transformSelectedNodes(transformer, node);
		}

	}

	@Override
	public void setAttribute(String attributeName, NateNode node) {
		throw new IllegalArgumentException("Illegal attempt to inject into attribute " + attributeName
				+ " the map: " + map);
	}

}
