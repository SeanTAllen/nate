package org.nate.internal.transformer;

import java.util.Map;

import org.nate.Engine;

public class NateTransformers {

	@SuppressWarnings("unchecked")
	public static NateTransformer from(Object data) {
		if (data instanceof Iterable) {
			return new TransformationSequence((Iterable) data);
		}
		if (data instanceof Map) {
			return new TransformationMap((Map) data);
		}
		if (data instanceof Engine) {
			return new EngineInjector((Engine) data);
		}
		if (data == null) {
			return new NullDataInjector();
		}
		return new TextInjector(data.toString());
	}

}
