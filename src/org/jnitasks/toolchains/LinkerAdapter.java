/* JNITasks: Ant tasks for JNI projects.
 * Copyright (C) 2013-2014 Alexander Barker.  All Rights Received.
 * https://github.com/kwhat/jnitasks/
 *
 * JNITasks is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JNITasks is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
