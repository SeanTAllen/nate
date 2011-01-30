package org.nate;


public class Dom4jBackedEngineTest extends EngineTestBase {
	
	protected Engine encodeHtmlFragment(String html) {
		return Nate.newWith(html, Nate.encoders().encoderFor("XMLF"));
	}
	
	protected Engine encodeHtmlDocument(String html) {
		return Nate.newWith(html, Nate.encoders().encoderFor("XML"));
	}

}
