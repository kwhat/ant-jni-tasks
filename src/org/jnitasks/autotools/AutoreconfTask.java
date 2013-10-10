package org.jnitasks.autotools;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.ExecTask;
import java.io.File;

// TODO Add support for -I, -B  via TaskContainer and DirSet
public class AutoreconfTask extends Task {
	private boolean force = false;
	private boolean install = false;
	private File dir = null;
	
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

		// Create an exec task to run a shell.  Using the current shell to 
		// execute commands is required for Windows support.
		ExecTask shell = (ExecTask) getProject().createTask("exec");
		
		shell.setDir(dir);
		shell.setExecutable("sh");
		shell.setFailonerror(true);
		
		shell.createArg().setValue("-c");
		shell.createArg().setValue(command.toString());
		
		shell.execute();
    }

	public void setDir(File dir) {
		this.dir  = dir;
	}

	public void setForce(boolean force) {
		this.force = force;
	}

	public void setInstall(boolean install) {
		this.install = install;
	}
}
