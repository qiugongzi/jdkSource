


package com.sun.org.apache.xerces.internal.impl.xs;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


public class XSGrammarBucket {

    Map<String, SchemaGrammar> fGrammarRegistry = new HashMap();
    SchemaGrammar fNoNSGrammar = null;


    public SchemaGrammar getGrammar(String namespace) {
        if (namespace == null)
            return fNoNSGrammar;
        return (SchemaGrammar)fGrammarRegistry.get(namespace);
    }


    public void putGrammar(SchemaGrammar grammar) {
        if (grammar.getTargetNamespace() == null)
            fNoNSGrammar = grammar;
        else
            fGrammarRegistry.put(grammar.getTargetNamespace(), grammar);
    }


    public boolean putGrammar(SchemaGrammar grammar, boolean deep) {
        SchemaGrammar sg = getGrammar(grammar.fTargetNamespace);
        if (sg != null) {
            return sg == grammar;
        }
        if (!deep) {
            putGrammar(grammar);
            return true;
        }

        Vector currGrammars = (Vector)grammar.getImportedGrammars();
        if (currGrammars == null) {
            putGrammar(grammar);
            return true;
        }

        Vector grammars = ((Vector)currGrammars.clone());
        SchemaGrammar sg1, sg2;
        Vector gs;
        for (int i = 0; i < grammars.size(); i++) {
            sg1 = (SchemaGrammar)grammars.elementAt(i);
            sg2 = getGrammar(sg1.fTargetNamespace);
            if (sg2 == null) {
                gs = sg1.getImportedGrammars();
                if(gs == null) continue;
                for (int j = gs.size() - 1; j >= 0; j--) {
                    sg2 = (SchemaGrammar)gs.elementAt(j);
                    if (!grammars.contains(sg2))
                        grammars.addElement(sg2);
                }
            }
            else if (sg2 != sg1) {
                return false;
            }
        }

        putGrammar(grammar);
        for (int i = grammars.size() - 1; i >= 0; i--)
            putGrammar((SchemaGrammar)grammars.elementAt(i));

        return true;
    }


    public boolean putGrammar(SchemaGrammar grammar, boolean deep, boolean ignoreConflict) {
        if (!ignoreConflict) {
            return putGrammar(grammar, deep);
        }

        SchemaGrammar sg = getGrammar(grammar.fTargetNamespace);
        if (sg == null) {
            putGrammar(grammar);
        }

        if (!deep) {
            return true;
        }

        Vector currGrammars = (Vector)grammar.getImportedGrammars();
        if (currGrammars == null) {
            return true;
        }

        Vector grammars = ((Vector)currGrammars.clone());
        SchemaGrammar sg1, sg2;
        Vector gs;
        for (int i = 0; i < grammars.size(); i++) {
            sg1 = (SchemaGrammar)grammars.elementAt(i);
            sg2 = getGrammar(sg1.fTargetNamespace);
            if (sg2 == null) {
                gs = sg1.getImportedGrammars();
                if(gs == null) continue;
                for (int j = gs.size() - 1; j >= 0; j--) {
                    sg2 = (SchemaGrammar)gs.elementAt(j);
                    if (!grammars.contains(sg2))
                        grammars.addElement(sg2);
                }
            }
            else  {
                grammars.remove(sg1);
            }
        }

        for (int i = grammars.size() - 1; i >= 0; i--) {
            putGrammar((SchemaGrammar)grammars.elementAt(i));
        }

        return true;
    }


    public SchemaGrammar[] getGrammars() {
        int count = fGrammarRegistry.size() + (fNoNSGrammar==null ? 0 : 1);
        SchemaGrammar[] grammars = new SchemaGrammar[count];
        int i = 0;
        for(Map.Entry<String, SchemaGrammar> entry : fGrammarRegistry.entrySet()){
            grammars[i++] = entry.getValue();
        }

        if (fNoNSGrammar != null)
            grammars[count-1] = fNoNSGrammar;
        return grammars;
    }


    public void reset() {
        fNoNSGrammar = null;
        fGrammarRegistry.clear();
    }

}