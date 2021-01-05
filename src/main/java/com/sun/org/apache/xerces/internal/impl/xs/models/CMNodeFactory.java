



package com.sun.org.apache.xerces.internal.impl.xs.models;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.dtd.models.CMNode;
import com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;


public class CMNodeFactory {



    private static final String ERROR_REPORTER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_REPORTER_PROPERTY;


    private static final String SECURITY_MANAGER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SECURITY_MANAGER_PROPERTY;

    private static final boolean DEBUG = false ;

    private static final int MULTIPLICITY = 1 ;

    private int nodeCount = 0;

    private int maxNodeLimit ;



    private XMLErrorReporter fErrorReporter;

    private XMLSecurityManager fSecurityManager = null;


    public CMNodeFactory() {
    }

    public void reset(XMLComponentManager componentManager){
        fErrorReporter = (XMLErrorReporter)componentManager.getProperty(ERROR_REPORTER);
        try {
            fSecurityManager = (XMLSecurityManager)componentManager.getProperty(SECURITY_MANAGER);
            if(fSecurityManager != null){
                maxNodeLimit = fSecurityManager.getLimit(XMLSecurityManager.Limit.MAX_OCCUR_NODE_LIMIT) * MULTIPLICITY ;
            }
        }
        catch (XMLConfigurationException e) {
            fSecurityManager = null;
        }

    }public CMNode getCMLeafNode(int type, Object leaf, int id, int position) {
        return new XSCMLeaf(type, leaf, id, position) ;
    }

    public CMNode getCMRepeatingLeafNode(int type, Object leaf,
            int minOccurs, int maxOccurs, int id, int position) {
        nodeCountCheck();
        return new XSCMRepeatingLeaf(type, leaf, minOccurs, maxOccurs, id, position);
    }

    public CMNode getCMUniOpNode(int type, CMNode childNode) {
        nodeCountCheck();
        return new XSCMUniOp(type, childNode) ;
    }

    public CMNode getCMBinOpNode(int type, CMNode leftNode, CMNode rightNode) {
        return new XSCMBinOp(type, leftNode, rightNode) ;
    }

    public void nodeCountCheck(){
        if( fSecurityManager != null && !fSecurityManager.isNoLimit(maxNodeLimit) &&
                nodeCount++ > maxNodeLimit){
            if(DEBUG){
                System.out.println("nodeCount = " + nodeCount ) ;
                System.out.println("nodeLimit = " + maxNodeLimit ) ;
            }
            fErrorReporter.reportError(XSMessageFormatter.SCHEMA_DOMAIN, "MaxOccurLimit", new Object[]{ new Integer(maxNodeLimit) }, XMLErrorReporter.SEVERITY_FATAL_ERROR);
            nodeCount = 0;
        }

    }public void resetNodeCount(){
        nodeCount = 0 ;
    }

    public void setProperty(String propertyId, Object value)
        throws XMLConfigurationException {

        if (propertyId.startsWith(Constants.XERCES_PROPERTY_PREFIX)) {
                final int suffixLength = propertyId.length() - Constants.XERCES_PROPERTY_PREFIX.length();

            if (suffixLength == Constants.SECURITY_MANAGER_PROPERTY.length() &&
                propertyId.endsWith(Constants.SECURITY_MANAGER_PROPERTY)) {
                fSecurityManager = (XMLSecurityManager)value;
                maxNodeLimit = (fSecurityManager != null) ?
                        fSecurityManager.getLimit(XMLSecurityManager.Limit.MAX_OCCUR_NODE_LIMIT) * MULTIPLICITY : 0 ;
                return;
            }
            if (suffixLength == Constants.ERROR_REPORTER_PROPERTY.length() &&
                propertyId.endsWith(Constants.ERROR_REPORTER_PROPERTY)) {
                fErrorReporter = (XMLErrorReporter)value;
                return;
            }
        }

    } }