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

import com.github.kwhat.ant.jni.autotools.AutoreconfTask.Include;
import java.io.File;
import org.apache.tools.ant.BuildFileRule;
import org.apache.tools.ant.Project;
import com.github.kwhat.ant.jni.autotools.AutoreconfTask;
import com.github.kwhat.ant.jni.test.TestLogger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AutoreconfTaskTest {
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
        project.addTaskDefinition("autoreconf", AutoreconfTask.class);
        project.addBuildListener(logger);

        task = (AutoreconfTask) project.createTask("autoreconf");
    }

    @Test
    public void testNoConf() {
        task.setVerbose(true);
        
        try {
            task.execute();

            Assert.fail("Autoreconf succeeded without a config file!");
        } catch (Exception e) {
            // Do Nothing.
        }

        String log = logger.getAllMessages();
        Assert.assertTrue(log.contains("autoreconf"));
        Assert.assertTrue(log.contains("'configure.ac' or 'configure.in' is required"));
    }

    @Test
    public void testWithForce() {
        task.setDir(dir);
        task.setForce(true);
        task.setVerbose(false);
        task.execute();

        String output = logger.getAllMessages();
        Assert.assertTrue(output.contains("autoreconf --force"));

        // Make sure configure script was generated.
        Assert.assertTrue(new File(dir + File.separator + "configure").exists());
    }

    @Test
    public void testWithInstall() {
        task.setDir(dir);
        task.setInstall(true);
        task.setVerbose(false);
        task.execute();

        Assert.assertTrue(logger.getAllMessages().contains("autoreconf --install"));

        // Make sure configure script was generated.
        File output = new File(dir + File.separator + "configure");
        Assert.assertTrue(output.exists());
        if (!output.delete()) {
            Assert.fail("Failed to remove generated configure");
        }
    }

    @Test
    public void testWithVerbose() {
        task.setDir(dir);
        task.setVerbose(true);
        task.execute();

        String log = logger.getAllMessages();
        Assert.assertTrue(log.contains("autoreconf --verbose"));
        Assert.assertTrue(log.contains("Entering directory"));

        // Make sure configure script was generated.
        File output = new File(dir + File.separator + "configure");
        Assert.assertTrue(output.exists());
        if (!output.delete()) {
            Assert.fail("Failed to remove generated configure");
        }
    }

    @Test
    public void testWithPrependInclude() {
        Include include = task.createInclude();
        include.setPath(dir.getPath() + File.separator + "include");
        include.setPrepend(true);

        task.setDir(dir);
        task.setVerbose(false);
        task.execute();

        String log = logger.getAllMessages();
        Assert.assertTrue(log.contains("autoreconf --prepend-include=" + dir.getPath() + File.separator + "include"));

        // Make sure configure script was generated.
        File output = new File(dir + File.separator + "configure");
        Assert.assertTrue(output.exists());
        if (!output.delete()) {
            Assert.fail("Failed to remove generated configure");
        }
    }
}
