package org.nate.internal;

import static org.nate.internal.Assertions.assertType;

import java.io.InputStream;
import java.util.Map;

import org.nate.Encoder;
import org.nate.Engine;
import org.nate.encoder.NateDocument;
import org.nate.internal.transformer.NateTransformers;

public class NateDocumentBackedEngine implements Engine {

	private final NateDocument document;

	public NateDocumentBackedEngine(InputStream source, Encoder encoder) {
		this.document = encoder.encode(source);
	}
	
	private NateDocumentBackedEngine(NateDocument newDocument) {
		this.document = newDocument;
	}

	public NateDocumentBackedEngine inject(Object data) {
		assertType("data", data, Map.class);
		NateDocument newDocument = document.copy();
		NateTransformers.from(data).transform(newDocument);
		return new NateDocumentBackedEngine(newDocument);
	}

	public NateDocumentBackedEngine select(String selector) {
		selector = selector.trim();
		if (selector.startsWith(CONTENT_SELECTION_FLAG)) {
			return new NateDocumentBackedEngine(document.copyContentOf(selector.substring(CONTENT_SELECTION_FLAG.length())));
		}
		return new NateDocumentBackedEngine(document.copy(selector));
	}


	public NateDocument getDocument() {
		return document;
	}

	public String render() {
		return document.render();
	}
	
	@Override
	public String toString() {
		return render();
	}

}