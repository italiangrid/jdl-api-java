/*
* JobAdException.java
*
* Copyright (c) 2001 The European DataGrid Project - IST programme, all rights reserved.
*
* Contributors are mentioned in the code there appropriate.
*
*/
package org.glite.jdl;


/**
 * Manage the exception raised while trying to build/check a JobAd
 *
 * @version 0.1
 * @author Alessandro Maraschini <alessandro.maraschini@datamat.it>
*/
public class JobAdException extends Exception {
    /**
    * Default Exception Constructor
    * Particular Exception thrown by Ad and son elements
    * @param msg the error reason explanation
    */
    public JobAdException(String msg) {
        super(msg);
    }

    /**
    * Exception Constructor
    * Particular Exception thrown by Ad and son elements
    * @param msg the error reason explanation
    * @param ex the throwable root object
    */
    public JobAdException(String msg, Throwable ex) {
        super(msg, ex);
    }
}
