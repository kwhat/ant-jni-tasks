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
package com.github.kwhat.ant.jni.test;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.listener.SilentLogger;

public class TestLogger extends SilentLogger {
    private final StringBuffer messages = new StringBuffer();

    public TestLogger() {
        super();

        setMessageOutputLevel(Project.MSG_INFO);
        setOutputPrintStream(System.out);
        setErrorPrintStream(System.err);
    }

    @Override
    protected void log(String message) {
        if (messages.length() > 0) {
            messages.append("\n");
        }
        messages.append(message);
    }

    public String getAllMessages() {
        return messages.toString();
    }
}
