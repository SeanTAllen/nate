package org.nate.encoder;

import org.nate.Encoder;
import org.nate.TransformResult;

public class HtmlEncoder implements Encoder {

	private static final String TYPE = "HTML";

	public boolean isNullEncoder() {
		return false;
	}

	public String type() {
		return TYPE;
	}

	public Object encode(String source) {
		return "todo";
	}

	public TransformResult transformWith(Object template, Object data) {
		return null;
	}
}