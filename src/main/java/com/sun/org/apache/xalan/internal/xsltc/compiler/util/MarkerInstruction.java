



package com.sun.org.apache.xalan.internal.xsltc.compiler.util;
import java.io.DataOutputStream;
import java.io.IOException;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.Visitor;


abstract class MarkerInstruction extends Instruction {

    public MarkerInstruction() {
        super(Constants.UNDEFINED, (short) 0);
    }


    public void accept(Visitor v) {
    }


    final public int consumeStack(ConstantPoolGen cpg) {
        return 0;
    }

    final public int produceStack(ConstantPoolGen cpg) {
        return 0;
    }


    public Instruction copy() {
        return this;
    }

    final public void dump(DataOutputStream out) throws IOException {
    }
}
