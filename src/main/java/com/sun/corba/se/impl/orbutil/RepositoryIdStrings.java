
public interface RepositoryIdStrings
{
    String createForAnyType(Class type);

    String createForJavaType(Serializable ser)
        throws TypeMismatchException;

    String createForJavaType(Class clz)
        throws TypeMismatchException;

    String createSequenceRepID(java.lang.Object ser);

    String createSequenceRepID(java.lang.Class clazz);

    RepositoryIdInterface getFromString(String repIdString);

    String getClassDescValueRepId();
    String getWStringValueRepId();
}
