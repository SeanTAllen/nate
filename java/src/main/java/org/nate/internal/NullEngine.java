package org.nate.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.nate.Engine;
import org.nate.exception.IONateException;

public class NullEngine implements Engine {

	private String content;

	public NullEngine(InputStream source) {
		try {
			StringWriter result = new StringWriter();
			BufferedReader reader = new BufferedReader(new InputStreamReader(source));
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				result.append(line);
			}
			content = result.toString();
		} catch (IOException e) {
			throw new IONateException(e);
		}
	}

	@Override
	public Engine inject(Object data) {
		return this;
	}

	@Override
	public Engine select(String selector) {
		return this;
	}

	@Override
	public String render() {
		return content;
	}

}
