/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core.asm.global;

import net.labymod.core.asm.LabyModTransformer;
import net.labymod.core.asm.global.ClassEditor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class ServerPingerVisitor
extends ClassEditor {
    private String serverDataName = LabyModTransformer.getMappingImplementation().getServerDataName();

    public ServerPingerVisitor() {
        super(ClassEditor.ClassEditorType.CLASS_VISITOR);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv2 = super.visitMethod(access, name, desc, signature, exceptions);
        if (access == 1 && desc.equals("(L" + this.serverDataName + ";)V")) {
            return new MethodVisitor(262144, mv2){
                private boolean insertedIfEq;
                private boolean visitedAConstNull;
                private Label label;
                {
                    this.label = new Label();
                }

                @Override
                public void visitInsn(int opcode) {
                    super.visitInsn(opcode);
                    if (opcode == 87 && !this.insertedIfEq) {
                        Label startLabel = new Label();
                        this.visitVarInsn(25, 1);
                        this.visitFieldInsn(180, ServerPingerVisitor.this.serverDataName, LabyModTransformer.getMappingImplementation().getPingToServerName(), "J");
                        this.visitLdcInsn(new Long(-2L));
                        this.visitInsn(148);
                        this.visitJumpInsn(153, startLabel);
                        this.visitMethodInsn(184, "BytecodeMethods", "shouldKeepServerData", "()Z", false);
                        this.visitJumpInsn(153, this.label);
                        this.visitLabel(startLabel);
                        this.insertedIfEq = true;
                    }
                    if (this.insertedIfEq && opcode == 1) {
                        this.visitedAConstNull = true;
                    }
                }

                @Override
                public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                    super.visitFieldInsn(opcode, owner, name, desc);
                    if (opcode == 181 && this.visitedAConstNull) {
                        this.visitedAConstNull = false;
                        this.visitLabel(this.label);
                    }
                }
            };
        }
        return mv2;
    }
}

