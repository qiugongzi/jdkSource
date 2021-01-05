


package com.sun.org.apache.xerces.internal.impl.xs.models;

import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.impl.xs.XSElementDecl;
import com.sun.org.apache.xerces.internal.impl.xs.SubstitutionGroupHandler;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaException;
import com.sun.org.apache.xerces.internal.impl.xs.XSConstraints;

import java.util.Vector;
import java.util.ArrayList;


public class XSAllCM implements XSCMValidator {

    private static final short STATE_START = 0;
    private static final short STATE_VALID = 1;
    private static final short STATE_CHILD = 1;


    private XSElementDecl fAllElements[];
    private boolean fIsOptionalElement[];
    private boolean fHasOptionalContent = false;
    private int fNumElements = 0;

    public XSAllCM (boolean hasOptionalContent, int size) {
        fHasOptionalContent = hasOptionalContent;
        fAllElements = new XSElementDecl[size];
        fIsOptionalElement = new boolean[size];
    }

    public void addElement (XSElementDecl element, boolean isOptional) {
        fAllElements[fNumElements] = element;
        fIsOptionalElement[fNumElements] = isOptional;
        fNumElements++;
    }


    public int[] startContentModel() {

        int[] state = new int[fNumElements + 1];

        for (int i = 0; i <= fNumElements; i++) {
            state[i] = STATE_START;
        }
        return state;
    }

    Object findMatchingDecl(QName elementName, SubstitutionGroupHandler subGroupHandler) {
        Object matchingDecl = null;
        for (int i = 0; i < fNumElements; i++) {
            matchingDecl = subGroupHandler.getMatchingElemDecl(elementName, fAllElements[i]);
            if (matchingDecl != null)
                break;
        }
        return matchingDecl;
    }


    public Object oneTransition (QName elementName, int[] currentState, SubstitutionGroupHandler subGroupHandler) {

        if (currentState[0] < 0) {
            currentState[0] = XSCMValidator.SUBSEQUENT_ERROR;
            return findMatchingDecl(elementName, subGroupHandler);
        }

        currentState[0] = STATE_CHILD;

        Object matchingDecl = null;

        for (int i = 0; i < fNumElements; i++) {
            if (currentState[i+1] != STATE_START)
                continue;
            matchingDecl = subGroupHandler.getMatchingElemDecl(elementName, fAllElements[i]);
            if (matchingDecl != null) {
                currentState[i+1] = STATE_VALID;
                return matchingDecl;
            }
        }

        currentState[0] = XSCMValidator.FIRST_ERROR;
        return findMatchingDecl(elementName, subGroupHandler);
    }



    public boolean endContentModel (int[] currentState) {

        int state = currentState[0];

        if (state == XSCMValidator.FIRST_ERROR || state == XSCMValidator.SUBSEQUENT_ERROR) {
            return false;
        }

        if (fHasOptionalContent && state == STATE_START) {
            return true;
        }

        for (int i = 0; i < fNumElements; i++) {
            if (!fIsOptionalElement[i] && currentState[i+1] == STATE_START)
                return false;
        }

        return true;
    }


    public boolean checkUniqueParticleAttribution(SubstitutionGroupHandler subGroupHandler) throws XMLSchemaException {
        for (int i = 0; i < fNumElements; i++) {
            for (int j = i+1; j < fNumElements; j++) {
                if (XSConstraints.overlapUPA(fAllElements[i], fAllElements[j], subGroupHandler)) {
                    throw new XMLSchemaException("cos-nonambig", new Object[]{fAllElements[i].toString(),
                                                                              fAllElements[j].toString()});
                }
            }
        }

        return false;
    }


    public Vector whatCanGoHere(int[] state) {
        Vector ret = new Vector();
        for (int i = 0; i < fNumElements; i++) {
            if (state[i+1] == STATE_START)
                ret.addElement(fAllElements[i]);
        }
        return ret;
    }

    public ArrayList checkMinMaxBounds() {
        return null;
    }

}