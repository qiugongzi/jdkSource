
package com.sun.org.apache.bcel.internal.generic;


import com.sun.org.apache.bcel.internal.Constants;


public class InstructionFactory
  implements InstructionConstants, java.io.Serializable
{
  protected ClassGen        cg;
  protected ConstantPoolGen cp;

  public InstructionFactory(ClassGen cg, ConstantPoolGen cp) {
    this.cg = cg;
    this.cp = cp;
  }


  public InstructionFactory(ClassGen cg) {
    this(cg, cg.getConstantPool());
  }


  public InstructionFactory(ConstantPoolGen cp) {
    this(null, cp);
  }


  public InvokeInstruction createInvoke(String class_name, String name, Type ret_type,
                                        Type[] arg_types, short kind) {
    int    index;
    int    nargs      = 0;
    String signature  = Type.getMethodSignature(ret_type, arg_types);

    for(int i=0; i < arg_types.length; i++) nargs += arg_types[i].getSize();

    if(kind == Constants.INVOKEINTERFACE)
      index = cp.addInterfaceMethodref(class_name, name, signature);
    else
      index = cp.addMethodref(class_name, name, signature);

    switch(kind) {
    case Constants.INVOKESPECIAL:   return new INVOKESPECIAL(index);
    case Constants.INVOKEVIRTUAL:   return new INVOKEVIRTUAL(index);
    case Constants.INVOKESTATIC:    return new INVOKESTATIC(index);
    case Constants.INVOKEINTERFACE: return new INVOKEINTERFACE(index, nargs + 1);
    default:
      throw new RuntimeException("Oops: Unknown invoke kind:" + kind);
    }
  }


  public InstructionList createPrintln(String s) {
    InstructionList il      = new InstructionList();
    int             out     = cp.addFieldref("java.lang.System", "out",
                                             "Ljava/io/PrintStream;");
    int             println = cp.addMethodref("java.io.PrintStream", "println",
                                              "(Ljava/lang/String;)V");

    il.append(new GETSTATIC(out));
    il.append(new PUSH(cp, s));
    il.append(new INVOKEVIRTUAL(println));

    return il;
  }


  public Instruction createConstant(Object value) {
    PUSH push;

    if(value instanceof Number)
      push = new PUSH(cp, (Number)value);
    else if(value instanceof String)
      push = new PUSH(cp, (String)value);
    else if(value instanceof Boolean)
      push = new PUSH(cp, (Boolean)value);
    else if(value instanceof Character)
      push = new PUSH(cp, (Character)value);
    else
      throw new ClassGenException("Illegal type: " + value.getClass());

    return push.getInstruction();
  }

  private static class MethodObject {
    Type[]   arg_types;
    Type     result_type;
    String[] arg_names;
    String   class_name;
    String   name;
    int      access;

    MethodObject(String c, String n, Type r, Type[] a, int acc) {
      class_name  = c;
      name        = n;
      result_type = r;
      arg_types   = a;
      access      = acc;
    }
  }

  private InvokeInstruction createInvoke(MethodObject m, short kind) {
    return createInvoke(m.class_name, m.name, m.result_type, m.arg_types, kind);
  }

  private static MethodObject[] append_mos = {
    new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER,
                     new Type[] { Type.STRING }, Constants.ACC_PUBLIC),
    new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER,
                     new Type[] { Type.OBJECT }, Constants.ACC_PUBLIC),
    null, null, new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER,
                     new Type[] { Type.BOOLEAN }, Constants.ACC_PUBLIC),
    new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER,
                     new Type[] { Type.CHAR }, Constants.ACC_PUBLIC),
    new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER,
                     new Type[] { Type.FLOAT }, Constants.ACC_PUBLIC),
    new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER,
                     new Type[] { Type.DOUBLE }, Constants.ACC_PUBLIC),
    new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER,
                     new Type[] { Type.INT }, Constants.ACC_PUBLIC),
    new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[] { Type.INT }, Constants.ACC_PUBLIC),
    new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[] { Type.INT }, Constants.ACC_PUBLIC),
    new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER,
                     new Type[] { Type.LONG }, Constants.ACC_PUBLIC)
  };

  private static final boolean isString(Type type) {
    return ((type instanceof ObjectType) &&
            ((ObjectType)type).getClassName().equals("java.lang.String"));
  }

  public Instruction createAppend(Type type) {
    byte t = type.getType();

    if(isString(type))
      return createInvoke(append_mos[0], Constants.INVOKEVIRTUAL);

    switch(t) {
    case Constants.T_BOOLEAN:
    case Constants.T_CHAR:
    case Constants.T_FLOAT:
    case Constants.T_DOUBLE:
    case Constants.T_BYTE:
    case Constants.T_SHORT:
    case Constants.T_INT:
    case Constants.T_LONG
      :   return createInvoke(append_mos[t], Constants.INVOKEVIRTUAL);
    case Constants.T_ARRAY:
    case Constants.T_OBJECT:
      return createInvoke(append_mos[1], Constants.INVOKEVIRTUAL);
    default:
      throw new RuntimeException("Oops: No append for this type? " + type);
    }
  }


  public FieldInstruction createFieldAccess(String class_name, String name, Type type, short kind) {
    int    index;
    String signature  = type.getSignature();

    index = cp.addFieldref(class_name, name, signature);

    switch(kind) {
    case Constants.GETFIELD:  return new GETFIELD(index);
    case Constants.PUTFIELD:  return new PUTFIELD(index);
    case Constants.GETSTATIC: return new GETSTATIC(index);
    case Constants.PUTSTATIC: return new PUTSTATIC(index);

    default:
      throw new RuntimeException("Oops: Unknown getfield kind:" + kind);
    }
  }


  public static Instruction createThis() {
    return new ALOAD(0);
  }


  public static ReturnInstruction createReturn(Type type) {
    switch(type.getType()) {
    case Constants.T_ARRAY:
    case Constants.T_OBJECT:  return ARETURN;
    case Constants.T_INT:
    case Constants.T_SHORT:
    case Constants.T_BOOLEAN:
    case Constants.T_CHAR:
    case Constants.T_BYTE:    return IRETURN;
    case Constants.T_FLOAT:   return FRETURN;
    case Constants.T_DOUBLE:  return DRETURN;
    case Constants.T_LONG:    return LRETURN;
    case Constants.T_VOID:    return RETURN;

    default:
      throw new RuntimeException("Invalid type: " + type);
    }
  }

  private static final ArithmeticInstruction createBinaryIntOp(char first, String op) {
    switch(first) {
    case '-' : return ISUB;
    case '+' : return IADD;
    case '%' : return IREM;
    case '*' : return IMUL;
    case '/' : return IDIV;
    case '&' : return IAND;
    case '|' : return IOR;
    case '^' : return IXOR;
    case '<' : return ISHL;
    case '>' : return op.equals(">>>")? (ArithmeticInstruction)IUSHR :
      (ArithmeticInstruction)ISHR;
    default: throw new RuntimeException("Invalid operand " + op);
    }
  }

  private static final ArithmeticInstruction createBinaryLongOp(char first, String op) {
    switch(first) {
    case '-' : return LSUB;
    case '+' : return LADD;
    case '%' : return LREM;
    case '*' : return LMUL;
    case '/' : return LDIV;
    case '&' : return LAND;
    case '|' : return LOR;
    case '^' : return LXOR;
    case '<' : return LSHL;
    case '>' : return op.equals(">>>")? (ArithmeticInstruction)LUSHR :
      (ArithmeticInstruction)LSHR;
    default: throw new RuntimeException("Invalid operand " + op);
    }
  }

  private static final ArithmeticInstruction createBinaryFloatOp(char op) {
    switch(op) {
    case '-' : return FSUB;
    case '+' : return FADD;
    case '*' : return FMUL;
    case '/' : return FDIV;
    default: throw new RuntimeException("Invalid operand " + op);
    }
  }

  private static final ArithmeticInstruction createBinaryDoubleOp(char op) {
    switch(op) {
    case '-' : return DSUB;
    case '+' : return DADD;
    case '*' : return DMUL;
    case '/' : return DDIV;
    default: throw new RuntimeException("Invalid operand " + op);
    }
  }


  public static ArithmeticInstruction createBinaryOperation(String op, Type type) {
    char first = op.toCharArray()[0];

    switch(type.getType()) {
    case Constants.T_BYTE:
    case Constants.T_SHORT:
    case Constants.T_INT:
    case Constants.T_CHAR:    return createBinaryIntOp(first, op);
    case Constants.T_LONG:    return createBinaryLongOp(first, op);
    case Constants.T_FLOAT:   return createBinaryFloatOp(first);
    case Constants.T_DOUBLE:  return createBinaryDoubleOp(first);
    default:        throw new RuntimeException("Invalid type " + type);
    }
  }


  public static StackInstruction createPop(int size) {
    return (size == 2)? (StackInstruction)POP2 :
      (StackInstruction)POP;
  }


  public static StackInstruction createDup(int size) {
    return (size == 2)? (StackInstruction)DUP2 :
      (StackInstruction)DUP;
  }


  public static StackInstruction createDup_2(int size) {
    return (size == 2)? (StackInstruction)DUP2_X2 :
      (StackInstruction)DUP_X2;
  }


  public static StackInstruction createDup_1(int size) {
    return (size == 2)? (StackInstruction)DUP2_X1 :
      (StackInstruction)DUP_X1;
  }


  public static LocalVariableInstruction createStore(Type type, int index) {
    switch(type.getType()) {
    case Constants.T_BOOLEAN:
    case Constants.T_CHAR:
    case Constants.T_BYTE:
    case Constants.T_SHORT:
    case Constants.T_INT:    return new ISTORE(index);
    case Constants.T_FLOAT:  return new FSTORE(index);
    case Constants.T_DOUBLE: return new DSTORE(index);
    case Constants.T_LONG:   return new LSTORE(index);
    case Constants.T_ARRAY:
    case Constants.T_OBJECT: return new ASTORE(index);
    default:       throw new RuntimeException("Invalid type " + type);
    }
  }


  public static LocalVariableInstruction createLoad(Type type, int index) {
    switch(type.getType()) {
    case Constants.T_BOOLEAN:
    case Constants.T_CHAR:
    case Constants.T_BYTE:
    case Constants.T_SHORT:
    case Constants.T_INT:    return new ILOAD(index);
    case Constants.T_FLOAT:  return new FLOAD(index);
    case Constants.T_DOUBLE: return new DLOAD(index);
    case Constants.T_LONG:   return new LLOAD(index);
    case Constants.T_ARRAY:
    case Constants.T_OBJECT: return new ALOAD(index);
    default:       throw new RuntimeException("Invalid type " + type);
    }
  }


  public static ArrayInstruction createArrayLoad(Type type) {
    switch(type.getType()) {
    case Constants.T_BOOLEAN:
    case Constants.T_BYTE:   return BALOAD;
    case Constants.T_CHAR:   return CALOAD;
    case Constants.T_SHORT:  return SALOAD;
    case Constants.T_INT:    return IALOAD;
    case Constants.T_FLOAT:  return FALOAD;
    case Constants.T_DOUBLE: return DALOAD;
    case Constants.T_LONG:   return LALOAD;
    case Constants.T_ARRAY:
    case Constants.T_OBJECT: return AALOAD;
    default:       throw new RuntimeException("Invalid type " + type);
    }
  }


  public static ArrayInstruction createArrayStore(Type type) {
    switch(type.getType()) {
    case Constants.T_BOOLEAN:
    case Constants.T_BYTE:   return BASTORE;
    case Constants.T_CHAR:   return CASTORE;
    case Constants.T_SHORT:  return SASTORE;
    case Constants.T_INT:    return IASTORE;
    case Constants.T_FLOAT:  return FASTORE;
    case Constants.T_DOUBLE: return DASTORE;
    case Constants.T_LONG:   return LASTORE;
    case Constants.T_ARRAY:
    case Constants.T_OBJECT: return AASTORE;
    default:       throw new RuntimeException("Invalid type " + type);
    }
  }



  public Instruction createCast(Type src_type, Type dest_type) {
    if((src_type instanceof BasicType) && (dest_type instanceof BasicType)) {
      byte dest = dest_type.getType();
      byte src  = src_type.getType();

      if(dest == Constants.T_LONG && (src == Constants.T_CHAR || src == Constants.T_BYTE ||
                                      src == Constants.T_SHORT))
        src = Constants.T_INT;

      String[] short_names = { "C", "F", "D", "B", "S", "I", "L" };

      String name = "com.sun.org.apache.bcel.internal.generic." + short_names[src - Constants.T_CHAR] +
        "2" + short_names[dest - Constants.T_CHAR];

      Instruction i = null;
      try {
        i = (Instruction)java.lang.Class.forName(name).newInstance();
      } catch(Exception e) {
        throw new RuntimeException("Could not find instruction: " + name);
      }

      return i;
    } else if((src_type instanceof ReferenceType) && (dest_type instanceof ReferenceType)) {
      if(dest_type instanceof ArrayType)
        return new CHECKCAST(cp.addArrayClass((ArrayType)dest_type));
      else
        return new CHECKCAST(cp.addClass(((ObjectType)dest_type).getClassName()));
    }
    else
      throw new RuntimeException("Can not cast " + src_type + " to " + dest_type);
  }

  public GETFIELD createGetField(String class_name, String name, Type t) {
    return new GETFIELD(cp.addFieldref(class_name, name, t.getSignature()));
  }

  public GETSTATIC createGetStatic(String class_name, String name, Type t) {
    return new GETSTATIC(cp.addFieldref(class_name, name, t.getSignature()));
  }

  public PUTFIELD createPutField(String class_name, String name, Type t) {
    return new PUTFIELD(cp.addFieldref(class_name, name, t.getSignature()));
  }

  public PUTSTATIC createPutStatic(String class_name, String name, Type t) {
    return new PUTSTATIC(cp.addFieldref(class_name, name, t.getSignature()));
  }

  public CHECKCAST createCheckCast(ReferenceType t) {
    if(t instanceof ArrayType)
      return new CHECKCAST(cp.addArrayClass((ArrayType)t));
    else
      return new CHECKCAST(cp.addClass((ObjectType)t));
  }

  public INSTANCEOF createInstanceOf(ReferenceType t) {
    if(t instanceof ArrayType)
      return new INSTANCEOF(cp.addArrayClass((ArrayType)t));
    else
      return new INSTANCEOF(cp.addClass((ObjectType)t));
  }

  public NEW createNew(ObjectType t) {
    return new NEW(cp.addClass(t));
  }

  public NEW createNew(String s) {
    return createNew(new ObjectType(s));
  }


  public Instruction createNewArray(Type t, short dim) {
    if(dim == 1) {
      if(t instanceof ObjectType)
        return new ANEWARRAY(cp.addClass((ObjectType)t));
      else if(t instanceof ArrayType)
        return new ANEWARRAY(cp.addArrayClass((ArrayType)t));
      else
        return new NEWARRAY(((BasicType)t).getType());
    } else {
      ArrayType at;

      if(t instanceof ArrayType)
        at = (ArrayType)t;
      else
        at = new ArrayType(t, dim);

      return new MULTIANEWARRAY(cp.addArrayClass(at), dim);
    }
  }


  public static Instruction createNull(Type type) {
    switch(type.getType()) {
    case Constants.T_ARRAY:
    case Constants.T_OBJECT:  return ACONST_NULL;
    case Constants.T_INT:
    case Constants.T_SHORT:
    case Constants.T_BOOLEAN:
    case Constants.T_CHAR:
    case Constants.T_BYTE:    return ICONST_0;
    case Constants.T_FLOAT:   return FCONST_0;
    case Constants.T_DOUBLE:  return DCONST_0;
    case Constants.T_LONG:    return LCONST_0;
    case Constants.T_VOID:    return NOP;

    default:
      throw new RuntimeException("Invalid type: " + type);
    }
  }


  public static BranchInstruction createBranchInstruction(short opcode, InstructionHandle target) {
    switch(opcode) {
    case Constants.IFEQ:      return new IFEQ(target);
    case Constants.IFNE:      return new IFNE(target);
    case Constants.IFLT:      return new IFLT(target);
    case Constants.IFGE:      return new IFGE(target);
    case Constants.IFGT:      return new IFGT(target);
    case Constants.IFLE:      return new IFLE(target);
    case Constants.IF_ICMPEQ: return new IF_ICMPEQ(target);
    case Constants.IF_ICMPNE: return new IF_ICMPNE(target);
    case Constants.IF_ICMPLT: return new IF_ICMPLT(target);
    case Constants.IF_ICMPGE: return new IF_ICMPGE(target);
    case Constants.IF_ICMPGT: return new IF_ICMPGT(target);
    case Constants.IF_ICMPLE: return new IF_ICMPLE(target);
    case Constants.IF_ACMPEQ: return new IF_ACMPEQ(target);
    case Constants.IF_ACMPNE: return new IF_ACMPNE(target);
    case Constants.GOTO:      return new GOTO(target);
    case Constants.JSR:       return new JSR(target);
    case Constants.IFNULL:    return new IFNULL(target);
    case Constants.IFNONNULL: return new IFNONNULL(target);
    case Constants.GOTO_W:    return new GOTO_W(target);
    case Constants.JSR_W:     return new JSR_W(target);
    default:
        throw new RuntimeException("Invalid opcode: " + opcode);
    }
  }

  public void            setClassGen(ClassGen c)            { cg = c; }
  public ClassGen        getClassGen()                      { return cg; }
  public void            setConstantPool(ConstantPoolGen c) { cp = c; }
  public ConstantPoolGen getConstantPool()                  { return cp; }
}
