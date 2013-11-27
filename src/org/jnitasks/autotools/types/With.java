package org.jnitasks.autotools.types;

public class With extends ConfigFeature {
	@Override
	public String toString() {
		return "--with-" + this.getFlag();
	}
}
