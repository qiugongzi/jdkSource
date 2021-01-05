


package com.sun.org.apache.xerces.internal.impl.xs.models;

import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.impl.xs.SubstitutionGroupHandler;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaException;

import java.util.Vector;
import java.util.ArrayList;


public class XSEmptyCM  implements XSCMValidator {

    private static final short STATE_START = 0;

    private static final Vector EMPTY = new Vector(0);

    public int[] startContentModel(){
        return (new int[] {STATE_START});
    }



    public Object oneTransition (QName elementName, int[] currentState, SubstitutionGroupHandler subGroupHandler){

        if (currentState[0] < 0) {
            currentState[0] = XSCMValidator.SUBSEQUENT_ERROR;
            return null;
        }

        currentState[0] = XSCMValidator.FIRST_ERROR;
        return null;
    }



    public boolean endContentModel (int[] currentState){
        boolean isFinal =  false;
        int state = currentState[0];

        if (state < 0) {
            return false;
        }


        return true;
    }


    public boolean checkUniqueParticleAttribution(SubstitutionGroupHandler subGroupHandler) throws XMLSchemaException {
        return false;
    }


    public Vector whatCanGoHere(int[] state) {
        return EMPTY;
    }

    public ArrayList checkMinMaxBounds() {
        return null;
    }

}