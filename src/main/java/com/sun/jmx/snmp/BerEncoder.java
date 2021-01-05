


package com.sun.jmx.snmp;




public class BerEncoder {



  public BerEncoder(byte b[]) {
    bytes = b ;
    start = b.length ;
    stackTop = 0 ;
  }




  public int trim() {
    final int result = bytes.length - start ;

    if (result > 0)
        java.lang.System.arraycopy(bytes,start,bytes,0,result);

    start = bytes.length ;
    stackTop = 0 ;

    return result ;
  }



  public void putInteger(int v) {
    putInteger(v, IntegerTag) ;
  }




  public void putInteger(int v, int tag) {
    putIntegerValue(v) ;
    putTag(tag) ;
  }





  public void putInteger(long v) {
    putInteger(v, IntegerTag) ;
  }




  public void putInteger(long v, int tag) {
    putIntegerValue(v) ;
    putTag(tag) ;
  }





  public void putOctetString(byte[] s) {
    putOctetString(s, OctetStringTag) ;
  }




  public void putOctetString(byte[] s, int tag) {
    putStringValue(s) ;
    putTag(tag) ;
  }




  public void putOid(long[] s) {
    putOid(s, OidTag) ;
  }




  public void putOid(long[] s, int tag) {
    putOidValue(s) ;
    putTag(tag) ;
  }




  public void putNull() {
    putNull(NullTag) ;
  }




  public void putNull(int tag) {
    putLength(0) ;
    putTag(tag) ;
  }





  public void putAny(byte[] s) {
        putAny(s, s.length) ;
  }




  public void putAny(byte[] s, int byteCount) {
      java.lang.System.arraycopy(s,0,bytes,start-byteCount,byteCount);
      start -= byteCount;
      }




  public void openSequence() {
    stackBuf[stackTop++] = start ;
  }




  public void closeSequence() {
    closeSequence(SequenceTag) ;
  }




  public void closeSequence(int tag) {
    final int end = stackBuf[--stackTop] ;
    putLength(end - start) ;
    putTag(tag) ;
  }


  public final static int BooleanTag      = 1 ;
  public final static int IntegerTag      = 2 ;
  public final static int OctetStringTag  = 4 ;
  public final static int NullTag          = 5 ;
  public final static int OidTag          = 6 ;
  public final static int SequenceTag      = 0x30 ;




  protected final void putTag(int tag) {
    if (tag < 256) {
      bytes[--start] = (byte)tag ;
    }
    else {
      while (tag != 0) {
        bytes[--start] = (byte)(tag & 127) ;
        tag = tag << 7 ;
      }
    }
  }




  protected final void putLength(final int length) {
    if (length < 0) {
      throw new IllegalArgumentException() ;
    }
    else if (length < 128) {
      bytes[--start] = (byte)length ;
    }
    else if (length < 256) {
      bytes[--start] = (byte)length ;
      bytes[--start] = (byte)0x81 ;
    }
    else if (length < 65536) {
      bytes[--start] = (byte)(length) ;
      bytes[--start] = (byte)(length >> 8) ;
      bytes[--start] = (byte)0x82 ;
    }
    else if (length < 16777126) {
      bytes[--start] = (byte)(length) ;
      bytes[--start] = (byte)(length >> 8) ;
      bytes[--start] = (byte)(length >> 16) ;
      bytes[--start] = (byte)0x83 ;
    }
    else {
      bytes[--start] = (byte)(length) ;
      bytes[--start] = (byte)(length >> 8) ;
      bytes[--start] = (byte)(length >> 16) ;
      bytes[--start] = (byte)(length >> 24) ;
      bytes[--start] = (byte)0x84 ;
    }
  }




  protected final void putIntegerValue(int v) {
    final int end = start ;
    int mask = 0x7f800000 ;
    int byteNeeded = 4 ;
    if (v < 0) {
      while (((mask & v) == mask) && (byteNeeded > 1)) {
        mask = mask >> 8 ;
        byteNeeded-- ;
      }
    }
    else {
      while (((mask & v) == 0) && (byteNeeded > 1)) {
        mask = mask >> 8 ;
        byteNeeded-- ;
      }
    }
    for (int i = 0 ; i < byteNeeded ; i++) {
      bytes[--start] = (byte)v ;
      v =  v >> 8 ;
    }
    putLength(end - start) ;
  }




  protected final void putIntegerValue(long v) {
    final int end = start ;
    long mask = 0x7f80000000000000L ;
    int byteNeeded = 8 ;
    if (v < 0) {
      while (((mask & v) == mask) && (byteNeeded > 1)) {
        mask = mask >> 8 ;
        byteNeeded-- ;
      }
    }
    else {
      while (((mask & v) == 0) && (byteNeeded > 1)) {
        mask = mask >> 8 ;
        byteNeeded-- ;
      }
    }
    for (int i = 0 ; i < byteNeeded ; i++) {
      bytes[--start] = (byte)v ;
      v =  v >> 8 ;
    }
    putLength(end - start) ;
  }




  protected final void putStringValue(byte[] s) {
      final int datalen = s.length;
      java.lang.System.arraycopy(s,0,bytes,start-datalen,datalen);
      start -= datalen;
      putLength(datalen) ;
  }





  protected final void putOidValue(final long[] s) {
      final int end = start ;
      final int slength = s.length;

      if ((slength < 2) || (s[0] > 2) || (s[1] >= 40)) {
          throw new IllegalArgumentException() ;
      }
      for (int i = slength - 1 ; i >= 2 ; i--) {
          long c = s[i] ;
          if (c < 0) {
              throw new IllegalArgumentException() ;
          }
          else if (c < 128) {
              bytes[--start] = (byte)c ;
          }
          else {
              bytes[--start] = (byte)(c & 127) ;
              c = c >> 7 ;
              while (c != 0) {
                  bytes[--start] = (byte)(c | 128) ;
                  c = c >> 7 ;
              }
          }
      }
      bytes[--start] = (byte)(s[0] * 40 + s[1]) ;
      putLength(end - start) ;
  }


  protected final byte bytes[];

  protected int start = -1 ;

  protected final int stackBuf[] = new int[200] ;
  protected int stackTop = 0 ;

}
