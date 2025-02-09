/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.tree.analysis;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.analysis.Interpreter;
import org.objectweb.asm.tree.analysis.SmallSet;
import org.objectweb.asm.tree.analysis.SourceValue;

public class SourceInterpreter
extends Interpreter
implements Opcodes {
    public SourceInterpreter() {
        super(327680);
    }

    protected SourceInterpreter(int n2) {
        super(n2);
    }

    public SourceValue newValue(Type type) {
        if (type == Type.VOID_TYPE) {
            return null;
        }
        return new SourceValue(type == null ? 1 : type.getSize());
    }

    public SourceValue newOperation(AbstractInsnNode abstractInsnNode) {
        int n2;
        switch (abstractInsnNode.getOpcode()) {
            case 9: 
            case 10: 
            case 14: 
            case 15: {
                n2 = 2;
                break;
            }
            case 18: {
                Object object = ((LdcInsnNode)abstractInsnNode).cst;
                n2 = object instanceof Long || object instanceof Double ? 2 : 1;
                break;
            }
            case 178: {
                n2 = Type.getType(((FieldInsnNode)abstractInsnNode).desc).getSize();
                break;
            }
            default: {
                n2 = 1;
            }
        }
        return new SourceValue(n2, abstractInsnNode);
    }

    public SourceValue copyOperation(AbstractInsnNode abstractInsnNode, SourceValue sourceValue) {
        return new SourceValue(sourceValue.getSize(), abstractInsnNode);
    }

    public SourceValue unaryOperation(AbstractInsnNode abstractInsnNode, SourceValue sourceValue) {
        int n2;
        switch (abstractInsnNode.getOpcode()) {
            case 117: 
            case 119: 
            case 133: 
            case 135: 
            case 138: 
            case 140: 
            case 141: 
            case 143: {
                n2 = 2;
                break;
            }
            case 180: {
                n2 = Type.getType(((FieldInsnNode)abstractInsnNode).desc).getSize();
                break;
            }
            default: {
                n2 = 1;
            }
        }
        return new SourceValue(n2, abstractInsnNode);
    }

    public SourceValue binaryOperation(AbstractInsnNode abstractInsnNode, SourceValue sourceValue, SourceValue sourceValue2) {
        int n2;
        switch (abstractInsnNode.getOpcode()) {
            case 47: 
            case 49: 
            case 97: 
            case 99: 
            case 101: 
            case 103: 
            case 105: 
            case 107: 
            case 109: 
            case 111: 
            case 113: 
            case 115: 
            case 121: 
            case 123: 
            case 125: 
            case 127: 
            case 129: 
            case 131: {
                n2 = 2;
                break;
            }
            default: {
                n2 = 1;
            }
        }
        return new SourceValue(n2, abstractInsnNode);
    }

    public SourceValue ternaryOperation(AbstractInsnNode abstractInsnNode, SourceValue sourceValue, SourceValue sourceValue2, SourceValue sourceValue3) {
        return new SourceValue(1, abstractInsnNode);
    }

    public SourceValue naryOperation(AbstractInsnNode abstractInsnNode, List list) {
        int n2;
        int n3 = abstractInsnNode.getOpcode();
        if (n3 == 197) {
            n2 = 1;
        } else {
            String string = n3 == 186 ? ((InvokeDynamicInsnNode)abstractInsnNode).desc : ((MethodInsnNode)abstractInsnNode).desc;
            n2 = Type.getReturnType(string).getSize();
        }
        return new SourceValue(n2, abstractInsnNode);
    }

    public void returnOperation(AbstractInsnNode abstractInsnNode, SourceValue sourceValue, SourceValue sourceValue2) {
    }

    public SourceValue merge(SourceValue sourceValue, SourceValue sourceValue2) {
        if (sourceValue.insns instanceof SmallSet && sourceValue2.insns instanceof SmallSet) {
            Set set = ((SmallSet)sourceValue.insns).union((SmallSet)sourceValue2.insns);
            if (set == sourceValue.insns && sourceValue.size == sourceValue2.size) {
                return sourceValue;
            }
            return new SourceValue(Math.min(sourceValue.size, sourceValue2.size), set);
        }
        if (sourceValue.size != sourceValue2.size || !sourceValue.insns.containsAll(sourceValue2.insns)) {
            HashSet hashSet = new HashSet();
            hashSet.addAll(sourceValue.insns);
            hashSet.addAll(sourceValue2.insns);
            return new SourceValue(Math.min(sourceValue.size, sourceValue2.size), hashSet);
        }
        return sourceValue;
    }
}

