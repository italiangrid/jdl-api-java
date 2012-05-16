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
 * Version info: $Id: ParametricAd.java,v 1.12.2.2 2006/10/06 08:07:37 pandreet Exp $
 */

package org.glite.jdl;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;
import java.text.ParseException;
import javax.naming.directory.InvalidAttributeValueException;

import condor.classad.Expr;
import condor.classad.RecordExpr;
import condor.classad.Constant;
import condor.classad.ListExpr;
import condor.classad.RecordExpr;

public class ParametricAd extends JobAd implements Cloneable, EnumerableAd {

    private long status;

    public ParametricAd() {
        super();
        status = 0;
    }

    public ParametricAd(String ad) throws ParseException, JobAdException {
        super(ad);
        status = 0;
    }

    public ParametricAd(RecordExpr ad) throws JobAdException {
        super(ad);
        status = 0;
    }

    synchronized void insertAttribute(String attrName, Expr attrValue)
        throws InvalidAttributeValueException {

        super.insertAttribute(attrName, attrValue);

        if( status==Long.MAX_VALUE )
            status = 0;
        else
            status++;

    }

    public Object clone() {
		try {

            return new ParametricAd(jobAd);

		} catch (JobAdException jobEx) {
		}

        throw new RuntimeException("Cannot clone parametric ad");
	}

    private synchronized JobAd cloneWithParameter(String cString, long st){
        if( status!=st )
            throw new ConcurrentModificationException();

        RecordExpr result = new RecordExpr();
        boolean missingEnviron = true;

		Iterator it = jobAd.attributes();
		while (it.hasNext()) {
			String attrName = it.next().toString();

            if( attrName.equalsIgnoreCase(Jdl.PARAMETRIC_PARAMS_START) ||
                attrName.equalsIgnoreCase(Jdl.PARAMETRIC_PARAMS) ||
                attrName.equalsIgnoreCase(Jdl.PARAMETRIC_PARAMS_STEP) )
                continue;

            if( attrName.equalsIgnoreCase(Jdl.JOBTYPE) ){
                result.insertAttribute(attrName, Constant.getInstance(Jdl.JOBTYPE_NORMAL));
                continue;
            }

            Expr source = jobAd.lookup(attrName);
            Expr target = null;

            if( source.type==Expr.LIST ){

                ListExpr slist = (ListExpr)source;
                ListExpr tlist = new ListExpr();
                for(int k=0; k<slist.size(); k++){
                    Expr tmpe = slist.sub(k);
                    if( tmpe.type==Expr.STRING ){
                        String tmps = tmpe.stringValue();
                        if( tmps.indexOf("_PARAM_")>=0 ){
                            tlist.add(Constant.getInstance(tmps.replaceAll("_PARAM_", cString)));
                        }else{
                            tlist.add(Constant.getInstance(tmps));
                        }
                    }else{
                        tlist.add(tmpe);
                    }
                }

                if( attrName.equalsIgnoreCase(Jdl.ENVIRONMENT) ){
                    missingEnviron = false;
                    tlist.add(Constant.getInstance("ParameterValue=\"" + cString + "\""));
                }

                target = tlist;

            }else if( source.type==Expr.STRING ){

                String tmps = source.stringValue();
                if( tmps.indexOf("_PARAM_")>=0 ){
                    target = Constant.getInstance(tmps.replaceAll("_PARAM_", cString));
                }else{
                    target = Constant.getInstance(tmps);
                }

            }else{
                target = source;
            }

            result.insertAttribute(attrName, target);

		}

        if( missingEnviron ){
            ListExpr tmpe = new ListExpr();
            tmpe.add(Constant.getInstance("ParameterValue=\"" + cString + "\""));
            result.insertAttribute(Jdl.ENVIRONMENT, tmpe);
        }

        JobAd resAd = new JobAd();
        resAd.jobAd = result;
        return resAd;
    }

    private synchronized void checkLock(long st){
        if( status!=st )
            throw new ConcurrentModificationException();
    }

    public synchronized Enumeration getJobEnumeration() throws JobAdException {

        Expr tmpe = jobAd.lookup(Jdl.JOBTYPE);
        if( tmpe==null || tmpe.type!=Expr.STRING ||
            !tmpe.stringValue().equalsIgnoreCase(Jdl.JOBTYPE_PARAMETRIC) )
            throw new JobAdException("JDL does not contain a parametric job");

        try{
            tmpe = jobAd.lookup(Jdl.PARAMETRIC_PARAMS);

            if( tmpe==null )
                throw new JobAdException("Missing attribute \"Parameters\"");

            if( tmpe.type==Expr.INTEGER ){

                int begin = 0;
                int end = tmpe.intValue();
                int step = 1;

                tmpe = jobAd.lookup(Jdl.PARAMETRIC_PARAMS_START);
                if( tmpe!=null && tmpe.type==Expr.INTEGER )
                    begin = tmpe.intValue();

                tmpe = jobAd.lookup(Jdl.PARAMETRIC_PARAMS_STEP);
                if( tmpe!=null && tmpe.type==Expr.INTEGER )
                    step = tmpe.intValue();

                if( ( begin<end && step<=0 ) || ( begin>end && step>=0 ) )
                    throw new JobAdException("Wrong parameter range");

                return new ParametricAdEnumeration(begin, end, step, status);

            }

            if( tmpe.type!=Expr.LIST )
                throw new JobAdException("Wrong type for attribute \"Parameters\"");

            ListExpr list = (ListExpr)tmpe;
            String[] pArray = new String[list.size()];
            for(int k=0; k<list.size(); k++){
                pArray[k] = list.sub(k).toString();
            }

            return new ParametricAdEnumeration(pArray, status);

        }catch(ArithmeticException ex){
            throw new JobAdException("Wrong parameter range");
        }
    }

    public class ParametricAdEnumeration implements Enumeration {

        private int counter;
        private int end;
        private int step;
        private String[] patterns;
        private long lock;

        ParametricAdEnumeration(int begin, int end, int incr, long status){
            counter = begin;
            this.end = end;
            step = incr;
            lock = status;
            patterns = null;
        }

        ParametricAdEnumeration(String[] list, long status){

            patterns = list;
            counter = 0;
            end = list.length;
            step = 1;

            lock = status;
            
        }

        public boolean hasMoreElements() {
            checkLock(lock);
            return counter<end;
        }

        public Object nextElement(){

            if( counter>=end )
                throw new NoSuchElementException();

            String tmps = patterns==null ? Integer.toString(counter) : patterns[counter];
            JobAd result = cloneWithParameter(tmps, lock);    

            counter+=step;

            return result;
        }

    }

}
