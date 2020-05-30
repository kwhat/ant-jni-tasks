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
import com.github.kwhat.ant.jni.CcTask.Define;
import com.github.kwhat.ant.jni.CcTask.Include;
import com.github.kwhat.ant.jni.toolchains.CompilerAdapter;
import com.github.kwhat.ant.jni.types.AbstractFeature;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MscvCompiler extends CompilerAdapter {
    protected String executable = "cl";

    public MscvCompiler() {
        // Add any cflag env variables.
        String cflags = System.getenv("CFLAGS");
        if (cflags != null && !cflags.isEmpty()) {
            CcTask.Argument arg = new CcTask.Argument();
            arg.setValue(cflags);
            addArg(arg);
        }
    }

    public Iterator<String> getArgs() {
        List<String> args = new ArrayList<>();

        for (AbstractFeature feat : features) {
            if (feat instanceof Define) {
                Define def = (Define) feat;
                String macro = "/D" + def.getName();

                if (def.getValue() != null) {
                    macro += '=' + def.getValue();
                }
                args.add(macro);
            } else if (feat instanceof Include) {
                String path = ((Include) feat).getPath().replace('/', '\\');
                if (path.contains(" ")) {
                    path = '"' + path + '"';
                }

                args.add("/I" + path);
            } else if (feat instanceof CcTask.Argument) {
                CcTask.Argument arg = (CcTask.Argument) feat;

                args.add(arg.getValue());
            }
        }

        String infile = this.getInFile().getPath().replace('/', '\\');
        if (infile.contains(" ")) {
            infile = '"' + infile + '"';
        }

        String outfile = this.getOutFile().getPath().replace('/', '\\');
        if (outfile.contains(" ")) {
            outfile = '"' + outfile + '"';
        }

        args.add("-c " + infile);
        args.add("-o " + outfile);

        return args.iterator();
    }
}
