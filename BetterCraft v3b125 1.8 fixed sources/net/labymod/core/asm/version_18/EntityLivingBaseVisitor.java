/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core.asm.version_18;

import net.labymod.core.asm.LabyModCoreMod;
import net.labymod.core.asm.LabyModTransformer;
import net.labymod.core.asm.global.ClassEditor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class EntityLivingBaseVisitor
extends ClassEditor {
    private String entityPlayerSPName = LabyModCoreMod.isObfuscated() ? "bew" : "net/minecraft/client/entity/EntityPlayerSP";
    private String entityName = LabyModCoreMod.isObfuscated() ? "pk" : "net/minecraft/entity/Entity";
    private String getLookInEntityLivingBaseName = LabyModCoreMod.isObfuscated() ? "d" : "getLook";
    private String vec3Name = LabyModCoreMod.isObfuscated() ? "aui" : "net/minecraft/util/Vec3";
    private String lastAttackerTimeName = LabyModTransformer.getMappingImplementation().getLastAttackerTimeName();

    public EntityLivingBaseVisitor() {
        super(ClassEditor.ClassEditorType.CLASS_VISITOR);
    }

    @Override
    public MethodVisitor visitMethod(int access, final String name, final String desc, String signature, String[] exceptions) {
        MethodVisitor mv2 = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals(this.getLookInEntityLivingBaseName) && desc.equals("(F)L" + this.vec3Name + ";")) {
            return new MethodVisitor(262144, mv2){

                @Override
                public void visitCode() {
                    Label label = new Label();
                    this.mv.visitIntInsn(25, 0);
                    this.mv.visitTypeInsn(193, EntityLivingBaseVisitor.this.entityPlayerSPName);
                    this.mv.visitJumpInsn(153, label);
                    this.mv.visitMethodInsn(184, "BytecodeMethods", "isCrosshairsyncEnabled", "()Z", false);
                    this.mv.visitJumpInsn(153, label);
                    this.mv.visitIntInsn(25, 0);
                    this.mv.visitIntInsn(23, 1);
                    this.mv.visitMethodInsn(183, EntityLivingBaseVisitor.this.entityName, name, desc, false);
                    this.mv.visitInsn(176);
                    this.mv.visitLabel(label);
                    super.visitCode();
                }
            };
        }
        if (!desc.endsWith("V")) {
            return mv2;
        }
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
}

