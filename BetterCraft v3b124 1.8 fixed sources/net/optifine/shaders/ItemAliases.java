/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.shaders;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.config.ConnectedParser;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import net.optifine.shaders.IShaderPack;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.config.MacroProcessor;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.StrUtils;

public class ItemAliases {
    private static int[] itemAliases = null;
    private static boolean updateOnResourcesReloaded;
    private static final int NO_ALIAS = Integer.MIN_VALUE;

    public static int getItemAliasId(int itemId) {
        if (itemAliases == null) {
            return itemId;
        }
        if (itemId >= 0 && itemId < itemAliases.length) {
            int i2 = itemAliases[itemId];
            return i2 == Integer.MIN_VALUE ? itemId : i2;
        }
        return itemId;
    }

    public static void resourcesReloaded() {
        if (updateOnResourcesReloaded) {
            updateOnResourcesReloaded = false;
            ItemAliases.update(Shaders.getShaderPack());
        }
    }

    public static void update(IShaderPack shaderPack) {
        ItemAliases.reset();
        if (shaderPack != null) {
            if (Reflector.Loader_getActiveModList.exists() && Config.getResourceManager() == null) {
                Config.dbg("[Shaders] Delayed loading of item mappings after resources are loaded");
                updateOnResourcesReloaded = true;
            } else {
                ArrayList<Integer> list = new ArrayList<Integer>();
                String s2 = "/shaders/item.properties";
                InputStream inputstream = shaderPack.getResourceAsStream(s2);
                if (inputstream != null) {
                    ItemAliases.loadItemAliases(inputstream, s2, list);
                }
                ItemAliases.loadModItemAliases(list);
                if (list.size() > 0) {
                    itemAliases = ItemAliases.toArray(list);
                }
            }
        }
    }

    private static void loadModItemAliases(List<Integer> listItemAliases) {
        String[] astring = ReflectorForge.getForgeModIds();
        int i2 = 0;
        while (i2 < astring.length) {
            String s2 = astring[i2];
            try {
                ResourceLocation resourcelocation = new ResourceLocation(s2, "shaders/item.properties");
                InputStream inputstream = Config.getResourceStream(resourcelocation);
                ItemAliases.loadItemAliases(inputstream, resourcelocation.toString(), listItemAliases);
            }
            catch (IOException iOException) {
                // empty catch block
            }
            ++i2;
        }
    }

    private static void loadItemAliases(InputStream in2, String path, List<Integer> listItemAliases) {
        if (in2 != null) {
            try {
                in2 = MacroProcessor.process(in2, path);
                PropertiesOrdered properties = new PropertiesOrdered();
                properties.load(in2);
                in2.close();
                Config.dbg("[Shaders] Parsing item mappings: " + path);
                ConnectedParser connectedparser = new ConnectedParser("Shaders");
                for (Object o2 : ((Properties)properties).keySet()) {
                    String s2 = (String)o2;
                    String s1 = properties.getProperty(s2);
                    String s22 = "item.";
                    if (!s2.startsWith(s22)) {
                        Config.warn("[Shaders] Invalid item ID: " + s2);
                        continue;
                    }
                    String s3 = StrUtils.removePrefix(s2, s22);
                    int i2 = Config.parseInt(s3, -1);
                    if (i2 < 0) {
                        Config.warn("[Shaders] Invalid item alias ID: " + i2);
                        continue;
                    }
                    int[] aint = connectedparser.parseItems(s1);
                    if (aint != null && aint.length >= 1) {
                        int j2 = 0;
                        while (j2 < aint.length) {
                            int k2 = aint[j2];
                            ItemAliases.addToList(listItemAliases, k2, i2);
                            ++j2;
                        }
                        continue;
                    }
                    Config.warn("[Shaders] Invalid item ID mapping: " + s2 + "=" + s1);
                }
            }
            catch (IOException var15) {
                Config.warn("[Shaders] Error reading: " + path);
            }
        }
    }

    private static void addToList(List<Integer> list, int index, int val) {
        while (list.size() <= index) {
            list.add(Integer.MIN_VALUE);
        }
        list.set(index, val);
    }

    private static int[] toArray(List<Integer> list) {
        int[] aint = new int[list.size()];
        int i2 = 0;
        while (i2 < aint.length) {
            aint[i2] = list.get(i2);
            ++i2;
        }
        return aint;
    }

    public static void reset() {
        itemAliases = null;
    }
}

