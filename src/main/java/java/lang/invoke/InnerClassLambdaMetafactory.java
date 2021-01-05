

package java.lang.invoke;

import jdk.internal.org.objectweb.asm.*;
import sun.invoke.util.BytecodeDescriptor;
import sun.misc.Unsafe;
import sun.security.action.GetPropertyAction;

import java.io.FilePermission;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.LinkedHashSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.PropertyPermission;
import java.util.Set;

import static jdk.internal.org.objectweb.asm.Opcodes.*;


 final class InnerClassLambdaMetafactory extends AbstractValidatingLambdaMetafactory {
    private static final Unsafe UNSAFE = Unsafe.getUnsafe();

    private static final int CLASSFILE_VERSION = 52;
    private static final String METHOD_DESCRIPTOR_VOID = Type.getMethodDescriptor(Type.VOID_TYPE);
    private static final String JAVA_LANG_OBJECT = "java/lang/Object";
    private static final String NAME_CTOR = "<init>";
    private static final String NAME_FACTORY = "get$Lambda";

    private static final String NAME_SERIALIZED_LAMBDA = "java/lang/invoke/SerializedLambda";
    private static final String NAME_NOT_SERIALIZABLE_EXCEPTION = "java/io/NotSerializableException";
    private static final String DESCR_METHOD_WRITE_REPLACE = "()Ljava/lang/Object;";
    private static final String DESCR_METHOD_WRITE_OBJECT = "(Ljava/io/ObjectOutputStream;)V";
    private static final String DESCR_METHOD_READ_OBJECT = "(Ljava/io/ObjectInputStream;)V";
    private static final String NAME_METHOD_WRITE_REPLACE = "writeReplace";
    private static final String NAME_METHOD_READ_OBJECT = "readObject";
    private static final String NAME_METHOD_WRITE_OBJECT = "writeObject";
    private static final String DESCR_CTOR_SERIALIZED_LAMBDA
            = MethodType.methodType(void.class,
                                    Class.class,
                                    String.class, String.class, String.class,
                                    int.class, String.class, String.class, String.class,
                                    String.class,
                                    Object[].class).toMethodDescriptorString();
    private static final String DESCR_CTOR_NOT_SERIALIZABLE_EXCEPTION
            = MethodType.methodType(void.class, String.class).toMethodDescriptorString();
    private static final String[] SER_HOSTILE_EXCEPTIONS = new String[] {NAME_NOT_SERIALIZABLE_EXCEPTION};


    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private static final AtomicInteger counter = new AtomicInteger(0);

    private static final ProxyClassesDumper dumper;

    static {
        final String key = "jdk.internal.lambda.dumpProxyClasses";
        String path = AccessController.doPrivileged(
                new GetPropertyAction(key), null,
                new PropertyPermission(key , "read"));
        dumper = (null == path) ? null : ProxyClassesDumper.getInstance(path);
    }

    private final String implMethodClassName;        private final String implMethodName;             private final String implMethodDesc;             private final Class<?> implMethodReturnClass;    private final MethodType constructorType;        private final ClassWriter cw;                    private final String[] argNames;                 private final String[] argDescs;                 private final String lambdaClassName;            public InnerClassLambdaMetafactory(MethodHandles.Lookup caller,
                                       MethodType invokedType,
                                       String samMethodName,
                                       MethodType samMethodType,
                                       MethodHandle implMethod,
                                       MethodType instantiatedMethodType,
                                       boolean isSerializable,
                                       Class<?>[] markerInterfaces,
                                       MethodType[] additionalBridges)
            throws LambdaConversionException {
        super(caller, invokedType, samMethodName, samMethodType,
              implMethod, instantiatedMethodType,
              isSerializable, markerInterfaces, additionalBridges);
        implMethodClassName = implDefiningClass.getName().replace('.', '/');
        implMethodName = implInfo.getName();
        implMethodDesc = implMethodType.toMethodDescriptorString();
        implMethodReturnClass = (implKind == MethodHandleInfo.REF_newInvokeSpecial)
                ? implDefiningClass
                : implMethodType.returnType();
        constructorType = invokedType.changeReturnType(Void.TYPE);
        lambdaClassName = targetClass.getName().replace('.', '/') + "$$Lambda$" + counter.incrementAndGet();
        cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        int parameterCount = invokedType.parameterCount();
        if (parameterCount > 0) {
            argNames = new String[parameterCount];
            argDescs = new String[parameterCount];
            for (int i = 0; i < parameterCount; i++) {
                argNames[i] = "arg$" + (i + 1);
                argDescs[i] = BytecodeDescriptor.unparse(invokedType.parameterType(i));
            }
        } else {
            argNames = argDescs = EMPTY_STRING_ARRAY;
        }
    }


    @Override
    CallSite buildCallSite() throws LambdaConversionException {
        final Class<?> innerClass = spinInnerClass();
        if (invokedType.parameterCount() == 0) {
            final Constructor<?>[] ctrs = AccessController.doPrivileged(
                    new PrivilegedAction<Constructor<?>[]>() {
                @Override
                public Constructor<?>[] run() {
                    Constructor<?>[] ctrs = innerClass.getDeclaredConstructors();
                    if (ctrs.length == 1) {
                        ctrs[0].setAccessible(true);
                    }
                    return ctrs;
                }
                    });
            if (ctrs.length != 1) {
                throw new LambdaConversionException("Expected one lambda constructor for "
                        + innerClass.getCanonicalName() + ", got " + ctrs.length);
            }

            try {
                Object inst = ctrs[0].newInstance();
                return new ConstantCallSite(MethodHandles.constant(samBase, inst));
            }
            catch (ReflectiveOperationException e) {
                throw new LambdaConversionException("Exception instantiating lambda object", e);
            }
        } else {
            try {
                UNSAFE.ensureClassInitialized(innerClass);
                return new ConstantCallSite(
                        MethodHandles.Lookup.IMPL_LOOKUP
                             .findStatic(innerClass, NAME_FACTORY, invokedType));
            }
            catch (ReflectiveOperationException e) {
                throw new LambdaConversionException("Exception finding constructor", e);
            }
        }
    }


    private Class<?> spinInnerClass() throws LambdaConversionException {
        String[] interfaces;
        String samIntf = samBase.getName().replace('.', '/');
        boolean accidentallySerializable = !isSerializable && Serializable.class.isAssignableFrom(samBase);
        if (markerInterfaces.length == 0) {
            interfaces = new String[]{samIntf};
        } else {
            Set<String> itfs = new LinkedHashSet<>(markerInterfaces.length + 1);
            itfs.add(samIntf);
            for (Class<?> markerInterface : markerInterfaces) {
                itfs.add(markerInterface.getName().replace('.', '/'));
                accidentallySerializable |= !isSerializable && Serializable.class.isAssignableFrom(markerInterface);
            }
            interfaces = itfs.toArray(new String[itfs.size()]);
        }

        cw.visit(CLASSFILE_VERSION, ACC_SUPER + ACC_FINAL + ACC_SYNTHETIC,
                 lambdaClassName, null,
                 JAVA_LANG_OBJECT, interfaces);

        for (int i = 0; i < argDescs.length; i++) {
            FieldVisitor fv = cw.visitField(ACC_PRIVATE + ACC_FINAL,
                                            argNames[i],
                                            argDescs[i],
                                            null, null);
            fv.visitEnd();
        }

        generateConstructor();

        if (invokedType.parameterCount() != 0) {
            generateFactory();
        }

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, samMethodName,
                                          samMethodType.toMethodDescriptorString(), null, null);
        mv.visitAnnotation("Ljava/lang/invoke/LambdaForm$Hidden;", true);
        new ForwardingMethodGenerator(mv).generate(samMethodType);

        if (additionalBridges != null) {
            for (MethodType mt : additionalBridges) {
                mv = cw.visitMethod(ACC_PUBLIC|ACC_BRIDGE, samMethodName,
                                    mt.toMethodDescriptorString(), null, null);
                mv.visitAnnotation("Ljava/lang/invoke/LambdaForm$Hidden;", true);
                new ForwardingMethodGenerator(mv).generate(mt);
            }
        }

        if (isSerializable)
            generateSerializationFriendlyMethods();
        else if (accidentallySerializable)
            generateSerializationHostileMethods();

        cw.visitEnd();

        final byte[] classBytes = cw.toByteArray();

        if (dumper != null) {
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    dumper.dumpClass(lambdaClassName, classBytes);
                    return null;
                }
            }, null,
            new FilePermission("<<ALL FILES>>", "read, write"),
            new PropertyPermission("user.dir", "read"));
        }

        return UNSAFE.defineAnonymousClass(targetClass, classBytes, null);
    }


    private void generateFactory() {
        MethodVisitor m = cw.visitMethod(ACC_PRIVATE | ACC_STATIC, NAME_FACTORY, invokedType.toMethodDescriptorString(), null, null);
        m.visitCode();
        m.visitTypeInsn(NEW, lambdaClassName);
        m.visitInsn(Opcodes.DUP);
        int parameterCount = invokedType.parameterCount();
        for (int typeIndex = 0, varIndex = 0; typeIndex < parameterCount; typeIndex++) {
            Class<?> argType = invokedType.parameterType(typeIndex);
            m.visitVarInsn(getLoadOpcode(argType), varIndex);
            varIndex += getParameterSize(argType);
        }
        m.visitMethodInsn(INVOKESPECIAL, lambdaClassName, NAME_CTOR, constructorType.toMethodDescriptorString(), false);
        m.visitInsn(ARETURN);
        m.visitMaxs(-1, -1);
        m.visitEnd();
    }


    private void generateConstructor() {
        MethodVisitor ctor = cw.visitMethod(ACC_PRIVATE, NAME_CTOR,
                                            constructorType.toMethodDescriptorString(), null, null);
        ctor.visitCode();
        ctor.visitVarInsn(ALOAD, 0);
        ctor.visitMethodInsn(INVOKESPECIAL, JAVA_LANG_OBJECT, NAME_CTOR,
                             METHOD_DESCRIPTOR_VOID, false);
        int parameterCount = invokedType.parameterCount();
        for (int i = 0, lvIndex = 0; i < parameterCount; i++) {
            ctor.visitVarInsn(ALOAD, 0);
            Class<?> argType = invokedType.parameterType(i);
            ctor.visitVarInsn(getLoadOpcode(argType), lvIndex + 1);
            lvIndex += getParameterSize(argType);
            ctor.visitFieldInsn(PUTFIELD, lambdaClassName, argNames[i], argDescs[i]);
        }
        ctor.visitInsn(RETURN);
        ctor.visitMaxs(-1, -1);
        ctor.visitEnd();
    }


    private void generateSerializationFriendlyMethods() {
        TypeConvertingMethodAdapter mv
                = new TypeConvertingMethodAdapter(
                    cw.visitMethod(ACC_PRIVATE + ACC_FINAL,
                    NAME_METHOD_WRITE_REPLACE, DESCR_METHOD_WRITE_REPLACE,
                    null, null));

        mv.visitCode();
        mv.visitTypeInsn(NEW, NAME_SERIALIZED_LAMBDA);
        mv.visitInsn(DUP);
        mv.visitLdcInsn(Type.getType(targetClass));
        mv.visitLdcInsn(invokedType.returnType().getName().replace('.', '/'));
        mv.visitLdcInsn(samMethodName);
        mv.visitLdcInsn(samMethodType.toMethodDescriptorString());
        mv.visitLdcInsn(implInfo.getReferenceKind());
        mv.visitLdcInsn(implInfo.getDeclaringClass().getName().replace('.', '/'));
        mv.visitLdcInsn(implInfo.getName());
        mv.visitLdcInsn(implInfo.getMethodType().toMethodDescriptorString());
        mv.visitLdcInsn(instantiatedMethodType.toMethodDescriptorString());
        mv.iconst(argDescs.length);
        mv.visitTypeInsn(ANEWARRAY, JAVA_LANG_OBJECT);
        for (int i = 0; i < argDescs.length; i++) {
            mv.visitInsn(DUP);
            mv.iconst(i);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, lambdaClassName, argNames[i], argDescs[i]);
            mv.boxIfTypePrimitive(Type.getType(argDescs[i]));
            mv.visitInsn(AASTORE);
        }
        mv.visitMethodInsn(INVOKESPECIAL, NAME_SERIALIZED_LAMBDA, NAME_CTOR,
                DESCR_CTOR_SERIALIZED_LAMBDA, false);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();
    }


    private void generateSerializationHostileMethods() {
        MethodVisitor mv = cw.visitMethod(ACC_PRIVATE + ACC_FINAL,
                                          NAME_METHOD_WRITE_OBJECT, DESCR_METHOD_WRITE_OBJECT,
                                          null, SER_HOSTILE_EXCEPTIONS);
        mv.visitCode();
        mv.visitTypeInsn(NEW, NAME_NOT_SERIALIZABLE_EXCEPTION);
        mv.visitInsn(DUP);
        mv.visitLdcInsn("Non-serializable lambda");
        mv.visitMethodInsn(INVOKESPECIAL, NAME_NOT_SERIALIZABLE_EXCEPTION, NAME_CTOR,
                           DESCR_CTOR_NOT_SERIALIZABLE_EXCEPTION, false);
        mv.visitInsn(ATHROW);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();

        mv = cw.visitMethod(ACC_PRIVATE + ACC_FINAL,
                            NAME_METHOD_READ_OBJECT, DESCR_METHOD_READ_OBJECT,
                            null, SER_HOSTILE_EXCEPTIONS);
        mv.visitCode();
        mv.visitTypeInsn(NEW, NAME_NOT_SERIALIZABLE_EXCEPTION);
        mv.visitInsn(DUP);
        mv.visitLdcInsn("Non-serializable lambda");
        mv.visitMethodInsn(INVOKESPECIAL, NAME_NOT_SERIALIZABLE_EXCEPTION, NAME_CTOR,
                           DESCR_CTOR_NOT_SERIALIZABLE_EXCEPTION, false);
        mv.visitInsn(ATHROW);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();
    }


    private class ForwardingMethodGenerator extends TypeConvertingMethodAdapter {

        ForwardingMethodGenerator(MethodVisitor mv) {
            super(mv);
        }

        void generate(MethodType methodType) {
            visitCode();

            if (implKind == MethodHandleInfo.REF_newInvokeSpecial) {
                visitTypeInsn(NEW, implMethodClassName);
                visitInsn(DUP);
            }
            for (int i = 0; i < argNames.length; i++) {
                visitVarInsn(ALOAD, 0);
                visitFieldInsn(GETFIELD, lambdaClassName, argNames[i], argDescs[i]);
            }

            convertArgumentTypes(methodType);

            visitMethodInsn(invocationOpcode(), implMethodClassName,
                            implMethodName, implMethodDesc,
                            implDefiningClass.isInterface());

            Class<?> samReturnClass = methodType.returnType();
            convertType(implMethodReturnClass, samReturnClass, samReturnClass);
            visitInsn(getReturnOpcode(samReturnClass));
            visitMaxs(-1, -1);
            visitEnd();
        }

        private void convertArgumentTypes(MethodType samType) {
            int lvIndex = 0;
            boolean samIncludesReceiver = implIsInstanceMethod &&
                                                   invokedType.parameterCount() == 0;
            int samReceiverLength = samIncludesReceiver ? 1 : 0;
            if (samIncludesReceiver) {
                Class<?> rcvrType = samType.parameterType(0);
                visitVarInsn(getLoadOpcode(rcvrType), lvIndex + 1);
                lvIndex += getParameterSize(rcvrType);
                convertType(rcvrType, implDefiningClass, instantiatedMethodType.parameterType(0));
            }
            int samParametersLength = samType.parameterCount();
            int argOffset = implMethodType.parameterCount() - samParametersLength;
            for (int i = samReceiverLength; i < samParametersLength; i++) {
                Class<?> argType = samType.parameterType(i);
                visitVarInsn(getLoadOpcode(argType), lvIndex + 1);
                lvIndex += getParameterSize(argType);
                convertType(argType, implMethodType.parameterType(argOffset + i), instantiatedMethodType.parameterType(i));
            }
        }

        private int invocationOpcode() throws InternalError {
            switch (implKind) {
                case MethodHandleInfo.REF_invokeStatic:
                    return INVOKESTATIC;
                case MethodHandleInfo.REF_newInvokeSpecial:
                    return INVOKESPECIAL;
                 case MethodHandleInfo.REF_invokeVirtual:
                    return INVOKEVIRTUAL;
                case MethodHandleInfo.REF_invokeInterface:
                    return INVOKEINTERFACE;
                case MethodHandleInfo.REF_invokeSpecial:
                    return INVOKESPECIAL;
                default:
                    throw new InternalError("Unexpected invocation kind: " + implKind);
            }
        }
    }

    static int getParameterSize(Class<?> c) {
        if (c == Void.TYPE) {
            return 0;
        } else if (c == Long.TYPE || c == Double.TYPE) {
            return 2;
        }
        return 1;
    }

    static int getLoadOpcode(Class<?> c) {
        if(c == Void.TYPE) {
            throw new InternalError("Unexpected void type of load opcode");
        }
        return ILOAD + getOpcodeOffset(c);
    }

    static int getReturnOpcode(Class<?> c) {
        if(c == Void.TYPE) {
            return RETURN;
        }
        return IRETURN + getOpcodeOffset(c);
    }

    private static int getOpcodeOffset(Class<?> c) {
        if (c.isPrimitive()) {
            if (c == Long.TYPE) {
                return 1;
            } else if (c == Float.TYPE) {
                return 2;
            } else if (c == Double.TYPE) {
                return 3;
            }
            return 0;
        } else {
            return 4;
        }
    }

}
