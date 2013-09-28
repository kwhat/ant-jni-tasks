package org.jnitasks.test.autotools;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;

public class VerboseLogger extends DefaultLogger {
	private String lastMessage;
    
	public VerboseLogger() {
		super();
        
		setMessageOutputLevel(Project.MSG_INFO);
		setOutputPrintStream(System.out);
		setErrorPrintStream(System.err);
	}
    
	@Override
	protected void log(String message) {
		this.lastMessage = message;

		//System.out.println(message);
	}
	
	public String getLastMessgae() {
		return this.lastMessage;
	}
}
