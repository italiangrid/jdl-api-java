/*
* JobAdSchema.java
*
* Copyright (c) 2001 The European DataGrid Project - IST programme, all rights reserved.
*
* Contributors are mentioned in the code there appropriate.
*
*/
package org.glite.jdl;
/**
 * Provides a Schema used to check the attribute in the JobAd
 * The default utilised schema is GLUE. To instanciate a schema you
 * will need to provide an array of Strings, one per attribute
 * Known used schema so far: EDG (deprecated) , GLUE (Default used schema)
 * @version 0.1
 * @author Alessandro Maraschini <alessandro.maraschini@datamat.it>
*/
public class JobAdSchema {
	/** CUSTOM schema constructor
	* @param values an array of string of #SCHEMA_ARRAY dimension
	*throw ArrayIndexOutOfBoundsException the length of value must be of the exact dimension
	*@see #SCHEMA_ARRAY */
	public JobAdSchema(String[] values) throws ArrayIndexOutOfBoundsException {
		// chek the dimension:
		if (values.length != SCHEMA_ARRAY) {
			throw new ArrayIndexOutOfBoundsException("JobAd Schema constructor dimension must be " + SCHEMA_ARRAY);
		}
		this.values = values;
	}
	/** GLUE schema static constructor  (JobAd default utilised schema)*/
	static public JobAdSchema glueSchema() {
		String[] values = {
			"other.dataAccessCost",
			"other.GlueHostApplicationSoftwareRunTimeEnvironment",
			"other.GlueCEInfoTotalCPUs", "other.GlueCEStateFreeCPUs",
			"other.GlueHostNetworkAdapterOutboundIP"
		};
		return new JobAdSchema(values);
	}
	/** Create and return a deep copy of the current schema */
	public Object clone() {
		return new JobAdSchema(values);
	}
	/**
	* Retrieve a default value from the schema
	* @param attrName refers to the attributes of the Schema, must be less than #SCHEMA_ARRAY
	* @see #SCHEMA_ARRAY
	* @return the String value of the specified attribute
	*/
	public String get(int attrName) throws ArrayIndexOutOfBoundsException {
		if (attrName >= SCHEMA_ARRAY) {
			throw new ArrayIndexOutOfBoundsException( "JobAd Schema values must be in the range 0-" + (SCHEMA_ARRAY - 1));
		}
		return values[attrName];
	}
	/** other.dataAccessCost attribute */
	static final int SCHEMA_DAC = 0;
	/** other.GlueHostApplicationSoftwareRunTimeEnvironment attribute.
	* Appended for MPI jobs in Requirements expression */
	static final int SCHEMA_RTE = 1;
	/** other.GlueCEInfoTotalCPUs attribute.
	* Appended for MPI jobs in Requirements expression */
	static final int SCHEMA_TCPU = 2;
	/**  other.GlueCEStateFreeCPUs attribute
	* Set for MPI jobs as a default rank (if not given) */
	static final int SCHEMA_FCPU = 3;
	/** other.GlueHostNetworkAdapterOutboundIP
	* Added for  Interactive Jobs in requirements expression*/
	static final int SCHEMA_OIP = 4;
	/*** Array dimension for Schema Attributes values
	@see #get(int)  */
	public static final int SCHEMA_ARRAY = 5;
	private String[] values;
} // End class

