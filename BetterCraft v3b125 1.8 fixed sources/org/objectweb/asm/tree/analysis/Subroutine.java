/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.tree.analysis;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

class Subroutine {
    LabelNode start;
    boolean[] access;
    List callers;

    private Subroutine() {
    }

    Subroutine(LabelNode labelNode, int n2, JumpInsnNode jumpInsnNode) {
        this.start = labelNode;
        this.access = new boolean[n2];
        this.callers = new ArrayList();
        this.callers.add(jumpInsnNode);
    }

    public Subroutine copy() {
        Subroutine subroutine = new Subroutine();
        subroutine.start = this.start;
        subroutine.access = new boolean[this.access.length];
        System.arraycopy(this.access, 0, subroutine.access, 0, this.access.length);
        subroutine.callers = new ArrayList(this.callers);
        return subroutine;
    }

    public boolean merge(Subroutine subroutine) throws AnalyzerException {
        int n2;
        boolean bl2 = false;
        for (n2 = 0; n2 < this.access.length; ++n2) {
            if (!subroutine.access[n2] || this.access[n2]) continue;
            this.access[n2] = true;
            bl2 = true;
        }
        if (subroutine.start == this.start) {
            for (n2 = 0; n2 < subroutine.callers.size(); ++n2) {
                JumpInsnNode jumpInsnNode = (JumpInsnNode)subroutine.callers.get(n2);
                if (this.callers.contains(jumpInsnNode)) continue;
                this.callers.add(jumpInsnNode);
                bl2 = true;
            }
        }
        return bl2;
    }
}

