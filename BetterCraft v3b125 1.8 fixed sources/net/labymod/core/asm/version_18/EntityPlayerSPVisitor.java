/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core.asm.version_18;

import net.labymod.core.asm.LabyModCoreMod;
import net.labymod.core.asm.global.ClassEditor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class EntityPlayerSPVisitor
extends ClassEditor {
    private String swingItemName = LabyModCoreMod.isObfuscated() ? "bw" : "swingItem";
    private String onUpdateName = LabyModCoreMod.isObfuscated() ? "t_" : "onUpdate";

    public EntityPlayerSPVisitor() {
        super(ClassEditor.ClassEditorType.CLASS_VISITOR);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv2 = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals(this.swingItemName) && desc.equals("()V")) {
            return new MethodVisitor(262144, mv2){
                private boolean foundFirstVarInsn;
                private Label ifLabel;
                {
                    this.ifLabel = new Label();
                }

                @Override
                public void visitVarInsn(int opcode, int var) {
                    if (opcode == 25) {
                        if (this.foundFirstVarInsn) {
                            this.mv.visitMethodInsn(184, "BytecodeMethods", "shouldCancelAnimation", "()Z", false);
                            this.mv.visitJumpInsn(153, this.ifLabel);
                            this.mv.visitInsn(177);
                            this.mv.visitLabel(this.ifLabel);
                        } else {
                            this.foundFirstVarInsn = true;
                        }
                    }
                    this.mv.visitVarInsn(opcode, var);
                }
            };
        }
        if (name.equals(this.onUpdateName) && desc.equals("()V")) {
            return new MethodVisitor(262144, mv2){
                boolean added;
                {
                    this.added = false;
                }

                @Override
                public void visitVarInsn(int opcode, int var) {
                    if (!this.added && opcode == 25) {
                        this.added = true;
                        super.visitIntInsn(opcode, var);
                        super.visitMethodInsn(184, "BytecodeMethods", "onUpdateBlockBuild", "()V", false);
                        super.visitIntInsn(opcode, var);
                    } else {
                        super.visitIntInsn(opcode, var);
                    }
                }
            };
        }
        return mv2;
    }
}

