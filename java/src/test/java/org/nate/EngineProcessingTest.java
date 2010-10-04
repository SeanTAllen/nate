package org.nate;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EngineProcessingTest {

	@Mock private Template template;
	@Mock private Object data;
	@Mock private Match match;

	private Map<String, Object> model;
	private Engine engine;
	private String selector;

	@Before public void setup() {
		initMocks(this);
		selector = "selector";
		model = modelWith(selector, data);
		engine = engineUnderTest();
	}

	@Test public void shouldInjectDataIntoTemplateForMatchingSelectors() {
		when(template.match(selector)).thenReturn(match);
		engine.injectInto(model, template);
		verify(template).match(selector);
		verify(match).inject(data);
	}

	private Map<String, Object> modelWith(String selector, Object data) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(selector, data);
		return map;
	}

	protected Engine engineUnderTest() {
		return new DefaultEngine();
	}
}