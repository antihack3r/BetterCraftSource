/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.tree;

import java.util.Map;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;

public class MethodInsnNode
extends AbstractInsnNode {
    public String owner;
    public String name;
    public String desc;
    public boolean itf;

    @Deprecated
    public MethodInsnNode(int opcode, String owner, String name, String descriptor) {
        this(opcode, owner, name, descriptor, opcode == 185);
    }

    public MethodInsnNode(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        super(opcode);
        this.owner = owner;
        this.name = name;
        this.desc = descriptor;
        this.itf = isInterface;
    }

    public void setOpcode(int opcode) {
        this.opcode = opcode;
    }

    @Override
    public int getType() {
        return 5;
    }

    @Override
    public void accept(MethodVisitor methodVisitor) {
        methodVisitor.visitMethodInsn(this.opcode, this.owner, this.name, this.desc, this.itf);
        this.acceptAnnotations(methodVisitor);
    }

    @Override
    public AbstractInsnNode clone(Map<LabelNode, LabelNode> clonedLabels) {
        return new MethodInsnNode(this.opcode, this.owner, this.name, this.desc, this.itf).cloneAnnotations(this);
    }
}

