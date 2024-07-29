/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.mixin.transformer;

import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.struct.MemberRef;
import org.spongepowered.asm.mixin.transformer.ClassInfo;

abstract class ClassContext {
    private final Set<ClassInfo.Method> upgradedMethods = new HashSet<ClassInfo.Method>();

    ClassContext() {
    }

    abstract String getClassRef();

    abstract ClassNode getClassNode();

    abstract ClassInfo getClassInfo();

    void addUpgradedMethod(MethodNode method) {
        ClassInfo.Method md2 = this.getClassInfo().findMethod(method);
        if (md2 == null) {
            throw new IllegalStateException("Meta method for " + method.name + " not located in " + this);
        }
        this.upgradedMethods.add(md2);
    }

    protected void upgradeMethods() {
        for (MethodNode method : this.getClassNode().methods) {
            this.upgradeMethod(method);
        }
    }

    private void upgradeMethod(MethodNode method) {
        ListIterator<AbstractInsnNode> iter = method.instructions.iterator();
        while (iter.hasNext()) {
            MemberRef.Method methodRef;
            AbstractInsnNode insn = (AbstractInsnNode)iter.next();
            if (!(insn instanceof MethodInsnNode) || !((MemberRef)(methodRef = new MemberRef.Method((MethodInsnNode)insn))).getOwner().equals(this.getClassRef())) continue;
            ClassInfo.Method md2 = this.getClassInfo().findMethod(((MemberRef)methodRef).getName(), ((MemberRef)methodRef).getDesc(), 10);
            this.upgradeMethodRef(method, methodRef, md2);
        }
    }

    protected void upgradeMethodRef(MethodNode containingMethod, MemberRef methodRef, ClassInfo.Method method) {
        if (methodRef.getOpcode() != 183) {
            return;
        }
        if (this.upgradedMethods.contains(method)) {
            methodRef.setOpcode(182);
        }
    }
}

