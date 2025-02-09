/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core.asm.version_18;

import net.labymod.core.asm.LabyModCoreMod;
import net.labymod.core.asm.LabyModTransformer;
import net.labymod.core.asm.global.ClassEditor;
import org.objectweb.asm.MethodVisitor;

public class ItemStackVisitor
extends ClassEditor {
    private String itemStackName = LabyModTransformer.getMappingImplementation().getItemStackName();
    private String getIsItemStackEqualName = LabyModCoreMod.isObfuscated() ? "c" : "getIsItemStackEqual";

    public ItemStackVisitor() {
        super(ClassEditor.ClassEditorType.CLASS_VISITOR);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv2 = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals(this.getIsItemStackEqualName) && desc.equals("(L" + this.itemStackName + ";)Z")) {
            return new MethodVisitor(262144, mv2){

                @Override
                public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                    super.visitMethodInsn(opcode, owner, name, desc, itf);
                    if (opcode == 183) {
                        super.visitVarInsn(25, 0);
                        super.visitVarInsn(25, 1);
                        super.visitMethodInsn(184, "BytecodeMethods", "isItemStackEqual", "(ZL" + ItemStackVisitor.this.itemStackName + ";L" + ItemStackVisitor.this.itemStackName + ";)Z", false);
                    }
                }
            };
        }
        return mv2;
    }
}

