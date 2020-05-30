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
package com.github.kwhat.ant.jni.pkgconfig;

import com.github.kwhat.ant.jni.CcTask;
import java.io.File;
import java.util.List;
import java.util.Vector;
import lombok.Setter;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Echo;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.types.DirSet;
import org.apache.tools.ant.types.Environment;

public class PkgConfigTask extends Task {
    private static final String cmd = "pkg-config";

    @Setter
    private String outputProperty = null;

    @Setter
    private String libraries = null;

    @Setter
    private boolean modVersion = false;

    @Setter
    private boolean quiet = false;

    @Setter
    private boolean cflags = false;

    @Setter
    private boolean libs = false;

    @Setter
    private boolean libsOnlyPath = false;

    @Setter
    private boolean libsOnlyLib = false;

    @Setter
    private boolean uninstalled = false;

    @Setter
    private String exists = null;

    private boolean linkStatic = false;

    private final List<PkgConfigTask.Variable> var = new Vector<>();
    private final List<Environment.Variable> env = new Vector<>();
    private final List<DirSet> dirsets = new Vector<>();



    public void setStatic(boolean linkStatic) {
        this.linkStatic = linkStatic;
    }

    public void createVariable(PkgConfigTask.Variable var) {
        this.var.add(var);
    }

    public void addEnv(Environment.Variable var) {
        this.env.add(var);
    }

    public void addDirset(DirSet dir) {
        dirsets.add(dir);
    }

    public PkgConfigTask.Variable createVariable() {
        PkgConfigTask.Variable variable = new PkgConfigTask.Variable();
        var.add(variable);

        return variable;
    }

    @Override
    public void execute() throws BuildException {
        // Set the command to execute along with any required arguments.
        StringBuilder command = new StringBuilder(PkgConfigTask.cmd);

        // TODO Check MSVC toolchain and set --msvc-syntax and possibly --dont-define-prefix

        if (modVersion) {
            command.append(" --modversion");
        }

        if (quiet) {
            command.append(" --silence-errors");
        } else {
            command.append(" --print-errors");
        }

        if (cflags) {
            command.append(" --cflags");
        }

        if (libs) {
            command.append(" --libs");
        }

        if (libsOnlyPath) {
            command.append(" --libs-only-L");
        }

        if (libsOnlyLib) {
            command.append(" --libs-only-l");
        }

        if (this.uninstalled) {
            command.append(" --uninstalled");
        }

        if (this.exists != null) {
            command.append(" --exists ").append(exists);
        }

        if (this.linkStatic) {
            command.append(" --static");
        }

        // Variable arguments for variable and defined-variable.
        for (Variable var : this.var) {
            if (var.getValue() == null) {
                command.append(" --variable=").append(var.getName());
            } else {
                command.append(" --variable=").append(var.getName())
                    .append('=').append(var.getValue());
            }
        }

        if (libraries != null) {
            command.append(' ').append(libraries);
        }

        // Add dirset to the env variable for PKG_CONFIG_PATH
        StringBuilder configPath = new StringBuilder();
        for (DirSet path : this.dirsets) {
            DirectoryScanner scanner = path.getDirectoryScanner();
            String[] dirs = scanner.getIncludedDirectories();
            for (String dir : dirs) {
                File basePath = scanner.getBasedir();

                // Check for previous data and add the platform dependent separator if needed.
                if (configPath.length() > 0) {
                    // If Windows use comma.
                    if (System.getProperty("os.name", "").toLowerCase().contains("win")) {
                        configPath.append(',');
                    } else {
                        configPath.append(';');
                    }
                }

                // Do not convert Windows dirsets to posix compatible paths.
                configPath.append(new File(basePath, dir).getAbsolutePath());
            }
        }

        // Create the required environment variables.
        if (configPath.length() > 0) {
            Environment.Variable envVar = new Environment.Variable();
            envVar.setKey("PKG_CONFIG_PATH");
            envVar.setValue(configPath.toString());
            env.add(envVar);
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

        // Environment.Variable arguments for nested env items.
        for (Environment.Variable variable : env) {
            shell.addEnv(variable);
        }

        shell.setFailonerror(true);
        shell.setOutputproperty(outputProperty);

        shell.setExecutable("sh");
        shell.createArg().setValue("-c");

        shell.createArg().setValue(command.toString());

        shell.execute();
    }

    public static class Variable extends CcTask.Define {
    }
}
