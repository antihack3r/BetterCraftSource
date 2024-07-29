/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.tools.agent;

import java.util.HashMap;
import java.util.Map;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.service.MixinService;

class MixinAgentClassLoader
extends ClassLoader {
    private static final ILogger logger = MixinService.getService().getLogger("mixin.agent");
    private Map<Class<?>, byte[]> mixins = new HashMap();
    private Map<String, byte[]> targets = new HashMap<String, byte[]>();

    MixinAgentClassLoader() {
    }

    void addMixinClass(String name) {
        logger.debug("Mixin class {} added to class loader", name);
        try {
            byte[] bytes = this.materialise(name);
            Class<?> clazz = this.defineClass(name, bytes, 0, bytes.length);
            clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            this.mixins.put(clazz, bytes);
        }
        catch (Throwable e2) {
            logger.catching(e2);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void addTargetClass(String name, ClassNode classNode) {
        Map<String, byte[]> map = this.targets;
        synchronized (map) {
            if (this.targets.containsKey(name)) {
                return;
            }
            try {
                ClassWriter cw2 = new ClassWriter(0);
                classNode.accept(cw2);
                this.targets.put(name, cw2.toByteArray());
            }
            catch (Exception ex2) {
                logger.error("Error storing original class bytecode for {} in mixin hotswap agent. {}: {}", name, ex2.getClass().getName(), ex2.getMessage());
                logger.debug(ex2.toString(), new Object[0]);
            }
        }
    }

    byte[] getFakeMixinBytecode(Class<?> clazz) {
        return this.mixins.get(clazz);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    byte[] getOriginalTargetBytecode(String name) {
        Map<String, byte[]> map = this.targets;
        synchronized (map) {
            return this.targets.get(name);
        }
    }

    private byte[] materialise(String name) {
        ClassWriter cw2 = new ClassWriter(3);
        cw2.visit(MixinEnvironment.getCompatibilityLevel().getClassVersion(), 1, name.replace('.', '/'), null, Type.getInternalName(Object.class), null);
        MethodVisitor mv2 = cw2.visitMethod(1, "<init>", "()V", null, null);
        mv2.visitCode();
        mv2.visitVarInsn(25, 0);
        mv2.visitMethodInsn(183, Type.getInternalName(Object.class), "<init>", "()V", false);
        mv2.visitInsn(177);
        mv2.visitMaxs(1, 1);
        mv2.visitEnd();
        cw2.visitEnd();
        return cw2.toByteArray();
    }
}

