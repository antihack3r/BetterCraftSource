/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core.asm.global;

import net.labymod.core.asm.LabyModTransformer;
import net.labymod.core.asm.global.ClassEditor;
import net.labymod.main.Source;
import org.objectweb.asm.MethodVisitor;

public class ItemRendererVisitor
extends ClassEditor {
    private static final boolean MC18 = Source.ABOUT_MC_VERSION.startsWith("1.8");
    private String transformFirstPersonItemName = LabyModTransformer.getMappingImplementation().getTransformFirstPersonItemName();
    private String renderItemInFirstPersonName = LabyModTransformer.getMappingImplementation().getRenderItemInFirstPersonName();
    private String itemStackName = LabyModTransformer.getMappingImplementation().getItemStackName();
    private String itemRendererName = LabyModTransformer.getMappingImplementation().getItemRendererName();
    private String itemToRenderName = LabyModTransformer.getMappingImplementation().getItemToRenderName();
    private String pushMatrixName = LabyModTransformer.getMappingImplementation().getPushMatrixName();

    public ItemRendererVisitor() {
        super(ClassEditor.ClassEditorType.CLASS_VISITOR);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv2 = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals(this.transformFirstPersonItemName) && !(MC18 ? !desc.equals("(FF)V") : !desc.startsWith("(L") || !desc.endsWith(";F)V"))) {
            return new MethodVisitor(262144, mv2){

                @Override
                public void visitCode() {
                    this.mv.visitVarInsn(25, 0);
                    this.mv.visitFieldInsn(180, ItemRendererVisitor.this.itemRendererName, ItemRendererVisitor.this.itemToRenderName, "L" + ItemRendererVisitor.this.itemStackName + ";");
                    this.mv.visitMethodInsn(184, "BytecodeMethods", "transformFirstPersonItem", "(L" + ItemRendererVisitor.this.itemStackName + ";)V", false);
                    super.visitCode();
                }
            };
        }
        if (name.equals(this.renderItemInFirstPersonName)) {
            if (MC18 ? !desc.equals("(F)V") : !desc.endsWith("(;F)V") || !desc.contains(";FFL")) {
                return mv2;
            }
            return new MethodVisitor(262144, mv2){

                @Override
                public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                    super.visitMethodInsn(opcode, owner, name, desc, itf);
                    if (opcode == 184 && name.equals(ItemRendererVisitor.this.pushMatrixName)) {
                        this.mv.visitVarInsn(25, 0);
                        this.mv.visitFieldInsn(180, ItemRendererVisitor.this.itemRendererName, ItemRendererVisitor.this.itemToRenderName, "L" + ItemRendererVisitor.this.itemStackName + ";");
                        this.mv.visitIntInsn(23, 4);
                        this.mv.visitMethodInsn(184, "BytecodeMethods", "renderItemInFirstPerson", "(L" + ItemRendererVisitor.this.itemStackName + ";F)F", false);
                        this.mv.visitIntInsn(56, 6);
                    }
                }

                @Override
                public void visitInsn(int opcode) {
                    if (opcode == 11 && MC18) {
                        this.mv.visitIntInsn(23, 6);
                    } else {
                        super.visitInsn(opcode);
                    }
                }
            };
        }
        return mv2;
    }
}

