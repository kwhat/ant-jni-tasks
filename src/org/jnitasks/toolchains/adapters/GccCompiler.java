package org.jnitasks.toolchains.adapters;

import org.jnitasks.toolchains.CompilerAdapter;
import org.jnitasks.types.AbstractFeature;
import org.jnitasks.types.Define;
import org.jnitasks.types.Include;

public class GccCompiler extends CompilerAdapter {
	public GccCompiler() {
		super();
		super.executable = "gcc";
	}

	public String describeCommand() {
		StringBuilder command = new StringBuilder(this.getExecutable());

		for (AbstractFeature feat : features) {
			if (feat instanceof Define) {
				Define def = (Define) feat;

				command.append(" -D").append(def.getName());
				if (def.getValue() != null) {
					command.append('=').append(def.getValue());
				}
			}
			else if (feat instanceof Include) {
				Include inc = (Include) feat;

				command.append(" -I").append(inc.getPath());
			}
			else if (feat instanceof Argument) {
				Argument arg = (Argument) feat;

				command.append(' ').append(arg.getValue());
			}
		}

		command.append(" -c ").append(this.getInFile());
		command.append(" -o ").append(this.getOutFile());

		return command.toString();
	}
}
