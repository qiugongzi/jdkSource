


package com.sun.org.apache.xalan.internal.xsltc.compiler;

import java.util.ArrayList;
import java.util.Vector;

import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ANEWARRAY;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.NOP;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.PUTFIELD;
import com.sun.org.apache.bcel.internal.generic.TABLESWITCH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.CompareGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.IntType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSortRecordFactGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSortRecordGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.dtm.Axis;



final class Sort extends Instruction implements Closure {

    private Expression     _select;
    private AttributeValue _order;
    private AttributeValue _caseOrder;
    private AttributeValue _dataType;
    private String         _lang; private String _className = null;
    private ArrayList<VariableRefBase> _closureVars = null;
    private boolean _needsSortRecordFactory = false;

    public boolean inInnerClass() {
        return (_className != null);
    }


    public Closure getParentClosure() {
        return null;
    }


    public String getInnerClassName() {
        return _className;
    }


    public void addVariable(VariableRefBase variableRef) {
        if (_closureVars == null) {
            _closureVars = new ArrayList<>();
        }

        if (!_closureVars.contains(variableRef)) {
            _closureVars.add(variableRef);
            _needsSortRecordFactory = true;
        }
    }

    private void setInnerClassName(String className) {
        _className = className;
    }


    public void parseContents(Parser parser) {

        final SyntaxTreeNode parent = getParent();
        if (!(parent instanceof ApplyTemplates) &&
            !(parent instanceof ForEach)) {
            reportError(this, parser, ErrorMsg.STRAY_SORT_ERR, null);
            return;
        }

        _select = parser.parseExpression(this, "select", "string(.)");

        String val = getAttribute("order");
        if (val.length() == 0) val = "ascending";
        _order = AttributeValue.create(this, val, parser);

        val = getAttribute("data-type");
        if (val.length() == 0) {
            try {
                final Type type = _select.typeCheck(parser.getSymbolTable());
                if (type instanceof IntType)
                    val = "number";
                else
                    val = "text";
            }
            catch (TypeCheckError e) {
                val = "text";
            }
        }
        _dataType = AttributeValue.create(this, val, parser);

         _lang =  getAttribute("lang"); val = getAttribute("case-order");
    _caseOrder = AttributeValue.create(this, val, parser);

    }


    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        final Type tselect = _select.typeCheck(stable);

        if (!(tselect instanceof StringType)) {
            _select = new CastExpr(_select, Type.String);
        }

        _order.typeCheck(stable);
        _caseOrder.typeCheck(stable);
        _dataType.typeCheck(stable);
        return Type.Void;
    }


    public void translateSortType(ClassGenerator classGen,
                                  MethodGenerator methodGen) {
        _dataType.translate(classGen, methodGen);
    }

    public void translateSortOrder(ClassGenerator classGen,
                                   MethodGenerator methodGen) {
        _order.translate(classGen, methodGen);
    }

     public void translateCaseOrder(ClassGenerator classGen,
                   MethodGenerator methodGen) {
    _caseOrder.translate(classGen, methodGen);
    }

    public void translateLang(ClassGenerator classGen,
                   MethodGenerator methodGen) {
    final ConstantPoolGen cpg = classGen.getConstantPool();
    final InstructionList il = methodGen.getInstructionList();
    il.append(new PUSH(cpg, _lang)); }


    public void translateSelect(ClassGenerator classGen,
                                MethodGenerator methodGen) {
        _select.translate(classGen,methodGen);
    }


    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        }


    public static void translateSortIterator(ClassGenerator classGen,
                                      MethodGenerator methodGen,
                                      Expression nodeSet,
                                      Vector<Sort> sortObjects)
    {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();

        final int init = cpg.addMethodref(SORT_ITERATOR, "<init>",
                                          "("
                                          + NODE_ITERATOR_SIG
                                          + NODE_SORT_FACTORY_SIG
                                          + ")V");

        LocalVariableGen nodesTemp =
            methodGen.addLocalVariable("sort_tmp1",
                                       Util.getJCRefType(NODE_ITERATOR_SIG),
                                       null, null);

        LocalVariableGen sortRecordFactoryTemp =
            methodGen.addLocalVariable("sort_tmp2",
                                      Util.getJCRefType(NODE_SORT_FACTORY_SIG),
                                      null, null);

        if (nodeSet == null) {  final int children = cpg.addInterfaceMethodref(DOM_INTF,
                                                           "getAxisIterator",
                                                           "(I)"+
                                                           NODE_ITERATOR_SIG);
            il.append(methodGen.loadDOM());
            il.append(new PUSH(cpg, Axis.CHILD));
            il.append(new INVOKEINTERFACE(children, 2));
        }
        else {
            nodeSet.translate(classGen, methodGen);
        }

        nodesTemp.setStart(il.append(new ASTORE(nodesTemp.getIndex())));

        compileSortRecordFactory(sortObjects, classGen, methodGen);
        sortRecordFactoryTemp.setStart(
                il.append(new ASTORE(sortRecordFactoryTemp.getIndex())));

        il.append(new NEW(cpg.addClass(SORT_ITERATOR)));
        il.append(DUP);
        nodesTemp.setEnd(il.append(new ALOAD(nodesTemp.getIndex())));
        sortRecordFactoryTemp.setEnd(
                il.append(new ALOAD(sortRecordFactoryTemp.getIndex())));
        il.append(new INVOKESPECIAL(init));
    }



    public static void compileSortRecordFactory(Vector<Sort> sortObjects,
        ClassGenerator classGen, MethodGenerator methodGen)
    {
        String sortRecordClass =
            compileSortRecord(sortObjects, classGen, methodGen);

        boolean needsSortRecordFactory = false;
        final int nsorts = sortObjects.size();
        for (int i = 0; i < nsorts; i++) {
            final Sort sort = sortObjects.elementAt(i);
            needsSortRecordFactory |= sort._needsSortRecordFactory;
        }

        String sortRecordFactoryClass = NODE_SORT_FACTORY;
        if (needsSortRecordFactory) {
            sortRecordFactoryClass =
                compileSortRecordFactory(sortObjects, classGen, methodGen,
                    sortRecordClass);
        }

        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();

        LocalVariableGen sortOrderTemp
                 = methodGen.addLocalVariable("sort_order_tmp",
                                      Util.getJCRefType("[" + STRING_SIG),
                                      null, null);
        il.append(new PUSH(cpg, nsorts));
        il.append(new ANEWARRAY(cpg.addClass(STRING)));
        for (int level = 0; level < nsorts; level++) {
            final Sort sort = (Sort)sortObjects.elementAt(level);
            il.append(DUP);
            il.append(new PUSH(cpg, level));
            sort.translateSortOrder(classGen, methodGen);
            il.append(AASTORE);
        }
        sortOrderTemp.setStart(il.append(new ASTORE(sortOrderTemp.getIndex())));

        LocalVariableGen sortTypeTemp
                 = methodGen.addLocalVariable("sort_type_tmp",
                                      Util.getJCRefType("[" + STRING_SIG),
                                      null, null);
        il.append(new PUSH(cpg, nsorts));
        il.append(new ANEWARRAY(cpg.addClass(STRING)));
        for (int level = 0; level < nsorts; level++) {
            final Sort sort = (Sort)sortObjects.elementAt(level);
            il.append(DUP);
            il.append(new PUSH(cpg, level));
            sort.translateSortType(classGen, methodGen);
            il.append(AASTORE);
        }
        sortTypeTemp.setStart(il.append(new ASTORE(sortTypeTemp.getIndex())));

        LocalVariableGen sortLangTemp
                 = methodGen.addLocalVariable("sort_lang_tmp",
                                      Util.getJCRefType("[" + STRING_SIG),
                                      null, null);
        il.append(new PUSH(cpg, nsorts));
        il.append(new ANEWARRAY(cpg.addClass(STRING)));
        for (int level = 0; level < nsorts; level++) {
              final Sort sort = (Sort)sortObjects.elementAt(level);
              il.append(DUP);
              il.append(new PUSH(cpg, level));
              sort.translateLang(classGen, methodGen);
              il.append(AASTORE);
        }
        sortLangTemp.setStart(il.append(new ASTORE(sortLangTemp.getIndex())));

        LocalVariableGen sortCaseOrderTemp
                 = methodGen.addLocalVariable("sort_case_order_tmp",
                                      Util.getJCRefType("[" + STRING_SIG),
                                      null, null);
        il.append(new PUSH(cpg, nsorts));
        il.append(new ANEWARRAY(cpg.addClass(STRING)));
        for (int level = 0; level < nsorts; level++) {
            final Sort sort = (Sort)sortObjects.elementAt(level);
            il.append(DUP);
            il.append(new PUSH(cpg, level));
            sort.translateCaseOrder(classGen, methodGen);
            il.append(AASTORE);
        }
        sortCaseOrderTemp.setStart(
                il.append(new ASTORE(sortCaseOrderTemp.getIndex())));

        il.append(new NEW(cpg.addClass(sortRecordFactoryClass)));
        il.append(DUP);
        il.append(methodGen.loadDOM());
        il.append(new PUSH(cpg, sortRecordClass));
        il.append(classGen.loadTranslet());

        sortOrderTemp.setEnd(il.append(new ALOAD(sortOrderTemp.getIndex())));
        sortTypeTemp.setEnd(il.append(new ALOAD(sortTypeTemp.getIndex())));
        sortLangTemp.setEnd(il.append(new ALOAD(sortLangTemp.getIndex())));
        sortCaseOrderTemp.setEnd(
                il.append(new ALOAD(sortCaseOrderTemp.getIndex())));

        il.append(new INVOKESPECIAL(
            cpg.addMethodref(sortRecordFactoryClass, "<init>",
                "(" + DOM_INTF_SIG
                    + STRING_SIG
                    + TRANSLET_INTF_SIG
                    + "[" + STRING_SIG
                    + "[" + STRING_SIG
                    + "[" + STRING_SIG
                    + "[" + STRING_SIG + ")V")));

        final ArrayList<VariableRefBase> dups = new ArrayList<>();

        for (int j = 0; j < nsorts; j++) {
            final Sort sort = (Sort) sortObjects.get(j);
            final int length = (sort._closureVars == null) ? 0 :
                sort._closureVars.size();

            for (int i = 0; i < length; i++) {
                VariableRefBase varRef = sort._closureVars.get(i);

                if (dups.contains(varRef)) continue;

                final VariableBase var = varRef.getVariable();

                il.append(DUP);
                il.append(var.loadInstruction());
                il.append(new PUTFIELD(
                        cpg.addFieldref(sortRecordFactoryClass, var.getEscapedName(),
                            var.getType().toSignature())));
                dups.add(varRef);
            }
        }
    }

    public static String compileSortRecordFactory(Vector<Sort> sortObjects,
        ClassGenerator classGen, MethodGenerator methodGen,
        String sortRecordClass)
    {
        final XSLTC xsltc = (sortObjects.firstElement()).getXSLTC();
        final String className = xsltc.getHelperClassName();

        final NodeSortRecordFactGenerator sortRecordFactory =
            new NodeSortRecordFactGenerator(className,
                                        NODE_SORT_FACTORY,
                                        className + ".java",
                                        ACC_PUBLIC | ACC_SUPER | ACC_FINAL,
                                        new String[] {},
                                        classGen.getStylesheet());

        ConstantPoolGen cpg = sortRecordFactory.getConstantPool();

        final int nsorts = sortObjects.size();
        final ArrayList<VariableRefBase> dups = new ArrayList<>();

        for (int j = 0; j < nsorts; j++) {
            final Sort sort = sortObjects.get(j);
            final int length = (sort._closureVars == null) ? 0 :
                sort._closureVars.size();

            for (int i = 0; i < length; i++) {
                final VariableRefBase varRef = sort._closureVars.get(i);

                if (dups.contains(varRef)) continue;

                final VariableBase var = varRef.getVariable();
                sortRecordFactory.addField(new Field(ACC_PUBLIC,
                                           cpg.addUtf8(var.getEscapedName()),
                                           cpg.addUtf8(var.getType().toSignature()),
                                           null, cpg.getConstantPool()));
                dups.add(varRef);
            }
        }

        final com.sun.org.apache.bcel.internal.generic.Type[] argTypes =
            new com.sun.org.apache.bcel.internal.generic.Type[7];
        argTypes[0] = Util.getJCRefType(DOM_INTF_SIG);
        argTypes[1] = Util.getJCRefType(STRING_SIG);
        argTypes[2] = Util.getJCRefType(TRANSLET_INTF_SIG);
        argTypes[3] = Util.getJCRefType("[" + STRING_SIG);
        argTypes[4] = Util.getJCRefType("[" + STRING_SIG);
  argTypes[5] = Util.getJCRefType("[" + STRING_SIG);
  argTypes[6] = Util.getJCRefType("[" + STRING_SIG);

        final String[] argNames = new String[7];
        argNames[0] = DOCUMENT_PNAME;
        argNames[1] = "className";
        argNames[2] = TRANSLET_PNAME;
        argNames[3] = "order";
        argNames[4] = "type";
  argNames[5] = "lang";
  argNames[6] = "case_order";


        InstructionList il = new InstructionList();
        final MethodGenerator constructor =
            new MethodGenerator(ACC_PUBLIC,
                                com.sun.org.apache.bcel.internal.generic.Type.VOID,
                                argTypes, argNames, "<init>",
                                className, il, cpg);

        il.append(ALOAD_0);
        il.append(ALOAD_1);
        il.append(ALOAD_2);
        il.append(new ALOAD(3));
        il.append(new ALOAD(4));
        il.append(new ALOAD(5));
  il.append(new ALOAD(6));
  il.append(new ALOAD(7));
        il.append(new INVOKESPECIAL(cpg.addMethodref(NODE_SORT_FACTORY,
            "<init>",
            "(" + DOM_INTF_SIG
                + STRING_SIG
                + TRANSLET_INTF_SIG
                + "[" + STRING_SIG
    + "[" + STRING_SIG
    + "[" + STRING_SIG
                + "[" + STRING_SIG + ")V")));
        il.append(RETURN);

        il = new InstructionList();
        final MethodGenerator makeNodeSortRecord =
            new MethodGenerator(ACC_PUBLIC,
                Util.getJCRefType(NODE_SORT_RECORD_SIG),
                new com.sun.org.apache.bcel.internal.generic.Type[] {
                    com.sun.org.apache.bcel.internal.generic.Type.INT,
                    com.sun.org.apache.bcel.internal.generic.Type.INT },
                new String[] { "node", "last" }, "makeNodeSortRecord",
                className, il, cpg);

        il.append(ALOAD_0);
        il.append(ILOAD_1);
        il.append(ILOAD_2);
        il.append(new INVOKESPECIAL(cpg.addMethodref(NODE_SORT_FACTORY,
            "makeNodeSortRecord", "(II)" + NODE_SORT_RECORD_SIG)));
        il.append(DUP);
        il.append(new CHECKCAST(cpg.addClass(sortRecordClass)));

        final int ndups = dups.size();
        for (int i = 0; i < ndups; i++) {
            final VariableRefBase varRef = (VariableRefBase) dups.get(i);
            final VariableBase var = varRef.getVariable();
            final Type varType = var.getType();

            il.append(DUP);

            il.append(ALOAD_0);
            il.append(new GETFIELD(
                cpg.addFieldref(className,
                    var.getEscapedName(), varType.toSignature())));

            il.append(new PUTFIELD(
                cpg.addFieldref(sortRecordClass,
                    var.getEscapedName(), varType.toSignature())));
        }
        il.append(POP);
        il.append(ARETURN);

        constructor.setMaxLocals();
        constructor.setMaxStack();
        sortRecordFactory.addMethod(constructor);
        makeNodeSortRecord.setMaxLocals();
        makeNodeSortRecord.setMaxStack();
        sortRecordFactory.addMethod(makeNodeSortRecord);
        xsltc.dumpClass(sortRecordFactory.getJavaClass());

        return className;
    }


    private static String compileSortRecord(Vector<Sort> sortObjects,
                                            ClassGenerator classGen,
                                            MethodGenerator methodGen) {
        final XSLTC  xsltc = sortObjects.firstElement().getXSLTC();
        final String className = xsltc.getHelperClassName();

        final NodeSortRecordGenerator sortRecord =
            new NodeSortRecordGenerator(className,
                                        NODE_SORT_RECORD,
                                        "sort$0.java",
                                        ACC_PUBLIC | ACC_SUPER | ACC_FINAL,
                                        new String[] {},
                                        classGen.getStylesheet());

        final ConstantPoolGen cpg = sortRecord.getConstantPool();

        final int nsorts = sortObjects.size();
        final ArrayList<VariableRefBase> dups = new ArrayList<>();

        for (int j = 0; j < nsorts; j++) {
            final Sort sort = sortObjects.get(j);

            sort.setInnerClassName(className);

            final int length = (sort._closureVars == null) ? 0 :
                sort._closureVars.size();
            for (int i = 0; i < length; i++) {
                final VariableRefBase varRef = (VariableRefBase) sort._closureVars.get(i);

                if (dups.contains(varRef)) continue;

                final VariableBase var = varRef.getVariable();
                sortRecord.addField(new Field(ACC_PUBLIC,
                                    cpg.addUtf8(var.getEscapedName()),
                                    cpg.addUtf8(var.getType().toSignature()),
                                    null, cpg.getConstantPool()));
                dups.add(varRef);
            }
        }

        MethodGenerator init = compileInit(sortRecord, cpg, className);
        MethodGenerator extract = compileExtract(sortObjects, sortRecord,
                                        cpg, className);
        sortRecord.addMethod(init);
        sortRecord.addMethod(extract);

        xsltc.dumpClass(sortRecord.getJavaClass());
        return className;
    }


    private static MethodGenerator compileInit(NodeSortRecordGenerator sortRecord,
                                           ConstantPoolGen cpg,
                                           String className)
    {
        final InstructionList il = new InstructionList();
        final MethodGenerator init =
            new MethodGenerator(ACC_PUBLIC,
                                com.sun.org.apache.bcel.internal.generic.Type.VOID,
                                null, null, "<init>", className,
                                il, cpg);

        il.append(ALOAD_0);
        il.append(new INVOKESPECIAL(cpg.addMethodref(NODE_SORT_RECORD,
                                                     "<init>", "()V")));



        il.append(RETURN);

        return init;
    }



    private static MethodGenerator compileExtract(Vector<Sort> sortObjects,
                                         NodeSortRecordGenerator sortRecord,
                                         ConstantPoolGen cpg,
                                         String className) {
        final InstructionList il = new InstructionList();

        final CompareGenerator extractMethod =
            new CompareGenerator(ACC_PUBLIC | ACC_FINAL,
                                 com.sun.org.apache.bcel.internal.generic.Type.STRING,
                                 new com.sun.org.apache.bcel.internal.generic.Type[] {
                                     Util.getJCRefType(DOM_INTF_SIG),
                                     com.sun.org.apache.bcel.internal.generic.Type.INT,
                                     com.sun.org.apache.bcel.internal.generic.Type.INT,
                                     Util.getJCRefType(TRANSLET_SIG),
                                     com.sun.org.apache.bcel.internal.generic.Type.INT
                                 },
                                 new String[] { "dom",
                                                "current",
                                                "level",
                                                "translet",
                                                "last"
                                 },
                                 "extractValueFromDOM", className, il, cpg);

        final int levels = sortObjects.size();
        final int match[] = new int[levels];
        final InstructionHandle target[] = new InstructionHandle[levels];
        InstructionHandle tblswitch = null;

        if (levels > 1) {
            il.append(new ILOAD(extractMethod.getLocalIndex("level")));
            tblswitch = il.append(new NOP());
        }

        for (int level = 0; level < levels; level++) {
            match[level] = level;
            final Sort sort = sortObjects.elementAt(level);
            target[level] = il.append(NOP);
            sort.translateSelect(sortRecord, extractMethod);
            il.append(ARETURN);
        }

        if (levels > 1) {
            InstructionHandle defaultTarget =
                il.append(new PUSH(cpg, EMPTYSTRING));
            il.insert(tblswitch,new TABLESWITCH(match, target, defaultTarget));
            il.append(ARETURN);
        }

        return extractMethod;
    }
}
