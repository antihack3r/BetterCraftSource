/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core.asm.global;

import net.labymod.core.asm.LabyModTransformer;
import net.labymod.core.asm.global.ClassEditor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class ServerListEntryNormalVisitor
extends ClassEditor {
    private String drawEntryName = LabyModTransformer.getMappingImplementation().getDrawEntryName();
    private String serverDataName;

    public ServerListEntryNormalVisitor() {
        super(ClassEditor.ClassEditorType.CLASS_VISITOR);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv2 = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals("<init>")) {
            this.serverDataName = desc.split(";")[1].substring(1);
        }
        if (name.equals(this.drawEntryName) && (desc.equals("(IIIIIIIZ)V") || desc.equals("(IIIIIIIZF)V"))) {
            return new MethodVisitor(262144, mv2){
                private Label label;
                private boolean insertedJumpNode;
                private boolean insertedLabel;
                {
                    this.label = new Label();
                }

                @Override
                public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                    if (opcode == 181 && !this.insertedJumpNode) {
                        Label startLabel = new Label();
                        super.visitFieldInsn(opcode, owner, name, desc);
                        this.visitVarInsn(25, 0);
                        this.visitFieldInsn(180, LabyModTransformer.getMappingImplementation().getServerListEntryNormalName(), LabyModTransformer.getMappingImplementation().getServerName(), "L" + ServerListEntryNormalVisitor.this.serverDataName + ";");
                        this.visitFieldInsn(180, ServerListEntryNormalVisitor.this.serverDataName, LabyModTransformer.getMappingImplementation().getServerMotdName(), "Ljava/lang/String;");
                        this.visitJumpInsn(198, startLabel);
                        this.visitMethodInsn(184, "BytecodeMethods", "shouldKeepServerData", "()Z", false);
                        this.visitJumpInsn(153, this.label);
                        this.visitLabel(startLabel);
                        this.insertedJumpNode = true;
                        return;
                    }
                    if (opcode == 178 && this.insertedJumpNode && !this.insertedLabel) {
                        this.visitLabel(this.label);
                        super.visitFieldInsn(opcode, owner, name, desc);
                        this.insertedLabel = true;
                        return;
                    }
                    super.visitFieldInsn(opcode, owner, name, desc);
                }
            };
        }
        return mv2;
    }
}

