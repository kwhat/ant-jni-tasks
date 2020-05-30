/* JNITasks: Ant tasks for JNI projects.
 * Copyright (C) 2013-2018 Alexander Barker.  All Rights Received.
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
package com.github.kwhat.ant.jni.test.autotools;

import com.github.kwhat.ant.jni.autotools.AutoreconfTask;
import com.github.kwhat.ant.jni.test.TestLogger;
import java.io.File;
import org.apache.tools.ant.BuildFileRule;
import org.apache.tools.ant.Project;
import org.junit.Before;

public class ConfigureTaskTest {
    private File dir;
    
    private TestLogger logger;

    private AutoreconfTask task;

    @Before
    public void setUp() {
        dir = new File(System.getProperty("project.build.testOutputDirectory", "."));
        logger = new TestLogger();

        BuildFileRule buildRule = new BuildFileRule();
        buildRule.configureProject(System.getProperty("project.build.testOutputDirectory", ".") + File.separator + "build.xml");

        Project project = buildRule.getProject();
        project.addTaskDefinition("configure", ConfigureTaskTest.class);
        project.addBuildListener(logger);

        task = (AutoreconfTask) project.createTask("configure");
    }
}
