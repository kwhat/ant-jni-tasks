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
package com.github.kwhat.ant.jni.autotools;

import com.github.kwhat.ant.jni.CcTask;
import java.io.File;
import java.util.List;
import java.util.Vector;
import lombok.Setter;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Echo;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.types.Environment;

public class MakeTask extends Task {
    private static final String cmd = "make";

    @Setter
    private File dir = new File(".");

    private Integer jobs = null;

    @Setter
    private boolean force = false;

    @Setter
    private String debug = null;

    @Setter
    private boolean override = false;

    @Setter
    private File makefile = null;

    @Setter
    private boolean ignoreErrors = false;

    @Setter
    private boolean keepGoing = false;

    @Setter
    private Float load = null;

    @Setter
    private boolean quiet = false;

    @Setter
    private boolean touch = false;

    @Setter
    private boolean printDirectory = false;

    @Setter
    private String target = null;

    private final Vector<MakeTask.Include> include = new Vector<>();
    private final List<Environment.Variable> env = new Vector<>();

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

    @SuppressWarnings("unused")
    public void addEnv(Environment.Variable var) {
        env.add(var);
    }

    @Override
    public void execute() {
        // Set the command to execute along with any required arguments.
        StringBuilder command = new StringBuilder();

        command.append(MakeTask.cmd);

        if (force) {
            command.append(" --always-make");
        }

        if (debug != null) {
            command.append(" --debug");

            if (debug.trim().length() > 0) {
                command.append("=").append(debug);
            }
        }

        if (override) {
            command.append(" --environment-overrides");
        }

        if (quiet) {
            command.append(" --quiet");
        }

        if (makefile != null) {
            command.append(" --makefile=");

            String path = makefile.getPath().replace('\\', '/');
            if (path.contains(" ")) {
                path = '"' + path.replaceAll("\"", "\\\"") + '"';
            }

            command.append(path);
        }

        if (ignoreErrors) {
            command.append(" --ignore-errors");
        }

        if (jobs != null) {
            command.append(" --jobs=").append(jobs);
        }

        if (keepGoing) {
            command.append(" --keep-going");
        }

        if (load != null) {
            command.append(" --load-average=").append(load);
        }

        if (quiet) {
            command.append(" --quiet");
        }

        if (touch) {
            command.append(" -touch");
        }

        if (printDirectory) {
            command.append(" --print-directory");
        }

        // Include arguments for nested Include.
        for (MakeTask.Include inc : include) {
            if (inc.isIfConditionValid() && inc.isUnlessConditionValid()) {
                command.append(" --include-dir=");

                String path = inc.getPath().replace('\\', '/');
                if (path.contains(" ")) {
                    path = '"' + path.replaceAll("\"", "\\\"") + '"';
                }

                command.append(path);
            }
        }

        if (target != null) {
            command.append(" ").append(target);
        }

        // Print the executed command.
        Echo echo = (Echo) getProject().createTask("echo");
        echo.addText(command.toString());
        echo.setTaskName(getTaskName());
        echo.execute();

        // Create an exec task to run a shell.  Using the current shell to
        // execute commands is required for Windows support.
        ExecTask shell = (ExecTask) getProject().createTask("exec");
        shell.setTaskName(getTaskName());

        // Environment.Variable arguments for nested env items.
        for (Environment.Variable var : env) {
            shell.addEnv(var);
        }

        shell.setDir(dir);
        shell.setFailonerror(true);

        shell.setExecutable("sh");
        shell.createArg().setValue("-c");

        shell.createArg().setValue(command.toString());

        shell.execute();
    }

    public static class Include extends CcTask.Include {
    }
}
