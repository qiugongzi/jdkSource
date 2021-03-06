


package javax.management.remote.rmi;

public final class RMIServerImpl_Stub
    extends java.rmi.server.RemoteStub
    implements javax.management.remote.rmi.RMIServer
{
    private static final long serialVersionUID = 2;
    
    private static java.lang.reflect.Method $method_getVersion_0;
    private static java.lang.reflect.Method $method_newClient_1;
    
    static {
	try {
	    $method_getVersion_0 = javax.management.remote.rmi.RMIServer.class.getMethod("getVersion", new java.lang.Class[] {});
	    $method_newClient_1 = javax.management.remote.rmi.RMIServer.class.getMethod("newClient", new java.lang.Class[] {java.lang.Object.class});
	} catch (java.lang.NoSuchMethodException e) {
	    throw new java.lang.NoSuchMethodError(
		"stub class initialization failed");
	}
    }
    

    public RMIServerImpl_Stub(java.rmi.server.RemoteRef ref) {
	super(ref);
    }
    

    

    public java.lang.String getVersion()
	throws java.rmi.RemoteException
    {
	try {
	    Object $result = ref.invoke(this, $method_getVersion_0, null, -8081107751519807347L);
	    return ((java.lang.String) $result);
	} catch (java.lang.RuntimeException e) {
	    throw e;
	} catch (java.rmi.RemoteException e) {
	    throw e;
	} catch (java.lang.Exception e) {
	    throw new java.rmi.UnexpectedException("undeclared checked exception", e);
	}
    }
    

    public javax.management.remote.rmi.RMIConnection newClient(java.lang.Object $param_Object_1)
	throws java.io.IOException
    {
	try {
	    Object $result = ref.invoke(this, $method_newClient_1, new java.lang.Object[] {$param_Object_1}, -1089742558549201240L);
	    return ((javax.management.remote.rmi.RMIConnection) $result);
	} catch (java.lang.RuntimeException e) {
	    throw e;
	} catch (java.io.IOException e) {
	    throw e;
	} catch (java.lang.Exception e) {
	    throw new java.rmi.UnexpectedException("undeclared checked exception", e);
	}
    }
}
