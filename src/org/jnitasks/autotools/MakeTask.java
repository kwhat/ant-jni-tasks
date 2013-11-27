package org.jnitasks.autotools;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Echo;
import org.apache.tools.ant.taskdefs.ExecTask;

import java.io.File;

public class MakeTask extends Task {
	private File dir;
	private Integer jobs;
	private String target;

	@Override
    public void execute() {
		// Set the command to execute along with any required arguments.
		StringBuilder command = new StringBuilder();

		command.append("make");

		// Take care of the optional arguments.
		if (jobs != null) {
			command.append(" -j").append(jobs);
		}

		if (target != null) {
			command.append(" ").append(target);
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

		shell.setDir(dir);
		shell.setExecutable("sh");

		shell.setFailonerror(true);

		shell.createArg().setValue("-c");
		shell.createArg().setValue(command.toString());

		shell.execute();
    }

	public void setDir(File dir) {
		this.dir = dir;
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

	public void setTarget(String target) {
		this.target = target.replaceAll(",", " ");
	}
}
