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

import com.github.kwhat.ant.jni.types.AbstractFeature;
import java.io.File;
import java.util.List;
import java.util.Vector;
import lombok.Getter;
import lombok.Setter;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Echo;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.types.Environment;
import com.github.kwhat.ant.jni.toolchains.CompilerAdapter;
import com.github.kwhat.ant.jni.toolchains.ToolchainFactory;

public class ConfigureTask extends Task {
    @Setter
    private File dir = new File(".");

    @Setter
    private File script = new File("configure");


    // Configuration
    @Setter
    private boolean quiet = false;

    @Setter
    private File cache = null;

    @Setter
    private boolean create = true;

    @Setter
    private File src = null;


    // Installation directories
    @Setter
    private File prefix = null;

    @Setter
    private File execPrefix = null;


    // Fine tuning of the installation directories
    @Setter
    private File bin = null;

    @Setter
    private File sbin = null;

    @Setter
    private File libExec = null;

    @Setter
    private File sysConf = null;

    @Setter
    private File sharedState = null;

    @Setter
    private File localState = null;

    @Setter
    private File lib = null;

    @Setter
    private File include = null;

    @Setter
    private File oldInclude = null;

    @Setter
    private File dataRoot = null;

    @Setter
    private File data = null;

    @Setter
    private File info = null;

    @Setter
    private File locale = null;

    @Setter
    private File man = null;

    @Setter
    private File doc = null;

    @Setter
    private File html = null;

    @Setter
    private File dvi = null;

    @Setter
    private File pdf = null;

    @Setter
    private File ps = null;

    // Program names
    @Setter
    private String programPrefix = null;

    @Setter
    private String programSuffix = null;

    @Setter
    private String programTransformName = null;

    @Setter
    private String toolchain = null;

    private final List<ToggleFeature> flags = new Vector<>();

    private final List<Environment.Variable> env = new Vector<>();


    // Optional Features
    public Enable createEnable() {
        Enable feat = new Enable();
        flags.add(feat);

        return feat;
    }

    public Disable createDisable() {
        Disable feat = new Disable();
        flags.add(feat);

        return feat;
    }

    // Optional Packages
    public With createWith() {
        With feat = new With();
        flags.add(feat);

        return feat;
    }

    public Without createWithout() {
        Without feat = new Without();
        flags.add(feat);

        return feat;
    }

    // Environment Variables
    public void addEnv(Environment.Variable var) {
        env.add(var);
    }

    @Override
    public void execute() {
        // First, populate all of the properties we care about for this task.
        if (getProject().getProperty("ant.jni.toolchain") != null) {
            setToolchain(getProject().getProperty("ant.jni.toolchain"));
        }

        // Set the command to execute along with any required arguments.
        StringBuilder command = new StringBuilder();

        // FIXME Replace the path and configure command with a variable
        // similar to <ant antfile="" />
        command.append(script.getPath().replace('\\', '/'));

        // Quote the string if it contains a space.
        if (command.indexOf(" ") >= 0) {
            command.insert(0, '"').append('"');
        }

        if (quiet) {
            command.append(" --quiet");
        }

        if (cache != null) {
            command.append(" --cache-file=");

            String path = cache.getAbsolutePath().replace('\\', '/');
            if (path.contains(" ")) {
                path = '"' + path.replaceAll("\"", "\\\"") + '"';
            }

            command.append(path);
        }

        if (!create) {
            command.append(" --no-create");
        }

        if (src != null) {
            command.append(" --srcdir=");

            String path = src.getAbsolutePath().replace('\\', '/');
            if (path.contains(" ")) {
                path = '"' + path.replaceAll("\"", "\\\"") + '"';
            }

            command.append(path);
        }

        if (prefix != null) {
            command.append(" --prefix=");

            String path = prefix.getPath().replace('\\', '/');
            if (path.contains(" ")) {
                path = '"' + path.replaceAll("\"", "\\\"") + '"';
            }

            command.append(path);
        }

        if (execPrefix != null) {
            command.append(" --exec-prefix=");

            String path = execPrefix.getPath().replace('\\', '/');
            if (path.contains(" ")) {
                path = '"' + path.replaceAll("\"", "\\\"") + '"';
            }

            command.append(path);
        }

        if (bin != null) {
            command.append(" --bindir=");

            String path = bin.getAbsolutePath().replace('\\', '/');
            if (path.contains(" ")) {
                path = '"' + path.replaceAll("\"", "\\\"") + '"';
            }

            command.append(path);
        }

        if (sbin != null) {
            command.append(" --bindir=");

            String path = sbin.getAbsolutePath().replace('\\', '/');
            if (path.contains(" ")) {
                path = '"' + path.replaceAll("\"", "\\\"") + '"';
            }

            command.append(path);
        }

        if (libExec != null) {
            command.append(" --libexecdir=");

            String path = libExec.getAbsolutePath().replace('\\', '/');
            if (path.contains(" ")) {
                path = '"' + path.replaceAll("\"", "\\\"") + '"';
            }

            command.append(path);
        }

        if (sysConf != null) {
            command.append(" --sysconfdir=");

            String path = sysConf.getAbsolutePath().replace('\\', '/');
            if (path.contains(" ")) {
                path = '"' + path.replaceAll("\"", "\\\"") + '"';
            }

            command.append(path);
        }

        if (sharedState != null) {
            command.append(" --sharedstatedir=");

            String path = sharedState.getAbsolutePath().replace('\\', '/');
            if (path.contains(" ")) {
                path = '"' + path.replaceAll("\"", "\\\"") + '"';
            }

            command.append(path);
        }

        if (localState != null) {
            command.append(" --localstatedir=");

            String path = localState.getAbsolutePath().replace('\\', '/');
            if (path.contains(" ")) {
                path = '"' + path.replaceAll("\"", "\\\"") + '"';
            }

            command.append(path);
        }

        if (lib != null) {
            command.append(" --libdir=");

            String path = lib.getAbsolutePath().replace('\\', '/');
            if (path.contains(" ")) {
                path = '"' + path.replaceAll("\"", "\\\"") + '"';
            }

            command.append(path);
        }

        if (include != null) {
            command.append(" --includedir=");

            String path = include.getAbsolutePath().replace('\\', '/');
            if (path.contains(" ")) {
                path = '"' + path.replaceAll("\"", "\\\"") + '"';
            }

            command.append(path);
        }

        if (oldInclude != null) {
            command.append(" --oldincludedir=");

            String path = oldInclude.getAbsolutePath().replace('\\', '/');
            if (path.contains(" ")) {
                path = '"' + path.replaceAll("\"", "\\\"") + '"';
            }

            command.append(path);
        }

        if (dataRoot != null) {
            command.append(" --datarootdir=");

            String path = dataRoot.getAbsolutePath().replace('\\', '/');
            if (path.contains(" ")) {
                path = '"' + path.replaceAll("\"", "\\\"") + '"';
            }

            command.append(path);
        }

        if (data != null) {
            command.append(" --datadir=");

            String path = data.getAbsolutePath().replace('\\', '/');
            if (path.contains(" ")) {
                path = '"' + path.replaceAll("\"", "\\\"") + '"';
            }

            command.append(path);
        }

        if (info != null) {
            command.append(" --infodir=");

            String path = info.getAbsolutePath().replace('\\', '/');
            if (path.contains(" ")) {
                path = '"' + path.replaceAll("\"", "\\\"") + '"';
            }

            command.append(path);
        }

        if (locale != null) {
            command.append(" --localedir=");

            String path = locale.getAbsolutePath().replace('\\', '/');
            if (path.contains(" ")) {
                path = '"' + path.replaceAll("\"", "\\\"") + '"';
            }

            command.append(path);
        }

        if (man != null) {
            command.append(" --mandir=");

            String path = man.getAbsolutePath().replace('\\', '/');
            if (path.contains(" ")) {
                path = '"' + path.replaceAll("\"", "\\\"") + '"';
            }

            command.append(path);
        }

        if (doc != null) {
            command.append(" --docdir=");

            String path = doc.getAbsolutePath().replace('\\', '/');
            if (path.contains(" ")) {
                path = '"' + path.replaceAll("\"", "\\\"") + '"';
            }

            command.append(path);
        }

        if (html != null) {
            command.append(" --htmldir=");

            String path = html.getAbsolutePath().replace('\\', '/');
            if (path.contains(" ")) {
                path = '"' + path.replaceAll("\"", "\\\"") + '"';
            }

            command.append(path);
        }

        if (dvi != null) {
            command.append(" --dvidir=");

            String path = dvi.getAbsolutePath().replace('\\', '/');
            if (path.contains(" ")) {
                path = '"' + path.replaceAll("\"", "\\\"") + '"';
            }

            command.append(path);
        }

        if (pdf != null) {
            command.append(" --pdfdir=");

            String path = pdf.getAbsolutePath().replace('\\', '/');
            if (path.contains(" ")) {
                path = '"' + path.replaceAll("\"", "\\\"") + '"';
            }

            command.append(path);
        }

        if (ps != null) {
            command.append(" --psdir=");

            String path = ps.getAbsolutePath().replace('\\', '/');
            if (path.contains(" ")) {
                path = '"' + path.replaceAll("\"", "\\\"") + '"';
            }

            command.append(path);
        }

        if (programPrefix != null) {
            command.append(" --program-prefix=").append(programPrefix);
        }

        if (programSuffix != null) {
            command.append(" --program-suffix=").append(programSuffix);
        }

        if (programTransformName != null) {
            command.append(" --program-transform-name=").append(programTransformName);
        }


        if (toolchain != null) {
            CompilerAdapter compiler = ToolchainFactory.getCompiler(toolchain);

            String cc = System.getenv("CC");
            if (cc != null && !cc.isEmpty()) {
                compiler.setExecutable(cc);
            }

            Environment.Variable envCc = new Environment.Variable();
            envCc.setKey("CC");
            envCc.setValue(compiler.getExecutable());
            env.add(envCc);

            String cxx = System.getenv("CC");
            if (cxx != null && !cxx.isEmpty()) {
                compiler.setExecutable(cxx);
            }

            Environment.Variable envCxx = new Environment.Variable();
            envCxx.setKey("CXX");
            envCxx.setValue(compiler.getExecutable());
            env.add(envCxx);
        }

        // AbstractFeature arguments for nested enable/disable & with/without.
        for (AbstractFeature flag : flags) {
            if (flag.isIfConditionValid() && flag.isUnlessConditionValid()) {
                command.append(" ").append(flag.toString());
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

    private static class ToggleFeature extends AbstractFeature {
        @Getter
        @Setter
        private String flag;
    }

    public static class Enable extends ToggleFeature {
        public String toString() {
            return "--enable-" + getFlag();
        }
    }

    public static class Disable extends ToggleFeature {
        public String toString() {
            return "--disable-" + getFlag();
        }
    }

    public static class With extends ToggleFeature {
        public String toString() {
            return "--with-" + getFlag();
        }
    }

    public static class Without extends ToggleFeature {
        public String toString() {
            return "--without-" + getFlag();
        }
    }
}
