


package com.sun.jmx.snmp;






public class BerDecoder {



  public BerDecoder(byte b[]) {
    bytes = b ;
    reset() ;
  }

  public void reset() {
    next = 0 ;
    stackTop = 0 ;
  }



  public int fetchInteger() throws BerException {
    return fetchInteger(IntegerTag) ;
  }




  public int fetchInteger(int tag) throws BerException {
    int result = 0 ;
    final int backup = next ;
    try {
      if (fetchTag() != tag) {
        throw new BerException() ;
      }
      result = fetchIntegerValue() ;
    }
    catch(BerException e) {
      next = backup ;
      throw e ;
    }

    return result ;
  }





  public long fetchIntegerAsLong() throws BerException {
    return fetchIntegerAsLong(IntegerTag) ;
  }




  public long fetchIntegerAsLong(int tag) throws BerException {
    long result = 0 ;
    final int backup = next ;
    try {
      if (fetchTag() != tag) {
        throw new BerException() ;
      }
      result = fetchIntegerValueAsLong() ;
    }
    catch(BerException e) {
      next = backup ;
      throw e ;
    }

    return result ;
  }





  public byte[] fetchOctetString() throws BerException {
    return fetchOctetString(OctetStringTag) ;
  }




  public byte[] fetchOctetString(int tag) throws BerException {
    byte[] result = null ;
    final int backup = next ;
    try {
      if (fetchTag() != tag) {
        throw new BerException() ;
      }
      result = fetchStringValue() ;
    }
    catch(BerException e) {
      next = backup ;
      throw e ;
    }

    return result ;
  }




  public long[] fetchOid() throws BerException {
    return fetchOid(OidTag) ;
  }




  public long[] fetchOid(int tag) throws BerException {
    long[] result = null ;
    final int backup = next ;
    try {
      if (fetchTag() != tag) {
        throw new BerException() ;
      }
      result = fetchOidValue() ;
    }
    catch(BerException e) {
      next = backup ;
      throw e ;
    }

    return result ;
  }




  public void fetchNull() throws BerException {
    fetchNull(NullTag) ;
  }




  public void fetchNull(int tag) throws BerException {
    final int backup = next ;
    try {
      if (fetchTag() != tag) {
        throw new BerException() ;
      }
      final int length = fetchLength();
      if (length != 0) throw new BerException();
    }
    catch(BerException e) {
      next = backup ;
      throw e ;
    }
  }





  public byte[] fetchAny() throws BerException {
    byte[] result = null ;
    final int backup = next ;
    try {
      final int tag = fetchTag() ;
      final int contentLength = fetchLength() ;
      if (contentLength < 0) throw new BerException() ;
      final int tlvLength = next + contentLength - backup ;
      if (contentLength > (bytes.length - next))
          throw new IndexOutOfBoundsException("Decoded length exceeds buffer");
      final byte[] data = new byte[tlvLength] ;
      java.lang.System.arraycopy(bytes,backup,data,0,tlvLength);
      next = next + contentLength ;
      result = data;
    }
    catch(IndexOutOfBoundsException e) {
      next = backup ;
      throw new BerException() ;
    }
    return result ;
  }




  public byte[] fetchAny(int tag) throws BerException {
    if (getTag() != tag) {
      throw new BerException() ;
    }
    return fetchAny() ;
  }





  public void openSequence() throws BerException {
    openSequence(SequenceTag) ;
  }




  public void openSequence(int tag) throws BerException {
    final int backup = next ;
    try {
      if (fetchTag() != tag) {
        throw new BerException() ;
      }
      final int l = fetchLength() ;
      if (l < 0) throw new BerException();
      if (l > (bytes.length - next)) throw new BerException();
      stackBuf[stackTop++] = next + l ;
    }
    catch(BerException e) {
      next = backup ;
      throw e ;
    }
  }




  public void closeSequence() throws BerException {
    if (stackBuf[stackTop - 1] == next) {
      stackTop-- ;
    }
    else {
      throw new BerException() ;
    }
  }




  public boolean cannotCloseSequence() {
    return (next < stackBuf[stackTop - 1]) ;
  }




  public int getTag() throws BerException {
    int result = 0 ;
    final int backup = next ;
    try {
      result = fetchTag() ;
    }
    finally {
      next = backup ;
    }

    return result ;
  }



  public String toString() {
    final StringBuffer result = new StringBuffer(bytes.length * 2) ;
    for (int i = 0 ; i < bytes.length ; i++) {
      final int b = (bytes[i] > 0) ? bytes[i] : bytes[i] + 256 ;
      if (i == next) {
        result.append("(") ;
      }
      result.append(Character.forDigit(b / 16, 16)) ;
      result.append(Character.forDigit(b % 16, 16)) ;
      if (i == next) {
        result.append(")") ;
      }
    }
    if (bytes.length == next) {
      result.append("()") ;
    }

    return new String(result) ;
  }


  public final static int BooleanTag      = 1 ;
  public final static int IntegerTag      = 2 ;
  public final static int OctetStringTag  = 4 ;
  public final static int NullTag          = 5 ;
  public final static int OidTag          = 6 ;
  public final static int SequenceTag      = 0x30 ;




  private final int fetchTag() throws BerException {
    int result = 0 ;
    final int backup = next ;

    try {
      final byte b0 = bytes[next++] ;
      result = (b0 >= 0) ? b0 : b0 + 256 ;
      if ((result & 31) == 31) {
        while ((bytes[next] & 128) != 0) {
          result = result << 7 ;
          result = result | (bytes[next++] & 127);
        }
      }
    }
    catch(IndexOutOfBoundsException e) {
      next = backup ;
      throw new BerException() ;
    }

    return result ;
  }




  private final int fetchLength() throws BerException {
    int result = 0 ;
    final int backup = next ;

    try {
      final byte b0 = bytes[next++] ;
      if (b0 >= 0) {
        result = b0 ;
      }
      else {
        for (int c = 128 + b0 ; c > 0 ; c--) {
          final byte bX = bytes[next++] ;
          result = result << 8 ;
          result = result | ((bX >= 0) ? bX : bX+256) ;
        }
      }
    }
    catch(IndexOutOfBoundsException e) {
      next = backup ;
      throw new BerException() ;
    }

    return result ;
  }




  private int fetchIntegerValue() throws BerException {
    int result = 0 ;
    final int backup = next ;

    try {
      final int length = fetchLength() ;
      if (length <= 0) throw new BerException() ;
      if (length > (bytes.length - next)) throw
          new IndexOutOfBoundsException("Decoded length exceeds buffer");
      final int end = next + length ;
      result = bytes[next++] ;
      while (next < end) {
        final byte b = bytes[next++] ;
        if (b < 0) {
          result = (result << 8) | (256 + b) ;
        }
        else {
          result = (result << 8) | b ;
        }
      }
    }
    catch(BerException e) {
      next = backup ;
      throw e ;
    }
    catch(IndexOutOfBoundsException e) {
      next = backup ;
      throw new BerException() ;
    }
    catch(ArithmeticException e) {
      next = backup ;
      throw new BerException() ;
    }
    return result ;
  }




  private final long fetchIntegerValueAsLong() throws BerException {
    long result = 0 ;
    final int backup = next ;

    try {
      final int length = fetchLength() ;
      if (length <= 0) throw new BerException() ;
      if (length > (bytes.length - next)) throw
          new IndexOutOfBoundsException("Decoded length exceeds buffer");

      final int end = next + length ;
      result = bytes[next++] ;
      while (next < end) {
        final byte b = bytes[next++] ;
        if (b < 0) {
          result = (result << 8) | (256 + b) ;
        }
        else {
          result = (result << 8) | b ;
        }
      }
    }
    catch(BerException e) {
      next = backup ;
      throw e ;
    }
    catch(IndexOutOfBoundsException e) {
      next = backup ;
      throw new BerException() ;
    }
    catch(ArithmeticException e) {
      next = backup ;
      throw new BerException() ;
    }
    return result ;
  }




  private byte[] fetchStringValue() throws BerException {
    byte[] result = null ;
    final int backup = next ;

    try {
      final int length = fetchLength() ;
      if (length < 0) throw new BerException() ;
      if (length > (bytes.length - next))
          throw new IndexOutOfBoundsException("Decoded length exceeds buffer");
      final byte data[] = new byte[length] ;
      java.lang.System.arraycopy(bytes,next,data,0,length);
      next += length;
      result = data;
    }
    catch(BerException e) {
        next = backup ;
      throw e ;
    }
    catch(IndexOutOfBoundsException e) {
      next = backup ;
      throw new BerException() ;
    }
    catch(ArithmeticException e) {
      next = backup ;
      throw new BerException() ;
    }
    return result ;
  }





  private final long[] fetchOidValue() throws BerException {
    long[] result = null ;
    final int backup = next ;

    try {
      final int length = fetchLength() ;
      if (length <= 0) throw new BerException() ;
      if (length > (bytes.length - next))
          throw new IndexOutOfBoundsException("Decoded length exceeds buffer");
      int subidCount = 2 ;
      for (int i = 1 ; i < length ; i++) {
        if ((bytes[next + i] & 0x80) == 0) {
          subidCount++ ;
        }
      }
      final int datalen = subidCount;
      final long[] data = new long[datalen];
      final byte b0 = bytes[next++] ;

      if (b0 < 0) throw new BerException();

      final long lb0 =  b0 / 40 ;
      if (lb0 > 2) throw new BerException();

      final long lb1 = b0 % 40;
      data[0] = lb0 ;
      data[1] = lb1 ;
      int i = 2 ;
      while (i < datalen) {
        long subid = 0 ;
        byte b = bytes[next++] ;
        while ((b & 0x80) != 0) {
          subid = (subid << 7) | (b & 0x7f) ;
          if (subid < 0) throw new BerException();
          b = bytes[next++] ;
        }
        subid = (subid << 7) | b ;
        if (subid < 0) throw new BerException();
        data[i++] = subid ;
      }
      result = data;
    }
    catch(BerException e) {
      next = backup ;
      throw e ;
    }
    catch(IndexOutOfBoundsException e) {
      next = backup ;
      throw new BerException() ;
    }
    return result ;
  }

    private final byte bytes[];

  private int next = 0 ;

  private final int stackBuf[] = new int[200] ;
  private int stackTop = 0 ;

}
