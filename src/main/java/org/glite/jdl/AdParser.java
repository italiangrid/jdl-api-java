/*
 * Copyright (c) 2004 on behalf of the EU EGEE Project:
 * The European Organization for Nuclear Research (CERN),
 * Istituto Nazionale di Fisica Nucleare (INFN), Italy
 * Datamat Spa, Italy
 * Centre National de la Recherche Scientifique (CNRS), France
 * CS Systeme d'Information (CSSI), France
 * Royal Institute of Technology, Center for Parallel Computers (KTH-PDC), Sweden
 * Universiteit van Amsterdam (UvA), Netherlands
 * University of Helsinki (UH.HIP), Finland
 * University of Bergen (UiB), Norway
 * Council for the Central Laboratory of the Research Councils (CCLRC), United Kingdom
 *
 * Authors: Paolo Andreetto, <paolo.andreetto@pd.infn.it>
 *
 * Version info: $Id: AdParser.java,v 1.3.2.2 2006/10/06 08:07:37 pandreet Exp $
 */

package org.glite.jdl;

import condor.classad.ClassAdParser;
import condor.classad.Expr;
import condor.classad.RecordExpr;

public class AdParser {

    public static Ad parseJdl(String jdl) throws JobAdException {

        jdl = Ad.parseStringValue(jdl.trim());
        if (!jdl.startsWith("[")) { 
			jdl = "[ " + jdl + "]"; 
		}

        ClassAdParser cp = new ClassAdParser(jdl);
		Expr expr = cp.parse();
		if (expr == null) {
			throw new JobAdException("Unable to parse: doesn't seem to be a valid Expression");
		} else if (expr.type != Expr.RECORD) {
			throw new JobAdException("Unable to parse: the parsed expression is not a ClassAd");
		}

        RecordExpr jdlExpr = (RecordExpr)expr;

        String type = null;
        expr = jdlExpr.lookup(Jdl.TYPE);
        if( expr==null ) 
            type = Jdl.TYPE_JOB;
        else if( !expr.isConstant() )
            throw new JobAdException("Wrong type parameter");
        else
            type = expr.stringValue();

        if( type.equalsIgnoreCase(Jdl.TYPE_JOB) ){

            expr = jdlExpr.lookup(Jdl.JOBTYPE);
            if( expr==null )
                return new JobAd(jdlExpr);

            if( !expr.isConstant() )
                throw new JobAdException("Missing or wrong JobType");

            String jobType = expr.stringValue();

            if( jobType.equalsIgnoreCase(Jdl.JOBTYPE_PARAMETRIC) )
                return new ParametricAd(jdlExpr);

            return new JobAd(jdlExpr);

        }else if( type.equalsIgnoreCase(Jdl.TYPE_COLLECTION ) ){

            return new CollectionAd(jdlExpr);

        }else{
            throw new JobAdException("Unsupported type " + expr.toString());
        }
    }
}
