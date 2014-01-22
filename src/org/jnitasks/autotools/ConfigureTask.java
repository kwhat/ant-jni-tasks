package org.jnitasks.autotools;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Echo;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.types.Environment;
import org.jnitasks.types.AbstractFeature;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class ConfigureTask extends Task {
	private String build = null;
	private String host = null;
	private File dir = null;
	private File path = null;
	private File prefix = null;
	private List<AbstractFeature> flags = new Vector<AbstractFeature>();

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

	public void setBuild(String build) {
		this.build = build;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setDir(File dir) {
		this.dir = dir;
	}

	public void setPath(File path) {
		this.path = path;
	}

	public void setPrefix(File prefix) {
		this.prefix = prefix;
	}

	@Override
    public void execute() {
		// Set the command to execute along with any required arguments.
		StringBuilder command = new StringBuilder();

		// FIXME Replace the path and configure command with a variable
		// similar to <ant antfile="" />
		if (path != null) {
			command.append(path.getAbsolutePath().replace('\\', '/'));

			if (command.charAt(command.length() - 1) != '/') {
				command.append('/');
			}

			if (command.indexOf(" ") >= 0) {
				command.insert(0, '"').append('"');
			}
		}

		command.append("configure --verbose");

		if (this.build != null) {
			command.append(" --build=").append(this.build);
		}
		else if (getProject().getProperty("ant.build.native.build") != null) {
			command.append(" --build=").append(getProject().getProperty("ant.build.native.build"));
		}

		if (this.host != null) {
			command.append(" --host=").append(this.host);
		}
		else if (getProject().getProperty("ant.build.native.host") != null) {
			command.append(" --host=").append(getProject().getProperty("ant.build.native.host"));
		}

		// Take care of the optional arguments.
		if (this.prefix != null) {
			command.append(" --prefix=");
			// TODO Change to getCanonicalPath() when ready to deal with the io exception.
			// TODO Make sure the drive letter is lower case.
			String path = prefix.getAbsolutePath().replace('\\', '/');
			if (path.indexOf(" ") >= 0) {
				path = '"' + path + '"';
			}

			command.append(path);
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

		String cflags = getProject().getProperty("ant.build.native.cflags");
		if (cflags != null) {
			Environment.Variable env = new Environment.Variable();
			env.setKey("CFLAGS");
			env.setValue(cflags);

			shell.addEnv(env);
		}

		String cxxflags = getProject().getProperty("ant.build.native.cxxflags");
		if (cxxflags != null) {
			Environment.Variable env = new Environment.Variable();
			env.setKey("CXXFLAGS");
			env.setValue(cxxflags);

			shell.addEnv(env);
		}

		String ldflags = getProject().getProperty("ant.build.native.ldflags");
		if (ldflags != null) {
			Environment.Variable env = new Environment.Variable();
			env.setKey("LDFLAGS");
			env.setValue(ldflags);

			shell.addEnv(env);
		}

		String cc = getProject().getProperty("ant.build.native.compiler");
		if (cc != null) {
			Environment.Variable env = new Environment.Variable();
			env.setKey("CC");
			env.setValue(cc);

			shell.addEnv(env);
		}

		String ld = getProject().getProperty("ant.build.native.linker");
		if (ld != null) {
			Environment.Variable env = new Environment.Variable();
			env.setKey("LD");
			env.setValue(ld);

			shell.addEnv(env);
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
