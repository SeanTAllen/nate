package org.nate.encoder;


public interface NateDocument extends NateNode {

	NateDocument copy();

	NateDocument copy(String selector);

	NateDocument copyContentOf(String selector);

}
