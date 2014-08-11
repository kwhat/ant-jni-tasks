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
package org.jnitasks.autotools;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Echo;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.types.Environment;

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
			command.append(" -j ").append(jobs);
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

		String cflags = getProject().getProperty("ant.build.native.cflags");
		if (cflags != null) {
			Environment.Variable env = new Environment.Variable();
			env.setKey("CFLAGS");
			env.setValue(cflags);

			shell.addEnv(env);
		}

		String cxxflags = getProject().getProperty("ant.build.native.cxxflags");
		if (cxxflags != null) {
			Environment.Variable env = new Environment.Variable();
			env.setKey("CXXFLAGS");
			env.setValue(cxxflags);

			shell.addEnv(env);
		}

		String ldflags = getProject().getProperty("ant.build.native.ldflags");
		if (ldflags != null) {
			Environment.Variable env = new Environment.Variable();
			env.setKey("LDFLAGS");
			env.setValue(ldflags);

			shell.addEnv(env);
		}

		String cc = getProject().getProperty("ant.build.native.compiler");
		if (cc != null) {
			Environment.Variable env = new Environment.Variable();
			env.setKey("CC");
			env.setValue(cc);

			shell.addEnv(env);
		}

		String ld = getProject().getProperty("ant.build.native.linker");
		if (ld != null) {
			Environment.Variable env = new Environment.Variable();
			env.setKey("LD");
			env.setValue(ld);

			shell.addEnv(env);
		}

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
