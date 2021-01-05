
public interface RepositoryIdInterface
{
    Class getClassFromType() throws ClassNotFoundException;

    Class getClassFromType(String codebaseURL)
        throws ClassNotFoundException, MalformedURLException;

    Class getClassFromType(Class expectedType,
                           String codebaseURL)
        throws ClassNotFoundException, MalformedURLException;

    String getClassName();
}
