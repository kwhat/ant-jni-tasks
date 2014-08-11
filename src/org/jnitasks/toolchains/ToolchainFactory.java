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
package org.jnitasks.toolchains;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.ClasspathUtils;

public abstract class ToolchainFactory {
	public static CompilerAdapter getCompiler(String toolchain) throws BuildException {
		String ucFirstCompiler = toolchain.substring(0, 1).toUpperCase() + toolchain.substring(1).toLowerCase();

		return (CompilerAdapter) ClasspathUtils.newInstance("org.jnitasks.toolchains.adapters." + ucFirstCompiler + "Compiler",
				ToolchainFactory.class.getClassLoader(),
				CompilerAdapter.class);
	}

	public static LinkerAdapter getLinker(String toolchain) throws BuildException {
		String ucFirstLinker = toolchain.substring(0, 1).toUpperCase() + toolchain.substring(1).toLowerCase();

		return (LinkerAdapter) ClasspathUtils.newInstance("org.jnitasks.toolchains.adapters." + ucFirstLinker + "Linker",
				ToolchainFactory.class.getClassLoader(),
				LinkerAdapter.class);
	}
}
