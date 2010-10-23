package org.nate;

import cuke4duke.annotation.I18n.EN.Given;
import cuke4duke.annotation.I18n.EN.Then;
import cuke4duke.annotation.I18n.EN.When;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.custommonkey.xmlunit.XMLAssert;
import org.jruby.RubyArray;
import org.jruby.RubyHash;

public class NateSteps {


	private Engine nate;
	private Encoder encoder;
	private String transformedHtml;

	static {
		// Needed to allow us to evaluate Ruby code without interfering with
		// Cucumber!
		System.setProperty("org.jruby.embed.localcontext.scope", "singlethread");
	}

	@Given("^the HTML fragment \"([^\"]*)\"$")
	public void setHtml(String html) {
		encoder = Engine.encoders().encoderFor("HTML");
		nate = Engine.newWith(html, encoder);
	}

	@Given("^the file \"([^\"]*)\"$")
	public void theFile(String filename) {
		nate = Engine.newWith(new File(filename));
	}

	@When("^([^\"]*) is injected$")
	public void inject(String data) throws ScriptException {
		transformedHtml = nate.inject(parseRubyExpression(data));
	}

	@Then("^the HTML fragment is (.*)$")
	public void test(String expectedHtml) throws Exception {
		XMLAssert.assertXMLEqual(expectedHtml, transformedHtml);
	}

	// This method is needed because the features express the data used to fill in the templates using ruby syntax like:
	// { 'h2' => 'Monkey' } 
	private Object parseRubyExpression(String rubyString) throws ScriptException {
		ScriptEngine rubyEngine = new ScriptEngineManager().getEngineByName("jruby");
		Object result = rubyEngine.eval(wrapRubyConstantsInQuotes(rubyString), new SimpleScriptContext());
		return convertToOrdinaryJavaClasses(result);
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

	private static final Pattern rubyConstantPattern = Pattern.compile("([A-Z]\\w*::)+[A-Z]\\w*");

	// We really need to figure out how to avoid doing this!!
	// Either the features need to stop using Ruby constants (use JSON perhaps?),
	// or we need independent features for each supported language.
	static String wrapRubyConstantsInQuotes(String rubyString) {
		return rubyConstantPattern.matcher(rubyString).replaceAll("'$0'");
	}

}