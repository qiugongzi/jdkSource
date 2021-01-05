
package com.sun.org.apache.bcel.internal.generic;



import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.classfile.*;
import java.util.ArrayList;


public abstract class Type implements java.io.Serializable {
  protected byte   type;
  protected String signature; public static final BasicType     VOID         = new BasicType(Constants.T_VOID);
  public static final BasicType     BOOLEAN      = new BasicType(Constants.T_BOOLEAN);
  public static final BasicType     INT          = new BasicType(Constants.T_INT);
  public static final BasicType     SHORT        = new BasicType(Constants.T_SHORT);
  public static final BasicType     BYTE         = new BasicType(Constants.T_BYTE);
  public static final BasicType     LONG         = new BasicType(Constants.T_LONG);
  public static final BasicType     DOUBLE       = new BasicType(Constants.T_DOUBLE);
  public static final BasicType     FLOAT        = new BasicType(Constants.T_FLOAT);
  public static final BasicType     CHAR         = new BasicType(Constants.T_CHAR);
  public static final ObjectType    OBJECT       = new ObjectType("java.lang.Object");
  public static final ObjectType    STRING       = new ObjectType("java.lang.String");
  public static final ObjectType    STRINGBUFFER = new ObjectType("java.lang.StringBuffer");
  public static final ObjectType    THROWABLE    = new ObjectType("java.lang.Throwable");
  public static final Type[]        NO_ARGS      = new Type[0];
  public static final ReferenceType NULL         = new ReferenceType(){};
  public static final Type          UNKNOWN      = new Type(Constants.T_UNKNOWN,
                                                            "<unknown object>"){};

  protected Type(byte t, String s) {
    type      = t;
    signature = s;
  }


  public String getSignature() { return signature; }


  public byte getType() { return type; }


  public int getSize() {
    switch(type) {
    case Constants.T_DOUBLE:
    case Constants.T_LONG: return 2;
    case Constants.T_VOID: return 0;
    default:     return 1;
    }
  }


  public String toString() {
    return ((this.equals(Type.NULL) || (type >= Constants.T_UNKNOWN)))? signature :
      Utility.signatureToString(signature, false);
  }


  public static String getMethodSignature(Type return_type, Type[] arg_types) {
    StringBuffer buf = new StringBuffer("(");
    int length = (arg_types == null)? 0 : arg_types.length;

    for(int i=0; i < length; i++)
      buf.append(arg_types[i].getSignature());

    buf.append(')');
    buf.append(return_type.getSignature());

    return buf.toString();
  }

  private static int consumed_chars=0; public static final Type getType(String signature)
    throws StringIndexOutOfBoundsException
  {
    byte type = Utility.typeOfSignature(signature);

    if(type <= Constants.T_VOID) {
      consumed_chars = 1;
      return BasicType.getType(type);
    } else if(type == Constants.T_ARRAY) {
      int dim=0;
      do { dim++;
      } while(signature.charAt(dim) == '[');

      Type t = getType(signature.substring(dim));

      consumed_chars += dim; return new ArrayType(t, dim);
    } else { int index = signature.indexOf(';'); if(index < 0)
        throw new ClassFormatException("Invalid signature: " + signature);

      consumed_chars = index + 1; return new ObjectType(signature.substring(1, index).replace('/', '.'));
    }
  }


  public static Type getReturnType(String signature) {
    try {
      int index = signature.lastIndexOf(')') + 1;
      return getType(signature.substring(index));
    } catch(StringIndexOutOfBoundsException e) { throw new ClassFormatException("Invalid method signature: " + signature);
    }
  }


  public static Type[] getArgumentTypes(String signature) {
    ArrayList vec = new ArrayList();
    int       index;
    Type[]     types;

    try { if(signature.charAt(0) != '(')
        throw new ClassFormatException("Invalid method signature: " + signature);

      index = 1; while(signature.charAt(index) != ')') {
        vec.add(getType(signature.substring(index)));
        index += consumed_chars; }
    } catch(StringIndexOutOfBoundsException e) { throw new ClassFormatException("Invalid method signature: " + signature);
    }

    types = new Type[vec.size()];
    vec.toArray(types);
    return types;
  }


  public static Type getType(java.lang.Class cl) {
    if(cl == null) {
      throw new IllegalArgumentException("Class must not be null");
    }


    if(cl.isArray()) {
      return getType(cl.getName());
    } else if(cl.isPrimitive()) {
      if(cl == Integer.TYPE) {
        return INT;
      } else if(cl == Void.TYPE) {
        return VOID;
      } else if(cl == Double.TYPE) {
        return DOUBLE;
      } else if(cl == Float.TYPE) {
        return FLOAT;
      } else if(cl == Boolean.TYPE) {
        return BOOLEAN;
      } else if(cl == Byte.TYPE) {
        return BYTE;
      } else if(cl == Short.TYPE) {
        return SHORT;
      } else if(cl == Byte.TYPE) {
        return BYTE;
      } else if(cl == Long.TYPE) {
        return LONG;
      } else if(cl == Character.TYPE) {
        return CHAR;
      } else {
        throw new IllegalStateException("Ooops, what primitive type is " + cl);
      }
    } else { return new ObjectType(cl.getName());
    }
  }

  public static String getSignature(java.lang.reflect.Method meth) {
    StringBuffer sb = new StringBuffer("(");
    Class[] params = meth.getParameterTypes(); for(int j = 0; j < params.length; j++) {
      sb.append(getType(params[j]).getSignature());
    }

    sb.append(")");
    sb.append(getType(meth.getReturnType()).getSignature());
    return sb.toString();
  }
}
