package org.omg.PortableInterceptor.ORBInitInfoPackage;



  public String name = null;

  public DuplicateName ()
  {
    super(DuplicateNameHelper.id());
  }

  public DuplicateName (String _name)
  {
    super(DuplicateNameHelper.id());
    name = _name;
  }


  public DuplicateName (String $reason, String _name)
  {
    super(DuplicateNameHelper.id() + "  " + $reason);
    name = _name;
  }

}
