/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.tree;

import java.util.List;
import java.util.Map;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.Util;

public class LookupSwitchInsnNode
extends AbstractInsnNode {
    public LabelNode dflt;
    public List<Integer> keys;
    public List<LabelNode> labels;

    public LookupSwitchInsnNode(LabelNode dflt, int[] keys, LabelNode[] labels) {
        super(171);
        this.dflt = dflt;
        this.keys = Util.asArrayList(keys);
        this.labels = Util.asArrayList(labels);
    }

    @Override
    public int getType() {
        return 12;
    }

    @Override
    public void accept(MethodVisitor methodVisitor) {
        int[] keysArray = new int[this.keys.size()];
        int i2 = 0;
        int n2 = keysArray.length;
        while (i2 < n2) {
            keysArray[i2] = this.keys.get(i2);
            ++i2;
        }
        Label[] labelsArray = new Label[this.labels.size()];
        int i3 = 0;
        int n3 = labelsArray.length;
        while (i3 < n3) {
            labelsArray[i3] = this.labels.get(i3).getLabel();
            ++i3;
        }
        methodVisitor.visitLookupSwitchInsn(this.dflt.getLabel(), keysArray, labelsArray);
        this.acceptAnnotations(methodVisitor);
    }

    @Override
    public AbstractInsnNode clone(Map<LabelNode, LabelNode> clonedLabels) {
        LookupSwitchInsnNode clone = new LookupSwitchInsnNode(LookupSwitchInsnNode.clone(this.dflt, clonedLabels), null, LookupSwitchInsnNode.clone(this.labels, clonedLabels));
        clone.keys.addAll(this.keys);
        return clone.cloneAnnotations(this);
    }
}

