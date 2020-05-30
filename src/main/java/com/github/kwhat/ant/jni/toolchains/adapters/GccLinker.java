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
package com.github.kwhat.ant.jni.toolchains.adapters;

import com.github.kwhat.ant.jni.CcTask;
import com.github.kwhat.ant.jni.LdTask;
import com.github.kwhat.ant.jni.toolchains.LinkerAdapter;
import com.github.kwhat.ant.jni.types.AbstractFeature;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.tools.ant.DirectoryScanner;

public class GccLinker extends LinkerAdapter {
    protected String executable = "gcc";

    public GccLinker() {
        // Add any cflag env variables.
        String cflags = System.getenv("LDFLAGS");
        if (cflags != null && !cflags.isEmpty()) {
            CcTask.Argument arg = new CcTask.Argument();
            arg.setValue(cflags);
            addArg(arg);
        }
    }

    public Iterator<String> getArgs() {
        List<String> args = new ArrayList<>();

        for (AbstractFeature feat : features) {
            if (feat.isIfConditionValid() && feat.isUnlessConditionValid()) {
                if (feat instanceof LdTask.Argument) {
                    LdTask.Argument arg = (LdTask.Argument) feat;

                    args.add(arg.getValue());
                } else if (feat instanceof LdTask.Library) {
                    LdTask.Library lib = (LdTask.Library) feat;

                    if (lib.getPath() != null) {
                        String path = lib.getPath().replace('\\', '/');
                        if (path.contains(" ")) {
                            path = '"' + path.replaceAll("\"", "\\\"") + '"';
                        }

                        args.add("-L" + path);
                    }

                    if (lib.getLib() != null) {
                        args.add("-l" + lib.getLib());
                    }
                } else if (feat instanceof LdTask.FileSetArgument) {
                    LdTask.FileSetArgument arg = (LdTask.FileSetArgument) feat;

                    DirectoryScanner scanner = arg.getFileSet().getDirectoryScanner(getProject());
                    String[] files = scanner.getIncludedFiles();
                    for (String file : files) {
                        // Convert Windows paths to posix compatible paths.
                        String path = new File(scanner.getBasedir(), file).getPath()
                            .replace('\\', '/');
                        if (path.contains(" ")) {
                            path = '"' + path.replaceAll("\"", "\\\"") + '"';
                        }

                        args.add(path);
                    }
                }
            }
        }

        // Convert Windows paths to posix compatible paths.
        String outfile = this.getOutFile().getPath().replace('\\', '/');
        if (outfile.contains(" ")) {
            outfile = '"' + outfile.replaceAll("\"", "\\\"") + '"';
        }

        args.add("-o " + outfile);

        return args.iterator();
    }
}
