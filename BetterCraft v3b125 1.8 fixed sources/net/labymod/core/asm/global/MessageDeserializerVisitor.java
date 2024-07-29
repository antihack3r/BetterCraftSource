/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core.asm.global;

import net.labymod.core.asm.global.ClassEditor;
import org.objectweb.asm.MethodVisitor;

public class MessageDeserializerVisitor
extends ClassEditor {
    public MessageDeserializerVisitor() {
        super(ClassEditor.ClassEditorType.CLASS_VISITOR);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv2 = super.visitMethod(access, name, desc, signature, exceptions);
        if (desc.endsWith(";Ljava/util/List;)V")) {
            return new MethodVisitor(262144, mv2){
                private boolean add;
                {
                    this.add = false;
                }

                @Override
                public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                    super.visitMethodInsn(opcode, owner, name, desc, itf);
                    if (opcode == 185 && owner.equals("java/util/List") && name.equals("add") && desc.equals("(Ljava/lang/Object;)Z")) {
                        this.add = true;
                    }
                }

                @Override
                public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                    if (opcode == 178 && this.add) {
                        this.add = false;
                        super.visitIntInsn(25, 6);
                        super.visitMethodInsn(184, "BytecodeMethods", "onIncomingPacket", "(Ljava/lang/Object;)V", false);
                    }
                    super.visitFieldInsn(opcode, owner, name, desc);
                }
            };
        }
        return mv2;
    }
}

