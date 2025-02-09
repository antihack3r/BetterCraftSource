/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core.asm.global;

import net.labymod.core.asm.LabyModTransformer;
import net.labymod.core.asm.global.ClassEditor;
import org.objectweb.asm.MethodVisitor;

public class GuiDisconnectedVisitor
extends ClassEditor {
    private String initGuiName = LabyModTransformer.getMappingImplementation().getInitGuiName();
    private String messageName = LabyModTransformer.getMappingImplementation().getGuiDisconnectedMessageName();
    private String iChatComponentName = LabyModTransformer.getMappingImplementation().getChatComponentClassName();
    private String guiDisconnectedName = LabyModTransformer.getMappingImplementation().getGuiDisconnectedName();
    private String getUnformattedTextForChatName = LabyModTransformer.getMappingImplementation().getGetUnformattedTextForChatName();
    private String parentScreenName = LabyModTransformer.getMappingImplementation().getParentScreenName();
    private String guiScreenName = LabyModTransformer.getMappingImplementation().getGuiScreenName();

    public GuiDisconnectedVisitor() {
        super(ClassEditor.ClassEditorType.CLASS_VISITOR);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv2 = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals(this.initGuiName) && desc.equals("()V")) {
            return new MethodVisitor(262144, mv2){

                @Override
                public void visitInsn(int opcode) {
                    if (opcode == 177) {
                        this.mv.visitVarInsn(25, 0);
                        this.mv.visitFieldInsn(180, GuiDisconnectedVisitor.this.guiDisconnectedName, GuiDisconnectedVisitor.this.messageName, "L" + GuiDisconnectedVisitor.this.iChatComponentName + ";");
                        this.mv.visitMethodInsn(185, GuiDisconnectedVisitor.this.iChatComponentName, GuiDisconnectedVisitor.this.getUnformattedTextForChatName, "()Ljava/lang/String;", true);
                        this.mv.visitVarInsn(25, 0);
                        this.mv.visitFieldInsn(180, GuiDisconnectedVisitor.this.guiDisconnectedName, GuiDisconnectedVisitor.this.parentScreenName, "L" + GuiDisconnectedVisitor.this.guiScreenName + ";");
                        this.mv.visitMethodInsn(184, "net/labymod/gui/GuiWebPanel", "open", "(Ljava/lang/String;L" + GuiDisconnectedVisitor.this.guiScreenName + ";)V", false);
                    }
                    super.visitInsn(opcode);
                }
            };
        }
        return mv2;
    }
}

