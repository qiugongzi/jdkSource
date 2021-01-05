
package org.omg.CORBA.portable;

import org.omg.CORBA.Request;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.NVList;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.Context;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.SystemException;




abstract public class ObjectImpl implements org.omg.CORBA.Object
{


    private transient Delegate __delegate;



    public Delegate _get_delegate() {
        if (__delegate == null)
            throw new BAD_OPERATION("The delegate has not been set!");
        return __delegate;
    }



    public void _set_delegate(Delegate delegate) {
        __delegate = delegate;
    }


    public abstract String[] _ids();



    public org.omg.CORBA.Object _duplicate() {
        return _get_delegate().duplicate(this);
    }


    public void _release() {
        _get_delegate().release(this);
    }


    public boolean _is_a(String repository_id) {
        return _get_delegate().is_a(this, repository_id);
    }


    public boolean _is_equivalent(org.omg.CORBA.Object that) {
        return _get_delegate().is_equivalent(this, that);
    }


    public boolean _non_existent() {
        return _get_delegate().non_existent(this);
    }


    public int _hash(int maximum) {
        return _get_delegate().hash(this, maximum);
    }


    public Request _request(String operation) {
        return _get_delegate().request(this, operation);
    }


    public Request _create_request(Context ctx,
                                   String operation,
                                   NVList arg_list,
                                   NamedValue result) {
        return _get_delegate().create_request(this,
                                              ctx,
                                              operation,
                                              arg_list,
                                              result);
    }


    public Request _create_request(Context ctx,
                                   String operation,
                                   NVList arg_list,
                                   NamedValue result,
                                   ExceptionList exceptions,
                                   ContextList contexts) {
        return _get_delegate().create_request(this,
                                              ctx,
                                              operation,
                                              arg_list,
                                              result,
                                              exceptions,
                                              contexts);
    }


    public org.omg.CORBA.Object _get_interface_def()
    {
        org.omg.CORBA.portable.Delegate delegate = _get_delegate();
        try {
            return delegate.get_interface_def(this);
        }
        catch( org.omg.CORBA.NO_IMPLEMENT ex ) {
            try {
                Class[] argc = { org.omg.CORBA.Object.class };
                java.lang.reflect.Method meth =
                    delegate.getClass().getMethod("get_interface", argc);
                Object[] argx = { this };
                return (org.omg.CORBA.Object)meth.invoke(delegate, argx);
            }
            catch( java.lang.reflect.InvocationTargetException exs ) {
                Throwable t = exs.getTargetException();
                if (t instanceof Error) {
                    throw (Error) t;
                }
                else if (t instanceof RuntimeException) {
                    throw (RuntimeException) t;
                }
                else {
                    throw new org.omg.CORBA.NO_IMPLEMENT();
                }
            } catch( RuntimeException rex ) {
                throw rex;
            } catch( Exception exr ) {
                throw new org.omg.CORBA.NO_IMPLEMENT();
            }
        }
    }


    public org.omg.CORBA.ORB _orb() {
        return _get_delegate().orb(this);
    }



    public org.omg.CORBA.Policy _get_policy(int policy_type) {
        return _get_delegate().get_policy(this, policy_type);
    }


    public org.omg.CORBA.DomainManager[] _get_domain_managers() {
        return _get_delegate().get_domain_managers(this);
    }


    public org.omg.CORBA.Object
        _set_policy_override(org.omg.CORBA.Policy[] policies,
                             org.omg.CORBA.SetOverrideType set_add) {
        return _get_delegate().set_policy_override(this, policies,
                                                   set_add);
    }


    public boolean _is_local() {
        return _get_delegate().is_local(this);
    }


    public ServantObject _servant_preinvoke(String operation,
                                            Class expectedType) {
        return _get_delegate().servant_preinvoke(this, operation,
                                                 expectedType);
    }


    public void _servant_postinvoke(ServantObject servant) {
        _get_delegate().servant_postinvoke(this, servant);
    }




    public OutputStream _request(String operation,
                                 boolean responseExpected) {
        return _get_delegate().request(this, operation, responseExpected);
    }


    public InputStream _invoke(OutputStream output)
        throws ApplicationException, RemarshalException {
        return _get_delegate().invoke(this, output);
    }


    public void _releaseReply(InputStream input) {
        _get_delegate().releaseReply(this, input);
    }


    public String toString() {
        if ( __delegate != null )
           return __delegate.toString(this);
        else
           return getClass().getName() + ": no delegate set";
    }


    public int hashCode() {
        if ( __delegate != null )
           return __delegate.hashCode(this);
        else
            return super.hashCode();
    }


    public boolean equals(java.lang.Object obj) {
        if ( __delegate != null )
           return __delegate.equals(this, obj);
        else
           return (this==obj);
    }
}
