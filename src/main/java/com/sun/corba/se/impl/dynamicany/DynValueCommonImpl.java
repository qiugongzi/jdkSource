

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

abstract class DynValueCommonImpl extends DynAnyComplexImpl implements DynValueCommon
{
    protected boolean isNull;

    private DynValueCommonImpl() {
        this(null, (Any)null, false);
        isNull = true;
    }

    protected DynValueCommonImpl(ORB orb, Any any, boolean copyValue) {
        super(orb, any, copyValue);
        isNull = checkInitComponents();
    }

    protected DynValueCommonImpl(ORB orb, TypeCode typeCode) {
        super(orb, typeCode);
        isNull = true;
    }

    public boolean is_null() {
        return isNull;
    }

    public void set_to_null() {
        isNull = true;
        clearData();
    }

    public void set_to_value() {
        if (isNull) {
            isNull = false;
            }
        }

    public org.omg.DynamicAny.NameValuePair[] get_members ()
        throws org.omg.DynamicAny.DynAnyPackage.InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (isNull) {
            throw new InvalidValue();
        }
        checkInitComponents();
        return nameValuePairs;
    }

    public org.omg.DynamicAny.NameDynAnyPair[] get_members_as_dyn_any ()
        throws org.omg.DynamicAny.DynAnyPackage.InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (isNull) {
            throw new InvalidValue();
        }
        checkInitComponents();
        return nameDynAnyPairs;
    }

    public void set_members (org.omg.DynamicAny.NameValuePair[] value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue
    {
        super.set_members(value);
        isNull = false;
    }

    public void set_members_as_dyn_any (org.omg.DynamicAny.NameDynAnyPair[] value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue
    {
        super.set_members_as_dyn_any(value);
        isNull = false;
    }
}
