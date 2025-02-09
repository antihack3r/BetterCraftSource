/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.commons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

public abstract class AdviceAdapter
extends GeneratorAdapter
implements Opcodes {
    private static final Object THIS;
    private static final Object OTHER;
    protected int methodAccess;
    protected String methodDesc;
    private boolean constructor;
    private boolean superInitialized;
    private List stackFrame;
    private Map branches;

    protected AdviceAdapter(int n2, MethodVisitor methodVisitor, int n3, String string, String string2) {
        super(n2, methodVisitor, n3, string, string2);
        this.methodAccess = n3;
        this.methodDesc = string2;
        this.constructor = "<init>".equals(string);
    }

    public void visitCode() {
        this.mv.visitCode();
        if (this.constructor) {
            this.stackFrame = new ArrayList();
            this.branches = new HashMap();
        } else {
            this.superInitialized = true;
            this.onMethodEnter();
        }
    }

    public void visitLabel(Label label) {
        List list;
        this.mv.visitLabel(label);
        if (this.constructor && this.branches != null && (list = (List)this.branches.get(label)) != null) {
            this.stackFrame = list;
            this.branches.remove(label);
        }
    }

    public void visitInsn(int n2) {
        if (this.constructor) {
            switch (n2) {
                case 177: {
                    this.onMethodExit(n2);
                    break;
                }
                case 172: 
                case 174: 
                case 176: 
                case 191: {
                    this.popValue();
                    this.onMethodExit(n2);
                    break;
                }
                case 173: 
                case 175: {
                    this.popValue();
                    this.popValue();
                    this.onMethodExit(n2);
                    break;
                }
                case 0: 
                case 47: 
                case 49: 
                case 116: 
                case 117: 
                case 118: 
                case 119: 
                case 134: 
                case 138: 
                case 139: 
                case 143: 
                case 145: 
                case 146: 
                case 147: 
                case 190: {
                    break;
                }
                case 1: 
                case 2: 
                case 3: 
                case 4: 
                case 5: 
                case 6: 
                case 7: 
                case 8: 
                case 11: 
                case 12: 
                case 13: 
                case 133: 
                case 135: 
                case 140: 
                case 141: {
                    this.pushValue(OTHER);
                    break;
                }
                case 9: 
                case 10: 
                case 14: 
                case 15: {
                    this.pushValue(OTHER);
                    this.pushValue(OTHER);
                    break;
                }
                case 46: 
                case 48: 
                case 50: 
                case 51: 
                case 52: 
                case 53: 
                case 87: 
                case 96: 
                case 98: 
                case 100: 
                case 102: 
                case 104: 
                case 106: 
                case 108: 
                case 110: 
                case 112: 
                case 114: 
                case 120: 
                case 121: 
                case 122: 
                case 123: 
                case 124: 
                case 125: 
                case 126: 
                case 128: 
                case 130: 
                case 136: 
                case 137: 
                case 142: 
                case 144: 
                case 149: 
                case 150: 
                case 194: 
                case 195: {
                    this.popValue();
                    break;
                }
                case 88: 
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
                case 127: 
                case 129: 
                case 131: {
                    this.popValue();
                    this.popValue();
                    break;
                }
                case 79: 
                case 81: 
                case 83: 
                case 84: 
                case 85: 
                case 86: 
                case 148: 
                case 151: 
                case 152: {
                    this.popValue();
                    this.popValue();
                    this.popValue();
                    break;
                }
                case 80: 
                case 82: {
                    this.popValue();
                    this.popValue();
                    this.popValue();
                    this.popValue();
                    break;
                }
                case 89: {
                    this.pushValue(this.peekValue());
                    break;
                }
                case 90: {
                    int n3 = this.stackFrame.size();
                    this.stackFrame.add(n3 - 2, this.stackFrame.get(n3 - 1));
                    break;
                }
                case 91: {
                    int n4 = this.stackFrame.size();
                    this.stackFrame.add(n4 - 3, this.stackFrame.get(n4 - 1));
                    break;
                }
                case 92: {
                    int n5 = this.stackFrame.size();
                    this.stackFrame.add(n5 - 2, this.stackFrame.get(n5 - 1));
                    this.stackFrame.add(n5 - 2, this.stackFrame.get(n5 - 1));
                    break;
                }
                case 93: {
                    int n6 = this.stackFrame.size();
                    this.stackFrame.add(n6 - 3, this.stackFrame.get(n6 - 1));
                    this.stackFrame.add(n6 - 3, this.stackFrame.get(n6 - 1));
                    break;
                }
                case 94: {
                    int n7 = this.stackFrame.size();
                    this.stackFrame.add(n7 - 4, this.stackFrame.get(n7 - 1));
                    this.stackFrame.add(n7 - 4, this.stackFrame.get(n7 - 1));
                    break;
                }
                case 95: {
                    int n8 = this.stackFrame.size();
                    this.stackFrame.add(n8 - 2, this.stackFrame.get(n8 - 1));
                    this.stackFrame.remove(n8);
                }
            }
        } else {
            switch (n2) {
                case 172: 
                case 173: 
                case 174: 
                case 175: 
                case 176: 
                case 177: 
                case 191: {
                    this.onMethodExit(n2);
                }
            }
        }
        this.mv.visitInsn(n2);
    }

    public void visitVarInsn(int n2, int n3) {
        super.visitVarInsn(n2, n3);
        if (this.constructor) {
            switch (n2) {
                case 21: 
                case 23: {
                    this.pushValue(OTHER);
                    break;
                }
                case 22: 
                case 24: {
                    this.pushValue(OTHER);
                    this.pushValue(OTHER);
                    break;
                }
                case 25: {
                    this.pushValue(n3 == 0 ? THIS : OTHER);
                    break;
                }
                case 54: 
                case 56: 
                case 58: {
                    this.popValue();
                    break;
                }
                case 55: 
                case 57: {
                    this.popValue();
                    this.popValue();
                }
            }
        }
    }

    public void visitFieldInsn(int n2, String string, String string2, String string3) {
        this.mv.visitFieldInsn(n2, string, string2, string3);
        if (this.constructor) {
            char c2 = string3.charAt(0);
            boolean bl2 = c2 == 'J' || c2 == 'D';
            switch (n2) {
                case 178: {
                    this.pushValue(OTHER);
                    if (!bl2) break;
                    this.pushValue(OTHER);
                    break;
                }
                case 179: {
                    this.popValue();
                    if (!bl2) break;
                    this.popValue();
                    break;
                }
                case 181: {
                    this.popValue();
                    this.popValue();
                    if (!bl2) break;
                    this.popValue();
                    break;
                }
                default: {
                    if (!bl2) break;
                    this.pushValue(OTHER);
                }
            }
        }
    }

    public void visitIntInsn(int n2, int n3) {
        this.mv.visitIntInsn(n2, n3);
        if (this.constructor && n2 != 188) {
            this.pushValue(OTHER);
        }
    }

    public void visitLdcInsn(Object object) {
        this.mv.visitLdcInsn(object);
        if (this.constructor) {
            this.pushValue(OTHER);
            if (object instanceof Double || object instanceof Long) {
                this.pushValue(OTHER);
            }
        }
    }

    public void visitMultiANewArrayInsn(String string, int n2) {
        this.mv.visitMultiANewArrayInsn(string, n2);
        if (this.constructor) {
            for (int i2 = 0; i2 < n2; ++i2) {
                this.popValue();
            }
            this.pushValue(OTHER);
        }
    }

    public void visitTypeInsn(int n2, String string) {
        this.mv.visitTypeInsn(n2, string);
        if (this.constructor && n2 == 187) {
            this.pushValue(OTHER);
        }
    }

    public void visitMethodInsn(int n2, String string, String string2, String string3) {
        if (this.api >= 327680) {
            super.visitMethodInsn(n2, string, string2, string3);
            return;
        }
        this.doVisitMethodInsn(n2, string, string2, string3, n2 == 185);
    }

    public void visitMethodInsn(int n2, String string, String string2, String string3, boolean bl2) {
        if (this.api < 327680) {
            super.visitMethodInsn(n2, string, string2, string3, bl2);
            return;
        }
        this.doVisitMethodInsn(n2, string, string2, string3, bl2);
    }

    private void doVisitMethodInsn(int n2, String string, String string2, String string3, boolean bl2) {
        this.mv.visitMethodInsn(n2, string, string2, string3, bl2);
        if (this.constructor) {
            Type[] typeArray = Type.getArgumentTypes(string3);
            for (int i2 = 0; i2 < typeArray.length; ++i2) {
                this.popValue();
                if (typeArray[i2].getSize() != 2) continue;
                this.popValue();
            }
            switch (n2) {
                case 182: 
                case 185: {
                    this.popValue();
                    break;
                }
                case 183: {
                    Object object = this.popValue();
                    if (object != THIS || this.superInitialized) break;
                    this.onMethodEnter();
                    this.superInitialized = true;
                    this.constructor = false;
                }
            }
            Type type = Type.getReturnType(string3);
            if (type != Type.VOID_TYPE) {
                this.pushValue(OTHER);
                if (type.getSize() == 2) {
                    this.pushValue(OTHER);
                }
            }
        }
    }

    public void visitInvokeDynamicInsn(String string, String string2, Handle handle, Object ... objectArray) {
        this.mv.visitInvokeDynamicInsn(string, string2, handle, objectArray);
        if (this.constructor) {
            Type[] typeArray = Type.getArgumentTypes(string2);
            for (int i2 = 0; i2 < typeArray.length; ++i2) {
                this.popValue();
                if (typeArray[i2].getSize() != 2) continue;
                this.popValue();
            }
            Type type = Type.getReturnType(string2);
            if (type != Type.VOID_TYPE) {
                this.pushValue(OTHER);
                if (type.getSize() == 2) {
                    this.pushValue(OTHER);
                }
            }
        }
    }

    public void visitJumpInsn(int n2, Label label) {
        this.mv.visitJumpInsn(n2, label);
        if (this.constructor) {
            switch (n2) {
                case 153: 
                case 154: 
                case 155: 
                case 156: 
                case 157: 
                case 158: 
                case 198: 
                case 199: {
                    this.popValue();
                    break;
                }
                case 159: 
                case 160: 
                case 161: 
                case 162: 
                case 163: 
                case 164: 
                case 165: 
                case 166: {
                    this.popValue();
                    this.popValue();
                    break;
                }
                case 168: {
                    this.pushValue(OTHER);
                }
            }
            this.addBranch(label);
        }
    }

    public void visitLookupSwitchInsn(Label label, int[] nArray, Label[] labelArray) {
        this.mv.visitLookupSwitchInsn(label, nArray, labelArray);
        if (this.constructor) {
            this.popValue();
            this.addBranches(label, labelArray);
        }
    }

    public void visitTableSwitchInsn(int n2, int n3, Label label, Label ... labelArray) {
        this.mv.visitTableSwitchInsn(n2, n3, label, labelArray);
        if (this.constructor) {
            this.popValue();
            this.addBranches(label, labelArray);
        }
    }

    public void visitTryCatchBlock(Label label, Label label2, Label label3, String string) {
        super.visitTryCatchBlock(label, label2, label3, string);
        if (this.constructor && !this.branches.containsKey(label3)) {
            ArrayList<Object> arrayList = new ArrayList<Object>();
            arrayList.add(OTHER);
            this.branches.put(label3, arrayList);
        }
    }

    private void addBranches(Label label, Label[] labelArray) {
        this.addBranch(label);
        for (int i2 = 0; i2 < labelArray.length; ++i2) {
            this.addBranch(labelArray[i2]);
        }
    }

    private void addBranch(Label label) {
        if (this.branches.containsKey(label)) {
            return;
        }
        this.branches.put(label, new ArrayList(this.stackFrame));
    }

    private Object popValue() {
        return this.stackFrame.remove(this.stackFrame.size() - 1);
    }

    private Object peekValue() {
        return this.stackFrame.get(this.stackFrame.size() - 1);
    }

    private void pushValue(Object object) {
        this.stackFrame.add(object);
    }

    protected void onMethodEnter() {
    }

    protected void onMethodExit(int n2) {
    }

    static {
        AdviceAdapter._clinit_();
        THIS = new Object();
        OTHER = new Object();
    }

    static /* synthetic */ void _clinit_() {
    }
}

