package org.jnitasks.toolchains.gnu;

import org.jnitasks.toolchains.AbstractCompiler;
import org.jnitasks.types.Include;
import org.jnitasks.types.Library;

import java.io.File;
import java.util.Enumeration;
import java.util.Iterator;

public class Compiler extends AbstractCompiler {


	public static class Standard extends AbstractCompiler.Standard {
		public String[] getValues() {
			String[] parentValues = super.getValues();
			String[] extraValues = new String[] { "gnu90", "gnu99", "gnu11" };
			String[] allValues = new String[parentValues.length + extraValues.length];

			System.arraycopy(parentValues, 0, allValues, 0, allValues.length);
			System.arraycopy(extraValues, 0, allValues, allValues.length, extraValues.length);

			return allValues;
		}
	}

	public static class Optimization extends AbstractCompiler.Optimization {
		public String[] getValues() {
			String[] parentValues = super.getValues();
			String[] extraValues = new String[] { "s", "fast", "g" };
			String[] allValues = new String[parentValues.length + extraValues.length];

			System.arraycopy(parentValues, 0, allValues, 0, allValues.length);
			System.arraycopy(extraValues, 0, allValues, allValues.length, extraValues.length);

			return allValues;
		}
	}



	public String toString() {
		StringBuilder command = new StringBuilder("gcc");

		if (prefix != null) {
			command.insert(0, '-').insert(0, prefix);
		}

		if (this.cflags != null) {
			command.append(this.cflags);
		}
		else if (System.getenv("CFLAGS") != null) {
			command.append(System.getenv("CFLAGS"));
		}

		if (optimize != null) {
			String level = optimize.getValue();

			if (level.equalsIgnoreCase("none")) {
				level = "0";
			}
			else if (level.equalsIgnoreCase("size")) {
				level = "s";
			}
			else if (level.equalsIgnoreCase("debug")) {
				level = "g";
			}

			command.append(" -O").append(level);
		}

		if (standard != null) {
			command.append(" -std=").append(standard.getValue());
		}

		Enumeration<String> macroKeys = macros.keys();
		while (macroKeys.hasMoreElements()) {
			String key = macroKeys.nextElement();
			String value = macros.get(key);

			command.append(" -D").append(key);
			if (value != null) {
				command.append('=').append(value);
			}
		}

		for (Include inc : includes) {
			String path = inc.getPath();

			if (path != null) {
				command.append(" -I").append(path);
			}
		}

		Iterator<Library> libs = libraries.iterator();
		for (Library lib : libraries) {
			String name = lib.getLib();
			File path = lib.getPath();

			if (path != null) {
				command.append(" -L").append(path.getPath());
			}

			if (name != null) {
				command.append(" -l").append(name);
			}
		}

		return command.toString();
	}
}
