package org.jnitasks.types;

public class ToggleFeature extends AbstractFeature {
	private boolean negate = false;

	public void setNegation(boolean negate) {
		this.negate = negate;
	}

	public boolean isNegated() {
		return this.negate;
	}
}
