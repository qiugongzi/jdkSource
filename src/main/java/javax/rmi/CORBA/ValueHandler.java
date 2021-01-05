


package javax.rmi.CORBA;


public interface ValueHandler {


    void writeValue(org.omg.CORBA.portable.OutputStream out,
                    java.io.Serializable value);


    java.io.Serializable readValue(org.omg.CORBA.portable.InputStream in,
                                   int offset,
                                   java.lang.Class clz,
                                   String repositoryID,
                                   org.omg.SendingContext.RunTime sender);


    java.lang.String getRMIRepositoryID(java.lang.Class clz);


    boolean isCustomMarshaled(java.lang.Class clz);


    org.omg.SendingContext.RunTime getRunTimeCodeBase();


    java.io.Serializable writeReplace(java.io.Serializable value);

}
