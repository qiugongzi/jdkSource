

package java.lang.invoke;

import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.Type;
import sun.invoke.util.BytecodeDescriptor;
import sun.invoke.util.Wrapper;
import static sun.invoke.util.Wrapper.*;

class TypeConvertingMethodAdapter extends MethodVisitor {

    TypeConvertingMethodAdapter(MethodVisitor mv) {
        super(Opcodes.ASM5, mv);
    }

    private static final int NUM_WRAPPERS = Wrapper.values().length;

    private static final String NAME_OBJECT = "java/lang/Object";
    private static final String WRAPPER_PREFIX = "Ljava/lang/";

    private static final String NAME_BOX_METHOD = "valueOf";

    private static final int[][] wideningOpcodes = new int[NUM_WRAPPERS][NUM_WRAPPERS];

    private static final Wrapper[] FROM_WRAPPER_NAME = new Wrapper[16];

    private static final Wrapper[] FROM_TYPE_SORT = new Wrapper[16];

    static {
        for (Wrapper w : Wrapper.values()) {
            if (w.basicTypeChar() != 'L') {
                int wi = hashWrapperName(w.wrapperSimpleName());
                assert (FROM_WRAPPER_NAME[wi] == null);
                FROM_WRAPPER_NAME[wi] = w;
            }
        }

        for (int i = 0; i < NUM_WRAPPERS; i++) {
            for (int j = 0; j < NUM_WRAPPERS; j++) {
                wideningOpcodes[i][j] = Opcodes.NOP;
            }
        }

        initWidening(LONG,   Opcodes.I2L, BYTE, SHORT, INT, CHAR);
        initWidening(LONG,   Opcodes.F2L, FLOAT);
        initWidening(FLOAT,  Opcodes.I2F, BYTE, SHORT, INT, CHAR);
        initWidening(FLOAT,  Opcodes.L2F, LONG);
        initWidening(DOUBLE, Opcodes.I2D, BYTE, SHORT, INT, CHAR);
        initWidening(DOUBLE, Opcodes.F2D, FLOAT);
        initWidening(DOUBLE, Opcodes.L2D, LONG);

        FROM_TYPE_SORT[Type.BYTE] = Wrapper.BYTE;
        FROM_TYPE_SORT[Type.SHORT] = Wrapper.SHORT;
        FROM_TYPE_SORT[Type.INT] = Wrapper.INT;
        FROM_TYPE_SORT[Type.LONG] = Wrapper.LONG;
        FROM_TYPE_SORT[Type.CHAR] = Wrapper.CHAR;
        FROM_TYPE_SORT[Type.FLOAT] = Wrapper.FLOAT;
        FROM_TYPE_SORT[Type.DOUBLE] = Wrapper.DOUBLE;
        FROM_TYPE_SORT[Type.BOOLEAN] = Wrapper.BOOLEAN;
    }

    private static void initWidening(Wrapper to, int opcode, Wrapper... from) {
        for (Wrapper f : from) {
            wideningOpcodes[f.ordinal()][to.ordinal()] = opcode;
        }
    }


    private static int hashWrapperName(String xn) {
        if (xn.length() < 3) {
            return 0;
        }
        return (3 * xn.charAt(1) + xn.charAt(2)) % 16;
    }

    private Wrapper wrapperOrNullFromDescriptor(String desc) {
        if (!desc.startsWith(WRAPPER_PREFIX)) {
            return null;
        }
        String cname = desc.substring(WRAPPER_PREFIX.length(), desc.length() - 1);
        Wrapper w = FROM_WRAPPER_NAME[hashWrapperName(cname)];
        if (w == null || w.wrapperSimpleName().equals(cname)) {
            return w;
        } else {
            return null;
        }
    }

    private static String wrapperName(Wrapper w) {
        return "java/lang/" + w.wrapperSimpleName();
    }

    private static String unboxMethod(Wrapper w) {
        return w.primitiveSimpleName() + "Value";
    }

    private static String boxingDescriptor(Wrapper w) {
        return String.format("(%s)L%s;", w.basicTypeChar(), wrapperName(w));
    }

    private static String unboxingDescriptor(Wrapper w) {
        return "()" + w.basicTypeChar();
    }

    void boxIfTypePrimitive(Type t) {
        Wrapper w = FROM_TYPE_SORT[t.getSort()];
        if (w != null) {
            box(w);
        }
    }

    void widen(Wrapper ws, Wrapper wt) {
        if (ws != wt) {
            int opcode = wideningOpcodes[ws.ordinal()][wt.ordinal()];
            if (opcode != Opcodes.NOP) {
                visitInsn(opcode);
            }
        }
    }

    void box(Wrapper w) {
        visitMethodInsn(Opcodes.INVOKESTATIC,
                wrapperName(w),
                NAME_BOX_METHOD,
                boxingDescriptor(w), false);
    }


    void unbox(String sname, Wrapper wt) {
        visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                sname,
                unboxMethod(wt),
                unboxingDescriptor(wt), false);
    }

    private String descriptorToName(String desc) {
        int last = desc.length() - 1;
        if (desc.charAt(0) == 'L' && desc.charAt(last) == ';') {
            return desc.substring(1, last);
        } else {
            return desc;
        }
    }

    void cast(String ds, String dt) {
        String ns = descriptorToName(ds);
        String nt = descriptorToName(dt);
        if (!nt.equals(ns) && !nt.equals(NAME_OBJECT)) {
            visitTypeInsn(Opcodes.CHECKCAST, nt);
        }
    }

    private boolean isPrimitive(Wrapper w) {
        return w != OBJECT;
    }

    private Wrapper toWrapper(String desc) {
        char first = desc.charAt(0);
        if (first == '[' || first == '(') {
            first = 'L';
        }
        return Wrapper.forBasicType(first);
    }


    void convertType(Class<?> arg, Class<?> target, Class<?> functional) {
        if (arg.equals(target) && arg.equals(functional)) {
            return;
        }
        if (arg == Void.TYPE || target == Void.TYPE) {
            return;
        }
        if (arg.isPrimitive()) {
            Wrapper wArg = Wrapper.forPrimitiveType(arg);
            if (target.isPrimitive()) {
                widen(wArg, Wrapper.forPrimitiveType(target));
            } else {
                String dTarget = BytecodeDescriptor.unparse(target);
                Wrapper wPrimTarget = wrapperOrNullFromDescriptor(dTarget);
                if (wPrimTarget != null) {
                    widen(wArg, wPrimTarget);
                    box(wPrimTarget);
                } else {
                    box(wArg);
                    cast(wrapperName(wArg), dTarget);
                }
            }
        } else {
            String dArg = BytecodeDescriptor.unparse(arg);
            String dSrc;
            if (functional.isPrimitive()) {
                dSrc = dArg;
            } else {
                dSrc = BytecodeDescriptor.unparse(functional);
                cast(dArg, dSrc);
            }
            String dTarget = BytecodeDescriptor.unparse(target);
            if (target.isPrimitive()) {
                Wrapper wTarget = toWrapper(dTarget);
                Wrapper wps = wrapperOrNullFromDescriptor(dSrc);
                if (wps != null) {
                    if (wps.isSigned() || wps.isFloating()) {
                        unbox(wrapperName(wps), wTarget);
                    } else {
                        unbox(wrapperName(wps), wps);
                        widen(wps, wTarget);
                    }
                } else {
                    String intermediate;
                    if (wTarget.isSigned() || wTarget.isFloating()) {
                        intermediate = "java/lang/Number";
                    } else {
                        intermediate = wrapperName(wTarget);
                    }
                    cast(dSrc, intermediate);
                    unbox(intermediate, wTarget);
                }
            } else {
                cast(dSrc, dTarget);
            }
        }
    }


    void iconst(final int cst) {
        if (cst >= -1 && cst <= 5) {
            mv.visitInsn(Opcodes.ICONST_0 + cst);
        } else if (cst >= Byte.MIN_VALUE && cst <= Byte.MAX_VALUE) {
            mv.visitIntInsn(Opcodes.BIPUSH, cst);
        } else if (cst >= Short.MIN_VALUE && cst <= Short.MAX_VALUE) {
            mv.visitIntInsn(Opcodes.SIPUSH, cst);
        } else {
            mv.visitLdcInsn(cst);
        }
    }
}
