/* JNITasks: Ant tasks for JNI projects.
 * Copyright (C) 2013-2020 Alexander Barker.  All Rights Received.
 * https://github.com/kwhat/ant-jni-tasks/
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
package com.github.kwhat.ant.jni.cmake;

import java.io.File;
import lombok.Setter;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Echo;
import org.apache.tools.ant.taskdefs.ExecTask;

public class BuildTask extends Task {
    private static final String cmd = "cmake";

    private Integer jobs = null;

    @Setter
    private File dir = new File(".");

    @Setter
    private String config = null;

    @Setter
    private String target = null;

    @Setter
    private boolean clean = false;

    @Setter
    private boolean verbose = true;

    @SuppressWarnings("unused")
    public void setJobs(String jobs) {
        if (jobs.equalsIgnoreCase("auto")) {
            this.jobs = Runtime.getRuntime().availableProcessors();
        } else {
            try {
                this.jobs = Integer.parseInt(jobs);
            } catch (NumberFormatException e) {
                throw new BuildException("Invalid property value for jobs.");
            }
        }
    }

    @Override
    public void execute() throws BuildException {
        // Set the command to execute along with any required arguments.
        StringBuilder command = new StringBuilder(BuildTask.cmd);
        command.append(" --build .");

        if (jobs != null) {
            command.append(" --parallel ").append(jobs);
        }

        if (config != null) {
            String path = config;
            if (path.contains(" ")) {
                path = '"' + path.replaceAll("\"", "\\\"") + '"';
            }
            
            command.append(" --config ").append(path);
        }

        if (target != null) {
            command.append(" --target ").append(target);
        }

        if (clean) {
            command.append(" --clean-first");
        }

        if (verbose) {
            command.append(" --verbose");
        }

        // Print the executed command.
        Echo echo = (Echo) getProject().createTask("echo");
        echo.addText(command.toString());
        echo.setTaskName(getTaskName());
        echo.execute();

        ExecTask shell = (ExecTask) getProject().createTask("exec");
        shell.setTaskName(getTaskName());

        shell.setDir(dir);
        shell.setFailonerror(true);

        if (System.getProperty("os.name").startsWith("Windows")) {
            shell.setExecutable("cmd");
            shell.createArg().setValue("/c");
        } else {
            shell.setExecutable("sh");
            shell.createArg().setValue("-c");
        }

        shell.createArg().setValue(command.toString());
        
        shell.execute();
    }
}
