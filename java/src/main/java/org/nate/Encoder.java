package org.nate;

import java.io.InputStream;

import org.nate.encoder.NateDocument;

public interface Encoder {
	String type();
	NateDocument encode(InputStream source);
}