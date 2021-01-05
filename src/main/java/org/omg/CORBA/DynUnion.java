


package org.omg.CORBA;


@Deprecated
public interface DynUnion extends org.omg.CORBA.Object, org.omg.CORBA.DynAny
{

    public boolean set_as_default();


    public void set_as_default(boolean arg);


    public org.omg.CORBA.DynAny discriminator();


    public org.omg.CORBA.TCKind discriminator_kind();


    public org.omg.CORBA.DynAny member();


    public String member_name();


    public void member_name(String arg);


    public org.omg.CORBA.TCKind member_kind();
}
