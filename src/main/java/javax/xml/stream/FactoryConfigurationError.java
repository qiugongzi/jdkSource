
  public String getMessage() {
    String msg = super.getMessage();
    if(msg != null)
      return msg;
    if(nested != null){
      msg = nested.getMessage();
      if(msg == null)
        msg = nested.getClass().toString();
    }
    return msg;
  }



}
