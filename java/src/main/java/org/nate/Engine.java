package org.nate;

import java.util.Map;

public interface Engine {
	void injectInto(Map<String, Object> model, Template html);
}