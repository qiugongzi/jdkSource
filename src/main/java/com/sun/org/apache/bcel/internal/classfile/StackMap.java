
package com.sun.org.apache.bcel.internal.classfile;



import  com.sun.org.apache.bcel.internal.Constants;
import  java.io.*;


public final class StackMap extends Attribute implements Node {
  private int             map_length;
  private StackMapEntry[] map; public StackMap(int name_index, int length,  StackMapEntry[] map,
                  ConstantPool constant_pool)
  {
    super(Constants.ATTR_STACK_MAP, name_index, length, constant_pool);

    setStackMap(map);
  }


  StackMap(int name_index, int length, DataInputStream file,
           ConstantPool constant_pool) throws IOException
  {
    this(name_index, length, (StackMapEntry[])null, constant_pool);

    map_length = file.readUnsignedShort();
    map = new StackMapEntry[map_length];

    for(int i=0; i < map_length; i++)
      map[i] = new StackMapEntry(file, constant_pool);
  }


  public final void dump(DataOutputStream file) throws IOException
  {
    super.dump(file);
    file.writeShort(map_length);
    for(int i=0; i < map_length; i++)
      map[i].dump(file);
  }


  public final StackMapEntry[] getStackMap() { return map; }


  public final void setStackMap(StackMapEntry[] map) {
    this.map = map;

    map_length = (map == null)? 0 : map.length;
  }


  public final String toString() {
    StringBuffer buf = new StringBuffer("StackMap(");

    for(int i=0; i < map_length; i++) {
      buf.append(map[i].toString());

      if(i < map_length - 1)
        buf.append(", ");
    }

    buf.append(')');

    return buf.toString();
  }


  public Attribute copy(ConstantPool constant_pool) {
    StackMap c = (StackMap)clone();

    c.map = new StackMapEntry[map_length];
    for(int i=0; i < map_length; i++)
      c.map[i] = map[i].copy();

    c.constant_pool = constant_pool;
    return c;
  }


   public void accept(Visitor v) {
     v.visitStackMap(this);
   }

  public final int getMapLength() { return map_length; }
}
