/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core.asm.version_112;

import net.labymod.core.asm.LabyModCoreMod;
import net.labymod.core.asm.LabyModTransformer;
import net.labymod.core.asm.global.ClassEditor;
import org.objectweb.asm.MethodVisitor;

public class EntityLivingBaseVisitor
extends ClassEditor {
    private String entityName = LabyModCoreMod.isObfuscated() ? "vg" : "net/minecraft/entity/Entity";
    private String lastAttackerTimeName = LabyModTransformer.getMappingImplementation().getLastAttackerTimeName();

    public EntityLivingBaseVisitor() {
        super(ClassEditor.ClassEditorType.CLASS_VISITOR);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv2 = super.visitMethod(access, name, desc, signature, exceptions);
        if (desc.equals("()V")) {
            return new MethodVisitor(262144, mv2){
                private boolean canModify;
                private boolean modified;
                {
                    this.canModify = false;
                    this.modified = false;
                }

                @Override
                public void visitLdcInsn(Object cst) {
                    super.visitLdcInsn(cst);
                    if (!this.modified && this.canModify && cst instanceof Float && ((Float)cst).floatValue() == 180.0f) {
                        this.mv.visitMethodInsn(184, "BytecodeMethods", "subtractBackwardsWalkingAnimation", "(F)F", false);
                        this.canModify = false;
                        this.modified = true;
                    }
                }

                @Override
                public void visitVarInsn(int opcode, int var) {
                    super.visitVarInsn(opcode, var);
                    if (!this.modified && opcode == 23) {
                        this.canModify = true;
                    }
                }
            };
        }
        if (desc.endsWith("V")) {
            return new MethodVisitor(262144, mv2){

                @Override
                public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                    super.visitFieldInsn(opcode, owner, name, desc);
                    if (opcode == 181 && name.equals(EntityLivingBaseVisitor.this.lastAttackerTimeName)) {
                        this.mv.visitVarInsn(25, 1);
                        this.mv.visitMethodInsn(184, "BytecodeMethods", "onAttack", "(L" + EntityLivingBaseVisitor.this.entityName + ";)V", false);
                    }
                }
            };
        }
        return mv2;
    }
}

