


package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.util.NamespaceSupport;


public class SchemaNamespaceSupport
    extends NamespaceSupport {

    public SchemaNamespaceSupport () {
        super();
    } public SchemaNamespaceSupport(SchemaNamespaceSupport nSupport) {
        fNamespaceSize = nSupport.fNamespaceSize;
        if (fNamespace.length < fNamespaceSize)
            fNamespace = new String[fNamespaceSize];
        System.arraycopy(nSupport.fNamespace, 0, fNamespace, 0, fNamespaceSize);
        fCurrentContext = nSupport.fCurrentContext;
        if (fContext.length <= fCurrentContext)
            fContext = new int[fCurrentContext+1];
        System.arraycopy(nSupport.fContext, 0, fContext, 0, fCurrentContext+1);
    } public void setEffectiveContext (String [] namespaceDecls) {
        if(namespaceDecls == null || namespaceDecls.length == 0) return;
        pushContext();
        int newSize = fNamespaceSize + namespaceDecls.length;
        if (fNamespace.length < newSize) {
            String[] tempNSArray = new String[newSize];
            System.arraycopy(fNamespace, 0, tempNSArray, 0, fNamespace.length);
            fNamespace = tempNSArray;
        }
        System.arraycopy(namespaceDecls, 0, fNamespace, fNamespaceSize,
                         namespaceDecls.length);
        fNamespaceSize = newSize;
    } public String [] getEffectiveLocalContext() {
        String[] returnVal = null;
        if (fCurrentContext >= 3) {
            int bottomLocalContext = fContext[3];
            int copyCount = fNamespaceSize - bottomLocalContext;
            if (copyCount > 0) {
                returnVal = new String[copyCount];
                System.arraycopy(fNamespace, bottomLocalContext, returnVal, 0,
                                 copyCount);
            }
        }
        return returnVal;
    } public void makeGlobal() {
        if (fCurrentContext >= 3) {
            fCurrentContext = 3;
            fNamespaceSize = fContext[3];
        }
    } }