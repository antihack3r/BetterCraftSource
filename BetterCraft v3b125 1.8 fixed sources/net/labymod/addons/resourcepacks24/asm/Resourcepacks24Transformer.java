/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.resourcepacks24.asm;

import net.labymod.addons.resourcepacks24.asm.editors.AbstractResourcePackEditor;
import net.labymod.addons.resourcepacks24.asm.editors.ResourcePackRepositoryEditor;
import net.labymod.core.asm.global.ClassEditor;
import net.labymod.main.Source;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

public class Resourcepacks24Transformer
implements IClassTransformer {
    private static final AbstractResourcePackEditor ABSTRACT_RESOURCE_PACK_EDITOR = new AbstractResourcePackEditor();
    private static final ResourcePackRepositoryEditor RESOURCE_PACK_REPOSITORY_EDITOR = new ResourcePackRepositoryEditor();
    private static final boolean MC18 = Source.ABOUT_MC_VERSION.startsWith("1.8");

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        bytes = this.transform(name, bytes, ABSTRACT_RESOURCE_PACK_EDITOR, "net.minecraft.client.resources.AbstractResourcePack", "bmx", "ced");
        bytes = this.transform(name, bytes, RESOURCE_PACK_REPOSITORY_EDITOR, "net.minecraft.client.resources.ResourcePackRepository", "bnm", "ceu");
        return bytes;
    }

    public byte[] transform(String name, byte[] bytes, ClassEditor editor, String mcp, String mc18, String mc112) {
        if (MC18 ? !name.equals(mcp) && !name.equals(mc18) : !name.equals(mcp) && !name.equals(mc112)) {
            return bytes;
        }
        try {
            ClassNode node = new ClassNode();
            ClassReader reader = new ClassReader(bytes);
            reader.accept(node, 0);
            editor.accept(name, node);
            ClassWriter writer = new ClassWriter(3);
            node.accept(writer);
            return writer.toByteArray();
        }
        catch (Exception error) {
            error.printStackTrace();
            return bytes;
        }
    }
}

