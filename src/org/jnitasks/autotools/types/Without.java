package org.jnitasks.autotools.types;

public class Without extends ConfigFeature {
	@Override
	public String toString() {
		return "--without-" + this.getFlag();
	}
}
