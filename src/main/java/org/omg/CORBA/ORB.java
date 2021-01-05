

package org.omg.CORBA;

import org.omg.CORBA.portable.*;
import org.omg.CORBA.ORBPackage.InvalidName;

import java.util.Properties;
import java.applet.Applet;
import java.io.File;
import java.io.FileInputStream;

import java.security.AccessController;
import java.security.PrivilegedAction;

import sun.reflect.misc.ReflectUtil;


abstract public class ORB {

    private static final String ORBClassKey = "org.omg.CORBA.ORBClass";
    private static final String ORBSingletonClassKey = "org.omg.CORBA.ORBSingletonClass";

    static private ORB singleton;

    private static String getSystemProperty(final String name) {

        String propValue = (String) AccessController.doPrivileged(
            new PrivilegedAction() {
                public java.lang.Object run() {
                    return System.getProperty(name);
                }
            }
        );

        return propValue;
    }

    private static String getPropertyFromFile(final String name) {
        String propValue = (String) AccessController.doPrivileged(
            new PrivilegedAction() {
                private Properties getFileProperties( String fileName ) {
                    try {
                        File propFile = new File( fileName ) ;
                        if (!propFile.exists())
                            return null ;

                        Properties props = new Properties() ;
                        FileInputStream fis = new FileInputStream(propFile);
                        try {
                            props.load( fis );
                        } finally {
                            fis.close() ;
                        }

                        return props ;
                    } catch (Exception exc) {
                        return null ;
                    }
                }

                public java.lang.Object run() {
                    String userHome = System.getProperty("user.home");
                    String fileName = userHome + File.separator +
                        "orb.properties" ;
                    Properties props = getFileProperties( fileName ) ;

                    if (props != null) {
                        String value = props.getProperty( name ) ;
                        if (value != null)
                            return value ;
                    }

                    String javaHome = System.getProperty("java.home");
                    fileName = javaHome + File.separator
                        + "lib" + File.separator + "orb.properties";
                    props = getFileProperties( fileName ) ;

                    if (props == null)
                        return null ;
                    else
                        return props.getProperty( name ) ;
                }
            }
        );

        return propValue;
    }


    public static synchronized ORB init() {
        if (singleton == null) {
            String className = getSystemProperty(ORBSingletonClassKey);
            if (className == null)
                className = getPropertyFromFile(ORBSingletonClassKey);
            if ((className == null) ||
                    (className.equals("com.sun.corba.se.impl.orb.ORBSingleton"))) {
                singleton = new com.sun.corba.se.impl.orb.ORBSingleton();
            } else {
                singleton = create_impl(className);
            }
        }
        return singleton;
    }

    private static ORB create_impl(String className) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null)
            cl = ClassLoader.getSystemClassLoader();

        try {
            ReflectUtil.checkPackageAccess(className);
            Class<org.omg.CORBA.ORB> orbBaseClass = org.omg.CORBA.ORB.class;
            Class<?> orbClass = Class.forName(className, true, cl).asSubclass(orbBaseClass);
            return (ORB)orbClass.newInstance();
        } catch (Throwable ex) {
            SystemException systemException = new INITIALIZE(
               "can't instantiate default ORB implementation " + className);
            systemException.initCause(ex);
            throw systemException;
        }
    }


    public static ORB init(String[] args, Properties props) {
        String className = null;
        ORB orb;

        if (props != null)
            className = props.getProperty(ORBClassKey);
        if (className == null)
            className = getSystemProperty(ORBClassKey);
        if (className == null)
            className = getPropertyFromFile(ORBClassKey);
        if ((className == null) ||
                    (className.equals("com.sun.corba.se.impl.orb.ORBImpl"))) {
            orb = new com.sun.corba.se.impl.orb.ORBImpl();
        } else {
            orb = create_impl(className);
        }
        orb.set_parameters(args, props);
        return orb;
    }



    public static ORB init(Applet app, Properties props) {
        String className;
        ORB orb;

        className = app.getParameter(ORBClassKey);
        if (className == null && props != null)
            className = props.getProperty(ORBClassKey);
        if (className == null)
            className = getSystemProperty(ORBClassKey);
        if (className == null)
            className = getPropertyFromFile(ORBClassKey);
        if ((className == null) ||
                    (className.equals("com.sun.corba.se.impl.orb.ORBImpl"))) {
            orb = new com.sun.corba.se.impl.orb.ORBImpl();
        } else {
            orb = create_impl(className);
        }
        orb.set_parameters(app, props);
        return orb;
    }


    abstract protected void set_parameters(String[] args, Properties props);


    abstract protected void set_parameters(Applet app, Properties props);


    public void connect(org.omg.CORBA.Object obj) {
        throw new NO_IMPLEMENT();
    }


    public void destroy( ) {
        throw new NO_IMPLEMENT();
    }


    public void disconnect(org.omg.CORBA.Object obj) {
        throw new NO_IMPLEMENT();
    }

    abstract public String[] list_initial_services();


    abstract public org.omg.CORBA.Object resolve_initial_references(String object_name)
        throws InvalidName;


    abstract public String object_to_string(org.omg.CORBA.Object obj);


    abstract public org.omg.CORBA.Object string_to_object(String str);


    abstract public NVList create_list(int count);


    public NVList create_operation_list(org.omg.CORBA.Object oper)
    {
        try {
            String opDefClassName = "org.omg.CORBA.OperationDef";
            Class<?> opDefClass = null;

            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if ( cl == null )
                cl = ClassLoader.getSystemClassLoader();
            opDefClass = Class.forName(opDefClassName, true, cl);

            Class<?>[] argc = { opDefClass };
            java.lang.reflect.Method meth =
                this.getClass().getMethod("create_operation_list", argc);

            java.lang.Object[] argx = { oper };
            return (org.omg.CORBA.NVList)meth.invoke(this, argx);
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
        }
        catch( RuntimeException ex ) {
            throw ex;
        }
        catch( Exception exr ) {
            throw new org.omg.CORBA.NO_IMPLEMENT();
        }
    }



    abstract public NamedValue create_named_value(String s, Any any, int flags);


    abstract public ExceptionList create_exception_list();


    abstract public ContextList create_context_list();


    abstract public Context get_default_context();


    abstract public Environment create_environment();


    abstract public org.omg.CORBA.portable.OutputStream create_output_stream();


    abstract public void send_multiple_requests_oneway(Request[] req);


    abstract public void send_multiple_requests_deferred(Request[] req);


    abstract public boolean poll_next_response();


    abstract public Request get_next_response() throws WrongTransaction;


    abstract public TypeCode get_primitive_tc(TCKind tcKind);


    abstract public TypeCode create_struct_tc(String id, String name,
                                              StructMember[] members);


    abstract public TypeCode create_union_tc(String id, String name,
                                             TypeCode discriminator_type,
                                             UnionMember[] members);


    abstract public TypeCode create_enum_tc(String id, String name, String[] members);


    abstract public TypeCode create_alias_tc(String id, String name,
                                             TypeCode original_type);


    abstract public TypeCode create_exception_tc(String id, String name,
                                                 StructMember[] members);



    abstract public TypeCode create_interface_tc(String id, String name);



    abstract public TypeCode create_string_tc(int bound);


    abstract public TypeCode create_wstring_tc(int bound);


    abstract public TypeCode create_sequence_tc(int bound, TypeCode element_type);


    @Deprecated
    abstract public TypeCode create_recursive_sequence_tc(int bound, int offset);


    abstract public TypeCode create_array_tc(int length, TypeCode element_type);


    public org.omg.CORBA.TypeCode create_native_tc(String id,
                                                   String name)
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }


    public org.omg.CORBA.TypeCode create_abstract_interface_tc(
                                                               String id,
                                                               String name)
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }



    public org.omg.CORBA.TypeCode create_fixed_tc(short digits, short scale)
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }


    public org.omg.CORBA.TypeCode create_value_tc(String id,
                                                  String name,
                                                  short type_modifier,
                                                  TypeCode concrete_base,
                                                  ValueMember[] members)
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }


    public org.omg.CORBA.TypeCode create_recursive_tc(String id) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }


    public org.omg.CORBA.TypeCode create_value_box_tc(String id,
                                                      String name,
                                                      TypeCode boxed_type)
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    abstract public Any create_any();





    @Deprecated
    public org.omg.CORBA.Current get_current()
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }


    public void run()
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }


    public void shutdown(boolean wait_for_completion)
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }


    public boolean work_pending()
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }


    public void perform_work()
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }


    public boolean get_service_information(short service_type,
                                           ServiceInformationHolder service_info)
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    @Deprecated
    public org.omg.CORBA.DynAny create_dyn_any(org.omg.CORBA.Any value)
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }


    @Deprecated
    public org.omg.CORBA.DynAny create_basic_dyn_any(org.omg.CORBA.TypeCode type) throws org.omg.CORBA.ORBPackage.InconsistentTypeCode
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }


    @Deprecated
    public org.omg.CORBA.DynStruct create_dyn_struct(org.omg.CORBA.TypeCode type) throws org.omg.CORBA.ORBPackage.InconsistentTypeCode
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }


    @Deprecated
    public org.omg.CORBA.DynSequence create_dyn_sequence(org.omg.CORBA.TypeCode type) throws org.omg.CORBA.ORBPackage.InconsistentTypeCode
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }



    @Deprecated
    public org.omg.CORBA.DynArray create_dyn_array(org.omg.CORBA.TypeCode type) throws org.omg.CORBA.ORBPackage.InconsistentTypeCode
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }


    @Deprecated
    public org.omg.CORBA.DynUnion create_dyn_union(org.omg.CORBA.TypeCode type) throws org.omg.CORBA.ORBPackage.InconsistentTypeCode
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }


    @Deprecated
    public org.omg.CORBA.DynEnum create_dyn_enum(org.omg.CORBA.TypeCode type) throws org.omg.CORBA.ORBPackage.InconsistentTypeCode
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }


    public org.omg.CORBA.Policy create_policy(int type, org.omg.CORBA.Any val)
        throws org.omg.CORBA.PolicyError
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }
}
