

package javax.script;


public interface Invocable  {

    public Object invokeMethod(Object thiz, String name, Object... args)
        throws ScriptException, NoSuchMethodException;


     public Object invokeFunction(String name, Object... args)
        throws ScriptException, NoSuchMethodException;



    public <T> T getInterface(Class<T> clasz);


     public <T> T getInterface(Object thiz, Class<T> clasz);

}
