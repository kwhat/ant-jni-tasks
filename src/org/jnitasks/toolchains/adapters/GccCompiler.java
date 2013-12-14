/* JNITasks: Ant tasks for JNI projects.
 * Copyright (C) 2013-2014 Alexander Barker.  All Rights Received.
 * https://github.com/kwhat/jnitasks/
 *
 * JNITasks is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JNITasks is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
		StringBuilder command = new StringBuilder(this.getCommand());

		for (AbstractFeature feat : features) {
			if (feat.isValidOs() && feat.isIfConditionValid() && feat.isUnlessConditionValid()) {
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
		}

		command.append(" -c ").append(this.getInFile());
		command.append(" -o ").append(this.getOutFile());

		return command.toString();
	}
}
