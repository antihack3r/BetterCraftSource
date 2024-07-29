/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core.asm.global;

import net.labymod.core.asm.LabyModTransformer;
import net.labymod.core.asm.global.ClassEditor;
import org.objectweb.asm.MethodVisitor;

public class ServerListEntryNormal$1Visitor
extends ClassEditor {
    private String serverDataName = LabyModTransformer.getMappingImplementation().getServerDataName();
    private String serverListEntryNormalName = LabyModTransformer.getMappingImplementation().getServerListEntryNormalName();
    private String thisClassName = String.valueOf(this.serverListEntryNormalName) + "$1";

    public ServerListEntryNormal$1Visitor() {
        super(ClassEditor.ClassEditorType.CLASS_VISITOR);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv2 = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals("run")) {
            return new MethodVisitor(262144, mv2){

                @Override
                public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                    super.visitFieldInsn(opcode, owner, name, desc);
                    if (owner.equals(ServerListEntryNormal$1Visitor.this.serverDataName) && name.equals(LabyModTransformer.getMappingImplementation().getServerMotdName())) {
                        this.visitVarInsn(25, 0);
                        this.visitFieldInsn(180, ServerListEntryNormal$1Visitor.this.thisClassName, "this$0", "L" + ServerListEntryNormal$1Visitor.this.serverListEntryNormalName + ";");
                        this.visitMethodInsn(184, ServerListEntryNormal$1Visitor.this.serverListEntryNormalName, "access$000", "(L" + ServerListEntryNormal$1Visitor.this.serverListEntryNormalName + ";)L" + ServerListEntryNormal$1Visitor.this.serverDataName + ";", false);
                        this.visitLdcInsn("");
                        this.visitFieldInsn(181, ServerListEntryNormal$1Visitor.this.serverDataName, LabyModTransformer.getMappingImplementation().getPopulationInfoName(), "Ljava/lang/String;");
                    }
                }
            };
        }
        return mv2;
    }
}

