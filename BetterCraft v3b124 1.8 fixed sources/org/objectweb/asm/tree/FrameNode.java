/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.Util;

public class FrameNode
extends AbstractInsnNode {
    public int type;
    public List<Object> local;
    public List<Object> stack;

    private FrameNode() {
        super(-1);
    }

    public FrameNode(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
        super(-1);
        this.type = type;
        switch (type) {
            case -1: 
            case 0: {
                this.local = Util.asArrayList(numLocal, local);
                this.stack = Util.asArrayList(numStack, stack);
            }
            case 1: {
                this.local = Util.asArrayList(numLocal, local);
            }
            case 2: {
                this.local = Util.asArrayList(numLocal);
            }
            case 3: {
                return;
            }
            case 4: {
                this.stack = Util.asArrayList(1, stack);
            }
        }
        throw new IllegalArgumentException();
    }

    private static Object[] asArray(List<Object> list) {
        Object[] array = new Object[list.size()];
        int i2 = 0;
        int n2 = array.length;
        while (i2 < n2) {
            Object o2 = list.get(i2);
            if (o2 instanceof LabelNode) {
                o2 = ((LabelNode)o2).getLabel();
            }
            array[i2] = o2;
            ++i2;
        }
        return array;
    }

    @Override
    public int getType() {
        return 14;
    }

    @Override
    public void accept(MethodVisitor methodVisitor) {
        switch (this.type) {
            case -1: 
            case 0: {
                methodVisitor.visitFrame(this.type, this.local.size(), FrameNode.asArray(this.local), this.stack.size(), FrameNode.asArray(this.stack));
                return;
            }
            case 1: {
                methodVisitor.visitFrame(this.type, this.local.size(), FrameNode.asArray(this.local), 0, null);
                return;
            }
            case 2: {
                methodVisitor.visitFrame(this.type, this.local.size(), null, 0, null);
                return;
            }
            case 3: {
                methodVisitor.visitFrame(this.type, 0, null, 0, null);
                return;
            }
            case 4: {
                methodVisitor.visitFrame(this.type, 0, null, 1, FrameNode.asArray(this.stack));
                return;
            }
        }
        throw new IllegalArgumentException();
    }

    @Override
    public AbstractInsnNode clone(Map<LabelNode, LabelNode> clonedLabels) {
        int n2;
        int i2;
        FrameNode clone = new FrameNode();
        clone.type = this.type;
        if (this.local != null) {
            clone.local = new ArrayList<Object>();
            i2 = 0;
            n2 = this.local.size();
            while (i2 < n2) {
                Object localElement = this.local.get(i2);
                if (localElement instanceof LabelNode) {
                    localElement = clonedLabels.get(localElement);
                }
                clone.local.add(localElement);
                ++i2;
            }
        }
        if (this.stack != null) {
            clone.stack = new ArrayList<Object>();
            i2 = 0;
            n2 = this.stack.size();
            while (i2 < n2) {
                Object stackElement = this.stack.get(i2);
                if (stackElement instanceof LabelNode) {
                    stackElement = clonedLabels.get(stackElement);
                }
                clone.stack.add(stackElement);
                ++i2;
            }
        }
        return clone;
    }
}

