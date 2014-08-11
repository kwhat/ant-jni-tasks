/* JNITasks: Ant tasks for JNI projects.
 * Copyright (C) 2013-2014 Alexander Barker.  All Rights Received.
 * https://github.com/kwhat/jnitasks/
 *
 * JNITasks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * libUIOHook is distributed in the hope that it will be useful,
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

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class PkgConfigTask extends Task {
	private static final String cmd = "pkg-config";

	private boolean cflags = false;
	private boolean libs = false;
	private String outputProperty = null;
	private String packages = null;
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

	public void setPackages(String packages) {
		this.packages = packages;
	}

	public void addDirset(DirSet dir) {
		dirsets.add(dir);
	}

	@Override
	public void execute() throws BuildException {
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

		echo.addText(' ' + this.packages);
		shell.createArg().setLine(this.packages);

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
}
