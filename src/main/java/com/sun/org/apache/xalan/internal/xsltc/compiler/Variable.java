



package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.bcel.internal.generic.ACONST_NULL;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.DCONST;
import com.sun.org.apache.bcel.internal.generic.ICONST;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUTFIELD;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.IntType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.RealType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

final class Variable extends VariableBase {

    public int getIndex() {
        return (_local != null) ? _local.getIndex() : -1;
    }


    public void parseContents(Parser parser) {
        super.parseContents(parser);

        SyntaxTreeNode parent = getParent();
        if (parent instanceof Stylesheet) {
            _isLocal = false;
            Variable var = parser.getSymbolTable().lookupVariable(_name);
            if (var != null) {
                final int us = this.getImportPrecedence();
                final int them = var.getImportPrecedence();
                if (us == them) {
                    final String name = _name.toString();
                    reportError(this, parser, ErrorMsg.VARIABLE_REDEF_ERR,name);
                }
                else if (them > us) {
                    _ignore = true;
                    copyReferences(var);
                    return;
                }
                else {
                    var.copyReferences(this);
                    var.disable();
                }
                }
            ((Stylesheet)parent).addVariable(this);
            parser.getSymbolTable().addVariable(this);
        }
        else {
            _isLocal = true;
        }
    }


    public Type typeCheck(SymbolTable stable) throws TypeCheckError {

        if (_select != null) {
            _type = _select.typeCheck(stable);
        }
        else if (hasContents()) {
            typeCheckContents(stable);
            _type = Type.ResultTree;
        }
        else {
            _type = Type.Reference;
        }
        return Type.Void;
    }


    public void initialize(ClassGenerator classGen, MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();

        if (isLocal() && !_refs.isEmpty()) {
            if (_local == null) {
                _local = methodGen.addLocalVariable2(getEscapedName(),
                                                     _type.toJCType(),
                                                     null);
            }
            if ((_type instanceof IntType) ||
                (_type instanceof NodeType) ||
                (_type instanceof BooleanType))
                il.append(new ICONST(0)); else if (_type instanceof RealType)
                il.append(new DCONST(0)); else
                il.append(new ACONST_NULL()); _local.setStart(il.append(_type.STORE(_local.getIndex())));

        }
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();

        if (_refs.isEmpty()) {
            _ignore = true;
        }

        if (_ignore) return;
        _ignore = true;

        final String name = getEscapedName();

        if (isLocal()) {
            translateValue(classGen, methodGen);

            boolean createLocal = _local == null;
            if (createLocal) {
                mapRegister(methodGen);
            }
            InstructionHandle storeInst =
            il.append(_type.STORE(_local.getIndex()));

            if (createLocal) {
                _local.setStart(storeInst);
        }
        }
        else {
            String signature = _type.toSignature();

            if (classGen.containsField(name) == null) {
                classGen.addField(new Field(ACC_PUBLIC,
                                            cpg.addUtf8(name),
                                            cpg.addUtf8(signature),
                                            null, cpg.getConstantPool()));

                il.append(classGen.loadTranslet());
                translateValue(classGen, methodGen);
                il.append(new PUTFIELD(cpg.addFieldref(classGen.getClassName(),
                                                       name, signature)));
            }
        }
    }
}
