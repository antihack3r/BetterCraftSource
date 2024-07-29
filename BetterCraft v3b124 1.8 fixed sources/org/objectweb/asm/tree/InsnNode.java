/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.tree;

import java.util.Map;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;

public class InsnNode
extends AbstractInsnNode {
    public InsnNode(int opcode) {
        super(opcode);
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public void accept(MethodVisitor methodVisitor) {
        methodVisitor.visitInsn(this.opcode);
        this.acceptAnnotations(methodVisitor);
    }

    @Override
    public AbstractInsnNode clone(Map<LabelNode, LabelNode> clonedLabels) {
        return new InsnNode(this.opcode).cloneAnnotations(this);
    }
}

