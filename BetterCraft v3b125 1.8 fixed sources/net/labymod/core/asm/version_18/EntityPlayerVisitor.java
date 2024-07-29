/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core.asm.version_18;

import net.labymod.core.asm.LabyModCoreMod;
import net.labymod.core.asm.LabyModTransformer;
import net.labymod.core.asm.global.ClassEditor;
import org.objectweb.asm.MethodVisitor;

public class EntityPlayerVisitor
extends ClassEditor {
    private String entityName = LabyModTransformer.getMappingImplementation().getEntityClassName();
    private String attackTargetEntityWithCurrentItemName = LabyModCoreMod.isObfuscated() ? "f" : "attackTargetEntityWithCurrentItem";

    public EntityPlayerVisitor() {
        super(ClassEditor.ClassEditorType.CLASS_VISITOR);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv2 = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals(this.attackTargetEntityWithCurrentItemName) && desc.equals("(L" + this.entityName + ";)V")) {
            return new MethodVisitor(262144, mv2){
                private int invoked;
                private boolean injected;
                {
                    this.invoked = 0;
                    this.injected = false;
                }

                @Override
                public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                    super.visitMethodInsn(opcode, owner, name, desc, itf);
                    if (opcode == 184 && !this.injected && this.invoked < 2 && desc.endsWith(";)I")) {
                        ++this.invoked;
                    }
                }

                @Override
                public void visitVarInsn(int opcode, int var) {
                    super.visitVarInsn(opcode, var);
                    if (opcode == 54 && this.invoked == 2 && !this.injected) {
                        this.injected = true;
                        super.visitIntInsn(21, 5);
                        super.visitMethodInsn(184, "BytecodeMethods", "modifyCriticalHit", "(Z)Z", false);
                        super.visitIntInsn(54, 5);
                        super.visitIntInsn(23, 4);
                        super.visitMethodInsn(184, "BytecodeMethods", "modifyEnchantmentCritical", "(F)F", false);
                        super.visitIntInsn(56, 4);
                    }
                }
            };
        }
        return mv2;
    }
}

