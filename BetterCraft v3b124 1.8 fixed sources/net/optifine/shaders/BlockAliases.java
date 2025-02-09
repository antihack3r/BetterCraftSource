/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.shaders;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.config.ConnectedParser;
import net.optifine.config.MatchBlock;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import net.optifine.shaders.BlockAlias;
import net.optifine.shaders.IShaderPack;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.config.MacroProcessor;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.StrUtils;

public class BlockAliases {
    private static BlockAlias[][] blockAliases = null;
    private static PropertiesOrdered blockLayerPropertes = null;
    private static boolean updateOnResourcesReloaded;

    public static int getBlockAliasId(int blockId, int metadata) {
        if (blockAliases == null) {
            return blockId;
        }
        if (blockId >= 0 && blockId < blockAliases.length) {
            BlockAlias[] ablockalias = blockAliases[blockId];
            if (ablockalias == null) {
                return blockId;
            }
            int i2 = 0;
            while (i2 < ablockalias.length) {
                BlockAlias blockalias = ablockalias[i2];
                if (blockalias.matches(blockId, metadata)) {
                    return blockalias.getBlockAliasId();
                }
                ++i2;
            }
            return blockId;
        }
        return blockId;
    }

    public static void resourcesReloaded() {
        if (updateOnResourcesReloaded) {
            updateOnResourcesReloaded = false;
            BlockAliases.update(Shaders.getShaderPack());
        }
    }

    public static void update(IShaderPack shaderPack) {
        BlockAliases.reset();
        if (shaderPack != null) {
            if (Reflector.Loader_getActiveModList.exists() && Minecraft.getMinecraft().getResourcePackRepository() == null) {
                Config.dbg("[Shaders] Delayed loading of block mappings after resources are loaded");
                updateOnResourcesReloaded = true;
            } else {
                ArrayList<List<BlockAlias>> list = new ArrayList<List<BlockAlias>>();
                String s2 = "/shaders/block.properties";
                InputStream inputstream = shaderPack.getResourceAsStream(s2);
                if (inputstream != null) {
                    BlockAliases.loadBlockAliases(inputstream, s2, list);
                }
                BlockAliases.loadModBlockAliases(list);
                if (list.size() > 0) {
                    blockAliases = BlockAliases.toArrays(list);
                }
            }
        }
    }

    private static void loadModBlockAliases(List<List<BlockAlias>> listBlockAliases) {
        String[] astring = ReflectorForge.getForgeModIds();
        int i2 = 0;
        while (i2 < astring.length) {
            String s2 = astring[i2];
            try {
                ResourceLocation resourcelocation = new ResourceLocation(s2, "shaders/block.properties");
                InputStream inputstream = Config.getResourceStream(resourcelocation);
                BlockAliases.loadBlockAliases(inputstream, resourcelocation.toString(), listBlockAliases);
            }
            catch (IOException iOException) {
                // empty catch block
            }
            ++i2;
        }
    }

    private static void loadBlockAliases(InputStream in2, String path, List<List<BlockAlias>> listBlockAliases) {
        if (in2 != null) {
            try {
                in2 = MacroProcessor.process(in2, path);
                PropertiesOrdered properties = new PropertiesOrdered();
                properties.load(in2);
                in2.close();
                Config.dbg("[Shaders] Parsing block mappings: " + path);
                ConnectedParser connectedparser = new ConnectedParser("Shaders");
                for (Object o2 : ((Properties)properties).keySet()) {
                    String s2 = (String)o2;
                    String s1 = properties.getProperty(s2);
                    if (s2.startsWith("layer.")) {
                        if (blockLayerPropertes == null) {
                            blockLayerPropertes = new PropertiesOrdered();
                        }
                        blockLayerPropertes.put(s2, s1);
                        continue;
                    }
                    String s22 = "block.";
                    if (!s2.startsWith(s22)) {
                        Config.warn("[Shaders] Invalid block ID: " + s2);
                        continue;
                    }
                    String s3 = StrUtils.removePrefix(s2, s22);
                    int i2 = Config.parseInt(s3, -1);
                    if (i2 < 0) {
                        Config.warn("[Shaders] Invalid block ID: " + s2);
                        continue;
                    }
                    MatchBlock[] amatchblock = connectedparser.parseMatchBlocks(s1);
                    if (amatchblock != null && amatchblock.length >= 1) {
                        BlockAlias blockalias = new BlockAlias(i2, amatchblock);
                        BlockAliases.addToList(listBlockAliases, blockalias);
                        continue;
                    }
                    Config.warn("[Shaders] Invalid block ID mapping: " + s2 + "=" + s1);
                }
            }
            catch (IOException var14) {
                Config.warn("[Shaders] Error reading: " + path);
            }
        }
    }

    private static void addToList(List<List<BlockAlias>> blocksAliases, BlockAlias ba2) {
        int[] aint = ba2.getMatchBlockIds();
        int i2 = 0;
        while (i2 < aint.length) {
            int j2 = aint[i2];
            while (j2 >= blocksAliases.size()) {
                blocksAliases.add(null);
            }
            List<BlockAlias> list = blocksAliases.get(j2);
            if (list == null) {
                list = new ArrayList<BlockAlias>();
                blocksAliases.set(j2, list);
            }
            BlockAlias blockalias = new BlockAlias(ba2.getBlockAliasId(), ba2.getMatchBlocks(j2));
            list.add(blockalias);
            ++i2;
        }
    }

    private static BlockAlias[][] toArrays(List<List<BlockAlias>> listBlocksAliases) {
        BlockAlias[][] ablockalias = new BlockAlias[listBlocksAliases.size()][];
        int i2 = 0;
        while (i2 < ablockalias.length) {
            List<BlockAlias> list = listBlocksAliases.get(i2);
            if (list != null) {
                ablockalias[i2] = list.toArray(new BlockAlias[list.size()]);
            }
            ++i2;
        }
        return ablockalias;
    }

    public static PropertiesOrdered getBlockLayerPropertes() {
        return blockLayerPropertes;
    }

    public static void reset() {
        blockAliases = null;
        blockLayerPropertes = null;
    }
}

