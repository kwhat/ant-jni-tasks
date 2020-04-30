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
package org.jnitasks;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import lombok.Getter;
import lombok.Setter;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Echo;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.FileSet;
import org.jnitasks.toolchains.LinkerAdapter;
import org.jnitasks.toolchains.ToolchainFactory;
import org.jnitasks.types.AbstractFeature;

public class LdTask extends MatchingTask {
    @Setter
    private String toolchain = "gcc";

    @Setter
    private File outFile;

    @Setter
    private String host;

    private final List<AbstractFeature> features = new Vector<>();

    public void addFileset(FileSet fileset) {
        // Wrap FileSet to allow for argument order.
        LdTask.FileSetArgument arg = new LdTask.FileSetArgument();
        arg.setFileSet(fileset);

        features.add(arg);
    }

    public void addLibrary(Library library) {
        features.add(library);
    }

    public LdTask.Argument createArg() {
        LdTask.Argument arg = new LdTask.Argument();
        features.add(arg);

        return arg;
    }

    public void execute() {
        // Make sure we have all the required fields set.
        if (outFile == null) {
            throw new BuildException("The outFile attribute is required");
        }

        // First, populate all of the properties we care about for this task.
        if (getProject().getProperty("ant.build.native.toolchain") != null) {
            this.setToolchain(getProject().getProperty("ant.build.native.toolchain"));
        }

        if (getProject().getProperty("ant.build.native.host") != null) {
            this.setHost(getProject().getProperty("ant.build.native.host"));
        }

        // Setup the compiler.
        LinkerAdapter linker = ToolchainFactory.getLinker(toolchain);
        linker.setProject(getProject());
        linker.setOutFile(outFile);

        if (host != null && host.length() > 0) {
            // Prepend the host string to the executable.
            linker.setExecutable(host + '-' + linker.getExecutable());
        }

        for (AbstractFeature feat : features) {
            if (feat.isIfConditionValid() && feat.isUnlessConditionValid()) {
                linker.addArg(feat);
            }
        }

        long newest = 0;
        Iterator<File> inFile = linker.getInFiles();
        while (inFile.hasNext()) {
            long modified = inFile.next().lastModified();

            if (newest < modified) {
                newest = modified;
            }
        }

        if (newest >= outFile.lastModified()) {
            // Print the executed command.
            Echo echo = (Echo) getProject().createTask("echo");
            echo.setTaskName(this.getTaskName());
            echo.setAppend(true);

            // Create an exec task to run a shell.  Using the current shell to
            // execute commands is required for Windows support.
            ExecTask shell = (ExecTask) getProject().createTask("exec");
            shell.setTaskName(this.getTaskName());
            shell.setFailonerror(true);
            //shell.setDir(dir);

            echo.addText(linker.getExecutable());
            shell.setExecutable(linker.getExecutable());

            Iterator<String> args = linker.getArgs();
            while (args.hasNext()) {
                String arg = args.next();

                echo.addText(" " + arg);
                shell.createArg().setLine(arg);
            }

            echo.execute();
            shell.execute();
        }
    }

    public static class Argument extends AbstractFeature {
        @Getter
        @Setter
        private String value;
    }

    public static class Library extends AbstractFeature {
        @Getter
        @Setter
        private File path;

        @Getter
        @Setter
        private String lib;
    }

    public static class FileSetArgument extends AbstractFeature {
        @Getter
        @Setter
        private FileSet fileSet;
    }
}
