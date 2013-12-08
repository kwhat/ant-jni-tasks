package org.jnitasks.types;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.DataType;

import java.util.Locale;

public abstract class AbstractFeature extends DataType {
	private String os;
	private String osFamily;

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
