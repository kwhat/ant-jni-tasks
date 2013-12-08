package org.jnitasks.toolchains;

import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.FileSet;
import org.jnitasks.types.AbstractFeature;

import java.util.Vector;

public abstract class LinkerAdapter extends ProjectComponent {
	protected String prefix = "";
	protected String executable = "cc";

	protected Vector<AbstractFeature> features = new Vector<AbstractFeature>();
	private String outFile = "a.out";

	public void addArg(AbstractFeature arg) {
		this.features.add(arg);
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

	public static class FileSetArgument extends AbstractFeature {
		private FileSet files;

		public void setFileSet(FileSet files) {
			this.files = files;
		}

		public FileSet getFileSet() {
			return this.files;
		}
	}
}
