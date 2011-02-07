package org.nate.internal.transformer;

import static java.util.Collections.singletonMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.nate.encoder.NateNode;
import org.nate.internal.selector.NateSelector;
import org.nate.internal.selector.NateSelectors;

public class TransformationMap implements NateTransformer {

	private final Map<NateSelector, NateTransformer> map;

	@SuppressWarnings("unchecked")
	public static TransformationMap fromObjectMap(Map data) { 
		Map<NateSelector, NateTransformer> map = new HashMap<NateSelector, NateTransformer>(data.size());
		Set<Map.Entry> entrySet = data.entrySet();
		for (Map.Entry entry : entrySet) {
			Object key = entry.getKey();
			Object value = entry.getValue();
			NateSelector selector = NateSelectors.from(key);
			NateTransformer transformer = NateTransformers.from(value);
			map.put(selector, transformer);
		}
		return new TransformationMap(map);
	}
	
	public TransformationMap(Map<NateSelector, NateTransformer> map) {
		this.map = map;
	}
	
	public TransformationMap(NateSelector selector, NateTransformer transformer) {
		this.map = singletonMap(selector, transformer);
	}

	@Override
	public void transform(NateNode node) {
		for (Map.Entry<NateSelector, NateTransformer> entry : map.entrySet()) {
			NateSelector selector = entry.getKey();
			NateTransformer transformer = entry.getValue();
			selector.transformSelectedNodes(transformer, node);
		}
	}

	@Override
	public void setAttribute(String attributeName, NateNode node) {
		throw new IllegalArgumentException("Illegal attempt to inject into attribute " + attributeName
				+ " the map: " + map);
	}


}
