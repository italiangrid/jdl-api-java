/*
* JobState.java
*
* Copyright (c) 2001 The European DataGrid Project - IST programme, all rights reserved.
*
*/
package org.glite.jdl;

import condor.classad.*;

import java.util.Iterator;
import java.util.Vector;

import javax.naming.directory.InvalidAttributeValueException;


/**
* This class stores the information related to a particular state of a checkpointable Job
 *
 * @version 0.1
 * @author Alessandro Maraschini <alessandro.maraschini@datamat.it> */
public class JobState extends Ad {
    /** String representation for JobState  attribute*/
    public static String JOBID = "StateId";

    /** String representation for   CurrentStep attribute*/
    public static String CURRENT = "CurrentStep";

    /** String representation for   JobSteps attribute*/
    public static String JOBSTEPS = "JobSteps";

    /** String representation for  UserData attribute */
    public static String USERDATA = "UserData";

    /** Constructor copy*/
    public JobState(String state)
        throws java.text.ParseException, JobAdException {
        super(state);
    }

    /** Default Constructor */
    public JobState() {
        super();
    }

    /** Set the specified JobId inside the State
    * @param jobId the string representation of the JobId*/
    public void setId(String jobId) {
        // Discard previous value (if any)
        jobAd.insertAttribute(JOBID, Constant.getInstance(jobId));
    }

    /** Check the validity of the JobState instance*/
    public void check() throws JobAdException {
        String finalException = "";
        Iterator it = attributes();
        String attrName;

        //Whole attributes cycle check:
        while (it.hasNext()) {
            try {
                attrName = (String) (it.next());
                checkAttribute(attrName, lookup(attrName));
            } catch (Exception exc) {
                finalException += (exc.getMessage() + "\n");
            }
        }

        // If error found than throw exception
        if (!finalException.equals("")) {
            throw new JobAdException(finalException.trim());
        }
    }

    /** Set the specified couple attributre, value, inside the JobState
    *@param attrName the name of the attribute to be set
    *@param attrValue the classad expression to be added
    * @throws  IllegalArgumentException- The attribute  attrName had been
    * @throws InvalidAttributeValueException - the value is out of limits for the specified attribute*/
    public void setAttribute(String attrName, Expr attrValue)
        throws InvalidAttributeValueException, IllegalArgumentException {
        System.out.println("JobState setAttribute Checking 1 " + attrName);

        // Check the couple
        checkAttribute(attrName, attrValue);

        // insert the attribute
        jobAd.insertAttribute(attrName, attrValue);
    }

    /*
    * Check if the couple attribute/value is admitted
    * @param attrName  the name of the attribute
    * @param attrValue  the value of the attribute
    * @throws  InvalidAttributeValueException  The type of value is not allowed for the specified attribute name */
    public void checkAttribute(String attrName, Expr attrValue)
        throws InvalidAttributeValueException {
        if ((attrName.toLowerCase()).equals(JOBID.toLowerCase())) {
            System.out.println("JobState Checking jobid " + attrName);
        } else if ((attrName.toLowerCase()).equals(CURRENT.toLowerCase())) {
            System.out.println("JobState Checking current " + attrName);
        } else if ((attrName.toLowerCase()).equals(JOBSTEPS.toLowerCase())) {
            System.out.println("JobState Checking jobsteps " + attrName);
        } else if ((attrName.toLowerCase()).equals(USERDATA.toLowerCase())) {
            System.out.println("JobState Checking userData " + attrName);
        }
    }
}
