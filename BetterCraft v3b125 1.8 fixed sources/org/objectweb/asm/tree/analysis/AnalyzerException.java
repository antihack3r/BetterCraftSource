/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.tree.analysis;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.analysis.Value;

public class AnalyzerException
extends Exception {
    public final AbstractInsnNode node;

    public AnalyzerException(AbstractInsnNode abstractInsnNode, String string) {
        super(string);
        this.node = abstractInsnNode;
    }

    public AnalyzerException(AbstractInsnNode abstractInsnNode, String string, Throwable throwable) {
        super(string, throwable);
        this.node = abstractInsnNode;
    }

    public AnalyzerException(AbstractInsnNode abstractInsnNode, String string, Object object, Value value) {
        super((string == null ? "Expected " : string + ": expected ") + object + ", but found " + value);
        this.node = abstractInsnNode;
    }
}

