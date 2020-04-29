/* JNITasks: Ant tasks for JNI projects.
 * Copyright (C) 2013-2020 Alexander Barker.  All Rights Received.
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
package org.jnitasks.cmake;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.ExecTask;

public class BuildTask extends Task {
    private static final String cmd = "cmake";

    private int jobs = Runtime.getRuntime().availableProcessors();

    private File dir = null;

    private String config = null;
    private String target = null;

    private boolean clean = false;
    private boolean verbose = true;

    public void setJobs(String jobs) {
        if (jobs.equalsIgnoreCase("auto")) {
            this.jobs = Runtime.getRuntime().availableProcessors();
        } else {
            // FIXME else throw exception!
            this.jobs = Integer.parseInt(jobs);
        }
    }

    public void setDir(File dir) {
        this.dir = dir;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setClean(boolean clean) {
        this.clean = clean;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    @Override
    public void execute() throws BuildException {
        // Set the command to execute along with any required arguments.
        StringBuilder command = new StringBuilder(BuildTask.cmd);
        command
            .append(" --build")
            .append(" --parallel ")
            .append(jobs);

        if (this.config != null) {
            command
                .append(" --config ")
                .append(this.config);
        }

        if (this.target != null) {
            command
                .append(" --target ")
                .append(this.target);
        }

        if (this.clean) {
            command.append(" --clean-first");
        }

        if (!this.verbose) {
            command.append(" --verbose");
        }

        ExecTask shell = (ExecTask) getProject().createTask("exec");
        shell.setTaskName(this.getTaskName());

        shell.setDir(dir);
        shell.setExecutable(command.toString());
        shell.setFailonerror(true);

        shell.execute();
    }
}
