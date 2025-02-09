/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core.asm.version_18;

import net.labymod.core.asm.LabyModCoreMod;
import net.labymod.core.asm.LabyModTransformer;
import net.labymod.core.asm.global.ClassEditor;
import org.objectweb.asm.MethodVisitor;

public class CapeImageBufferVisitor
extends ClassEditor {
    private String abstractClientPlayerName = LabyModCoreMod.isObfuscated() ? "bet" : "net/minecraft/client/entity/AbstractClientPlayer";
    private String resourceLocationName = LabyModTransformer.getMappingImplementation().getResourceLocationName();

    public CapeImageBufferVisitor() {
        super(ClassEditor.ClassEditorType.CLASS_VISITOR);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv2 = super.visitMethod(access, name, desc, signature, exceptions);
        return new MethodVisitor(262144, mv2){

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                if (name.equals("parseCape")) {
                    owner = "CapeUtils";
                }
                if (name.equals("setLocationOfCape")) {
                    opcode = 182;
                    owner = CapeImageBufferVisitor.this.abstractClientPlayerName;
                    desc = "(L" + CapeImageBufferVisitor.this.resourceLocationName + ";)V";
                }
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        };
    }
}

