package org.nate;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;

import org.junit.Test;
import org.nate.encoder.NateDocument;


public class EncodersTest {

	@Test
	public void shouldReturnNullEncoderForUnknownId() throws Exception {
		Encoder encoder = new Encoders().encoderFor("unknown");
		NateDocument doc = encoder.encode(new ByteArrayInputStream("banana".getBytes()));
		assertEquals("banana", doc.render());
	}
}
