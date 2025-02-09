/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.tree;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.TypeAnnotationNode;

public abstract class AbstractInsnNode {
    public static final int INSN = 0;
    public static final int INT_INSN = 1;
    public static final int VAR_INSN = 2;
    public static final int TYPE_INSN = 3;
    public static final int FIELD_INSN = 4;
    public static final int METHOD_INSN = 5;
    public static final int INVOKE_DYNAMIC_INSN = 6;
    public static final int JUMP_INSN = 7;
    public static final int LABEL = 8;
    public static final int LDC_INSN = 9;
    public static final int IINC_INSN = 10;
    public static final int TABLESWITCH_INSN = 11;
    public static final int LOOKUPSWITCH_INSN = 12;
    public static final int MULTIANEWARRAY_INSN = 13;
    public static final int FRAME = 14;
    public static final int LINE = 15;
    public List<TypeAnnotationNode> visibleTypeAnnotations;
    public List<TypeAnnotationNode> invisibleTypeAnnotations;
    protected int opcode;
    AbstractInsnNode previousInsn;
    AbstractInsnNode nextInsn;
    int index;

    protected AbstractInsnNode(int opcode) {
        this.opcode = opcode;
        this.index = -1;
    }

    static LabelNode clone(LabelNode label, Map<LabelNode, LabelNode> clonedLabels) {
        return clonedLabels.get(label);
    }

    static LabelNode[] clone(List<LabelNode> labels, Map<LabelNode, LabelNode> clonedLabels) {
        LabelNode[] clones = new LabelNode[labels.size()];
        int i2 = 0;
        int n2 = clones.length;
        while (i2 < n2) {
            clones[i2] = clonedLabels.get(labels.get(i2));
            ++i2;
        }
        return clones;
    }

    public int getOpcode() {
        return this.opcode;
    }

    public abstract int getType();

    public AbstractInsnNode getPrevious() {
        return this.previousInsn;
    }

    public AbstractInsnNode getNext() {
        return this.nextInsn;
    }

    public abstract void accept(MethodVisitor var1);

    protected final void acceptAnnotations(MethodVisitor methodVisitor) {
        TypeAnnotationNode typeAnnotation;
        int n2;
        int i2;
        if (this.visibleTypeAnnotations != null) {
            i2 = 0;
            n2 = this.visibleTypeAnnotations.size();
            while (i2 < n2) {
                typeAnnotation = this.visibleTypeAnnotations.get(i2);
                typeAnnotation.accept(methodVisitor.visitInsnAnnotation(typeAnnotation.typeRef, typeAnnotation.typePath, typeAnnotation.desc, true));
                ++i2;
            }
        }
        if (this.invisibleTypeAnnotations != null) {
            i2 = 0;
            n2 = this.invisibleTypeAnnotations.size();
            while (i2 < n2) {
                typeAnnotation = this.invisibleTypeAnnotations.get(i2);
                typeAnnotation.accept(methodVisitor.visitInsnAnnotation(typeAnnotation.typeRef, typeAnnotation.typePath, typeAnnotation.desc, false));
                ++i2;
            }
        }
    }

    public abstract AbstractInsnNode clone(Map<LabelNode, LabelNode> var1);

    protected final AbstractInsnNode cloneAnnotations(AbstractInsnNode insnNode) {
        TypeAnnotationNode cloneAnnotation;
        TypeAnnotationNode sourceAnnotation;
        int n2;
        int i2;
        if (insnNode.visibleTypeAnnotations != null) {
            this.visibleTypeAnnotations = Collections.synchronizedList(new CopyOnWriteArrayList());
            i2 = 0;
            n2 = insnNode.visibleTypeAnnotations.size();
            while (i2 < n2) {
                sourceAnnotation = insnNode.visibleTypeAnnotations.get(i2);
                cloneAnnotation = new TypeAnnotationNode(sourceAnnotation.typeRef, sourceAnnotation.typePath, sourceAnnotation.desc);
                sourceAnnotation.accept(cloneAnnotation);
                this.visibleTypeAnnotations.add(cloneAnnotation);
                ++i2;
            }
        }
        if (insnNode.invisibleTypeAnnotations != null) {
            this.invisibleTypeAnnotations = Collections.synchronizedList(new CopyOnWriteArrayList());
            i2 = 0;
            n2 = insnNode.invisibleTypeAnnotations.size();
            while (i2 < n2) {
                sourceAnnotation = insnNode.invisibleTypeAnnotations.get(i2);
                cloneAnnotation = new TypeAnnotationNode(sourceAnnotation.typeRef, sourceAnnotation.typePath, sourceAnnotation.desc);
                sourceAnnotation.accept(cloneAnnotation);
                this.invisibleTypeAnnotations.add(cloneAnnotation);
                ++i2;
            }
        }
        return this;
    }
}

