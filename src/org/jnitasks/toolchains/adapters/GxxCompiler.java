package org.jnitasks.toolchains.adapters;

public class GxxCompiler extends GccCompiler {
	public GxxCompiler() {
		super();
		super.executable = "g++";
	}
}
