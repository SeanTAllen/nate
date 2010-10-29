package org.nate;

import org.nate.html.Html;

public interface Encoder {
	boolean isNullEncoder();
	String type();
	Html encode(String source);
}