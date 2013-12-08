package org.jnitasks.autotools;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Echo;
import org.apache.tools.ant.taskdefs.ExecTask;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

// TODO Add support for -I, -B  via TaskContainer and DirSet
public class AutoreconfTask extends Task {
	private File dir;
	private boolean force = false;
	private boolean install = false;
	private boolean verbose = true;
	private List<Include> includes = new Vector<Include>();

	public void setDir(File dir) {
		this.dir  = dir;
	}

	public void setForce(boolean force) {
		this.force = force;
	}

	public void setInstall(boolean install) {
		this.install = install;
	}

	public AutoreconfTask.Include createInclude() {
		Include inc = new Include();
		includes.add(inc);

		return inc;
	}

	public static class Include extends org.jnitasks.types.Include {
		private boolean prepend = false;

		public void setPrepend(boolean prepend) {
			this.prepend = prepend;
		}

		public boolean isPrepend() {
			return this.prepend;
		}
	}

	@Override
	public void execute() throws BuildException {
		// Set the command to execute along with any required arguments.
		StringBuilder command = new StringBuilder("autoreconf --verbose");

		// Take care of the optional arguments.
		if (this.force) {
			command.append(" --force");
		}

		if (this.install) {
			command.append(" --install");
		}

		// Include arguments for nested Include.
		Iterator<Include> iterator = includes.iterator();
		while (iterator.hasNext()) {
			Include include = iterator.next();

			if (include.isValidOs()) {
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
}
