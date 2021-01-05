

package java.lang.invoke;

import java.lang.reflect.*;
import java.util.*;
import java.lang.invoke.MethodHandleNatives.Constants;
import java.lang.invoke.MethodHandles.Lookup;
import static java.lang.invoke.MethodHandleStatics.*;


public
interface MethodHandleInfo {

    public static final int
        REF_getField                = Constants.REF_getField,
        REF_getStatic               = Constants.REF_getStatic,
        REF_putField                = Constants.REF_putField,
        REF_putStatic               = Constants.REF_putStatic,
        REF_invokeVirtual           = Constants.REF_invokeVirtual,
        REF_invokeStatic            = Constants.REF_invokeStatic,
        REF_invokeSpecial           = Constants.REF_invokeSpecial,
        REF_newInvokeSpecial        = Constants.REF_newInvokeSpecial,
        REF_invokeInterface         = Constants.REF_invokeInterface;


    public int getReferenceKind();


    public Class<?> getDeclaringClass();


    public String getName();


    public MethodType getMethodType();

    public <T extends Member> T reflectAs(Class<T> expected, Lookup lookup);


    public int getModifiers();


    public default boolean isVarArgs()  {
        if (MethodHandleNatives.refKindIsField((byte) getReferenceKind()))
            return false;
        final int ACC_VARARGS = 0x00000080;  assert(ACC_VARARGS == Modifier.TRANSIENT);
        return Modifier.isTransient(getModifiers());
    }


    public static String referenceKindToString(int referenceKind) {
        if (!MethodHandleNatives.refKindIsValid(referenceKind))
            throw newIllegalArgumentException("invalid reference kind", referenceKind);
        return MethodHandleNatives.refKindName((byte)referenceKind);
    }


    public static String toString(int kind, Class<?> defc, String name, MethodType type) {
        Objects.requireNonNull(name); Objects.requireNonNull(type);
        return String.format("%s %s.%s:%s", referenceKindToString(kind), defc.getName(), name, type);
    }
}
