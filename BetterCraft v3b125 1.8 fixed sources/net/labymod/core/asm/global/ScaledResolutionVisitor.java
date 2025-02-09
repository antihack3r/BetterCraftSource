/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core.asm.global;

import net.labymod.core.asm.global.ClassEditor;
import org.objectweb.asm.MethodVisitor;

public class ScaledResolutionVisitor
extends ClassEditor {
    public ScaledResolutionVisitor() {
        super(ClassEditor.ClassEditorType.CLASS_VISITOR);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv2 = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals("<init>")) {
            return new MethodVisitor(262144, mv2){
                boolean flag;
                {
                    this.flag = true;
                }

                @Override
                public void visitInsn(int opcode) {
                    if (opcode == 135) {
                        return;
                    }
                    if (opcode == 111) {
                        super.visitInsn(this.flag ? 4 : 3);
                        super.visitMethodInsn(184, "BytecodeMethods", "getCustomScale", "(IIZ)D", false);
                        this.flag = false;
                        return;
                    }
                    super.visitInsn(opcode);
                }
            };
        }
        return mv2;
    }
}

