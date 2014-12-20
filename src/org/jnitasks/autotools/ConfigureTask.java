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
package org.jnitasks.autotools;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Echo;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.types.Environment;
import org.jnitasks.toolchains.CompilerAdapter;
import org.jnitasks.toolchains.ToolchainFactory;
import org.jnitasks.types.AbstractFeature;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class ConfigureTask extends Task {
	private File dir = null;
	private File script = new File("configure");

	// Configuration
	private boolean quiet = false;
	private File cache = null;
	private boolean create = true;
	private File src = null;

	// Installation directories
	private File prefix = null;
	private File exec_prefix = null;

	// Fine tuning of the installation directories
	private File bin = null;
	private File sbin = null;
	private File libexec = null;
	private File sysconf = null;
	private File sharedstate = null;
	private File localstate = null;
	private File lib = null;
	private File include = null;
	private File oldinclude = null;
	private File dataroot = null;
	private File data = null;
	private File info = null;
	private File locale = null;
	private File man = null;
	private File doc = null;
	private File html = null;
	private File dvi = null;
	private File pdf = null;
	private File ps = null;

	// Program names
	private String programPrefix = null;
	private String programSuffix = null;
	private String programTransformName = null;

	// System types
	private String toolchain = null;
	private String build = null;
	private String host = null;
	private String target = null;

	private List<AbstractFeature> flags = new Vector<AbstractFeature>();
	private List<Environment.Variable> env = new Vector<Environment.Variable>();

	public void setDir(File dir) {
		this.dir = dir;
	}
	public void setScript(File script) {
		this.script = script;
	}

	public void setQuiet(boolean quiet) {
		this.quiet = quiet;
	}
	public void setCache(File cache) {
		this.cache = cache;
	}
	public void setCreate(boolean create) {
		this.create = create;
	}
	public void setSrc(File src) {
		this.src = src;
	}

	public void setPrefix(File prefix) {
		this.prefix = prefix;
	}
	public void setExecprefix(File exec_prefix) {
		this.exec_prefix = exec_prefix;
	}

	public void setBin(File bin) {
		this.bin = bin;
	}
	public void setSbin(File sbin) {
		this.sbin = sbin;
	}
	public void setLibexec(File libexec) {
		this.libexec = libexec;
	}
	public void setSysconf(File sysconf) {
		this.sysconf = sysconf;
	}
	public void setSharedstate(File sharedstate) {
		this.sharedstate = sharedstate;
	}
	public void setLocalstate(File localstate) {
		this.localstate = localstate;
	}
	public void setLib(File lib) {
		this.lib = lib;
	}
	public void setInclude(File include) {
		this.include = include;
	}
	public void setOldinclude(File oldinclude) {
		this.oldinclude = oldinclude;
	}
	public void setDataroot(File dataroot) {
		this.dataroot = dataroot;
	}
	public void setData(File data) {
		this.data = data;
	}
	public void setInfo(File info) {
		this.info = info;
	}
	public void setLocale(File locale) {
		this.locale = locale;
	}
	public void setMan(File man) {
		this.man = man;
	}
	public void setDoc(File doc) {
		this.doc = doc;
	}
	public void setHtml(File html) {
		this.html = html;
	}
	public void setDvi(File dvi) {
		this.dvi = dvi;
	}
	public void setPdf(File pdf) {
		this.pdf = pdf;
	}
	public void setPs(File ps) {
		this.ps = ps;
	}

	public void setProgramprefix(String programPrefix) {
		this.programPrefix = programPrefix;
	}
	public void setProgramsuffix(String programSuffix) {
		this.programSuffix = programSuffix;
	}
	public void setProgramtransformname(String programTransformName) {
		this.programTransformName = programTransformName;
	}

	public void setToolchain(String toolchain) {
		this.toolchain = toolchain;
	}
	public void setBuild(String build) {
		this.build = build;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public void setTarget(String target) {
		this.target = target;
	}


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
		this.env.add(var);
	}

	@Override
    public void execute() {
		// First, populate all of the properties we care about for this task.
		if (getProject().getProperty("ant.build.native.toolchain") != null) {
			this.setToolchain(getProject().getProperty("ant.build.native.toolchain"));
		}

		if (getProject().getProperty("ant.build.native.build") != null) {
			this.setBuild(getProject().getProperty("ant.build.native.build"));
		}

		if (getProject().getProperty("ant.build.native.host") != null) {
			this.setHost(getProject().getProperty("ant.build.native.host"));
		}

		if (getProject().getProperty("ant.build.native.target") != null) {
			this.setTarget(getProject().getProperty("ant.build.native.target"));
		}


		// Set the command to execute along with any required arguments.
		StringBuilder command = new StringBuilder();

		// FIXME Replace the path and configure command with a variable
		// similar to <ant antfile="" />
		command.append(script.getAbsolutePath().replace('\\', '/'));

		// Quote the string if it contains a space.
		if (command.indexOf(" ") >= 0) {
			command.insert(0, '"').append('"');
		}

		if (this.quiet) {
			command.append(" --quiet");
		}

		if (this.cache != null) {
			command.append(" --cache-file=");
			// TODO Change to getCanonicalPath() when ready to deal with the io exception.
			// TODO Make sure the drive letter is lower case.
			String tmpPath = cache.getAbsolutePath().replace('\\', '/');
			if (tmpPath.indexOf(" ") >= 0) {
				tmpPath = '"' + tmpPath + '"';
			}

			command.append(tmpPath);
		}

		if (!this.create) {
			command.append(" --no-create");
		}

		if (this.src != null) {
			command.append(" --srcdir=");
			// TODO Change to getCanonicalPath() when ready to deal with the io exception.
			// TODO Make sure the drive letter is lower case.
			String tmpPath = cache.getAbsolutePath().replace('\\', '/');
			if (tmpPath.indexOf(" ") >= 0) {
				tmpPath = '"' + tmpPath + '"';
			}

			command.append(tmpPath);
		}


		if (this.prefix != null) {
			command.append(" --prefix=");
			// TODO Change to getCanonicalPath() when ready to deal with the io exception.
			// TODO Make sure the drive letter is lower case.
			String tmpPath = prefix.getAbsolutePath().replace('\\', '/');
			if (tmpPath.indexOf(" ") >= 0) {
				tmpPath = '"' + tmpPath + '"';
			}

			command.append(tmpPath);
		}

		if (this.exec_prefix != null) {
			command.append(" --exec-prefix=");
			// TODO Change to getCanonicalPath() when ready to deal with the io exception.
			// TODO Make sure the drive letter is lower case.
			String tmpPath = exec_prefix.getAbsolutePath().replace('\\', '/');
			if (tmpPath.indexOf(" ") >= 0) {
				tmpPath = '"' + tmpPath + '"';
			}

			command.append(tmpPath);
		}


		if (this.bin != null) {
			command.append(" --bindir=");
			// TODO Change to getCanonicalPath() when ready to deal with the io exception.
			// TODO Make sure the drive letter is lower case.
			String tmpPath = bin.getAbsolutePath().replace('\\', '/');
			if (tmpPath.indexOf(" ") >= 0) {
				tmpPath = '"' + tmpPath + '"';
			}

			command.append(tmpPath);
		}

		if (this.sbin != null) {
			command.append(" --bindir=");
			// TODO Change to getCanonicalPath() when ready to deal with the io exception.
			// TODO Make sure the drive letter is lower case.
			String tmpPath = sbin.getAbsolutePath().replace('\\', '/');
			if (tmpPath.indexOf(" ") >= 0) {
				tmpPath = '"' + tmpPath + '"';
			}

			command.append(tmpPath);
		}

		if (this.libexec != null) {
			command.append(" --libexecdir=");
			// TODO Change to getCanonicalPath() when ready to deal with the io exception.
			// TOD<envO Make sure the drive letter is lower case.
			String tmpPath = libexec.getAbsolutePath().replace('\\', '/');
			if (tmpPath.indexOf(" ") >= 0) {
				tmpPath = '"' + tmpPath + '"';
			}

			command.append(tmpPath);
		}

		if (this.sysconf != null) {
			command.append(" --sysconfdir=");
			// TODO Change to getCanonicalPath() when ready to deal with the io exception.
			// TODO Make sure the drive letter is lower case.
			String tmpPath = sysconf.getAbsolutePath().replace('\\', '/');
			if (tmpPath.indexOf(" ") >= 0) {
				tmpPath = '"' + tmpPath + '"';
			}

			command.append(tmpPath);
		}

		if (this.sharedstate != null) {
			command.append(" --sharedstatedir=");
			// TODO Change to getCanonicalPath() when ready to deal with the io exception.
			// TODO Make sure the drive letter is lower case.
			String tmpPath = sharedstate.getAbsolutePath().replace('\\', '/');
			if (tmpPath.indexOf(" ") >= 0) {
				tmpPath = '"' + tmpPath + '"';
			}

			command.append(tmpPath);
		}

		if (this.localstate != null) {
			command.append(" --localstatedir=");
			// TODO Change to getCanonicalPath() when ready to deal with the io exception.
			// TODO Make sure the drive letter is lower case.
			String tmpPath = localstate.getAbsolutePath().replace('\\', '/');
			if (tmpPath.indexOf(" ") >= 0) {
				tmpPath = '"' + tmpPath + '"';
			}

			command.append(tmpPath);
		}

		if (this.lib != null) {
			command.append(" --libdir=");
			// TODO Change to getCanonicalPath() when ready to deal with the io exception.
			// TODO Make sure the drive letter is lower case.
			String tmpPath = lib.getAbsolutePath().replace('\\', '/');
			if (tmpPath.indexOf(" ") >= 0) {
				tmpPath = '"' + tmpPath + '"';
			}

			command.append(tmpPath);
		}

		if (this.include != null) {
			command.append(" --includedir=");
			// TODO Change to getCanonicalPath() when ready to deal with the io exception.
			// TODO Make sure the drive letter is lower case.
			String tmpPath = include.getAbsolutePath().replace('\\', '/');
			if (tmpPath.indexOf(" ") >= 0) {
				tmpPath = '"' + tmpPath + '"';
			}

			command.append(tmpPath);
		}

		if (this.oldinclude != null) {
			command.append(" --oldincludedir=");
			// TODO Change to getCanonicalPath() when ready to deal with the io exception.
			// TODO Make sure the drive letter is lower case.
			String tmpPath = oldinclude.getAbsolutePath().replace('\\', '/');
			if (tmpPath.indexOf(" ") >= 0) {
				tmpPath = '"' + tmpPath + '"';
			}

			command.append(tmpPath);
		}

		if (this.dataroot != null) {
			command.append(" --datarootdir=");
			// TODO Change to getCanonicalPath() when ready to deal with the io exception.
			// TODO Make sure the drive letter is lower case.
			String tmpPath = dataroot.getAbsolutePath().replace('\\', '/');
			if (tmpPath.indexOf(" ") >= 0) {
				tmpPath = '"' + tmpPath + '"';
			}

			command.append(tmpPath);
		}

		if (this.info != null) {
			command.append(" --infodir=");
			// TODO Change to getCanonicalPath() when ready to deal with the io exception.
			// TODO Make sure the drive letter is lower case.
			String tmpPath = info.getAbsolutePath().replace('\\', '/');
			if (tmpPath.indexOf(" ") >= 0) {
				tmpPath = '"' + tmpPath + '"';
			}

			command.append(tmpPath);
		}

		if (this.locale != null) {
			command.append(" --localedir=");
			// TODO Change to getCanonicalPath() when ready to deal with the io exception.
			// TODO Make sure the drive letter is lower case.
			String tmpPath = locale.getAbsolutePath().replace('\\', '/');
			if (tmpPath.indexOf(" ") >= 0) {
				tmpPath = '"' + tmpPath + '"';
			}

			command.append(tmpPath);
		}

		if (this.man != null) {
			command.append(" --mandir=");
			// TODO Change to getCanonicalPath() when ready to deal with the io exception.
			// TODO Make sure the drive letter is lower case.
			String tmpPath = man.getAbsolutePath().replace('\\', '/');
			if (tmpPath.indexOf(" ") >= 0) {
				tmpPath = '"' + tmpPath + '"';
			}

			command.append(tmpPath);
		}

		if (this.doc != null) {
			command.append(" --docdir=");
			// TODO Change to getCanonicalPath() when ready to deal with the io exception.
			// TODO Make sure the drive letter is lower case.
			String tmpPath = doc.getAbsolutePath().replace('\\', '/');
			if (tmpPath.indexOf(" ") >= 0) {
				tmpPath = '"' + tmpPath + '"';
			}

			command.append(tmpPath);
		}

		if (this.html != null) {
			command.append(" --htmldir=");
			// TODO Change to getCanonicalPath() when ready to deal with the io exception.
			// TODO Make sure the drive letter is lower case.
			String tmpPath = html.getAbsolutePath().replace('\\', '/');
			if (tmpPath.indexOf(" ") >= 0) {
				tmpPath = '"' + tmpPath + '"';
			}

			command.append(tmpPath);
		}

		if (this.dvi != null) {
			command.append(" --dvidir=");
			// TODO Change to getCanonicalPath() when ready to deal with the io exception.
			// TODO Make sure the drive letter is lower case.
			String tmpPath = dvi.getAbsolutePath().replace('\\', '/');
			if (tmpPath.indexOf(" ") >= 0) {
				tmpPath = '"' + tmpPath + '"';
			}

			command.append(tmpPath);
		}

		if (this.pdf != null) {
			command.append(" --pdfdir=");
			// TODO Change to getCanonicalPath() when ready to deal with the io exception.
			// TODO Make sure the drive letter is lower case.
			String tmpPath = pdf.getAbsolutePath().replace('\\', '/');
			if (tmpPath.indexOf(" ") >= 0) {
				tmpPath = '"' + tmpPath + '"';
			}

			command.append(tmpPath);
		}

		if (this.ps != null) {
			command.append(" --psdir=");
			// TODO Change to getCanonicalPath() when ready to deal with the io exception.
			// TODO Make sure the drive letter is lower case.
			String tmpPath = ps.getAbsolutePath().replace('\\', '/');
			if (tmpPath.indexOf(" ") >= 0) {
				tmpPath = '"' + tmpPath + '"';
			}

			command.append(tmpPath);
		}


		if (this.programPrefix != null) {
			command.append(" --program-prefix=").append(this.programPrefix);
		}

		if (this.programSuffix != null) {
			command.append(" --program-suffix=").append(this.programSuffix);
		}

		if (this.programTransformName != null) {
			command.append(" --program-transform-name=").append(this.programTransformName);
		}

		if (this.toolchain != null) {
			CompilerAdapter compiler = ToolchainFactory.getCompiler(this.toolchain);
			if (host != null && host.length() > 0) {
				// Prepend the host string to the executable.
				compiler.setExecutable(host + '-' + compiler.getExecutable());
			}

			Environment.Variable cc = new Environment.Variable();
			cc.setKey("CC");
			cc.setValue(compiler.getExecutable());
			env.add(cc);

			Environment.Variable cxx = new Environment.Variable();
			cxx.setKey("CXX");
			cxx.setValue(compiler.getExecutable());
			env.add(cxx);
		}

		if (this.build != null) {
			command.append(" --build=").append(this.build);
		}

		if (this.host != null) {
			command.append(" --host=").append(this.host);
		}

		if (this.target != null) {
			command.append(" --target=").append(this.target);
		}

		// AbstractFeature arguments for nested enable/disable & with/without.
		Iterator<AbstractFeature> iterator = flags.iterator();
		while (iterator.hasNext()) {
			AbstractFeature feature = iterator.next();

			if (feature.isIfConditionValid() && feature.isUnlessConditionValid()) {
				if (feature instanceof Enable) {
					command.append(" --enable-");
				}
				else if (feature instanceof Disable) {
					command.append(" --disable-");
				}
				else if (feature instanceof With) {
					command.append(" --with-");
				}
				else if (feature instanceof Without) {
					command.append(" --without-");
				}

				command.append(((ToggleFeature) feature).getFlag());
			}
		}


		// Print the executed command.
		Echo echo = (Echo) getProject().createTask("echo");
		echo.addText(command.toString());
		echo.setTaskName(this.getTaskName());
		echo.execute();

		// Create an exec task to run a shell.  Using the current shell to 
		// execute commands is required for Windows support.
		ExecTask shell = (ExecTask) this.getProject().createTask("exec");

		shell.setTaskName(this.getTaskName());

		// Environment.Variable arguments for nested env items.
		Iterator<Environment.Variable> envItems = env.iterator();
		while (envItems.hasNext()) {
			shell.addEnv(envItems.next());
		}

		shell.setDir(dir);
		shell.setExecutable("sh");

		shell.setFailonerror(true);

		shell.createArg().setValue("-c");
		shell.createArg().setValue(command.toString());

		shell.execute();
    }

	private static class ToggleFeature extends AbstractFeature {
		private String flag;

		public void addText(String flag) {
			this.flag = flag;
		}

		public void setFlag(String flag) {
			this.flag = flag;
		}

		public String getFlag() {
			return this.flag;
		}
	}

	public static class Enable extends ToggleFeature { }

	public static class Disable extends ToggleFeature { }

	public static class With extends ToggleFeature { }

	public static class Without extends ToggleFeature { }
}
