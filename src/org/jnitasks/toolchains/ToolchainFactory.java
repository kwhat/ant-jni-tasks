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
