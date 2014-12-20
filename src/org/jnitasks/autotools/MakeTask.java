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
import org.apache.tools.ant.types.Environment;
import org.jnitasks.types.AbstractFeature;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class MakeTask extends Task {
	private static final String cmd = "make";

	private boolean force = false;
	private File dir = null;
	private String debug = null;
	private boolean override = false;
	private File makefile = null;
	private boolean ignoreErrors = false;
	private Integer jobs = null;
	private boolean keepGoing = false;
	private Float load = null;
	private boolean quiet = false;
	private boolean touch = false;
	private boolean printDirectory = false;
	private String target = null;

	private Vector<MakeTask.Include> features = new Vector<MakeTask.Include>();
	private List<Environment.Variable> env = new Vector<Environment.Variable>();

	public void setForce(boolean force) {
		this.force = force;
	}
	public void setDir(File dir) {
		this.dir = dir;
	}
	public void setDebug(String debug) {
		this.debug = debug;
	}
	public void setOverride(boolean override) {
		this.override = override;
	}
	public void setMakefile(File makefile) {
		this.makefile = makefile;
	}
	public void setIgnoreerrors(boolean ignoreErrors) {
		this.ignoreErrors = ignoreErrors;
	}
	public void setJobs(String jobs) {
		if (jobs.equalsIgnoreCase("auto")) {
			this.jobs = Runtime.getRuntime().availableProcessors();
		}
		else {
			try {
				this.jobs = Integer.parseInt(jobs);
			}
			catch (NumberFormatException e) {
				throw new BuildException("Invalid property value for jobs.");
			}
		}
	}
	public void setKeepgoing(boolean keepGoing) {
		this.keepGoing = keepGoing;
	}
	public void setLoad(float jobs) {
		this.load = new Float(jobs);
	}
	public void setQuiet(boolean quiet) {
		this.quiet = quiet;
	}
	public void setTouch(boolean touch) {
		this.touch = touch;
	}
	public void setPrintdirectory(boolean printDirectory) {
		this.printDirectory = printDirectory;
	}
	public void setTarget(String target) {
		this.target = target.replaceAll(",", " ");
	}

	public void addEnv(Environment.Variable var) {
		this.env.add(var);
	}

	@Override
    public void execute() {
		// Set the command to execute along with any required arguments.
		StringBuilder command = new StringBuilder();

		command.append(MakeTask.cmd);

		if (force) {
			command.append(" -B");
		}

		if (debug != null) {
			if (debug.trim().length() > 0) {
				command.append(" --debug=").append(debug);
			}
			else {
				command.append(" -d");
			}
		}

		if (override) {
			command.append(" -e");
		}

		if (quiet) {
			command.append(" -s");
		}

		if (makefile != null) {
			command.append(" -f ");
			// TODO Change to getCanonicalPath() when ready to deal with the io exception.
			// TODO Make sure the drive letter is lower case.
			String tmpPath = makefile.getAbsolutePath().replace('\\', '/');
			if (tmpPath.indexOf(" ") >= 0) {
				tmpPath = '"' + tmpPath + '"';
			}

			command.append(tmpPath);
		}

		if (ignoreErrors) {
			command.append(" -i");
		}

		if (jobs != null) {
			command.append(" -j ").append(jobs);
		}

		if (keepGoing) {
			command.append(" -k");
		}

		if (load != null) {
			command.append(" -l ").append(load);
		}

		if (quiet) {
			command.append(" -s");
		}

		if (touch) {
			command.append(" -t");
		}

		if (printDirectory) {
			command.append(" -w");
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
}
