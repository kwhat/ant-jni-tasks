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
	private boolean uninstalled = false;
	private String exists = null;
	private boolean linkStatic = false;
	private String libraries = null;

	private List<PkgConfigTask.Variable> var = new Vector<PkgConfigTask.Variable>();
	private List<Environment.Variable> env = new Vector<Environment.Variable>();
	private List<DirSet> dirsets = new Vector<DirSet>();


	public void setOutputproperty(String outputProperty) {
		this.outputProperty = outputProperty;
	}

	public void setModversion(boolean modversion) {
		this.modversion = modversion;
	}
	public void setQuiet(boolean quiet) {
		this.quiet = quiet;
	}
	public void setCflags(boolean cflags) {
		this.cflags = cflags;
	}
	public void setLibs(boolean libs) {
		this.libs = libs;
	}
	public void setLibsonlypath(boolean libsOnlyPath) {
		this.libsOnlyPath = libsOnlyPath;
	}
	public void setLibsonlylib(boolean libsOnlyPath) {
		this.libsOnlyLib = libsOnlyLib;
	}
	public void setUninstalled(boolean uninstalled) {
		this.uninstalled = uninstalled;
	}
	public void setExists(boolean quiet) {
		this.exists = exists;
	}
	public void setStatic(boolean linkStatic) {
		this.linkStatic = linkStatic;
	}
	public void setLibraries(String libraries) {
		this.libraries = libraries;
	}

	public void createVariable(PkgConfigTask.Variable var) {
		this.var.add(var);
	}
	public void addEnv(Environment.Variable var) {
		this.env.add(var);
	}
	public void addDirset(DirSet dir) {
		dirsets.add(dir);
	}

	public PkgConfigTask.Variable createVariable() {
		PkgConfigTask.Variable variable = new PkgConfigTask.Variable();
		var.add(variable);

		return variable;
	}

	@Override
	public void execute() throws BuildException {
		// Set the command to execute along with any required arguments.
		StringBuilder command = new StringBuilder(PkgConfigTask.cmd);

		// TODO Check MSVC toolchain and set --msvc-syntax and possibly --dont-define-prefix

		if (modversion) {
			command.append(" --modversion");
		}

		if (quiet) {
			command.append(" --silence-errors");
		}
		else {
			command.append(" --print-errors");
		}

		if (cflags) {
			command.append(" --cflags");
		}

		if (libs) {
			command.append(" --libs");
		}

		if (libsOnlyPath) {
			command.append(" --libs-only-L");
		}

		if (libsOnlyLib) {
			command.append(" --libs-only-l");
		}

		if (this.uninstalled) {
			command.append(" --uninstalled");
		}

		if (this.exists != null) {
			command.append(" --exists ").append(exists);
		}

		if (this.linkStatic) {
			command.append(" --static");
		}

		// Variable arguments for variable and defined-variable.
		Iterator<PkgConfigTask.Variable> varItems = var.iterator();
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

		if (libraries != null) {
			command.append(' ').append(libraries);
		}


		// Add dirset to the env variable for PKG_CONFIG_PATH
		StringBuilder configPath = new StringBuilder();
		Iterator<DirSet> dirItems = dirsets.iterator();
		while (dirItems.hasNext()) {
			DirSet path = dirItems.next();

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
			Environment.Variable envVar = new Environment.Variable();
			envVar.setKey("PKG_CONFIG_PATH");
			envVar.setValue(configPath.toString());
			env.add(envVar);
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

		shell.setExecutable("sh");

		shell.setFailonerror(true);
		shell.setOutputproperty(this.outputProperty);

		shell.createArg().setValue("-c");
		shell.createArg().setValue(command.toString());

		shell.execute();
	}

	public static class Variable extends CcTask.Define {
	}
}
