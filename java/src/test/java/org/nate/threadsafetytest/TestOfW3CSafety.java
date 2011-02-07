package org.nate.threadsafetytest;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Test showing that even the accessors for org.w3c.dom.Document are not thread safe.
 * This sometimes fails.
 */
public class TestOfW3CSafety {
	private static final Document DOC = parseXml("<div><p>p1</p><p>p2</p></div>");

	private static final int NUM_THREADS = 20;
	private static final long DURATION_IN_MILLIS = 5 * 1000;
	private static final CyclicBarrier START_BARRIER = new CyclicBarrier(NUM_THREADS);
	private static final List<Throwable> errors = Collections.synchronizedList(new ArrayList<Throwable>());

//	@Test
	public void cloneShouldBeThreadSafe() throws Throwable {
		
		ArrayList<Thread> threads = new ArrayList<Thread>();
		long finishTime = System.currentTimeMillis() + DURATION_IN_MILLIS;
		for(int i = 0; i < NUM_THREADS; i++) {
			Thread thread = new Thread(new ThreadSafetyTask(finishTime));
			thread.start();
			threads.add(thread);
		}
		for (Thread thread : threads) {
			thread.join(2 * DURATION_IN_MILLIS);
		}
		if (!errors.isEmpty()) {
			throw errors.get(0);
		}
	}
	
	private static class ThreadSafetyTask implements Runnable {

		private final long finishTime;

		public ThreadSafetyTask(long finishTime) {
			this.finishTime = finishTime;
		}

		@Override
		public void run() {
			try {
				START_BARRIER.await(2, TimeUnit.SECONDS);
				while(System.currentTimeMillis() < finishTime) {
					for(int i = 0; i < 10; i++) {
						assertNotNull(DOC.getDocumentElement());
					}
				}
			} catch (Throwable e) {
				errors.add(e);
				e.printStackTrace();
			}
		}
		
	}
	private static Document parseXml(String input) {
		try {
			return parseXml(new ByteArrayInputStream(input.getBytes()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	private static Document parseXml(InputStream inputStream) throws Exception {
			return createDocumentParser().parse(inputStream);

	}

	private static DocumentBuilder createDocumentParser() throws ParserConfigurationException {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		builder.setEntityResolver(NULL_ENTITY_RESOLVER);
		return builder;
	}

	private static final EntityResolver NULL_ENTITY_RESOLVER = new EntityResolver() {
		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
			return new InputSource(new StringReader(""));
		}
	};

}
