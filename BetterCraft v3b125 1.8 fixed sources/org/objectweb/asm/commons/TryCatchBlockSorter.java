/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.commons;

import java.util.Collections;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.TryCatchBlockSorter$1;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

public class TryCatchBlockSorter
extends MethodNode {
    public TryCatchBlockSorter(MethodVisitor methodVisitor, int n2, String string, String string2, String string3, String[] stringArray) {
        this(327680, methodVisitor, n2, string, string2, string3, stringArray);
    }

    protected TryCatchBlockSorter(int n2, MethodVisitor methodVisitor, int n3, String string, String string2, String string3, String[] stringArray) {
        super(n2, n3, string, string2, string3, stringArray);
        this.mv = methodVisitor;
    }

    public void visitEnd() {
        TryCatchBlockSorter$1 tryCatchBlockSorter$1 = new TryCatchBlockSorter$1(this);
        Collections.sort(this.tryCatchBlocks, tryCatchBlockSorter$1);
        for (int i2 = 0; i2 < this.tryCatchBlocks.size(); ++i2) {
            ((TryCatchBlockNode)this.tryCatchBlocks.get(i2)).updateIndex(i2);
        }
        if (this.mv != null) {
            this.accept(this.mv);
        }
    }
}

