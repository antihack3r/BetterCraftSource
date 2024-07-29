/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.commons;

import java.util.AbstractMap;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.objectweb.asm.commons.JSRInlinerAdapter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;

class JSRInlinerAdapter$Instantiation
extends AbstractMap {
    final JSRInlinerAdapter$Instantiation previous;
    public final BitSet subroutine;
    public final Map rangeTable = new HashMap();
    public final LabelNode returnLabel;
    final /* synthetic */ JSRInlinerAdapter this$0;

    JSRInlinerAdapter$Instantiation(JSRInlinerAdapter jSRInlinerAdapter, JSRInlinerAdapter$Instantiation jSRInlinerAdapter$Instantiation, BitSet bitSet) {
        this.this$0 = jSRInlinerAdapter;
        this.previous = jSRInlinerAdapter$Instantiation;
        this.subroutine = bitSet;
        Object object = jSRInlinerAdapter$Instantiation;
        while (object != null) {
            if (((JSRInlinerAdapter$Instantiation)object).subroutine == bitSet) {
                throw new RuntimeException("Recursive invocation of " + bitSet);
            }
            object = ((JSRInlinerAdapter$Instantiation)object).previous;
        }
        this.returnLabel = jSRInlinerAdapter$Instantiation != null ? new LabelNode() : null;
        object = null;
        int n2 = jSRInlinerAdapter.instructions.size();
        for (int i2 = 0; i2 < n2; ++i2) {
            AbstractInsnNode abstractInsnNode = jSRInlinerAdapter.instructions.get(i2);
            if (abstractInsnNode.getType() == 8) {
                LabelNode labelNode = (LabelNode)abstractInsnNode;
                if (object == null) {
                    object = new LabelNode();
                }
                this.rangeTable.put(labelNode, object);
                continue;
            }
            if (this.findOwner(i2) != this) continue;
            object = null;
        }
    }

    public JSRInlinerAdapter$Instantiation findOwner(int n2) {
        if (!this.subroutine.get(n2)) {
            return null;
        }
        if (!this.this$0.dualCitizens.get(n2)) {
            return this;
        }
        JSRInlinerAdapter$Instantiation jSRInlinerAdapter$Instantiation = this;
        JSRInlinerAdapter$Instantiation jSRInlinerAdapter$Instantiation2 = this.previous;
        while (jSRInlinerAdapter$Instantiation2 != null) {
            if (jSRInlinerAdapter$Instantiation2.subroutine.get(n2)) {
                jSRInlinerAdapter$Instantiation = jSRInlinerAdapter$Instantiation2;
            }
            jSRInlinerAdapter$Instantiation2 = jSRInlinerAdapter$Instantiation2.previous;
        }
        return jSRInlinerAdapter$Instantiation;
    }

    public LabelNode gotoLabel(LabelNode labelNode) {
        JSRInlinerAdapter$Instantiation jSRInlinerAdapter$Instantiation = this.findOwner(this.this$0.instructions.indexOf(labelNode));
        return (LabelNode)jSRInlinerAdapter$Instantiation.rangeTable.get(labelNode);
    }

    public LabelNode rangeLabel(LabelNode labelNode) {
        return (LabelNode)this.rangeTable.get(labelNode);
    }

    public Set entrySet() {
        return null;
    }

    public LabelNode get(Object object) {
        return this.gotoLabel((LabelNode)object);
    }
}

