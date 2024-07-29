/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core.asm.global;

import net.labymod.core.asm.LabyModTransformer;
import net.labymod.core.asm.global.ClassEditor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class GuiScreenVisitor
extends ClassEditor {
    private String drawWorldBackgroundName = LabyModTransformer.getMappingImplementation().getDrawWorldBackgroundName();
    private String drawScreenName = LabyModTransformer.getMappingImplementation().getGuiScreenDrawScreenName();

    public GuiScreenVisitor() {
        super(ClassEditor.ClassEditorType.CLASS_VISITOR);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv2 = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals(this.drawWorldBackgroundName)) {
            return new MethodVisitor(262144, mv2){
                private Label guiBackgroundLabel;

                @Override
                public void visitJumpInsn(int opcode, Label label) {
                    this.mv.visitJumpInsn(opcode, label);
                    if (opcode == 198) {
                        this.guiBackgroundLabel = new Label();
                        this.mv.visitMethodInsn(184, "BytecodeMethods", "isGuiBackground", "()Z", false);
                        this.mv.visitJumpInsn(153, this.guiBackgroundLabel);
                    }
                }

                @Override
                public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                    this.mv.visitMethodInsn(opcode, owner, name, desc, itf);
                    if (opcode == 182 && this.guiBackgroundLabel != null) {
                        this.mv.visitLabel(this.guiBackgroundLabel);
                        this.guiBackgroundLabel = null;
                    }
                }
            };
        }
        if (name.equals(this.drawScreenName) && desc.equals("(IIF)V")) {
            return new MethodVisitor(262144, mv2){

                @Override
                public void visitInsn(int opcode) {
                    if (opcode == 177) {
                        this.mv.visitIntInsn(21, 1);
                        this.mv.visitIntInsn(21, 2);
                        this.mv.visitIntInsn(23, 3);
                        this.mv.visitMethodInsn(184, "BytecodeMethods", "drawMenuOverlay", "(IIF)V", false);
                    }
                    super.visitInsn(opcode);
                }
            };
        }
        return mv2;
    }
}

