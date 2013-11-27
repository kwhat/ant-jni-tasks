package org.jnitasks.autotools.types;

public class Enable extends ConfigFeature {
	@Override
	public String toString() {
		return "--enable-" + this.getFlag();
	}
}
