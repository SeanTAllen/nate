package org.nate;

import java.io.InputStream;

import org.nate.encoder.NateDocument;

public interface Encoder {
	boolean isNullEncoder();
	String type();
	NateDocument encode(InputStream source);
}