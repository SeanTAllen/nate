package org.nate;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.nate.encoder.Dom4jEncoder;
import org.nate.encoder.Dom4jFragmentEncoder;
import org.nate.encoder.HtmlEncoder;
import org.nate.encoder.HtmlFragmentEncoder;
import org.nate.exception.IONateException;
import org.nate.internal.NateDocumentBackedEngine;

public final class Nate {

	private Nate() {
	}
	
	private static Encoders encoders = new Encoders();
	static {
		encoders.register(new HtmlEncoder());
		encoders.register(new HtmlFragmentEncoder());
		encoders.register(new Dom4jEncoder());
		encoders.register(new Dom4jFragmentEncoder());
	}

	public static Engine newWith(InputStream source, Encoder encoder) {
		return new NateDocumentBackedEngine(source, encoder);
	}

	public static Engine newWith(String source, Encoder encoder) {
		return new NateDocumentBackedEngine(source, encoder);
	}

	public static Engine newWith(String source) {
		return newWith(source, encoders.encoderFor("XMLF"));
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
