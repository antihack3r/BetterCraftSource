/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core.asm.global;

import net.labymod.core.asm.LabyModCoreMod;
import net.labymod.core.asm.LabyModTransformer;
import net.labymod.core.asm.global.ClassEditor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class GuiContainerVisitor
extends ClassEditor {
    private String itemStackName = LabyModTransformer.getMappingImplementation().getItemStackName();
    private String mouseReleasedName = LabyModTransformer.getMappingImplementation().getGuiContainerMouseReleasedName();
    private String slotName = LabyModTransformer.getMappingImplementation().getSlotName();
    private String getStackName = LabyModTransformer.getMappingImplementation().getSlotGetStackName();

    public GuiContainerVisitor() {
        super(ClassEditor.ClassEditorType.CLASS_VISITOR);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv2 = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals(this.mouseReleasedName) && desc.equals("(III)V")) {
            return new MethodVisitor(262144, mv2){
                private boolean invokeStaticCalled;
                private boolean getFieldCalled;
                {
                    this.invokeStaticCalled = false;
                    this.getFieldCalled = false;
                }

                @Override
                public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                    super.visitFieldInsn(opcode, owner, name, desc);
                    this.getFieldCalled = opcode == 180 && desc.equals("L" + GuiContainerVisitor.this.itemStackName + ";");
                }

                @Override
                public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                    super.visitMethodInsn(opcode, owner, name, desc, itf);
                    this.invokeStaticCalled = opcode == 184;
                    if (!this.invokeStaticCalled) {
                        this.getFieldCalled = false;
                    }
                }

                @Override
                public void visitJumpInsn(int opcode, Label label) {
                    super.visitJumpInsn(opcode, label);
                    if (opcode != 153) {
                        this.invokeStaticCalled = false;
                        this.getFieldCalled = false;
                    }
                    if (opcode == 153 && this.invokeStaticCalled && this.getFieldCalled) {
                        this.invokeStaticCalled = false;
                        this.getFieldCalled = false;
                        super.visitIntInsn(25, LabyModCoreMod.isObfuscated() || LabyModCoreMod.isForge() ? 10 : 9);
                        super.visitMethodInsn(182, GuiContainerVisitor.this.slotName, GuiContainerVisitor.this.getStackName, "()L" + GuiContainerVisitor.this.itemStackName + ";", false);
                        super.visitMethodInsn(184, "BytecodeMethods", "allowedToShiftAllItems", "(L" + GuiContainerVisitor.this.itemStackName + ";)Z", false);
                        super.visitJumpInsn(153, label);
                    }
                }
            };
        }
        return mv2;
    }
}

