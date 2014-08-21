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
package org.jnitasks.pkgconfig;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Echo;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.types.DirSet;
import org.apache.tools.ant.types.Environment;
import org.jnitasks.CcTask;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class PkgConfigTask extends Task {
	private static final String cmd = "pkg-config";

	private String outputProperty = null;

	private boolean modversion = false;
	private boolean quiet = false;

	private boolean cflags = false;
	private boolean libs = false;
	private boolean libsOnlyPath = false;
	private boolean libsOnlyLib = false;

	// TODO Check MSVC toolchain and set --msvc-syntax and --dont-define-prefix

	protected Vector<PkgConfigTask.Variable> variables = new Vector<PkgConfigTask.Variable>();
	private boolean uninstalled = false;
	private String exists = null;
	private boolean libsStatic = false;




	private String libraries = null;
	private List<DirSet> dirsets = new Vector<DirSet>();

	public void setLibs(boolean libs) {
		this.libs = libs;
	}

	public void setCflags(boolean cflags) {
		this.cflags = cflags;
	}

	public void setOutputproperty(String outputProperty) {
		this.outputProperty = outputProperty;
	}

	public void setLibraries(String libraries) {
		this.libraries = libraries;
	}

	public void addDirset(DirSet dir) {
		dirsets.add(dir);
	}

	public PkgConfigTask.Variable createVariable() {
		PkgConfigTask.Variable variable = new PkgConfigTask.Variable();
		variables.add(variable);

		return variable;
	}

	@Override
	public void execute() throws BuildException {
		// Set the command to execute along with any required arguments.
		StringBuilder command = new StringBuilder(cmd);

		if (this.modversion) {
			command.append(" --modversion");
		}

		if (this.quiet) {
			command.append(" --silence-errors");
		}
		else {
			command.append(" --print-errors");
		}

		if (this.cflags) {
			command.append(" --cflags");
		}

		if (this.libs) {
			command.append(" --libs");
		}

		if (this.libsOnlyPath) {
			command.append(" --libs-only-L");
		}

		if (this.libsOnlyLib) {
			command.append(" --libs-only-l");
		}

		// Variable arguments for variable and defined-variable.
		Iterator<PkgConfigTask.Variable> varItems = variables.iterator();
		while (varItems.hasNext()) {
			PkgConfigTask.Variable var = varItems.next();

			if (var.getValue() == null) {
				command.append(" --variable=").append(var.getName());
			}
			else {
				command.append(" --variable=").append(var.getName())
						.append('=').append(var.getValue());
			}
		}

		if (this.uninstalled) {
			command.append(" --uninstalled");
		}

		if (this.exists != null) {
			command.append(" --exists ").append(exists);
		}

		if (this.libsStatic) {
			command.append(" --static");
		}


		// Print the executed command.
		Echo echo = (Echo) getProject().createTask("echo");
		echo.addText(command.toString());
		echo.setTaskName(this.getTaskName());
		echo.execute();

		// Create an exec task to run a shell.  Using the current shell to
		// execute commands is required for Windows support.
		ExecTask shell = (ExecTask) this.getProject().createTask("exec");

		shell.setTaskName(this.getTaskName());

		// Environment.Variable arguments for nested env items.
		Iterator<Environment.Variable> envItems = env.iterator();
		while (envItems.hasNext()) {
			shell.addEnv(envItems.next());
		}

		shell.setDir(dir);
		shell.setExecutable("sh");

		shell.setFailonerror(true);

		shell.createArg().setValue("-c");
		shell.createArg().setValue(command.toString());

		shell.execute();


		// Print the executed command.
		Echo echo = (Echo) getProject().createTask("echo");
		echo.setTaskName(this.getTaskName());
		echo.setAppend(true);


		// Create an exec task to run a shell.  Using the current shell to
		// execute commands is required for Windows support.
		ExecTask shell = (ExecTask) getProject().createTask("exec");
		shell.setTaskName(this.getTaskName());
		shell.setFailonerror(true);
		shell.setOutputproperty(this.outputProperty);

		shell.setExecutable(PkgConfigTask.cmd);
		echo.addText(PkgConfigTask.cmd);

		// Take care of the optional arguments.
		if (this.cflags) {
			echo.addText(" --cflags");
			shell.createArg().setValue("--cflags");
		}

		if (this.libs) {
			echo.addText(" --libs");
			shell.createArg().setValue("--libs");
		}



		echo.addText(' ' + this.libraries);
		shell.createArg().setLine(this.libraries);

		StringBuilder configPath = new StringBuilder();
		Iterator<DirSet> iterator = dirsets.iterator();
		while (iterator.hasNext()) {
			DirSet path = iterator.next();

			DirectoryScanner scanner = path.getDirectoryScanner();
			String[] dirs = scanner.getIncludedDirectories();
			for(int i = 0; i < dirs.length; i++) {
				File basePath = scanner.getBasedir();

				// Check for previous data and add the platform dependent separator if needed.
				if (configPath.length() > 0) {
					// If Windows use comma.
					if (System.getProperty("os.name", "").toLowerCase().indexOf("win") >= 0) {
						configPath.append(',');
					}
					else {
						configPath.append(';');
					}
				}

				// Do not convert Windows dirsets to posix compatible paths.
				configPath.append(new File(basePath, dirs[i]).getAbsolutePath());
			}
		}

		// Create the required environment variables.
		if (configPath.length() > 0) {
			Environment.Variable var = new Environment.Variable();
			var.setKey("PKG_CONFIG_PATH");
			var.setValue(configPath.toString());

			shell.addEnv(var);
		}

		echo.execute();
		shell.execute();
	}

	public static class Variable extends CcTask.Define {
	}
}
