


package com.sun.org.apache.xerces.internal.impl.dtd;

import java.util.ArrayList;
import java.util.Vector;

import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;


public class XMLDTDDescription extends XMLResourceIdentifierImpl
        implements com.sun.org.apache.xerces.internal.xni.grammars.XMLDTDDescription {

    protected String fRootName = null;

    protected ArrayList fPossibleRoots = null;

    public XMLDTDDescription(XMLResourceIdentifier id, String rootName) {
        this.setValues(id.getPublicId(), id.getLiteralSystemId(),
                id.getBaseSystemId(), id.getExpandedSystemId());
        this.fRootName = rootName;
        this.fPossibleRoots = null;
    } public XMLDTDDescription(String publicId, String literalId,
                String baseId, String expandedId, String rootName) {
        this.setValues(publicId, literalId, baseId, expandedId);
        this.fRootName = rootName;
        this.fPossibleRoots = null;
    } public XMLDTDDescription(XMLInputSource source) {
        this.setValues(source.getPublicId(), null,
                source.getBaseSystemId(), source.getSystemId());
        this.fRootName = null;
        this.fPossibleRoots = null;
    } public String getGrammarType () {
        return XMLGrammarDescription.XML_DTD;
    } public String getRootName() {
        return fRootName;
    } public void setRootName(String rootName) {
        fRootName = rootName;
        fPossibleRoots = null;
    }


    public void setPossibleRoots(ArrayList possibleRoots) {
        fPossibleRoots = possibleRoots;
    }


    public void setPossibleRoots(Vector possibleRoots) {
        fPossibleRoots = (possibleRoots != null) ? new ArrayList(possibleRoots) : null;
    }


    public boolean equals(Object desc) {
        if (!(desc instanceof XMLGrammarDescription)) return false;
        if (!getGrammarType().equals(((XMLGrammarDescription)desc).getGrammarType())) {
            return false;
        }
        XMLDTDDescription dtdDesc = (XMLDTDDescription)desc;
        if (fRootName != null) {
            if ((dtdDesc.fRootName) != null && !dtdDesc.fRootName.equals(fRootName)) {
                return false;
            }
            else if (dtdDesc.fPossibleRoots != null && !dtdDesc.fPossibleRoots.contains(fRootName)) {
                return false;
            }
        }
        else if (fPossibleRoots != null) {
            if (dtdDesc.fRootName != null) {
                if (!fPossibleRoots.contains(dtdDesc.fRootName)) {
                    return false;
                }
            }
            else if (dtdDesc.fPossibleRoots == null) {
                return false;
            }
            else {
                boolean found = false;
                final int size = fPossibleRoots.size();
                for (int i = 0; i < size; ++i) {
                    String root = (String) fPossibleRoots.get(i);
                    found = dtdDesc.fPossibleRoots.contains(root);
                    if (found) break;
                }
                if (!found) return false;
            }
        }
        if (fExpandedSystemId != null) {
            if (!fExpandedSystemId.equals(dtdDesc.fExpandedSystemId)) {
                return false;
            }
        }
        else if (dtdDesc.fExpandedSystemId != null) {
            return false;
        }
        if (fPublicId != null) {
            if (!fPublicId.equals(dtdDesc.fPublicId)) {
                return false;
            }
        }
        else if (dtdDesc.fPublicId != null) {
            return false;
        }
        return true;
    }


    public int hashCode() {
        if (fExpandedSystemId != null) {
            return fExpandedSystemId.hashCode();
        }
        if (fPublicId != null) {
            return fPublicId.hashCode();
        }
        return 0;
    }
}