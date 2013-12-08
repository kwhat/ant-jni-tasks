package org.jnitasks.toolchains.adapters;

public class GxxLinker extends GccLinker {
	public GxxLinker() {
		super();
		super.executable = "g++";
	}
}
