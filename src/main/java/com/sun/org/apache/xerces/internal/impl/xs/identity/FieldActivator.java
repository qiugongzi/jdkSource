


package com.sun.org.apache.xerces.internal.impl.xs.identity;



public interface FieldActivator {

    public void startValueScopeFor(IdentityConstraint identityConstraint,
            int initialDepth);


    public XPathMatcher activateField(Field field, int initialDepth);


    public void setMayMatch(Field field, Boolean state);


    public Boolean mayMatch(Field field);


    public void endValueScopeFor(IdentityConstraint identityConstraint, int initialDepth);

}