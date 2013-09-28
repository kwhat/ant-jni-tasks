package org.jnitasks.deps;

import java.util.Vector;

import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.DirSet;

public class Include extends DataType {
	private Vector<DirSet> dirsets = new Vector<DirSet>();
	
	public void addDirset(DirSet dirset) {
		dirsets.add(dirset);
	}
}
