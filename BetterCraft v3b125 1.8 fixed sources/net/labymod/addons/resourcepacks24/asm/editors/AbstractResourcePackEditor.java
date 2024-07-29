/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.resourcepacks24.asm.editors;

import net.labymod.core.asm.LabyModCoreMod;
import net.labymod.core.asm.global.ClassEditor;
import net.labymod.main.Source;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class AbstractResourcePackEditor
extends ClassEditor {
    private String abstractResourcePackName;
    private String resourcePackFileName;

    public AbstractResourcePackEditor() {
        super(ClassEditor.ClassEditorType.CLASS_NODE);
        boolean mc18 = Source.ABOUT_MC_VERSION.startsWith("1.8");
        if (LabyModCoreMod.isObfuscated()) {
            this.abstractResourcePackName = mc18 ? "bmx" : "ced";
            this.resourcePackFileName = mc18 ? "a" : "a";
        } else {
            this.abstractResourcePackName = "net/minecraft/client/resources/AbstractResourcePack";
            this.resourcePackFileName = LabyModCoreMod.isForge() ? (mc18 ? "field_110597_b" : "field_110597_b") : "resourcePackFile";
        }
    }

    @Override
    public void accept(String name, ClassNode node) {
        for (MethodNode m2 : node.methods) {
            if (!m2.desc.equals("()Ljava/lang/String;")) continue;
            AbstractInsnNode[] abstractInsnNodeArray = m2.instructions.toArray();
            int n2 = abstractInsnNodeArray.length;
            int n3 = 0;
            while (n3 < n2) {
                InsnNode ldc;
                AbstractInsnNode abstractInsnNode = abstractInsnNodeArray[n3];
                if (abstractInsnNode instanceof InsnNode && (ldc = (InsnNode)abstractInsnNode).getOpcode() == 176) {
                    InsnList list = new InsnList();
                    list.add(new IntInsnNode(25, 0));
                    list.add(new FieldInsnNode(180, this.abstractResourcePackName, this.resourcePackFileName, "Ljava/io/File;"));
                    list.add(new MethodInsnNode(184, "net/labymod/addons/resourcepacks24/asm/ResourcepackMethods", "modifyName", "(Ljava/lang/String;Ljava/io/File;)Ljava/lang/String;", false));
                    m2.instructions.insertBefore((AbstractInsnNode)ldc, list);
                    return;
                }
                ++n3;
            }
        }
    }
}

