package org.nate;


public class JSoupBackedEngineTest extends EngineTestBase {
	
	protected Engine encodeHtmlFragment(String html) {
		return Nate.newWith(html, Nate.encoders().encoderFor("HTMLF"));
	}
	
	protected Engine encodeHtmlDocument(String html) {
		return Nate.newWith(html, Nate.encoders().encoderFor("HTML"));
	}

}
