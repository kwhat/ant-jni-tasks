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
		StringBuilder command = new StringBuilder(this.getExecutable());

		for (AbstractFeature feat : features) {
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

		command.append(" -o ").append(this.getOutFile());

		return command.toString();
	}
}
