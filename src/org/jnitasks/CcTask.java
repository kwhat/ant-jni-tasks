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
package org.jnitasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Echo;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.taskdefs.Parallel;
import org.apache.tools.ant.taskdefs.Sequential;
import org.apache.tools.ant.types.FileSet;
import org.jnitasks.toolchains.CompilerAdapter;
import org.jnitasks.toolchains.ToolchainFactory;
import org.jnitasks.types.AbstractFeature;
import java.io.File;
import java.util.Iterator;
import java.util.Vector;

public class CcTask extends Task {
	private int jobs = Runtime.getRuntime().availableProcessors();
	private File objdir = null;
	private String toolchain = "gcc";
	private String host = "";

	private Vector<FileSet> filesets = new Vector<FileSet>();
	private Vector<AbstractFeature> features = new Vector<AbstractFeature>();

	public CcTask.Argument createArg() {
		CcTask.Argument arg = new CcTask.Argument();
		features.add(arg);

		return arg;
	}

	public CcTask.Define createDefine() {
		CcTask.Define macro = new CcTask.Define();
		features.add(macro);

		return macro;
	}

	public CcTask.Include createInclude() {
		CcTask.Include inc = new CcTask.Include();
		features.add(inc);

		return inc;
	}

	public void addFileset(FileSet fileset) {
		filesets.add(fileset);
	}

	public void setToolchain(String toolchain) {
		this.toolchain = toolchain;
	}

	public void setHost(String host) {
		if (host == null) {
			this.host = "";
		}
		else {
			this.host = host;
		}
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

	public void execute() {
		// First, populate all of the properties we care about for this task.
		if (getProject().getProperty("ant.build.native.toolchain") != null) {
			this.setToolchain(getProject().getProperty("ant.build.native.toolchain"));
		}

		if (getProject().getProperty("ant.build.native.host") != null) {
			this.setHost(getProject().getProperty("ant.build.native.host"));
		}

		// Setup the compiler.
		CompilerAdapter compiler = ToolchainFactory.getCompiler(toolchain);
		compiler.setProject(getProject());

		if (host != null && host.length() > 0) {
			// Prepend the host string to the executable.
			compiler.setExecutable(host + '-' + compiler.getExecutable());
		}

		for (AbstractFeature feat : features) {
			if (feat.isIfConditionValid() && feat.isUnlessConditionValid()) {
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
				File inFile = new File(basePath, files[i]);
				compiler.setInFile(inFile);


				File outFile;
				if (objdir != null) {
					// If the objdir is set, use that for output.
					outFile = new File(objdir, files[i].substring(0, files[i].lastIndexOf('.')) + ".o");

					compiler.setOutFile(outFile);
				}
				else {
					outFile = compiler.getOutFile();
				}

				// Check to see if the source file has been modified more recently than the object file.
				if (inFile.lastModified() >= outFile.lastModified()) {
					Sequential sequential = (Sequential) this.getProject().createTask("sequential");

					// Print the executed command.
					Echo echo = (Echo) getProject().createTask("echo");
					echo.setTaskName(this.getTaskName());
					echo.setAppend(true);

					// Create an exec task to run a shell.  Using the current shell to
					// execute commands is required for Windows support.

					ExecTask shell = (ExecTask) getProject().createTask("exec");
					shell.setTaskName(this.getTaskName());
					shell.setFailonerror(true);
					//shell.setDir(dir);

					echo.addText(compiler.getExecutable());
					shell.setExecutable(compiler.getExecutable());

					Iterator<String> args = compiler.getArgs();
					while (args.hasNext()) {
						String arg = args.next();

						echo.addText(" " + arg);
						shell.createArg().setLine(arg);
					}

					sequential.addTask(echo);
					sequential.addTask(shell);

					// Add the sequential task containing echo and cc shell command to the parallel task.
					parallel.addTask(sequential);
				}
			}
		}

		// Execute the compile.
		parallel.execute();
	}

	public static class Argument extends AbstractFeature implements Cloneable {
		private String value;

		public void setValue(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	public static class Include extends AbstractFeature implements Cloneable {
		private String path = ".";

		public void setPath(String path) {
			this.path = path;
		}

		public String getPath() {
			return this.path;
		}
	}

	public static class Define extends AbstractFeature implements Cloneable {
		private String name;
		private String value;

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}
}
