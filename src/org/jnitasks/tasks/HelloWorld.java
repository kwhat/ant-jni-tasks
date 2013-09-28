package org.jnitasks.tasks;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import org.apache.tools.ant.Task;

public class HelloWorld extends Task {
    public void execute() {
        // use of the reference to Project-instance
        String message = getProject().getProperty("ant.project.name");

        // Task's log method
        log("Here is project '" + message + "'.");

        // where this task is used?
        log("I am used in: " +  getLocation() );
    }
}
