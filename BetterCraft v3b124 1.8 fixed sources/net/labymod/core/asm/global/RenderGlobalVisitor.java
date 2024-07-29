/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core.asm.global;

import net.labymod.core.asm.LabyModTransformer;
import net.labymod.core.asm.global.ClassEditor;
import org.objectweb.asm.MethodVisitor;

public class RenderGlobalVisitor
extends ClassEditor {
    private String renderEntitiesName = LabyModTransformer.getMappingImplementation().getRenderEntitiesName();
    private String renderGlobalName = LabyModTransformer.getMappingImplementation().getRenderGlobalName();
    private String countEntitiesRenderedName = LabyModTransformer.getMappingImplementation().getCountEntitiesRenderedName();
    private String countEntitiesTotalName = LabyModTransformer.getMappingImplementation().getCountEntitiesTotalName();
    private String entityClassName = LabyModTransformer.getMappingImplementation().getEntityClassName();
    private String iCameraName = LabyModTransformer.getMappingImplementation().getICameraClassName();

    public RenderGlobalVisitor() {
        super(ClassEditor.ClassEditorType.CLASS_VISITOR);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv2 = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals(this.renderEntitiesName) && desc.equals("(L" + this.entityClassName + ";L" + this.iCameraName + ";F)V")) {
            return new MethodVisitor(262144, mv2){

                @Override
                public void visitInsn(int opcode) {
                    if (opcode == 177) {
                        super.visitIntInsn(25, 0);
                        super.visitIntInsn(25, 0);
                        super.visitFieldInsn(180, RenderGlobalVisitor.this.renderGlobalName, RenderGlobalVisitor.this.countEntitiesRenderedName, "I");
                        super.visitIntInsn(25, 0);
                        super.visitFieldInsn(180, RenderGlobalVisitor.this.renderGlobalName, RenderGlobalVisitor.this.countEntitiesTotalName, "I");
                        super.visitMethodInsn(184, "BytecodeMethods", "onUpdateEntityCountInfo", "(II)V", false);
                    }
                    super.visitInsn(opcode);
                }
            };
        }
        return mv2;
    }
}

