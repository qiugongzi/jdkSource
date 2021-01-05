



package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

import com.sun.org.apache.xml.internal.serializer.ElemDesc;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;


final class LiteralAttribute extends Instruction {

    private final String  _name;         private final AttributeValue _value; public LiteralAttribute(String name, String value, Parser parser,
        SyntaxTreeNode parent)
    {
        _name = name;
        setParent(parent);
        _value = AttributeValue.create(this, value, parser);
    }

    public void display(int indent) {
        indent(indent);
        Util.println("LiteralAttribute name=" + _name + " value=" + _value);
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        _value.typeCheck(stable);
        typeCheckContents(stable);
        return Type.Void;
    }

    protected boolean contextDependent() {
        return _value.contextDependent();
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();

        il.append(methodGen.loadHandler());
        il.append(new PUSH(cpg, _name));
        _value.translate(classGen, methodGen);

        SyntaxTreeNode parent = getParent();
        if (parent instanceof LiteralElement
            && ((LiteralElement)parent).allAttributesUnique()) {

            int flags = 0;
            boolean isHTMLAttrEmpty = false;
            ElemDesc elemDesc = ((LiteralElement)parent).getElemDesc();

            if (elemDesc != null) {
                if (elemDesc.isAttrFlagSet(_name, ElemDesc.ATTREMPTY)) {
                    flags = flags | SerializationHandler.HTML_ATTREMPTY;
                    isHTMLAttrEmpty = true;
                }
                else if (elemDesc.isAttrFlagSet(_name, ElemDesc.ATTRURL)) {
                    flags = flags | SerializationHandler.HTML_ATTRURL;
                }
            }

            if (_value instanceof SimpleAttributeValue) {
                String attrValue = ((SimpleAttributeValue)_value).toString();

                if (!hasBadChars(attrValue) && !isHTMLAttrEmpty) {
                    flags = flags | SerializationHandler.NO_BAD_CHARS;
                }
            }

            il.append(new PUSH(cpg, flags));
            il.append(methodGen.uniqueAttribute());
        }
        else {
            il.append(methodGen.attribute());
        }
    }


    private boolean hasBadChars(String value) {
        char[] chars = value.toCharArray();
        int size = chars.length;
        for (int i = 0; i < size; i++) {
            char ch = chars[i];
            if (ch < 32 || 126 < ch || ch == '<' || ch == '>' || ch == '&' || ch == '\"')
                return true;
        }
        return false;
    }


    public String getName() {
        return _name;
    }


    public AttributeValue getValue() {
        return _value;
    }

}
