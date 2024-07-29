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

public class AnalyzerAdapter
extends MethodVisitor {
    public List locals;
    public List stack;
    private List labels;
    public Map uninitializedTypes;
    private int maxStack;
    private int maxLocals;
    private String owner;
    static /* synthetic */ Class class$org$objectweb$asm$commons$AnalyzerAdapter;

    public AnalyzerAdapter(String string, int n2, String string2, String string3, MethodVisitor methodVisitor) {
        this(327680, string, n2, string2, string3, methodVisitor);
        if (this.getClass() != class$org$objectweb$asm$commons$AnalyzerAdapter) {
            throw new IllegalStateException();
        }
    }

    protected AnalyzerAdapter(int n2, String string, int n3, String string2, String string3, MethodVisitor methodVisitor) {
        super(n2, methodVisitor);
        this.owner = string;
        this.locals = new ArrayList();
        this.stack = new ArrayList();
        this.uninitializedTypes = new HashMap();
        if ((n3 & 8) == 0) {
            if ("<init>".equals(string2)) {
                this.locals.add(Opcodes.UNINITIALIZED_THIS);
            } else {
                this.locals.add(string);
            }
        }
        Type[] typeArray = Type.getArgumentTypes(string3);
        block7: for (int i2 = 0; i2 < typeArray.length; ++i2) {
            Type type = typeArray[i2];
            switch (type.getSort()) {
                case 1: 
                case 2: 
                case 3: 
                case 4: 
                case 5: {
                    this.locals.add(Opcodes.INTEGER);
                    continue block7;
                }
                case 6: {
                    this.locals.add(Opcodes.FLOAT);
                    continue block7;
                }
                case 7: {
                    this.locals.add(Opcodes.LONG);
                    this.locals.add(Opcodes.TOP);
                    continue block7;
                }
                case 8: {
                    this.locals.add(Opcodes.DOUBLE);
                    this.locals.add(Opcodes.TOP);
                    continue block7;
                }
                case 9: {
                    this.locals.add(typeArray[i2].getDescriptor());
                    continue block7;
                }
                default: {
                    this.locals.add(typeArray[i2].getInternalName());
                }
            }
        }
        this.maxLocals = this.locals.size();
    }

    public void visitFrame(int n2, int n3, Object[] objectArray, int n4, Object[] objectArray2) {
        if (n2 != -1) {
            throw new IllegalStateException("ClassReader.accept() should be called with EXPAND_FRAMES flag");
        }
        if (this.mv != null) {
            this.mv.visitFrame(n2, n3, objectArray, n4, objectArray2);
        }
        if (this.locals != null) {
            this.locals.clear();
            this.stack.clear();
        } else {
            this.locals = new ArrayList();
            this.stack = new ArrayList();
        }
        AnalyzerAdapter.visitFrameTypes(n3, objectArray, this.locals);
        AnalyzerAdapter.visitFrameTypes(n4, objectArray2, this.stack);
        this.maxStack = Math.max(this.maxStack, this.stack.size());
    }

    private static void visitFrameTypes(int n2, Object[] objectArray, List list) {
        for (int i2 = 0; i2 < n2; ++i2) {
            Object object = objectArray[i2];
            list.add(object);
            if (object != Opcodes.LONG && object != Opcodes.DOUBLE) continue;
            list.add(Opcodes.TOP);
        }
    }

    public void visitInsn(int n2) {
        if (this.mv != null) {
            this.mv.visitInsn(n2);
        }
        this.execute(n2, 0, null);
        if (n2 >= 172 && n2 <= 177 || n2 == 191) {
            this.locals = null;
            this.stack = null;
        }
    }

    public void visitIntInsn(int n2, int n3) {
        if (this.mv != null) {
            this.mv.visitIntInsn(n2, n3);
        }
        this.execute(n2, n3, null);
    }

    public void visitVarInsn(int n2, int n3) {
        if (this.mv != null) {
            this.mv.visitVarInsn(n2, n3);
        }
        this.execute(n2, n3, null);
    }

    public void visitTypeInsn(int n2, String string) {
        if (n2 == 187) {
            if (this.labels == null) {
                Label label = new Label();
                this.labels = new ArrayList(3);
                this.labels.add(label);
                if (this.mv != null) {
                    this.mv.visitLabel(label);
                }
            }
            for (int i2 = 0; i2 < this.labels.size(); ++i2) {
                this.uninitializedTypes.put(this.labels.get(i2), string);
            }
        }
        if (this.mv != null) {
            this.mv.visitTypeInsn(n2, string);
        }
        this.execute(n2, 0, string);
    }

    public void visitFieldInsn(int n2, String string, String string2, String string3) {
        if (this.mv != null) {
            this.mv.visitFieldInsn(n2, string, string2, string3);
        }
        this.execute(n2, 0, string3);
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
        if (this.mv != null) {
            this.mv.visitMethodInsn(n2, string, string2, string3, bl2);
        }
        if (this.locals == null) {
            this.labels = null;
            return;
        }
        this.pop(string3);
        if (n2 != 184) {
            Object object = this.pop();
            if (n2 == 183 && string2.charAt(0) == '<') {
                int n3;
                String string4 = object == Opcodes.UNINITIALIZED_THIS ? this.owner : this.uninitializedTypes.get(object);
                for (n3 = 0; n3 < this.locals.size(); ++n3) {
                    if (this.locals.get(n3) != object) continue;
                    this.locals.set(n3, string4);
                }
                for (n3 = 0; n3 < this.stack.size(); ++n3) {
                    if (this.stack.get(n3) != object) continue;
                    this.stack.set(n3, string4);
                }
            }
        }
        this.pushDesc(string3);
        this.labels = null;
    }

    public void visitInvokeDynamicInsn(String string, String string2, Handle handle, Object ... objectArray) {
        if (this.mv != null) {
            this.mv.visitInvokeDynamicInsn(string, string2, handle, objectArray);
        }
        if (this.locals == null) {
            this.labels = null;
            return;
        }
        this.pop(string2);
        this.pushDesc(string2);
        this.labels = null;
    }

    public void visitJumpInsn(int n2, Label label) {
        if (this.mv != null) {
            this.mv.visitJumpInsn(n2, label);
        }
        this.execute(n2, 0, null);
        if (n2 == 167) {
            this.locals = null;
            this.stack = null;
        }
    }

    public void visitLabel(Label label) {
        if (this.mv != null) {
            this.mv.visitLabel(label);
        }
        if (this.labels == null) {
            this.labels = new ArrayList(3);
        }
        this.labels.add(label);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void visitLdcInsn(Object object) {
        if (this.mv != null) {
            this.mv.visitLdcInsn(object);
        }
        if (this.locals == null) {
            this.labels = null;
            return;
        }
        if (object instanceof Integer) {
            this.push(Opcodes.INTEGER);
        } else if (object instanceof Long) {
            this.push(Opcodes.LONG);
            this.push(Opcodes.TOP);
        } else if (object instanceof Float) {
            this.push(Opcodes.FLOAT);
        } else if (object instanceof Double) {
            this.push(Opcodes.DOUBLE);
            this.push(Opcodes.TOP);
        } else if (object instanceof String) {
            this.push("java/lang/String");
        } else if (object instanceof Type) {
            int n2 = ((Type)object).getSort();
            if (n2 == 10 || n2 == 9) {
                this.push("java/lang/Class");
            } else {
                if (n2 != 11) throw new IllegalArgumentException();
                this.push("java/lang/invoke/MethodType");
            }
        } else {
            if (!(object instanceof Handle)) throw new IllegalArgumentException();
            this.push("java/lang/invoke/MethodHandle");
        }
        this.labels = null;
    }

    public void visitIincInsn(int n2, int n3) {
        if (this.mv != null) {
            this.mv.visitIincInsn(n2, n3);
        }
        this.execute(132, n2, null);
    }

    public void visitTableSwitchInsn(int n2, int n3, Label label, Label ... labelArray) {
        if (this.mv != null) {
            this.mv.visitTableSwitchInsn(n2, n3, label, labelArray);
        }
        this.execute(170, 0, null);
        this.locals = null;
        this.stack = null;
    }

    public void visitLookupSwitchInsn(Label label, int[] nArray, Label[] labelArray) {
        if (this.mv != null) {
            this.mv.visitLookupSwitchInsn(label, nArray, labelArray);
        }
        this.execute(171, 0, null);
        this.locals = null;
        this.stack = null;
    }

    public void visitMultiANewArrayInsn(String string, int n2) {
        if (this.mv != null) {
            this.mv.visitMultiANewArrayInsn(string, n2);
        }
        this.execute(197, n2, string);
    }

    public void visitMaxs(int n2, int n3) {
        if (this.mv != null) {
            this.maxStack = Math.max(this.maxStack, n2);
            this.maxLocals = Math.max(this.maxLocals, n3);
            this.mv.visitMaxs(this.maxStack, this.maxLocals);
        }
    }

    private Object get(int n2) {
        this.maxLocals = Math.max(this.maxLocals, n2 + 1);
        return n2 < this.locals.size() ? this.locals.get(n2) : Opcodes.TOP;
    }

    private void set(int n2, Object object) {
        this.maxLocals = Math.max(this.maxLocals, n2 + 1);
        while (n2 >= this.locals.size()) {
            this.locals.add(Opcodes.TOP);
        }
        this.locals.set(n2, object);
    }

    private void push(Object object) {
        this.stack.add(object);
        this.maxStack = Math.max(this.maxStack, this.stack.size());
    }

    private void pushDesc(String string) {
        int n2 = string.charAt(0) == '(' ? string.indexOf(41) + 1 : 0;
        switch (string.charAt(n2)) {
            case 'V': {
                return;
            }
            case 'B': 
            case 'C': 
            case 'I': 
            case 'S': 
            case 'Z': {
                this.push(Opcodes.INTEGER);
                return;
            }
            case 'F': {
                this.push(Opcodes.FLOAT);
                return;
            }
            case 'J': {
                this.push(Opcodes.LONG);
                this.push(Opcodes.TOP);
                return;
            }
            case 'D': {
                this.push(Opcodes.DOUBLE);
                this.push(Opcodes.TOP);
                return;
            }
            case '[': {
                if (n2 == 0) {
                    this.push(string);
                    break;
                }
                this.push(string.substring(n2, string.length()));
                break;
            }
            default: {
                if (n2 == 0) {
                    this.push(string.substring(1, string.length() - 1));
                    break;
                }
                this.push(string.substring(n2 + 1, string.length() - 1));
            }
        }
    }

    private Object pop() {
        return this.stack.remove(this.stack.size() - 1);
    }

    private void pop(int n2) {
        int n3 = this.stack.size();
        int n4 = n3 - n2;
        for (int i2 = n3 - 1; i2 >= n4; --i2) {
            this.stack.remove(i2);
        }
    }

    private void pop(String string) {
        char c2 = string.charAt(0);
        if (c2 == '(') {
            int n2 = 0;
            Type[] typeArray = Type.getArgumentTypes(string);
            for (int i2 = 0; i2 < typeArray.length; ++i2) {
                n2 += typeArray[i2].getSize();
            }
            this.pop(n2);
        } else if (c2 == 'J' || c2 == 'D') {
            this.pop(2);
        } else {
            this.pop(1);
        }
    }

    private void execute(int n2, int n3, String string) {
        if (this.locals == null) {
            this.labels = null;
            return;
        }
        block0 : switch (n2) {
            case 0: 
            case 116: 
            case 117: 
            case 118: 
            case 119: 
            case 145: 
            case 146: 
            case 147: 
            case 167: 
            case 177: {
                break;
            }
            case 1: {
                this.push(Opcodes.NULL);
                break;
            }
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 16: 
            case 17: {
                this.push(Opcodes.INTEGER);
                break;
            }
            case 9: 
            case 10: {
                this.push(Opcodes.LONG);
                this.push(Opcodes.TOP);
                break;
            }
            case 11: 
            case 12: 
            case 13: {
                this.push(Opcodes.FLOAT);
                break;
            }
            case 14: 
            case 15: {
                this.push(Opcodes.DOUBLE);
                this.push(Opcodes.TOP);
                break;
            }
            case 21: 
            case 23: 
            case 25: {
                this.push(this.get(n3));
                break;
            }
            case 22: 
            case 24: {
                this.push(this.get(n3));
                this.push(Opcodes.TOP);
                break;
            }
            case 46: 
            case 51: 
            case 52: 
            case 53: {
                this.pop(2);
                this.push(Opcodes.INTEGER);
                break;
            }
            case 47: 
            case 143: {
                this.pop(2);
                this.push(Opcodes.LONG);
                this.push(Opcodes.TOP);
                break;
            }
            case 48: {
                this.pop(2);
                this.push(Opcodes.FLOAT);
                break;
            }
            case 49: 
            case 138: {
                this.pop(2);
                this.push(Opcodes.DOUBLE);
                this.push(Opcodes.TOP);
                break;
            }
            case 50: {
                this.pop(1);
                Object object = this.pop();
                if (object instanceof String) {
                    this.pushDesc(((String)object).substring(1));
                    break;
                }
                this.push("java/lang/Object");
                break;
            }
            case 54: 
            case 56: 
            case 58: {
                Object object;
                Object object2 = this.pop();
                this.set(n3, object2);
                if (n3 <= 0 || (object = this.get(n3 - 1)) != Opcodes.LONG && object != Opcodes.DOUBLE) break;
                this.set(n3 - 1, Opcodes.TOP);
                break;
            }
            case 55: 
            case 57: {
                Object object;
                this.pop(1);
                Object object3 = this.pop();
                this.set(n3, object3);
                this.set(n3 + 1, Opcodes.TOP);
                if (n3 <= 0 || (object = this.get(n3 - 1)) != Opcodes.LONG && object != Opcodes.DOUBLE) break;
                this.set(n3 - 1, Opcodes.TOP);
                break;
            }
            case 79: 
            case 81: 
            case 83: 
            case 84: 
            case 85: 
            case 86: {
                this.pop(3);
                break;
            }
            case 80: 
            case 82: {
                this.pop(4);
                break;
            }
            case 87: 
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 170: 
            case 171: 
            case 172: 
            case 174: 
            case 176: 
            case 191: 
            case 194: 
            case 195: 
            case 198: 
            case 199: {
                this.pop(1);
                break;
            }
            case 88: 
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: 
            case 165: 
            case 166: 
            case 173: 
            case 175: {
                this.pop(2);
                break;
            }
            case 89: {
                Object object = this.pop();
                this.push(object);
                this.push(object);
                break;
            }
            case 90: {
                Object object = this.pop();
                Object object4 = this.pop();
                this.push(object);
                this.push(object4);
                this.push(object);
                break;
            }
            case 91: {
                Object object = this.pop();
                Object object5 = this.pop();
                Object object6 = this.pop();
                this.push(object);
                this.push(object6);
                this.push(object5);
                this.push(object);
                break;
            }
            case 92: {
                Object object = this.pop();
                Object object7 = this.pop();
                this.push(object7);
                this.push(object);
                this.push(object7);
                this.push(object);
                break;
            }
            case 93: {
                Object object = this.pop();
                Object object8 = this.pop();
                Object object9 = this.pop();
                this.push(object8);
                this.push(object);
                this.push(object9);
                this.push(object8);
                this.push(object);
                break;
            }
            case 94: {
                Object object = this.pop();
                Object object10 = this.pop();
                Object object11 = this.pop();
                Object object12 = this.pop();
                this.push(object10);
                this.push(object);
                this.push(object12);
                this.push(object11);
                this.push(object10);
                this.push(object);
                break;
            }
            case 95: {
                Object object = this.pop();
                Object object13 = this.pop();
                this.push(object);
                this.push(object13);
                break;
            }
            case 96: 
            case 100: 
            case 104: 
            case 108: 
            case 112: 
            case 120: 
            case 122: 
            case 124: 
            case 126: 
            case 128: 
            case 130: 
            case 136: 
            case 142: 
            case 149: 
            case 150: {
                this.pop(2);
                this.push(Opcodes.INTEGER);
                break;
            }
            case 97: 
            case 101: 
            case 105: 
            case 109: 
            case 113: 
            case 127: 
            case 129: 
            case 131: {
                this.pop(4);
                this.push(Opcodes.LONG);
                this.push(Opcodes.TOP);
                break;
            }
            case 98: 
            case 102: 
            case 106: 
            case 110: 
            case 114: 
            case 137: 
            case 144: {
                this.pop(2);
                this.push(Opcodes.FLOAT);
                break;
            }
            case 99: 
            case 103: 
            case 107: 
            case 111: 
            case 115: {
                this.pop(4);
                this.push(Opcodes.DOUBLE);
                this.push(Opcodes.TOP);
                break;
            }
            case 121: 
            case 123: 
            case 125: {
                this.pop(3);
                this.push(Opcodes.LONG);
                this.push(Opcodes.TOP);
                break;
            }
            case 132: {
                this.set(n3, Opcodes.INTEGER);
                break;
            }
            case 133: 
            case 140: {
                this.pop(1);
                this.push(Opcodes.LONG);
                this.push(Opcodes.TOP);
                break;
            }
            case 134: {
                this.pop(1);
                this.push(Opcodes.FLOAT);
                break;
            }
            case 135: 
            case 141: {
                this.pop(1);
                this.push(Opcodes.DOUBLE);
                this.push(Opcodes.TOP);
                break;
            }
            case 139: 
            case 190: 
            case 193: {
                this.pop(1);
                this.push(Opcodes.INTEGER);
                break;
            }
            case 148: 
            case 151: 
            case 152: {
                this.pop(4);
                this.push(Opcodes.INTEGER);
                break;
            }
            case 168: 
            case 169: {
                throw new RuntimeException("JSR/RET are not supported");
            }
            case 178: {
                this.pushDesc(string);
                break;
            }
            case 179: {
                this.pop(string);
                break;
            }
            case 180: {
                this.pop(1);
                this.pushDesc(string);
                break;
            }
            case 181: {
                this.pop(string);
                this.pop();
                break;
            }
            case 187: {
                this.push(this.labels.get(0));
                break;
            }
            case 188: {
                this.pop();
                switch (n3) {
                    case 4: {
                        this.pushDesc("[Z");
                        break block0;
                    }
                    case 5: {
                        this.pushDesc("[C");
                        break block0;
                    }
                    case 8: {
                        this.pushDesc("[B");
                        break block0;
                    }
                    case 9: {
                        this.pushDesc("[S");
                        break block0;
                    }
                    case 10: {
                        this.pushDesc("[I");
                        break block0;
                    }
                    case 6: {
                        this.pushDesc("[F");
                        break block0;
                    }
                    case 7: {
                        this.pushDesc("[D");
                        break block0;
                    }
                }
                this.pushDesc("[J");
                break;
            }
            case 189: {
                this.pop();
                this.pushDesc("[" + Type.getObjectType(string));
                break;
            }
            case 192: {
                this.pop();
                this.pushDesc(Type.getObjectType(string).getDescriptor());
                break;
            }
            default: {
                this.pop(n3);
                this.pushDesc(string);
            }
        }
        this.labels = null;
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            String string2 = classNotFoundException.getMessage();
            throw new NoClassDefFoundError(string2);
        }
    }

    static {
        class$org$objectweb$asm$commons$AnalyzerAdapter = AnalyzerAdapter.class$("org.objectweb.asm.commons.AnalyzerAdapter");
    }
}

