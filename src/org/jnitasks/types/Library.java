package org.jnitasks.types;

import java.io.File;

public class Library extends AbstractFeature {
	private File path;
	private String lib;

	public void setPath(File path) {
		this.path = path;
	}

	public File getPath() {
		return this.path;
	}

	public void setLib(String lib) {
		this.lib = lib;
	}

	public String getLib() {
		return lib;
	}
}
