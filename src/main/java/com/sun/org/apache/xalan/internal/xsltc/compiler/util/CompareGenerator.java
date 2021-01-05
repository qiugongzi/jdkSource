



package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ACONST_NULL;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Constants;


public final class CompareGenerator extends MethodGenerator {

    private static int DOM_INDEX      = 1;
    private static int CURRENT_INDEX  = 2;
    private static int LEVEL_INDEX    = 3;
    private static int TRANSLET_INDEX = 4;
    private static int LAST_INDEX     = 5;
    private int ITERATOR_INDEX = 6;

    private final Instruction _iloadCurrent;
    private final Instruction _istoreCurrent;
    private final Instruction _aloadDom;
    private final Instruction _iloadLast;
    private final Instruction _aloadIterator;
    private final Instruction _astoreIterator;

    public CompareGenerator(int access_flags, Type return_type,
                            Type[] arg_types, String[] arg_names,
                            String method_name, String class_name,
                            InstructionList il, ConstantPoolGen cp) {
        super(access_flags, return_type, arg_types, arg_names, method_name,
              class_name, il, cp);

        _iloadCurrent = new ILOAD(CURRENT_INDEX);
        _istoreCurrent = new ISTORE(CURRENT_INDEX);
        _aloadDom = new ALOAD(DOM_INDEX);
        _iloadLast = new ILOAD(LAST_INDEX);

        LocalVariableGen iterator =
            addLocalVariable("iterator",
                             Util.getJCRefType(Constants.NODE_ITERATOR_SIG),
                             null, null);
        ITERATOR_INDEX = iterator.getIndex();
        _aloadIterator = new ALOAD(ITERATOR_INDEX);
        _astoreIterator = new ASTORE(ITERATOR_INDEX);
        il.append(new ACONST_NULL());
        il.append(storeIterator());
    }

    public Instruction loadLastNode() {
        return _iloadLast;
    }

    public Instruction loadCurrentNode() {
        return _iloadCurrent;
    }

    public Instruction storeCurrentNode() {
        return _istoreCurrent;
    }

    public Instruction loadDOM() {
        return _aloadDom;
    }

    public int getHandlerIndex() {
        return INVALID_INDEX;           }

    public int getIteratorIndex() {
        return INVALID_INDEX;
    }

    public Instruction storeIterator() {
        return _astoreIterator;
    }

    public Instruction loadIterator() {
        return _aloadIterator;
    }

    public int getLocalIndex(String name) {
        if (name.equals("current")) {
            return CURRENT_INDEX;
        }
        return super.getLocalIndex(name);
    }
}
