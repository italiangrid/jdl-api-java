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
 * Version info: $Id: CollectionAd.java,v 1.3.2.2 2006/10/06 08:07:37 pandreet Exp $
 */

package org.glite.jdl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.text.ParseException;

import condor.classad.Expr;
import condor.classad.RecordExpr;
import condor.classad.Constant;
import condor.classad.ListExpr;
import condor.classad.RecordExpr;
import condor.classad.ClassAdParser;
import condor.classad.AttrName;
import condor.classad.SubscriptExpr;
import condor.classad.Env;

public class CollectionAd extends Ad implements EnumerableAd {

    public CollectionAd(){
        super();
    }

    public CollectionAd(String ad) throws JobAdException, ParseException {
        super(ad);

        if( super.lookup(Jdl.OUTPUTSB)!=null )
            throw new JobAdException(Jdl.OUTPUTSB + " top level attribute not allowed for a collection");
    }

    public CollectionAd(RecordExpr ad) throws JobAdException {
        super(ad);

        if( super.lookup(Jdl.OUTPUTSB)!=null )
            throw new JobAdException(Jdl.OUTPUTSB + " top level attribute not allowed for a collection");
    }

    public Enumeration getJobEnumeration() throws JobAdException {

        Expr voExpr = super.lookup(Jdl.VIRTUAL_ORGANISATION);
        Expr hlrExpr = super.lookup(Jdl.HLR_LOCATION);
        Expr lbExpr = super.lookup(Jdl.LB_ADDRESS);
        Expr myproxyExpr = super.lookup(Jdl.MYPROXY);
        Expr jpExpr = super.lookup(Jdl.JOB_PROVENANCE);
        Expr allowZipExpr = super.lookup(Jdl.ALLOWED_ZIPPED_ISB);
        Expr zipISBExpr = super.lookup(Jdl.ZIPPED_ISB);
        Expr eTimeExpr = super.lookup(Jdl.EXPIRY_TIME);
        Expr perusalExpr = super.lookup(Jdl.PERUSALFILEENABLE);
        Expr rankExpr = super.lookup(Jdl.RANK);
        Expr reqExpr = super.lookup(Jdl.REQUIREMENTS);
        Expr isbExpr = super.lookup(Jdl.INPUTSB);
        Expr isbBaseExpr = super.lookup(Jdl.ISBBASEURI);

        Env baseEnv = new Env();
        baseEnv.push(jobAd);
        

        try{
            ListExpr nodes = (ListExpr)super.lookup(Jdl.COLLECTION_NODES);

            Vector subJobs = new Vector(nodes.size());

            Iterator subExprs = nodes.iterator();
            while( subExprs.hasNext() ){
                RecordExpr expr = (RecordExpr)subExprs.next();
                Expr fileExpr = expr.lookup(Jdl.COLLECTION_FILE);


                if( fileExpr!=null ){
                    String fileName = ((Constant)fileExpr).stringValue();

                    if( expr.size()!=1 )
                        throw new JobAdException("Attribute \"File\" must be the only attribute into a node");

                    StringBuffer buff = new StringBuffer();
                    BufferedReader in = null;
                    String tmps = null;
                    try{
                        in = new BufferedReader(new FileReader(fileName));
                        tmps = in.readLine();
                        while( tmps!=null ){
                            buff.append(tmps.trim()).append("\n");
                            tmps = in.readLine();
                        }
                    }catch(IOException ioEx){
                        throw new JobAdException(ioEx.getMessage());
                    }finally{
                        if( in!=null ){
                            try{
                                in.close();
                            }catch(Exception ex){}
                        }
                    }

                    tmps = Ad.parseStringValue(buff.toString());
                    if( !tmps.startsWith("[") )
                        tmps = "[" + tmps + "]";

                    ClassAdParser cp = new ClassAdParser(tmps);
                    expr = (RecordExpr)cp.parse();
                    if( expr==null )
                        throw new JobAdException("Cannot parse file: " + fileName);

                }

                Expr tmpexpr = expr.lookup(Jdl.JOBTYPE);
                String jobType = tmpexpr!=null ? ((Constant)tmpexpr).stringValue() : null;

                if( jobType!=null && ( jobType.equalsIgnoreCase(Jdl.JOBTYPE_PARAMETRIC) 
                                       || jobType.equalsIgnoreCase(Jdl.JOBTYPE_PARTITIONABLE)) ){

                    throw new JobAdException("Wrong job type for collection: " + jobType);

                }else{

                    if( voExpr==null )
                        throw new JobAdException("Missing attribute " + Jdl.VIRTUAL_ORGANISATION);
                    expr.insertAttribute(Jdl.VIRTUAL_ORGANISATION, voExpr);

                    if( hlrExpr!=null )
                        expr.insertAttribute(Jdl.HLR_LOCATION, hlrExpr);

                    if( lbExpr!=null )
                        expr.insertAttribute(Jdl.LB_ADDRESS, lbExpr);

                    if( myproxyExpr!=null )
                        expr.insertAttribute(Jdl.MYPROXY, myproxyExpr);

                    if( jpExpr!=null )
                        expr.insertAttribute(Jdl.JOB_PROVENANCE, jpExpr);

                    if( allowZipExpr!=null )
                        expr.insertAttribute(Jdl.ALLOWED_ZIPPED_ISB, allowZipExpr);
                    else
                        expr.insertAttribute(Jdl.ALLOWED_ZIPPED_ISB, Constant.FALSE);

                    if( zipISBExpr!=null )
                        expr.insertAttribute(Jdl.ZIPPED_ISB, zipISBExpr);
                    else if( expr.lookup(Jdl.ZIPPED_ISB)!=null )
                        expr.removeAttribute(AttrName.fromString(Jdl.ZIPPED_ISB));

                    if( eTimeExpr!=null && expr.lookup(Jdl.EXPIRY_TIME)==null )
                        expr.insertAttribute(Jdl.EXPIRY_TIME, eTimeExpr);

                    if( perusalExpr!=null && expr.lookup(Jdl.PERUSALFILEENABLE)==null )
                        expr.insertAttribute(Jdl.PERUSALFILEENABLE, perusalExpr);

                    if( rankExpr!=null && expr.lookup(Jdl.RANK)==null )
                        expr.insertAttribute(Jdl.RANK, rankExpr);

                    if( reqExpr!=null && expr.lookup(Jdl.REQUIREMENTS)==null )
                        expr.insertAttribute(Jdl.REQUIREMENTS, reqExpr);

                    if( isbBaseExpr!=null && expr.lookup(Jdl.ISBBASEURI)==null )
                        expr.insertAttribute(Jdl.ISBBASEURI, isbBaseExpr);

                    tmpexpr = expr.lookup(Jdl.INPUTSB);
                    if( tmpexpr==null ){
                        if( isbExpr!=null)
                            expr.insertAttribute(Jdl.INPUTSB, isbExpr);
                    }else{
                        expr.insertAttribute(Jdl.INPUTSB, checkISB(tmpexpr, baseEnv));
                    }

                    subJobs.add(new JobAd(expr));
                }

            }

            return subJobs.elements();

        }catch(Exception ex){
            throw new JobAdException(ex.getMessage());
        }
    }

    private Expr checkISB(Expr isbExpr, Env env) throws JobAdException {

        if( isbExpr==null )
            return null;

        if( isbExpr.type==Expr.LIST ){

            Iterator allExpr = ((ListExpr)isbExpr).iterator();
            boolean foundRef = false;
            while( allExpr.hasNext() && !foundRef ){
                Expr tmpExpr = (Expr)allExpr.next();
                foundRef = ( tmpExpr.type==Expr.SELECTION || tmpExpr.type==Expr.SUBSCRIPT );
            }

            if( foundRef ){

                ListExpr result = new ListExpr();
                allExpr = ((ListExpr)isbExpr).iterator();
                while( allExpr.hasNext() ){
                    Expr tmpExpr = ((Expr)allExpr.next()).eval(env);

                    if( tmpExpr.type==Expr.INTEGER ) {
                        int res = ((Constant)tmpExpr).intValue();
                        if( res==Expr.ERROR || res==Expr.UNDEFINED )
                            throw new JobAdException("Error resolving references in " + isbExpr.toString());
                    }

                    result.add(tmpExpr);
                }

                return result;

            }

            return isbExpr;
        }

        if( isbExpr.type==Expr.ATTRIBUTE ){
            return isbExpr;
        }

        throw new JobAdException("Bad attribute type for ISB");
    }

}
