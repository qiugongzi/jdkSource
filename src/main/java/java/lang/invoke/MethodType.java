

package java.lang.invoke;

import sun.invoke.util.Wrapper;
import java.lang.ref.WeakReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import sun.invoke.util.BytecodeDescriptor;
import static java.lang.invoke.MethodHandleStatics.*;
import sun.invoke.util.VerifyType;


public final
class MethodType implements java.io.Serializable {
    private static final long serialVersionUID = 292L;  private final Class<?>   rtype;
    private final Class<?>[] ptypes;

    private @Stable MethodTypeForm form; private @Stable MethodType wrapAlt;  private @Stable Invokers invokers;   private @Stable String methodDescriptor;  private MethodType(Class<?> rtype, Class<?>[] ptypes, boolean trusted) {
        checkRtype(rtype);
        checkPtypes(ptypes);
        this.rtype = rtype;
        this.ptypes = trusted ? ptypes : Arrays.copyOf(ptypes, ptypes.length);
    }


    private MethodType(Class<?>[] ptypes, Class<?> rtype) {
        this.rtype = rtype;
        this.ptypes = ptypes;
    }

     MethodTypeForm form() { return form; }
     Class<?> rtype() { return rtype; }
     Class<?>[] ptypes() { return ptypes; }

    void setForm(MethodTypeForm f) { form = f; }


     static final int MAX_JVM_ARITY = 255;  static final int MAX_MH_ARITY = MAX_JVM_ARITY-1;  static final int MAX_MH_INVOKER_ARITY = MAX_MH_ARITY-1;  private static void checkRtype(Class<?> rtype) {
        Objects.requireNonNull(rtype);
    }
    private static void checkPtype(Class<?> ptype) {
        Objects.requireNonNull(ptype);
        if (ptype == void.class)
            throw newIllegalArgumentException("parameter type cannot be void");
    }

    private static int checkPtypes(Class<?>[] ptypes) {
        int slots = 0;
        for (Class<?> ptype : ptypes) {
            checkPtype(ptype);
            if (ptype == double.class || ptype == long.class) {
                slots++;
            }
        }
        checkSlotCount(ptypes.length + slots);
        return slots;
    }
    static void checkSlotCount(int count) {
        assert((MAX_JVM_ARITY & (MAX_JVM_ARITY+1)) == 0);
        if ((count & MAX_JVM_ARITY) != count)
            throw newIllegalArgumentException("bad parameter count "+count);
    }
    private static IndexOutOfBoundsException newIndexOutOfBoundsException(Object num) {
        if (num instanceof Integer)  num = "bad index: "+num;
        return new IndexOutOfBoundsException(num.toString());
    }

    static final ConcurrentWeakInternSet<MethodType> internTable = new ConcurrentWeakInternSet<>();

    static final Class<?>[] NO_PTYPES = {};


    public static
    MethodType methodType(Class<?> rtype, Class<?>[] ptypes) {
        return makeImpl(rtype, ptypes, false);
    }


    public static
    MethodType methodType(Class<?> rtype, List<Class<?>> ptypes) {
        boolean notrust = false;  return makeImpl(rtype, listToArray(ptypes), notrust);
    }

    private static Class<?>[] listToArray(List<Class<?>> ptypes) {
        checkSlotCount(ptypes.size());
        return ptypes.toArray(NO_PTYPES);
    }


    public static
    MethodType methodType(Class<?> rtype, Class<?> ptype0, Class<?>... ptypes) {
        Class<?>[] ptypes1 = new Class<?>[1+ptypes.length];
        ptypes1[0] = ptype0;
        System.arraycopy(ptypes, 0, ptypes1, 1, ptypes.length);
        return makeImpl(rtype, ptypes1, true);
    }


    public static
    MethodType methodType(Class<?> rtype) {
        return makeImpl(rtype, NO_PTYPES, true);
    }


    public static
    MethodType methodType(Class<?> rtype, Class<?> ptype0) {
        return makeImpl(rtype, new Class<?>[]{ ptype0 }, true);
    }


    public static
    MethodType methodType(Class<?> rtype, MethodType ptypes) {
        return makeImpl(rtype, ptypes.ptypes, true);
    }


     static
    MethodType makeImpl(Class<?> rtype, Class<?>[] ptypes, boolean trusted) {
        MethodType mt = internTable.get(new MethodType(ptypes, rtype));
        if (mt != null)
            return mt;
        if (ptypes.length == 0) {
            ptypes = NO_PTYPES; trusted = true;
        }
        mt = new MethodType(rtype, ptypes, trusted);
        mt.form = MethodTypeForm.findForm(mt);
        return internTable.add(mt);
    }
    private static final MethodType[] objectOnlyTypes = new MethodType[20];


    public static
    MethodType genericMethodType(int objectArgCount, boolean finalArray) {
        MethodType mt;
        checkSlotCount(objectArgCount);
        int ivarargs = (!finalArray ? 0 : 1);
        int ootIndex = objectArgCount*2 + ivarargs;
        if (ootIndex < objectOnlyTypes.length) {
            mt = objectOnlyTypes[ootIndex];
            if (mt != null)  return mt;
        }
        Class<?>[] ptypes = new Class<?>[objectArgCount + ivarargs];
        Arrays.fill(ptypes, Object.class);
        if (ivarargs != 0)  ptypes[objectArgCount] = Object[].class;
        mt = makeImpl(Object.class, ptypes, true);
        if (ootIndex < objectOnlyTypes.length) {
            objectOnlyTypes[ootIndex] = mt;     }
        return mt;
    }


    public static
    MethodType genericMethodType(int objectArgCount) {
        return genericMethodType(objectArgCount, false);
    }


    public MethodType changeParameterType(int num, Class<?> nptype) {
        if (parameterType(num) == nptype)  return this;
        checkPtype(nptype);
        Class<?>[] nptypes = ptypes.clone();
        nptypes[num] = nptype;
        return makeImpl(rtype, nptypes, true);
    }


    public MethodType insertParameterTypes(int num, Class<?>... ptypesToInsert) {
        int len = ptypes.length;
        if (num < 0 || num > len)
            throw newIndexOutOfBoundsException(num);
        int ins = checkPtypes(ptypesToInsert);
        checkSlotCount(parameterSlotCount() + ptypesToInsert.length + ins);
        int ilen = ptypesToInsert.length;
        if (ilen == 0)  return this;
        Class<?>[] nptypes = Arrays.copyOfRange(ptypes, 0, len+ilen);
        System.arraycopy(nptypes, num, nptypes, num+ilen, len-num);
        System.arraycopy(ptypesToInsert, 0, nptypes, num, ilen);
        return makeImpl(rtype, nptypes, true);
    }


    public MethodType appendParameterTypes(Class<?>... ptypesToInsert) {
        return insertParameterTypes(parameterCount(), ptypesToInsert);
    }


    public MethodType insertParameterTypes(int num, List<Class<?>> ptypesToInsert) {
        return insertParameterTypes(num, listToArray(ptypesToInsert));
    }


    public MethodType appendParameterTypes(List<Class<?>> ptypesToInsert) {
        return insertParameterTypes(parameterCount(), ptypesToInsert);
    }


     MethodType replaceParameterTypes(int start, int end, Class<?>... ptypesToInsert) {
        if (start == end)
            return insertParameterTypes(start, ptypesToInsert);
        int len = ptypes.length;
        if (!(0 <= start && start <= end && end <= len))
            throw newIndexOutOfBoundsException("start="+start+" end="+end);
        int ilen = ptypesToInsert.length;
        if (ilen == 0)
            return dropParameterTypes(start, end);
        return dropParameterTypes(start, end).insertParameterTypes(start, ptypesToInsert);
    }


     MethodType asSpreaderType(Class<?> arrayType, int arrayLength) {
        assert(parameterCount() >= arrayLength);
        int spreadPos = ptypes.length - arrayLength;
        if (arrayLength == 0)  return this;  if (arrayType == Object[].class) {
            if (isGeneric())  return this;  if (spreadPos == 0) {
                MethodType res = genericMethodType(arrayLength);
                if (rtype != Object.class) {
                    res = res.changeReturnType(rtype);
                }
                return res;
            }
        }
        Class<?> elemType = arrayType.getComponentType();
        assert(elemType != null);
        for (int i = spreadPos; i < ptypes.length; i++) {
            if (ptypes[i] != elemType) {
                Class<?>[] fixedPtypes = ptypes.clone();
                Arrays.fill(fixedPtypes, i, ptypes.length, elemType);
                return methodType(rtype, fixedPtypes);
            }
        }
        return this;  }


     Class<?> leadingReferenceParameter() {
        Class<?> ptype;
        if (ptypes.length == 0 ||
            (ptype = ptypes[0]).isPrimitive())
            throw newIllegalArgumentException("no leading reference parameter");
        return ptype;
    }


     MethodType asCollectorType(Class<?> arrayType, int arrayLength) {
        assert(parameterCount() >= 1);
        assert(lastParameterType().isAssignableFrom(arrayType));
        MethodType res;
        if (arrayType == Object[].class) {
            res = genericMethodType(arrayLength);
            if (rtype != Object.class) {
                res = res.changeReturnType(rtype);
            }
        } else {
            Class<?> elemType = arrayType.getComponentType();
            assert(elemType != null);
            res = methodType(rtype, Collections.nCopies(arrayLength, elemType));
        }
        if (ptypes.length == 1) {
            return res;
        } else {
            return res.insertParameterTypes(0, parameterList().subList(0, ptypes.length-1));
        }
    }


    public MethodType dropParameterTypes(int start, int end) {
        int len = ptypes.length;
        if (!(0 <= start && start <= end && end <= len))
            throw newIndexOutOfBoundsException("start="+start+" end="+end);
        if (start == end)  return this;
        Class<?>[] nptypes;
        if (start == 0) {
            if (end == len) {
                nptypes = NO_PTYPES;
            } else {
                nptypes = Arrays.copyOfRange(ptypes, end, len);
            }
        } else {
            if (end == len) {
                nptypes = Arrays.copyOfRange(ptypes, 0, start);
            } else {
                int tail = len - end;
                nptypes = Arrays.copyOfRange(ptypes, 0, start + tail);
                System.arraycopy(ptypes, end, nptypes, start, tail);
            }
        }
        return makeImpl(rtype, nptypes, true);
    }


    public MethodType changeReturnType(Class<?> nrtype) {
        if (returnType() == nrtype)  return this;
        return makeImpl(nrtype, ptypes, true);
    }


    public boolean hasPrimitives() {
        return form.hasPrimitives();
    }


    public boolean hasWrappers() {
        return unwrap() != this;
    }


    public MethodType erase() {
        return form.erasedType();
    }


     MethodType basicType() {
        return form.basicType();
    }


     MethodType invokerType() {
        return insertParameterTypes(0, MethodHandle.class);
    }


    public MethodType generic() {
        return genericMethodType(parameterCount());
    }

     boolean isGeneric() {
        return this == erase() && !hasPrimitives();
    }


    public MethodType wrap() {
        return hasPrimitives() ? wrapWithPrims(this) : this;
    }


    public MethodType unwrap() {
        MethodType noprims = !hasPrimitives() ? this : wrapWithPrims(this);
        return unwrapWithNoPrims(noprims);
    }

    private static MethodType wrapWithPrims(MethodType pt) {
        assert(pt.hasPrimitives());
        MethodType wt = pt.wrapAlt;
        if (wt == null) {
            wt = MethodTypeForm.canonicalize(pt, MethodTypeForm.WRAP, MethodTypeForm.WRAP);
            assert(wt != null);
            pt.wrapAlt = wt;
        }
        return wt;
    }

    private static MethodType unwrapWithNoPrims(MethodType wt) {
        assert(!wt.hasPrimitives());
        MethodType uwt = wt.wrapAlt;
        if (uwt == null) {
            uwt = MethodTypeForm.canonicalize(wt, MethodTypeForm.UNWRAP, MethodTypeForm.UNWRAP);
            if (uwt == null)
                uwt = wt;    wt.wrapAlt = uwt;
        }
        return uwt;
    }


    public Class<?> parameterType(int num) {
        return ptypes[num];
    }

    public int parameterCount() {
        return ptypes.length;
    }

    public Class<?> returnType() {
        return rtype;
    }


    public List<Class<?>> parameterList() {
        return Collections.unmodifiableList(Arrays.asList(ptypes.clone()));
    }

     Class<?> lastParameterType() {
        int len = ptypes.length;
        return len == 0 ? void.class : ptypes[len-1];
    }


    public Class<?>[] parameterArray() {
        return ptypes.clone();
    }


    @Override
    public boolean equals(Object x) {
        return this == x || x instanceof MethodType && equals((MethodType)x);
    }

    private boolean equals(MethodType that) {
        return this.rtype == that.rtype
            && Arrays.equals(this.ptypes, that.ptypes);
    }


    @Override
    public int hashCode() {
      int hashCode = 31 + rtype.hashCode();
      for (Class<?> ptype : ptypes)
          hashCode = 31*hashCode + ptype.hashCode();
      return hashCode;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (int i = 0; i < ptypes.length; i++) {
            if (i > 0)  sb.append(",");
            sb.append(ptypes[i].getSimpleName());
        }
        sb.append(")");
        sb.append(rtype.getSimpleName());
        return sb.toString();
    }



    boolean isViewableAs(MethodType newType, boolean keepInterfaces) {
        if (!VerifyType.isNullConversion(returnType(), newType.returnType(), keepInterfaces))
            return false;
        return parametersAreViewableAs(newType, keepInterfaces);
    }


    boolean parametersAreViewableAs(MethodType newType, boolean keepInterfaces) {
        if (form == newType.form && form.erasedType == this)
            return true;  if (ptypes == newType.ptypes)
            return true;
        int argc = parameterCount();
        if (argc != newType.parameterCount())
            return false;
        for (int i = 0; i < argc; i++) {
            if (!VerifyType.isNullConversion(newType.parameterType(i), parameterType(i), keepInterfaces))
                return false;
        }
        return true;
    }

    boolean isConvertibleTo(MethodType newType) {
        MethodTypeForm oldForm = this.form();
        MethodTypeForm newForm = newType.form();
        if (oldForm == newForm)
            return true;
        if (!canConvert(returnType(), newType.returnType()))
            return false;
        Class<?>[] srcTypes = newType.ptypes;
        Class<?>[] dstTypes = ptypes;
        if (srcTypes == dstTypes)
            return true;
        int argc;
        if ((argc = srcTypes.length) != dstTypes.length)
            return false;
        if (argc <= 1) {
            if (argc == 1 && !canConvert(srcTypes[0], dstTypes[0]))
                return false;
            return true;
        }
        if ((oldForm.primitiveParameterCount() == 0 && oldForm.erasedType == this) ||
            (newForm.primitiveParameterCount() == 0 && newForm.erasedType == newType)) {
            assert(canConvertParameters(srcTypes, dstTypes));
            return true;
        }
        return canConvertParameters(srcTypes, dstTypes);
    }



    boolean explicitCastEquivalentToAsType(MethodType newType) {
        if (this == newType)  return true;
        if (!explicitCastEquivalentToAsType(rtype, newType.rtype)) {
            return false;
        }
        Class<?>[] srcTypes = newType.ptypes;
        Class<?>[] dstTypes = ptypes;
        if (dstTypes == srcTypes) {
            return true;
        }
        assert(dstTypes.length == srcTypes.length);
        for (int i = 0; i < dstTypes.length; i++) {
            if (!explicitCastEquivalentToAsType(srcTypes[i], dstTypes[i])) {
                return false;
            }
        }
        return true;
    }


    private static boolean explicitCastEquivalentToAsType(Class<?> src, Class<?> dst) {
        if (src == dst || dst == Object.class || dst == void.class)  return true;
        if (src.isPrimitive()) {
            return canConvert(src, dst);
        } else if (dst.isPrimitive()) {
            return false;
        } else {
            return !dst.isInterface() || dst.isAssignableFrom(src);
        }
    }

    private boolean canConvertParameters(Class<?>[] srcTypes, Class<?>[] dstTypes) {
        for (int i = 0; i < srcTypes.length; i++) {
            if (!canConvert(srcTypes[i], dstTypes[i])) {
                return false;
            }
        }
        return true;
    }


    static boolean canConvert(Class<?> src, Class<?> dst) {
        if (src == dst || src == Object.class || dst == Object.class)  return true;
        if (src.isPrimitive()) {
            if (src == void.class)  return true;  Wrapper sw = Wrapper.forPrimitiveType(src);
            if (dst.isPrimitive()) {
                return Wrapper.forPrimitiveType(dst).isConvertibleFrom(sw);
            } else {
                return dst.isAssignableFrom(sw.wrapperType());
            }
        } else if (dst.isPrimitive()) {
            if (dst == void.class)  return true;
            Wrapper dw = Wrapper.forPrimitiveType(dst);
            if (src.isAssignableFrom(dw.wrapperType())) {
                return true;
            }
            if (Wrapper.isWrapperType(src) &&
                dw.isConvertibleFrom(Wrapper.forWrapperType(src))) {
                return true;
            }
            return false;
        } else {
            return true;
        }
    }

    int parameterSlotCount() {
        return form.parameterSlotCount();
    }

     Invokers invokers() {
        Invokers inv = invokers;
        if (inv != null)  return inv;
        invokers = inv = new Invokers(this);
        return inv;
    }


     int parameterSlotDepth(int num) {
        if (num < 0 || num > ptypes.length)
            parameterType(num);  return form.parameterToArgSlot(num-1);
    }


     int returnSlotCount() {
        return form.returnSlotCount();
    }


    public static MethodType fromMethodDescriptorString(String descriptor, ClassLoader loader)
        throws IllegalArgumentException, TypeNotPresentException
    {
        if (!descriptor.startsWith("(") ||  descriptor.indexOf(')') < 0 ||
            descriptor.indexOf('.') >= 0)
            throw newIllegalArgumentException("not a method descriptor: "+descriptor);
        List<Class<?>> types = BytecodeDescriptor.parseMethod(descriptor, loader);
        Class<?> rtype = types.remove(types.size() - 1);
        checkSlotCount(types.size());
        Class<?>[] ptypes = listToArray(types);
        return makeImpl(rtype, ptypes, true);
    }


    public String toMethodDescriptorString() {
        String desc = methodDescriptor;
        if (desc == null) {
            desc = BytecodeDescriptor.unparse(this);
            methodDescriptor = desc;
        }
        return desc;
    }

     static String toFieldDescriptorString(Class<?> cls) {
        return BytecodeDescriptor.unparse(cls);
    }

    private static final java.io.ObjectStreamField[] serialPersistentFields = { };


    private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
        s.defaultWriteObject();  s.writeObject(returnType());
        s.writeObject(parameterArray());
    }


    private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
        MethodType_init(void.class, NO_PTYPES);

        s.defaultReadObject();  Class<?>   returnType     = (Class<?>)   s.readObject();
        Class<?>[] parameterArray = (Class<?>[]) s.readObject();
        parameterArray = parameterArray.clone();  MethodType_init(returnType, parameterArray);
    }

    private void MethodType_init(Class<?> rtype, Class<?>[] ptypes) {
        checkRtype(rtype);
        checkPtypes(ptypes);
        UNSAFE.putObject(this, rtypeOffset, rtype);
        UNSAFE.putObject(this, ptypesOffset, ptypes);
    }

    private static final long rtypeOffset, ptypesOffset;
    static {
        try {
            rtypeOffset = UNSAFE.objectFieldOffset
                (MethodType.class.getDeclaredField("rtype"));
            ptypesOffset = UNSAFE.objectFieldOffset
                (MethodType.class.getDeclaredField("ptypes"));
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }


    private Object readResolve() {
        try {
            return methodType(rtype, ptypes);
        } finally {
            MethodType_init(void.class, NO_PTYPES);
        }
    }


    private static class ConcurrentWeakInternSet<T> {

        private final ConcurrentMap<WeakEntry<T>, WeakEntry<T>> map;
        private final ReferenceQueue<T> stale;

        public ConcurrentWeakInternSet() {
            this.map = new ConcurrentHashMap<>();
            this.stale = new ReferenceQueue<>();
        }


        public T get(T elem) {
            if (elem == null) throw new NullPointerException();
            expungeStaleElements();

            WeakEntry<T> value = map.get(new WeakEntry<>(elem));
            if (value != null) {
                T res = value.get();
                if (res != null) {
                    return res;
                }
            }
            return null;
        }


        public T add(T elem) {
            if (elem == null) throw new NullPointerException();

            T interned;
            WeakEntry<T> e = new WeakEntry<>(elem, stale);
            do {
                expungeStaleElements();
                WeakEntry<T> exist = map.putIfAbsent(e, e);
                interned = (exist == null) ? elem : exist.get();
            } while (interned == null);
            return interned;
        }

        private void expungeStaleElements() {
            Reference<? extends T> reference;
            while ((reference = stale.poll()) != null) {
                map.remove(reference);
            }
        }

        private static class WeakEntry<T> extends WeakReference<T> {

            public final int hashcode;

            public WeakEntry(T key, ReferenceQueue<T> queue) {
                super(key, queue);
                hashcode = key.hashCode();
            }

            public WeakEntry(T key) {
                super(key);
                hashcode = key.hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof WeakEntry) {
                    Object that = ((WeakEntry) obj).get();
                    Object mine = get();
                    return (that == null || mine == null) ? (this == obj) : mine.equals(that);
                }
                return false;
            }

            @Override
            public int hashCode() {
                return hashcode;
            }

        }
    }

}
