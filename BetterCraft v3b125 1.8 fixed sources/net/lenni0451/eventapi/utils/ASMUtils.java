/*
 * Decompiled with CFR 0.152.
 */
package net.lenni0451.eventapi.utils;

import java.lang.reflect.Method;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

public class ASMUtils {
    public static byte[] toBytes(ClassNode node) {
        ClassWriter classWriter = new ClassWriter(1);
        node.accept(classWriter);
        return classWriter.toByteArray();
    }

    public static ClassNode fromBytes(byte[] classBytes) {
        ClassReader classReader = new ClassReader(classBytes);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);
        return classNode;
    }

    public static Class<?> defineClass(ClassLoader classLoader, ClassNode classNode) throws Throwable {
        Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, Integer.TYPE, Integer.TYPE);
        defineClass.setAccessible(true);
        byte[] classBytes = ASMUtils.toBytes(classNode);
        return (Class)defineClass.invoke((Object)classLoader, classNode.name.replace("/", "."), classBytes, 0, classBytes.length);
    }
}

