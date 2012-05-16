/*
* Jdl.java
*
* Copyright (c) 2001 The European DataGrid Project - IST programme, all rights reserved.
*
* Contributors are mentioned in the code there appropriate.
*
*/
package org.glite.jdl;


/**
* Provides a list of all the known/allowd attributes.
* For each attribute is possible to determine which kind of type (String, Boolean, Integer)
* is allowed
* @version 0.1
* @author Alessandro Maraschini <alessandro.maraschini@datamat.it>
*/
public class Jdl {
    public static final String TYPE = "Type";
    public static final String TYPE_DAG = "dag";
    public static final String TYPE_JOB = "job";
    public static final String TYPE_COLLECTION = "collection";
    public static final String NOTIFTYPE = "NotificationType";
    public static final String JOBSTATUS = "JobStatus";
    public static final String EXITCODE = "ExitCode";
    public static final String ABORTREASON = "AbortedReason";
    public static final String GLOBUSRESOURCE = "GlobusResourceContactString";
    public static final String QUEUENAME = "QueueName";
    public static final String MATCHSTATUS = "MatchStatus";
    public static final String CE_MATCH = "CE_Match";
    public static final String MATCHERROR = "MatchError";
    public static final String CE_RANK = "CE_Rank";
    public static final String CE_REQUIREMENTS = "cerequirements";
    public static final String CANCELSTATUS = "CancelStatus";
    public static final String CANCELFAILURE_REAS = "FailureReason";
    public static final String TRANSFERSTATUS = "TransferStatus";
    public static final String SANDBOXFILE = "SandboxFile";
    public static final String GETOUTFAILURE_REAS = "FailureReason";
    public static final String MAX_OUTPUT_SANDBOX_SIZE = "MaxOutputSandboxSize";

    /*** Jdl attributes */
    public static final String JOBID = "edg_jobid";
    public static final String CEID = "SubmitTo";
    public static final String EXECUTABLE = "Executable";
    public static final String STDINPUT = "StdInput";
    public static final String STDOUTPUT = "StdOutput";
    public static final String STDERROR = "StdError";
    public static final String OUTPUTSB = "OutputSandbox";
    public static final String OSBBASEURI = "OutputSandboxBaseDestURI";
    public static final String OSBURI = "OutputSandboxDestURI";
    public static final String ARGUMENTS = "Arguments";
    public static final String INPUTSB = "InputSandbox";
    public static final String ISBBASEURI = "InputSandboxBaseURI";
    public static final String ENVIRONMENT = "Environment";
    public static final String MYPROXY = "MyProxyServer";
    public static final String STR_USER_PROXY = "StrictUserProxy";
    public static final String RETRYCOUNT = "RetryCount";
    public static final String LB_SEQUENCE_CODE = "LB_sequence_code";
    public static final String LB_ADDRESS = "LBAddress";
    public static final String JOB_PROVENANCE = "JobProvenance";
    public static final String JOBTYPE = "JobType";
    public static final String NODENUMB = "NodeNumber";
    public static final String CPUNUMB = "CpuNumber";
    public static final String ALLOWED_ZIPPED_ISB = "AllowedZippedISB";
    public static final String ZIPPED_ISB = "ZippedISB";
    public static final String EXPIRY_TIME = "ExpiryTime";
    public static final String PERUSALFILEENABLE = "PerusalFileEnable";
    public static final String PROLOGUE = "Prologue";
    public static final String PROLOGUE_ARGUMENTS = "PrologueArguments";
    public static final String EPILOGUE = "Epilogue";
    public static final String EPILOGUE_ARGUMENTS = "EpilogueArguments";
    public static final String MW_VERSION = "MwVersion";
    public static final String WMS_HOSTNAME = "WMSHostname";
    public static final String WHOLE_NODES = "WholeNodes";
    public static final String SMP_GRANULARITY = "SMPGranularity";
    public static final String HOST_NUMBER = "HostNumber";

    // InputData:
    public static final String VIRTUAL_ORGANISATION = "VirtualOrganisation";
    public static final String INPUTDATA = "InputData";
    public static final String DATA_ACCESS = "DataAccessProtocol";

    //OutputData:
    public static final String OUTPUTDATA = "OutputData";
    public static final String DSUPLOAD = "DSUpload";
    public static final String OD_OUTPUT_FILE = "OutputFile";
    public static final String OD_LOGICAL_FILENAME = "LogicalFileName";
    public static final String OD_STORAGE_ELEMENT = "StorageElement";

    // User Tags
    public static final String USER_TAGS = "UserTags";
    public static final String EDG_WL_UI_DAG_NODE_NAME = "glite_wms_ui_DagNodeName";
    public static final String OUTPUT_SE = "OutputSE";
    public static final String USER_CONTACT = "UserContact";
    public static final String RANK = "rank";
    public static final String DEFAULT_RANK = "DefaultRank";
    public static final String RANK_MPI = "rankMpi";
    public static final String REQUIREMENTS = "requirements";
    public static final String CERT_SUBJ = "CertificateSubject";
    public static final String FUZZY_RANK = "FuzzyRank";

    //JobTtype Values and types:
    public static final String JOBTYPE_MPICH = "mpich";
    public static final String JOBTYPE_NORMAL = "normal";
    public static final String JOBTYPE_PARTITIONABLE = "partitionable";
    public static final String JOBTYPE_MULTIPLE = "multiple";
    public static final String JOBTYPE_CHECKPOINTABLE = "checkpointable";
    public static final String JOBTYPE_INTERACTIVE = "interactive";
    public static final String JOBTYPE_PARAMETRIC = "parametric";

    //Interactive Jobtype values:
    public static final String SHPORT = "ListenerPort";
    public static final String INTERACTIVE_STDIN = "GRID_CONSOLE_STDIN";
    public static final String INTERACTIVE_STDOUT = "GRID_CONSOLE_STDOUT";
    public static final String INTERACTIVE_STDERR = "GRID_CONSOLE_STDERR";
    public static final String INTERACTIVE_SHADOWHOST = "BYPASS_SHADOW_HOST";
    public static final String INTERACTIVE_SHADOWPORT = "BYPASS_SHADOW_PORT";

    // Checkpointable JobType values:
    public static final String CHKPT_JOBSTATE = "JobState";
    public static final String CHKPT_DATA = "UserData";
    public static final String CHKPT_STATEID = "StateId";
    public static final String CHKPT_STEPS = "JobSteps";
    public static final String CHKPT_CURRENTSTEP = "CurrentStep";
    public static final String HLR_LOCATION = "HLRLocation";
    public static final String HLR_LOCATION_ENV = "HLR_LOCATION";

    // Partitionable Jobtype values:
    public static final String PRE_JOB ="PreJob" ;
    public static final String POST_JOB ="PostJob" ;
    public static final String STEP_WEIGHT="StepWeigth";

    // DagAd attribute names and values:
    public static final String DAG_DEPENDENCIES = "Dependencies";
    public static final String DAG_NODES = "Nodes";
    public static final String REQ_DEFAULT = "other.GlueCEStateStatus == \"Production\""; 
    public static final String RANK_DEFAULT = "-other.GlueCEStateEstimatedResponseTime";

    //Parametric Jobtype values:
    public static final String PARAMETRIC_PARAMS_START = "ParameterStart";
    public static final String PARAMETRIC_PARAMS = "Parameters";
    public static final String PARAMETRIC_PARAMS_STEP = "ParameterStep";

    //Collection Jobtype values:
    public static final String COLLECTION_NODES = "Nodes";
    public static final String COLLECTION_FILE = "File";

    public static final String[] listAttributes = {
        INPUTSB, OUTPUTSB, ENVIRONMENT, INPUTDATA, DATA_ACCESS, CE_MATCH,
        CE_RANK, CHKPT_STEPS, USER_CONTACT, JOBTYPE, OUTPUTDATA,STEP_WEIGHT
    };
    public static final String[] stringAttributes = {
        EXECUTABLE, ARGUMENTS, STDINPUT, INPUTSB, STDOUTPUT, STDERROR, OUTPUTSB,
        ENVIRONMENT, MYPROXY, TYPE, JOBTYPE, CHKPT_STEPS, VIRTUAL_ORGANISATION,
        INPUTDATA, DATA_ACCESS, OUTPUT_SE, USER_CONTACT, JOBID, CE_MATCH,
        CERT_SUBJ, CEID, MW_VERSION
    };
    private static final String[] boolAttributes = {
        REQUIREMENTS, FUZZY_RANK, STR_USER_PROXY
    };
    private static final String[] intAttributes = {
        NODENUMB, SHPORT, CHKPT_STEPS, RETRYCOUNT, STEP_WEIGHT
    };
    private static final String[] doubleAttributes = {
        DEFAULT_RANK, RANK, CE_RANK
    };
    private static final String[] exprAttributes = {
        RANK, REQUIREMENTS, DEFAULT_RANK
    };
    private static final String[] adAttributes = { OUTPUTDATA, USER_TAGS, PRE_JOB,POST_JOB};

    // This attributes can be changed by JobAd::check methods
    static final String[] changeAttributes = {
        RANK, DEFAULT_RANK, REQUIREMENTS, INPUTSB, ENVIRONMENT, SHPORT, JOBTYPE,
        TYPE, USER_CONTACT, STR_USER_PROXY
    };

    /**
    * Check if the specified value could be of Ad type
    * @param attrName the name of the attribute to be look for
    * @return true if the attribute type match, false otherwise*/
    public static boolean findAd(String attrName) {
        return findAttribute(attrName, adAttributes);
    }

    /**
    * Check if the specified value could be of Expression type
    * @param attrName the name of the attribute to be look for
    * @return true if the attribute type match, false otherwise*/
    public static boolean findExpr(String attrName) {
        return findAttribute(attrName, exprAttributes);
    }

    /**
    * Check if the specified value could be of Boolean type
    * @param attrName the name of the attribute to be look for
    * @return true if the attribute type match, false otherwise*/
    public static boolean findBool(String attrName) {
        return findAttribute(attrName, boolAttributes);
    }

    /**
    * Check if the specified value could be of Integer type
    * @param attrName the name of the attribute to be look for
    * @return true if the attribute type match, false otherwise*/
    public static boolean findInt(String attrName) {
        return findAttribute(attrName, intAttributes);
    }

    /**
    * Check if the specified value could be of String type
    * @param attrName the name of the attribute to be look for
    * @return true if the attribute type match, false otherwise*/
    public static boolean findString(String attrName) {
        return findAttribute(attrName, stringAttributes);
    }

    /**
    * Check if the specified value could be of Double type
    * @param attrName the name of the attribute to be look for
    * @return true if the attribute type match, false otherwise*/
    public static boolean findDouble(String attrName) {
        return findAttribute(attrName, doubleAttributes);
    }

    /**
    * Check if the specified value is known by JobAd
    * @param attrName the name of the attribute to be look for
    * NB: this list is updated periodically depending on the version of the package
    * so it might be possible not to recognise one or more attributes
    * @return true if the attribute type match, false otherwise*/
    public static boolean find(String attrName) {
        return (findAttribute(attrName, boolAttributes) ||
        findAttribute(attrName, intAttributes) ||
        findAttribute(attrName, stringAttributes) ||
        findAttribute(attrName, doubleAttributes) ||
        findAttribute(attrName, adAttributes) ||
        findAttribute(attrName, exprAttributes));
    }

    /**Check if the two strings are equals (case insensitive)*/
    public static final boolean compare(String a, String b) {
        return a.equalsIgnoreCase(b);
    }

    static boolean findAttribute(String attrName, String[] list) {
        for (int i = 0; i < list.length; i++) {
            if (compare(attrName, list[i])) {
                return true;
            }
        }

        return false;
    }
}
