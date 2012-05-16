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
import java.lang.*; // primitive class
import java.text.ParseException;
import java.util.*;
/**
 * This class is not yet functional
 *
 * @version 0.1
 * @author Alessandro Maraschini <alessandro.maraschini@datamat.it>
*/
public class DagAd extends Ad {
    /**
      * This Variable is used in order to store the classAd information */
    /**
    * The Structure of jobs is as follows:
    * HashMap jobs = {
    *    <String node1> : <JobAd [ 1 ]>
    *    <String node2> : <JobAd [ 2 ]>
    *    <String node3> : <JobAd [ 3 ]>
    * }
    */
    /**
      nodes = [
              node_type = "edg-jdl" ; // If this attribute is specified it becomes the default value
              nodeA= [
                      file = "jobad.jdl" ;
                      retry= 3 ;
                      ]
              nodeB = [
                      node_type = "edg-jdl" ;
                      description = "[Executable ="foobar" ; ]
              ]
              nodeB = [
                      node_type = "edg-jdl" ;
                      file = "b.ad" ;
              ]
              dependendencies =  {
                      {            nodeA             ,              nodeB  } ,
                      {            nodeA             ,              nodeC  },
                      {     {nodeA, nodeC}       ,              nodeD  }
              }
      ]
    */
    /** Used to print the current DagAd */
    public static int STRING_CURRENT = 0;
    /** Used to print the submission DagAd */
    public static int STRING_SUBMISSION = 1;
    /** Used to print the simple DagAd without nodes*/
    public static int STRING_NO_NODES = 2;
    /** Used to print the DagAd over multi lines*/
    public static int STRING_MULTI_LINES = 3;
    private Ad nodes;
    private HashMap cycleJobs;
    /**
    * The Structure of dependencies is as follows:
    * HashMap dependencies={
    *              <String node1>:<HashSet[ node2 , node3, ... all node1's sons]   ;
    *             <String node2>:<HashSet[ node5 , node6, ... all node2's sons]   >
    *              }                                                                            */
    private HashMap dependencies;

    /**
    * The default empty constructor */
    public DagAd() {
        super();

        // jobs = new HashMap() ;
        dependencies = new HashMap();

        //  dagAd= new RecordExpr() ;
    }

    /**
    * Return the Dag into a string representation
    * @see #STRING_CURRENT
    * @see #STRING_SUBMISSION
    * @see #STRING_NO_NODES
    * @see #STRING_MULTI_LINES
    */
    public String toString(int level) {
        switch (level) {
        case 0: // STRING_CURRENT:
            break;

        case 1: //STRING_SUBMISSION:
            break;

        case 2: //STRING_NO_NODES:
            break;

        case 3: //STRING_MULTI_LINES:
            break;

        default:
            return "";
        }

        return "DagAd ToString:\n\n" + jobAd.toString();
    }

    /**
    Add a new Ad with the specified String node
    @param nodeValue - the value of the node*/
    public void addNode(Node node) throws Exception {
        if (nodes.lookup(node.name) != null) { //  The node already exists
            throw new JobAdException("The node \"" + node.name +
                "\" already exists");
        }

        // nodes.setAttribute(  nodeName , nodeValue ) ; //TBD method COPYAD
    }

    /**
    * Remove the specified job from the DagAd
    */
    public void removeNode(String node) throws NoSuchFieldException {
        if (nodes.lookup(node) == null) {
            //  The node doesn't exist
            throw new NoSuchFieldException("Unknown node: \"" + node + "\"");
        }

        // nodes.removeAttribute( AttrName.fromString( jobKey)) ; //TBDclassad2.0
        //nodes.removeAttribute( jobKey) ;
        nodes.delAttribute(node);
    }

    /**This method is used by addDependency*/
    private void addArc(String jobFather, String jobSon)
        throws Exception {
        Vector depList = new Vector();
        ListExpr depExpr = (ListExpr) nodes.lookup(Jdl.DAG_DEPENDENCIES);

        if (depExpr != null) {
            for (int i = 0; i < depExpr.size(); i++)
                depList.add(depExpr.sub(i));
        }

        // Build the new value to be added
        Vector vect = new Vector();
        vect.add(Constant.getInstance(jobFather));
        vect.add(Constant.getInstance(jobSon));

        ListExpr exprToAdd = new ListExpr(vect);

        // Add the new value to the list:
        depList.add(exprToAdd);

        // Re-Generate the new DEPENDENCIES attribute:
        nodes.setAttribute(Jdl.DAG_DEPENDENCIES, new ListExpr(depList));
    }

    /**
    * Add the couple father, son to the DagAd dependencies
    */
    public void addDependency(String jobFather, String jobSon)
        throws NoSuchFieldException, JobAdException, Exception {
        if (jobFather.equals(jobSon)) {
            throw new NoSuchFieldException(
                "Unable to add a dependency with two equal nodes (" +
                jobFather + ")");
        }

        if (nodes.lookup(jobFather) == null) {
            //  The node doesn't exist
            throw new NoSuchFieldException("Unknown node: \"" + jobFather +
                "\"");
        }

        if (nodes.lookup(jobSon) == null) {
            //  The node doesn't exist
            throw new NoSuchFieldException("Unknown node: \"" + jobSon + "\"");
        }

        HashSet hSet = (HashSet) dependencies.get(jobFather);

        if (hSet == null) {
            //  The node doesn't exist
            hSet = new HashSet();
            hSet.add(jobSon);
            dependencies.put(jobFather, hSet);
            addArc(jobFather, jobSon);
        } else {
            //  The node already exists
            if (hSet.contains(jobSon)) {
                //  The son already exists TBD Exception
                throw new JobAdException("The dependency \"" + jobFather +
                    "-\"" + jobSon + "\" already exists");
            } else {
                hSet.add(jobSon);
                addArc(jobFather, jobSon);
            }
        }
    }

    /**
    * Remove the couple father, son from the DagAd dependencies if it has been already set
    */
    public void removeDependency(String jobFather, String jobSon)
        throws NoSuchFieldException {
        HashSet hSet = (HashSet) dependencies.get(jobFather);

        if (hSet == null) {
            //  The node doesn't exist
            throw new NoSuchFieldException("Unknown node: \"" + jobFather +
                "\"");
        } else {
            //  The node already exists
            if (!hSet.remove(jobSon)) {
                // Unable to remove TBD Exception
                throw new NoSuchFieldException("Unknown node: \"" + jobSon +
                    "\"");
            }
        }
    }

    private void setNodes(RecordExpr attrValue) {
        Iterator it = attrValue.attributes();

        while (it.hasNext()) {
            String nodeName = (String) (it.next());

            if (attrValue.lookup(nodeName).type != Expr.RECORD) {
                throw new IllegalArgumentException(nodeName +
                    ": Must be a of classad type");
            }
        }
    }

    /** Set an Expr Attribute to the DagAd. Thie method is called by fromString and fromFile while building the DagAd*/
    public void setAttribute(String attrName, Expr attrValue)
        throws IllegalArgumentException {
        if (lookup(attrName) != null) {
            throw new IllegalArgumentException(attrName +
                ": already set attribute");
        }

        if (attrName.toLowerCase().equals(Jdl.DAG_NODES.toLowerCase())) {
            if (attrValue.type != Expr.RECORD) {
                throw new IllegalArgumentException(attrName +
                    ": Must be a of classad type");
            } else {
                setNodes((RecordExpr) attrValue);
            }
        } else if (attrName.toLowerCase().equals(Jdl.DAG_DEPENDENCIES.toLowerCase())) {
        }

        /*
                if (Jdl.find(attrName)){
                if ( attrValue.type == Expr.LIST) {
                if (!Jdl.findAttribute (attrName , Jdl.listAttributes)  )
                throw new InvalidAttributeValueException(attrName + ": List not allowed" ); //   Unexpected type
        */
        jobAd.insertAttribute(attrName, attrValue);
    }

    /** This method is used by checkLoops ()*/
    private HashSet extractSons(String father) throws ParseException {
        // System.out.println( "\nExtracting for : " + father ) ;
        HashSet sons = (HashSet) cycleJobs.get(father);

        if (sons == null) {
            // No previous search has been done
            cycleJobs.put(father, new HashSet());
            sons = (HashSet) dependencies.get(father);

            if (sons == null) {
                // leaf Job: no dependency found
                // System.out.println( "\n leaf") ;
                sons = new HashSet();
                sons.add(father);
                cycleJobs.put(father, sons);

                return sons;
            } else {
                // Look for and append the new sons got directly from dependencies:
                // System.out.println( "-------     First Search: sons... iterate over " +  sons +": ") ;
                HashSet newSons = new HashSet();
                Iterator it = sons.iterator();

                while (it.hasNext()) {
                    String node = (String) (it.next());
                    Iterator iter = (extractSons(node)).iterator();

                    while (iter.hasNext()) {
                        newSons.add((String) iter.next());
                    }
                }

                newSons.add(father);
                cycleJobs.put(father, newSons);

                // System.out.println( "-------      ... done iteration for" + father) ;
                return newSons;
            }
        } else if (sons.isEmpty()) {
            // A previous search has begun but still in progress: closed cycle!!!!
            throw new ParseException(father +
                ": dependency error closed cycle catched", 0);
        } else {
            // The search for this father has been already made: just return the value
            // System.out.println( "\n already made! skip") ;
            return sons;
        }
    }

    /** Controls wheter there are closed loops inside the Dag*/
    private void checkLoops() throws Exception {
        // Launch one job per father:
        HashMap thMap = new HashMap();
        cycleJobs = new HashMap();

        Iterator it = (dependencies.keySet()).iterator();

        while (it.hasNext()) {
            String father = (String) (it.next());

            if (!cycleJobs.containsKey(father)) {
                extractSons(father);
            }

            /*
            String father =  (String) (it.next()) ;
            if (!  thMap.contains (  father )  ){
            Thread th ;
            thMap.add( father, th) ;
            th= new Thread( father ) ;
            th.start() ;
            }
            */
        }

        // System.out.println( "Dependencies:\n" + cycleJobs ) ;
    }

    public void check() throws Exception {
        // Look for recoursive loops in dependencies:
        checkLoops();

        // Add the Type field
        jobAd.insertAttribute(Jdl.TYPE,
            (Expr) Constant.getInstance(Jdl.TYPE_DAG));
    }
}


//  END DagAd Class
