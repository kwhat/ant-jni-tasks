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

import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.ProjectComponent;
import org.jnitasks.LdTask;
import org.jnitasks.types.AbstractFeature;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

public abstract class LinkerAdapter extends ProjectComponent {
	private String executable = "cc";

	protected Vector<AbstractFeature> features = new Vector<AbstractFeature>();
	private File outFile = new File("a.out");

	public void addArg(AbstractFeature arg) {
		this.features.add(arg);
	}

	public void setOutFile(File file) {
		this.outFile = file;
	}

	public File getOutFile() {
		return this.outFile;
	}

	public Iterator<File> getInFiles() {
		ArrayList<File> inFiles = new ArrayList<File>(features.size());

		for (AbstractFeature feat : features) {
			if (feat.isIfConditionValid() && feat.isUnlessConditionValid()) {
				if (feat instanceof LdTask.FileSetArgument) {
					LdTask.FileSetArgument arg = (LdTask.FileSetArgument) feat;

					DirectoryScanner scanner = arg.getFileSet().getDirectoryScanner(getProject());
					String[] files = scanner.getIncludedFiles();
					for(int i = 0; i < files.length; i++) {
						File basePath = scanner.getBasedir();

						inFiles.add(new File(basePath, files[i]));
					}
				}
			}
		}

		return inFiles.iterator();
	}

	public void setExecutable(String executable) {
		this.executable = executable;
	}

	public String getExecutable() {
		return executable;
	}

	public abstract Iterator<String> getArgs();
}
