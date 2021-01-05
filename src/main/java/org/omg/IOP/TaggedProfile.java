package org.omg.IOP;



  public byte profile_data[] = null;

  public TaggedProfile ()
  {
  }

  public TaggedProfile (int _tag, byte[] _profile_data)
  {
    tag = _tag;
    profile_data = _profile_data;
  }

}
