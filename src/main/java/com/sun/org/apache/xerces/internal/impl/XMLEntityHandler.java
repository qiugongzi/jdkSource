


package com.sun.org.apache.xerces.internal.impl;

import java.io.IOException;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XNIException;


public interface XMLEntityHandler {

    public void startEntity(String name,
                            XMLResourceIdentifier identifier,
                            String encoding, Augmentations augs) throws XNIException;


    public void endEntity(String name, Augmentations augs) throws IOException, XNIException;

}