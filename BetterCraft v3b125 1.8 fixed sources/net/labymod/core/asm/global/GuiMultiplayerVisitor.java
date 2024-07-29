/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core.asm.global;

import net.labymod.core.asm.LabyModTransformer;
import net.labymod.core.asm.global.ClassEditor;
import org.objectweb.asm.MethodVisitor;

public class GuiMultiplayerVisitor
extends ClassEditor {
    private String drawScreenName = LabyModTransformer.getMappingImplementation().getGuiScreenDrawScreenName();

    public GuiMultiplayerVisitor() {
        super(ClassEditor.ClassEditorType.CLASS_VISITOR);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv2 = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals(this.drawScreenName) && desc.equals("(IIF)V")) {
            return new MethodVisitor(262144, mv2){

                @Override
                public void visitLdcInsn(Object cst) {
                    if (cst instanceof String && String.valueOf(cst).equals("multiplayer.title")) {
                        cst = "";
                    }
                    super.visitLdcInsn(cst);
                }
            };
        }
        return mv2;
    }
}

