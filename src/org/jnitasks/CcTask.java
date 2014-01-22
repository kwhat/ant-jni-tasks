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
	protected Vector<FileSet> filesets = new Vector<FileSet>();
	protected Vector<AbstractFeature> features = new Vector<AbstractFeature>();
	private int jobs = Runtime.getRuntime().availableProcessors();
	private File objdir = null;
	private String toolchain = "gcc";
	private String host = "";

	public void addFileset(FileSet fileset) {
		filesets.add(fileset);
	}

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

	public void setToolchain(String toolchain) {
		this.toolchain = toolchain;
	}

	public String getToolchain() {
		return this.toolchain;
	}

	public void setHost(String host) {
		if (host == null) {
			this.host = "";
		}
		else {
			this.host = host;
		}
	}

	public String getHost() {
		return this.host;
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
		// Setup the compiler.
		CompilerAdapter compiler = ToolchainFactory.getCompiler(toolchain);
		compiler.setProject(getProject());

		setHost(getProject().getProperty("ant.build.native.host"));

		if (host.length() > 0) {
			// Prepend the host string to the executable.
			compiler.setExecutable(host + '-' + compiler.getExecutable());
		}
		else if (getProject().getProperty("ant.build.native.compiler") != null) {
			compiler.setExecutable(getProject().getProperty("ant.build.native.compiler"));
		}
		else if (System.getenv().get("CC") != null) {
			compiler.setExecutable(System.getenv().get("CC"));
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

				compiler.setInFile(new File(basePath, files[i]).getAbsolutePath());
				if (objdir != null) {
					// If the objdir is set, use that for output.
					compiler.setOutFile(
							new File(objdir, files[i].substring(0, files[i].lastIndexOf('.')) + ".o")
									.getAbsolutePath());
				}


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

		// Execute the compile.
		parallel.execute();
	}

	public static class Argument extends AbstractFeature {
		private String value;

		public void setValue(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	public static class Include extends AbstractFeature implements Cloneable {
		private String path;

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
