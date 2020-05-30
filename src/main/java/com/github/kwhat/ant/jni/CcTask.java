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
package com.github.kwhat.ant.jni;

import com.github.kwhat.ant.jni.types.AbstractFeature;
import java.io.File;
import java.util.Iterator;
import java.util.Vector;
import lombok.Getter;
import lombok.Setter;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Echo;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.taskdefs.Parallel;
import org.apache.tools.ant.taskdefs.Sequential;
import org.apache.tools.ant.types.FileSet;
import com.github.kwhat.ant.jni.toolchains.CompilerAdapter;
import com.github.kwhat.ant.jni.toolchains.ToolchainFactory;

public class CcTask extends Task {
    private int jobs = Runtime.getRuntime().availableProcessors();
    private File objdir = null;

    @Setter
    private String toolchain = null;

    @Setter
    private String executable = null;

    private final Vector<FileSet> filesets = new Vector<>();
    private final Vector<AbstractFeature> features = new Vector<>();

    @SuppressWarnings("unused")
    public CcTask.Argument createArg() {
        CcTask.Argument arg = new CcTask.Argument();
        features.add(arg);

        return arg;
    }

    @SuppressWarnings("unused")
    public CcTask.Define createDefine() {
        CcTask.Define macro = new CcTask.Define();
        features.add(macro);

        return macro;
    }

    @SuppressWarnings("unused")
    public CcTask.Include createInclude() {
        CcTask.Include inc = new CcTask.Include();
        features.add(inc);

        return inc;
    }

    @SuppressWarnings("unused")
    public void addFileset(FileSet fileset) {
        filesets.add(fileset);
    }

    @SuppressWarnings("unused")
    public void setObjdir(File objdir) {
        if (!objdir.exists() || !objdir.isDirectory()) {
            throw new BuildException("Invalid object directory.");
        }

        this.objdir = objdir;
    }

    @SuppressWarnings("unused")
    public void setJobs(String jobs) {
        if (jobs.equalsIgnoreCase("auto")) {
            this.jobs = Runtime.getRuntime().availableProcessors();
        } else {
            // FIXME else throw exception!
            this.jobs = Integer.parseInt(jobs);
        }
    }

    public void execute() {
        if (getProject().getProperty("ant.jni.toolchain") != null) {
            setToolchain(getProject().getProperty("ant.jni.toolchain"));
        }

        // Setup the compiler.
        CompilerAdapter compiler = ToolchainFactory.getCompiler(toolchain);
        compiler.setProject(getProject());

        String cc = System.getenv("CC");
        if (cc != null && !cc.isEmpty()) {
            compiler.setExecutable(cc);
        } else if (executable != null && !executable.isEmpty()) {
            // Prepend the host string to the executable.
            compiler.setExecutable(executable);
        }

        for (AbstractFeature feat : features) {
            if (feat.isIfConditionValid() && feat.isUnlessConditionValid()) {
                compiler.addArg(feat);
            }
        }

        // Create a parallel task to try and run the compiler command in parallel.
        Parallel parallel = (Parallel) this.getProject().createTask("parallel");
        parallel.setFailOnAny(true);
        parallel.setThreadCount(jobs);

        for (FileSet file : filesets) {
            DirectoryScanner scanner = file.getDirectoryScanner(getProject());
            String[] files = scanner.getIncludedFiles();
            for (String s : files) {
                File basePath = scanner.getBasedir();
                File inFile = new File(basePath, s);
                compiler.setInFile(inFile);

                File outFile;
                if (objdir != null) {
                    // If the objdir is set, use that for output.
                    outFile = new File(objdir, s.substring(0, s.lastIndexOf('.')) + ".o");

                    compiler.setOutFile(outFile);
                } else {
                    outFile = compiler.getOutFile();
                }

                // Check to see if the source file has been modified more recently than the object file.
                if (inFile.lastModified() >= outFile.lastModified()) {
                    Sequential sequential = (Sequential) this.getProject().createTask("sequential");

                    // Print the executed command.
                    Echo echo = (Echo) getProject().createTask("echo");
                    echo.setTaskName(this.getTaskName());
                    echo.setAppend(true);

                    // Create an exec task to run a shell.  Using the current shell to
                    // execute commands is required for Windows support.

                    ExecTask shell = (ExecTask) getProject().createTask("exec");
                    shell.setTaskName(getTaskName());
                    shell.setFailonerror(true);

                    echo.addText(compiler.getExecutable());
                    shell.setExecutable(compiler.getExecutable());

                    Iterator<String> args = compiler.getArgs();
                    while (args.hasNext()) {
                        String arg = args.next();

                        echo.addText(" " + arg);
                        shell.createArg().setLine(arg);
                    }

                    sequential.addTask(echo);
                    sequential.addTask(shell);

                    // Add the sequential task containing echo and cc shell command to the parallel task.
                    parallel.addTask(sequential);
                }
            }
        }

        // Execute the compile.
        parallel.execute();
    }

    public static class Argument extends AbstractFeature {
        @Getter
        @Setter
        private String value;

        @SuppressWarnings("unused")
        public void setText(String value) {
            this.value = value;
        }
    }

    public static class Include extends AbstractFeature {
        @Getter
        @Setter
        private String path = ".";

        @SuppressWarnings("unused")
        public void setText(String path) {
            this.path = path;
        }
    }

    public static class Define extends AbstractFeature {
        @Getter
        @Setter
        private String name;

        @Getter
        @Setter
        private String value;

        @SuppressWarnings("unused")
        public void setText(String value) {
            this.value = value;
        }
    }
}
