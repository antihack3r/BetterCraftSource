/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.tree.analysis;

import java.util.Set;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.analysis.SmallSet;
import org.objectweb.asm.tree.analysis.Value;

public class SourceValue
implements Value {
    public final int size;
    public final Set insns;

    public SourceValue(int n2) {
        this(n2, SmallSet.emptySet());
    }

    public SourceValue(int n2, AbstractInsnNode abstractInsnNode) {
        this.size = n2;
        this.insns = new SmallSet(abstractInsnNode, null);
    }

    public SourceValue(int n2, Set set) {
        this.size = n2;
        this.insns = set;
    }

    public int getSize() {
        return this.size;
    }

    public boolean equals(Object object) {
        if (!(object instanceof SourceValue)) {
            return false;
        }
        SourceValue sourceValue = (SourceValue)object;
        return this.size == sourceValue.size && this.insns.equals(sourceValue.insns);
    }

    public int hashCode() {
        return this.insns.hashCode();
    }
}

