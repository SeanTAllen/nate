package org.nate;

import org.nate.internal.NateDocumentBackedEngine;

public interface Engine {

	/**
	 * Pseudo-selector that matches the currently selected node.
	 * To be used in sub-selects when there is a need to replace the content of the currently selected node
	 * as well as one or more of its attributes.
	 * For example: <br /> 
	 * Given the HTML fragment "&lt;a href='#'&gt;my link&lt;/a&gt;" <br /> 
	 * When { 'a' => { 'href' => 'http://www.example.com', Engine.CONTENT_ATTRIBUTE => 'example.com' } } is injected<br /> 
	 * Then the HTML fragment is &lt;a href="http://www.example.com"&gt;example.com&lt;/a&gt; 
	 */
	public static final String CONTENT_ATTRIBUTE = "*content*";
	/**
	 * Prefix to use with the parameter to {@link NateDocumentBackedEngine#select(String)} to indicate when the content of the selected
	 * nodes is desired instead of the nodes themselves.
	 */
	public static final String CONTENT_SELECTION_FLAG = "##";

	Engine inject(Object data);

	Engine select(String selector);

	String render();

	String toString();

}