

package java.lang.invoke;

import sun.invoke.util.Wrapper;
import java.lang.ref.SoftReference;
import static java.lang.invoke.MethodHandleStatics.*;


final class MethodTypeForm {
    final int[] argToSlotTable, slotToArgTable;
    final long argCounts;               final long primCounts;              final MethodType erasedType;        final MethodType basicType;         @Stable final SoftReference<MethodHandle>[] methodHandles;
    static final int
            MH_BASIC_INV      =  0,  MH_NF_INV         =  1,  MH_UNINIT_CS      =  2,  MH_LIMIT          =  3;

    final @Stable SoftReference<LambdaForm>[] lambdaForms;
    static final int
            LF_INVVIRTUAL              =  0,  LF_INVSTATIC               =  1,
            LF_INVSPECIAL              =  2,
            LF_NEWINVSPECIAL           =  3,
            LF_INVINTERFACE            =  4,
            LF_INVSTATIC_INIT          =  5,  LF_INTERPRET               =  6,  LF_REBIND                  =  7,  LF_DELEGATE                =  8,  LF_DELEGATE_BLOCK_INLINING =  9,  LF_EX_LINKER               = 10,  LF_EX_INVOKER              = 11,  LF_GEN_LINKER              = 12,  LF_GEN_INVOKER             = 13,  LF_CS_LINKER               = 14,  LF_MH_LINKER               = 15,  LF_GWC                     = 16,  LF_GWT                     = 17,  LF_LIMIT                   = 18;


    public MethodType erasedType() {
        return erasedType;
    }


    public MethodType basicType() {
        return basicType;
    }

    private boolean assertIsBasicType() {
        assert(erasedType == basicType)
                : "erasedType: " + erasedType + " != basicType: " + basicType;
        return true;
    }

    public MethodHandle cachedMethodHandle(int which) {
        assert(assertIsBasicType());
        SoftReference<MethodHandle> entry = methodHandles[which];
        return (entry != null) ? entry.get() : null;
    }

    synchronized public MethodHandle setCachedMethodHandle(int which, MethodHandle mh) {
        SoftReference<MethodHandle> entry = methodHandles[which];
        if (entry != null) {
            MethodHandle prev = entry.get();
            if (prev != null) {
                return prev;
            }
        }
        methodHandles[which] = new SoftReference<>(mh);
        return mh;
    }

    public LambdaForm cachedLambdaForm(int which) {
        assert(assertIsBasicType());
        SoftReference<LambdaForm> entry = lambdaForms[which];
        return (entry != null) ? entry.get() : null;
    }

    synchronized public LambdaForm setCachedLambdaForm(int which, LambdaForm form) {
        SoftReference<LambdaForm> entry = lambdaForms[which];
        if (entry != null) {
            LambdaForm prev = entry.get();
            if (prev != null) {
                return prev;
            }
        }
        lambdaForms[which] = new SoftReference<>(form);
        return form;
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    protected MethodTypeForm(MethodType erasedType) {
        this.erasedType = erasedType;

        Class<?>[] ptypes = erasedType.ptypes();
        int ptypeCount = ptypes.length;
        int pslotCount = ptypeCount;            int rtypeCount = 1;                     int rslotCount = 1;                     int[] argToSlotTab = null, slotToArgTab = null;

        int pac = 0, lac = 0, prc = 0, lrc = 0;
        Class<?>[] epts = ptypes;
        Class<?>[] bpts = epts;
        for (int i = 0; i < epts.length; i++) {
            Class<?> pt = epts[i];
            if (pt != Object.class) {
                ++pac;
                Wrapper w = Wrapper.forPrimitiveType(pt);
                if (w.isDoubleWord())  ++lac;
                if (w.isSubwordOrInt() && pt != int.class) {
                    if (bpts == epts)
                        bpts = bpts.clone();
                    bpts[i] = int.class;
                }
            }
        }
        pslotCount += lac;                  Class<?> rt = erasedType.returnType();
        Class<?> bt = rt;
        if (rt != Object.class) {
            ++prc;          Wrapper w = Wrapper.forPrimitiveType(rt);
            if (w.isDoubleWord())  ++lrc;
            if (w.isSubwordOrInt() && rt != int.class)
                bt = int.class;
            if (rt == void.class)
                rtypeCount = rslotCount = 0;
            else
                rslotCount += lrc;
        }
        if (epts == bpts && bt == rt) {
            this.basicType = erasedType;
        } else {
            this.basicType = MethodType.makeImpl(bt, bpts, true);
            MethodTypeForm that = this.basicType.form();
            assert(this != that);
            this.primCounts = that.primCounts;
            this.argCounts = that.argCounts;
            this.argToSlotTable = that.argToSlotTable;
            this.slotToArgTable = that.slotToArgTable;
            this.methodHandles = null;
            this.lambdaForms = null;
            return;
        }
        if (lac != 0) {
            int slot = ptypeCount + lac;
            slotToArgTab = new int[slot+1];
            argToSlotTab = new int[1+ptypeCount];
            argToSlotTab[0] = slot;  for (int i = 0; i < epts.length; i++) {
                Class<?> pt = epts[i];
                Wrapper w = Wrapper.forBasicType(pt);
                if (w.isDoubleWord())  --slot;
                --slot;
                slotToArgTab[slot] = i+1; argToSlotTab[1+i]  = slot;
            }
            assert(slot == 0);  } else if (pac != 0) {
            assert(ptypeCount == pslotCount);
            MethodTypeForm that = MethodType.genericMethodType(ptypeCount).form();
            assert(this != that);
            slotToArgTab = that.slotToArgTable;
            argToSlotTab = that.argToSlotTable;
        } else {
            int slot = ptypeCount; slotToArgTab = new int[slot+1];
            argToSlotTab = new int[1+ptypeCount];
            argToSlotTab[0] = slot;  for (int i = 0; i < ptypeCount; i++) {
                --slot;
                slotToArgTab[slot] = i+1; argToSlotTab[1+i]  = slot;
            }
        }
        this.primCounts = pack(lrc, prc, lac, pac);
        this.argCounts = pack(rslotCount, rtypeCount, pslotCount, ptypeCount);
        this.argToSlotTable = argToSlotTab;
        this.slotToArgTable = slotToArgTab;

        if (pslotCount >= 256)  throw newIllegalArgumentException("too many arguments");

        assert(basicType == erasedType);
        this.lambdaForms   = new SoftReference[LF_LIMIT];
        this.methodHandles = new SoftReference[MH_LIMIT];
    }

    private static long pack(int a, int b, int c, int d) {
        assert(((a|b|c|d) & ~0xFFFF) == 0);
        long hw = ((a << 16) | b), lw = ((c << 16) | d);
        return (hw << 32) | lw;
    }
    private static char unpack(long packed, int word) { assert(word <= 3);
        return (char)(packed >> ((3-word) * 16));
    }

    public int parameterCount() {                      return unpack(argCounts, 3);
    }
    public int parameterSlotCount() {                  return unpack(argCounts, 2);
    }
    public int returnCount() {                         return unpack(argCounts, 1);
    }
    public int returnSlotCount() {                     return unpack(argCounts, 0);
    }
    public int primitiveParameterCount() {
        return unpack(primCounts, 3);
    }
    public int longPrimitiveParameterCount() {
        return unpack(primCounts, 2);
    }
    public int primitiveReturnCount() {                return unpack(primCounts, 1);
    }
    public int longPrimitiveReturnCount() {            return unpack(primCounts, 0);
    }
    public boolean hasPrimitives() {
        return primCounts != 0;
    }
    public boolean hasNonVoidPrimitives() {
        if (primCounts == 0)  return false;
        if (primitiveParameterCount() != 0)  return true;
        return (primitiveReturnCount() != 0 && returnCount() != 0);
    }
    public boolean hasLongPrimitives() {
        return (longPrimitiveParameterCount() | longPrimitiveReturnCount()) != 0;
    }
    public int parameterToArgSlot(int i) {
        return argToSlotTable[1+i];
    }
    public int argSlotToParameter(int argSlot) {
        return slotToArgTable[argSlot] - 1;
    }

    static MethodTypeForm findForm(MethodType mt) {
        MethodType erased = canonicalize(mt, ERASE, ERASE);
        if (erased == null) {
            return new MethodTypeForm(mt);
        } else {
            return erased.form();
        }
    }


    public static final int NO_CHANGE = 0, ERASE = 1, WRAP = 2, UNWRAP = 3, INTS = 4, LONGS = 5, RAW_RETURN = 6;


    public static MethodType canonicalize(MethodType mt, int howRet, int howArgs) {
        Class<?>[] ptypes = mt.ptypes();
        Class<?>[] ptc = MethodTypeForm.canonicalizeAll(ptypes, howArgs);
        Class<?> rtype = mt.returnType();
        Class<?> rtc = MethodTypeForm.canonicalize(rtype, howRet);
        if (ptc == null && rtc == null) {
            return null;
        }
        if (rtc == null)  rtc = rtype;
        if (ptc == null)  ptc = ptypes;
        return MethodType.makeImpl(rtc, ptc, true);
    }


    static Class<?> canonicalize(Class<?> t, int how) {
        Class<?> ct;
        if (t == Object.class) {
            } else if (!t.isPrimitive()) {
            switch (how) {
                case UNWRAP:
                    ct = Wrapper.asPrimitiveType(t);
                    if (ct != t)  return ct;
                    break;
                case RAW_RETURN:
                case ERASE:
                    return Object.class;
            }
        } else if (t == void.class) {
            switch (how) {
                case RAW_RETURN:
                    return int.class;
                case WRAP:
                    return Void.class;
            }
        } else {
            switch (how) {
                case WRAP:
                    return Wrapper.asWrapperType(t);
                case INTS:
                    if (t == int.class || t == long.class)
                        return null;  if (t == double.class)
                        return long.class;
                    return int.class;
                case LONGS:
                    if (t == long.class)
                        return null;  return long.class;
                case RAW_RETURN:
                    if (t == int.class || t == long.class ||
                        t == float.class || t == double.class)
                        return null;  return int.class;
            }
        }
        return null;
    }


    static Class<?>[] canonicalizeAll(Class<?>[] ts, int how) {
        Class<?>[] cs = null;
        for (int imax = ts.length, i = 0; i < imax; i++) {
            Class<?> c = canonicalize(ts[i], how);
            if (c == void.class)
                c = null;  if (c != null) {
                if (cs == null)
                    cs = ts.clone();
                cs[i] = c;
            }
        }
        return cs;
    }

    @Override
    public String toString() {
        return "Form"+erasedType;
    }
}
