package org.jnitasks.types;

public class Include extends AbstractFeature implements Cloneable {
	private String path;

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return this.path;
	}
}
