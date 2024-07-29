/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core.asm.global;

import net.labymod.core.asm.LabyModTransformer;
import net.labymod.core.asm.global.ClassEditor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class GuiSlotVisitor
extends ClassEditor {
    private String overlayBackgroundName = LabyModTransformer.getMappingImplementation().getGuiSlotOverlayBackgroundName();
    private String guiName = LabyModTransformer.getMappingImplementation().getGuiName();
    private String optionsBackgroundName = LabyModTransformer.getMappingImplementation().getOptionsBackgroundName();
    private String textureManagerName = LabyModTransformer.getMappingImplementation().getTextureManagerName();
    private String bindTextureName = LabyModTransformer.getMappingImplementation().getBindTextureName();
    private String resourceLocationName = LabyModTransformer.getMappingImplementation().getResourceLocationName();
    private String tessellatorName = LabyModTransformer.getMappingImplementation().getTessellatorName();
    private String drawName = LabyModTransformer.getMappingImplementation().getDrawName();

    public GuiSlotVisitor() {
        super(ClassEditor.ClassEditorType.CLASS_VISITOR);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv2 = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals(this.overlayBackgroundName) && desc.equals("(IIII)V")) {
            return mv2;
        }
        return new MethodVisitor(262144, mv2){
            private boolean detectedOptionsBackgroundInstruction;
            private Label label;

            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                super.visitFieldInsn(opcode, owner, name, desc);
                if (opcode == 178 && owner.equals(GuiSlotVisitor.this.guiName) && name.equals(GuiSlotVisitor.this.optionsBackgroundName)) {
                    this.detectedOptionsBackgroundInstruction = true;
                }
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                if (this.detectedOptionsBackgroundInstruction && opcode == 182 && owner.equals(GuiSlotVisitor.this.textureManagerName) && name.equals(GuiSlotVisitor.this.bindTextureName) && desc.equals("(L" + GuiSlotVisitor.this.resourceLocationName + ";)V")) {
                    this.detectedOptionsBackgroundInstruction = false;
                    this.mv.visitMethodInsn(184, "BytecodeMethods", "shouldRenderMultiplayerBackground", "()Z", false);
                    this.label = new Label();
                    this.mv.visitJumpInsn(153, this.label);
                }
                if (this.label != null && owner.equals(GuiSlotVisitor.this.tessellatorName) && name.equals(GuiSlotVisitor.this.drawName) && desc.equals("()V")) {
                    this.mv.visitLabel(this.label);
                    this.label = null;
                }
            }
        };
    }
}

