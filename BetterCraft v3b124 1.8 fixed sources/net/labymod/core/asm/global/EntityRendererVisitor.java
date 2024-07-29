/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core.asm.global;

import net.labymod.core.asm.LabyModCoreMod;
import net.labymod.core.asm.LabyModTransformer;
import net.labymod.core.asm.global.ClassEditor;
import net.labymod.main.Source;
import org.objectweb.asm.MethodVisitor;

public class EntityRendererVisitor
extends ClassEditor {
    private String entityName = LabyModTransformer.getMappingImplementation().getEntityClassName();
    private String orientCameraName = LabyModTransformer.getMappingImplementation().getOrientCameraName();
    private String renderWorldDirectionsName = LabyModCoreMod.isObfuscated() ? "h" : "renderWorldDirections";

    public EntityRendererVisitor() {
        super(ClassEditor.ClassEditorType.CLASS_VISITOR);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv2 = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals(this.orientCameraName) && desc.equals("(F)V")) {
            return new MethodVisitor(262144, mv2){

                @Override
                public void visitInsn(int opcode) {
                    if (opcode == 177) {
                        super.visitIntInsn(25, 2);
                        super.visitInsn(3);
                        super.visitMethodInsn(184, "BytecodeMethods", "translateEyeHeight", "(L" + EntityRendererVisitor.this.entityName + ";Z)V", false);
                    }
                    super.visitInsn(opcode);
                }
            };
        }
        if (!Source.ABOUT_MC_VERSION.startsWith("1.8") && desc.equals("(L" + this.entityName + ";)V")) {
            return new MethodVisitor(262144, mv2){

                @Override
                public void visitInsn(int opcode) {
                    if (opcode == 177) {
                        super.visitIntInsn(25, 1);
                        super.visitMethodInsn(184, "BytecodeMethods", "onLoadEntityShader", "(L" + EntityRendererVisitor.this.entityName + ";)V", false);
                    }
                    super.visitInsn(opcode);
                }
            };
        }
        if (Source.ABOUT_MC_VERSION.startsWith("1.8") && name.equals(this.renderWorldDirectionsName) && desc.equals("(F)V")) {
            return new MethodVisitor(262144, mv2){
                private int called;
                {
                    this.called = 0;
                }

                @Override
                public void visitTypeInsn(int opcode, String type) {
                    if (this.called == 0 && opcode == 187) {
                        super.visitIntInsn(25, 2);
                        super.visitInsn(4);
                        super.visitMethodInsn(184, "BytecodeMethods", "translateEyeHeight", "(L" + EntityRendererVisitor.this.entityName + ";Z)V", false);
                    }
                    super.visitTypeInsn(opcode, type);
                    ++this.called;
                }
            };
        }
        return mv2;
    }
}

