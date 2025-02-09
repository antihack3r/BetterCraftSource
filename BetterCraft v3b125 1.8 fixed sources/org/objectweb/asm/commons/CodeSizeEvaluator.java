/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.commons;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class CodeSizeEvaluator
extends MethodVisitor
implements Opcodes {
    private int minSize;
    private int maxSize;

    public CodeSizeEvaluator(MethodVisitor methodVisitor) {
        this(327680, methodVisitor);
    }

    protected CodeSizeEvaluator(int n2, MethodVisitor methodVisitor) {
        super(n2, methodVisitor);
    }

    public int getMinSize() {
        return this.minSize;
    }

    public int getMaxSize() {
        return this.maxSize;
    }

    public void visitInsn(int n2) {
        ++this.minSize;
        ++this.maxSize;
        if (this.mv != null) {
            this.mv.visitInsn(n2);
        }
    }

    public void visitIntInsn(int n2, int n3) {
        if (n2 == 17) {
            this.minSize += 3;
            this.maxSize += 3;
        } else {
            this.minSize += 2;
            this.maxSize += 2;
        }
        if (this.mv != null) {
            this.mv.visitIntInsn(n2, n3);
        }
    }

    public void visitVarInsn(int n2, int n3) {
        if (n3 < 4 && n2 != 169) {
            ++this.minSize;
            ++this.maxSize;
        } else if (n3 >= 256) {
            this.minSize += 4;
            this.maxSize += 4;
        } else {
            this.minSize += 2;
            this.maxSize += 2;
        }
        if (this.mv != null) {
            this.mv.visitVarInsn(n2, n3);
        }
    }

    public void visitTypeInsn(int n2, String string) {
        this.minSize += 3;
        this.maxSize += 3;
        if (this.mv != null) {
            this.mv.visitTypeInsn(n2, string);
        }
    }

    public void visitFieldInsn(int n2, String string, String string2, String string3) {
        this.minSize += 3;
        this.maxSize += 3;
        if (this.mv != null) {
            this.mv.visitFieldInsn(n2, string, string2, string3);
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
        if (n2 == 185) {
            this.minSize += 5;
            this.maxSize += 5;
        } else {
            this.minSize += 3;
            this.maxSize += 3;
        }
        if (this.mv != null) {
            this.mv.visitMethodInsn(n2, string, string2, string3, bl2);
        }
    }

    public void visitInvokeDynamicInsn(String string, String string2, Handle handle, Object ... objectArray) {
        this.minSize += 5;
        this.maxSize += 5;
        if (this.mv != null) {
            this.mv.visitInvokeDynamicInsn(string, string2, handle, objectArray);
        }
    }

    public void visitJumpInsn(int n2, Label label) {
        this.minSize += 3;
        this.maxSize = n2 == 167 || n2 == 168 ? (this.maxSize += 5) : (this.maxSize += 8);
        if (this.mv != null) {
            this.mv.visitJumpInsn(n2, label);
        }
    }

    public void visitLdcInsn(Object object) {
        if (object instanceof Long || object instanceof Double) {
            this.minSize += 3;
            this.maxSize += 3;
        } else {
            this.minSize += 2;
            this.maxSize += 3;
        }
        if (this.mv != null) {
            this.mv.visitLdcInsn(object);
        }
    }

    public void visitIincInsn(int n2, int n3) {
        if (n2 > 255 || n3 > 127 || n3 < -128) {
            this.minSize += 6;
            this.maxSize += 6;
        } else {
            this.minSize += 3;
            this.maxSize += 3;
        }
        if (this.mv != null) {
            this.mv.visitIincInsn(n2, n3);
        }
    }

    public void visitTableSwitchInsn(int n2, int n3, Label label, Label ... labelArray) {
        this.minSize += 13 + labelArray.length * 4;
        this.maxSize += 16 + labelArray.length * 4;
        if (this.mv != null) {
            this.mv.visitTableSwitchInsn(n2, n3, label, labelArray);
        }
    }

    public void visitLookupSwitchInsn(Label label, int[] nArray, Label[] labelArray) {
        this.minSize += 9 + nArray.length * 8;
        this.maxSize += 12 + nArray.length * 8;
        if (this.mv != null) {
            this.mv.visitLookupSwitchInsn(label, nArray, labelArray);
        }
    }

    public void visitMultiANewArrayInsn(String string, int n2) {
        this.minSize += 4;
        this.maxSize += 4;
        if (this.mv != null) {
            this.mv.visitMultiANewArrayInsn(string, n2);
        }
    }
}

