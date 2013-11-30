package org.jnitasks.toolchains;

import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.jnitasks.types.Include;
import org.jnitasks.types.Library;

import java.util.Hashtable;
import java.util.Vector;

/**
 * Created by kwhat on 11/27/13.
 */
public abstract class AbstractCompiler extends DataType {
	protected String prefix;
	protected Standard standard;
	protected Optimization optimize;
	protected String cflags;

	protected Hashtable<String, String> macros = new Hashtable<String, String>();
	protected Vector<Include> includes = new Vector<Include>();
	protected Vector<Library> libraries = new Vector<Library>();

	public void addMacro(String name) {
		this.addMacro(name, null);
	}

	public void addMacro(String name, String value) {
		this.macros.put(name, value);
	}

	public void addInclude(Include inc) {
		this.includes.add(inc);
	}

	public void addLibrary(Library lib) {
		this.libraries.add(lib);
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setStd(Standard standard) {
		this.standard = standard;
	}

	public void setCflags(String cflags) {
		this.cflags = cflags;
	}

	public void setOptimization(Optimization optimize) {
		this.optimize = optimize;
	}

	public abstract String toString();


	public static class Standard extends EnumeratedAttribute {
		public String[] getValues() {
			return new String[] { "c89", "c90", "c99", "c11" };
		}
	}

	public static class Optimization extends EnumeratedAttribute {
		public String[] getValues() {
			return new String[] { "0", "1", "2", "3" };
		}
	}
}
