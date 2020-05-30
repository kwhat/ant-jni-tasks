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
import lombok.Getter;
import lombok.Setter;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Echo;
import org.apache.tools.ant.taskdefs.ExecTask;

public class AutoreconfTask extends Task {
    private static final String cmd = "autoreconf";

    @Setter
    private File dir = new File(".");

    @Setter
    private boolean force = false;

    @Setter
    private boolean install = false;

    @Setter
    private boolean verbose = true;

    private final List<AutoreconfTask.Include> include = new Vector<>();

    @SuppressWarnings("unused")
    public AutoreconfTask.Include createInclude() {
        Include inc = new AutoreconfTask.Include();
        include.add(inc);

        return inc;
    }

    @Override
    public void execute() throws BuildException {
        // Set the command to execute along with any required arguments.
        StringBuilder command = new StringBuilder(AutoreconfTask.cmd);

        // Take care of the optional arguments.
        if (verbose) {
            command.append(" --verbose");
        }

        if (force) {
            command.append(" --force");
        }

        if (install) {
            command.append(" --install");
        }

        // Include arguments for nested Include.
        for (AutoreconfTask.Include inc : include) {
            if (inc.isIfConditionValid() && inc.isUnlessConditionValid()) {
                command.append(" ");
                if (inc.isPrepend()) {
                    command.append("--prepend-include=");
                } else {
                    command.append("--include=");
                }

                String path = inc.getPath().replace('\\', '/');
                if (path.contains(" ")) {
                    path = '"' + path.replaceAll("\"", "\\\"") + '"';
                }

                command.append(path);
            }
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

        shell.setDir(dir);
        shell.setFailonerror(true);

        shell.setExecutable("sh");
        shell.createArg().setValue("-c");

        shell.createArg().setValue(command.toString());

        shell.execute();
    }

    public static class Include extends CcTask.Include {
        @Getter
        @Setter
        private boolean prepend = false;
    }
}
