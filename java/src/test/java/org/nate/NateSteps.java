package org.nate;

import cuke4duke.annotation.*;
import static cuke4duke.annotation.I18n.EN.*;

public class NateSteps {

	private String htmlFragment;
	private String data;

	@Given("^the plain HTML fragment \"([^\"]*)\"$")
	public void setHtml(String htmlFragment) {
		this.htmlFragment = htmlFragment;
	}

	@When("^([^\"]*) is injected$")
	public void setData(String data) {
		this.data = data;
	}

	@Then("^the plain HTML fragment is (.*)$")
	public void test(String data) {
	}
}