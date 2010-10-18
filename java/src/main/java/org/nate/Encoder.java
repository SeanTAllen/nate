package org.nate;

public interface Encoder {
	boolean isNullEncoder();
	String type();
	Object encode(String source);
	TransformResult transformWith(Object template, Object data);
}