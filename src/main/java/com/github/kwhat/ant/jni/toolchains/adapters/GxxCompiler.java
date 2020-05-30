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

public class GxxCompiler extends GccCompiler {
    protected String executable = "g++";

    public GxxCompiler() {
        // Add any cflag env variables.
        String cflags = System.getenv("CXXFLAGS");
        if (cflags != null && !cflags.isEmpty()) {
            CcTask.Argument arg = new CcTask.Argument();
            arg.setValue(cflags);
            addArg(arg);
        }
    }
}
