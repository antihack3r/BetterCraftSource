/*
 * Decompiled with CFR 0.152.
 */
package javassist.util.proxy;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.security.ProtectionDomain;
import javassist.CannotCompileException;
import javassist.bytecode.ClassFile;
import javassist.util.proxy.DefineClassHelper;
import javassist.util.proxy.ProxyFactory;

public class FactoryHelper {
    public static final Class<?>[] primitiveTypes = new Class[]{Boolean.TYPE, Byte.TYPE, Character.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Void.TYPE};
    public static final String[] wrapperTypes = new String[]{"java.lang.Boolean", "java.lang.Byte", "java.lang.Character", "java.lang.Short", "java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double", "java.lang.Void"};
    public static final String[] wrapperDesc = new String[]{"(Z)V", "(B)V", "(C)V", "(S)V", "(I)V", "(J)V", "(F)V", "(D)V"};
    public static final String[] unwarpMethods = new String[]{"booleanValue", "byteValue", "charValue", "shortValue", "intValue", "longValue", "floatValue", "doubleValue"};
    public static final String[] unwrapDesc = new String[]{"()Z", "()B", "()C", "()S", "()I", "()J", "()F", "()D"};
    public static final int[] dataSize = new int[]{1, 1, 1, 1, 1, 2, 1, 2};

    public static final int typeIndex(Class<?> type) {
        for (int i2 = 0; i2 < primitiveTypes.length; ++i2) {
            if (primitiveTypes[i2] != type) continue;
            return i2;
        }
        throw new RuntimeException("bad type:" + type.getName());
    }

    public static Class<?> toClass(ClassFile cf2, ClassLoader loader) throws CannotCompileException {
        return FactoryHelper.toClass(cf2, null, loader, null);
    }

    public static Class<?> toClass(ClassFile cf2, ClassLoader loader, ProtectionDomain domain) throws CannotCompileException {
        return FactoryHelper.toClass(cf2, null, loader, domain);
    }

    public static Class<?> toClass(ClassFile cf2, Class<?> neighbor, ClassLoader loader, ProtectionDomain domain) throws CannotCompileException {
        try {
            byte[] b2 = FactoryHelper.toBytecode(cf2);
            if (ProxyFactory.onlyPublicMethods) {
                return DefineClassHelper.toPublicClass(cf2.getName(), b2);
            }
            return DefineClassHelper.toClass(cf2.getName(), neighbor, loader, domain, b2);
        }
        catch (IOException e2) {
            throw new CannotCompileException(e2);
        }
    }

    public static Class<?> toClass(ClassFile cf2, MethodHandles.Lookup lookup) throws CannotCompileException {
        try {
            byte[] b2 = FactoryHelper.toBytecode(cf2);
            return DefineClassHelper.toClass(lookup, b2);
        }
        catch (IOException e2) {
            throw new CannotCompileException(e2);
        }
    }

    private static byte[] toBytecode(ClassFile cf2) throws IOException {
        ByteArrayOutputStream barray = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(barray);){
            cf2.write(out);
        }
        return barray.toByteArray();
    }

    public static void writeFile(ClassFile cf2, String directoryName) throws CannotCompileException {
        try {
            FactoryHelper.writeFile0(cf2, directoryName);
        }
        catch (IOException e2) {
            throw new CannotCompileException(e2);
        }
    }

    private static void writeFile0(ClassFile cf2, String directoryName) throws CannotCompileException, IOException {
        String dir;
        String classname = cf2.getName();
        String filename = directoryName + File.separatorChar + classname.replace('.', File.separatorChar) + ".class";
        int pos = filename.lastIndexOf(File.separatorChar);
        if (pos > 0 && !(dir = filename.substring(0, pos)).equals(".")) {
            new File(dir).mkdirs();
        }
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));){
            cf2.write(out);
        }
    }
}

