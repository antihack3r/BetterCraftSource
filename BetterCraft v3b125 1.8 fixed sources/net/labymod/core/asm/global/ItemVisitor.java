/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core.asm.global;

import net.labymod.core.asm.LabyModTransformer;
import net.labymod.core.asm.global.ClassEditor;
import net.labymod.main.Source;
import org.objectweb.asm.MethodVisitor;

public class ItemVisitor
extends ClassEditor {
    private static final boolean MC18 = Source.ABOUT_MC_VERSION.startsWith("1.8");
    private String shouldCauseReequipAnimationName = "shouldCauseReequipAnimation";

    public ItemVisitor() {
        super(ClassEditor.ClassEditorType.CLASS_VISITOR_AND_REMAPPER);
    }

    @Override
    public String visitMapping(String typeName) {
        if (typeName.equals(LabyModTransformer.getMappingImplementation().getItemBucketName())) {
            return "net/labymod/core_implementation/" + (LabyModTransformer.getVersion().startsWith("1.8") ? "mc18" : "mc112") + "/item/ItemBucketCustom";
        }
        return super.visitMapping(typeName);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv2 = super.visitMethod(access, name, desc, signature, exceptions);
        if (MC18 && name.equals(this.shouldCauseReequipAnimationName)) {
            return new MethodVisitor(262144, mv2){

                @Override
                public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                    if (opcode == 184) {
                        owner = "BytecodeMethods";
                        name = "shouldCancelReequipAnimation";
                    }
                    super.visitMethodInsn(opcode, owner, name, desc, itf);
                }
            };
        }
        return mv2;
    }
}

