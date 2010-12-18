package org.nate.testutil;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.nate.testutil.WhiteSpaceIgnoringXmlMatcher.matchesXmlIgnoringWhiteSpace;

import org.junit.Test;


public class WhiteSpaceIgnoringXmlMatcherTest {

	@Test
	public void shouldMacthWhenXmlMatchesExactly() throws Exception {
		assertThat("<div/>", matchesXmlIgnoringWhiteSpace("<div/>"));
	}

	@Test
	public void shouldMatchWhenXmlMatchesIgnoringWhiteSpaceTextNodes() throws Exception {
		assertThat("<div/>", matchesXmlIgnoringWhiteSpace("<div> </div>"));
	}
	
	@Test
	public void shouldNotIgnoreNonWhiteSpaceNodes() throws Exception {
		assertThat("<div> hello </div>", matchesXmlIgnoringWhiteSpace("<div> hello </div>"));
	}
	
	@Test
	public void shouldTrim() throws Exception {
		assertThat("<div> \thello</div>", matchesXmlIgnoringWhiteSpace("<div>hello\n </div>"));
	}

	@Test
	public void shouldMacthWhenXmlMatchesIgnoringWhiteSpaceTextNodesWithNewLines() throws Exception {
		assertThat("<div/>", matchesXmlIgnoringWhiteSpace("<div> \n </div>"));
	}

	@Test
	public void shouldFailWhenDifferent() throws Exception {
		assertThat("<div></div>", not(matchesXmlIgnoringWhiteSpace("<div> hello </div>")));
	}

}
