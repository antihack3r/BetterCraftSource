/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core.asm;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.labymod.core.asm.LabyModCoreMod;
import net.labymod.core.asm.MappingAdapter;
import net.labymod.core.asm.global.ClassEditor;
import net.labymod.core.asm.global.EntityRendererVisitor;
import net.labymod.core.asm.global.GuiContainerVisitor;
import net.labymod.core.asm.global.GuiDisconnectedVisitor;
import net.labymod.core.asm.global.GuiMultiplayerVisitor;
import net.labymod.core.asm.global.GuiScreenVisitor;
import net.labymod.core.asm.global.GuiSlotVisitor;
import net.labymod.core.asm.global.ItemRendererVisitor;
import net.labymod.core.asm.global.ItemVisitor;
import net.labymod.core.asm.global.MainVisitor;
import net.labymod.core.asm.global.MessageDeserializerVisitor;
import net.labymod.core.asm.global.MinecraftVisitor;
import net.labymod.core.asm.global.ModelBipedVisitor;
import net.labymod.core.asm.global.NetHandlerPlayClientVisitor;
import net.labymod.core.asm.global.RenderGlobalVisitor;
import net.labymod.core.asm.global.ScaledResolutionVisitor;
import net.labymod.core.asm.global.ServerListEntryNormal$1Visitor;
import net.labymod.core.asm.global.ServerListEntryNormalVisitor;
import net.labymod.core.asm.global.ServerPingerVisitor;
import net.labymod.core.asm.mappings.Minecraft112MappingImplementation;
import net.labymod.core.asm.mappings.Minecraft18MappingImplementation;
import net.labymod.core.asm.mappings.UnobfuscatedImplementation;
import net.labymod.core.asm.version_18.CapeImageBufferVisitor;
import net.labymod.core.asm.version_18.CapeUtilsVisitor;
import net.labymod.core.asm.version_18.ChunkPacketVisitor;
import net.labymod.core.asm.version_18.EntityLivingBaseVisitor;
import net.labymod.core.asm.version_18.EntityPlayerSPVisitor;
import net.labymod.core.asm.version_18.EntityPlayerVisitor;
import net.labymod.core.asm.version_18.ItemStackVisitor;
import net.labymod.main.Source;
import net.labymod.support.util.Debug;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.RemappingClassAdapter;
import org.objectweb.asm.tree.ClassNode;

public class LabyModTransformer
implements IClassTransformer {
    private static LabyModTransformer instance;
    private static MappingAdapter mappingImplementation;
    private boolean debugASM = System.getProperty("debugASM") != null;
    private Map<String, Map<String, Class<? extends ClassEditor>>> visitorsByVersion = new HashMap<String, Map<String, Class<? extends ClassEditor>>>();

    public static MappingAdapter getMappingImplementation() {
        return mappingImplementation;
    }

    public static String getVersion() {
        return Source.ABOUT_MC_VERSION;
    }

    public static void addVisitors() {
        Map<String, Map<String, Class<? extends ClassEditor>>> visitorsByVersion = instance.getVisitorsByVersion();
        if (!LabyModCoreMod.isObfuscated()) {
            mappingImplementation = new UnobfuscatedImplementation();
        }
        if (Source.ABOUT_MC_VERSION.startsWith("1.8")) {
            if (LabyModCoreMod.isObfuscated()) {
                mappingImplementation = new Minecraft18MappingImplementation();
            }
            HashMap<String, Class<? extends ClassEditor>> visitors_18 = new HashMap<String, Class<? extends ClassEditor>>();
            LabyModTransformer.addVisitor(visitors_18, mappingImplementation.getEntityPlayerSpName(), EntityPlayerSPVisitor.class);
            LabyModTransformer.addVisitor(visitors_18, mappingImplementation.getEntityLivingBaseName(), EntityLivingBaseVisitor.class);
            LabyModTransformer.addVisitor(visitors_18, "net.labymod.core_implementation.mc18.of.CapeImageBuffer", CapeImageBufferVisitor.class);
            LabyModTransformer.addVisitor(visitors_18, "CapeUtils", CapeUtilsVisitor.class);
            LabyModTransformer.addVisitor(visitors_18, mappingImplementation.getEntityPlayerName(), EntityPlayerVisitor.class);
            LabyModTransformer.addVisitor(visitors_18, mappingImplementation.getS26PacketMapChunkBulkName(), ChunkPacketVisitor.class);
            LabyModTransformer.addVisitor(visitors_18, mappingImplementation.getS21PacketChunkDataName(), ChunkPacketVisitor.class);
            LabyModTransformer.addVisitor(visitors_18, mappingImplementation.getItemStackName(), ItemStackVisitor.class);
            visitorsByVersion.put("1.8", visitors_18);
        }
        if (Source.ABOUT_MC_VERSION.startsWith("1.12")) {
            if (LabyModCoreMod.isObfuscated()) {
                mappingImplementation = new Minecraft112MappingImplementation();
            }
            HashMap<String, Class<? extends ClassEditor>> visitors_19 = new HashMap<String, Class<? extends ClassEditor>>();
            LabyModTransformer.addVisitor(visitors_19, mappingImplementation.getEntityLivingBaseName(), net.labymod.core.asm.version_112.EntityLivingBaseVisitor.class);
            visitorsByVersion.put("1.12", visitors_19);
        }
        HashMap<String, Class<? extends ClassEditor>> globalVisitors = new HashMap<String, Class<? extends ClassEditor>>();
        LabyModTransformer.addVisitor(globalVisitors, mappingImplementation.getGuiScreenName(), GuiScreenVisitor.class);
        LabyModTransformer.addVisitor(globalVisitors, mappingImplementation.getGuiDisconnectedName(), GuiDisconnectedVisitor.class);
        LabyModTransformer.addVisitor(globalVisitors, mappingImplementation.getGuiSlotName(), GuiSlotVisitor.class);
        LabyModTransformer.addVisitor(globalVisitors, mappingImplementation.getEntityRendererName(), EntityRendererVisitor.class);
        LabyModTransformer.addVisitor(globalVisitors, mappingImplementation.getServerListEntryNormalName(), ServerListEntryNormalVisitor.class);
        LabyModTransformer.addVisitor(globalVisitors, String.valueOf(mappingImplementation.getServerListEntryNormalName()) + "$1", ServerListEntryNormal._1Visitor.class);
        LabyModTransformer.addVisitor(globalVisitors, mappingImplementation.getGuiMultiplayerName(), GuiMultiplayerVisitor.class);
        LabyModTransformer.addVisitor(globalVisitors, mappingImplementation.getScaledResolutionName(), ScaledResolutionVisitor.class);
        LabyModTransformer.addVisitor(globalVisitors, mappingImplementation.getNetHandlerPlayClientName(), NetHandlerPlayClientVisitor.class);
        LabyModTransformer.addVisitor(globalVisitors, mappingImplementation.getRenderGlobalName(), RenderGlobalVisitor.class);
        LabyModTransformer.addVisitor(globalVisitors, mappingImplementation.getGuiContainerName(), GuiContainerVisitor.class);
        LabyModTransformer.addVisitor(globalVisitors, mappingImplementation.getServerPingerName(), ServerPingerVisitor.class);
        LabyModTransformer.addVisitor(globalVisitors, mappingImplementation.getItemName(), ItemVisitor.class);
        LabyModTransformer.addVisitor(globalVisitors, mappingImplementation.getMessageDeserializerName(), MessageDeserializerVisitor.class);
        LabyModTransformer.addVisitor(globalVisitors, mappingImplementation.getModelBipedName(), ModelBipedVisitor.class);
        LabyModTransformer.addVisitor(globalVisitors, mappingImplementation.getItemRendererName(), ItemRendererVisitor.class);
        visitorsByVersion.put("global", globalVisitors);
    }

    private static void addVisitor(Map<String, Class<? extends ClassEditor>> globalVisitors, String name, Class<? extends ClassEditor> clazz) {
        globalVisitors.put(name.replace("/", "."), clazz);
    }

    public LabyModTransformer() {
        instance = this;
        HashMap<String, Class<MainVisitor>> globalVisitors = new HashMap<String, Class<MainVisitor>>();
        globalVisitors.put("net.minecraft.client.main.Main", MainVisitor.class);
        instance.getVisitorsByVersion().put("global", globalVisitors);
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        boolean isObfuscated = !name.equals(transformedName);
        HashSet<Class<? extends ClassEditor>> editors = new HashSet<Class<? extends ClassEditor>>();
        for (Map.Entry<String, Map<String, Class<? extends ClassEditor>>> visitorEntry : this.visitorsByVersion.entrySet()) {
            if (!visitorEntry.getKey().equals("global") && !Source.ABOUT_MC_VERSION.isEmpty() && !Source.ABOUT_MC_VERSION.startsWith(visitorEntry.getKey()) || !visitorEntry.getValue().containsKey(name)) continue;
            editors.add(visitorEntry.getValue().get(name));
        }
        if (editors.size() != 0) {
            bytes = this.transform(name, transformedName, editors, bytes, isObfuscated);
        }
        return bytes;
    }

    private byte[] transform(String name, String transformedName, Set<Class<? extends ClassEditor>> editors, byte[] bytes, boolean isObfuscated) {
        Debug.log(Debug.EnumDebugMode.ASM, "Transforming " + transformedName + "...");
        try {
            for (Class<? extends ClassEditor> editorClass : editors) {
                final ClassEditor editor = editorClass.newInstance();
                switch (editor.getType()) {
                    case CLASS_NODE: {
                        ClassNode node = new ClassNode();
                        ClassReader reader = new ClassReader(bytes);
                        reader.accept(node, 0);
                        editor.accept(name, node);
                        ClassWriter writer = new ClassWriter(3);
                        node.accept(writer);
                        return this.debugTransformedClass(transformedName, writer.toByteArray());
                    }
                    case CLASS_VISITOR: {
                        ClassReader classReader = new ClassReader(bytes);
                        ClassWriter classWriter = new ClassWriter(classReader, 3);
                        editor.accept(name, classWriter);
                        classReader.accept(editor, 0);
                        return this.debugTransformedClass(transformedName, classWriter.toByteArray());
                    }
                    case CLASS_VISITOR_AND_REMAPPER: {
                        ClassReader classReader = new ClassReader(bytes);
                        ClassWriter classWriter = new ClassWriter(classReader, 3);
                        editor.accept(name, classWriter);
                        classReader.accept(editor, 0);
                        classReader = new ClassReader(classWriter.toByteArray());
                        classWriter = new ClassWriter(classReader, 3);
                        RemappingClassAdapter adapter = new RemappingClassAdapter(classWriter, new Remapper(){

                            @Override
                            public String map(String typeName) {
                                return editor.visitMapping(typeName);
                            }
                        });
                        editor.accept(name, classWriter);
                        classReader.accept(adapter, 8);
                        return this.debugTransformedClass(transformedName, classWriter.toByteArray());
                    }
                }
            }
        }
        catch (Exception ex2) {
            ex2.printStackTrace();
        }
        return bytes;
    }

    public byte[] debugTransformedClass(String name, byte[] bytes) throws Exception {
        if (this.debugASM) {
            File asmFolder = new File("asm");
            if (!asmFolder.exists()) {
                asmFolder.mkdir();
            }
            File byteFile = new File(String.valueOf(name) + ".class");
            File byteFileInAsm = new File(asmFolder, String.valueOf(name) + ".class");
            byteFileInAsm.delete();
            FileUtils.writeByteArrayToFile(byteFile, bytes);
            byteFile.renameTo(byteFileInAsm);
        }
        return bytes;
    }

    public Map<String, Map<String, Class<? extends ClassEditor>>> getVisitorsByVersion() {
        return this.visitorsByVersion;
    }

    public static void resolveMinecraftClass(String minecraftClassName) {
        HashMap<String, Class<MinecraftVisitor>> globalVisitors = new HashMap<String, Class<MinecraftVisitor>>();
        globalVisitors.put(minecraftClassName.replace("/", "."), MinecraftVisitor.class);
        instance.getVisitorsByVersion().put("global", globalVisitors);
    }

    public static LabyModTransformer getInstance() {
        return instance;
    }
}

