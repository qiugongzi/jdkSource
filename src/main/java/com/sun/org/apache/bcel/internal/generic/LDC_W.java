
package com.sun.org.apache.bcel.internal.generic;


import java.io.IOException;
import com.sun.org.apache.bcel.internal.util.ByteSequence;


public class LDC_W extends LDC {

  LDC_W() {}

  public LDC_W(int index) {
    super(index);
  }


  protected void initFromFile(ByteSequence bytes, boolean wide)
       throws IOException
  {
    setIndex(bytes.readUnsignedShort());
    opcode = com.sun.org.apache.bcel.internal.Constants.LDC_W;
  }
}
