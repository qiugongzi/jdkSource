



package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.classfile.Method;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ClassGen;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Constants;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Parser;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Stylesheet;


public class ClassGenerator extends ClassGen {
    protected final static int TRANSLET_INDEX = 0;
    protected static int INVALID_INDEX  = -1;

    private Stylesheet _stylesheet;
    private final Parser _parser;               private final Instruction _aloadTranslet;
    private final String _domClass;
    private final String _domClassSig;
    private final String _applyTemplatesSig;
        private final String _applyTemplatesSigForImport;

    public ClassGenerator(String class_name, String super_class_name,
                          String file_name,
                          int access_flags, String[] interfaces,
                          Stylesheet stylesheet) {
        super(class_name, super_class_name, file_name,
              access_flags, interfaces);
        _stylesheet = stylesheet;
        _parser = stylesheet.getParser();
        _aloadTranslet = new ALOAD(TRANSLET_INDEX);

        if (stylesheet.isMultiDocument()) {
            _domClass = "com.sun.org.apache.xalan.internal.xsltc.dom.MultiDOM";
            _domClassSig = "Lcom/sun/org/apache/xalan/internal/xsltc/dom/MultiDOM;";
        }
        else {
            _domClass = "com.sun.org.apache.xalan.internal.xsltc.dom.DOMAdapter";
            _domClassSig = "Lcom/sun/org/apache/xalan/internal/xsltc/dom/DOMAdapter;";
        }
        _applyTemplatesSig = "("
            + Constants.DOM_INTF_SIG
            + Constants.NODE_ITERATOR_SIG
            + Constants.TRANSLET_OUTPUT_SIG
            + ")V";

    _applyTemplatesSigForImport = "("
        + Constants.DOM_INTF_SIG
        + Constants.NODE_ITERATOR_SIG
        + Constants.TRANSLET_OUTPUT_SIG
        + Constants.NODE_FIELD_SIG
        + ")V";
    }

    public final Parser getParser() {
        return _parser;
    }

    public final Stylesheet getStylesheet() {
        return _stylesheet;
    }


    public final String getClassName() {
        return _stylesheet.getClassName();
    }

    public Instruction loadTranslet() {
        return _aloadTranslet;
    }

    public final String getDOMClass() {
        return _domClass;
    }

    public final String getDOMClassSig() {
        return _domClassSig;
    }

    public final String getApplyTemplatesSig() {
        return _applyTemplatesSig;
    }

    public final String getApplyTemplatesSigForImport() {
    return _applyTemplatesSigForImport;
    }


    public boolean isExternal() {
        return false;
    }
    public void addMethod(MethodGenerator methodGen) {
        Method[] methodsToAdd = methodGen.getGeneratedMethods(this);
        for (int i = 0; i < methodsToAdd.length; i++) {
            addMethod(methodsToAdd[i]);
}
    }
}
