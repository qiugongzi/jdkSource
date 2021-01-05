
package com.sun.org.apache.xml.internal.resolver;


public class CatalogException extends Exception {

  public static final int WRAPPER = 1;

  public static final int INVALID_ENTRY = 2;

  public static final int INVALID_ENTRY_TYPE = 3;

  public static final int NO_XML_PARSER = 4;

  public static final int UNKNOWN_FORMAT = 5;

  public static final int UNPARSEABLE = 6;

  public static final int PARSE_FAILED = 7;

  public static final int UNENDED_COMMENT = 8;


  private Exception exception = null;
  private int exceptionType = 0;


  public CatalogException (int type, String message) {
    super(message);
    this.exceptionType = type;
    this.exception = null;
  }


  public CatalogException (int type) {
    super("Catalog Exception " + type);
    this.exceptionType = type;
    this.exception = null;
  }


  public CatalogException (Exception e) {
    super();
    this.exceptionType = WRAPPER;
    this.exception = e;
  }


  public CatalogException (String message, Exception e) {
    super(message);
    this.exceptionType = WRAPPER;
    this.exception = e;
  }


  public String getMessage ()
  {
    String message = super.getMessage();

    if (message == null && exception != null) {
      return exception.getMessage();
    } else {
      return message;
    }
  }


  public Exception getException ()
  {
    return exception;
  }


  public int getExceptionType ()
  {
    return exceptionType;
  }


  public String toString ()
  {
    if (exception != null) {
      return exception.toString();
    } else {
      return super.toString();
    }
  }
}
