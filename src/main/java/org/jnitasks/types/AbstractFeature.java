/* JNITasks: Ant tasks for JNI projects.
 * Copyright (C) 2013-2020 Alexander Barker.  All Rights Received.
 * https://github.com/kwhat/ant-jni-tasks/
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
package org.jnitasks.types;

import org.apache.tools.ant.types.DataType;

public abstract class AbstractFeature extends DataType {
    private String ifCondition = "";

    private String unlessCondition = "";

    /**
     * Sets the "if" condition to test on execution. This is the name of a property to test for
     * existence - if the property is not set, the task will not execute. The property goes through
     * property substitution once before testing, so if property <code>foo</code> has value
     * <code>bar</code>, setting the "if" condition to <code>${foo}_x</code> will mean that the
     * task
     * will only execute if property <code>bar_x</code> is set.
     *
     * @param property The property condition to test on execution. May be <code>null</code>, in
     *                 which case no "if" test is performed.
     */
    public void setIf(String property) {
        if (property == null) {
            ifCondition = "";
        } else {
            ifCondition = property;
        }
    }

    /**
     * Returns the "if" property condition of this target.
     *
     * @return the "if" property condition or <code>null</code> if no "if" condition had been
     * defined.
     * @since 1.6.2
     */
    public String getIf() {
        String condition = ifCondition;
        if ("".equals(condition)) {
            condition = null;
        }

        return condition;
    }

    /**
     * Sets the "unless" condition to test on execution. This is the name of a property to test for
     * existence - if the property is set, the task will not execute. The property goes through
     * property substitution once before testing, so if property <code>foo</code> has value
     * <code>bar</code>, setting the "unless" condition to <code>${foo}_x</code> will mean that the
     * task will only execute if property <code>bar_x</code> isn't set.
     *
     * @param property The property condition to test on execution. May be <code>null</code>, in
     *                 which case no "unless" test is performed.
     */
    public void setUnless(String property) {
        if (property == null) {
            unlessCondition = "";
        } else {
            unlessCondition = property;
        }
    }

    /**
     * Returns the "unless" property condition of this target.
     *
     * @return the "unless" property condition or <code>null</code> if no "unless" condition had
     * been defined.
     * @since 1.6.2
     */
    public String getUnless() {
        String condition = unlessCondition;
        if ("".equals(condition)) {
            condition = null;
        }

        return condition;
    }

    /**
     * Tests whether or not the "if" condition is satisfied.
     *
     * @return whether or not the "if" condition is satisfied. If no condition (or an empty
     * condition) has been set,
     * <code>true</code> is returned.
     * @see #setIf(String)
     */
    public boolean isIfConditionValid() {
        boolean isCondition = "".equals(ifCondition);
        if (!isCondition) {
            String test = getProject().replaceProperties(ifCondition);
            isCondition = getProject().getProperty(test) == null;
        }

        return isCondition;
    }

    /**
     * Tests whether or not the "unless" condition is satisfied.
     *
     * @return whether or not the "unless" condition is satisfied. If no condition (or an empty
     * condition) has been set,
     * <code>true</code> is returned.
     * @see #setUnless(String)
     */
    public boolean isUnlessConditionValid() {
        boolean isCondition = "".equals(unlessCondition);
        if (!isCondition) {
            String test = getProject().replaceProperties(unlessCondition);
            isCondition = getProject().getProperty(test) == null;
        }

        return isCondition;
    }
}
