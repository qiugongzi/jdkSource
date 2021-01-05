
package com.sun.org.apache.bcel.internal.generic;



import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.classfile.*;
import java.util.ArrayList;
import java.util.Iterator;


public class FieldGen extends FieldGenOrMethodGen {
  private Object value = null;


  public FieldGen(int access_flags, Type type, String name, ConstantPoolGen cp) {
    setAccessFlags(access_flags);
    setType(type);
    setName(name);
    setConstantPool(cp);
  }


  public FieldGen(Field field, ConstantPoolGen cp) {
    this(field.getAccessFlags(), Type.getType(field.getSignature()), field.getName(), cp);

    Attribute[] attrs = field.getAttributes();

    for(int i=0; i < attrs.length; i++) {
      if(attrs[i] instanceof ConstantValue)
        setValue(((ConstantValue)attrs[i]).getConstantValueIndex());
      else
        addAttribute(attrs[i]);
    }
  }

  private void setValue(int index) {
    ConstantPool cp  = this.cp.getConstantPool();
    Constant     c   = cp.getConstant(index);
    value = ((ConstantObject)c).getConstantValue(cp);
  }


  public void setInitValue(String str) {
    checkType(new ObjectType("java.lang.String"));

    if(str != null)
      value = str;
  }

  public void setInitValue(long l) {
    checkType(Type.LONG);

    if(l != 0L)
      value = new Long(l);
  }

  public void setInitValue(int i) {
    checkType(Type.INT);

    if(i != 0)
      value = new Integer(i);
  }

  public void setInitValue(short s) {
    checkType(Type.SHORT);

    if(s != 0)
      value = new Integer(s);
  }

  public void setInitValue(char c) {
    checkType(Type.CHAR);

    if(c != 0)
      value = new Integer(c);
  }

  public void setInitValue(byte b) {
    checkType(Type.BYTE);

    if(b != 0)
      value = new Integer(b);
  }

  public void setInitValue(boolean b) {
    checkType(Type.BOOLEAN);

    if(b)
      value = new Integer(1);
  }

  public void setInitValue(float f) {
    checkType(Type.FLOAT);

    if(f != 0.0)
      value = new Float(f);
  }

  public void setInitValue(double d) {
    checkType(Type.DOUBLE);

    if(d != 0.0)
      value = new Double(d);
  }


  public void cancelInitValue() {
    value = null;
  }

  private void checkType(Type atype) {
    if(type == null)
      throw new ClassGenException("You haven't defined the type of the field yet");

    if(!isFinal())
      throw new ClassGenException("Only final fields may have an initial value!");

    if(!type.equals(atype))
      throw new ClassGenException("Types are not compatible: " + type + " vs. " + atype);
  }


  public Field getField() {
    String      signature       = getSignature();
    int         name_index      = cp.addUtf8(name);
    int         signature_index = cp.addUtf8(signature);

    if(value != null) {
      checkType(type);
      int index = addConstant();
      addAttribute(new ConstantValue(cp.addUtf8("ConstantValue"),
                                     2, index, cp.getConstantPool()));
    }

    return new Field(access_flags, name_index, signature_index, getAttributes(),
                     cp.getConstantPool());
  }

  private int addConstant() {
    switch(type.getType()) {
    case Constants.T_INT: case Constants.T_CHAR: case Constants.T_BYTE:
    case Constants.T_BOOLEAN: case Constants.T_SHORT:
      return cp.addInteger(((Integer)value).intValue());

    case Constants.T_FLOAT:
      return cp.addFloat(((Float)value).floatValue());

    case Constants.T_DOUBLE:
      return cp.addDouble(((Double)value).doubleValue());

    case Constants.T_LONG:
      return cp.addLong(((Long)value).longValue());

    case Constants.T_REFERENCE:
      return cp.addString(((String)value));

    default:
      throw new RuntimeException("Oops: Unhandled : " + type.getType());
    }
  }

  public String  getSignature()  { return type.getSignature(); }

  private ArrayList observers;


  public void addObserver(FieldObserver o) {
    if(observers == null)
      observers = new ArrayList();

    observers.add(o);
  }


  public void removeObserver(FieldObserver o) {
    if(observers != null)
      observers.remove(o);
  }


  public void update() {
    if(observers != null)
      for(Iterator e = observers.iterator(); e.hasNext(); )
        ((FieldObserver)e.next()).notify(this);
  }

  public String getInitValue() {
    if(value != null) {
      return value.toString();
    } else
      return null;
  }


  public final String toString() {
    String name, signature, access; access    = Utility.accessToString(access_flags);
    access    = access.equals("")? "" : (access + " ");
    signature = type.toString();
    name      = getName();

    StringBuffer buf = new StringBuffer(access + signature + " " + name);
    String value = getInitValue();

    if(value != null)
      buf.append(" = " + value);

    return buf.toString();
  }


  public FieldGen copy(ConstantPoolGen cp) {
    FieldGen fg = (FieldGen)clone();

    fg.setConstantPool(cp);
    return fg;
  }
}
