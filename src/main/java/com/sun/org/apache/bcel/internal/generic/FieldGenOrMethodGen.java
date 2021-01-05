
package com.sun.org.apache.bcel.internal.generic;
import com.sun.org.apache.bcel.internal.Constants;



import com.sun.org.apache.bcel.internal.classfile.*;
import java.util.ArrayList;


public abstract class FieldGenOrMethodGen extends AccessFlags
  implements NamedAndTyped, Cloneable
{
  protected String          name;
  protected Type            type;
  protected ConstantPoolGen cp;
  private   ArrayList       attribute_vec = new ArrayList();

  protected FieldGenOrMethodGen() {}

  public void            setType(Type type)   {
    if(type.getType() == Constants.T_ADDRESS)
      throw new IllegalArgumentException("Type can not be " + type);

    this.type = type;
  }
  public Type            getType()            { return type; }


  public String          getName()            { return name; }
  public void            setName(String name) { this.name = name; }

  public ConstantPoolGen getConstantPool()                   { return cp; }
  public void            setConstantPool(ConstantPoolGen cp) { this.cp = cp; }


  public void addAttribute(Attribute a) { attribute_vec.add(a); }


  public void removeAttribute(Attribute a) { attribute_vec.remove(a); }


  public void removeAttributes() { attribute_vec.clear(); }


  public Attribute[] getAttributes() {
    Attribute[] attributes = new Attribute[attribute_vec.size()];
    attribute_vec.toArray(attributes);
    return attributes;
  }


  public abstract String  getSignature();

  public Object clone() {
    try {
      return super.clone();
    } catch(CloneNotSupportedException e) {
      System.err.println(e);
      return null;
    }
  }
}
