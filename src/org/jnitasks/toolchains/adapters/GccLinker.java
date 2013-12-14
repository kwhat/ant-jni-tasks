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
package org.jnitasks.toolchains.adapters;

import org.apache.tools.ant.DirectoryScanner;
import org.jnitasks.toolchains.LinkerAdapter;
import org.jnitasks.types.AbstractFeature;
import org.jnitasks.types.Library;

import java.io.File;

public class GccLinker extends LinkerAdapter {
	public GccLinker() {
		super();

		super.executable = "gcc";
	}

	public String describeCommand() {
		StringBuilder command = new StringBuilder(this.getCommand());

		for (AbstractFeature feat : features) {
			if (feat.isValidOs() && feat.isIfConditionValid() && feat.isUnlessConditionValid()) {
				if (feat instanceof Library) {
					Library lib = (Library) feat;

					if (lib.getPath() != null) {
						command.append(" -L").append(lib.getPath().getPath());
					}

					if (lib.getLib() != null) {
						command.append(" -l").append(lib.getLib());
					}
				}
				else if (feat instanceof LinkerAdapter.Argument) {
					LinkerAdapter.Argument arg = (LinkerAdapter.Argument) feat;

					command.append(' ').append(arg.getValue());
				}
				else if (feat instanceof LinkerAdapter.FileSetArgument) {
					LinkerAdapter.FileSetArgument arg = (LinkerAdapter.FileSetArgument) feat;

					DirectoryScanner scanner = arg.getFileSet().getDirectoryScanner(getProject());
					String[] files = scanner.getIncludedFiles();
					for(int i = 0; i < files.length; i++) {
						File basePath = scanner.getBasedir();

						// Convert Windows paths to posix compatible paths.
						command.append(' ').append(new File(basePath, files[i]).getAbsolutePath().replace('\\', '/'));
					}
				}
			}
		}

		command.append(" -o ").append(this.getOutFile());

		return command.toString();
	}
}
