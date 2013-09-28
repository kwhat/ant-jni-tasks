package org.jnitasks.autotools;

import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Commandline.Argument;

public class ConfigureTask extends Task {
	private String prefix = null;
	
	private List<FeatureType> use_flags = new ArrayList<FeatureType>();
	
	@Override
    public void execute() {
		// Set the command to execute along with any required arguments.
		StringBuilder command = new StringBuilder("configure --verbose");

		// Take care of the optional arguments.
		if (this.prefix != null) {
			command.append(" --prefix=");
			command.append(prefix);
		}

		// Create an exec task to run a shell.  Using the current shell to 
		// execute commands is required for Windows support.
		ExecTask shell = (ExecTask) this.getProject().createTask("exec");
		shell.setExecutable("sh");
		Argument arg = shell.createArg();
		arg.setLine(command.toString());
    }

	public void add(EnableType e) {
		use_flags.add(e);
	}
	
	public void add(DisableType e) {
		use_flags.add(e);
	}

	public void add(WithType e) {
		use_flags.add(e);
	}

	public void add(WithoutType e) {
		use_flags.add(e);
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	private abstract class FeatureType extends DataType {
		private String flag;
	    
	    public void addText(String text) {
	    	this.flag = text;
	    }
		
	    public void setFlag(String flag) {
	    	this.flag = flag;
	    }
	    
	    public String getFlag() {
	    	return this.flag;
	    }
	    
	    public abstract String toString();
	}

	public class EnableType extends FeatureType {
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "--enable-" + this.getFlag();
		}
		
	}

	private class DisableType extends FeatureType {
		@Override
		public String toString() {
			return "--disable-" + this.getFlag();
		}
	}
	
	public class WithType extends FeatureType {
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "--with-" + this.getFlag();
		}
		
	}

	private class WithoutType extends FeatureType {
		@Override
		public String toString() {
			return "--without-" + this.getFlag();
		}
	}
}
