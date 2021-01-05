



package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.InvokeInstruction;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.IntType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MultiHashtable;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ObjectType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;
import jdk.xml.internal.JdkXmlFeatures;


class FunctionCall extends Expression {

    private QName  _fname;
    private final Vector _arguments;
    private final static Vector EMPTY_ARG_LIST = new Vector(0);

    protected final static String EXT_XSLTC =
        TRANSLET_URI;

    protected final static String JAVA_EXT_XSLTC =
        EXT_XSLTC + "/java";

    protected final static String EXT_XALAN =
        "http:protected final static String JAVA_EXT_XALAN =
        "http:protected final static String JAVA_EXT_XALAN_OLD =
        "http:protected final static String EXSLT_COMMON =
        "http:protected final static String EXSLT_MATH =
        "http:protected final static String EXSLT_SETS =
        "http:protected final static String EXSLT_DATETIME =
        "http:protected final static String EXSLT_STRINGS =
        "http:protected final static String XALAN_CLASSPACKAGE_NAMESPACE =
        "xalan:protected final static int NAMESPACE_FORMAT_JAVA = 0;
    protected final static int NAMESPACE_FORMAT_CLASS = 1;
    protected final static int NAMESPACE_FORMAT_PACKAGE = 2;
    protected final static int NAMESPACE_FORMAT_CLASS_OR_PACKAGE = 3;

    private int _namespace_format = NAMESPACE_FORMAT_JAVA;


    Expression _thisArgument = null;

    private String      _className;
    private Class       _clazz;
    private Method      _chosenMethod;
    private Constructor _chosenConstructor;
    private MethodType  _chosenMethodType;

    private boolean    unresolvedExternal;

    private boolean     _isExtConstructor = false;

    private boolean       _isStatic = false;

    private static final MultiHashtable<Type, JavaType> _internal2Java = new MultiHashtable<>();

    private static final Map<Class<?>, Type> JAVA2INTERNAL;

    private static final Map<String, String> EXTENSIONNAMESPACE;

    private static final Map<String, String> EXTENSIONFUNCTION;

    static class JavaType {
        public Class<?>  type;
        public int distance;

        public JavaType(Class type, int distance){
            this.type = type;
            this.distance = distance;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.type);
        }

        @Override
        public boolean equals(Object query) {
            if (query == null) {
                return false;
            }
            if (query.getClass().isAssignableFrom(JavaType.class)) {
                return ((JavaType)query).type.equals(type);
            } else {
                return query.equals(type);
            }
        }
    }


    static {
        final Class<?> nodeClass, nodeListClass;
        try {
            nodeClass     = Class.forName("org.w3c.dom.Node");
            nodeListClass = Class.forName("org.w3c.dom.NodeList");
        }
        catch (ClassNotFoundException e) {
            ErrorMsg err = new ErrorMsg(ErrorMsg.CLASS_NOT_FOUND_ERR,"org.w3c.dom.Node or NodeList");
            throw new ExceptionInInitializerError(err.toString());
        }

        _internal2Java.put(Type.Boolean, new JavaType(Boolean.TYPE, 0));
        _internal2Java.put(Type.Boolean, new JavaType(Boolean.class, 1));
        _internal2Java.put(Type.Boolean, new JavaType(Object.class, 2));

        _internal2Java.put(Type.Real, new JavaType(Double.TYPE, 0));
        _internal2Java.put(Type.Real, new JavaType(Double.class, 1));
        _internal2Java.put(Type.Real, new JavaType(Float.TYPE, 2));
        _internal2Java.put(Type.Real, new JavaType(Long.TYPE, 3));
        _internal2Java.put(Type.Real, new JavaType(Integer.TYPE, 4));
        _internal2Java.put(Type.Real, new JavaType(Short.TYPE, 5));
        _internal2Java.put(Type.Real, new JavaType(Byte.TYPE, 6));
        _internal2Java.put(Type.Real, new JavaType(Character.TYPE, 7));
        _internal2Java.put(Type.Real, new JavaType(Object.class, 8));

        _internal2Java.put(Type.Int, new JavaType(Double.TYPE, 0));
        _internal2Java.put(Type.Int, new JavaType(Double.class, 1));
        _internal2Java.put(Type.Int, new JavaType(Float.TYPE, 2));
        _internal2Java.put(Type.Int, new JavaType(Long.TYPE, 3));
        _internal2Java.put(Type.Int, new JavaType(Integer.TYPE, 4));
        _internal2Java.put(Type.Int, new JavaType(Short.TYPE, 5));
        _internal2Java.put(Type.Int, new JavaType(Byte.TYPE, 6));
        _internal2Java.put(Type.Int, new JavaType(Character.TYPE, 7));
        _internal2Java.put(Type.Int, new JavaType(Object.class, 8));

        _internal2Java.put(Type.String, new JavaType(String.class, 0));
        _internal2Java.put(Type.String, new JavaType(Object.class, 1));

        _internal2Java.put(Type.NodeSet, new JavaType(nodeListClass, 0));
        _internal2Java.put(Type.NodeSet, new JavaType(nodeClass, 1));
        _internal2Java.put(Type.NodeSet, new JavaType(Object.class, 2));
        _internal2Java.put(Type.NodeSet, new JavaType(String.class, 3));

        _internal2Java.put(Type.Node, new JavaType(nodeListClass, 0));
        _internal2Java.put(Type.Node, new JavaType(nodeClass, 1));
        _internal2Java.put(Type.Node, new JavaType(Object.class, 2));
        _internal2Java.put(Type.Node, new JavaType(String.class, 3));

        _internal2Java.put(Type.ResultTree, new JavaType(nodeListClass, 0));
        _internal2Java.put(Type.ResultTree, new JavaType(nodeClass, 1));
        _internal2Java.put(Type.ResultTree, new JavaType(Object.class, 2));
        _internal2Java.put(Type.ResultTree, new JavaType(String.class, 3));

        _internal2Java.put(Type.Reference, new JavaType(Object.class, 0));

        _internal2Java.makeUnmodifiable();

        Map<Class<?>, Type> java2Internal = new HashMap<>();
        Map<String, String> extensionNamespaceTable = new HashMap<>();
        Map<String, String> extensionFunctionTable = new HashMap<>();

        java2Internal.put(Boolean.TYPE, Type.Boolean);
        java2Internal.put(Void.TYPE, Type.Void);
        java2Internal.put(Character.TYPE, Type.Real);
        java2Internal.put(Byte.TYPE, Type.Real);
        java2Internal.put(Short.TYPE, Type.Real);
        java2Internal.put(Integer.TYPE, Type.Real);
        java2Internal.put(Long.TYPE, Type.Real);
        java2Internal.put(Float.TYPE, Type.Real);
        java2Internal.put(Double.TYPE, Type.Real);

        java2Internal.put(String.class, Type.String);

        java2Internal.put(Object.class, Type.Reference);

        java2Internal.put(nodeListClass, Type.NodeSet);
        java2Internal.put(nodeClass, Type.NodeSet);

        extensionNamespaceTable.put(EXT_XALAN, "com.sun.org.apache.xalan.internal.lib.Extensions");
        extensionNamespaceTable.put(EXSLT_COMMON, "com.sun.org.apache.xalan.internal.lib.ExsltCommon");
        extensionNamespaceTable.put(EXSLT_MATH, "com.sun.org.apache.xalan.internal.lib.ExsltMath");
        extensionNamespaceTable.put(EXSLT_SETS, "com.sun.org.apache.xalan.internal.lib.ExsltSets");
        extensionNamespaceTable.put(EXSLT_DATETIME, "com.sun.org.apache.xalan.internal.lib.ExsltDatetime");
        extensionNamespaceTable.put(EXSLT_STRINGS, "com.sun.org.apache.xalan.internal.lib.ExsltStrings");

        extensionFunctionTable.put(EXSLT_COMMON + ":nodeSet", "nodeset");
        extensionFunctionTable.put(EXSLT_COMMON + ":objectType", "objectType");
        extensionFunctionTable.put(EXT_XALAN + ":nodeset", "nodeset");

        JAVA2INTERNAL = Collections.unmodifiableMap(java2Internal);
        EXTENSIONNAMESPACE = Collections.unmodifiableMap(extensionNamespaceTable);
        EXTENSIONFUNCTION = Collections.unmodifiableMap(extensionFunctionTable);

    }

    public FunctionCall(QName fname, Vector arguments) {
        _fname = fname;
        _arguments = arguments;
        _type = null;
    }

    public FunctionCall(QName fname) {
        this(fname, EMPTY_ARG_LIST);
    }

    public String getName() {
        return(_fname.toString());
    }

    @Override
    public void setParser(Parser parser) {
        super.setParser(parser);
        if (_arguments != null) {
            final int n = _arguments.size();
            for (int i = 0; i < n; i++) {
                final Expression exp = (Expression)_arguments.elementAt(i);
                exp.setParser(parser);
                exp.setParent(this);
            }
        }
    }

    public String getClassNameFromUri(String uri)
    {
        String className = EXTENSIONNAMESPACE.get(uri);

        if (className != null)
            return className;
        else {
            if (uri.startsWith(JAVA_EXT_XSLTC)) {
                int length = JAVA_EXT_XSLTC.length() + 1;
                return (uri.length() > length) ? uri.substring(length) : EMPTYSTRING;
            }
            else if (uri.startsWith(JAVA_EXT_XALAN)) {
                int length = JAVA_EXT_XALAN.length() + 1;
                return (uri.length() > length) ? uri.substring(length) : EMPTYSTRING;
            }
            else if (uri.startsWith(JAVA_EXT_XALAN_OLD)) {
                int length = JAVA_EXT_XALAN_OLD.length() + 1;
                return (uri.length() > length) ? uri.substring(length) : EMPTYSTRING;
            }
            else {
                int index = uri.lastIndexOf('/');
                return (index > 0) ? uri.substring(index+1) : uri;
            }
        }
    }


    @Override
    public Type typeCheck(SymbolTable stable)
        throws TypeCheckError
    {
        if (_type != null) return _type;

        final String namespace = _fname.getNamespace();
        String local = _fname.getLocalPart();

        if (isExtension()) {
            _fname = new QName(null, null, local);
            return typeCheckStandard(stable);
        }
        else if (isStandard()) {
            return typeCheckStandard(stable);
        }
        else {
            try {
                _className = getClassNameFromUri(namespace);

                final int pos = local.lastIndexOf('.');
                if (pos > 0) {
                    _isStatic = true;
                    if (_className != null && _className.length() > 0) {
                        _namespace_format = NAMESPACE_FORMAT_PACKAGE;
                        _className = _className + "." + local.substring(0, pos);
                    }
                    else {
                        _namespace_format = NAMESPACE_FORMAT_JAVA;
                        _className = local.substring(0, pos);
                    }

                    _fname = new QName(namespace, null, local.substring(pos + 1));
                }
                else {
                    if (_className != null && _className.length() > 0) {
                        try {
                            _clazz = ObjectFactory.findProviderClass(_className, true);
                            _namespace_format = NAMESPACE_FORMAT_CLASS;
                        }
                        catch (ClassNotFoundException e) {
                            _namespace_format = NAMESPACE_FORMAT_PACKAGE;
                        }
                    }
                    else
                        _namespace_format = NAMESPACE_FORMAT_JAVA;

                    if (local.indexOf('-') > 0) {
                        local = replaceDash(local);
                    }

                    String extFunction = EXTENSIONFUNCTION.get(namespace + ":" + local);
                    if (extFunction != null) {
                        _fname = new QName(null, null, extFunction);
                        return typeCheckStandard(stable);
                    }
                    else
                        _fname = new QName(namespace, null, local);
                }

                return typeCheckExternal(stable);
            }
            catch (TypeCheckError e) {
                ErrorMsg errorMsg = e.getErrorMsg();
                if (errorMsg == null) {
                    final String name = _fname.getLocalPart();
                    errorMsg = new ErrorMsg(ErrorMsg.METHOD_NOT_FOUND_ERR, name);
                }
                getParser().reportError(ERROR, errorMsg);
                return _type = Type.Void;
            }
          }
    }


    public Type typeCheckStandard(SymbolTable stable) throws TypeCheckError {
        _fname.clearNamespace();        final int n = _arguments.size();
        final Vector argsType = typeCheckArgs(stable);
        final MethodType args = new MethodType(Type.Void, argsType);
        final MethodType ptype =
            lookupPrimop(stable, _fname.getLocalPart(), args);

        if (ptype != null) {
            for (int i = 0; i < n; i++) {
                final Type argType = (Type) ptype.argsType().elementAt(i);
                final Expression exp = (Expression)_arguments.elementAt(i);
                if (!argType.identicalTo(exp.getType())) {
                    try {
                        _arguments.setElementAt(new CastExpr(exp, argType), i);
                    }
                    catch (TypeCheckError e) {
                        throw new TypeCheckError(this); }
                }
            }
            _chosenMethodType = ptype;
            return _type = ptype.resultType();
        }
        throw new TypeCheckError(this);
    }



    public Type typeCheckConstructor(SymbolTable stable) throws TypeCheckError{
        final Vector constructors = findConstructors();
        if (constructors == null) {
            throw new TypeCheckError(ErrorMsg.CONSTRUCTOR_NOT_FOUND,
                _className);

        }

        final int nConstructors = constructors.size();
        final int nArgs = _arguments.size();
        final Vector argsType = typeCheckArgs(stable);

        int bestConstrDistance = Integer.MAX_VALUE;
        _type = null;                   for (int j, i = 0; i < nConstructors; i++) {
            final Constructor constructor =
                (Constructor)constructors.elementAt(i);
            final Class[] paramTypes = constructor.getParameterTypes();

            Class<?> extType;
            int currConstrDistance = 0;
            for (j = 0; j < nArgs; j++) {
                extType = paramTypes[j];
                final Type intType = (Type)argsType.elementAt(j);
                JavaType match = _internal2Java.maps(intType, new JavaType(extType, 0));
                if (match != null) {
                    currConstrDistance += match.distance;
                }
                else if (intType instanceof ObjectType) {
                    ObjectType objectType = (ObjectType)intType;
                    if (objectType.getJavaClass() == extType)
                        continue;
                    else if (extType.isAssignableFrom(objectType.getJavaClass()))
                        currConstrDistance += 1;
                    else {
                        currConstrDistance = Integer.MAX_VALUE;
                        break;
                    }
                }
                else {
                    currConstrDistance = Integer.MAX_VALUE;
                    break;
                }
            }

            if (j == nArgs && currConstrDistance < bestConstrDistance ) {
                _chosenConstructor = constructor;
                _isExtConstructor = true;
                bestConstrDistance = currConstrDistance;

                _type = (_clazz != null) ? Type.newObjectType(_clazz)
                    : Type.newObjectType(_className);
            }
        }

        if (_type != null) {
            return _type;
        }

        throw new TypeCheckError(ErrorMsg.ARGUMENT_CONVERSION_ERR, getMethodSignature(argsType));
    }



    public Type typeCheckExternal(SymbolTable stable) throws TypeCheckError {
        int nArgs = _arguments.size();
        final String name = _fname.getLocalPart();

        if (_fname.getLocalPart().equals("new")) {
            return typeCheckConstructor(stable);
        }
        else {
            boolean hasThisArgument = false;

            if (nArgs == 0)
                _isStatic = true;

            if (!_isStatic) {
                if (_namespace_format == NAMESPACE_FORMAT_JAVA
                    || _namespace_format == NAMESPACE_FORMAT_PACKAGE)
                    hasThisArgument = true;

                Expression firstArg = (Expression)_arguments.elementAt(0);
                Type firstArgType = (Type)firstArg.typeCheck(stable);

                if (_namespace_format == NAMESPACE_FORMAT_CLASS
                    && firstArgType instanceof ObjectType
                    && _clazz != null
                    && _clazz.isAssignableFrom(((ObjectType)firstArgType).getJavaClass()))
                    hasThisArgument = true;

                if (hasThisArgument) {
                    _thisArgument = (Expression) _arguments.elementAt(0);
                    _arguments.remove(0); nArgs--;
                    if (firstArgType instanceof ObjectType) {
                        _className = ((ObjectType) firstArgType).getJavaClassName();
                    }
                    else
                        throw new TypeCheckError(ErrorMsg.NO_JAVA_FUNCT_THIS_REF, name);
                }
            }
            else if (_className.length() == 0) {

                final Parser parser = getParser();
                if (parser != null) {
                    reportWarning(this, parser, ErrorMsg.FUNCTION_RESOLVE_ERR,
                                  _fname.toString());
                }
                unresolvedExternal = true;
                return _type = Type.Int;        }
        }

        final Vector methods = findMethods();

        if (methods == null) {
            throw new TypeCheckError(ErrorMsg.METHOD_NOT_FOUND_ERR, _className + "." + name);
        }

        Class extType = null;
        final int nMethods = methods.size();
        final Vector argsType = typeCheckArgs(stable);

        int bestMethodDistance  = Integer.MAX_VALUE;
        _type = null;                       for (int j, i = 0; i < nMethods; i++) {
            final Method method = (Method)methods.elementAt(i);
            final Class[] paramTypes = method.getParameterTypes();

            int currMethodDistance = 0;
            for (j = 0; j < nArgs; j++) {
                extType = paramTypes[j];
                final Type intType = (Type)argsType.elementAt(j);
                JavaType match = _internal2Java.maps(intType, new JavaType(extType, 0));
                if (match != null) {
                    currMethodDistance += match.distance;
                }
                else {
                    if (intType instanceof ReferenceType) {
                       currMethodDistance += 1;
                    }
                    else if (intType instanceof ObjectType) {
                        ObjectType object = (ObjectType)intType;
                        if (extType.getName().equals(object.getJavaClassName()))
                            currMethodDistance += 0;
                        else if (extType.isAssignableFrom(object.getJavaClass()))
                            currMethodDistance += 1;
                        else {
                            currMethodDistance = Integer.MAX_VALUE;
                            break;
                        }
                    }
                    else {
                        currMethodDistance = Integer.MAX_VALUE;
                        break;
                    }
                }
            }

            if (j == nArgs) {
                  extType = method.getReturnType();

                  _type = JAVA2INTERNAL.get(extType);
                  if (_type == null) {
                      _type = Type.newObjectType(extType);
                  }

                  if (_type != null && currMethodDistance < bestMethodDistance) {
                      _chosenMethod = method;
                      bestMethodDistance = currMethodDistance;
                  }
            }
        }

        if (_chosenMethod != null && _thisArgument == null &&
            !Modifier.isStatic(_chosenMethod.getModifiers())) {
            throw new TypeCheckError(ErrorMsg.NO_JAVA_FUNCT_THIS_REF, getMethodSignature(argsType));
        }

        if (_type != null) {
            if (_type == Type.NodeSet) {
                getXSLTC().setMultiDocument(true);
            }
            return _type;
        }

        throw new TypeCheckError(ErrorMsg.ARGUMENT_CONVERSION_ERR, getMethodSignature(argsType));
    }


    public Vector typeCheckArgs(SymbolTable stable) throws TypeCheckError {
        final Vector result = new Vector();
        final Enumeration e = _arguments.elements();
        while (e.hasMoreElements()) {
            final Expression exp = (Expression)e.nextElement();
            result.addElement(exp.typeCheck(stable));
        }
        return result;
    }

    protected final Expression argument(int i) {
        return (Expression)_arguments.elementAt(i);
    }

    protected final Expression argument() {
        return argument(0);
    }

    protected final int argumentCount() {
        return _arguments.size();
    }

    protected final void setArgument(int i, Expression exp) {
        _arguments.setElementAt(exp, i);
    }


    @Override
    public void translateDesynthesized(ClassGenerator classGen,
                                       MethodGenerator methodGen)
    {
        Type type = Type.Boolean;
        if (_chosenMethodType != null)
            type = _chosenMethodType.resultType();

        final InstructionList il = methodGen.getInstructionList();
        translate(classGen, methodGen);

        if ((type instanceof BooleanType) || (type instanceof IntType)) {
            _falseList.add(il.append(new IFEQ(null)));
        }
    }



    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        final int n = argumentCount();
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final boolean isSecureProcessing = classGen.getParser().getXSLTC().isSecureProcessing();
        final boolean isExtensionFunctionEnabled = classGen.getParser().getXSLTC()
                .getFeature(JdkXmlFeatures.XmlFeature.ENABLE_EXTENSION_FUNCTION);
        int index;

        if (isStandard() || isExtension()) {
            for (int i = 0; i < n; i++) {
                final Expression exp = argument(i);
                exp.translate(classGen, methodGen);
                exp.startIterator(classGen, methodGen);
            }

            final String name = _fname.toString().replace('-', '_') + "F";
            String args = Constants.EMPTYSTRING;

            if (name.equals("sumF")) {
                args = DOM_INTF_SIG;
                il.append(methodGen.loadDOM());
            }
            else if (name.equals("normalize_spaceF")) {
                if (_chosenMethodType.toSignature(args).
                    equals("()Ljava/lang/String;")) {
                    args = "I"+DOM_INTF_SIG;
                    il.append(methodGen.loadContextNode());
                    il.append(methodGen.loadDOM());
                }
            }

            index = cpg.addMethodref(BASIS_LIBRARY_CLASS, name,
                                     _chosenMethodType.toSignature(args));
            il.append(new INVOKESTATIC(index));
        }
        else if (unresolvedExternal) {
            index = cpg.addMethodref(BASIS_LIBRARY_CLASS,
                                     "unresolved_externalF",
                                     "(Ljava/lang/String;)V");
            il.append(new PUSH(cpg, _fname.toString()));
            il.append(new INVOKESTATIC(index));
        }
        else if (_isExtConstructor) {
            if (isSecureProcessing && !isExtensionFunctionEnabled)
                translateUnallowedExtension(cpg, il);

            final String clazz =
                _chosenConstructor.getDeclaringClass().getName();
            Class[] paramTypes = _chosenConstructor.getParameterTypes();
            LocalVariableGen[] paramTemp = new LocalVariableGen[n];

            for (int i = 0; i < n; i++) {
                final Expression exp = argument(i);
                Type expType = exp.getType();
                exp.translate(classGen, methodGen);
                exp.startIterator(classGen, methodGen);
                expType.translateTo(classGen, methodGen, paramTypes[i]);
                paramTemp[i] =
                    methodGen.addLocalVariable("function_call_tmp"+i,
                                               expType.toJCType(),
                                               null, null);
                paramTemp[i].setStart(
                        il.append(expType.STORE(paramTemp[i].getIndex())));

            }

            il.append(new NEW(cpg.addClass(_className)));
            il.append(InstructionConstants.DUP);

            for (int i = 0; i < n; i++) {
                final Expression arg = argument(i);
                paramTemp[i].setEnd(
                        il.append(arg.getType().LOAD(paramTemp[i].getIndex())));
            }

            final StringBuffer buffer = new StringBuffer();
            buffer.append('(');
            for (int i = 0; i < paramTypes.length; i++) {
                buffer.append(getSignature(paramTypes[i]));
            }
            buffer.append(')');
            buffer.append("V");

            index = cpg.addMethodref(clazz,
                                     "<init>",
                                     buffer.toString());
            il.append(new INVOKESPECIAL(index));

            (Type.Object).translateFrom(classGen, methodGen,
                                _chosenConstructor.getDeclaringClass());

        }
        else {
            if (isSecureProcessing && !isExtensionFunctionEnabled)
                translateUnallowedExtension(cpg, il);

            final String clazz = _chosenMethod.getDeclaringClass().getName();
            Class[] paramTypes = _chosenMethod.getParameterTypes();

            if (_thisArgument != null) {
                _thisArgument.translate(classGen, methodGen);
            }

            for (int i = 0; i < n; i++) {
                final Expression exp = argument(i);
                exp.translate(classGen, methodGen);
                exp.startIterator(classGen, methodGen);
                exp.getType().translateTo(classGen, methodGen, paramTypes[i]);
            }

            final StringBuffer buffer = new StringBuffer();
            buffer.append('(');
            for (int i = 0; i < paramTypes.length; i++) {
                buffer.append(getSignature(paramTypes[i]));
            }
            buffer.append(')');
            buffer.append(getSignature(_chosenMethod.getReturnType()));

            if (_thisArgument != null && _clazz.isInterface()) {
                index = cpg.addInterfaceMethodref(clazz,
                                     _fname.getLocalPart(),
                                     buffer.toString());
                il.append(new INVOKEINTERFACE(index, n+1));
            }
            else {
                index = cpg.addMethodref(clazz,
                                     _fname.getLocalPart(),
                                     buffer.toString());
                il.append(_thisArgument != null ? (InvokeInstruction) new INVOKEVIRTUAL(index) :
                          (InvokeInstruction) new INVOKESTATIC(index));
            }

            _type.translateFrom(classGen, methodGen,
                                _chosenMethod.getReturnType());
        }
    }

    @Override
    public String toString() {
        return "funcall(" + _fname + ", " + _arguments + ')';
    }

    public boolean isStandard() {
        final String namespace = _fname.getNamespace();
        return (namespace == null) || (namespace.equals(Constants.EMPTYSTRING));
    }

    public boolean isExtension() {
        final String namespace = _fname.getNamespace();
        return (namespace != null) && (namespace.equals(EXT_XSLTC));
    }


    private Vector findMethods() {

          Vector result = null;
          final String namespace = _fname.getNamespace();

          if (_className != null && _className.length() > 0) {
            final int nArgs = _arguments.size();
            try {
                if (_clazz == null) {
                    final boolean isSecureProcessing = getXSLTC().isSecureProcessing();
                    final boolean isExtensionFunctionEnabled = getXSLTC()
                            .getFeature(JdkXmlFeatures.XmlFeature.ENABLE_EXTENSION_FUNCTION);

                    if (namespace != null && isSecureProcessing
                            && isExtensionFunctionEnabled
                            && (namespace.startsWith(JAVA_EXT_XALAN)
                            || namespace.startsWith(JAVA_EXT_XSLTC)
                            || namespace.startsWith(JAVA_EXT_XALAN_OLD)
                            || namespace.startsWith(XALAN_CLASSPACKAGE_NAMESPACE))) {
                        _clazz = getXSLTC().loadExternalFunction(_className);
                    } else {
                        _clazz = ObjectFactory.findProviderClass(_className, true);
                    }

                if (_clazz == null) {
                  final ErrorMsg msg =
                        new ErrorMsg(ErrorMsg.CLASS_NOT_FOUND_ERR, _className);
                  getParser().reportError(Constants.ERROR, msg);
                }
              }

              final String methodName = _fname.getLocalPart();
              final Method[] methods = _clazz.getMethods();

              for (int i = 0; i < methods.length; i++) {
                final int mods = methods[i].getModifiers();
                if (Modifier.isPublic(mods)
                    && methods[i].getName().equals(methodName)
                    && methods[i].getParameterTypes().length == nArgs)
                {
                  if (result == null) {
                    result = new Vector();
                  }
                  result.addElement(methods[i]);
                }
              }
            }
            catch (ClassNotFoundException e) {
                  final ErrorMsg msg = new ErrorMsg(ErrorMsg.CLASS_NOT_FOUND_ERR, _className);
                  getParser().reportError(Constants.ERROR, msg);
            }
          }
          return result;
    }


    private Vector findConstructors() {
        Vector result = null;
        final String namespace = _fname.getNamespace();

        final int nArgs = _arguments.size();
        try {
          if (_clazz == null) {
            _clazz = ObjectFactory.findProviderClass(_className, true);

            if (_clazz == null) {
              final ErrorMsg msg = new ErrorMsg(ErrorMsg.CLASS_NOT_FOUND_ERR, _className);
              getParser().reportError(Constants.ERROR, msg);
            }
          }

          final Constructor[] constructors = _clazz.getConstructors();

          for (int i = 0; i < constructors.length; i++) {
              final int mods = constructors[i].getModifiers();
              if (Modifier.isPublic(mods) &&
                  constructors[i].getParameterTypes().length == nArgs)
              {
                if (result == null) {
                  result = new Vector();
                }
                result.addElement(constructors[i]);
              }
          }
        }
        catch (ClassNotFoundException e) {
          final ErrorMsg msg = new ErrorMsg(ErrorMsg.CLASS_NOT_FOUND_ERR, _className);
          getParser().reportError(Constants.ERROR, msg);
        }

        return result;
    }



    static final String getSignature(Class clazz) {
        if (clazz.isArray()) {
            final StringBuffer sb = new StringBuffer();
            Class cl = clazz;
            while (cl.isArray()) {
                sb.append("[");
                cl = cl.getComponentType();
            }
            sb.append(getSignature(cl));
            return sb.toString();
        }
        else if (clazz.isPrimitive()) {
            if (clazz == Integer.TYPE) {
                return "I";
            }
            else if (clazz == Byte.TYPE) {
                return "B";
            }
            else if (clazz == Long.TYPE) {
                return "J";
            }
            else if (clazz == Float.TYPE) {
                return "F";
            }
            else if (clazz == Double.TYPE) {
                return "D";
            }
            else if (clazz == Short.TYPE) {
                return "S";
            }
            else if (clazz == Character.TYPE) {
                return "C";
            }
            else if (clazz == Boolean.TYPE) {
                return "Z";
            }
            else if (clazz == Void.TYPE) {
                return "V";
            }
            else {
                final String name = clazz.toString();
                ErrorMsg err = new ErrorMsg(ErrorMsg.UNKNOWN_SIG_TYPE_ERR,name);
                throw new Error(err.toString());
            }
        }
        else {
            return "L" + clazz.getName().replace('.', '/') + ';';
        }
    }


    static final String getSignature(Method meth) {
        final StringBuffer sb = new StringBuffer();
        sb.append('(');
        final Class[] params = meth.getParameterTypes(); for (int j = 0; j < params.length; j++) {
            sb.append(getSignature(params[j]));
        }
        return sb.append(')').append(getSignature(meth.getReturnType()))
            .toString();
    }


    static final String getSignature(Constructor cons) {
        final StringBuffer sb = new StringBuffer();
        sb.append('(');
        final Class[] params = cons.getParameterTypes(); for (int j = 0; j < params.length; j++) {
            sb.append(getSignature(params[j]));
        }
        return sb.append(")V").toString();
    }


    private String getMethodSignature(Vector argsType) {
        final StringBuffer buf = new StringBuffer(_className);
        buf.append('.').append(_fname.getLocalPart()).append('(');

        int nArgs = argsType.size();
        for (int i = 0; i < nArgs; i++) {
            final Type intType = (Type)argsType.elementAt(i);
            buf.append(intType.toString());
            if (i < nArgs - 1) buf.append(", ");
        }

        buf.append(')');
        return buf.toString();
    }


    protected static String replaceDash(String name)
    {
        char dash = '-';
        final StringBuilder buff = new StringBuilder("");
        for (int i = 0; i < name.length(); i++) {
        if (i > 0 && name.charAt(i-1) == dash)
            buff.append(Character.toUpperCase(name.charAt(i)));
        else if (name.charAt(i) != dash)
            buff.append(name.charAt(i));
        }
        return buff.toString();
    }


    private void translateUnallowedExtension(ConstantPoolGen cpg,
                                             InstructionList il) {
        int index = cpg.addMethodref(BASIS_LIBRARY_CLASS,
                                     "unallowed_extension_functionF",
                                     "(Ljava/lang/String;)V");
        il.append(new PUSH(cpg, _fname.toString()));
        il.append(new INVOKESTATIC(index));
    }
}
