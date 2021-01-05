

package com.sun.org.apache.xml.internal.security.encryption;

import java.util.Iterator;


public interface ReferenceList {


    int DATA_REFERENCE = 0x00000001;


    int KEY_REFERENCE  = 0x00000002;


    void add(Reference reference);


    void remove(Reference reference);


    int size();


    boolean isEmpty();


    Iterator<Reference> getReferences();


    Reference newDataReference(String uri);


    Reference newKeyReference(String uri);
}
