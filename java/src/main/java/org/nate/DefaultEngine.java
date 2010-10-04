package org.nate;

import static java.util.Map.Entry;
import java.util.Map;

public class DefaultEngine implements Engine {

	@Override
	public void injectInto(Map<String, Object> model, Template template) {
		for (Entry<String, Object> modelEntry : model.entrySet())
			injectInto(modelEntry, template);
	}

	private void injectInto(Entry<String, Object> modelEntry, Template template) {
		Match match = matchIn(selector(modelEntry), template);
		Object data = data(modelEntry);
		injectWith(match, data);
	}

	private Match matchIn(String selector, Template template) {
		return template.match(selector);
	}

	private void injectWith(Match match, Object data) {
		match.inject(data);
	}

	private String selector(Entry<String, Object> modelEntry) {
		return modelEntry.getKey();
	}

	private Object data(Entry<String, Object> modelEntry) {
		return modelEntry.getValue();
	}
}