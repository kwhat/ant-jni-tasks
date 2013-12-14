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
package org.jnitasks.types;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.DataType;

import java.util.Locale;

public abstract class AbstractFeature extends DataType {
	/** The "if" condition to test on execution. */
	private String ifCondition = "";
	/** The "unless" condition to test on execution. */
	private String unlessCondition = "";

	private String os;
	private String osFamily;

	/**
	 * Sets the "if" condition to test on execution. This is the
	 * name of a property to test for existence - if the property
	 * is not set, the task will not execute. The property goes
	 * through property substitution once before testing, so if
	 * property <code>foo</code> has value <code>bar</code>, setting
	 * the "if" condition to <code>${foo}_x</code> will mean that the
	 * task will only execute if property <code>bar_x</code> is set.
	 *
	 * @param property The property condition to test on execution.
	 *                 May be <code>null</code>, in which case
	 *                 no "if" test is performed.
	 */
	public void setIf(String property) {
		ifCondition = (property == null) ? "" : property;
	}

	/**
	 * Returns the "if" property condition of this target.
	 *
	 * @return the "if" property condition or <code>null</code> if no
	 *         "if" condition had been defined.
	 * @since 1.6.2
	 */
	public String getIf() {
		return ("".equals(ifCondition) ? null : ifCondition);
	}

	/**
	 * Sets the "unless" condition to test on execution. This is the
	 * name of a property to test for existence - if the property
	 * is set, the task will not execute. The property goes
	 * through property substitution once before testing, so if
	 * property <code>foo</code> has value <code>bar</code>, setting
	 * the "unless" condition to <code>${foo}_x</code> will mean that the
	 * task will only execute if property <code>bar_x</code> isn't set.
	 *
	 * @param property The property condition to test on execution.
	 *                 May be <code>null</code>, in which case
	 *                 no "unless" test is performed.
	 */
	public void setUnless(String property) {
		unlessCondition = (property == null) ? "" : property;
	}

	/**
	 * Returns the "unless" property condition of this target.
	 *
	 * @return the "unless" property condition or <code>null</code>
	 *         if no "unless" condition had been defined.
	 * @since 1.6.2
	 */
	public String getUnless() {
		return ("".equals(unlessCondition) ? null : unlessCondition);
	}

	/**
	 * Tests whether or not the "if" condition is satisfied.
	 *
	 * @return whether or not the "if" condition is satisfied. If no
	 *         condition (or an empty condition) has been set,
	 *         <code>true</code> is returned.
	 *
	 * @see #setIf(String)
	 */
	public boolean isIfConditionValid() {
		if ("".equals(ifCondition)) {
			return true;
		}

		String test = getProject().replaceProperties(ifCondition);
		return getProject().getProperty(test) != null;
	}

	/**
	 * Tests whether or not the "unless" condition is satisfied.
	 *
	 * @return whether or not the "unless" condition is satisfied. If no
	 *         condition (or an empty condition) has been set,
	 *         <code>true</code> is returned.
	 *
	 * @see #setUnless(String)
	 */
	public boolean isUnlessConditionValid() {
		if ("".equals(unlessCondition)) {
			return true;
		}
		String test = getProject().replaceProperties(unlessCondition);
		return getProject().getProperty(test) == null;
	}

	/**
	 * List of operating systems on which the command may be executed.
	 * @param os list of operating systems on which the command may be executed.
	 */
	public void setOs(String os) {
		this.os = os;
	}

	/**
	 * List of operating systems on which the command may be executed.
	 * @since Ant 1.8.0
	 */
	public final String getOs() {
		return os;
	}

	/**
	 * Restrict this execution to a single OS Family
	 * @param osFamily the family to restrict to.
	 */
	public void setOsFamily(String osFamily) {
		this.osFamily = osFamily.toLowerCase(Locale.ENGLISH);
	}

	/**
	 * Restrict this execution to a single OS Family
	 * @since Ant 1.8.0
	 */
	public final String getOsFamily() {
		return osFamily;
	}

	/**
	 * Is this the OS the user wanted?
	 * @return boolean.
	 * <ul>
	 * <li>
	 * <li><code>true</code> if the os and osfamily attributes are null.</li>
	 * <li><code>true</code> if osfamily is set, and the os family and must match
	 * that of the current OS, according to the logic of
	 * {@link org.apache.tools.ant.taskdefs.condition.Os#isOs(String, String, String, String)}, and the result of the
	 * <code>os</code> attribute must also evaluate true.
	 * </li>
	 * <li>
	 * <code>true</code> if os is set, and the system.property os.name
	 * is found in the os attribute,</li>
	 * <li><code>false</code> otherwise.</li>
	 * </ul>
	 */
	public boolean isValidOs() {
		boolean valid = false;

		//hand osfamily off to Os class, if set
		if (osFamily == null || Os.isFamily(osFamily)) {
			String myos = System.getProperty("os.name");

			log("Current OS is " + myos, Project.MSG_VERBOSE);
			if (os == null || os.equalsIgnoreCase(myos)) {
				// this command will be executed only on the specified OS
				valid = true;
			}
			else {
				//the Exec OS check is different from Os.isOs(), which
				//probes for a specific OS. Instead it searches the os field
				//for the current os.name
				log("This OS, " + myos
						+ " was not found in the specified list of valid OSes: "
						+ os, Project.MSG_VERBOSE);
			}
		}

		return valid;
	}
}
