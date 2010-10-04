package org.nate;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EngineInjectionRulesTest {

	private Engine engine;

	@Before public void setup() {
		initMocks(this);
		engine = engineUnderTest();
	}

	@Test public void shouldAddAttributeWhenNoAttributeInMatch() {
		// when <a href="#"></a> and { 'a' => { 'text' => 'some text' }}
		// expect <a href="#" text="some text"></a>
	}

	@Test public void shouldReplaceAttributeWhenAttributeInMatch() {
		// when <a href="#"></a> and data { 'a' => { 'href' => 'some text' }}
		// expect <a href="some text"></a>
	}

	@Test public void shouldProcessEachElementWhenDataContainsMap() {
		// when <img src="" height="200" width="400"/> and { 'img' => { 'src' => 'foo.gif', alt='an image', title='pretty' }}
		// expect <img src="foo.gif" height="200" width="400" alt="an image" title="pretty"/>
	}

	@Test public void shouldReplaceInnerContentWhenDataNotMap() {
		// when <a href="#"></a> and { 'a' => 'click me' }
		// expect <a href="#">click me</a>
	}

	@Test public void shouldReplaceInnerContentWhenDataContainsMapWithReservedContentKey() {
		// when <a href="#"></a> and { 'a' => { 'content' => 'click me', 'href' => 'foo.org' }}
		// expect <a href="foo.org">click me</a>
	}

	protected Engine engineUnderTest() {
		return new DefaultEngine();
	}
}