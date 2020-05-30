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
package com.github.kwhat.ant.jni.toolchains;

import com.github.kwhat.ant.jni.types.AbstractFeature;
import java.io.File;
import java.util.Iterator;
import java.util.Vector;
import lombok.Getter;
import lombok.Setter;
import org.apache.tools.ant.ProjectComponent;

public abstract class CompilerAdapter extends ProjectComponent {
    @Getter
    @Setter
    protected String executable;

    @Getter
    private File inFile;

    @Getter
    @Setter
    private File outFile;;

    protected Vector<AbstractFeature> features = new Vector<>();


    public void addArg(AbstractFeature arg) {
        features.add(arg);
    }

    public void setInFile(File file) {
        inFile = file;
        if (outFile == null) {
            outFile = new File(
                file.getPath().substring(0, file.getPath().lastIndexOf('.')) + ".o");
        }
    }

    public abstract Iterator<String> getArgs();

    public static class Argument extends AbstractFeature {
        @Getter
        @Setter
        private String value;
    }
}
