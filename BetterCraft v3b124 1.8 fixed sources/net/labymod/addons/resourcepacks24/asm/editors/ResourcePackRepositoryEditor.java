/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.resourcepacks24.asm.editors;

import net.labymod.core.asm.LabyModCoreMod;
import net.labymod.core.asm.global.ClassEditor;
import net.labymod.main.Source;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class ResourcePackRepositoryEditor
extends ClassEditor {
    private String updateRepositoryEntriesAllName;

    public ResourcePackRepositoryEditor() {
        super(ClassEditor.ClassEditorType.CLASS_NODE);
        boolean mc18 = Source.ABOUT_MC_VERSION.startsWith("1.8");
        this.updateRepositoryEntriesAllName = LabyModCoreMod.isObfuscated() ? (mc18 ? "a" : "b") : (LabyModCoreMod.isForge() ? (mc18 ? "a" : "b") : "updateRepositoryEntriesAll");
    }

    @Override
    public void accept(String name, ClassNode node) {
        for (MethodNode m2 : node.methods) {
            if (!m2.name.equals(this.updateRepositoryEntriesAllName) || !m2.desc.equals("()V")) continue;
            AbstractInsnNode[] abstractInsnNodeArray = m2.instructions.toArray();
            int n2 = abstractInsnNodeArray.length;
            int n3 = 0;
            while (n3 < n2) {
                MethodInsnNode method;
                AbstractInsnNode abstractInsnNode = abstractInsnNodeArray[n3];
                if (abstractInsnNode instanceof MethodInsnNode && (method = (MethodInsnNode)abstractInsnNode).getOpcode() == 183 && method.desc.equals("()Ljava/util/List;")) {
                    method.setOpcode(184);
                    method.owner = "net/labymod/addons/resourcepacks24/asm/ResourcepackMethods";
                    method.name = "getFiles";
                    method.desc = "(Ljava/lang/Object;)Ljava/util/List;";
                    return;
                }
                ++n3;
            }
        }
    }
}

