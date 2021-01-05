package org.omg.IOP;



  public byte component_data[] = null;

  public TaggedComponent ()
  {
  }

  public TaggedComponent (int _tag, byte[] _component_data)
  {
    tag = _tag;
    component_data = _component_data;
  }

}
