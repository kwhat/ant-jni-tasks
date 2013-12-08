package org.jnitasks.toolchains;

import org.apache.tools.ant.ProjectComponent;
import org.jnitasks.types.AbstractFeature;

import java.util.Iterator;
import java.util.Vector;

public abstract class LinkerAdapter extends ProjectComponent {
	protected String prefix = "";
	protected String executable = "cc";

	protected Vector<AbstractFeature> features = new Vector<AbstractFeature>();
	private Vector<String> inFiles = new Vector<String>();
	private String outFile = "a.out";

	public void addArg(AbstractFeature arg) {
		this.features.add(arg);
	}

	public void addInFile(String file) {
		this.inFiles.add(file);
	}

	public Iterator<String> getInFiles() {
		return inFiles.iterator();
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
