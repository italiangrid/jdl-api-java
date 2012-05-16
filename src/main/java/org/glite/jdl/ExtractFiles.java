/*
* JobAd.java
*
* Copyright (c) 2001 The European DataGrid Project - IST programme, all rights reserved.
*
* Contributors are mentioned in the code there appropriate.
*
*/
package org.glite.jdl;

import java.io.*; //Stream readers/writers, File

import java.lang.*; // primitive class

import java.util.*;
import java.util.regex.*; // extract files

import javax.naming.directory.*; //InvalidAttribute exception


/**
 *  This class is used principally by JobAd class.
 * It provides a series of utilities such as extracting a wildcards file extraction from a path
 *
 * @version 0.1
 * @author Alessandro Maraschini <alessandro.maraschini@datamat.it>
*/
public class ExtractFiles implements FilenameFilter {
    private Vector inputFiles;

    // Contain all the matching files
    private Vector extractedFiles;

    // Contain the duplicate files
    private HashSet duplicates;

    // Contain all the non-matching files
    private HashSet matches;

    // Contain the wildcards regex pattern
    private Pattern pattern;

    // The System path separator and the pattern selected
    private String sep;

    // The System path separator and the pattern selected
    private String expr;

    // A set containig all the simple  file names
    HashSet hSet;

    /**
    * Empty default constructor
    */
    public ExtractFiles() {
        sep = System.getProperty("file.separator");
        extractedFiles = new Vector();
        hSet = new HashSet();
        duplicates = new HashSet();
        matches = new HashSet();
        inputFiles = new Vector();
    }

    /**
    * Constructor from string
    * @param file the path of the file to be extraced in its string representation
    */
    public ExtractFiles(String file) {
        this();
        inputFiles = new Vector();
        inputFiles.add(file);
    }

    /**
    * Constructor with a Vector of Strings
    * @param files a Vector of paths to be extracted*/
    public ExtractFiles(Vector files) {
        this();
        inputFiles = files;
    }

    /**
    * Extract the files containing wildcards
    * @return a vector of Strings without wildcards, with the full path extracted
    */
    public Vector getMatchingFiles() {
        String dir;
        String filter;

        //    #################################################
        // Initialising Absoulute Directory:
        //  Windows          :  getAl�bsolutePath
        //  Unix-Linux-OS  :  $HOME directory
        String absDir;
        String operatingSystem = System.getProperty("os.name");

        if (operatingSystem.equals("Linux") ||
                operatingSystem.equals("Solaris") ||
                operatingSystem.equals("SunOS") ||
                operatingSystem.equals("Digital Unix") ||
                operatingSystem.equals("Unix")) {
            // Linux/Unix/Os Environment: get the Absolute working path
            absDir = new String((new File("")).getAbsolutePath());
        } else {
            //  Windows environment: get the absolute path
            absDir = new String((new File("")).getAbsolutePath());
        }

        for (int it = 0; it < inputFiles.size(); ++it) {
            String expr = (String) inputFiles.get(it);
            int separator = expr.lastIndexOf(sep);

            //Look for Operating System separator:
            if (separator >= (expr.length() - 1)) {
                // Separator last char ERROR
                dir = new String(expr.substring(0, separator));
                filter = new String("");
            } else if (separator >= 0) {
                // Separator found: split over dir and file (may contain wildcard)
                dir = expr.substring(0, separator + 1);
                filter = new String(expr.substring(separator + 1));

                if (!(new File(dir).isAbsolute())) {
                    dir = absDir + sep + dir + sep;
                }
            } else {
                // Separator not found: setting the directory to the working path (WIndows) or to the home path (Linux):
                filter = new String(expr);
                dir = absDir;
            }

            // Generate the pattern
            generatePattern(filter);

            // Extract the files
            extractFiles(new File(dir));
        }

        return extractedFiles;
    }

    /***
    * Check for duplicates or unexisting paths
    * @param name the name of the attribute cheked
    * @throws JobAdException if one or more file has a duplicated name or if it's impossible to find one or more specified path
    */
    public void check(String name) throws JobAdException {
        String exceptionMessage = new String();
        Iterator it;
        it = matches.iterator();

        // Appending matching errors
        while (it.hasNext())
            exceptionMessage += ("\n- " + name +
            ": unable to find any match for the following path:\n" + it.next());

        // Appending duplicates errors
        it = duplicates.iterator();

        while (it.hasNext())
            exceptionMessage += ("\n- " + name +
            ": filename conflict found while extracting files. The following file is repeated more than once: " +
            it.next());

        if ((matches.size() + duplicates.size()) != 0) {
            throw new JobAdException(exceptionMessage.trim().substring(2));
        }
    }

    /**
    * Check for No matching files
    * @return a HashSet contaning all the paths unable to be found
    */
    public HashSet getNoMatchingFiles() {
        return matches;
    }

    /**
    * Check for duplicate simple files
    * @return a HashSet contaning all the paths unable to be found
    */
    public HashSet getDuplicateFiles() {
        return duplicates;
    }

    private Vector extractFiles(File dir) {
        File[] tmp = dir.listFiles(this);

        if ((tmp == null) || (tmp.length == 0)) {
            // unable to find a match
            String tail = "";

            if (!dir.getAbsolutePath().endsWith(sep)) {
                tail = sep;
            }

            matches.add(dir.getAbsolutePath() + tail + expr);

            // rebuild the initial path and store it into the extractedFiles
            extractedFiles.add(dir.getAbsolutePath() + tail + expr);

            return extractedFiles;
        }

        // cycle over the files returned by extractFiles:
        for (int i = 0; i < tmp.length; ++i) {
            File file = tmp[i];

            if (file.isFile()) {
                if (!hSet.add(file.getName())) {
                    // There is already a file with the same simple name in the list, thi is not allowed
                    duplicates.add(file.getName());
                }

                extractedFiles.add(file.getAbsolutePath());

                //  addAttribute (attrName , file.getAbsolutePath()  );
            } else if (file.isDirectory()) {
                // Add All the matching file sof the specified directory
                if ((file.listFiles()).length != 0) {
                    extractFiles(file);
                }
            }
        }

        return extractedFiles;
    }

    public boolean accept(File dir, String name) {
        // Pattern pattern = Pattern.compile ( "h*") ;
        // System.out.print("\n" + pattern.pattern() +  " & " +name );
        Matcher match = pattern.matcher(name);

        if (match.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /** Replace the filter in order to match java.util.regex with wildcards rules:
            .            ---->      \x2E
            *            ---->      .*
            ?           ---->       .
    * No more wildcards to be replaced so far     */
    private void generatePattern(String filter) {
        expr = new String(filter);

        int index = filter.indexOf("\\");
        String toReplace = "\\\\";

        while (index != -1) {
            filter = filter.substring(0, index) + toReplace +
                filter.substring(index + 1);
            index = filter.indexOf("\\", index + 2);
        }

        index = filter.indexOf(".");
        toReplace = "\\x2E";

        while (index != -1) {
            filter = filter.substring(0, index) + toReplace +
                filter.substring(index + 1);
            index = filter.indexOf(".", index + 1);
        }

        index = filter.indexOf("*");
        toReplace = ".*";

        while (index != -1) {
            filter = filter.substring(0, index) + toReplace +
                filter.substring(index + 1);
            index = filter.indexOf("*", index + 2);
        }

        int fpar = filter.indexOf("{");
        int lpar = filter.indexOf("}");

        while ((fpar + lpar) != -2) {
            index = filter.indexOf(",");
            toReplace = "|";

            while (index != -1) {
                if ((index > fpar) && (index < lpar)) {
                    filter = filter.substring(0, index) + toReplace +
                        filter.substring(index + 1);
                }

                index = filter.indexOf(",", index + 1);
            }

            filter = filter.substring(0, fpar) + "(" +
                filter.substring(fpar + 1, lpar) + ")" +
                filter.substring(lpar + 1);
            fpar = filter.indexOf("{");
            lpar = filter.indexOf("}");
        }

        index = filter.indexOf("?");
        toReplace = ".";

        while (index != -1) {
            filter = filter.substring(0, index) + toReplace +
                filter.substring(index + 1);
            index = filter.indexOf("?", index + 1);
        }

        pattern = Pattern.compile(filter);
    }

    static void checkString(String attrName, String attrValue,
        String[] forbidden) throws InvalidAttributeValueException {
        for (int i = 0; i < forbidden.length; i++) {
            if (attrValue.indexOf(forbidden[i]) != -1) {
                //Forbidden string has been found
                throw new InvalidAttributeValueException(attrName +
                    ": wrong value for" + attrValue + ".Character '" +
                    forbidden[i] + "' is not allowed");
            }
        }
    }

    /*
    * Return the file name of a path (expressed in Unix-like or Windows format).
    * If path has no name return an empty string, if the path has wrong format return null.
    * @param path the string corresponding to the path
    */
    public static String getName(String path) {
        int indexBackSlash = path.lastIndexOf("\\");
        int indexSlash = path.lastIndexOf("/");

        if ((path.lastIndexOf("\\") != -1) && (path.lastIndexOf("/") != -1)) {
            return null;
        } else {
            String name1 = "";
            String name2 = "";
            int prefixLength = getPrefixLength(path);

            if (indexBackSlash < prefixLength) {
                name1 = path.substring(prefixLength);
            } else {
                name1 = path.substring(indexBackSlash + 1);
            }

            prefixLength = getPrefixLength(path);

            if (indexSlash < prefixLength) {
                name2 = path.substring(prefixLength);
            } else {
                name2 = path.substring(indexSlash + 1);
            }

            return (name1.length() < name2.length()) ? name1 : name2;
        }
    }

    /**
    This method is used by   static String getName(String path)
    */
    private static int getPrefixLength(String path) {
        if (path.length() == 0) {
            return 0;
        }

        return (path.charAt(0) == '/') ? 1 : 0;
    }

    /***
    * check wheater the path correspond to an absolute name
    * with no SO dependencies
    * @param path the string corresponding to the path to be checked
    * @return true if the path is absolute, false otherwise
    */
    public static boolean isAbsolute(String path) {
        int length = path.length();
        if (length == 0) {
            return false;
        }
	if (path.charAt(0) == '$') { return true;}
        if (path.charAt(0) == '\\') {
            if (path.indexOf("/") == -1) {
                return true;
            } else {
                return false;
            }
        }

        if (path.charAt(0) == '/') {
            if (path.indexOf("\\") == -1) {
                return true;
            } else {
                return false;
            }
        }

        if ((length >= 3) && Character.isLetter(path.charAt(0)) &&
                (path.charAt(1) == ':') && (path.charAt(2) == '\\') &&
                (path.indexOf("/") == -1)) {
            return true;
        }

        return false;
    }
}


// End Class  ExtractFiles
