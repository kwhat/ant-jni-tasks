/* JNITasks: Ant tasks for JNI projects.
 * Copyright (C) 2013-2014 Alexander Barker.  All Rights Received.
 * https://github.com/kwhat/jnitasks/
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
package org.jnitasks.test;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildFileTest;
import org.apache.tools.ant.Project;
import org.jnitasks.autotools.AutoreconfTask;
import org.jnitasks.test.autotools.VerboseLogger;

import java.io.File;
import java.io.PrintWriter;

public class AutoreconfTest extends BuildFileTest {
	private VerboseLogger logger;
	
	public AutoreconfTest(String name) {
		super(name);
	}
	
	public void setUp() {
		configureProject("build.xml");
		
		getProject().addTaskDefinition("autoreconf", AutoreconfTask.class);
		
		logger = new VerboseLogger();
	}
	
	public void testAutoreconfNoConf() {
		Project p = getProject();
		p.addBuildListener(logger);
		
		AutoreconfTask autoreconf = (AutoreconfTask) p.createTask("autoreconf");
		
		try {
			autoreconf.execute();
			
			fail("Autoreconf succeeded without a config file!");
		}
		catch (Exception e) {
			// Do Nothing.
		}

		assertDebuglogContaining("autoreconf");
		assertDebuglogContaining("'configure.ac' or 'configure.in' is required");
	}
	
	public void testAutoreconfWithForce() {
		Project p = getProject();
		p.addBuildListener(logger);
		
		File bin = getBinDir();
		
		try {
			File conf = new File(bin + File.separator + "configure.ac");
			
			String name = p.getProperty("ant.project.name");
			PrintWriter writer = new PrintWriter(conf);
			writer.println("AC_INIT([" + name + "], [9999])");
			//writer.println("AC_CONFIG_AUX_DIR([config])");
			//writer.println("AC_REQUIRE_AUX_FILE([config.guess])");
			writer.close();
		}
		catch (Exception e) {
			throw new BuildException(e);
		}
		
		
		AutoreconfTask autoreconf = (AutoreconfTask) p.createTask("autoreconf");
		
		autoreconf.setDir(bin);
		autoreconf.setForce(true);
		autoreconf.setInstall(true);
		autoreconf.execute();

		assertLogContaining("autoreconf");
		assertLogContaining("Entering directory");
		
		// Check for force flag.
		assertLogContaining("--force");
		
		// Make sure configure script was generated.
		assertTrue(new File(bin + File.separator + "configure").exists());
		
		// Check for install flag.
		//assertTrue(new File(bin + File.separator + "aclocal.m4").exists());
		//assertTrue(new File(bin + File.separator + "config").exists());
	}
	
	private File getBinDir() {
		Project p = getProject();
		
		String bin = p.getProperty("dir.bin");
		if (bin == null) {
			bin = System.getProperty("java.io.tmpdir", null);
		}
		
		if (bin == null) {
			throw new BuildException("Cannot locate a suitable build directory.");
		}
		
		return new File(bin);
	}
}
