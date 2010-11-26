package org.nate;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.nate.encoder.HtmlEncoder;
import org.nate.encoder.HtmlFragmentEncoder;
import org.nate.exception.IONateException;
import org.nate.exception.UnsupportedEncodingNateException;
import org.nate.internal.NateDocumentBackedEngine;
import org.nate.internal.NullEngine;

public final class Nate {

	private Nate() {
	}
	
	private static Encoders encoders = new Encoders();
	static {
		encoders.register(new HtmlEncoder());
		encoders.register(new HtmlFragmentEncoder());
	}

	public static Engine newWith(InputStream source, Encoder encoder) {
		if (encoder == null) {
			return new NullEngine(source);
		}
		return new NateDocumentBackedEngine(source, encoder);
	}

	public static Engine newWith(String source, Encoder encoder) {
		try {
			// We are using an xml parser, and so we really need to use UTF-8.
			// TODO: Test this assumption!!!
			return newWith(new ByteArrayInputStream(source.getBytes("UTF-8")), encoder);
		} catch (UnsupportedEncodingException e) {
			throw new UnsupportedEncodingNateException(e);
		}
	}

	public static Engine newWith(String source) {
		return newWith(source, encoders.encoderFor("HTMLF"));
	}

	public static NateDocumentBackedEngine newWith(File file) {
		InputStream inputStream = null;
		try {
			inputStream = new BufferedInputStream(new FileInputStream(file));
			Encoder encoder = encoders.encoderFor(file);
			return new NateDocumentBackedEngine(inputStream, encoder);
		} catch (FileNotFoundException e) {
			throw new IONateException(e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					throw new IONateException(e);
				}
			}
		}
	}

	public static Encoders encoders() {
		return encoders;
	}

}
