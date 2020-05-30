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
import java.util.List;
import java.util.Vector;
import lombok.Getter;
import lombok.Setter;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Echo;
import org.apache.tools.ant.taskdefs.ExecTask;
import com.github.kwhat.ant.jni.CcTask;
import com.github.kwhat.ant.jni.toolchains.CompilerAdapter;
import com.github.kwhat.ant.jni.toolchains.ToolchainFactory;
import com.github.kwhat.ant.jni.types.AbstractFeature;

public class GenerateTask extends Task {
    private static final String cmd = "cmake";

    @Setter
    private File dir = null;

    @Setter
    private File src = null;

    @Setter
    private File cache = null;

    @Setter
    private boolean verbose = true;

    private final List<GenerateTask.Define> defines = new Vector<>();

    public GenerateTask.Define createDefine() {
        GenerateTask.Define define = new GenerateTask.Define();
        defines.add(define);

        return define;
    }

    @Override
    public void execute() throws BuildException {
        // Set the command to execute along with any required arguments.
        StringBuilder command = new StringBuilder(GenerateTask.cmd);

        // Take care of the optional arguments.
        if (verbose) {
            command.append(" --verbose");
        }

        // First, populate all of the properties we care about for this task.
        String toolchain = getProject().getProperty("ant.build.native.toolchain");
        if (toolchain != null) {
            // Setup the compiler.
            CompilerAdapter compiler = ToolchainFactory.getCompiler(toolchain);
            //this.createDefine();
            compiler.getExecutable();
        }

        // Include arguments for nested Include.
        for (GenerateTask.Define define : this.defines) {
            if (define.isIfConditionValid() && define.isUnlessConditionValid()) {
                command
                    .append(" -D")
                    .append(define.getName())
                    .append("=")
                    .append(define.getValue());
            }
        }

        if (dir != null) {
            // TODO Change to getCanonicalPath() when ready to deal with the io exception.
            // TODO Make sure the drive letter is lower case.
            String tmpPath = dir.getAbsolutePath().replace('\\', '/');
            //if (tmpPath.contains(" ")) {
                tmpPath = '"' + tmpPath + '"';
            //}

            command
                .append(" -B ")
                .append(tmpPath);
        }

        if (src != null) {
            // TODO Change to getCanonicalPath() when ready to deal with the io exception.
            // TODO Make sure the drive letter is lower case.
            String tmpPath = src.getAbsolutePath().replace('\\', '/');
            //if (tmpPath.contains(" ")) {
                tmpPath = '"' + tmpPath + '"';
            //}

            command
                .append(" -S ")
                .append(tmpPath);
        }

        // Print the executed command.
        Echo echo = (Echo) getProject().createTask("echo");
        echo.addText(command.toString());
        echo.setTaskName(getTaskName());
        echo.execute();

        // Create an exec task to run a shell.  Using the current shell to
        // execute commands is required for Windows support.
        ExecTask shell = (ExecTask) getProject().createTask("exec");

        shell.setTaskName(this.getTaskName());
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


    public static class Argument extends CcTask.Argument {
    }

    public static class Define extends CcTask.Define {
        public enum Type {
            BOOL,
            FILEPATH,
            PATH,
            STRING,
            INTERNAL
        }

        @Getter
        @Setter
        private Type type;
    }

    public static class Undefine extends AbstractFeature {
    }

    public static class Warning extends CcTask.Define {
    }
}
