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
package org.jnitasks.cmake;

import java.io.File;
import java.util.List;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Echo;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.jnitasks.CcTask;
import org.jnitasks.types.AbstractFeature;

public class GenerateTask extends Task {
    private static final String cmd = "cmake";

    private File dir = null;
    private File src = null;
    private File cache = null;

    private boolean verbose = true;

    private List<GenerateTask.Define> defines = new Vector<>();

    @SuppressWarnings("unused")
    public void setSrc(File src) {
        if (!src.exists() || !src.isDirectory()) {
            throw new BuildException("Invalid source directory.");
        }

        this.src = src;
    }

    @SuppressWarnings("unused")
    public void setDir(File dir) {
        if (!dir.exists() && !dir.mkdir()) {
            throw new BuildException("Failed to create build directory.");
        } else if (!dir.isDirectory()) {
            throw new BuildException("Invalid build directory.");
        }

        this.dir = dir;
    }

    public void setCache(File cache) {
        if (!cache.exists() || !cache.isFile()) {
            throw new BuildException("Invalid cache file.");
        }

        this.cache = cache;
    }

    public GenerateTask.Define createDefine() {
        GenerateTask.Define define = new GenerateTask.Define();
        defines.add(define);

        return define;
    }

    @Override
    public void execute() throws BuildException {
        // Set the command to execute along with any required arguments.
        StringBuilder command = new StringBuilder(GenerateTask.cmd);

        if (this.src != null) {
            // TODO Change to getCanonicalPath() when ready to deal with the io exception.
            // TODO Make sure the drive letter is lower case.
            String tmpPath = src.getAbsolutePath().replace('\\', '/');
            if (tmpPath.contains(" ")) {
                tmpPath = '"' + tmpPath + '"';
            }

            command
                .append(" ")
                .append(tmpPath);
        }

        // Take care of the optional arguments.
        if (!this.verbose) {
            command.append(" --verbose");
        }

        // Include arguments for nested Include.
		for (GenerateTask.Define define : this.defines) {
			if (define.isIfConditionValid() && define.isUnlessConditionValid()) {
				command
                    .append(" ")
                    .append(define.getName());

				//if (define.getType())
			}
		}


        // Print the executed command.
        Echo echo = (Echo) getProject().createTask("echo");
        echo.addText(command.toString());
        echo.setTaskName(this.getTaskName());
        echo.execute();

        // Create an exec task to run a shell.  Using the current shell to
        // execute commands is required for Windows support.
        ExecTask shell = (ExecTask) getProject().createTask("exec");
        shell.setTaskName(this.getTaskName());

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
        
        private Type type;

        public void setType(Type type) {
            this.type = type;
        }

        public Type getType() {
            return this.type;
        }
    }

    public static class Undefine extends AbstractFeature implements Cloneable {
    }

    public static class Warning extends CcTask.Define {
    }
}
