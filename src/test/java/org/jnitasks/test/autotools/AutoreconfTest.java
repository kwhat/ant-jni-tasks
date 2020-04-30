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
package org.jnitasks.test.autotools;

import java.io.File;
import org.apache.tools.ant.BuildFileRule;
import org.apache.tools.ant.Project;
import org.jnitasks.autotools.AutoreconfTask;
import org.jnitasks.test.TestLogger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AutoreconfTest {
    private TestLogger logger;

    private Project project;

    @Before
    public void setUp() {
        logger = new TestLogger();

        BuildFileRule buildRule = new BuildFileRule();
        buildRule.configureProject(System.getProperty("project.build.testOutputDirectory", ".") + File.separator + "build.xml");

        project = buildRule.getProject();
        project.addTaskDefinition("autoreconf", AutoreconfTask.class);
        project.addBuildListener(logger);
    }

    @Test
    public void testAutoreconfNoConf() {
        AutoreconfTask autoreconf = (AutoreconfTask) project.createTask("autoreconf");

        try {
            autoreconf.execute();

            Assert.fail("Autoreconf succeeded without a config file!");
        } catch (Exception e) {
            // Do Nothing.
        }

        String output = logger.getAllMessages();
        Assert.assertTrue(output.contains("autoreconf"));
        Assert.assertTrue(output.contains("'configure.ac' or 'configure.in' is required"));
    }

    @Test
    public void testAutoreconfWithForce() {
        File resourceRoot = new File(System.getProperty("project.build.testOutputDirectory", "."));

        AutoreconfTask autoreconf = (AutoreconfTask) project.createTask("autoreconf");

        autoreconf.setDir(resourceRoot);
        autoreconf.setForce(true);
        autoreconf.setInstall(true);
        autoreconf.execute();

        String output = logger.getAllMessages();
        Assert.assertTrue(output.contains("autoreconf"));
        Assert.assertTrue(output.contains("Entering directory"));

        // Check for force flag.
        Assert.assertTrue(output.contains("--force"));

        // Make sure configure script was generated.
        Assert.assertTrue(new File(resourceRoot + File.separator + "configure").exists());

        // Check for install flag.
        //assertTrue(new File(bin + File.separator + "aclocal.m4").exists());
        //assertTrue(new File(bin + File.separator + "config").exists());
    }

}
