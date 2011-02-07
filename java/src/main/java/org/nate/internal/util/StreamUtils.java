package org.nate.internal.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;

public final class StreamUtils {
	// Element wrapped around html fragments to make fragment manipulation easier.
	private static final String PSEUDO_ROOT = "pseudoroot";

	private StreamUtils() {
	}

	public static InputStream wrapInPseudoRootElement(InputStream source) {
		return wrapInPseudoRootElement(source, PSEUDO_ROOT);
	}

	public static InputStream wrapInPseudoRootElement(InputStream source, String pseudoRootTagName) {
		InputStream startTag = new ByteArrayInputStream(("<" + pseudoRootTagName + ">").getBytes());
		InputStream endTag = new ByteArrayInputStream(("</" + pseudoRootTagName + ">").getBytes());
		return new SequenceInputStream(startTag, new SequenceInputStream(source, endTag));
	}
	
	
}
