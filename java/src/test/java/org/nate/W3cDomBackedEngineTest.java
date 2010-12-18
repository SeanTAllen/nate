package org.nate;

public class W3cDomBackedEngineTest extends EngineTestBase {
	
	@Override
	protected Engine encodeHtmlFragment(String html) {
		return Nate.newWith(html, Nate.encoders().encoderFor("HTMLF"));
	}
	
	@Override
	protected Engine encodeHtmlDocument(String html) {
		return Nate.newWith(html, Nate.encoders().encoderFor("HTML"));
	}

}
