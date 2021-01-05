package org.omg.IOP;



  public byte context_data[] = null;

  public ServiceContext ()
  {
  }

  public ServiceContext (int _context_id, byte[] _context_data)
  {
    context_id = _context_id;
    context_data = _context_data;
  }

}
