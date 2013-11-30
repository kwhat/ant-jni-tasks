package org.jnitasks.autotools;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Echo;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.jnitasks.autotools.types.Enable;
import org.jnitasks.autotools.types.With;
import org.jnitasks.types.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConfigureTask extends Task {
	private File dir;
	private File path;
	private File prefix = null;
	private List<ToggleFeature> flags = new ArrayList<ToggleFeature>();


	public void addEnable(Enable e) {
		flags.add(e);
	}

	public void addWith(With e) {
		flags.add(e);
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

	private String getUnixPath(File path) {
		String unixPath = null;

		try {
			unixPath = path.getCanonicalPath();
		}
		catch (IOException e) {
			unixPath = path.getAbsolutePath();
		}

		unixPath = unixPath.replaceAll(File.separator, "/");

		if (unixPath.charAt(unixPath.length() - 1) != '/') {
			unixPath += '/';
		}

		return unixPath;
	}

	@Override
    public void execute() {
		// Set the command to execute along with any required arguments.
		StringBuilder command = new StringBuilder();

		// FIXME Replace the path and configure command with a variable
		// similar to <ant antfile="" />
		if (path != null) {
			command.append(getUnixPath(path));
		}

		command.append("configure --verbose");

		// Take care of the optional arguments.
		if (this.prefix != null) {
			command.append(" --prefix=");
			// TODO Change to getCanonicalPath() when ready to deal with the io exception.
			// TODO Make sure the drive letter is lower case.
			command.append(getUnixPath(prefix));
		}

		// Include arguments for nested Include.
		Iterator<ToggleFeature> iterator = flags.iterator();
		while (iterator.hasNext()) {
			ToggleFeature feature = iterator.next();

			if (feature.isValidOs()) {
				if (feature instanceof With) {
					if (!feature.isNegated()) {
						command.append(" --with-").append(((With) feature).getFlag());
					}
					else {
						command.append(" --without-").append(((With) feature).getFlag());
					}
				}
				else if (feature instanceof Enable) {
					if (!feature.isNegated()) {
						command.append(" --enable-").append(((Enable) feature).getFlag());
					}
					else {
						command.append(" --disable-").append(((Enable) feature).getFlag());
					}
				}
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

		shell.setDir(dir);
		shell.setExecutable("sh");

		shell.setFailonerror(true);

		shell.createArg().setValue("-c");
		shell.createArg().setValue(command.toString());

		shell.execute();
    }
}
