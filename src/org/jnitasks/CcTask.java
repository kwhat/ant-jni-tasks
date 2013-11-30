package org.jnitasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Echo;
import org.apache.tools.ant.taskdefs.Parallel;
import org.apache.tools.ant.taskdefs.Sequential;
import org.apache.tools.ant.types.FileSet;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;

public class CcTask extends Task {
	private Vector<FileSet> filesets = new Vector<FileSet>();
	private int jobs;
	private File objdir;

	public void execute() {
		Parallel parallel = (Parallel) this.getProject().createTask("parallel");
		parallel.setFailOnAny(true);
		parallel.setThreadCount(jobs);

		String foundLocation = null;

		Iterator<FileSet> iterator = filesets.iterator();
		while (iterator.hasNext()) {
			FileSet file = iterator.next();

			DirectoryScanner scanner = file.getDirectoryScanner(getProject());
			String[] files = scanner.getIncludedFiles();
			for(int i = 0; i < files.length; i++) {
				String filename = files[i].replace('\\','/');

				filename = filename.substring(filename.lastIndexOf("/") + 1);
				if (foundLocation == null && file.equals(filename)) {
					File base  = scanner.getBasedir();
					File found = new File(base, files[i]);
					foundLocation = found.getAbsolutePath();


					StringBuilder command = new StringBuilder();
					command.append(base).append(' ')
							.append(found).append(' ')
							.append(foundLocation).append(' ');

					Sequential sequential = (Sequential) this.getProject().createTask("sequential");

					// Print the executed command.
					Echo echo = (Echo) getProject().createTask("echo");
					echo.addText(command.toString());
					echo.setTaskName(this.getTaskName());
					sequential.addTask(echo);

					// Create an exec task to run a shell.  Using the current shell to
					// execute commands is required for Windows support.
					/*
					ExecTask shell = (ExecTask) this.getProject().createTask("exec");

					shell.setTaskName(this.getTaskName());

					//shell.setPath(dir);
					shell.setExecutable("sh");

					shell.setFailonerror(true);

					shell.createArg().setValue("-c");
					shell.createArg().setValue(command.toString());

					sequential.addTask(shell);

					parallel.addTask(sequential);
					*/
				}
			}
		}
	}

	public void addFileset(FileSet fileset) {
		filesets.add(fileset);
	}

	public void setToolchain(String toolchain) {
		try {
			Class<?> test = Class.forName("org.jnitasks.toolchains." + toolchain);
		}
		catch (ClassNotFoundException e) {
			throw new BuildException("The toolchain \"" + toolchain + "\" could not be found");
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
}
