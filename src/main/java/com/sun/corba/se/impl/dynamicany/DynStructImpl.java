

package com.sun.corba.se.impl.dynamicany;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TypeCodePackage.Bounds;
import org.omg.DynamicAny.*;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;

import com.sun.corba.se.spi.orb.ORB ;
import com.sun.corba.se.spi.logging.CORBALogDomains ;
import com.sun.corba.se.impl.logging.ORBUtilSystemException ;

public class DynStructImpl extends DynAnyComplexImpl implements DynStruct
{




    private DynStructImpl() {
        this(null, (Any)null, false);
    }

    protected DynStructImpl(ORB orb, Any any, boolean copyValue) {

        super(orb, any, copyValue);


    }

    protected DynStructImpl(ORB orb, TypeCode typeCode) {

        super(orb, typeCode);



        index = 0;
    }





    public org.omg.DynamicAny.NameValuePair[] get_members () {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        checkInitComponents();
        return nameValuePairs;
    }

    public org.omg.DynamicAny.NameDynAnyPair[] get_members_as_dyn_any () {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        checkInitComponents();
        return nameDynAnyPairs;
    }
}
