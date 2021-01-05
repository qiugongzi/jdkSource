


package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.xni.XNIException;


public class XMLConfigurationException
    extends XNIException {


    static final long serialVersionUID = -5437427404547669188L;

    protected Status fType;


    protected String fIdentifier;

    public XMLConfigurationException(Status type, String identifier) {
        super(identifier);
        fType = type;
        fIdentifier = identifier;
    } public XMLConfigurationException(Status type, String identifier,
                                     String message) {
        super(message);
        fType = type;
        fIdentifier = identifier;
    } public Status getType() {
        return fType;
    } public String getIdentifier() {
        return fIdentifier;
    } }