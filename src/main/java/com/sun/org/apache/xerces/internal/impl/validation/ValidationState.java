


package com.sun.org.apache.xerces.internal.impl.validation;

import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import java.util.ArrayList;
import java.util.Locale;


public class ValidationState implements ValidationContext {

    private boolean fExtraChecking              = true;
    private boolean fFacetChecking              = true;
    private boolean fNormalize                  = true;
    private boolean fNamespaces                 = true;

    private EntityState fEntityState            = null;
    private NamespaceContext fNamespaceContext  = null;
    private SymbolTable fSymbolTable            = null;
    private Locale fLocale                      = null;

    private ArrayList<String> fIdList;
    private ArrayList<String> fIdRefList;

    public void setExtraChecking(boolean newValue) {
        fExtraChecking = newValue;
    }

    public void setFacetChecking(boolean newValue) {
        fFacetChecking = newValue;
    }

    public void setNormalizationRequired (boolean newValue) {
          fNormalize = newValue;
    }

    public void setUsingNamespaces (boolean newValue) {
          fNamespaces = newValue;
    }

    public void setEntityState(EntityState state) {
        fEntityState = state;
    }

    public void setNamespaceSupport(NamespaceContext namespace) {
        fNamespaceContext = namespace;
    }

    public void setSymbolTable(SymbolTable sTable) {
        fSymbolTable = sTable;
    }


    public String checkIDRefID () {
        if (fIdList == null) {
            if (fIdRefList != null) {
                return fIdRefList.get(0);
            }
        }

        if (fIdRefList != null) {
            String key;
            for (int i = 0; i < fIdRefList.size(); i++) {
                key = fIdRefList.get(i);
                if (!fIdList.contains(key)) {
                      return key;
                }
            }
        }
        return null;
    }

    public void reset () {
        fExtraChecking = true;
        fFacetChecking = true;
        fNamespaces = true;
        fIdList = null;
        fIdRefList = null;
        fEntityState = null;
        fNamespaceContext = null;
        fSymbolTable = null;
    }


    public void resetIDTables() {
        fIdList = null;
        fIdRefList = null;
    }

    public boolean needExtraChecking() {
        return fExtraChecking;
    }

    public boolean needFacetChecking() {
        return fFacetChecking;
    }

    public boolean needToNormalize (){
        return fNormalize;
    }

    public boolean useNamespaces() {
        return fNamespaces;
    }

    public boolean isEntityDeclared (String name) {
        if (fEntityState !=null) {
            return fEntityState.isEntityDeclared(getSymbol(name));
        }
        return false;
    }
    public boolean isEntityUnparsed (String name) {
        if (fEntityState !=null) {
            return fEntityState.isEntityUnparsed(getSymbol(name));
        }
        return false;
    }

    public boolean isIdDeclared(String name) {
        if (fIdList == null) return false;
        return fIdList.contains(name);
    }
    public void addId(String name) {
        if (fIdList == null) fIdList = new ArrayList();
        fIdList.add(name);
    }

    public void addIdRef(String name) {
        if (fIdRefList == null) fIdRefList = new ArrayList();
        fIdRefList.add(name);
    }
    public String getSymbol (String symbol) {
        if (fSymbolTable != null)
            return fSymbolTable.addSymbol(symbol);
        return symbol.intern();
    }
    public String getURI(String prefix) {
        if (fNamespaceContext !=null) {
            return fNamespaceContext.getURI(prefix);
        }
        return null;
    }

    public void setLocale(Locale locale) {
        fLocale = locale;
    }

    public Locale getLocale() {
        return fLocale;
    }
}
