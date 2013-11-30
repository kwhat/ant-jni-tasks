package org.jnitasks.autotools.types;

import org.jnitasks.types.ToggleFeature;

public class Enable extends ToggleFeature {
	private String flag;

	public void addText(String flag) {
		this.flag = flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getFlag() {
		return this.flag;
	}
}
