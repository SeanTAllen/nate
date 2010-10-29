package org.nate;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.jruby.RubyArray;
import org.jruby.RubyHash;

import cuke4duke.annotation.I18n.EN.Given;
import cuke4duke.annotation.I18n.EN.Then;
import cuke4duke.annotation.I18n.EN.When;

public class NateSteps {


	private List<Engine> nateStates = new ArrayList<Engine>();

	static {
		// Needed to allow us to evaluate Ruby code without interfering with Cucumber!
		System.setProperty("org.jruby.embed.localcontext.scope", "singlethread");
	}

	@Given("^the HTML fragment \"([^\"]*)\"$")
	public void setHtml(String html) {
		Engine nate = Engine.newWith(html, Engine.encoders().encoderFor("HTMLF"));
		nateStates.add(nate);
	}

	@Given("^the file \"([^\"]*)\"$")
	public void theFile(String filename) {
		Engine nate = Engine.newWith(new File(filename));
		nateStates.add(nate);
	}

	@When("^([^\"]*) is injected$")
	public void inject(String data) throws Exception {
		nateStates.add(currentNateEngine().inject(parseRubyExpression(data)));
	}

	@Then("^the HTML fragment is (.*)$")
	public void test(String expectedHtml) throws Exception {
		assertXmlFragmentsEqual(expectedHtml, currentNateEngine().render());
	}

	@When("^(.*) is injected sometime later$")
	public void isInjectedSometimeLater(String data) throws Exception {
		inject(data);
	}

	@Then("^the original HTML fragment is(.*)$")
	public void theOriginalHTMLFragmentIs(String expectedHtml) throws Exception {
		assertXmlFragmentsEqual(expectedHtml, nateStates.get(0).render());
	}

	@When("^\"([^\"]*)\" is selected$")
	public void isSelected(String selector) {
		nateStates.add(currentNateEngine().select(selector));
	}
	
	private void assertXmlFragmentsEqual(String expected, String actual) throws Exception {
		// Wrap in fake roots in case the xml has multiple roots, otherwise you get a parser exception
		assertXMLEqual(wrapInFakeRoot(expected.trim()), wrapInFakeRoot(actual));
	}

	private String wrapInFakeRoot(String fragment) {
		return "<fake>" + fragment + "</fake>";
	}

	// This method is needed because the features express the data used to fill in the templates using ruby syntax like:
	// { 'h2' => 'Monkey' } 
	private Object parseRubyExpression(String rubyString) throws ScriptException {
		ScriptEngine rubyEngine = new ScriptEngineManager().getEngineByName("jruby");
		defineRubyConstants(rubyEngine);
		Object result = rubyEngine.eval(rubyString, new SimpleScriptContext());
		return convertToOrdinaryJavaClasses(result);
	}

	private void defineRubyConstants(ScriptEngine rubyEngine) throws ScriptException {
		// This is so that the features can use Nate::Engine::CONTENT_ATTRIBUTE
		rubyEngine.eval("module Nate\n class Engine\n CONTENT_ATTRIBUTE = '*content*'\n end\n end\n");
	}

	// Would actually not need to do this, except that RubyHash and RubyArray seem to have been loaded
	// in a different class loader (at least I think that is the case!)!!!!
	private Object convertToOrdinaryJavaClasses(Object rubyObject) {
		if (rubyObject == null || isAnAcceptableJavaType(rubyObject)) {
			return rubyObject;
		}
		if (rubyObject instanceof RubyHash) {
			return convertToJavaMap((RubyHash) rubyObject);
		}
		if (rubyObject instanceof RubyArray) {
			return convertToJavaList((RubyArray) rubyObject);
		}
		throw new IllegalStateException("Cannot handle " + rubyObject.getClass());
	}

	@SuppressWarnings("unchecked")
	private List convertToJavaList(RubyArray rubyArray) {
		List result = new ArrayList();
		for (Object object : rubyArray) {
			result.add(convertToOrdinaryJavaClasses(object));
		}
		return result;
	}

	private boolean isAnAcceptableJavaType(Object rubyObject) {
		return rubyObject instanceof String || rubyObject instanceof Number;
	}

	private Map<String, Object> convertToJavaMap(RubyHash hash) {
		Map<String, Object> result = new HashMap<String, Object>();
		for (Object key : hash.keySet()) {
			if (!(key instanceof String)) {
				throw new IllegalStateException("Keys must be strings, but got:" + key.getClass());
			}
			result.put((String) key, convertToOrdinaryJavaClasses(hash.get(key)));
		}
		return result;
	}


	private Engine currentNateEngine() {
		return nateStates.get(nateStates.size() - 1);
	}

}