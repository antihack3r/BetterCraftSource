/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.commons;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class InstructionAdapter
extends MethodVisitor {
    public static final Type OBJECT_TYPE;
    static /* synthetic */ Class class$org$objectweb$asm$commons$InstructionAdapter;

    public InstructionAdapter(MethodVisitor methodVisitor) {
        this(327680, methodVisitor);
        if (this.getClass() != class$org$objectweb$asm$commons$InstructionAdapter) {
            throw new IllegalStateException();
        }
    }

    protected InstructionAdapter(int n2, MethodVisitor methodVisitor) {
        super(n2, methodVisitor);
    }

    public void visitInsn(int n2) {
        switch (n2) {
            case 0: {
                this.nop();
                break;
            }
            case 1: {
                this.aconst(null);
                break;
            }
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: {
                this.iconst(n2 - 3);
                break;
            }
            case 9: 
            case 10: {
                this.lconst(n2 - 9);
                break;
            }
            case 11: 
            case 12: 
            case 13: {
                this.fconst(n2 - 11);
                break;
            }
            case 14: 
            case 15: {
                this.dconst(n2 - 14);
                break;
            }
            case 46: {
                this.aload(Type.INT_TYPE);
                break;
            }
            case 47: {
                this.aload(Type.LONG_TYPE);
                break;
            }
            case 48: {
                this.aload(Type.FLOAT_TYPE);
                break;
            }
            case 49: {
                this.aload(Type.DOUBLE_TYPE);
                break;
            }
            case 50: {
                this.aload(OBJECT_TYPE);
                break;
            }
            case 51: {
                this.aload(Type.BYTE_TYPE);
                break;
            }
            case 52: {
                this.aload(Type.CHAR_TYPE);
                break;
            }
            case 53: {
                this.aload(Type.SHORT_TYPE);
                break;
            }
            case 79: {
                this.astore(Type.INT_TYPE);
                break;
            }
            case 80: {
                this.astore(Type.LONG_TYPE);
                break;
            }
            case 81: {
                this.astore(Type.FLOAT_TYPE);
                break;
            }
            case 82: {
                this.astore(Type.DOUBLE_TYPE);
                break;
            }
            case 83: {
                this.astore(OBJECT_TYPE);
                break;
            }
            case 84: {
                this.astore(Type.BYTE_TYPE);
                break;
            }
            case 85: {
                this.astore(Type.CHAR_TYPE);
                break;
            }
            case 86: {
                this.astore(Type.SHORT_TYPE);
                break;
            }
            case 87: {
                this.pop();
                break;
            }
            case 88: {
                this.pop2();
                break;
            }
            case 89: {
                this.dup();
                break;
            }
            case 90: {
                this.dupX1();
                break;
            }
            case 91: {
                this.dupX2();
                break;
            }
            case 92: {
                this.dup2();
                break;
            }
            case 93: {
                this.dup2X1();
                break;
            }
            case 94: {
                this.dup2X2();
                break;
            }
            case 95: {
                this.swap();
                break;
            }
            case 96: {
                this.add(Type.INT_TYPE);
                break;
            }
            case 97: {
                this.add(Type.LONG_TYPE);
                break;
            }
            case 98: {
                this.add(Type.FLOAT_TYPE);
                break;
            }
            case 99: {
                this.add(Type.DOUBLE_TYPE);
                break;
            }
            case 100: {
                this.sub(Type.INT_TYPE);
                break;
            }
            case 101: {
                this.sub(Type.LONG_TYPE);
                break;
            }
            case 102: {
                this.sub(Type.FLOAT_TYPE);
                break;
            }
            case 103: {
                this.sub(Type.DOUBLE_TYPE);
                break;
            }
            case 104: {
                this.mul(Type.INT_TYPE);
                break;
            }
            case 105: {
                this.mul(Type.LONG_TYPE);
                break;
            }
            case 106: {
                this.mul(Type.FLOAT_TYPE);
                break;
            }
            case 107: {
                this.mul(Type.DOUBLE_TYPE);
                break;
            }
            case 108: {
                this.div(Type.INT_TYPE);
                break;
            }
            case 109: {
                this.div(Type.LONG_TYPE);
                break;
            }
            case 110: {
                this.div(Type.FLOAT_TYPE);
                break;
            }
            case 111: {
                this.div(Type.DOUBLE_TYPE);
                break;
            }
            case 112: {
                this.rem(Type.INT_TYPE);
                break;
            }
            case 113: {
                this.rem(Type.LONG_TYPE);
                break;
            }
            case 114: {
                this.rem(Type.FLOAT_TYPE);
                break;
            }
            case 115: {
                this.rem(Type.DOUBLE_TYPE);
                break;
            }
            case 116: {
                this.neg(Type.INT_TYPE);
                break;
            }
            case 117: {
                this.neg(Type.LONG_TYPE);
                break;
            }
            case 118: {
                this.neg(Type.FLOAT_TYPE);
                break;
            }
            case 119: {
                this.neg(Type.DOUBLE_TYPE);
                break;
            }
            case 120: {
                this.shl(Type.INT_TYPE);
                break;
            }
            case 121: {
                this.shl(Type.LONG_TYPE);
                break;
            }
            case 122: {
                this.shr(Type.INT_TYPE);
                break;
            }
            case 123: {
                this.shr(Type.LONG_TYPE);
                break;
            }
            case 124: {
                this.ushr(Type.INT_TYPE);
                break;
            }
            case 125: {
                this.ushr(Type.LONG_TYPE);
                break;
            }
            case 126: {
                this.and(Type.INT_TYPE);
                break;
            }
            case 127: {
                this.and(Type.LONG_TYPE);
                break;
            }
            case 128: {
                this.or(Type.INT_TYPE);
                break;
            }
            case 129: {
                this.or(Type.LONG_TYPE);
                break;
            }
            case 130: {
                this.xor(Type.INT_TYPE);
                break;
            }
            case 131: {
                this.xor(Type.LONG_TYPE);
                break;
            }
            case 133: {
                this.cast(Type.INT_TYPE, Type.LONG_TYPE);
                break;
            }
            case 134: {
                this.cast(Type.INT_TYPE, Type.FLOAT_TYPE);
                break;
            }
            case 135: {
                this.cast(Type.INT_TYPE, Type.DOUBLE_TYPE);
                break;
            }
            case 136: {
                this.cast(Type.LONG_TYPE, Type.INT_TYPE);
                break;
            }
            case 137: {
                this.cast(Type.LONG_TYPE, Type.FLOAT_TYPE);
                break;
            }
            case 138: {
                this.cast(Type.LONG_TYPE, Type.DOUBLE_TYPE);
                break;
            }
            case 139: {
                this.cast(Type.FLOAT_TYPE, Type.INT_TYPE);
                break;
            }
            case 140: {
                this.cast(Type.FLOAT_TYPE, Type.LONG_TYPE);
                break;
            }
            case 141: {
                this.cast(Type.FLOAT_TYPE, Type.DOUBLE_TYPE);
                break;
            }
            case 142: {
                this.cast(Type.DOUBLE_TYPE, Type.INT_TYPE);
                break;
            }
            case 143: {
                this.cast(Type.DOUBLE_TYPE, Type.LONG_TYPE);
                break;
            }
            case 144: {
                this.cast(Type.DOUBLE_TYPE, Type.FLOAT_TYPE);
                break;
            }
            case 145: {
                this.cast(Type.INT_TYPE, Type.BYTE_TYPE);
                break;
            }
            case 146: {
                this.cast(Type.INT_TYPE, Type.CHAR_TYPE);
                break;
            }
            case 147: {
                this.cast(Type.INT_TYPE, Type.SHORT_TYPE);
                break;
            }
            case 148: {
                this.lcmp();
                break;
            }
            case 149: {
                this.cmpl(Type.FLOAT_TYPE);
                break;
            }
            case 150: {
                this.cmpg(Type.FLOAT_TYPE);
                break;
            }
            case 151: {
                this.cmpl(Type.DOUBLE_TYPE);
                break;
            }
            case 152: {
                this.cmpg(Type.DOUBLE_TYPE);
                break;
            }
            case 172: {
                this.areturn(Type.INT_TYPE);
                break;
            }
            case 173: {
                this.areturn(Type.LONG_TYPE);
                break;
            }
            case 174: {
                this.areturn(Type.FLOAT_TYPE);
                break;
            }
            case 175: {
                this.areturn(Type.DOUBLE_TYPE);
                break;
            }
            case 176: {
                this.areturn(OBJECT_TYPE);
                break;
            }
            case 177: {
                this.areturn(Type.VOID_TYPE);
                break;
            }
            case 190: {
                this.arraylength();
                break;
            }
            case 191: {
                this.athrow();
                break;
            }
            case 194: {
                this.monitorenter();
                break;
            }
            case 195: {
                this.monitorexit();
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }

    public void visitIntInsn(int n2, int n3) {
        block0 : switch (n2) {
            case 16: {
                this.iconst(n3);
                break;
            }
            case 17: {
                this.iconst(n3);
                break;
            }
            case 188: {
                switch (n3) {
                    case 4: {
                        this.newarray(Type.BOOLEAN_TYPE);
                        break block0;
                    }
                    case 5: {
                        this.newarray(Type.CHAR_TYPE);
                        break block0;
                    }
                    case 8: {
                        this.newarray(Type.BYTE_TYPE);
                        break block0;
                    }
                    case 9: {
                        this.newarray(Type.SHORT_TYPE);
                        break block0;
                    }
                    case 10: {
                        this.newarray(Type.INT_TYPE);
                        break block0;
                    }
                    case 6: {
                        this.newarray(Type.FLOAT_TYPE);
                        break block0;
                    }
                    case 11: {
                        this.newarray(Type.LONG_TYPE);
                        break block0;
                    }
                    case 7: {
                        this.newarray(Type.DOUBLE_TYPE);
                        break block0;
                    }
                }
                throw new IllegalArgumentException();
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }

    public void visitVarInsn(int n2, int n3) {
        switch (n2) {
            case 21: {
                this.load(n3, Type.INT_TYPE);
                break;
            }
            case 22: {
                this.load(n3, Type.LONG_TYPE);
                break;
            }
            case 23: {
                this.load(n3, Type.FLOAT_TYPE);
                break;
            }
            case 24: {
                this.load(n3, Type.DOUBLE_TYPE);
                break;
            }
            case 25: {
                this.load(n3, OBJECT_TYPE);
                break;
            }
            case 54: {
                this.store(n3, Type.INT_TYPE);
                break;
            }
            case 55: {
                this.store(n3, Type.LONG_TYPE);
                break;
            }
            case 56: {
                this.store(n3, Type.FLOAT_TYPE);
                break;
            }
            case 57: {
                this.store(n3, Type.DOUBLE_TYPE);
                break;
            }
            case 58: {
                this.store(n3, OBJECT_TYPE);
                break;
            }
            case 169: {
                this.ret(n3);
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }

    public void visitTypeInsn(int n2, String string) {
        Type type = Type.getObjectType(string);
        switch (n2) {
            case 187: {
                this.anew(type);
                break;
            }
            case 189: {
                this.newarray(type);
                break;
            }
            case 192: {
                this.checkcast(type);
                break;
            }
            case 193: {
                this.instanceOf(type);
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }

    public void visitFieldInsn(int n2, String string, String string2, String string3) {
        switch (n2) {
            case 178: {
                this.getstatic(string, string2, string3);
                break;
            }
            case 179: {
                this.putstatic(string, string2, string3);
                break;
            }
            case 180: {
                this.getfield(string, string2, string3);
                break;
            }
            case 181: {
                this.putfield(string, string2, string3);
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
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
        switch (n2) {
            case 183: {
                this.invokespecial(string, string2, string3, bl2);
                break;
            }
            case 182: {
                this.invokevirtual(string, string2, string3, bl2);
                break;
            }
            case 184: {
                this.invokestatic(string, string2, string3, bl2);
                break;
            }
            case 185: {
                this.invokeinterface(string, string2, string3);
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }

    public void visitInvokeDynamicInsn(String string, String string2, Handle handle, Object ... objectArray) {
        this.invokedynamic(string, string2, handle, objectArray);
    }

    public void visitJumpInsn(int n2, Label label) {
        switch (n2) {
            case 153: {
                this.ifeq(label);
                break;
            }
            case 154: {
                this.ifne(label);
                break;
            }
            case 155: {
                this.iflt(label);
                break;
            }
            case 156: {
                this.ifge(label);
                break;
            }
            case 157: {
                this.ifgt(label);
                break;
            }
            case 158: {
                this.ifle(label);
                break;
            }
            case 159: {
                this.ificmpeq(label);
                break;
            }
            case 160: {
                this.ificmpne(label);
                break;
            }
            case 161: {
                this.ificmplt(label);
                break;
            }
            case 162: {
                this.ificmpge(label);
                break;
            }
            case 163: {
                this.ificmpgt(label);
                break;
            }
            case 164: {
                this.ificmple(label);
                break;
            }
            case 165: {
                this.ifacmpeq(label);
                break;
            }
            case 166: {
                this.ifacmpne(label);
                break;
            }
            case 167: {
                this.goTo(label);
                break;
            }
            case 168: {
                this.jsr(label);
                break;
            }
            case 198: {
                this.ifnull(label);
                break;
            }
            case 199: {
                this.ifnonnull(label);
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }

    public void visitLabel(Label label) {
        this.mark(label);
    }

    public void visitLdcInsn(Object object) {
        if (object instanceof Integer) {
            int n2 = (Integer)object;
            this.iconst(n2);
        } else if (object instanceof Byte) {
            int n3 = ((Byte)object).intValue();
            this.iconst(n3);
        } else if (object instanceof Character) {
            char c2 = ((Character)object).charValue();
            this.iconst(c2);
        } else if (object instanceof Short) {
            int n4 = ((Short)object).intValue();
            this.iconst(n4);
        } else if (object instanceof Boolean) {
            int n5 = (Boolean)object != false ? 1 : 0;
            this.iconst(n5);
        } else if (object instanceof Float) {
            float f2 = ((Float)object).floatValue();
            this.fconst(f2);
        } else if (object instanceof Long) {
            long l2 = (Long)object;
            this.lconst(l2);
        } else if (object instanceof Double) {
            double d2 = (Double)object;
            this.dconst(d2);
        } else if (object instanceof String) {
            this.aconst(object);
        } else if (object instanceof Type) {
            this.tconst((Type)object);
        } else if (object instanceof Handle) {
            this.hconst((Handle)object);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void visitIincInsn(int n2, int n3) {
        this.iinc(n2, n3);
    }

    public void visitTableSwitchInsn(int n2, int n3, Label label, Label ... labelArray) {
        this.tableswitch(n2, n3, label, labelArray);
    }

    public void visitLookupSwitchInsn(Label label, int[] nArray, Label[] labelArray) {
        this.lookupswitch(label, nArray, labelArray);
    }

    public void visitMultiANewArrayInsn(String string, int n2) {
        this.multianewarray(string, n2);
    }

    public void nop() {
        this.mv.visitInsn(0);
    }

    public void aconst(Object object) {
        if (object == null) {
            this.mv.visitInsn(1);
        } else {
            this.mv.visitLdcInsn(object);
        }
    }

    public void iconst(int n2) {
        if (n2 >= -1 && n2 <= 5) {
            this.mv.visitInsn(3 + n2);
        } else if (n2 >= -128 && n2 <= 127) {
            this.mv.visitIntInsn(16, n2);
        } else if (n2 >= Short.MIN_VALUE && n2 <= Short.MAX_VALUE) {
            this.mv.visitIntInsn(17, n2);
        } else {
            this.mv.visitLdcInsn(new Integer(n2));
        }
    }

    public void lconst(long l2) {
        if (l2 == 0L || l2 == 1L) {
            this.mv.visitInsn(9 + (int)l2);
        } else {
            this.mv.visitLdcInsn(new Long(l2));
        }
    }

    public void fconst(float f2) {
        int n2 = Float.floatToIntBits(f2);
        if ((long)n2 == 0L || n2 == 1065353216 || n2 == 0x40000000) {
            this.mv.visitInsn(11 + (int)f2);
        } else {
            this.mv.visitLdcInsn(new Float(f2));
        }
    }

    public void dconst(double d2) {
        long l2 = Double.doubleToLongBits(d2);
        if (l2 == 0L || l2 == 0x3FF0000000000000L) {
            this.mv.visitInsn(14 + (int)d2);
        } else {
            this.mv.visitLdcInsn(new Double(d2));
        }
    }

    public void tconst(Type type) {
        this.mv.visitLdcInsn(type);
    }

    public void hconst(Handle handle) {
        this.mv.visitLdcInsn(handle);
    }

    public void load(int n2, Type type) {
        this.mv.visitVarInsn(type.getOpcode(21), n2);
    }

    public void aload(Type type) {
        this.mv.visitInsn(type.getOpcode(46));
    }

    public void store(int n2, Type type) {
        this.mv.visitVarInsn(type.getOpcode(54), n2);
    }

    public void astore(Type type) {
        this.mv.visitInsn(type.getOpcode(79));
    }

    public void pop() {
        this.mv.visitInsn(87);
    }

    public void pop2() {
        this.mv.visitInsn(88);
    }

    public void dup() {
        this.mv.visitInsn(89);
    }

    public void dup2() {
        this.mv.visitInsn(92);
    }

    public void dupX1() {
        this.mv.visitInsn(90);
    }

    public void dupX2() {
        this.mv.visitInsn(91);
    }

    public void dup2X1() {
        this.mv.visitInsn(93);
    }

    public void dup2X2() {
        this.mv.visitInsn(94);
    }

    public void swap() {
        this.mv.visitInsn(95);
    }

    public void add(Type type) {
        this.mv.visitInsn(type.getOpcode(96));
    }

    public void sub(Type type) {
        this.mv.visitInsn(type.getOpcode(100));
    }

    public void mul(Type type) {
        this.mv.visitInsn(type.getOpcode(104));
    }

    public void div(Type type) {
        this.mv.visitInsn(type.getOpcode(108));
    }

    public void rem(Type type) {
        this.mv.visitInsn(type.getOpcode(112));
    }

    public void neg(Type type) {
        this.mv.visitInsn(type.getOpcode(116));
    }

    public void shl(Type type) {
        this.mv.visitInsn(type.getOpcode(120));
    }

    public void shr(Type type) {
        this.mv.visitInsn(type.getOpcode(122));
    }

    public void ushr(Type type) {
        this.mv.visitInsn(type.getOpcode(124));
    }

    public void and(Type type) {
        this.mv.visitInsn(type.getOpcode(126));
    }

    public void or(Type type) {
        this.mv.visitInsn(type.getOpcode(128));
    }

    public void xor(Type type) {
        this.mv.visitInsn(type.getOpcode(130));
    }

    public void iinc(int n2, int n3) {
        this.mv.visitIincInsn(n2, n3);
    }

    public void cast(Type type, Type type2) {
        if (type != type2) {
            if (type == Type.DOUBLE_TYPE) {
                if (type2 == Type.FLOAT_TYPE) {
                    this.mv.visitInsn(144);
                } else if (type2 == Type.LONG_TYPE) {
                    this.mv.visitInsn(143);
                } else {
                    this.mv.visitInsn(142);
                    this.cast(Type.INT_TYPE, type2);
                }
            } else if (type == Type.FLOAT_TYPE) {
                if (type2 == Type.DOUBLE_TYPE) {
                    this.mv.visitInsn(141);
                } else if (type2 == Type.LONG_TYPE) {
                    this.mv.visitInsn(140);
                } else {
                    this.mv.visitInsn(139);
                    this.cast(Type.INT_TYPE, type2);
                }
            } else if (type == Type.LONG_TYPE) {
                if (type2 == Type.DOUBLE_TYPE) {
                    this.mv.visitInsn(138);
                } else if (type2 == Type.FLOAT_TYPE) {
                    this.mv.visitInsn(137);
                } else {
                    this.mv.visitInsn(136);
                    this.cast(Type.INT_TYPE, type2);
                }
            } else if (type2 == Type.BYTE_TYPE) {
                this.mv.visitInsn(145);
            } else if (type2 == Type.CHAR_TYPE) {
                this.mv.visitInsn(146);
            } else if (type2 == Type.DOUBLE_TYPE) {
                this.mv.visitInsn(135);
            } else if (type2 == Type.FLOAT_TYPE) {
                this.mv.visitInsn(134);
            } else if (type2 == Type.LONG_TYPE) {
                this.mv.visitInsn(133);
            } else if (type2 == Type.SHORT_TYPE) {
                this.mv.visitInsn(147);
            }
        }
    }

    public void lcmp() {
        this.mv.visitInsn(148);
    }

    public void cmpl(Type type) {
        this.mv.visitInsn(type == Type.FLOAT_TYPE ? 149 : 151);
    }

    public void cmpg(Type type) {
        this.mv.visitInsn(type == Type.FLOAT_TYPE ? 150 : 152);
    }

    public void ifeq(Label label) {
        this.mv.visitJumpInsn(153, label);
    }

    public void ifne(Label label) {
        this.mv.visitJumpInsn(154, label);
    }

    public void iflt(Label label) {
        this.mv.visitJumpInsn(155, label);
    }

    public void ifge(Label label) {
        this.mv.visitJumpInsn(156, label);
    }

    public void ifgt(Label label) {
        this.mv.visitJumpInsn(157, label);
    }

    public void ifle(Label label) {
        this.mv.visitJumpInsn(158, label);
    }

    public void ificmpeq(Label label) {
        this.mv.visitJumpInsn(159, label);
    }

    public void ificmpne(Label label) {
        this.mv.visitJumpInsn(160, label);
    }

    public void ificmplt(Label label) {
        this.mv.visitJumpInsn(161, label);
    }

    public void ificmpge(Label label) {
        this.mv.visitJumpInsn(162, label);
    }

    public void ificmpgt(Label label) {
        this.mv.visitJumpInsn(163, label);
    }

    public void ificmple(Label label) {
        this.mv.visitJumpInsn(164, label);
    }

    public void ifacmpeq(Label label) {
        this.mv.visitJumpInsn(165, label);
    }

    public void ifacmpne(Label label) {
        this.mv.visitJumpInsn(166, label);
    }

    public void goTo(Label label) {
        this.mv.visitJumpInsn(167, label);
    }

    public void jsr(Label label) {
        this.mv.visitJumpInsn(168, label);
    }

    public void ret(int n2) {
        this.mv.visitVarInsn(169, n2);
    }

    public void tableswitch(int n2, int n3, Label label, Label ... labelArray) {
        this.mv.visitTableSwitchInsn(n2, n3, label, labelArray);
    }

    public void lookupswitch(Label label, int[] nArray, Label[] labelArray) {
        this.mv.visitLookupSwitchInsn(label, nArray, labelArray);
    }

    public void areturn(Type type) {
        this.mv.visitInsn(type.getOpcode(172));
    }

    public void getstatic(String string, String string2, String string3) {
        this.mv.visitFieldInsn(178, string, string2, string3);
    }

    public void putstatic(String string, String string2, String string3) {
        this.mv.visitFieldInsn(179, string, string2, string3);
    }

    public void getfield(String string, String string2, String string3) {
        this.mv.visitFieldInsn(180, string, string2, string3);
    }

    public void putfield(String string, String string2, String string3) {
        this.mv.visitFieldInsn(181, string, string2, string3);
    }

    public void invokevirtual(String string, String string2, String string3) {
        if (this.api >= 327680) {
            this.invokevirtual(string, string2, string3, false);
            return;
        }
        this.mv.visitMethodInsn(182, string, string2, string3);
    }

    public void invokevirtual(String string, String string2, String string3, boolean bl2) {
        if (this.api < 327680) {
            if (bl2) {
                throw new IllegalArgumentException("INVOKEVIRTUAL on interfaces require ASM 5");
            }
            this.invokevirtual(string, string2, string3);
            return;
        }
        this.mv.visitMethodInsn(182, string, string2, string3, bl2);
    }

    public void invokespecial(String string, String string2, String string3) {
        if (this.api >= 327680) {
            this.invokespecial(string, string2, string3, false);
            return;
        }
        this.mv.visitMethodInsn(183, string, string2, string3, false);
    }

    public void invokespecial(String string, String string2, String string3, boolean bl2) {
        if (this.api < 327680) {
            if (bl2) {
                throw new IllegalArgumentException("INVOKESPECIAL on interfaces require ASM 5");
            }
            this.invokespecial(string, string2, string3);
            return;
        }
        this.mv.visitMethodInsn(183, string, string2, string3, bl2);
    }

    public void invokestatic(String string, String string2, String string3) {
        if (this.api >= 327680) {
            this.invokestatic(string, string2, string3, false);
            return;
        }
        this.mv.visitMethodInsn(184, string, string2, string3, false);
    }

    public void invokestatic(String string, String string2, String string3, boolean bl2) {
        if (this.api < 327680) {
            if (bl2) {
                throw new IllegalArgumentException("INVOKESTATIC on interfaces require ASM 5");
            }
            this.invokestatic(string, string2, string3);
            return;
        }
        this.mv.visitMethodInsn(184, string, string2, string3, bl2);
    }

    public void invokeinterface(String string, String string2, String string3) {
        this.mv.visitMethodInsn(185, string, string2, string3, true);
    }

    public void invokedynamic(String string, String string2, Handle handle, Object[] objectArray) {
        this.mv.visitInvokeDynamicInsn(string, string2, handle, objectArray);
    }

    public void anew(Type type) {
        this.mv.visitTypeInsn(187, type.getInternalName());
    }

    public void newarray(Type type) {
        int n2;
        switch (type.getSort()) {
            case 1: {
                n2 = 4;
                break;
            }
            case 2: {
                n2 = 5;
                break;
            }
            case 3: {
                n2 = 8;
                break;
            }
            case 4: {
                n2 = 9;
                break;
            }
            case 5: {
                n2 = 10;
                break;
            }
            case 6: {
                n2 = 6;
                break;
            }
            case 7: {
                n2 = 11;
                break;
            }
            case 8: {
                n2 = 7;
                break;
            }
            default: {
                this.mv.visitTypeInsn(189, type.getInternalName());
                return;
            }
        }
        this.mv.visitIntInsn(188, n2);
    }

    public void arraylength() {
        this.mv.visitInsn(190);
    }

    public void athrow() {
        this.mv.visitInsn(191);
    }

    public void checkcast(Type type) {
        this.mv.visitTypeInsn(192, type.getInternalName());
    }

    public void instanceOf(Type type) {
        this.mv.visitTypeInsn(193, type.getInternalName());
    }

    public void monitorenter() {
        this.mv.visitInsn(194);
    }

    public void monitorexit() {
        this.mv.visitInsn(195);
    }

    public void multianewarray(String string, int n2) {
        this.mv.visitMultiANewArrayInsn(string, n2);
    }

    public void ifnull(Label label) {
        this.mv.visitJumpInsn(198, label);
    }

    public void ifnonnull(Label label) {
        this.mv.visitJumpInsn(199, label);
    }

    public void mark(Label label) {
        this.mv.visitLabel(label);
    }

    static {
        InstructionAdapter._clinit_();
        OBJECT_TYPE = Type.getType("Ljava/lang/Object;");
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

    private static void _clinit_() {
        class$org$objectweb$asm$commons$InstructionAdapter = InstructionAdapter.class$("org.objectweb.asm.commons.InstructionAdapter");
    }
}

