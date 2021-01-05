


package com.sun.org.apache.xerces.internal.impl.dtd.models;

import com.sun.org.apache.xerces.internal.xni.QName;


public interface ContentModelValidator {

    public int validate(QName[] children, int offset, int length);

}