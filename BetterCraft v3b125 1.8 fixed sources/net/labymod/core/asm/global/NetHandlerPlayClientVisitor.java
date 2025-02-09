/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core.asm.global;

import net.labymod.core.asm.LabyModTransformer;
import net.labymod.core.asm.global.ClassEditor;
import net.labymod.main.Source;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class NetHandlerPlayClientVisitor
extends ClassEditor {
    private static final boolean MC_18 = Source.ABOUT_MC_VERSION.startsWith("1.8");
    private String packetCustomPayloadName = LabyModTransformer.getMappingImplementation().getCustomPayLoadPacketName();
    private String handleCustomPayloadName = LabyModTransformer.getMappingImplementation().getHandleCustomPayLoadName();
    private String getChannelNameName = LabyModTransformer.getMappingImplementation().getChannelNameName();
    private String getBufferDataName = LabyModTransformer.getMappingImplementation().getBufferDataName();
    private String packetBufferName = LabyModTransformer.getMappingImplementation().getPacketBufferName();
    private String handleResourcePackName = LabyModTransformer.getMappingImplementation().getHandleResourcePackName();
    private String packetResourcePackSendName = LabyModTransformer.getMappingImplementation().getPacketResourcePackSendName();

    public NetHandlerPlayClientVisitor() {
        super(ClassEditor.ClassEditorType.CLASS_NODE);
    }

    @Override
    public void accept(String name, ClassNode node) {
        for (MethodNode methodNode : node.methods) {
            AbstractInsnNode insnNode;
            int n2;
            int n3;
            AbstractInsnNode[] abstractInsnNodeArray;
            if (methodNode.name.equals(this.handleCustomPayloadName) && methodNode.desc.equals("(L" + this.packetCustomPayloadName + ";)V")) {
                AbstractInsnNode returnNode = null;
                abstractInsnNodeArray = methodNode.instructions.toArray();
                n3 = abstractInsnNodeArray.length;
                n2 = 0;
                while (n2 < n3) {
                    insnNode = abstractInsnNodeArray[n2];
                    if (insnNode instanceof InsnNode && ((InsnNode)insnNode).getOpcode() == 177) {
                        returnNode = insnNode;
                        break;
                    }
                    ++n2;
                }
                if (returnNode == null) continue;
                InsnList insnList = new InsnList();
                insnList.add(new VarInsnNode(25, 1));
                insnList.add(new MethodInsnNode(182, this.packetCustomPayloadName, this.getChannelNameName, "()Ljava/lang/String;", false));
                insnList.add(new VarInsnNode(25, 1));
                insnList.add(new MethodInsnNode(182, this.packetCustomPayloadName, this.getBufferDataName, "()L" + this.packetBufferName + ";", false));
                insnList.add(new MethodInsnNode(184, "BytecodeMethods", "onReceivePluginMessage", "(Ljava/lang/String;L" + this.packetBufferName + ";)V", false));
                methodNode.instructions.insertBefore(returnNode, insnList);
            }
            if (!MC_18 || !methodNode.name.equals(this.handleResourcePackName) || !methodNode.desc.equals("(L" + this.packetResourcePackSendName + ";)V")) continue;
            MethodInsnNode methodInsnNode = null;
            abstractInsnNodeArray = methodNode.instructions.toArray();
            n3 = abstractInsnNodeArray.length;
            n2 = 0;
            while (n2 < n3) {
                insnNode = abstractInsnNodeArray[n2];
                if (insnNode instanceof MethodInsnNode && ((MethodInsnNode)insnNode).getOpcode() == 182) {
                    methodInsnNode = (MethodInsnNode)insnNode;
                    break;
                }
                ++n2;
            }
            if (methodInsnNode == null) continue;
            methodNode.instructions.insert(methodInsnNode, new MethodInsnNode(184, "BytecodeMethods", "modifyResourcePackURL", "(Ljava/lang/String;)Ljava/lang/String;", false));
        }
    }
}

