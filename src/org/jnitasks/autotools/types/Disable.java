package org.jnitasks.autotools.types;

public class Disable extends ConfigFeature {
	@Override
	public String toString() {
		return "--disable-" + this.getFlag();
	}
}
