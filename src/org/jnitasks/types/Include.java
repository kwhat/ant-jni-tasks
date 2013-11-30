package org.jnitasks.types;

public class Include extends AbstractFeature implements Cloneable {
	private String path;
	private boolean prepend = false;

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return this.path;
	}

	public void setPrepend(boolean prepend) {
		this.prepend = prepend;
	}

	public boolean isPrepend() {
		return this.prepend;
	}
}
