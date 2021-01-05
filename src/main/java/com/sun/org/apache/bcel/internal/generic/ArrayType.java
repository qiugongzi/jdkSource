
package com.sun.org.apache.bcel.internal.generic;


import com.sun.org.apache.bcel.internal.Constants;


public final class ArrayType extends ReferenceType {
  private int  dimensions;
  private Type basic_type;


  public ArrayType(byte type, int dimensions) {
    this(BasicType.getType(type), dimensions);
  }


  public ArrayType(String class_name, int dimensions) {
    this(new ObjectType(class_name), dimensions);
  }


  public ArrayType(Type type, int dimensions) {
    super(Constants.T_ARRAY, "<dummy>");

    if((dimensions < 1) || (dimensions > Constants.MAX_BYTE))
      throw new ClassGenException("Invalid number of dimensions: " + dimensions);

    switch(type.getType()) {
    case Constants.T_ARRAY:
      ArrayType array = (ArrayType)type;
      this.dimensions = dimensions + array.dimensions;
      basic_type      = array.basic_type;
      break;

    case Constants.T_VOID:
      throw new ClassGenException("Invalid type: void[]");

    default: this.dimensions = dimensions;
      basic_type = type;
      break;
    }

    StringBuffer buf = new StringBuffer();
    for(int i=0; i < this.dimensions; i++)
      buf.append('[');

    buf.append(basic_type.getSignature());

    signature = buf.toString();
  }


  public Type getBasicType() {
    return basic_type;
  }


  public Type getElementType() {
    if(dimensions == 1)
      return basic_type;
    else
      return new ArrayType(basic_type, dimensions - 1);
  }


  public int getDimensions() { return dimensions; }


  public int hashCode() { return basic_type.hashCode() ^ dimensions; }


  public boolean equals(Object type) {
    if(type instanceof ArrayType) {
      ArrayType array = (ArrayType)type;
      return (array.dimensions == dimensions) && array.basic_type.equals(basic_type);
    } else
      return false;
  }
}
