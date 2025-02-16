/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.tree.analysis;

import java.util.List;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Value;

public abstract class Interpreter {
    protected final int api;

    protected Interpreter(int n2) {
        this.api = n2;
    }

    public abstract Value newValue(Type var1);

    public abstract Value newOperation(AbstractInsnNode var1) throws AnalyzerException;

    public abstract Value copyOperation(AbstractInsnNode var1, Value var2) throws AnalyzerException;

    public abstract Value unaryOperation(AbstractInsnNode var1, Value var2) throws AnalyzerException;

    public abstract Value binaryOperation(AbstractInsnNode var1, Value var2, Value var3) throws AnalyzerException;

    public abstract Value ternaryOperation(AbstractInsnNode var1, Value var2, Value var3, Value var4) throws AnalyzerException;

    public abstract Value naryOperation(AbstractInsnNode var1, List var2) throws AnalyzerException;

    public abstract void returnOperation(AbstractInsnNode var1, Value var2, Value var3) throws AnalyzerException;

    public abstract Value merge(Value var1, Value var2);
}

