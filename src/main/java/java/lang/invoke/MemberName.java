

package java.lang.invoke;

import sun.invoke.util.BytecodeDescriptor;
import sun.invoke.util.VerifyAccess;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import static java.lang.invoke.MethodHandleNatives.Constants.*;
import static java.lang.invoke.MethodHandleStatics.*;
import java.util.Objects;


 final class MemberName implements Member, Cloneable {
    private Class<?> clazz;       private String   name;        private Object   type;        private int      flags;       private Object   resolution;  public Class<?> getDeclaringClass() {
        return clazz;
    }


    public ClassLoader getClassLoader() {
        return clazz.getClassLoader();
    }


    public String getName() {
        if (name == null) {
            expandFromVM();
            if (name == null) {
                return null;
            }
        }
        return name;
    }

    public MethodType getMethodOrFieldType() {
        if (isInvocable())
            return getMethodType();
        if (isGetter())
            return MethodType.methodType(getFieldType());
        if (isSetter())
            return MethodType.methodType(void.class, getFieldType());
        throw new InternalError("not a method or field: "+this);
    }


    public MethodType getMethodType() {
        if (type == null) {
            expandFromVM();
            if (type == null) {
                return null;
            }
        }
        if (!isInvocable()) {
            throw newIllegalArgumentException("not invocable, no method type");
        }

        {
            final Object type = this.type;
            if (type instanceof MethodType) {
                return (MethodType) type;
            }
        }

        synchronized (this) {
            if (type instanceof String) {
                String sig = (String) type;
                MethodType res = MethodType.fromMethodDescriptorString(sig, getClassLoader());
                type = res;
            } else if (type instanceof Object[]) {
                Object[] typeInfo = (Object[]) type;
                Class<?>[] ptypes = (Class<?>[]) typeInfo[1];
                Class<?> rtype = (Class<?>) typeInfo[0];
                MethodType res = MethodType.methodType(rtype, ptypes);
                type = res;
            }
            assert type instanceof MethodType : "bad method type " + type;
        }
        return (MethodType) type;
    }


    public MethodType getInvocationType() {
        MethodType itype = getMethodOrFieldType();
        if (isConstructor() && getReferenceKind() == REF_newInvokeSpecial)
            return itype.changeReturnType(clazz);
        if (!isStatic())
            return itype.insertParameterTypes(0, clazz);
        return itype;
    }


    public Class<?>[] getParameterTypes() {
        return getMethodType().parameterArray();
    }


    public Class<?> getReturnType() {
        return getMethodType().returnType();
    }


    public Class<?> getFieldType() {
        if (type == null) {
            expandFromVM();
            if (type == null) {
                return null;
            }
        }
        if (isInvocable()) {
            throw newIllegalArgumentException("not a field or nested class, no simple type");
        }

        {
            final Object type = this.type;
            if (type instanceof Class<?>) {
                return (Class<?>) type;
            }
        }

        synchronized (this) {
            if (type instanceof String) {
                String sig = (String) type;
                MethodType mtype = MethodType.fromMethodDescriptorString("()"+sig, getClassLoader());
                Class<?> res = mtype.returnType();
                type = res;
            }
            assert type instanceof Class<?> : "bad field type " + type;
        }
        return (Class<?>) type;
    }


    public Object getType() {
        return (isInvocable() ? getMethodType() : getFieldType());
    }


    public String getSignature() {
        if (type == null) {
            expandFromVM();
            if (type == null) {
                return null;
            }
        }
        if (isInvocable())
            return BytecodeDescriptor.unparse(getMethodType());
        else
            return BytecodeDescriptor.unparse(getFieldType());
    }


    public int getModifiers() {
        return (flags & RECOGNIZED_MODIFIERS);
    }


    public byte getReferenceKind() {
        return (byte) ((flags >>> MN_REFERENCE_KIND_SHIFT) & MN_REFERENCE_KIND_MASK);
    }
    private boolean referenceKindIsConsistent() {
        byte refKind = getReferenceKind();
        if (refKind == REF_NONE)  return isType();
        if (isField()) {
            assert(staticIsConsistent());
            assert(MethodHandleNatives.refKindIsField(refKind));
        } else if (isConstructor()) {
            assert(refKind == REF_newInvokeSpecial || refKind == REF_invokeSpecial);
        } else if (isMethod()) {
            assert(staticIsConsistent());
            assert(MethodHandleNatives.refKindIsMethod(refKind));
            if (clazz.isInterface())
                assert(refKind == REF_invokeInterface ||
                       refKind == REF_invokeStatic    ||
                       refKind == REF_invokeSpecial   ||
                       refKind == REF_invokeVirtual && isObjectPublicMethod());
        } else {
            assert(false);
        }
        return true;
    }
    private boolean isObjectPublicMethod() {
        if (clazz == Object.class)  return true;
        MethodType mtype = getMethodType();
        if (name.equals("toString") && mtype.returnType() == String.class && mtype.parameterCount() == 0)
            return true;
        if (name.equals("hashCode") && mtype.returnType() == int.class && mtype.parameterCount() == 0)
            return true;
        if (name.equals("equals") && mtype.returnType() == boolean.class && mtype.parameterCount() == 1 && mtype.parameterType(0) == Object.class)
            return true;
        return false;
    }
     boolean referenceKindIsConsistentWith(int originalRefKind) {
        int refKind = getReferenceKind();
        if (refKind == originalRefKind)  return true;
        switch (originalRefKind) {
        case REF_invokeInterface:
            assert(refKind == REF_invokeVirtual ||
                   refKind == REF_invokeSpecial) : this;
            return true;
        case REF_invokeVirtual:
        case REF_newInvokeSpecial:
            assert(refKind == REF_invokeSpecial) : this;
            return true;
        }
        assert(false) : this+" != "+MethodHandleNatives.refKindName((byte)originalRefKind);
        return true;
    }
    private boolean staticIsConsistent() {
        byte refKind = getReferenceKind();
        return MethodHandleNatives.refKindIsStatic(refKind) == isStatic() || getModifiers() == 0;
    }
    private boolean vminfoIsConsistent() {
        byte refKind = getReferenceKind();
        assert(isResolved());  Object vminfo = MethodHandleNatives.getMemberVMInfo(this);
        assert(vminfo instanceof Object[]);
        long vmindex = (Long) ((Object[])vminfo)[0];
        Object vmtarget = ((Object[])vminfo)[1];
        if (MethodHandleNatives.refKindIsField(refKind)) {
            assert(vmindex >= 0) : vmindex + ":" + this;
            assert(vmtarget instanceof Class);
        } else {
            if (MethodHandleNatives.refKindDoesDispatch(refKind))
                assert(vmindex >= 0) : vmindex + ":" + this;
            else
                assert(vmindex < 0) : vmindex;
            assert(vmtarget instanceof MemberName) : vmtarget + " in " + this;
        }
        return true;
    }

    private MemberName changeReferenceKind(byte refKind, byte oldKind) {
        assert(getReferenceKind() == oldKind);
        assert(MethodHandleNatives.refKindIsValid(refKind));
        flags += (((int)refKind - oldKind) << MN_REFERENCE_KIND_SHIFT);
        return this;
    }

    private boolean testFlags(int mask, int value) {
        return (flags & mask) == value;
    }
    private boolean testAllFlags(int mask) {
        return testFlags(mask, mask);
    }
    private boolean testAnyFlags(int mask) {
        return !testFlags(mask, 0);
    }


    public boolean isMethodHandleInvoke() {
        final int bits = MH_INVOKE_MODS &~ Modifier.PUBLIC;
        final int negs = Modifier.STATIC;
        if (testFlags(bits | negs, bits) &&
            clazz == MethodHandle.class) {
            return isMethodHandleInvokeName(name);
        }
        return false;
    }
    public static boolean isMethodHandleInvokeName(String name) {
        switch (name) {
        case "invoke":
        case "invokeExact":
            return true;
        default:
            return false;
        }
    }
    private static final int MH_INVOKE_MODS = Modifier.NATIVE | Modifier.FINAL | Modifier.PUBLIC;


    public boolean isStatic() {
        return Modifier.isStatic(flags);
    }

    public boolean isPublic() {
        return Modifier.isPublic(flags);
    }

    public boolean isPrivate() {
        return Modifier.isPrivate(flags);
    }

    public boolean isProtected() {
        return Modifier.isProtected(flags);
    }

    public boolean isFinal() {
        return Modifier.isFinal(flags);
    }

    public boolean canBeStaticallyBound() {
        return Modifier.isFinal(flags | clazz.getModifiers());
    }

    public boolean isVolatile() {
        return Modifier.isVolatile(flags);
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(flags);
    }

    public boolean isNative() {
        return Modifier.isNative(flags);
    }
    static final int BRIDGE    = 0x00000040;
    static final int VARARGS   = 0x00000080;
    static final int SYNTHETIC = 0x00001000;
    static final int ANNOTATION= 0x00002000;
    static final int ENUM      = 0x00004000;

    public boolean isBridge() {
        return testAllFlags(IS_METHOD | BRIDGE);
    }

    public boolean isVarargs() {
        return testAllFlags(VARARGS) && isInvocable();
    }

    public boolean isSynthetic() {
        return testAllFlags(SYNTHETIC);
    }

    static final String CONSTRUCTOR_NAME = "<init>";  static final int RECOGNIZED_MODIFIERS = 0xFFFF;

    static final int
            IS_METHOD        = MN_IS_METHOD,        IS_CONSTRUCTOR   = MN_IS_CONSTRUCTOR,   IS_FIELD         = MN_IS_FIELD,         IS_TYPE          = MN_IS_TYPE,          CALLER_SENSITIVE = MN_CALLER_SENSITIVE; static final int ALL_ACCESS = Modifier.PUBLIC | Modifier.PRIVATE | Modifier.PROTECTED;
    static final int ALL_KINDS = IS_METHOD | IS_CONSTRUCTOR | IS_FIELD | IS_TYPE;
    static final int IS_INVOCABLE = IS_METHOD | IS_CONSTRUCTOR;
    static final int IS_FIELD_OR_METHOD = IS_METHOD | IS_FIELD;
    static final int SEARCH_ALL_SUPERS = MN_SEARCH_SUPERCLASSES | MN_SEARCH_INTERFACES;


    public boolean isInvocable() {
        return testAnyFlags(IS_INVOCABLE);
    }

    public boolean isFieldOrMethod() {
        return testAnyFlags(IS_FIELD_OR_METHOD);
    }

    public boolean isMethod() {
        return testAllFlags(IS_METHOD);
    }

    public boolean isConstructor() {
        return testAllFlags(IS_CONSTRUCTOR);
    }

    public boolean isField() {
        return testAllFlags(IS_FIELD);
    }

    public boolean isType() {
        return testAllFlags(IS_TYPE);
    }

    public boolean isPackage() {
        return !testAnyFlags(ALL_ACCESS);
    }

    public boolean isCallerSensitive() {
        return testAllFlags(CALLER_SENSITIVE);
    }


    public boolean isAccessibleFrom(Class<?> lookupClass) {
        return VerifyAccess.isMemberAccessible(this.getDeclaringClass(), this.getDeclaringClass(), flags,
                                               lookupClass, ALL_ACCESS|MethodHandles.Lookup.PACKAGE);
    }


    private void init(Class<?> defClass, String name, Object type, int flags) {
        this.clazz = defClass;
        this.name = name;
        this.type = type;
        this.flags = flags;
        assert(testAnyFlags(ALL_KINDS));
        assert(this.resolution == null);  }


    private void expandFromVM() {
        if (type != null) {
            return;
        }
        if (!isResolved()) {
            return;
        }
        MethodHandleNatives.expand(this);
    }

    private static int flagsMods(int flags, int mods, byte refKind) {
        assert((flags & RECOGNIZED_MODIFIERS) == 0);
        assert((mods & ~RECOGNIZED_MODIFIERS) == 0);
        assert((refKind & ~MN_REFERENCE_KIND_MASK) == 0);
        return flags | mods | (refKind << MN_REFERENCE_KIND_SHIFT);
    }

    public MemberName(Method m) {
        this(m, false);
    }
    @SuppressWarnings("LeakingThisInConstructor")
    public MemberName(Method m, boolean wantSpecial) {
        m.getClass();  MethodHandleNatives.init(this, m);
        if (clazz == null) {  if (m.getDeclaringClass() == MethodHandle.class &&
                isMethodHandleInvokeName(m.getName())) {
                MethodType type = MethodType.methodType(m.getReturnType(), m.getParameterTypes());
                int flags = flagsMods(IS_METHOD, m.getModifiers(), REF_invokeVirtual);
                init(MethodHandle.class, m.getName(), type, flags);
                if (isMethodHandleInvoke())
                    return;
            }
            throw new LinkageError(m.toString());
        }
        assert(isResolved() && this.clazz != null);
        this.name = m.getName();
        if (this.type == null)
            this.type = new Object[] { m.getReturnType(), m.getParameterTypes() };
        if (wantSpecial) {
            if (isAbstract())
                throw new AbstractMethodError(this.toString());
            if (getReferenceKind() == REF_invokeVirtual)
                changeReferenceKind(REF_invokeSpecial, REF_invokeVirtual);
            else if (getReferenceKind() == REF_invokeInterface)
                changeReferenceKind(REF_invokeSpecial, REF_invokeInterface);
        }
    }
    public MemberName asSpecial() {
        switch (getReferenceKind()) {
        case REF_invokeSpecial:     return this;
        case REF_invokeVirtual:     return clone().changeReferenceKind(REF_invokeSpecial, REF_invokeVirtual);
        case REF_invokeInterface:   return clone().changeReferenceKind(REF_invokeSpecial, REF_invokeInterface);
        case REF_newInvokeSpecial:  return clone().changeReferenceKind(REF_invokeSpecial, REF_newInvokeSpecial);
        }
        throw new IllegalArgumentException(this.toString());
    }

    public MemberName asConstructor() {
        switch (getReferenceKind()) {
        case REF_invokeSpecial:     return clone().changeReferenceKind(REF_newInvokeSpecial, REF_invokeSpecial);
        case REF_newInvokeSpecial:  return this;
        }
        throw new IllegalArgumentException(this.toString());
    }

    public MemberName asNormalOriginal() {
        byte normalVirtual = clazz.isInterface() ? REF_invokeInterface : REF_invokeVirtual;
        byte refKind = getReferenceKind();
        byte newRefKind = refKind;
        MemberName result = this;
        switch (refKind) {
        case REF_invokeInterface:
        case REF_invokeVirtual:
        case REF_invokeSpecial:
            newRefKind = normalVirtual;
            break;
        }
        if (newRefKind == refKind)
            return this;
        result = clone().changeReferenceKind(newRefKind, refKind);
        assert(this.referenceKindIsConsistentWith(result.getReferenceKind()));
        return result;
    }

    @SuppressWarnings("LeakingThisInConstructor")
    public MemberName(Constructor<?> ctor) {
        ctor.getClass();  MethodHandleNatives.init(this, ctor);
        assert(isResolved() && this.clazz != null);
        this.name = CONSTRUCTOR_NAME;
        if (this.type == null)
            this.type = new Object[] { void.class, ctor.getParameterTypes() };
    }

    public MemberName(Field fld) {
        this(fld, false);
    }
    @SuppressWarnings("LeakingThisInConstructor")
    public MemberName(Field fld, boolean makeSetter) {
        fld.getClass();  MethodHandleNatives.init(this, fld);
        assert(isResolved() && this.clazz != null);
        this.name = fld.getName();
        this.type = fld.getType();
        assert((REF_putStatic - REF_getStatic) == (REF_putField - REF_getField));
        byte refKind = this.getReferenceKind();
        assert(refKind == (isStatic() ? REF_getStatic : REF_getField));
        if (makeSetter) {
            changeReferenceKind((byte)(refKind + (REF_putStatic - REF_getStatic)), refKind);
        }
    }
    public boolean isGetter() {
        return MethodHandleNatives.refKindIsGetter(getReferenceKind());
    }
    public boolean isSetter() {
        return MethodHandleNatives.refKindIsSetter(getReferenceKind());
    }
    public MemberName asSetter() {
        byte refKind = getReferenceKind();
        assert(MethodHandleNatives.refKindIsGetter(refKind));
        assert((REF_putStatic - REF_getStatic) == (REF_putField - REF_getField));
        byte setterRefKind = (byte)(refKind + (REF_putField - REF_getField));
        return clone().changeReferenceKind(setterRefKind, refKind);
    }

    public MemberName(Class<?> type) {
        init(type.getDeclaringClass(), type.getSimpleName(), type,
                flagsMods(IS_TYPE, type.getModifiers(), REF_NONE));
        initResolved(true);
    }


    static MemberName makeMethodHandleInvoke(String name, MethodType type) {
        return makeMethodHandleInvoke(name, type, MH_INVOKE_MODS | SYNTHETIC);
    }
    static MemberName makeMethodHandleInvoke(String name, MethodType type, int mods) {
        MemberName mem = new MemberName(MethodHandle.class, name, type, REF_invokeVirtual);
        mem.flags |= mods;  assert(mem.isMethodHandleInvoke()) : mem;
        return mem;
    }

    MemberName() { }

    @Override protected MemberName clone() {
        try {
            return (MemberName) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw newInternalError(ex);
        }
     }


    public MemberName getDefinition() {
        if (!isResolved())  throw new IllegalStateException("must be resolved: "+this);
        if (isType())  return this;
        MemberName res = this.clone();
        res.clazz = null;
        res.type = null;
        res.name = null;
        res.resolution = res;
        res.expandFromVM();
        assert(res.getName().equals(this.getName()));
        return res;
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, getReferenceKind(), name, getType());
    }
    @Override
    public boolean equals(Object that) {
        return (that instanceof MemberName && this.equals((MemberName)that));
    }


    public boolean equals(MemberName that) {
        if (this == that)  return true;
        if (that == null)  return false;
        return this.clazz == that.clazz
                && this.getReferenceKind() == that.getReferenceKind()
                && Objects.equals(this.name, that.name)
                && Objects.equals(this.getType(), that.getType());
    }

    public MemberName(Class<?> defClass, String name, Class<?> type, byte refKind) {
        init(defClass, name, type, flagsMods(IS_FIELD, 0, refKind));
        initResolved(false);
    }

    public MemberName(Class<?> defClass, String name, MethodType type, byte refKind) {
        int initFlags = (name != null && name.equals(CONSTRUCTOR_NAME) ? IS_CONSTRUCTOR : IS_METHOD);
        init(defClass, name, type, flagsMods(initFlags, 0, refKind));
        initResolved(false);
    }

    public MemberName(byte refKind, Class<?> defClass, String name, Object type) {
        int kindFlags;
        if (MethodHandleNatives.refKindIsField(refKind)) {
            kindFlags = IS_FIELD;
            if (!(type instanceof Class))
                throw newIllegalArgumentException("not a field type");
        } else if (MethodHandleNatives.refKindIsMethod(refKind)) {
            kindFlags = IS_METHOD;
            if (!(type instanceof MethodType))
                throw newIllegalArgumentException("not a method type");
        } else if (refKind == REF_newInvokeSpecial) {
            kindFlags = IS_CONSTRUCTOR;
            if (!(type instanceof MethodType) ||
                !CONSTRUCTOR_NAME.equals(name))
                throw newIllegalArgumentException("not a constructor type or name");
        } else {
            throw newIllegalArgumentException("bad reference kind "+refKind);
        }
        init(defClass, name, type, flagsMods(kindFlags, 0, refKind));
        initResolved(false);
    }

    public boolean hasReceiverTypeDispatch() {
        return MethodHandleNatives.refKindDoesDispatch(getReferenceKind());
    }


    public boolean isResolved() {
        return resolution == null;
    }

    private void initResolved(boolean isResolved) {
        assert(this.resolution == null);  if (!isResolved)
            this.resolution = this;
        assert(isResolved() == isResolved);
    }

    void checkForTypeAlias(Class<?> refc) {
        if (isInvocable()) {
            MethodType type;
            if (this.type instanceof MethodType)
                type = (MethodType) this.type;
            else
                this.type = type = getMethodType();
            if (type.erase() == type)  return;
            if (VerifyAccess.isTypeVisible(type, refc))  return;
            throw new LinkageError("bad method type alias: "+type+" not visible from "+refc);
        } else {
            Class<?> type;
            if (this.type instanceof Class<?>)
                type = (Class<?>) this.type;
            else
                this.type = type = getFieldType();
            if (VerifyAccess.isTypeVisible(type, refc))  return;
            throw new LinkageError("bad field type alias: "+type+" not visible from "+refc);
        }
    }



    @SuppressWarnings("LocalVariableHidesMemberVariable")
    @Override
    public String toString() {
        if (isType())
            return type.toString();  StringBuilder buf = new StringBuilder();
        if (getDeclaringClass() != null) {
            buf.append(getName(clazz));
            buf.append('.');
        }
        String name = getName();
        buf.append(name == null ? "*" : name);
        Object type = getType();
        if (!isInvocable()) {
            buf.append('/');
            buf.append(type == null ? "*" : getName(type));
        } else {
            buf.append(type == null ? "(*)*" : getName(type));
        }
        byte refKind = getReferenceKind();
        if (refKind != REF_NONE) {
            buf.append('/');
            buf.append(MethodHandleNatives.refKindName(refKind));
        }
        return buf.toString();
    }
    private static String getName(Object obj) {
        if (obj instanceof Class<?>)
            return ((Class<?>)obj).getName();
        return String.valueOf(obj);
    }

    public IllegalAccessException makeAccessException(String message, Object from) {
        message = message + ": "+ toString();
        if (from != null)  message += ", from " + from;
        return new IllegalAccessException(message);
    }
    private String message() {
        if (isResolved())
            return "no access";
        else if (isConstructor())
            return "no such constructor";
        else if (isMethod())
            return "no such method";
        else
            return "no such field";
    }
    public ReflectiveOperationException makeAccessException() {
        String message = message() + ": "+ toString();
        ReflectiveOperationException ex;
        if (isResolved() || !(resolution instanceof NoSuchMethodError ||
                              resolution instanceof NoSuchFieldError))
            ex = new IllegalAccessException(message);
        else if (isConstructor())
            ex = new NoSuchMethodException(message);
        else if (isMethod())
            ex = new NoSuchMethodException(message);
        else
            ex = new NoSuchFieldException(message);
        if (resolution instanceof Throwable)
            ex.initCause((Throwable) resolution);
        return ex;
    }


     static Factory getFactory() {
        return Factory.INSTANCE;
    }

     static class Factory {
        private Factory() { } static Factory INSTANCE = new Factory();

        private static int ALLOWED_FLAGS = ALL_KINDS;

        List<MemberName> getMembers(Class<?> defc,
                String matchName, Object matchType,
                int matchFlags, Class<?> lookupClass) {
            matchFlags &= ALLOWED_FLAGS;
            String matchSig = null;
            if (matchType != null) {
                matchSig = BytecodeDescriptor.unparse(matchType);
                if (matchSig.startsWith("("))
                    matchFlags &= ~(ALL_KINDS & ~IS_INVOCABLE);
                else
                    matchFlags &= ~(ALL_KINDS & ~IS_FIELD);
            }
            final int BUF_MAX = 0x2000;
            int len1 = matchName == null ? 10 : matchType == null ? 4 : 1;
            MemberName[] buf = newMemberBuffer(len1);
            int totalCount = 0;
            ArrayList<MemberName[]> bufs = null;
            int bufCount = 0;
            for (;;) {
                bufCount = MethodHandleNatives.getMembers(defc,
                        matchName, matchSig, matchFlags,
                        lookupClass,
                        totalCount, buf);
                if (bufCount <= buf.length) {
                    if (bufCount < 0)  bufCount = 0;
                    totalCount += bufCount;
                    break;
                }
                totalCount += buf.length;
                int excess = bufCount - buf.length;
                if (bufs == null)  bufs = new ArrayList<>(1);
                bufs.add(buf);
                int len2 = buf.length;
                len2 = Math.max(len2, excess);
                len2 = Math.max(len2, totalCount / 4);
                buf = newMemberBuffer(Math.min(BUF_MAX, len2));
            }
            ArrayList<MemberName> result = new ArrayList<>(totalCount);
            if (bufs != null) {
                for (MemberName[] buf0 : bufs) {
                    Collections.addAll(result, buf0);
                }
            }
            result.addAll(Arrays.asList(buf).subList(0, bufCount));
            if (matchType != null && matchType != matchSig) {
                for (Iterator<MemberName> it = result.iterator(); it.hasNext();) {
                    MemberName m = it.next();
                    if (!matchType.equals(m.getType()))
                        it.remove();
                }
            }
            return result;
        }

        private MemberName resolve(byte refKind, MemberName ref, Class<?> lookupClass) {
            MemberName m = ref.clone();  assert(refKind == m.getReferenceKind());
            try {
                m = MethodHandleNatives.resolve(m, lookupClass);
                m.checkForTypeAlias(m.getDeclaringClass());
                m.resolution = null;
            } catch (ClassNotFoundException | LinkageError ex) {
                assert(!m.isResolved());
                m.resolution = ex;
                return m;
            }
            assert(m.referenceKindIsConsistent());
            m.initResolved(true);
            assert(m.vminfoIsConsistent());
            return m;
        }

        public
        <NoSuchMemberException extends ReflectiveOperationException>
        MemberName resolveOrFail(byte refKind, MemberName m, Class<?> lookupClass,
                                 Class<NoSuchMemberException> nsmClass)
                throws IllegalAccessException, NoSuchMemberException {
            MemberName result = resolve(refKind, m, lookupClass);
            if (result.isResolved())
                return result;
            ReflectiveOperationException ex = result.makeAccessException();
            if (ex instanceof IllegalAccessException)  throw (IllegalAccessException) ex;
            throw nsmClass.cast(ex);
        }

        public
        MemberName resolveOrNull(byte refKind, MemberName m, Class<?> lookupClass) {
            MemberName result = resolve(refKind, m, lookupClass);
            if (result.isResolved())
                return result;
            return null;
        }

        public List<MemberName> getMethods(Class<?> defc, boolean searchSupers,
                Class<?> lookupClass) {
            return getMethods(defc, searchSupers, null, null, lookupClass);
        }

        public List<MemberName> getMethods(Class<?> defc, boolean searchSupers,
                String name, MethodType type, Class<?> lookupClass) {
            int matchFlags = IS_METHOD | (searchSupers ? SEARCH_ALL_SUPERS : 0);
            return getMembers(defc, name, type, matchFlags, lookupClass);
        }

        public List<MemberName> getConstructors(Class<?> defc, Class<?> lookupClass) {
            return getMembers(defc, null, null, IS_CONSTRUCTOR, lookupClass);
        }

        public List<MemberName> getFields(Class<?> defc, boolean searchSupers,
                Class<?> lookupClass) {
            return getFields(defc, searchSupers, null, null, lookupClass);
        }

        public List<MemberName> getFields(Class<?> defc, boolean searchSupers,
                String name, Class<?> type, Class<?> lookupClass) {
            int matchFlags = IS_FIELD | (searchSupers ? SEARCH_ALL_SUPERS : 0);
            return getMembers(defc, name, type, matchFlags, lookupClass);
        }

        public List<MemberName> getNestedTypes(Class<?> defc, boolean searchSupers,
                Class<?> lookupClass) {
            int matchFlags = IS_TYPE | (searchSupers ? SEARCH_ALL_SUPERS : 0);
            return getMembers(defc, null, null, matchFlags, lookupClass);
        }
        private static MemberName[] newMemberBuffer(int length) {
            MemberName[] buf = new MemberName[length];
            for (int i = 0; i < length; i++)
                buf[i] = new MemberName();
            return buf;
        }
    }

}
