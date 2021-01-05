



package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFNULL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Constants;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;


public final class ObjectType extends Type {

    private String _javaClassName = "java.lang.Object";
    private Class  _clazz = java.lang.Object.class;


    protected ObjectType(String javaClassName) {
        _javaClassName = javaClassName;

        try {
          _clazz = ObjectFactory.findProviderClass(javaClassName, true);
        }
        catch (ClassNotFoundException e) {
          _clazz = null;
        }
    }

    protected ObjectType(Class clazz) {
        _clazz = clazz;
        _javaClassName = clazz.getName();
    }


    public int hashCode() {
        return java.lang.Object.class.hashCode();
    }

    public boolean equals(Object obj) {
        return (obj instanceof ObjectType);
    }

    public String getJavaClassName() {
        return _javaClassName;
    }

    public Class getJavaClass() {
        return _clazz;
    }

    public String toString() {
        return _javaClassName;
    }

    public boolean identicalTo(Type other) {
        return this == other;
    }

    public String toSignature() {
        final StringBuffer result = new StringBuffer("L");
        result.append(_javaClassName.replace('.', '/')).append(';');
        return result.toString();
    }

    public com.sun.org.apache.bcel.internal.generic.Type toJCType() {
        return Util.getJCRefType(toSignature());
    }


    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen,
                            Type type) {
        if (type == Type.String) {
            translateTo(classGen, methodGen, (StringType) type);
        }
        else {
            ErrorMsg err = new ErrorMsg(ErrorMsg.DATA_CONVERSION_ERR,
                                        toString(), type.toString());
            classGen.getParser().reportError(Constants.FATAL, err);
        }
    }


    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen,
                            StringType type) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();

        il.append(DUP);
        final BranchHandle ifNull = il.append(new IFNULL(null));
        il.append(new INVOKEVIRTUAL(cpg.addMethodref(_javaClassName,
                                                    "toString",
                                                    "()" + STRING_SIG)));
        final BranchHandle gotobh = il.append(new GOTO(null));
        ifNull.setTarget(il.append(POP));
        il.append(new PUSH(cpg, ""));
        gotobh.setTarget(il.append(NOP));
    }


    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen,
                            Class clazz) {
        if (clazz.isAssignableFrom(_clazz))
            methodGen.getInstructionList().append(NOP);
        else {
            ErrorMsg err = new ErrorMsg(ErrorMsg.DATA_CONVERSION_ERR,
                               toString(), clazz.getClass().toString());
            classGen.getParser().reportError(Constants.FATAL, err);
        }
    }


    public void translateFrom(ClassGenerator classGen,
                              MethodGenerator methodGen, Class clazz) {
        methodGen.getInstructionList().append(NOP);
    }

    public Instruction LOAD(int slot) {
        return new ALOAD(slot);
    }

    public Instruction STORE(int slot) {
        return new ASTORE(slot);
    }
}
