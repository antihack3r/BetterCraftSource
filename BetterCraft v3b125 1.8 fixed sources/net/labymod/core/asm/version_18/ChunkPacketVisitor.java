/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core.asm.version_18;

import net.labymod.core.asm.LabyModTransformer;
import net.labymod.core.asm.global.ClassEditor;
import org.objectweb.asm.MethodVisitor;

public class ChunkPacketVisitor
extends ClassEditor {
    private String readPacketDataName = LabyModTransformer.getMappingImplementation().getReadPacketDataName();
    private String packetBufferName = LabyModTransformer.getMappingImplementation().getPacketBufferName();

    public ChunkPacketVisitor() {
        super(ClassEditor.ClassEditorType.CLASS_VISITOR);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv2 = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals(this.readPacketDataName) && desc.equals("(L" + this.packetBufferName + ";)V")) {
            return new MethodVisitor(262144, mv2){

                @Override
                public void visitInsn(int opcode) {
                    if (opcode == 177) {
                        super.visitIntInsn(25, 1);
                        super.visitIntInsn(25, 0);
                        super.visitMethodInsn(184, "BytecodeMethods", "onReceiveChunkData", "(Ljava/lang/Object;Ljava/lang/Object;)V", false);
                    }
                    super.visitInsn(opcode);
                }
            };
        }
        return mv2;
    }
}

