

package java.lang.invoke;


import java.util.*;

import static java.lang.invoke.MethodHandleStatics.*;


public abstract class MethodHandle {
    static { MethodHandleImpl.initStatics(); }


    @java.lang.annotation.Target({java.lang.annotation.ElementType.METHOD})
    @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @interface PolymorphicSignature { }

    private final MethodType type;
     final LambdaForm form;
    MethodHandle asTypeCache;
    byte customizationCount;
    public MethodType type() {
        return type;
    }


    MethodHandle(MethodType type, LambdaForm form) {
        type.getClass();  form.getClass();  this.type = type;
        this.form = form.uncustomize();

        this.form.prepare();  }


    public final native @PolymorphicSignature Object invokeExact(Object... args) throws Throwable;


    public final native @PolymorphicSignature Object invoke(Object... args) throws Throwable;


     final native @PolymorphicSignature Object invokeBasic(Object... args) throws Throwable;


     static native @PolymorphicSignature Object linkToVirtual(Object... args) throws Throwable;


     static native @PolymorphicSignature Object linkToStatic(Object... args) throws Throwable;


     static native @PolymorphicSignature Object linkToSpecial(Object... args) throws Throwable;


     static native @PolymorphicSignature Object linkToInterface(Object... args) throws Throwable;


    public Object invokeWithArguments(Object... arguments) throws Throwable {
        MethodType invocationType = MethodType.genericMethodType(arguments == null ? 0 : arguments.length);
        return invocationType.invokers().spreadInvoker(0).invokeExact(asType(invocationType), arguments);
    }


    public Object invokeWithArguments(java.util.List<?> arguments) throws Throwable {
        return invokeWithArguments(arguments.toArray());
    }


    public MethodHandle asType(MethodType newType) {
        if (newType == type) {
            return this;
        }
        MethodHandle atc = asTypeCached(newType);
        if (atc != null) {
            return atc;
        }
        return asTypeUncached(newType);
    }

    private MethodHandle asTypeCached(MethodType newType) {
        MethodHandle atc = asTypeCache;
        if (atc != null && newType == atc.type) {
            return atc;
        }
        return null;
    }


     MethodHandle asTypeUncached(MethodType newType) {
        if (!type.isConvertibleTo(newType))
            throw new WrongMethodTypeException("cannot convert "+this+" to "+newType);
        return asTypeCache = MethodHandleImpl.makePairwiseConvert(this, newType, true);
    }


    public MethodHandle asSpreader(Class<?> arrayType, int arrayLength) {
        MethodType postSpreadType = asSpreaderChecks(arrayType, arrayLength);
        int arity = type().parameterCount();
        int spreadArgPos = arity - arrayLength;
        MethodHandle afterSpread = this.asType(postSpreadType);
        BoundMethodHandle mh = afterSpread.rebind();
        LambdaForm lform = mh.editor().spreadArgumentsForm(1 + spreadArgPos, arrayType, arrayLength);
        MethodType preSpreadType = postSpreadType.replaceParameterTypes(spreadArgPos, arity, arrayType);
        return mh.copyWith(preSpreadType, lform);
    }


    private MethodType asSpreaderChecks(Class<?> arrayType, int arrayLength) {
        spreadArrayChecks(arrayType, arrayLength);
        int nargs = type().parameterCount();
        if (nargs < arrayLength || arrayLength < 0)
            throw newIllegalArgumentException("bad spread array length");
        Class<?> arrayElement = arrayType.getComponentType();
        MethodType mtype = type();
        boolean match = true, fail = false;
        for (int i = nargs - arrayLength; i < nargs; i++) {
            Class<?> ptype = mtype.parameterType(i);
            if (ptype != arrayElement) {
                match = false;
                if (!MethodType.canConvert(arrayElement, ptype)) {
                    fail = true;
                    break;
                }
            }
        }
        if (match)  return mtype;
        MethodType needType = mtype.asSpreaderType(arrayType, arrayLength);
        if (!fail)  return needType;
        this.asType(needType);
        throw newInternalError("should not return", null);
    }

    private void spreadArrayChecks(Class<?> arrayType, int arrayLength) {
        Class<?> arrayElement = arrayType.getComponentType();
        if (arrayElement == null)
            throw newIllegalArgumentException("not an array type", arrayType);
        if ((arrayLength & 0x7F) != arrayLength) {
            if ((arrayLength & 0xFF) != arrayLength)
                throw newIllegalArgumentException("array length is not legal", arrayLength);
            assert(arrayLength >= 128);
            if (arrayElement == long.class ||
                arrayElement == double.class)
                throw newIllegalArgumentException("array length is not legal for long[] or double[]", arrayLength);
        }
    }


    public MethodHandle asCollector(Class<?> arrayType, int arrayLength) {
        asCollectorChecks(arrayType, arrayLength);
        int collectArgPos = type().parameterCount() - 1;
        BoundMethodHandle mh = rebind();
        MethodType resultType = type().asCollectorType(arrayType, arrayLength);
        MethodHandle newArray = MethodHandleImpl.varargsArray(arrayType, arrayLength);
        LambdaForm lform = mh.editor().collectArgumentArrayForm(1 + collectArgPos, newArray);
        if (lform != null) {
            return mh.copyWith(resultType, lform);
        }
        lform = mh.editor().collectArgumentsForm(1 + collectArgPos, newArray.type().basicType());
        return mh.copyWithExtendL(resultType, lform, newArray);
    }


     boolean asCollectorChecks(Class<?> arrayType, int arrayLength) {
        spreadArrayChecks(arrayType, arrayLength);
        int nargs = type().parameterCount();
        if (nargs != 0) {
            Class<?> lastParam = type().parameterType(nargs-1);
            if (lastParam == arrayType)  return true;
            if (lastParam.isAssignableFrom(arrayType))  return false;
        }
        throw newIllegalArgumentException("array type not assignable to trailing argument", this, arrayType);
    }


    public MethodHandle asVarargsCollector(Class<?> arrayType) {
        arrayType.getClass(); boolean lastMatch = asCollectorChecks(arrayType, 0);
        if (isVarargsCollector() && lastMatch)
            return this;
        return MethodHandleImpl.makeVarargsCollector(this, arrayType);
    }


    public boolean isVarargsCollector() {
        return false;
    }


    public MethodHandle asFixedArity() {
        assert(!isVarargsCollector());
        return this;
    }


    public MethodHandle bindTo(Object x) {
        x = type.leadingReferenceParameter().cast(x);  return bindArgumentL(0, x);
    }


    @Override
    public String toString() {
        if (DEBUG_METHOD_HANDLE_NAMES)  return "MethodHandle"+debugString();
        return standardString();
    }
    String standardString() {
        return "MethodHandle"+type;
    }

    String debugString() {
        return type+" : "+internalForm()+internalProperties();
    }

    BoundMethodHandle bindArgumentL(int pos, Object value) {
        return rebind().bindArgumentL(pos, value);
    }


    MethodHandle setVarargs(MemberName member) throws IllegalAccessException {
        if (!member.isVarargs())  return this;
        Class<?> arrayType = type().lastParameterType();
        if (arrayType.isArray()) {
            return MethodHandleImpl.makeVarargsCollector(this, arrayType);
        }
        throw member.makeAccessException("cannot make variable arity", null);
    }


    MethodHandle viewAsType(MethodType newType, boolean strict) {
        assert viewAsTypeChecks(newType, strict);
        BoundMethodHandle mh = rebind();
        assert(!((MethodHandle)mh instanceof DirectMethodHandle));
        return mh.copyWith(newType, mh.form);
    }


    boolean viewAsTypeChecks(MethodType newType, boolean strict) {
        if (strict) {
            assert(type().isViewableAs(newType, true))
                : Arrays.asList(this, newType);
        } else {
            assert(type().basicType().isViewableAs(newType.basicType(), true))
                : Arrays.asList(this, newType);
        }
        return true;
    }

    LambdaForm internalForm() {
        return form;
    }


    MemberName internalMemberName() {
        return null;  }


    Class<?> internalCallerClass() {
        return null;  }


    MethodHandleImpl.Intrinsic intrinsicName() {
        return MethodHandleImpl.Intrinsic.NONE;
    }


    MethodHandle withInternalMemberName(MemberName member, boolean isInvokeSpecial) {
        if (member != null) {
            return MethodHandleImpl.makeWrappedMember(this, member, isInvokeSpecial);
        } else if (internalMemberName() == null) {
            return this;
        } else {
            MethodHandle result = rebind();
            assert (result.internalMemberName() == null);
            return result;
        }
    }


    boolean isInvokeSpecial() {
        return false;  }


    Object internalValues() {
        return null;
    }


    Object internalProperties() {
        return "";
    }

    abstract MethodHandle copyWith(MethodType mt, LambdaForm lf);


    abstract BoundMethodHandle rebind();



    void updateForm(LambdaForm newForm) {
        assert(newForm.customized == null || newForm.customized == this);
        if (form == newForm)  return;
        newForm.prepare();  UNSAFE.putObject(this, FORM_OFFSET, newForm);
        UNSAFE.fullFence();
    }



    void customize() {
        if (form.customized == null) {
            LambdaForm newForm = form.customize(this);
            updateForm(newForm);
        } else {
            assert(form.customized == this);
        }
    }

    private static final long FORM_OFFSET;
    static {
        try {
            FORM_OFFSET = UNSAFE.objectFieldOffset(MethodHandle.class.getDeclaredField("form"));
        } catch (ReflectiveOperationException ex) {
            throw newInternalError(ex);
        }
    }
}
