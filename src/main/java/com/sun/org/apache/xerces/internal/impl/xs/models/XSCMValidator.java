


package com.sun.org.apache.xerces.internal.impl.xs.models;

import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.impl.xs.SubstitutionGroupHandler;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaException;

import java.util.Vector;
import java.util.ArrayList;


public interface XSCMValidator {


    public static final short FIRST_ERROR = -1;

    public static final short SUBSEQUENT_ERROR = -2;


    public int[] startContentModel();



    public Object oneTransition (QName elementName, int[] state, SubstitutionGroupHandler subGroupHandler);



    public boolean endContentModel (int[] state);


    public boolean checkUniqueParticleAttribution(SubstitutionGroupHandler subGroupHandler) throws XMLSchemaException;


    public Vector whatCanGoHere(int[] state);


    public ArrayList checkMinMaxBounds();

}