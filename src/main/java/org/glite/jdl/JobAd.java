/*
* JobAd.java
*
* Copyright (c) 2001 The European DataGrid Project - IST programme, all rights reserved.
*
* Contributors are mentioned in the code there appropriate.
*
*/
package org.glite.jdl;
import condor.classad.*;
import java.io.*; //Stream readers/writers
import java.lang.*; // primitive class
import java.text.ParseException;
import java.util.*;
import java.util.regex.*; // extract files
import javax.naming.directory.InvalidAttributeValueException;
/**
  * Provides a representation of the job description in the JDL language
 * and the functions for building and manipulating it. Basically the
 * JDL is the Condor ClassAd language, so it is legitimate the direct
 * use of the Condor API library for creating, modifying, deleting a
 * job description. However the JobAd class extends the ClassAd class
 * of the Condor ClassAd library additionally providing some helper methods
 * that ease the construction of job descriptions being fully compliant to
 * WP1 WMS specification.
 *
 * @version 0.1
 * @author Alessandro Maraschini <alessandro.maraschini@datamat.it>
*/
public class JobAd extends Ad {
	/** Default Constructor Instantiates an  empty  JobAd object */
	public JobAd() {
		super();
		if (user==null){user=new RecordExpr();}
	}
	/** Constructor from String: parse the string and create a JobAd
	* @param ad the JDL string to be parsed  */
	public JobAd(String ad) throws ParseException, JobAdException {
		super(ad);
		if (user==null){user=new RecordExpr();}
	}
	/**Instantiates a JobAd object from the given recordExpr
	* @param ad classAd Expression representing the Ad
	* @throws ParseException The Classad has JDL syntax error. Unable to build a valid Ad
	* @throws JobAdException   One or more attributes contain syntax error(s) */
	public JobAd(RecordExpr ad) throws JobAdException {
		super(ad);
		if (user==null){user=new RecordExpr();}
	}
	/***
	* Make a deep copy of the JobAd instance
	* @param source the expression to copy from
	*/
	public JobAd copy(RecordExpr source) throws JobAdException {
		JobAd result = new JobAd();
		copy(source, result);
		return result;
	}
	/** Copy all the attributes of the instance into a new JobAd
	* @return a new JobAd with a copy of all the attributes contained in the current JobAd instance*/
	public Object clone() {
		JobAd target = new JobAd();
		try {
			copy(jobAd, target);
		} catch (JobAdException exc) {
			// this exception shouldn't be reached in any case: do nothing
		}
		// Initialising default value
		if (schema != null) {
			target.schema = (JobAdSchema) (schema.clone());
		}
		return target;
	}
	/**
	* Copy the attributes of the classAd into a new Record Expression
	*@return the condor Excpression representing the Ad instance*/
	public RecordExpr copyAd() {
		Iterator attrs = attributes();
		RecordExpr re = new RecordExpr();
		while (attrs.hasNext()) {
			String attrName = attrs.next().toString();
			re.insertAttribute(attrName, jobAd.lookup(attrName));
		}
		return re;
	}

	/**
	* Check wheater an atribute has the value between its values. The search is
	* @param attrName the name of the attribute to be checked
	* @param attrValue the String value to search for. The case of the seek is unsensitive
	*@return true if the specified value is present, false otherwise (also when the attribute is no set) */
	public boolean hasAttribute(String attrName, String attrValue) {
		return hasAttribute(attrName, attrValue, true);
	}
	/**
	* Check wheater an atribute has the value between its values. The search is
	* @param attrName the name of the attribute to be checked
	* @param attrValue the String value to search for
	* @param determine wheater the case of the value should be sensitive or unsensitive
	*@return true if the specified value is present, false otherwise (also when the attribute is no set) */
	public boolean hasAttribute(String attrName, String attrValue, boolean unsensitive) {
		try {
			Vector vect = getStringValue(attrName);
			for (int i = 0; i < vect.size(); i++) {
				String tmpValue = (String) vect.get(i);
				if (unsensitive) {
					if (tmpValue.toLowerCase().equals(attrValue.toLowerCase())) {
						return true;
					}
				} else if (tmpValue.equals(attrValue)) {
					return true;
				}
			}
		} catch (Exception exc) { // Unable to find the attribute
			return false;
		}
		return false;
	}

	/** Convert the JobAd Instance into its String representation, one line per attribute, multi line for listed attribute active
	* This method is the same as toString ( true , true )
	* @return The JobAd representation into multi line
	* @see  #toSubmissionString()
	* @see  #toString()  */
	public String toLines() {
		return toString(true, true);
	}

	/** Convert the JobAd Instance into its String representation, one line per attribute, multi line for listed attribute active
	* @param multiLines one-attribute per line representation enablng
	* @param multiLists list attributes splitted into multi line representation enablng
	* @see  #toSubmissionString()
	* @see  #toString()  */
	public String toString(boolean multiLines, boolean multiLists) {
		return super.toString(multiLines, multiLists);
	}

	/** Perform a check over the JobAd instance and if possible convert it into its String representation as it would be ready for a submission
	* @throws JobAdException - one or more values do not  match with jobad semantic rule
	* @see  #toString()
	* @see  #toLines()  */
	public String toSubmissionString() throws JobAdException {
		String[] attributes = {  };
		check(attributes);
		String result = super.toString(false, false);
		restore();
		return result;
	}

	/** Convert the JobAd Instance into a sinlge-line String representation
	* @see  #toSubmissionString()
	* @see  #toLines()  */
	public String toString() {
		return super.toString(false, false);
	}

	/**
	*Print the JobAd instance into the specified file, with its multi-lines representation
	* @param filePath where to write the JobAd
	* @see #toLines()
	* @throws IOException - if unable to write the specified file  */
	public void toFile(String filePath) throws Exception {
		Iterator it = user.attributes();
		// Remove the attributes that might have been changed by previous check
		// and recover the previous values (if present)
		DataOutputStream out = new DataOutputStream(new BufferedOutputStream( new FileOutputStream(filePath)));
		out.writeBytes(toLines());
		out.close();
	}
	/**
	* If JobAd is used inside an applet, it is impossible to look into the local hard-disk
	* by default this parameter is set to TRUE
	* @param lookInto allow all the check methods to access to the local hard disk (true) or skip the check (false) */
	public void setLocalAccess(boolean lookInto) {
		localAccess = lookInto;
	}


	void insertAttribute(String attrName, Expr attrValue) throws InvalidAttributeValueException {
		super.setAttribute(attrName, attrValue);
		if (user == null) {
			user = new RecordExpr();
		}
		if (Jdl.findAttribute(attrName, Jdl.changeAttributes) && !checking) {
			user.insertAttribute(attrName, attrValue);
		}
	}

	/**
	* Add The specified Expression Attribute to the jdl istance
	* @param  attrName - The Name of the attribute to be added
	* @param  attrValue - The Expression of the value to be added
	* @throws IllegalArgumentException  The type of value is not allowed for the specified attribute name
	* @throws InvalidAttributeValueException  The value has not the right format for the specified attribute*/
	public void setAttribute(String attrName, Expr attrValue) throws InvalidAttributeValueException, IllegalArgumentException {
		if (lookup(attrName) != null) {
			throw new IllegalArgumentException(attrName + ": already set attribute");
		}
		if (Jdl.find(attrName)) {
			if (attrValue.type == Expr.LIST) {
				if (!Jdl.findAttribute(attrName, Jdl.listAttributes)) {
					throw new IllegalArgumentException(attrName + ": List not allowed"); //   Unexpected type
				}
				// cycle the checking over the list
				ListExpr list = (ListExpr) attrValue;
				for (int j = 0; j < list.size(); j++) checkSyntax(attrName, (Expr) list.sub(j));
			} else {
				// check if  the couple is Ok
				checkSyntax(attrName, attrValue);
			}
		}
		insertAttribute(attrName, attrValue);
	}
	/**
	* Add The specified Expression Attribute to the jdl istance
	* @param  attrName - The Name of the attribute to be added
	* @param  attrValue - The value of the attribute to be added
	* @throws IllegalArgumentException - The type of value is not allowed for the specified attribute name
	* @throws InvalidAttributeValueException - the value has not the right format for the specifiedattribute  */
	public void setAttributeExpr(String attrName, String attrValue)throws IllegalArgumentException, InvalidAttributeValueException {
		// chek the couple:
		checkAttributeExpr(attrName, attrValue);
		// Check If already Exists
		Expr previous = lookup(attrName);
		if (previous != null) {
			throw new IllegalArgumentException(attrName +": already set attribute");
		}
		// ClassAdParser bug override
		attrValue = parseStringValue(attrValue);
		ClassAdParser cp = new ClassAdParser(attrValue);
		Expr val = cp.parse();
		insertAttribute(attrName, val);
	}
	/**
	* Add The specified Integer Attribute to the jdl istance
	* @param  attrName - The Name of the attribute to be added
	* @param  attrValue - The value of the attribute to be added
	* @throws IllegalArgumentException - The type of value is not allowed for the specified attribute name
	* @throws InvalidAttributeValueException - The specified value is out of limits for the specified attribute*/
	public void setAttribute(String attrName, int attrValue)throws IllegalArgumentException, InvalidAttributeValueException {
		// chek the couple:
		checkAttribute(attrName, attrValue);
		// Check If already Exists
		Expr previous = lookup(attrName);
		if (previous != null) {
			throw new IllegalArgumentException(attrName +": already set attribute");
		}
		// Set the attribute
		Expr val = Constant.getInstance(attrValue);
		insertAttribute(attrName, val);
	}
	/**
	* Add The specified String Attribute to the jdl istance
	* @param  attrName - The Name of the attribute to be added
	* @param  attrValue - The value of the attribute to be added
	* @throws IllegalArgumentException - The type of value is not allowed for the specified attribute name
	* @throws InvalidAttributeValueException - the value has not the right format for the specified attribute*/
	public void setAttribute(String attrName, String attrValue)throws IllegalArgumentException, InvalidAttributeValueException {
		// chek the couple:
		checkAttribute(attrName, attrValue);
		// Check If already Exists
		Expr previous = lookup(attrName);
		if (previous != null) {
			throw new IllegalArgumentException(attrName + ": already set attribute");
		}
		// Case Insensitive rendering:
		if ((attrName.equals(Jdl.JOBTYPE)) || (attrName.equals(Jdl.TYPE)) || (attrName.equals(Jdl.VIRTUAL_ORGANISATION))) {
			attrValue = attrValue.toLowerCase();
		}
		// Set the attribute
		Expr val = Constant.getInstance(attrValue);
		insertAttribute(attrName, val);
	}
	/**
	* Add The specified String Attribute to the jdl istance
	* @param  attrName - The Name of the attribute to be added
	* @param  attrValue - The value of the attribute to be added
	* @throws  IllegalArgumentException- The attribute  attrName had been
	* @throws InvalidAttributeValueException - the value is out of limits for the specified attribute*/
	public void setAttribute(String attrName, double attrValue)throws IllegalArgumentException, InvalidAttributeValueException {
		// chek the couple:
		checkAttribute(attrName, attrValue);
		// Check If already Exists
		Expr previous = lookup(attrName);
		if (previous != null) {
			throw new IllegalArgumentException(attrName +": already set attribute");
		}
		// Set the attribute
		Expr val = Constant.getInstance(attrValue);
		insertAttribute(attrName, val);
	}
	/**
	* Add The specified String Attribute to the jdl istance
	* @param  attrName - The Name of the attribute to be added
	* @param  attrValue - The value of the attribute to be added
	* @throws  IllegalArgumentException- The attribute  attrName had been already set
	* @throws InvalidAttributeValueException - A value has not the right type for an attribute*/
	public void setAttribute(String attrName, boolean attrValue)throws IllegalArgumentException, InvalidAttributeValueException {
		// chek the couple:
		checkAttribute(attrName, attrValue);
		// Check If already Exists
		Expr previous = lookup(attrName);
		if (previous != null) {
			throw new IllegalArgumentException(attrName + ": already set");
		}
		// Set the attribute
		if (attrValue) {
			insertAttribute(attrName, Constant.TRUE);
		} else {
			insertAttribute(attrName, Constant.FALSE); //set, add or append to the previuos value
		}
	}

	/**
	* Allow adding a value to an already set attribute of the JobAd instance
	* (i.e. it transforms it in a list attribute). if used on a non-set attribute the corresponding setAttribute method is automatically called.
	*  @param attrName a String representing the attribute name
	*  @param attrValue -  The value of the attribute to be added
	* @throws  IllegalArgumentException- The specified value  is not allowed for the attribute
	* @throws InvalidAttributeValueException - A value has not the right type for an attribute*/
	public void addAttribute(String attrName, Ad attrValue) throws IllegalArgumentException, InvalidAttributeValueException {
		// chek the couple:
		checkAttribute(attrName, attrValue);
		//Create the expression to be appended
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
		// chek the couple:
		checkAttribute(attrName, attrValue);
		//Create the expression to be appended
		Expr val = Constant.getInstance(attrValue);
		addAttribute(attrName, val); //set, add or append the attribute to the previuos value
	}

	/**
	* Allow adding a value to an already set attribute of the JobAd instance
	* (i.e. it transforms it in a list attribute). if used on a non-set attribute the corresponding setAttribute method is automatically called.
	*  @param attrName a String representing the attribute name
	*  @param attrValue -  The value of the attribute to be added
	* @throws  IllegalArgumentException- The specified value  is not allowed for the attribute
	* @throws InvalidAttributeValueException - the has not the right format for the attribute*/
	public void addAttribute(String attrName, double attrValue)throws IllegalArgumentException, InvalidAttributeValueException {
		// chek the couple:
		checkAttribute(attrName, attrValue);
		//Create the expression to be appended
		Expr val = Constant.getInstance(attrValue);
		addAttribute(attrName, val); //set, add or append  to the previuos value
	}

	/**
	* Allow adding a value to an already set attribute of the JobAd instance
	* (i.e. it transforms it in a list attribute). if used on a non-set attribute the corresponding setAttribute method is automatically called.
	*  @param attrName a String representing the attribute name
	*  @param attrValue -  The value of the attribute to be added
	* @throws  IllegalArgumentException- The specified value  is not allowed for the attribute
	* @throws InvalidAttributeValueException - the value has not the right format for the attribute*/
	public void addAttribute(String attrName, boolean attrValue)throws IllegalArgumentException, InvalidAttributeValueException {
		// chek the couple:
		checkAttribute(attrName, attrValue);
		//Create the expression to be appended
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
		// chek the couple:
		checkAttribute(attrName, attrValue);
		//Create the expression to be appended
		if ((attrName.equals(Jdl.JOBTYPE)) || (attrName.equals(Jdl.TYPE)) ||
			(attrName.equals(Jdl.VIRTUAL_ORGANISATION))) {
		attrValue = attrValue.toLowerCase();
		}
		Expr val = Constant.getInstance(attrValue);
		addAttribute(attrName, val); //set, add or append to the previuos value
	}


	/**
	* Append a new Expression to an existing attribute value */
	private void addAttribute(String attrName, Expr attrValue)throws InvalidAttributeValueException, IllegalArgumentException{
		Expr previous = lookup(attrName); //Get the previous value (if present)
		if (previous == null) {
			// it's a new attribute:
			insertAttribute(attrName, attrValue);
		} else if (previous.type == Expr.LIST) {
			if (!Jdl.findAttribute(attrName, Jdl.listAttributes) &&Jdl.find(attrName)) {
				throw new InvalidAttributeValueException(attrName +": List not allowed"); //   Unexpected type
			}
			//  It's a list of elements, append the new one:
			Expr valueToAppend = appendValue(attrValue, (ListExpr) previous);
			insertAttribute(attrName, valueToAppend);
		} else if ((previous.type == Expr.BOOLEAN) ||
		(previous.type == Expr.REAL) ||
		(previous.type == Expr.INTEGER) ||
		(previous.type == Expr.RECORD) ||
		(previous.type == Expr.STRING)) {
			//  It's a single attribute, 2-value ListExpr must be created:
			if (!Jdl.findAttribute(attrName, Jdl.listAttributes) &&
				Jdl.find(attrName)) {
				throw new InvalidAttributeValueException(attrName +": List not allowed"); //   Unexpected type
			}
			Vector vect = new Vector();
			vect.add(previous);
			vect.add(attrValue);
			insertAttribute(attrName, new ListExpr(vect));
		} else {
			throw new IllegalArgumentException(attrName + ": unexpected type");
		}
	}

	/**
	* Retreive the value of the specified Expression attribute as a string
	*  @param attrName The name of the attribute name to be retrieved
	* @return the String representng the value of the specified attribute
	* @throws InvalidAttributeValueException -  Not an Expression value is allowed for the specified attribute
	* @throws NoSuchFieldException - The attribute is not present in the JobAd*/
	public String getAttributeExpr(String attrName) throws Exception {  //TBD Exception thrown!!!
		Expr result = lookup(attrName);
		if (Jdl.find(attrName) && !Jdl.findExpr(attrName)) {
		throw new InvalidAttributeValueException(attrName + ": expression value not allowed"); //   Unexpected type
		}
		if (result == null) {
			throw new NoSuchFieldException(attrName + ": attribute has not been set");
		}
		return result.toString();
	}
	/**
	* Retreive the JobAd value of the specified attribute
	*  @param attrName The name of the attribute name to be retrieved
	* @return a Vector cantaining the values listed in the specified attribute ,  (1-size Vector if the attribute has a single value)
	* @throws nvalidAttributeValueException The specified attribute is of list type
	* @throws IllegalArgumentException - The type of retrieved value is not allowed for the specified attribute name
	* @throws  NoSuchFieldException - The requested attribute has not been set yet*/
	public JobAd getJobAdValue(String attrName)throws NoSuchFieldException, IllegalArgumentException, JobAdException {
		Expr value = lookup(attrName); //Get the value
		if (value == null) {
			throw new NoSuchFieldException(attrName +": attribute has not beenset"); // The attribute has not been set yet
		} else if (value.type == Expr.RECORD) {
			JobAd result = new JobAd();
			copy((RecordExpr) value, result);
			return result;
		} else {
			throw new IllegalArgumentException(attrName + ": attribute type is not a JobAd instance"); //   Unexpected type
		}
	}
	/**
	* Retrieve the value of the specified attribute, only if it is of non-list type
	* @param attrName The name of the attribute  to be retrieved
	* @see #getStringValue
	* @return the value of the specified aqttribute as in the JobAd
	* @throws nvalidAttributeValueException The specified attribute is of list type
	* @throws IllegalArgumentException - The type of retrieved value is not allowed for the specified attribute name
	* @throws  NoSuchFieldException - The requested attribute has not been set yet*/
	public String getString(String attrName) throws NoSuchFieldException, IllegalArgumentException,InvalidAttributeValueException {
		if (Jdl.findAttribute(attrName, Jdl.listAttributes)) {
			throw new InvalidAttributeValueException(attrName +": List not allowed, please use getStringValue method"); //   Unexpected type
		} else {
			return (String) getStringValue(attrName).get(0);
		}
	}
	/**
	* Retrieve the value of the specified attribute, only if it is of non-list type
	* @param attrName The name of the attribute  to be retrieved
	* @return the int value found inside the JobAd
	* @see #getIntValue
	* @return the value of the specified aqttribute as in the JobAd
	* @throws InvalidAttributeValueException The specified attribute is of list type
	* @throws IllegalArgumentException - The type of retrieved value is not allowed for the specified attribute name
	* @throws  NoSuchFieldException - The requested attribute has not been set yet*/
	public int getInt(String attrName)throws NoSuchFieldException, IllegalArgumentException, InvalidAttributeValueException {
			if (Jdl.findAttribute(attrName, Jdl.listAttributes)) {
				throw new InvalidAttributeValueException(attrName +": List not allowed, please use getIntValue method"); //   Unexpected type
			} else {
				return ((Integer) getIntValue(attrName).get(0)).intValue();
			}
	}

	/**
	* Retrieve the value of the specified attribute, only if it is of non-list type
	* @param attrName The name of the attribute  to be retrieved
	* @return the boolean value found inside the JobAd
	* @see #getBooleanValue
	* @return the value of the specified aqttribute as in the JobAd
	* @throws InvalidAttributeValueException The specified attribute is of list type
	* @throws IllegalArgumentException - The type of retrieved value is not allowed for the specified attribute name
	* @throws  NoSuchFieldException - The requested attribute has not been set yet*/
	public boolean getBoolean(String attrName)throws NoSuchFieldException, IllegalArgumentException,InvalidAttributeValueException {
		if (Jdl.findAttribute(attrName, Jdl.listAttributes)) {
			throw new InvalidAttributeValueException(attrName + ": List not allowed, please use getBooleanValue method"); //   Unexpected type
		} else {
			return ((Boolean) getIntValue(attrName).get(0)).booleanValue();
		}
	}

	/**
	* Retrieve the value of the specified attribute, only if it is of non-list type
	* @param attrName The name of the attribute  to be retrieved
	* @return the double value of the specified aqttribute as in the JobAd
	* @see #getDoubleValue
	* @throws InvalidAttributeValueException The specified attribute is of list type
	* @throws IllegalArgumentException - The type of retrieved value is not allowed for the specified attribute name
	* @throws  NoSuchFieldException - The requested attribute has not been set yet*/
	public double getDouble(String attrName) throws NoSuchFieldException, IllegalArgumentException,InvalidAttributeValueException {
		if (Jdl.findAttribute(attrName, Jdl.listAttributes)) {
			throw new InvalidAttributeValueException(attrName +": List not allowed, please use getDoubleValue method"); //   Unexpected type
		} else {
			return ((Double) getIntValue(attrName).get(0)).doubleValue();
		}
	}
	/**
	* Delete an Attribute. It fails if the attribute doesn't exist
	* @param attrName The name of the attibute to be deleted
	* @throws NoSuchFieldException - The attribute has not been set yet  */
	public void delAttribute(String attrName) throws NoSuchFieldException {
		if (hasAttribute(attrName)) {
			//remove patch fixed
			Iterator attributes = jobAd.attributes();
			while (attributes.hasNext()) {
				String attr = attributes.next().toString();
				if (attrName.toLowerCase().equals(attr.toLowerCase())) {
					attrName = new String(attr);
					break;
				}
			}
			jobAd.removeAttribute(AttrName.fromString(attrName));
			if ((user.lookup(attrName) != null) && !checking) {
				user.removeAttribute(AttrName.fromString(attrName));
			}
		} else {
			throw new NoSuchFieldException(attrName + ": attribute not set, unable to remove"); // The attribute has not been set yet
		}
	}
	/**
	* Reset the JobAd Instance. All the previous existing attributes will be deleted  */
	public void clear() {
		jobAd = new RecordExpr();
		user = new RecordExpr();
	}

	/**
	* restore the values set by the user, discard JobAd self changes */
	private void restore() {
		Iterator it = user.attributes();
		// Remove the attributes that might have been changed by previous check
		// and recover the previous values (if present)
		while (it.hasNext()) {
			String attrName = it.next().toString();
			try {
				jobAd.removeAttribute(AttrName.fromText(attrName));
			} catch (Exception exc) {
				/* do Nothing */
			}
			jobAd.insertAttribute(attrName, user.lookup(attrName));
		}
		/** Special Attributes: if a previous check has been made some attribute could have initialised **/
		String[] specials = { Jdl.DEFAULT_RANK, Jdl.ENVIRONMENT, Jdl.TYPE, Jdl.JOBTYPE }; //TBD special defined elsewhere???!?!?!?
		for (int i = 0; i < specials.length; i++)
		if (user.lookup(specials[i]) == null) {
			try {
				jobAd.removeAttribute(AttrName.fromText(specials[i]));
			} catch (Exception exc) {
				/* do Nothing */
			}
		}
	}

	/**
	* Check the JobAd instance for both syntax and semanthic errors. all attribute will be checked
	* @throws IllegalArgumentException - A value is not of the right type/format/value for an attribute name in the JobAd
	* @throws NoSuchFieldException - Unable to find a JobAd mandatory attribute/value
	* @throws JobAdException - one or more values do not  match with jobad semantic rule
	* @see  #checkAll( String[] ) */
	public void checkAll() throws JobAdException {
		String[] attributes = {  };
		checkAll(attributes);
	}
	/**
	* Check the JobAd instance for both syntax and semanthic errors
	* @param attributes specify attributes to be checked
	* @throws IllegalArgumentException - A value is not of the right type/format/value for an attribute name in the JobAd
	* @throws NoSuchFieldException - Unable to find a JobAd mandatory attribute/value
	* @throws JobAdException - one or more values do not  match with jobad semantic rule
	* @see  #checkAll(  ) */
	public void checkAll(String[] attributes) throws JobAdException {
		check(attributes);
		restore();
	}
	/**
	* Check the JobAd instance for both syntax and semanthic errors
	* @throws IllegalArgumentException - A value is not of the right type/format/value for an attribute name in the JobAd
	* @throws NoSuchFieldException - Unable to find a JobAd mandatory attribute/value
	* @throws JobAdException - one or more values do not  match with jobad semantic rule*/
	private void check(String[] attributes) throws JobAdException {
		checking = true;
		if (schema == null) {
			schema = JobAdSchema.glueSchema();
		}
		// restore the values set by the user, discard JobAd self changes
		finalExceptionMessage = new String();
		try {
			checkSemantic(attributes);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		checking = false;
		if (!finalExceptionMessage.equals("")) {
			// One or more Exceptions has been catched:
			throw new JobAdException(finalExceptionMessage.trim());
		}
	}
	private void checkRank() throws Exception {
		// Behaviour: other.DataAccessCost   could be used if, and only if, it's alone and with
		// Inputdata& DtatAccessProtocol
		String DAC = schema.get(schema.SCHEMA_DAC).toLowerCase();
		String rank = getAttributeExpr(Jdl.RANK);
		if (rank.toLowerCase().indexOf(DAC) != -1) {
			// DAC  found:
			if (!hasAttribute(Jdl.DATA_ACCESS) || !hasAttribute(Jdl.INPUTDATA)) {
				throwCheck(new JobAdException(Jdl.DATA_ACCESS + "," + Jdl.INPUTDATA +
				": Both attributes must be specified when Rank = other.DataAccessCost"));
			} else if ((rank.indexOf("+") != -1) || (rank.indexOf("-") != -1) ||  (rank.indexOf("*") != -1) || (rank.indexOf("/") != -1)) {
				throwCheck(new JobAdException(Jdl.RANK +
				": other.DataAccessCost is a stand alone value, no other values are allowed"));
			}
		}
	}
	/** Checks the jobType value and if necessary modify other attribute values(such as rank or requirements)  */
	private void checkJobType(Vector vectType) throws Exception {  //TBD throws Exception fix me!!!
		//JobType - NodeNumber -requirements Check
		// Create a number that has a unique representation of all JobType values at a time:
		int jobType = 1;
		boolean req = hasAttribute(Jdl.REQUIREMENTS);
		String requirements = "";
		String rank = "";
		/* Initial checks:
		- NodeNumber ->Mpi
		- ShPort -> interacive
		- JobSteps-> checkponintable   */
		
		if (hasAttribute(Jdl.SHPORT) && !hasAttribute(Jdl.JOBTYPE, Jdl.JOBTYPE_INTERACTIVE)) {
			throw new JobAdException(Jdl.SHPORT +": attribute cannot be specified with non INTERACTIVE jobs");
		}
		if (hasAttribute(Jdl.CHKPT_STEPS) &&!(hasAttribute(Jdl.JOBTYPE, Jdl.JOBTYPE_CHECKPOINTABLE)||hasAttribute(Jdl.JOBTYPE, Jdl.JOBTYPE_PARTITIONABLE))){
			throw new JobAdException(Jdl.CHKPT_STEPS +": attribute can be specified either for PARTITIONABLE or CHECKPOINTABLE jobs");
		}
		if (hasAttribute(Jdl.CHKPT_CURRENTSTEP) &&!( hasAttribute(Jdl.JOBTYPE, Jdl.JOBTYPE_CHECKPOINTABLE)||hasAttribute(Jdl.JOBTYPE, Jdl.JOBTYPE_PARTITIONABLE))){
			throw new JobAdException(Jdl.CHKPT_CURRENTSTEP +": attribute cannot be specified with non CHECKPOINTABLE jobs");
		}
		/**
		 *                        MPICH  Jobs
		 */
		if (hasAttribute(Jdl.JOBTYPE, Jdl.JOBTYPE_MPICH)) {
		    Expr cn = lookup(Jdl.CPUNUMB);
		    boolean isCpuNumb = true;

		    if (cn == null) {
		        isCpuNumb = false;

		        Expr nn = lookup(Jdl.NODENUMB);
		        if (nn == null) {
		            //Either CpuNumber not found:ERROR
		            throw new JobAdException(Jdl.JOBTYPE + ":  " + Jdl.CPUNUMB + "attribute must be specified with MPICH jobs");
		        }
		    }

		    //Add MPI changes to Requirements
		    if (req) {
		        requirements = "(" + getAttributeExpr(Jdl.REQUIREMENTS) + ")" +
		        "  && Member( \"MPICH\", " + schema.get(schema.SCHEMA_RTE) +
		        ")" + " && " + schema.get(schema.SCHEMA_TCPU) + " >= " + (isCpuNumb? Jdl.CPUNUMB: Jdl.NODENUMB);

		        delAttribute(Jdl.REQUIREMENTS);
		        setAttributeExpr(Jdl.REQUIREMENTS, requirements);
		    }
		    //Add JobType changes to Rank: DEPRECATED
		    if (!hasAttribute(Jdl.RANK)) {
		        setAttributeExpr(Jdl.RANK, schema.get(schema.SCHEMA_FCPU));
		    }
		}
		/**
		*                        INTERACTIVE  Jobs
		*/
		else if (hasAttribute(Jdl.JOBTYPE, Jdl.JOBTYPE_INTERACTIVE)) {  //TBD ELSE IF.... shouldn't it be plain if???!?!? fixme (the same for the following)
			if ((hasAttribute(Jdl.STDINPUT)) || (hasAttribute(Jdl.STDOUTPUT)) || (hasAttribute(Jdl.STDERROR))) {
				throw new JobAdException("StdInput/Output/Error: attributes cannot be specified for interactive jobs");
			}
			//Add  INTERACTIVE changes to Requirements
			if (req) {
				requirements = "(" + getAttributeExpr(Jdl.REQUIREMENTS) +
				")  && " + schema.get(schema.SCHEMA_OIP);
				delAttribute(Jdl.REQUIREMENTS);
				setAttributeExpr(Jdl.REQUIREMENTS, requirements);
			}
		}
		/**
		*                        CHECKPOINTABLE  Jobs
		*/
		else if (hasAttribute(Jdl.JOBTYPE, Jdl.JOBTYPE_CHECKPOINTABLE)) {
			if (hasAttribute(Jdl.CHKPT_STEPS)) {
				if (hasAttribute(Jdl.CHKPT_CURRENTSTEP)) {
					Vector steps;
					int current = getInt(Jdl.CHKPT_CURRENTSTEP);
					if (getType(Jdl.CHKPT_STEPS) == TYPE_INTEGER) {
						// JobSteps is Integer
						if (current < 0){
							throw new JobAdException(Jdl.CHKPT_CURRENTSTEP +"The value cannot be less than 0");
						}
						steps = getIntValue(Jdl.CHKPT_STEPS);
						if (current > ((Integer) getIntValue(Jdl.CHKPT_STEPS) .get(0)).intValue()) {
							throw new JobAdException(Jdl.CHKPT_CURRENTSTEP + ": The value cannot be greater than the value specified in " + Jdl.CHKPT_STEPS);
						}
					} else if (getType(Jdl.CHKPT_STEPS) == TYPE_STRING) {
						// JobSteps is String
						if (current < 1) {
							throw new JobAdException(Jdl.CHKPT_CURRENTSTEP + ": The value cannot be less than 1");
						}
						steps = getStringValue(Jdl.CHKPT_STEPS);
						if (current > steps.size()) {
							throw new JobAdException(Jdl.CHKPT_CURRENTSTEP + ": The value cannot be greater then or equal to the number of steps specified in " +Jdl.CHKPT_STEPS);
						}
					}
				} else {
					setAttribute(Jdl.CHKPT_CURRENTSTEP, 1);
				}
			} else if (hasAttribute(Jdl.CHKPT_CURRENTSTEP)) {
				throw new JobAdException(Jdl.CHKPT_CURRENTSTEP + ": attribute must not be specified without " +
				Jdl.CHKPT_STEPS);
			}
		}
	}
		/**************************************
		Check Methods
		**************************************/


	/**
	* Check if the couple attribute/value is admitted
	* @param attrName - the name of the attribute
	* @param attrValue - the value of the attribute
	* @throws  InvalidAttributeValueException- The type of value is not allowed for the specified attribute name */
	static void checkAttribute(String attrName, int attrValue)throws InvalidAttributeValueException {
		if (Jdl.find(attrName)) {
			if (!Jdl.findInt(attrName) && (!Jdl.findDouble(attrName))) {
				throw new InvalidAttributeValueException(attrName + ": mismatch value, cannot be an integer"); //   Unexpected type
			}
			if (attrValue < 0) {
				throw new InvalidAttributeValueException(attrName + ": must be greater or equal to zero (" + attrValue + ")"); //   Unexpected value
			}
			if ((attrName.equals(Jdl.NODENUMB) || attrName.equals(Jdl.CPUNUMB)) && (attrValue < 2)) {
				throw new InvalidAttributeValueException(attrName + ": value must be greater than 1 (" + attrValue + ")"); //   Unexpected value
			}
		}
	}

	/**
	* Check if the couple attribute/value is admitted
	* @param attrName - the name of the attribute
	* @param attrValue - the value of the attribute
	* @throws  InvalidAttributeValueException- The type of value is not allowed for the specified attribute name*/
	static void checkAttribute(String attrName, double attrValue) throws InvalidAttributeValueException {
		if (Jdl.find(attrName)) {
			if (!Jdl.findDouble(attrName)) {
				throw new InvalidAttributeValueException(attrName + ": mismatch value, cannot be a double"); //   Unexpected type
			}
		}
	}

    /**
    * Check if the couple attribute/value is admitted
    * @param attrName - the name of the attribute
    * @param attrValue - the value of the attribute
    * @throws InvalidAttributeValueException - The type of value is not allowed for the specified attribute name */
    static void checkAttribute(String attrName, boolean attrValue)
        throws InvalidAttributeValueException {
        if (Jdl.find(attrName)) {
            if (!Jdl.findBool(attrName)) {
                throw new InvalidAttributeValueException(attrName +
                    ": mismatch value, cannot be a boolean"); //   Unexpected type
            }
        }
    }

    static void checkAttribute(String attrName, Ad attrValue)
        throws InvalidAttributeValueException {
        if (Jdl.find(attrName)) {
            if (!Jdl.findAd(attrName)) {
                throw new InvalidAttributeValueException(attrName +
                    ": mismatch value, cannot be an Expression"); //   Unexpected type
            }
            if (Jdl.compare(attrName, Jdl.OUTPUTDATA)) {
                try {
                    if (attrValue.getType(Jdl.OD_OUTPUT_FILE) != TYPE_STRING) {
                        throw new InvalidAttributeValueException(attrName +
                            "." + Jdl.OD_OUTPUT_FILE +
                            ": mismatch value, String expected");
                    }
                    if (attrValue.hasAttribute(Jdl.OD_LOGICAL_FILENAME)) {
                        if (attrValue.getType(Jdl.OD_LOGICAL_FILENAME) != TYPE_STRING) {
                            throw new InvalidAttributeValueException(attrName +
                                "." + Jdl.OD_LOGICAL_FILENAME +
                                ": mismatch value, String expected");
                        }
                    }
                    if (attrValue.hasAttribute(Jdl.OD_STORAGE_ELEMENT)) {
                        if (attrValue.getType(Jdl.OD_STORAGE_ELEMENT) != TYPE_STRING) {
                            throw new InvalidAttributeValueException(attrName +
                                "." + Jdl.OD_STORAGE_ELEMENT +
                                ": mismatch value, String expected");
                        }
                    }
                } catch (NoSuchFieldException exc) {
                    throw new InvalidAttributeValueException(attrName + "." +
                        exc.getMessage());
                }
            } else if (Jdl.compare(attrName, Jdl.USER_TAGS)) {
                Iterator attrs = attrValue.attributes();
                try {
                    while (attrs.hasNext()) {
                        String attr = attrs.next().toString();

                        if (attrValue.getType(attr) != TYPE_STRING) {
                            throw new InvalidAttributeValueException(Jdl.USER_TAGS +
                                "." + attr +
                                ": mismatch value, String expected");
                        }
                    }
                } catch (NoSuchFieldException exc) {
                    exc.printStackTrace();
                    throw new RuntimeException(attrName +
                        ": Fatal attribute value filled.");
                }
            } else if (Jdl.compare(attrName, Jdl.CHKPT_JOBSTATE)) {
                // TBD
                // if (  attrValue.isInstanceOf ( JobState.Class)  )
                // System.out.println("JobState instance added" ) ;
            }
        }
    }
    /**
    * Check if the couple attribute/value is admitted
    * @param attrName - the name of the attribute
    * @param attrValue - the value of the attribute
    * @throws IllegalArgumentException - The type of value is not allowed for the specified attribute name  */
    static void checkAttributeExpr(String attrName, String attrValue)
        throws InvalidAttributeValueException {
        if (Jdl.find(attrName)) {
            if (!Jdl.findExpr(attrName)) {
                throw new InvalidAttributeValueException(attrName +
                    ": mismatch value, cannot be an Expression"); //   Unexpected type
            }
        }
    }
    /**
    * Check if the couple attribute/value is admitted
    * @param attrName - the name of the attribute
    * @param attrValue - the value of the attribute
    * @throws InvalidAttributeValueException - The type of value is not allowed for the specified attribute name */
    static void checkAttribute(String attrName, String attrValue)
        throws InvalidAttributeValueException {
        if (Jdl.find(attrName)) { //The attribute name exists:

            if (!Jdl.findString(attrName)) {
                throw new InvalidAttributeValueException(attrName +
                    ": mismatch value, cannot be a String"); //   Unexpected type
            }
            String errMsgStr = attrName + ": wrong value for " + attrValue +
                ".\nThe right format is: ";
            if (Jdl.compare(attrName, Jdl.CEID)) {
                String[] format = {
                    sepStr, ":", sepInt, "/", sepStr, "-", sepStr, "-", sepStr
                };
                if (!checkFormat(format, attrValue)) {
                    throw new InvalidAttributeValueException(errMsgStr +
                        "<full-hostname>:<port-number>/jobmanager-<service>-<queuename>");
                }
            }
            //Environment Check
            else if (Jdl.compare(attrName, Jdl.ENVIRONMENT)) {
                String[] format = { sepStr, "=", sepStr };

                if (!checkFormat(format, attrValue)) {
                    throw new InvalidAttributeValueException(errMsgStr +
                        "<variable>=<value>");
                }
            }
            // INPUTSB
            else if (Jdl.compare(attrName, Jdl.INPUTSB)) {
                if ((attrValue.indexOf("/") != -1) &&
                        (attrValue.indexOf("\\") != -1)) {
                    throw new InvalidAttributeValueException(attrName +
                        ": wrong value for " + attrValue +
                        ". Two different system operator's separators checked");
                }
            }
            //STDINPUT & EXECUTABLE
            else if (Jdl.compare(attrName, Jdl.STDINPUT) ||
                    Jdl.compare(attrName, Jdl.EXECUTABLE)) {
                if (!ExtractFiles.isAbsolute(attrValue)) {
                    if (attrValue.indexOf(OpSysSeparator) != -1) {
                        throw new InvalidAttributeValueException(errMsgStr +
                            "<absolute path>|<simple file name>");
                    }
                }
            }
            //InputData Check
            else if (Jdl.compare(attrName, Jdl.INPUTDATA)) {
                String[] format_lfn = { "lfn:", sepStr };
                String[] format_guid = { "guid:", sepStr };
                String[] format_si_lfn = { "si-lfn:", sepStr };
                String[] format_si_guid = { "si-guid:", sepStr };
                if ( !checkFormat(format_lfn, attrValue) &&
                    !checkFormat(format_guid, attrValue) &&
                    !checkFormat(format_si_lfn, attrValue) &&
                    !checkFormat(format_si_guid, attrValue)
                ) {
                    throw new InvalidAttributeValueException(errMsgStr + "lfn | guid | si-lfn | si-guid :<value>");
                }
            }
            //UserContact Check
            else if (Jdl.compare(attrName, Jdl.USER_CONTACT)) {
                int at = 0;
                int ind = attrValue.indexOf("@");

                while (ind != -1) {
                    ++at;
                    ind = attrValue.indexOf("@", ind + 1);
                }

                if (at != 1) {
                    throw new InvalidAttributeValueException(errMsgStr +
                        "<user>@<host domain>");
                }
            }
            //JobType Format
            else if (Jdl.compare(attrName, Jdl.JOBTYPE)) {
                if (!Jdl.compare(attrValue, Jdl.JOBTYPE_MPICH) &&
                        !Jdl.compare(attrValue, Jdl.JOBTYPE_NORMAL) &&
                        !Jdl.compare(attrValue, Jdl.JOBTYPE_PARTITIONABLE) &&
                        !Jdl.compare(attrValue, Jdl.JOBTYPE_MULTIPLE) &&
                        !Jdl.compare(attrValue, Jdl.JOBTYPE_CHECKPOINTABLE) &&
                        !Jdl.compare(attrValue, Jdl.JOBTYPE_INTERACTIVE) &&
                        !Jdl.compare(attrValue, Jdl.JOBTYPE_PARAMETRIC)){
                    throw new InvalidAttributeValueException(errMsgStr +
                        "mpich | normal | partitionable | multiple | checkpointable | parametric");
                }
            } else if (Jdl.compare(attrName, Jdl.STDOUTPUT) ||
                    Jdl.compare(attrName, Jdl.STDERROR) ||
                    Jdl.compare(attrName, Jdl.OUTPUTSB)) {
                if (attrValue.startsWith("..")) {
                    throw new InvalidAttributeValueException(attrName +
                        ": relative path are allowed only when directory name are specified");
                }
            } else if (Jdl.compare(attrName, Jdl.STDOUTPUT) ||
                    Jdl.compare(attrName, Jdl.STDERROR) ||
                    Jdl.compare(attrName, Jdl.STDINPUT) ||
                    Jdl.compare(attrName, Jdl.EXECUTABLE)) {
                // WildCard not admitted
                String[] format = { "*", "[", "]", "?", "{", "}" };
                ExtractFiles.checkString(attrName, attrValue, format);
            }
        }
    }

    /**
    * Check if the specified value respect the given format */
    private static boolean checkFormat(String[] format, String value) {
        int index = 0;
        int last = 0;
        String previous = "";

        // Checking the format :
        for (int i = 0; i < format.length; i++) {
            if (format[i].equals(sepStr) || format[i].equals(sepInt)) {
                // It's a String-Int value
                if (previous.equals("")) {
                    previous = format[i];
                } else {
                    return false;
                }
            } else {
                // It's a separator , find it
                index = value.indexOf(format[i], index) + format[i].length();

                if (index < format[i].length()) { //not found

                    return false;
                } else if (index == format[i].length()) { // found at the beginning: previous be empty

                    if (!previous.equals("")) {
                        return false;
                    }
                } else { // found at a certain position: check previous

                    if (previous.equals("")) {
                        return false;
                    } else {
                        try {
                            java.lang.Integer.parseInt(value.substring(last,
                                    index - format[i].length()));

                            // it's an integer
                            if (previous.equals(sepStr)) {
                                return false;
                            }
                        } catch (NumberFormatException exc) {
                            // it's not an Integer
                            if (previous.equals(sepInt)) {
                                return false;
                            }
                        }
                    }
                }

                previous = "";
            }

            last = index;
        }

        // for Cycle end- no errors found so far
        //System.out.println("OK!!!");
        return true;
    }

    // Check Only the syntax of the Jdl
	static void checkSyntax(String attrName, Expr value) throws InvalidAttributeValueException, IllegalArgumentException {
		if (value.type == Expr.ERROR) {
			throw new IllegalArgumentException(attrName + ": syntax error, unable to parse the value");
		} else if (value.type == Expr.STRING) {
			checkAttribute(attrName, ((Constant) value).stringValue());
		} else if (value.type == Expr.REAL) {
			checkAttribute(attrName, ((Constant) value).realValue());
		} else if (value.type == Expr.INTEGER) {
			checkAttribute(attrName, ((Constant) value).intValue());
		} else if (value.type == Expr.BOOLEAN) {
			checkAttribute(attrName, ((Constant) value).isTrue());
		} else if (value.type == Expr.RECORD) {
			try {
				checkAttribute(attrName, new Ad((RecordExpr) value));
			} catch (JobAdException exc) {
				exc.printStackTrace();
				throw new InvalidAttributeValueException(attrName + ": Fatal Error caught");
			}
		} else if (!Jdl.findExpr(attrName)) {
			throw new InvalidAttributeValueException(attrName + ": expression type not allowed"); //   Unexpected type
		}
	}


    private void throwCheck(Exception exc) {
        if (exc.getMessage() != null) {
            finalExceptionMessage += ("- " + exc.getMessage() + "\n");
        } else {
            exc.printStackTrace();
        }
    }

    /**
    * Set the JobAd checking attributes utilised schema (Default Schema: Glue) */
    public void setSchema(JobAdSchema schema) {
        this.schema = schema;
    }

    /** Rank  default attribute initialisation */
    public void setDefaultRank(String attrValue) {
        // ClassAdParser bug override
        attrValue = parseStringValue(attrValue);

        ClassAdParser cp = new ClassAdParser(attrValue);
        defaultRank = cp.parse();
    }

    /** Requirements default attribute initialisation */
    public void setDefaultRequirements(String attrValue) {
        // ClassAdParser bug override
        attrValue = parseStringValue(attrValue);

        ClassAdParser cp = new ClassAdParser(attrValue);
        defaultReq = cp.parse();
    }

    // Check Jdl semantic cross-semantic rules
    private void checkSemantic(String[] attributes) throws Exception {
        /*
        *  General variables:
        */
        boolean execFound = false;
        boolean stdInputFound = false;
        String exec = "";
        String stdInput = "";

        /*
        *   TYPE
        */
        if (Jdl.findAttribute(Jdl.TYPE, attributes) ||
                (attributes.length == 0)) {
            if (hasAttribute(Jdl.TYPE)) {
                String type = getString(Jdl.TYPE).toLowerCase();

                if (!type.equals(Jdl.TYPE_JOB)) {
                    throwCheck(new JobAdException(Jdl.TYPE + ": '" + type +
                            "' is not allowed for JobAd"));
                }

                delAttribute(Jdl.TYPE);
                setAttribute(Jdl.TYPE, type);
            } else {
                setAttribute(Jdl.TYPE, Jdl.TYPE_JOB);
            }
        }

        /*
        *   EXECUTABLE  Check:
        * If the specified path is:
        * 1) an absolute path -> nothing required
        * 2) a simple filename-> it has to be found listed in Inputsanbox
        * 3) neither an absolut nor a simple filename-> Error
        */
        if (Jdl.findAttribute(Jdl.EXECUTABLE, attributes) ||
                (attributes.length == 0)) {
            if (hasAttribute(Jdl.EXECUTABLE)) {
                exec = getString(Jdl.EXECUTABLE);
                execFound = false;

                // OpSys un-dependent check
                if (ExtractFiles.isAbsolute(exec)) {
                    // absolute path: has not to be specified in InputSandbox
                    execFound = true;
                } else if (exec.equals(ExtractFiles.getName(exec))) {
                    execFound = true;
                }
            } else {
                throwCheck(new NoSuchFieldException(Jdl.EXECUTABLE +
                        ": mandatory attribute not set")); // The mandatory attribute has not been set yet
            }
        }

        /*
        *   VIRTUAL_ORGANISATION
        */
        if (Jdl.findAttribute(Jdl.VIRTUAL_ORGANISATION, attributes) ||
                (attributes.length == 0)) {
            if (!hasAttribute(Jdl.VIRTUAL_ORGANISATION)) {
                throwCheck(new NoSuchFieldException(Jdl.VIRTUAL_ORGANISATION +
                        ": mandatory attribute not set")); // The mandatory attribute has not been set yet
            }
        }

        /*
        *  STDINPUT   Check
        */
        if (Jdl.findAttribute(Jdl.STDINPUT, attributes) ||
                (attributes.length == 0)) {
            if (hasAttribute(Jdl.STDINPUT)) {
                stdInput = getString(Jdl.STDINPUT);

                // OpSys un-dependent check
                if (ExtractFiles.isAbsolute(stdInput)) {
                    // an absolute path has not to be specified in InputSandbox
                    stdInputFound = true;
                }
            }
        }

        /*
        *   INPUTSB  Check
        */
        if (Jdl.findAttribute(Jdl.INPUTSB, attributes) ||
                (attributes.length == 0)) {
            if (hasAttribute(Jdl.INPUTSB) && localAccess) {
                try {
                    extractFiles(Jdl.INPUTSB);
                } catch (Exception exc) {
                    throwCheck(exc);
                }

                // Look for Executable and StdInput in InputSandox (if required)
                if (!(stdInputFound && execFound)) {
                    Vector extracted = getStringValue(Jdl.INPUTSB);

                    for (int i = 0; i < extracted.size(); i++) {
                        String extr = extracted.get(i).toString();

                        if (extr.endsWith(exec)) {
                            execFound = true;
                        }

                        if (extr.endsWith(stdInput)) {
                            stdInputFound = true;
                        }
                    }
                }
            }
        }

        // The following check are performed only if access to local hard disk is allowed, look setLocalAccess method
        if (localAccess) {
            /*
            *    INPUTSB / EXECUTABLE cross- check
            */
            if ((Jdl.findAttribute(Jdl.EXECUTABLE, attributes) &&
                    Jdl.findAttribute(Jdl.INPUTSB, attributes)) ||
                    (attributes.length == 0)) {
                if (!execFound && hasAttribute(Jdl.EXECUTABLE)) {
                    throwCheck(new NoSuchFieldException(Jdl.EXECUTABLE +
                            ": file is not listed in " + Jdl.INPUTSB));
                }
            }

            /*
            *    INPUTSB / STDINPUTcross- check
            */
            if ((Jdl.findAttribute(Jdl.STDINPUT, attributes) &&
                    Jdl.findAttribute(Jdl.INPUTSB, attributes)) ||
                    (attributes.length == 0)) {
                if (!stdInputFound && hasAttribute(Jdl.STDINPUT)) {
                    throwCheck(new NoSuchFieldException(Jdl.STDINPUT +
                            ": file is not listed in " + Jdl.INPUTSB));
                }
            }
        }

        //end   if ( localAccess )

        /*
        *    STDERROR / STDOUTPUT cross- check
        */
        if ((Jdl.findAttribute(Jdl.STDOUTPUT, attributes) &&
                Jdl.findAttribute(Jdl.STDERROR, attributes)) ||
                (attributes.length == 0)) {
            if (hasAttribute(Jdl.STDOUTPUT) && hasAttribute(Jdl.STDERROR)) {
                String stdOutput = getString(Jdl.STDOUTPUT);
                String stdError = getString(Jdl.STDERROR);

                if (ExtractFiles.getName(stdOutput).equals(ExtractFiles.getName(
                                stdError))) {
                    if (!stdOutput.equals(stdError)) {
                        throwCheck(new IllegalArgumentException(Jdl.STDOUTPUT +
                                "," + Jdl.STDERROR +
                                ": if same file then paths must be equal"));
                    }
                }
            }
        }

        /*
        *    OUTPUTSB Check
        */
        if (Jdl.findAttribute(Jdl.OUTPUTSB, attributes) ||
                (attributes.length == 0)) {
            if (hasAttribute(Jdl.OUTPUTSB)) {
                Vector extracted = getStringValue(Jdl.OUTPUTSB);
                HashSet outSboxMap = new HashSet();
                HashSet outSboxDupli = new HashSet();

                // Check for duplicate files
                String outputSimple;

                for (int i = 0; i < extracted.size(); i++) {
                    outputSimple = new File(extracted.get(i).toString()).getName();

                    if (!outSboxMap.add(outputSimple)) {
                        outSboxDupli.add(outputSimple);
                    }
                }

                if (!outSboxDupli.isEmpty()) {
                    Iterator it = outSboxDupli.iterator();
                    String exceptionMessage = new String();

                    while (it.hasNext())
                        exceptionMessage += ("\n- " + Jdl.OUTPUTSB +
                        ": filename conflict found. The following file is repeated more than once: " +
                        it.next());

                    throwCheck(new JobAdException(exceptionMessage.trim()
                                                                  .substring(2)));
                }
            }
        }

        /*
        *   INPUTDATA  DATA_ACCESS Check
        */
        if ((Jdl.findAttribute(Jdl.INPUTDATA, attributes) &&
                Jdl.findAttribute(Jdl.DATA_ACCESS, attributes)) ||
                (attributes.length == 0)) {
            boolean id = hasAttribute(Jdl.INPUTDATA);
            boolean dp = hasAttribute(Jdl.DATA_ACCESS);

            if (id || dp) {
                //if one out of these attribute has been specified then all of have to be set
                if (!(id && dp)) {
                    String errStr = Jdl.INPUTDATA + ", " + Jdl.DATA_ACCESS;
                    throwCheck(new IllegalArgumentException(errStr +
                            ": attributes must be specified all togheter"));
                }
            }
        }

        /*
        *  USERCONTACT  Check
        */
        if (Jdl.findAttribute(Jdl.USER_CONTACT, attributes) ||
                (attributes.length == 0)) {
            if (hasAttribute(Jdl.USER_CONTACT)) {
                Vector ctVect = getStringValue(Jdl.USER_CONTACT);
                String contact = ctVect.get(0).toString();

                for (int i = 1; i < ctVect.size(); i++)
                    contact += ("," + ctVect.get(i));

                jobAd.removeAttribute(AttrName.fromText(Jdl.USER_CONTACT));
                jobAd.insertAttribute(Jdl.USER_CONTACT,
                    Constant.getInstance(contact));
            }
        }

        /*
        *  JOBTYPE  Check
        */
        if (Jdl.findAttribute(Jdl.JOBTYPE, attributes) ||
                (attributes.length == 0)) {
            if (hasAttribute(Jdl.JOBTYPE)) {
                try {
                    Vector jtvect = getStringValue(Jdl.JOBTYPE);
                    jobAd.removeAttribute(AttrName.fromText(Jdl.JOBTYPE));

                    for (int i = 0; i < jtvect.size(); i++) {
                        jtvect.add(i,
                            (jtvect.remove(i).toString()).toLowerCase());
                        addAttribute(Jdl.JOBTYPE, jtvect.get(i).toString());
                    }

                    checkJobType(jtvect);
                } catch (Exception exc) {
                    throwCheck(exc);
                }
            } else {
                setAttribute(Jdl.JOBTYPE, Jdl.JOBTYPE_NORMAL);
            }
        }

        /*
        *   REQUIREMENTS   Check
        * Initialising default attributes (if necessary)
        */
        if (Jdl.findAttribute(Jdl.REQUIREMENTS, attributes) ||
                (attributes.length == 0)) {
            if (!hasAttribute(Jdl.REQUIREMENTS)) {
                if (defaultReq != null) {
                    insertAttribute(Jdl.REQUIREMENTS, defaultReq);
                } else {
                    throwCheck(new NoSuchFieldException(Jdl.REQUIREMENTS +
                            ": mandatory attribute not set"));
                }
            } else {
                // Append value TBD
            }
        }

        /*
        *   RANK   Check
        * Initialising default attributes (if necessary)
        */
        if (Jdl.findAttribute(Jdl.RANK, attributes) ||
                (attributes.length == 0)) {
            if (!hasAttribute(Jdl.RANK)) {
                if (defaultRank != null) {
                    insertAttribute(Jdl.RANK, defaultRank);
                    checkRank();
                } else {
                    throwCheck(new NoSuchFieldException(Jdl.RANK +
                            ": mandatory attribute not set"));
                }
            } else {
                checkRank();
            }
        }

        /*
        *   DEFAULT_RANK Check
        */
        if (Jdl.findAttribute(Jdl.DEFAULT_RANK, attributes) ||
                (attributes.length == 0)) {
            if (!hasAttribute(Jdl.DEFAULT_RANK)) {
                setAttributeExpr(Jdl.DEFAULT_RANK, Jdl.RANK_DEFAULT);
            }
        }
    }

    private void extractFiles(String attrName)
        throws JobAdException, NoSuchFieldException {
        // Extract the files :
        ExtractFiles eFiles = new ExtractFiles(getStringValue(attrName));
        Vector extractedFiles = eFiles.getMatchingFiles();
        Vector exprFiles = new Vector();

        for (int i = 0; i < extractedFiles.size(); i++)
            exprFiles.add(Constant.getInstance(extractedFiles.get(i).toString()));

        if (extractedFiles.size() != 0) {
            delAttribute(attrName);
            jobAd.insertAttribute(attrName, new ListExpr(exprFiles));
        }

        eFiles.check(attrName);
    }
    
		/*******************************************************
		*             Private / protected package MEMBERS:
		********************************************************/
	/** These variable are utilized to check the format of a string*/
	static final String sepStr = "$STR$";
	static final String sepInt = "$INT$";
	private static String OpSysSeparator = System.getProperty("file.separator");

	/** This member keeps track of the attributes that might be changed by check method */
	private RecordExpr user ;

	/**This member is used to store all the information about the attributes*/
	private boolean checking = false;
	private boolean throwing = false;
	private String finalExceptionMessage;

	/*If this variable is set to false, local access to hard-disk is not available,
	no check on this term will be performed */
	private boolean localAccess = true;

	/** The JobAd utilised Schema private instance*/
	private JobAdSchema schema;
	private Expr defaultRank;
	private Expr defaultReq;

}  //End class



