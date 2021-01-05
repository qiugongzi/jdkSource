


package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import java.util.HashMap;
import java.util.Map;


public class DTDGrammarBucket {

    protected Map<XMLDTDDescription, DTDGrammar> fGrammars;

    protected DTDGrammar fActiveGrammar;

    protected boolean fIsStandalone;

    public DTDGrammarBucket() {
        fGrammars = new HashMap<>();
    } public void putGrammar(DTDGrammar grammar) {
        XMLDTDDescription desc = (XMLDTDDescription)grammar.getGrammarDescription();
        fGrammars.put(desc, grammar);
    } public DTDGrammar getGrammar(XMLGrammarDescription desc) {
        return fGrammars.get((XMLDTDDescription)desc);
    } public void clear() {
        fGrammars.clear();
        fActiveGrammar = null;
        fIsStandalone = false;
    } void setStandalone(boolean standalone) {
        fIsStandalone = standalone;
    }

    boolean getStandalone() {
        return fIsStandalone;
    }

    void setActiveGrammar (DTDGrammar grammar) {
        fActiveGrammar = grammar;
    }
    DTDGrammar getActiveGrammar () {
        return fActiveGrammar;
    }
}