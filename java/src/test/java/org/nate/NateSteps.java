package org.nate;

import cuke4duke.annotation.*;

import java.io.File;

import static cuke4duke.annotation.I18n.EN.*;

public class NateSteps {

	private Engine nate;
	private Encoder encoder;
	private String transformedHtml;

	@Given("^the HTML fragment \"([^\"]*)\"$")
	public void setHtml(String html) {
		encoder = Engine.encoders().of("HTML");
		nate = Engine.create(html, encoder);
	}

	@Given ("^the file \"([^\"]*)\"$")
	public void theFile(String filename) {
		nate = Engine.create(new File(filename));
	}

	@When("^([^\"]*) is injected$")
	public void inject(String data) {
		transformedHtml = nate.inject(data);
	}

	@Then("^the HTML fragment is (.*)$")
	public void test(String expectedHtml) {
		transformedHtml.equals(expectedHtml);
	}
}