package org.omg.IOP;



  public byte minor_version = (byte)0;

  public Encoding ()
  {
  }

  public Encoding (short _format, byte _major_version, byte _minor_version)
  {
    format = _format;
    major_version = _major_version;
    minor_version = _minor_version;
  }

}
