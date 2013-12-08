package org.jnitasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.Echo;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.FileSet;
import org.jnitasks.toolchains.LinkerAdapter;
import org.jnitasks.toolchains.ToolchainFactory;
import org.jnitasks.types.AbstractFeature;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;

public class LdTask extends MatchingTask {
	protected Vector<FileSet> filesets = new Vector<FileSet>();
	protected Vector<AbstractFeature> features = new Vector<AbstractFeature>();
	private String outfile = null;
	private String toolchain = "gcc";

	public void execute() {
		if (outfile == null) {
			throw new BuildException("The outfile attribute is required");
		}

		// Setup the compiler.
		LinkerAdapter linker = ToolchainFactory.getLinker(toolchain);
		linker.setOutFile(outfile);

		for (AbstractFeature feat : features) {
			if (feat.isValidOs()) {
				linker.addArg(feat);
			}
		}

		Iterator<FileSet> iterator = filesets.iterator();
		while (iterator.hasNext()) {
			FileSet file = iterator.next();

			DirectoryScanner scanner = file.getDirectoryScanner(getProject());
			String[] files = scanner.getIncludedFiles();
			for(int i = 0; i < files.length; i++) {
				File basePath = scanner.getBasedir();

				// Convert Windows paths to posix compatible paths.
				linker.addInFile(new File(basePath, files[i]).getAbsolutePath().replace('\\','/'));
			}
		}


		// Print the executed command.
		Echo echo = (Echo) getProject().createTask("echo");
		echo.addText(linker.describeCommand());
		echo.setTaskName(this.getTaskName());
		echo.execute();


		// Create an exec task to run a shell.  Using the current shell to
		// execute commands is required for Windows support.
		ExecTask shell = (ExecTask) getProject().createTask("exec");
		shell.setTaskName(this.getTaskName());
		//shell.setDir(dir);
		shell.setExecutable("sh");
		shell.setFailonerror(true);
		shell.createArg().setValue("-c");
		shell.createArg().setValue(linker.describeCommand());
		shell.execute();
	}

	public void addFileset(FileSet fileset) {
		filesets.add(fileset);
	}

	public void setToolchain(String toolchain) {
		this.toolchain = toolchain;
	}

	public void setOutfile(String outfile) {
		this.outfile = outfile;
	}

	public LinkerAdapter.Argument createArg() {
		LinkerAdapter.Argument arg = new LinkerAdapter.Argument();
		features.add(arg);

		return arg;
	}
}
