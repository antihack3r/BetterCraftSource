/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core.asm.global;

import net.labymod.core.asm.LabyModTransformer;
import net.labymod.core.asm.global.ClassEditor;
import org.objectweb.asm.MethodVisitor;

public class MainVisitor
extends ClassEditor {
    public MainVisitor() {
        super(ClassEditor.ClassEditorType.CLASS_VISITOR);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv2 = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals("main")) {
            return new MethodVisitor(262144, mv2){
                private String lastInvokeOwner;

                @Override
                public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                    super.visitMethodInsn(opcode, owner, name, desc, itf);
                    if (opcode == 182) {
                        this.lastInvokeOwner = owner;
                    }
                }

                @Override
                public void visitInsn(int opcode) {
                    super.visitInsn(opcode);
                    if (opcode == 177 && this.lastInvokeOwner != null) {
                        LabyModTransformer.resolveMinecraftClass(this.lastInvokeOwner);
                    }
                }
            };
        }
        return mv2;
    }
}

