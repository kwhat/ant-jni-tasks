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
package org.jnitasks.toolchains.adapters;

import org.apache.tools.ant.DirectoryScanner;
import org.jnitasks.LdTask;
import org.jnitasks.toolchains.LinkerAdapter;
import org.jnitasks.types.AbstractFeature;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GccLinker extends LinkerAdapter {
	public GccLinker() {
		super();

		setExecutable("gcc");
	}

	public Iterator<String> getArgs() {
		List<String> args = new ArrayList<String>();

		for (AbstractFeature feat : features) {
			if (feat.isIfConditionValid() && feat.isUnlessConditionValid()) {
				if (feat instanceof LdTask.Library) {
					LdTask.Library lib = (LdTask.Library) feat;

					if (lib.getPath() != null) {
						String path = lib.getPath().getPath().replace('\\', '/');

						if (path.indexOf(" ") >= 0) {
							path = '"' + path + '"';
						}

						args.add("-L" + path);
					}

					if (lib.getLib() != null) {
						args.add("-l" + lib.getLib());
					}
				}
				else if (feat instanceof LdTask.Argument) {
					LdTask.Argument arg = (LdTask.Argument) feat;

					args.add(arg.getValue());
				}
				else if (feat instanceof LdTask.FileSetArgument) {
					LdTask.FileSetArgument arg = (LdTask.FileSetArgument) feat;

					DirectoryScanner scanner = arg.getFileSet().getDirectoryScanner(getProject());
					String[] files = scanner.getIncludedFiles();
					for(int i = 0; i < files.length; i++) {
						File basePath = scanner.getBasedir();

						// Convert Windows paths to posix compatible paths.
						args.add(new File(basePath, files[i]).getAbsolutePath().replace('\\', '/'));
					}
				}
			}
		}

		String outfile = this.getOutFile().getPath().replace('\\', '/');
		if (outfile.indexOf(" ") >= 0) {
			outfile = '"' + outfile + '"';
		}

		args.add("-o " + outfile);

		return args.iterator();
	}
}
