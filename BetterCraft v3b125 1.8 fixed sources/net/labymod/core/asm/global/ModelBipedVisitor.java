/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core.asm.global;

import net.labymod.core.asm.global.ClassEditor;
import org.objectweb.asm.MethodVisitor;

public class ModelBipedVisitor
extends ClassEditor {
    public ModelBipedVisitor() {
        super(ClassEditor.ClassEditorType.CLASS_VISITOR);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv2 = super.visitMethod(access, name, desc, signature, exceptions);
        if (desc.startsWith("(FFFFFFL") && desc.endsWith(";)V")) {
            return new MethodVisitor(262144, mv2){
                private boolean preAdded;
                {
                    this.preAdded = false;
                }

                @Override
                public void visitVarInsn(int opcode, int var) {
                    super.visitVarInsn(opcode, var);
                    if (!this.preAdded && opcode == 25 && var == 0) {
                        this.preAdded = true;
                        super.visitVarInsn(25, 7);
                        super.visitMethodInsn(184, "BytecodeMethods", "transformModelPre", "(Ljava/lang/Object;Ljava/lang/Object;)V", false);
                        super.visitVarInsn(25, 0);
                    }
                }

                @Override
                public void visitInsn(int opcode) {
                    if (opcode == 177) {
                        super.visitVarInsn(25, 0);
                        super.visitVarInsn(25, 7);
                        super.visitMethodInsn(184, "BytecodeMethods", "transformModelPost", "(Ljava/lang/Object;Ljava/lang/Object;)V", false);
                    }
                    super.visitInsn(opcode);
                }
            };
        }
        return mv2;
    }
}

