// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.gen;

import net.minecraft.world.biome.Biome;
import net.minecraft.init.Biomes;
import java.util.Collection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.init.Blocks;
import net.minecraft.block.Block;
import java.util.Locale;
import java.util.Iterator;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import java.util.Map;
import java.util.List;

public class FlatGeneratorInfo
{
    private final List<FlatLayerInfo> flatLayers;
    private final Map<String, Map<String, String>> worldFeatures;
    private int biomeToUse;
    
    public FlatGeneratorInfo() {
        this.flatLayers = (List<FlatLayerInfo>)Lists.newArrayList();
        this.worldFeatures = (Map<String, Map<String, String>>)Maps.newHashMap();
    }
    
    public int getBiome() {
        return this.biomeToUse;
    }
    
    public void setBiome(final int biome) {
        this.biomeToUse = biome;
    }
    
    public Map<String, Map<String, String>> getWorldFeatures() {
        return this.worldFeatures;
    }
    
    public List<FlatLayerInfo> getFlatLayers() {
        return this.flatLayers;
    }
    
    public void updateLayers() {
        int i = 0;
        for (final FlatLayerInfo flatlayerinfo : this.flatLayers) {
            flatlayerinfo.setMinY(i);
            i += flatlayerinfo.getLayerCount();
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append(3);
        stringbuilder.append(";");
        for (int i = 0; i < this.flatLayers.size(); ++i) {
            if (i > 0) {
                stringbuilder.append(",");
            }
            stringbuilder.append(this.flatLayers.get(i));
        }
        stringbuilder.append(";");
        stringbuilder.append(this.biomeToUse);
        if (this.worldFeatures.isEmpty()) {
            stringbuilder.append(";");
        }
        else {
            stringbuilder.append(";");
            int k = 0;
            for (final Map.Entry<String, Map<String, String>> entry : this.worldFeatures.entrySet()) {
                if (k++ > 0) {
                    stringbuilder.append(",");
                }
                stringbuilder.append(entry.getKey().toLowerCase(Locale.ROOT));
                final Map<String, String> map = entry.getValue();
                if (!map.isEmpty()) {
                    stringbuilder.append("(");
                    int j = 0;
                    for (final Map.Entry<String, String> entry2 : map.entrySet()) {
                        if (j++ > 0) {
                            stringbuilder.append(" ");
                        }
                        stringbuilder.append(entry2.getKey());
                        stringbuilder.append("=");
                        stringbuilder.append(entry2.getValue());
                    }
                    stringbuilder.append(")");
                }
            }
        }
        return stringbuilder.toString();
    }
    
    private static FlatLayerInfo getLayerFromString(final int p_180715_0_, final String p_180715_1_, final int p_180715_2_) {
        String[] astring = (p_180715_0_ >= 3) ? p_180715_1_.split("\\*", 2) : p_180715_1_.split("x", 2);
        int i = 1;
        int j = 0;
        if (astring.length == 2) {
            try {
                i = Integer.parseInt(astring[0]);
                if (p_180715_2_ + i >= 256) {
                    i = 256 - p_180715_2_;
                }
                if (i < 0) {
                    i = 0;
                }
            }
            catch (final Throwable var8) {
                return null;
            }
        }
        Block block;
        try {
            final String s = astring[astring.length - 1];
            if (p_180715_0_ < 3) {
                astring = s.split(":", 2);
                if (astring.length > 1) {
                    j = Integer.parseInt(astring[1]);
                }
                block = Block.getBlockById(Integer.parseInt(astring[0]));
            }
            else {
                astring = s.split(":", 3);
                block = ((astring.length > 1) ? Block.getBlockFromName(String.valueOf(astring[0]) + ":" + astring[1]) : null);
                if (block != null) {
                    j = ((astring.length > 2) ? Integer.parseInt(astring[2]) : 0);
                }
                else {
                    block = Block.getBlockFromName(astring[0]);
                    if (block != null) {
                        j = ((astring.length > 1) ? Integer.parseInt(astring[1]) : 0);
                    }
                }
                if (block == null) {
                    return null;
                }
            }
            if (block == Blocks.AIR) {
                j = 0;
            }
            if (j < 0 || j > 15) {
                j = 0;
            }
        }
        catch (final Throwable var9) {
            return null;
        }
        final FlatLayerInfo flatlayerinfo = new FlatLayerInfo(p_180715_0_, i, block, j);
        flatlayerinfo.setMinY(p_180715_2_);
        return flatlayerinfo;
    }
    
    private static List<FlatLayerInfo> getLayersFromString(final int p_180716_0_, final String p_180716_1_) {
        if (p_180716_1_ != null && p_180716_1_.length() >= 1) {
            final List<FlatLayerInfo> list = (List<FlatLayerInfo>)Lists.newArrayList();
            final String[] astring = p_180716_1_.split(",");
            int i = 0;
            String[] array;
            for (int length = (array = astring).length, j = 0; j < length; ++j) {
                final String s = array[j];
                final FlatLayerInfo flatlayerinfo = getLayerFromString(p_180716_0_, s, i);
                if (flatlayerinfo == null) {
                    return null;
                }
                list.add(flatlayerinfo);
                i += flatlayerinfo.getLayerCount();
            }
            return list;
        }
        return null;
    }
    
    public static FlatGeneratorInfo createFlatGeneratorFromString(final String flatGeneratorSettings) {
        if (flatGeneratorSettings == null) {
            return getDefaultFlatGenerator();
        }
        final String[] astring = flatGeneratorSettings.split(";", -1);
        final int i = (astring.length == 1) ? 0 : MathHelper.getInt(astring[0], 0);
        if (i < 0 || i > 3) {
            return getDefaultFlatGenerator();
        }
        final FlatGeneratorInfo flatgeneratorinfo = new FlatGeneratorInfo();
        int j = (astring.length != 1) ? 1 : 0;
        final List<FlatLayerInfo> list = getLayersFromString(i, astring[j++]);
        if (list != null && !list.isEmpty()) {
            flatgeneratorinfo.getFlatLayers().addAll(list);
            flatgeneratorinfo.updateLayers();
            int k = Biome.getIdForBiome(Biomes.PLAINS);
            if (i > 0 && astring.length > j) {
                k = MathHelper.getInt(astring[j++], k);
            }
            flatgeneratorinfo.setBiome(k);
            if (i > 0 && astring.length > j) {
                final String[] astring2 = astring[j++].toLowerCase(Locale.ROOT).split(",");
                String[] array;
                for (int length = (array = astring2).length, l = 0; l < length; ++l) {
                    final String s = array[l];
                    final String[] astring3 = s.split("\\(", 2);
                    final Map<String, String> map = (Map<String, String>)Maps.newHashMap();
                    if (!astring3[0].isEmpty()) {
                        flatgeneratorinfo.getWorldFeatures().put(astring3[0], map);
                        if (astring3.length > 1 && astring3[1].endsWith(")") && astring3[1].length() > 1) {
                            final String[] astring4 = astring3[1].substring(0, astring3[1].length() - 1).split(" ");
                            String[] array2;
                            for (int length2 = (array2 = astring4).length, n = 0; n < length2; ++n) {
                                final String s2 = array2[n];
                                final String[] astring5 = s2.split("=", 2);
                                if (astring5.length == 2) {
                                    map.put(astring5[0], astring5[1]);
                                }
                            }
                        }
                    }
                }
            }
            else {
                flatgeneratorinfo.getWorldFeatures().put("village", (Map<String, String>)Maps.newHashMap());
            }
            return flatgeneratorinfo;
        }
        return getDefaultFlatGenerator();
    }
    
    public static FlatGeneratorInfo getDefaultFlatGenerator() {
        final FlatGeneratorInfo flatgeneratorinfo = new FlatGeneratorInfo();
        flatgeneratorinfo.setBiome(Biome.getIdForBiome(Biomes.PLAINS));
        flatgeneratorinfo.getFlatLayers().add(new FlatLayerInfo(1, Blocks.BEDROCK));
        flatgeneratorinfo.getFlatLayers().add(new FlatLayerInfo(2, Blocks.DIRT));
        flatgeneratorinfo.getFlatLayers().add(new FlatLayerInfo(1, Blocks.GRASS));
        flatgeneratorinfo.updateLayers();
        flatgeneratorinfo.getWorldFeatures().put("village", (Map<String, String>)Maps.newHashMap());
        return flatgeneratorinfo;
    }
}
