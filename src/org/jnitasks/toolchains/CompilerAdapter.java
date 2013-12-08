package org.jnitasks.toolchains;

import org.apache.tools.ant.ProjectComponent;
import org.jnitasks.types.AbstractFeature;

import java.util.Collection;
import java.util.Vector;

public abstract class CompilerAdapter extends ProjectComponent {
	protected String prefix = "";
	protected String executable = "cc";

	protected Vector<AbstractFeature> features = new Vector<AbstractFeature>();
	private String inFile, outFile;

	public void addArg(AbstractFeature arg) {
		this.features.add(arg);
	}

	public void addArgs(Collection<AbstractFeature> args) {
		this.features.addAll(args);
	}

	public void setInFile(String file) {
		this.inFile = file;
		this.outFile = inFile.substring(0, inFile.lastIndexOf('.')) + ".o";
	}

	public String getInFile() {
		return this.inFile;
	}

	public void setOutFile(String file) {

		this.outFile = file;
	}

	public String getOutFile() {
		return this.outFile;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getExecutable() {
		return prefix + executable;
	}

	public abstract String describeCommand();

	public String toString() {
		return this.describeCommand();
	}


	public static class Argument extends AbstractFeature {
		private String value;

		public void setValue(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

}
