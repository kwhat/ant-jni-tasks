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

import org.jnitasks.CcTask;
import org.jnitasks.toolchains.CompilerAdapter;
import org.jnitasks.types.AbstractFeature;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GccCompiler extends CompilerAdapter {
	public GccCompiler() {
		super();

		setExecutable("gcc");
	}

	public Iterator<String> getArgs() {
		List<String> args = new ArrayList<String>();

		for (AbstractFeature feat : features) {
			if (feat instanceof CcTask.Define) {
				CcTask.Define def = (CcTask.Define) feat;
				String macro = "-D" + def.getName();

				if (def.getValue() != null) {
					macro += '=' + def.getValue();
				}
				args.add(macro);
			}
			else if (feat instanceof CcTask.Include) {
				CcTask.Include inc = (CcTask.Include) feat;

				args.add("-I" + inc.getPath().replace('\\', '/'));
			}
			else if (feat instanceof CcTask.Argument) {
				CcTask.Argument arg = (CcTask.Argument) feat;

				args.add(arg.getValue());
			}
		}

		args.add("-c " + this.getInFile().replace('\\', '/'));
		args.add("-o " + this.getOutFile().replace('\\', '/'));

		return args.iterator();
	}
}
