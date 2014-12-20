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
package org.jnitasks.autotools;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Echo;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.jnitasks.CcTask;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class AutoreconfTask extends Task {
	private static final String cmd = "autoreconf";

	private File dir = null;
	private boolean force = false;
	private boolean install = false;
	private boolean quiet = false;
	private List<AutoreconfTask.Include> includes = new Vector<AutoreconfTask.Include>();

	public void setDir(File dir) {
		this.dir  = dir;
	}

	public void setForce(boolean force) {
		this.force = force;
	}

	public void setInstall(boolean install) {
		this.install = install;
	}

	public void setQuiet(boolean quiet) {
		this.quiet = quiet;
	}

	public AutoreconfTask.Include createInclude() {
		Include inc = new Include();
		includes.add(inc);

		return inc;
	}

	@Override
	public void execute() throws BuildException {
		// Set the command to execute along with any required arguments.
		StringBuilder command = new StringBuilder(AutoreconfTask.cmd);

		// Take care of the optional arguments.
		if (!this.quiet) {
			command.append(" --verbose");
		}

		if (this.force) {
			command.append(" --force");
		}

		if (this.install) {
			command.append(" --install");
		}

		// Include arguments for nested Include.
		Iterator<AutoreconfTask.Include> iterator = includes.iterator();
		while (iterator.hasNext()) {
			AutoreconfTask.Include include = iterator.next();

			if (include.isIfConditionValid() && include.isUnlessConditionValid()) {
				if (include.isPrepend()) {
					command.append(" -B").append(include.getPath());
				}
				else {
					command.append(" -I").append(include.getPath());
				}
			}
		}

		// Print the executed command.
		Echo echo = (Echo) getProject().createTask("echo");
		echo.addText(command.toString());
		echo.setTaskName(this.getTaskName());
		echo.execute();

		// Create an exec task to run a shell.  Using the current shell to
		// execute commands is required for Windows support.
		ExecTask shell = (ExecTask) getProject().createTask("exec");
		shell.setTaskName(this.getTaskName());

		shell.setDir(dir);
		shell.setExecutable("sh");
		shell.setFailonerror(true);

		shell.createArg().setValue("-c");
		shell.createArg().setValue(command.toString());

		shell.execute();
	}

	public static class Include extends CcTask.Include {
		private boolean prepend = false;

		public void setPrepend(boolean prepend) {
			this.prepend = prepend;
		}

		public boolean isPrepend() {
			return this.prepend;
		}
	}
}
