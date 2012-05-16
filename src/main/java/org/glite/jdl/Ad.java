/*
* Ad.java
*
* Copyright (c) 2001 The European DataGrid Project - IST programme, all rights reserved.
*
*/
package org.glite.jdl;
import condor.classad.*;
import java.io.*; //Stream readers/writers
import java.text.ParseException;
import java.util.Iterator;
import java.util.Vector;
import javax.naming.directory.InvalidAttributeValueException;
/**
* This class is used to user-friendly represent and manipulate a condor classad
* it provides several utilities such us string and file constructor and manipulation of native classes
* (String, int, boolean) instead of classad Expression
*
* @version 0.1
* @author Alessandro Maraschini <alessandro.maraschini@datamat.it> */
public class Ad {
	/**
	* Default Constructor */
	public Ad() {
		jobAd = new RecordExpr();
	}
	/**Instantiates a Ad object from the given Ad-string
	* @param ad A String representing the Ad
	* @throws ParseException The Classad has JDL syntax error. Unable to build a valid Ad
	* @throws JobAdException   One or more attributes contain syntax error(s) */
	public Ad(String ad) throws ParseException, JobAdException {
		fromString(ad);
	}
	/**Instantiates a Ad object from the given Ad-string
	* @param ad A classAd Expression representing the Ad
	* @throws ParseException The Classad has JDL syntax error. Unable to build a valid Ad
	* @throws JobAdException   One or more attributes contain syntax error(s) */
	public Ad(RecordExpr ad) throws JobAdException {
		fromRecord(ad);
	}
	/** Copy all the attributes of the instance into a new Ad
	*@param re a classad recordExpr containing all the attributes to be copied from
	*@param target the Ad instance to be filled with the attributes
	* @throws JobAdException  all the error occurred while inserting the attributes
	* @return a new Ad with a copy of all the attributes contained in the passed RecordExpr instance */
	static protected void copy(RecordExpr re, Ad target) throws JobAdException {
		Iterator attrs = re.attributes();
		String excMessage = "";
		while (attrs.hasNext()) {
			String attrName = attrs.next().toString();
			try {
				target.setAttribute(attrName, re.lookup(attrName));
			} catch (Exception exc) {
				excMessage += ("\n- " + exc.getMessage());
			}
		}
		if (!excMessage.equals("")) {
			throw new JobAdException("JobAd:The following error(s) have been found while creating an instance:" + excMessage);
		}
	}
	/** Copy all the attributes of the instance into a new JobAd
	* @return a new Ad with a copy of all the attributes contained in the current instance*/
	public Object clone() {
		Ad target = new Ad();
		try {
			copy(jobAd, target);
		} catch (JobAdException exc) {
		// this exception shouldn't be reached in any case: do nothing
		}
		return target;
	}

		/*********************************************
		*            SET METHODS
		**********************************************
	/**  Insert an attribute of integer type
	* @param attrName the name of the attribute to be set
	* @param attrValue the value for the attribute to be set
	*/
	public void setAttribute(String attrName, int attrValue)throws InvalidAttributeValueException {
		setAttribute(attrName, Constant.getInstance(attrValue));
	}

	/** Insert an attribute of boolean type
	* @param attrName the name of the attribute to be set
	* @param attrValue the value for the attribute to be set
	*/
	public void setAttribute(String attrName, boolean attrValue)throws InvalidAttributeValueException {
		setAttribute(attrName, attrValue ? Constant.TRUE : Constant.FALSE);
	}

	/**Insert an attribute of double type
	* @param attrName the name of the attribute to be set
	* @param attrValue the value for the attribute to be set
	*/
	public void setAttribute(String attrName, double attrValue)throws InvalidAttributeValueException {
		setAttribute(attrName, Constant.getInstance(attrValue));
	}

	/**Insert an attribute of String type
	* @param attrName the name of the attribute to be set
	* @param attrValue the value for the attribute to be set
	*/
	public void setAttribute(String attrName, String attrValue)throws InvalidAttributeValueException {
		setAttribute(attrName, Constant.getInstance(attrValue));
	}
	/**Insert an attribute of Ad type
	* @param attrName the name of the attribute to be set
	* @param attrValue the value for the attribute to be set
	*/
	public void setAttribute(String attrName, Ad attrValue)throws InvalidAttributeValueException {
		setAttribute(attrName, attrValue.jobAd);
	}
	/**Insert an attribute of Expr type
	* @param attrName the name of the attribute to be set
	* @param attrValue the value for the attribute to be set
	*/
	public void setAttribute(String attrName, Expr attrValue) throws InvalidAttributeValueException, IllegalArgumentException {
		// Check for not allowed chars
		char[] notAllowed = { ' ', ':', '#', '@', '[', ']', '+', '*', '$', '%', '!', '?', '~' };
		for (int i = 0; i < notAllowed.length; i++) if (attrName.indexOf(notAllowed[i]) != -1) {
			throw new InvalidAttributeValueException(attrName + ": Not allowed char '" + notAllowed[i] + "' found");
		}
		jobAd.insertAttribute(attrName, attrValue);
	}
		/*********************************************
		*            ADD ATTRIBUTE METHODS
		**********************************************
	/**
	* Allow adding a value to an already set attribute of the JobAd instance
	* (i.e. it transforms it in a list attribute). if used on a non-set attribute the corresponding setAttribute method is automatically called.
	*  @param attrName a String representing the attribute name
	*  @param attrValue -  The value of the attribute to be added
	* @throws  IllegalArgumentException- The specified value  is not allowed for the attribute
	* @throws InvalidAttributeValueException - A value has not the right type for an attribute*/
	public void addAttribute(String attrName, Ad attrValue)throws IllegalArgumentException, InvalidAttributeValueException {
		addAttribute(attrName, attrValue.jobAd); //set, add or append the attribute to the previuos value
	}
	/**
	* Allow adding a value to an already set attribute of the JobAd instance
	* (i.e. it transforms it in a list attribute). if used on a non-set attribute the corresponding setAttribute method is automatically called.
	*  @param attrName a String representing the attribute name
	*  @param attrValue -  The value of the attribute to be added
	* @throws  IllegalArgumentException- The specified value  is not allowed for the attribute
	* @throws InvalidAttributeValueException - A value has not the right type for an attribute*/
	public void addAttribute(String attrName, int attrValue)throws IllegalArgumentException, InvalidAttributeValueException {
		addAttribute(attrName, Constant.getInstance(attrValue)); //set, add or append the attribute to the previuos value
	}
	/**
	* Allow adding a value to an already set attribute of the JobAd instance
	* (i.e. it transforms it in a list attribute). if used on a non-set attribute the corresponding setAttribute method is automatically called.
	*  @param attrName a String representing the attribute name
	*  @param attrValue -  The value of the attribute to be added
	* @throws  IllegalArgumentException- The specified value  is not allowed for the attribute
	* @throws InvalidAttributeValueException - the has not the right format for the attribute*/
	public void addAttribute(String attrName, double attrValue)throws IllegalArgumentException, InvalidAttributeValueException {
		addAttribute(attrName, Constant.getInstance(attrValue)); //set, add or append  to the previuos value
	}

	/**
	* Allow adding a value to an already set attribute of the JobAd instance
	* (i.e. it transforms it in a list attribute). if used on a non-set attribute the corresponding setAttribute method is automatically called.
	*  @param attrName a String representing the attribute name
	*  @param attrValue -  The value of the attribute to be added
	* @throws  IllegalArgumentException- The specified value  is not allowed for the attribute
	* @throws InvalidAttributeValueException - the value has not the right format for the attribute*/
	public void addAttribute(String attrName, boolean attrValue)throws IllegalArgumentException, InvalidAttributeValueException {
		if (attrValue) {
		addAttribute(attrName, Constant.TRUE);
		} else {
		addAttribute(attrName, Constant.FALSE); //set, add or append to the previuos value
		}
	}
	/**
	* Allow adding a value to an already set attribute of the JobAd instance
	* (i.e. it transforms it in a list attribute). if used on a non-set attribute the corresponding setAttribute method is automatically called.
	*  @param attrName a String representing the attribute name
	*  @param attrValue -  The value of the attribute to be added
	* @throws  IllegalArgumentException  The specified type  is not allowed for the attribute value
	* @throws InvalidAttributeValueException  The value has not the right format for the attribute*/
	public void addAttribute(String attrName, String attrValue)throws IllegalArgumentException, InvalidAttributeValueException {
		addAttribute(attrName, Constant.getInstance(attrValue)); //set, add or append to the previuos value
	}
	/**
	* Append a new Expression to an existing attribute value */
	private void addAttribute(String attrName, Expr attrValue)throws InvalidAttributeValueException, IllegalArgumentException {
		Expr previous = lookup(attrName); //Get the previous value (if present)
		if (previous == null) {
			// it's a new attribute:
			setAttribute(attrName, attrValue);
		} else if (previous.type == Expr.LIST) {
			//  It's a list of elements, append the new one:
			Expr valueToAppend = appendValue(attrValue, (ListExpr) previous);
			setAttribute(attrName, valueToAppend);
		} else if ((previous.type == Expr.BOOLEAN) ||
			(previous.type == Expr.REAL) ||
			(previous.type == Expr.INTEGER) ||
			(previous.type == Expr.RECORD) ||
			(previous.type == Expr.STRING)) {
			//  It's a single attribute, 2-value ListExpr must be created:
			Vector vect = new Vector();
			vect.add(previous);
			vect.add(attrValue);
			setAttribute(attrName, new ListExpr(vect));
		} else {
			throw new IllegalArgumentException(attrName + ": unexpected type");
		}
	}
		/*********************************************
		*            GET METHODS
		**********************************************
	/**
	* Retrieve the type of the specified attribute, if present
	*@param attrName the name of the attribute to look for
	*@return one of the following value: TYPE_UNKNOWN, TYPE_INTEGER , TYPE_BOOL, TYPE_STRING, TYPE_DOUBLE, TYPE_EXPRESSION, TYPE_AD
	* @throws NoSuchFieldException - Unable to find the attribute inside the Ad*/
	public int getType(String attrName) throws NoSuchFieldException {
		Expr expr = jobAd.lookup(attrName);
		if (expr == null) {
			throw new NoSuchFieldException(attrName + ": attribute has not been set");
		} else if (expr.type == Expr.LIST) {
			expr = ((ListExpr) expr).sub(0);
		}
		switch (expr.type) {
			case Expr.INTEGER:
			case Expr.BOOLEAN:
			case Expr.STRING:
			case Expr.REAL:
			case Expr.UNDEFINED:
			case Expr.RECORD:
				return expr.type;
			default:
				return TYPE_UNKNOWN;
		}
	}
	/**
	* Retreive the value of the specified attribute
	*  @param attrName The name of the attribute name to be retrieved
	* @return a Vector containing the values listed in the specified attribute ,  (1-size Vector if the attribute has a single value)
	* @throws IllegalArgumentException - The type of retrieved value is not allowed for the specified attribute name
	* @throws  NoSuchFieldException - The requested attribute has not been set yet*/
	public Vector getIntValue(String attrName)throws NoSuchFieldException, IllegalArgumentException {
		Expr value = lookup(attrName); //Get the value  //TBD for all "get<type>Value methods try and avoid the Expr variable
		return getValue(attrName, value, Expr.INTEGER);
	}
	/**
	* Retreive the value of the specified attribute
	*  @param attrName The name of the attribute  to be retrieved
	* @return a Vector containing the values listed in the specified attribute ,  (1-size Vector if the attribute has a single value) */
	public Vector getDoubleValue(String attrName)throws NoSuchFieldException, IllegalArgumentException {
		Expr value = lookup(attrName); //Get the value
		return getValue(attrName, value, Expr.REAL);
	}
	/**
	* Retreive the value of the specified attribute
	*  @param attrName The name of the attribute  to be retrieved
	* @return a Vector containing the values listed in the specified attribute ,  (1-size Vector if the attribute has a single value)
	* @throws IllegalArgumentException - The type of retrieved value is not allowed for the specified attribute name
	* @throws  NoSuchFieldException - The requested attribute has not been set yet**/
	public Vector getBooleanValue(String attrName)throws NoSuchFieldException, IllegalArgumentException {
		Expr value = lookup(attrName); //Get the value
		return getValue(attrName, value, Expr.BOOLEAN);
	}
	/**
	* Retreive the value of the specified attribute
	*  @param attrName The name of the attribute  to be retrieved
	* @return a Vector containing the values listed in the specified attribute ,  (1-size Vector if the attribute has a single value)
	* @throws IllegalArgumentException - The type of retrieved value is not allowed for the specified attribute name
	* @throws  NoSuchFieldException - The requested attribute has not been set yet**/
	public Vector getStringValue(String attrName)throws NoSuchFieldException, IllegalArgumentException {
		Expr value = lookup(attrName); //Get the value
		return getValue(attrName, value, Expr.STRING);
	}
	/**
	* Retreive the value of the specified attribute
	*  @param attrName The name of the attribute  to be retrieved
	* @return a Vector containing the values listed in the specified attribute ,  (1-size Vector if the attribute has a single value)
	* @throws IllegalArgumentException - The type of retrieved value is not allowed for the specified attribute name
	* @throws  NoSuchFieldException - The requested attribute has not been set yet**/
	public Vector getAdValue(String attrName) throws NoSuchFieldException, IllegalArgumentException {
		Expr value = lookup(attrName); //Get the value
		return getValue(attrName, value, Expr.RECORD);
	}
	/**
	* Retrieve the Ad
	* @param attrName the name of the attribute to be retrieved
	* @return an Ad instance representing the classad for the specified attribute
	* @throws IllegalArgumentException - The type of retrieved value is not allowed for the specified attribute name
	* @throws  NoSuchFieldException - The requested attribute has not been set yet**/
	public Ad getAd(String attrName) throws NoSuchFieldException, IllegalArgumentException {
		Ad result = new Ad();
		Expr value = lookup(attrName); //Get the value
		if (value == null) {
			throw new NoSuchFieldException(attrName + ": attribute has not beenset"); // The attribute has not been set yet
		} else if (value.type == Expr.RECORD) {
			result.jobAd = (RecordExpr) value; // uhm... TBD control
		} else {
			throw new IllegalArgumentException(attrName + ": attribute type is not an Ad instance"); //   Unexpected type
		}
		return result;
	}
	protected Object getConstant(String attrName, Expr value, int exprType) throws IllegalArgumentException, NoSuchFieldException {
		if (value.type == Expr.LIST) { //The passed value could be a sub-list
			return getValue(attrName, value, exprType); //Vector Value
		} else if (value.type != exprType) {
			throw new IllegalArgumentException(attrName + ": Requested type doesn't match with value type found.");
		}
		if (exprType == Expr.RECORD) {
			Ad ad = new Ad();
			ad.jobAd = (RecordExpr) value; //  put the right casting TBD put a valid constructor
			return ad;
		} else {
			Constant co = (Constant) value;
			if (exprType == Expr.INTEGER) {
				return new Integer(co.intValue()); //  Integer Value
			} else if (exprType == Expr.BOOLEAN) {
				return new Boolean(co.isTrue()); //  Boolean Value
			} else if (exprType == Expr.REAL) {
				return new Double(co.realValue()); //  Real Value
			} else if (exprType == Expr.STRING) {
				return co.stringValue(); //  String Value
			} else {
				throw new IllegalArgumentException(attrName + ": unexpected type found.");
			}
		}
	}
	/** get the value(s) from an Expression and return it/them as a Vector of Expr*/
	protected Vector getValue(String attrName, Expr value, int exprType) throws NoSuchFieldException, IllegalArgumentException {
		Vector vect = new Vector();
		if (value == null) {
			throw new NoSuchFieldException(attrName + ": attribute has not been set");
		} else if (value.type == Expr.LIST) {
			//  It's a list of elements, put them in a vector
			ListExpr le = (ListExpr) value;
			for (int i = 0; i < le.size(); i++) {
				vect.add(getConstant(attrName, le.sub(i), exprType));
			}
		} else {
			vect.add(getConstant(attrName, value, exprType));
		}
		return vect;
	}
		/***********************************************
		* MISCELLANEOUS
		* lookup
		* attributes
		* hasAttribute
		* isSet
		* size
		* delAttribute
		************************************************/
	/**
	* Find the attribute with the given name.
	* @param name - the attribute name to look for
	* @return the value of the attribute (null if not present) */
	public Expr lookup(String attrName) {
		return jobAd.lookup(attrName);
	}
	/**
	* Enumerate the attribute names.
	@return an iterator of objects of type String, representing the the attribute names.  */
	public Iterator attributes() {
		return jobAd.attributes();
	}
	/**
	* Check If the specified attribute has already been set
	* @param attrName The name of the attibute to be looked for
	* @return true if the attribute has been found, false otherwise */
	public boolean hasAttribute(String attrName) {
		return (lookup(attrName) != null);
	}
	/**
	* Check whether the JobAd has been initialised (true) or not (false)*/
	public boolean isSet() {
		return (jobAd.size() != 0);
	}
	/**
	* Return the number of attributes that have been inserted so far
	* @return the size of the JobAd */
	public int size() {
		return jobAd.size();
	}
	/**
	* Reset the JobAd Instance. All the previous existing attributes will be deleted  */
	public void clear() {
		jobAd = new RecordExpr();
	}
	/**
	* Remove the specified attribute from the Ad instance
	*@param attrName the attribute to be retrieved
	@throws NoSuchFieldException The attribute is not present   */
	public void delAttribute(String attrName) throws NoSuchFieldException {
		if (jobAd.lookup(attrName) == null) {
			throw new NoSuchFieldException(attrName +": attribute not set, unable to remove"); // The attribute has not been set yet
		}
		jobAd.removeAttribute(AttrName.fromText(attrName));
	}
	/**
	* Append a value to a list and return the corresponding Expression List */
	protected ListExpr appendValue(Expr attrValue, ListExpr list) {
		Vector vect = new Vector();
		for (int i = 0; i < list.size(); i++) vect.add(list.sub(i));
		vect.add(attrValue);
		return new ListExpr(vect);
	}
		/***********************************************
		* FROM-TO  METHODS
		* fromFile
		* fromString
		* toString
		************************************************/
	/**Retrieve the JobAd from the specified file path
	* @param path the path of the file from where the jobAd is supposed to be read
	* @throws ParseException - The string doesn't seem to be a valid classad
	* @throws JobAdException -  One or more attributes contain syntax error(s)
	* @throws FileNotFoundException the specified path does not exist*/
	public void fromFile(String path) throws Exception {
		BufferedReader in = new BufferedReader(new FileReader(path));
		String result;
		String tmp;
		result = new String();
		tmp = new String();
		while (((tmp = in.readLine()) != null)) {
			tmp = tmp.trim();
			if ((tmp.startsWith("#")) || (tmp.startsWith("//"))) {
				// It's a comment, do nothing
			} else {
				// It's not a comment, add to parse
				result += (tmp + " ");
			}
		}
		in.close();
		fromString(result);
	}
	/** Convert the JobAd Instance into a sinlge-line String representation
	* This method is equivalent as toString (false , false )
	* @see  #toString(boolean multiLines , boolean multiLists)  */
	public String toString() {
		return toString(false, false);
	}
	/** Convert the JobAd Instance into its String representation, one line per attribute, multi line for listed attribute active
	* @param multiLines one-attribute per line representation enablng
	* @param multiLists list attributes splitted into multi line representation enablng
	* @see  #toString()  */
	public String toString(boolean multiLines, boolean multiLists) {
		StringWriter result = new StringWriter();
		ClassAdWriter caWriter = new ClassAdWriter(result);
		caWriter.setFormatFlags(0);
		if (multiLines) {
			caWriter.enableFormatFlags(ClassAdWriter.MULTI_LINE_ADS);
		}
		if (multiLists) {
			caWriter.enableFormatFlags(ClassAdWriter.MULTI_LINE_LISTS);
		}
		caWriter.print(jobAd);
		caWriter.setFormatFlags(0);
		return result.toString();
	}

	/**
	* Load the classad from a classad Expression and create a new Ad instance
	* @param re the expression to load the Ad from
	* @throws JobAdException if unable to load any of the classad expression attribute
	*/
	public void fromRecord(RecordExpr re)throws JobAdException{
		Iterator it = re.attributes();
		String finalExceptionMessage = "";
		jobAd = new RecordExpr();
		while (it.hasNext()) {
			String attrName = it.next().toString();
			try {
				setAttribute(attrName, re.lookup(attrName));
			} catch (Exception exc) {
				finalExceptionMessage += exc.getMessage();
			}
		}
		if (finalExceptionMessage.length() != 0) {
			throw new JobAdException(finalExceptionMessage);
		}
	}
	/**
	* Update and load the Ad object with the given ClassAd-jdl String
	* @param  jdl A String representig the description of the job
	* @throws ParseException  The string doesn't seem to be a valid classad
	* @throws JobAdException   One or more attributes contain syntax error(s) */
	public void fromString(String jdl) throws ParseException, JobAdException {
		jdl = jdl.trim();
		// ClassAdParser bug override (TBD needed with new classad??)
		jdl = parseStringValue(jdl);
		// Append parenthesis (if  needed)
		if (!jdl.startsWith("[")) { 
			jdl = "[ " + jdl + "]"; 
		}
		ClassAdParser cp = new ClassAdParser(jdl);
		Expr ex = cp.parse();
		if (ex == null) {
			throw new ParseException("Unable to parse: doesn't seem to be a valid Expression", 1);
		} else if (ex.type != Expr.RECORD) {
			throw new ParseException("Unable to parse: the parsed expression is not a ClassAd",1);
		}
		// Parse The Attributes
		RecordExpr re = (RecordExpr) ex;
		ex = null;
		fromRecord(re);
	}
		/***********************************************************************
		*  PARSE-UNPARSE METHODS
		************************************************************************/
	static String parseStringValue(String value) {
		int ind = 0;
		ind = value.indexOf("\\");
		while (ind != -1) {
			value = value.substring(0, ind) + ("\\\\") +
				value.substring(ind + 1);
			ind = value.indexOf("\\", ind + 2);
		}
		ind = value.indexOf("\\\"");
		while (ind != -1) {
			value = value.substring(0, ind) + value.substring(ind + 1);
			ind = value.indexOf("\\\"", ind + 1);
		}
		return value;
	}
	static String unparseStringValue(String jdl) {
		int index = jdl.indexOf("\\\\");
		while (index != -1) {
		jdl = jdl.substring(0, index) + jdl.substring(index + 1);
		index = jdl.indexOf("\\\\", index + 1);
		}
		return jdl;
	}
		/*********************************************
		*            PUBLIC MEMBERS
		**********************************************/
	/**unknown Attribute value
	@see #getType*/
	public static final int TYPE_UNKNOWN = Expr.ERROR;
	/**Attribute Integer type value
	@see #getType*/
	public static final int TYPE_INTEGER = Expr.INTEGER;
	/**Attribute Boolean type value
	@see #getType*/
	public static final int TYPE_BOOL = Expr.BOOLEAN;
	/**Attribute String type value
	@see #getType*/
	public static final int TYPE_STRING = Expr.STRING;
	/**Attribute Real/Double type value
	@see #getType*/
	public static final int TYPE_REAL = Expr.REAL;
	/**Attribute Ad type value
	@see #getType*/
	public static final int TYPE_AD = Expr.RECORD;
	/**Attribute Expression type value
	@see #getType*/
	public static final int TYPE_EXPRESSION = Expr.UNDEFINED;
		/*********************************************
		*            PRIVATE - PROTECTED MEMBERS
		**********************************************/
	/** The internal Ad representation */
	protected RecordExpr jobAd;
	private String finalExceptionMessage;
}  // end class

