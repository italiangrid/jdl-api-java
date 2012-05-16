/**
* DagAd.java
*
* Copyright (c) 2001 The European DataGrid Project - IST programme, all rights reserved.
*
* Contributors are mentioned in the code there appropriate.
*
*/
package org.glite.jdl;

import condor.classad.*;

import java.io.File;

import java.lang.*; // primitive class

import java.text.ParseException;

import java.util.*;


/**
 * Provide a representetion of a DagAd Node
 *
 * @version 0.1
 * @author Alessandro Maraschini <alessandro.maraschini@datamat.it>
*/
public class Node extends Ad {
    /* PRIVATE MEMBERS */
    private RecordExpr node;
    String name;
    private JobAd job;

    /*************************
    *   CONSTRUCTORS
    *************************/
    /** Constructor with a ClassAd RecrdExpr */
    Node(String nodeName, RecordExpr nodeValue) {
    }

    /** Constructor with Ad*/
    Node(String nodeName, Ad nodeValue) {
    }

    /** Constructor with File Description*/
    Node(String nodeName, File nodeValue) {
    }

    Node(String nodeName, String nodeValue) {
    }

    /*************************
    *   METHODS
    *************************/
    /** Expand the node looking for the description Ad and replace the file description*/
    void expand() {
    }

    public String getName() {
        return name;
    }

    // String toString (  ){   return "Node::toString" ;  } ;
}
