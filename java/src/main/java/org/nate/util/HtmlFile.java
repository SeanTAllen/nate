package org.nate.util;

import java.io.*;

public class HtmlFile {

	private static final int CAPACITY = 1024;

	private StringBuffer lineBuffer;
	private String contents;
	private File file;

	public static String contentsOf(File file) {
		return new HtmlFile(file).contents();
	}

	public HtmlFile(File file) {
		this.file = file;
	}

	public String contents() {
		initializeBuffer();
		readContents();
		resetBuffer();
		return contents;
	}

	private void resetBuffer() {
		lineBuffer = null;
	}

	private void initializeBuffer() {
		lineBuffer = new StringBuffer(CAPACITY);
	}

	private void readContents() {
		Reader reader = createReader();
		try {
			readContentsWith(reader);
		} finally {
			close(reader);
		}
	}

	private void readContentsWith(Reader reader) {
		String line;
		BufferedReader lineReader = new BufferedReader(reader);
		try {
			while ((line = lineReader.readLine()) != null)
				lineBuffer.append(line).append('\n');
			contents = lineBuffer.toString();
		} catch (IOException e) {
			contents = "Error reading '" + file.getName() + "' - " + e.getMessage();
		}
	}

	private Reader createReader() {
		try {
			return new FileReader(file);
		} catch (FileNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}

	private void close(Reader reader) {
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}