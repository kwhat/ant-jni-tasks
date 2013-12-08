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
package org.jnitasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.*;
import org.apache.tools.ant.types.FileSet;
import org.jnitasks.toolchains.CompilerAdapter;
import org.jnitasks.toolchains.ToolchainFactory;
import org.jnitasks.types.AbstractFeature;
import org.jnitasks.types.Define;
import org.jnitasks.types.Include;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;

public class CcTask extends MatchingTask {
	protected Vector<FileSet> filesets = new Vector<FileSet>();
	protected Vector<AbstractFeature> features = new Vector<AbstractFeature>();
	private int jobs = Runtime.getRuntime().availableProcessors();
	private File objdir = null;
	private String toolchain = "gcc";

	public void execute() {
		// Setup the compiler.
		CompilerAdapter compiler = ToolchainFactory.getCompiler(toolchain);
		compiler.setProject(getProject());

		for (AbstractFeature feat : features) {
			if (feat.isValidOs()) {
				compiler.addArg(feat);
			}
		}

		// Create a parallel task to try and run the compiler command in parallel.
		Parallel parallel = (Parallel) this.getProject().createTask("parallel");
		parallel.setFailOnAny(true);
		parallel.setThreadCount(jobs);


		Iterator<FileSet> iterator = filesets.iterator();
		while (iterator.hasNext()) {
			FileSet file = iterator.next();

			DirectoryScanner scanner = file.getDirectoryScanner(getProject());
			String[] files = scanner.getIncludedFiles();
			for(int i = 0; i < files.length; i++) {
				File basePath = scanner.getBasedir();

				// Convert Windows paths to posix compatible paths.
				compiler.setInFile(new File(basePath, files[i]).getAbsolutePath().replace('\\','/'));
				if (objdir != null) {
					compiler.setOutFile(
							new File(objdir, files[i].substring(0, files[i].lastIndexOf('.')) + ".o")
									.getAbsolutePath().replace('\\','/'));
				}


				Sequential sequential = (Sequential) this.getProject().createTask("sequential");

				// Print the executed command.
				Echo echo = (Echo) getProject().createTask("echo");
				echo.addText(compiler.describeCommand());
				echo.setTaskName(this.getTaskName());
				sequential.addTask(echo);


				// Create an exec task to run a shell.  Using the current shell to
				// execute commands is required for Windows support.

				ExecTask shell = (ExecTask) getProject().createTask("exec");
				shell.setTaskName(this.getTaskName());
				//shell.setDir(dir);
				shell.setExecutable("sh");
				shell.setFailonerror(true);
				shell.createArg().setValue("-c");
				shell.createArg().setValue(compiler.describeCommand());
				sequential.addTask(shell);

				// Add the sequential task containing echo and cc shell command to the parallel task.
				parallel.addTask(sequential);
			}
		}

		// Execute the compile.
		parallel.execute();
	}

	public void addFileset(FileSet fileset) {
		filesets.add(fileset);
	}

	public void addDefine(Define macro) {
		features.add(macro);
	}

	public void addInclude(Include include) {
		features.add(include);
	}

	public void setToolchain(String toolchain) {
		this.toolchain = toolchain;
	}

	public void setObjdir(File objdir) {
		if (!objdir.exists() || !objdir.isDirectory()) {
			throw new BuildException("Invalid object directory.");
		}

		this.objdir = objdir;
	}

	public void setJobs(String jobs) {
		if (jobs.equalsIgnoreCase("auto")) {
			this.jobs = Runtime.getRuntime().availableProcessors();
		}
		else {
			// FIXME else throw exception!
			this.jobs = Integer.parseInt(jobs);
		}
	}

	public CompilerAdapter.Argument createArg() {
		CompilerAdapter.Argument arg = new CompilerAdapter.Argument();
		features.add(arg);

		return arg;
	}
}
