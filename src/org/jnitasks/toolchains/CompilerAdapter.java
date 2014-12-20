/* JNITasks: Ant tasks for JNI projects.
 * Copyright (C) 2013-2014 Alexander Barker.  All Rights Received.
 * https://github.com/kwhat/jnitasks/
 *
 * JNITasks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JNITasks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jnitasks.toolchains;

import org.apache.tools.ant.ProjectComponent;
import org.jnitasks.types.AbstractFeature;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

public abstract class CompilerAdapter extends ProjectComponent {
	private String executable = "cc";
	private File inFile, outFile;

	protected Vector<AbstractFeature> features = new Vector<AbstractFeature>();

	public void addArg(AbstractFeature arg) {
		this.features.add(arg);
	}

	public void addArgs(Collection<AbstractFeature> args) {
		this.features.addAll(args);
	}

	public void setInFile(File file) {
		this.inFile = file;
		this.outFile = new File(file.getPath().substring(0, file.getPath().lastIndexOf('.')) + ".o");
	}

	public File getInFile() {
		return this.inFile;
	}

	public void setOutFile(File file) {
		this.outFile = file;
	}

	public File getOutFile() {
		return this.outFile;
	}

	public void setExecutable(String executable) {
		this.executable = executable;
	}

	public String getExecutable() {
		return this.executable;
	}

	public abstract Iterator<String> getArgs();

	public static class Argument extends AbstractFeature {
		private String value;

		public void setValue(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}
}
