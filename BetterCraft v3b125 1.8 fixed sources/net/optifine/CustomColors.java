/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import javax.imageio.ImageIO;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.BlockStem;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.BlockPosM;
import net.optifine.CustomColorFader;
import net.optifine.CustomColormap;
import net.optifine.LightMap;
import net.optifine.LightMapPack;
import net.optifine.config.ConnectedParser;
import net.optifine.config.MatchBlock;
import net.optifine.reflect.Reflector;
import net.optifine.render.RenderEnv;
import net.optifine.util.EntityUtils;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.ResUtils;
import net.optifine.util.StrUtils;
import net.optifine.util.TextureUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class CustomColors {
    private static String paletteFormatDefault = "vanilla";
    private static CustomColormap waterColors = null;
    private static CustomColormap foliagePineColors = null;
    private static CustomColormap foliageBirchColors = null;
    private static CustomColormap swampFoliageColors = null;
    private static CustomColormap swampGrassColors = null;
    private static CustomColormap[] colorsBlockColormaps = null;
    private static CustomColormap[][] blockColormaps = null;
    private static CustomColormap skyColors = null;
    private static CustomColorFader skyColorFader = new CustomColorFader();
    private static CustomColormap fogColors = null;
    private static CustomColorFader fogColorFader = new CustomColorFader();
    private static CustomColormap underwaterColors = null;
    private static CustomColorFader underwaterColorFader = new CustomColorFader();
    private static CustomColormap underlavaColors = null;
    private static CustomColorFader underlavaColorFader = new CustomColorFader();
    private static LightMapPack[] lightMapPacks = null;
    private static int lightmapMinDimensionId = 0;
    private static CustomColormap redstoneColors = null;
    private static CustomColormap xpOrbColors = null;
    private static int xpOrbTime = -1;
    private static CustomColormap durabilityColors = null;
    private static CustomColormap stemColors = null;
    private static CustomColormap stemMelonColors = null;
    private static CustomColormap stemPumpkinColors = null;
    private static CustomColormap myceliumParticleColors = null;
    private static boolean useDefaultGrassFoliageColors = true;
    private static int particleWaterColor = -1;
    private static int particlePortalColor = -1;
    private static int lilyPadColor = -1;
    private static int expBarTextColor = -1;
    private static int bossTextColor = -1;
    private static int signTextColor = -1;
    private static Vec3 fogColorNether = null;
    private static Vec3 fogColorEnd = null;
    private static Vec3 skyColorEnd = null;
    private static int[] spawnEggPrimaryColors = null;
    private static int[] spawnEggSecondaryColors = null;
    private static float[][] wolfCollarColors = null;
    private static float[][] sheepColors = null;
    private static int[] textColors = null;
    private static int[] mapColorsOriginal = null;
    private static int[] potionColors = null;
    private static final IBlockState BLOCK_STATE_DIRT = Blocks.dirt.getDefaultState();
    private static final IBlockState BLOCK_STATE_WATER = Blocks.water.getDefaultState();
    public static Random random = new Random();
    private static final IColorizer COLORIZER_GRASS = new IColorizer(){

        @Override
        public int getColor(IBlockState blockState, IBlockAccess blockAccess, BlockPos blockPos) {
            BiomeGenBase biomegenbase = CustomColors.getColorBiome(blockAccess, blockPos);
            return swampGrassColors != null && biomegenbase == BiomeGenBase.swampland ? swampGrassColors.getColor(biomegenbase, blockPos) : biomegenbase.getGrassColorAtPos(blockPos);
        }

        @Override
        public boolean isColorConstant() {
            return false;
        }
    };
    private static final IColorizer COLORIZER_FOLIAGE = new IColorizer(){

        @Override
        public int getColor(IBlockState blockState, IBlockAccess blockAccess, BlockPos blockPos) {
            BiomeGenBase biomegenbase = CustomColors.getColorBiome(blockAccess, blockPos);
            return swampFoliageColors != null && biomegenbase == BiomeGenBase.swampland ? swampFoliageColors.getColor(biomegenbase, blockPos) : biomegenbase.getFoliageColorAtPos(blockPos);
        }

        @Override
        public boolean isColorConstant() {
            return false;
        }
    };
    private static final IColorizer COLORIZER_FOLIAGE_PINE = new IColorizer(){

        @Override
        public int getColor(IBlockState blockState, IBlockAccess blockAccess, BlockPos blockPos) {
            return foliagePineColors != null ? foliagePineColors.getColor(blockAccess, blockPos) : ColorizerFoliage.getFoliageColorPine();
        }

        @Override
        public boolean isColorConstant() {
            return foliagePineColors == null;
        }
    };
    private static final IColorizer COLORIZER_FOLIAGE_BIRCH = new IColorizer(){

        @Override
        public int getColor(IBlockState blockState, IBlockAccess blockAccess, BlockPos blockPos) {
            return foliageBirchColors != null ? foliageBirchColors.getColor(blockAccess, blockPos) : ColorizerFoliage.getFoliageColorBirch();
        }

        @Override
        public boolean isColorConstant() {
            return foliageBirchColors == null;
        }
    };
    private static final IColorizer COLORIZER_WATER = new IColorizer(){

        @Override
        public int getColor(IBlockState blockState, IBlockAccess blockAccess, BlockPos blockPos) {
            BiomeGenBase biomegenbase = CustomColors.getColorBiome(blockAccess, blockPos);
            return waterColors != null ? waterColors.getColor(biomegenbase, blockPos) : (Reflector.ForgeBiome_getWaterColorMultiplier.exists() ? Reflector.callInt(biomegenbase, Reflector.ForgeBiome_getWaterColorMultiplier, new Object[0]) : biomegenbase.waterColorMultiplier);
        }

        @Override
        public boolean isColorConstant() {
            return false;
        }
    };

    public static void update() {
        paletteFormatDefault = "vanilla";
        waterColors = null;
        foliageBirchColors = null;
        foliagePineColors = null;
        swampGrassColors = null;
        swampFoliageColors = null;
        skyColors = null;
        fogColors = null;
        underwaterColors = null;
        underlavaColors = null;
        redstoneColors = null;
        xpOrbColors = null;
        xpOrbTime = -1;
        durabilityColors = null;
        stemColors = null;
        myceliumParticleColors = null;
        lightMapPacks = null;
        particleWaterColor = -1;
        particlePortalColor = -1;
        lilyPadColor = -1;
        expBarTextColor = -1;
        bossTextColor = -1;
        signTextColor = -1;
        fogColorNether = null;
        fogColorEnd = null;
        skyColorEnd = null;
        colorsBlockColormaps = null;
        blockColormaps = null;
        useDefaultGrassFoliageColors = true;
        spawnEggPrimaryColors = null;
        spawnEggSecondaryColors = null;
        wolfCollarColors = null;
        sheepColors = null;
        textColors = null;
        CustomColors.setMapColors(mapColorsOriginal);
        potionColors = null;
        paletteFormatDefault = CustomColors.getValidProperty("mcpatcher/color.properties", "palette.format", CustomColormap.FORMAT_STRINGS, "vanilla");
        String s2 = "mcpatcher/colormap/";
        String[] astring = new String[]{"water.png", "watercolorX.png"};
        waterColors = CustomColors.getCustomColors(s2, astring, 256, 256);
        CustomColors.updateUseDefaultGrassFoliageColors();
        if (Config.isCustomColors()) {
            String[] astring1 = new String[]{"pine.png", "pinecolor.png"};
            foliagePineColors = CustomColors.getCustomColors(s2, astring1, 256, 256);
            String[] astring2 = new String[]{"birch.png", "birchcolor.png"};
            foliageBirchColors = CustomColors.getCustomColors(s2, astring2, 256, 256);
            String[] astring3 = new String[]{"swampgrass.png", "swampgrasscolor.png"};
            swampGrassColors = CustomColors.getCustomColors(s2, astring3, 256, 256);
            String[] astring4 = new String[]{"swampfoliage.png", "swampfoliagecolor.png"};
            swampFoliageColors = CustomColors.getCustomColors(s2, astring4, 256, 256);
            String[] astring5 = new String[]{"sky0.png", "skycolor0.png"};
            skyColors = CustomColors.getCustomColors(s2, astring5, 256, 256);
            String[] astring6 = new String[]{"fog0.png", "fogcolor0.png"};
            fogColors = CustomColors.getCustomColors(s2, astring6, 256, 256);
            String[] astring7 = new String[]{"underwater.png", "underwatercolor.png"};
            underwaterColors = CustomColors.getCustomColors(s2, astring7, 256, 256);
            String[] astring8 = new String[]{"underlava.png", "underlavacolor.png"};
            underlavaColors = CustomColors.getCustomColors(s2, astring8, 256, 256);
            String[] astring9 = new String[]{"redstone.png", "redstonecolor.png"};
            redstoneColors = CustomColors.getCustomColors(s2, astring9, 16, 1);
            xpOrbColors = CustomColors.getCustomColors(String.valueOf(s2) + "xporb.png", -1, -1);
            durabilityColors = CustomColors.getCustomColors(String.valueOf(s2) + "durability.png", -1, -1);
            String[] astring10 = new String[]{"stem.png", "stemcolor.png"};
            stemColors = CustomColors.getCustomColors(s2, astring10, 8, 1);
            stemPumpkinColors = CustomColors.getCustomColors(String.valueOf(s2) + "pumpkinstem.png", 8, 1);
            stemMelonColors = CustomColors.getCustomColors(String.valueOf(s2) + "melonstem.png", 8, 1);
            String[] astring11 = new String[]{"myceliumparticle.png", "myceliumparticlecolor.png"};
            myceliumParticleColors = CustomColors.getCustomColors(s2, astring11, -1, -1);
            Pair<LightMapPack[], Integer> pair = CustomColors.parseLightMapPacks();
            lightMapPacks = pair.getLeft();
            lightmapMinDimensionId = pair.getRight();
            CustomColors.readColorProperties("mcpatcher/color.properties");
            blockColormaps = CustomColors.readBlockColormaps(new String[]{String.valueOf(s2) + "custom/", String.valueOf(s2) + "blocks/"}, colorsBlockColormaps, 256, 256);
            CustomColors.updateUseDefaultGrassFoliageColors();
        }
    }

    private static String getValidProperty(String fileName, String key, String[] validValues, String valDef) {
        try {
            ResourceLocation resourcelocation = new ResourceLocation(fileName);
            InputStream inputstream = Config.getResourceStream(resourcelocation);
            if (inputstream == null) {
                return valDef;
            }
            PropertiesOrdered properties = new PropertiesOrdered();
            properties.load(inputstream);
            inputstream.close();
            String s2 = properties.getProperty(key);
            if (s2 == null) {
                return valDef;
            }
            List<String> list = Arrays.asList(validValues);
            if (!list.contains(s2)) {
                CustomColors.warn("Invalid value: " + key + "=" + s2);
                CustomColors.warn("Expected values: " + Config.arrayToString(validValues));
                return valDef;
            }
            CustomColors.dbg(key + "=" + s2);
            return s2;
        }
        catch (FileNotFoundException var9) {
            return valDef;
        }
        catch (IOException ioexception) {
            ioexception.printStackTrace();
            return valDef;
        }
    }

    private static Pair<LightMapPack[], Integer> parseLightMapPacks() {
        String s2 = "mcpatcher/lightmap/world";
        String s1 = ".png";
        String[] astring = ResUtils.collectFiles(s2, s1);
        HashMap<Integer, String> map = new HashMap<Integer, String>();
        int i2 = 0;
        while (i2 < astring.length) {
            String s22 = astring[i2];
            String s3 = StrUtils.removePrefixSuffix(s22, s2, s1);
            int j2 = Config.parseInt(s3, Integer.MIN_VALUE);
            if (j2 == Integer.MIN_VALUE) {
                CustomColors.warn("Invalid dimension ID: " + s3 + ", path: " + s22);
            } else {
                map.put(j2, s22);
            }
            ++i2;
        }
        Set set = map.keySet();
        Object[] ainteger = set.toArray(new Integer[set.size()]);
        Arrays.sort(ainteger);
        if (ainteger.length <= 0) {
            return new ImmutablePair<Object, Integer>(null, 0);
        }
        int j1 = (Integer)ainteger[0];
        int k1 = (Integer)ainteger[ainteger.length - 1];
        int k2 = k1 - j1 + 1;
        CustomColormap[] acustomcolormap = new CustomColormap[k2];
        int l2 = 0;
        while (l2 < ainteger.length) {
            Object integer = ainteger[l2];
            String s4 = (String)map.get(integer);
            CustomColormap customcolormap = CustomColors.getCustomColors(s4, -1, -1);
            if (customcolormap != null) {
                if (customcolormap.getWidth() < 16) {
                    CustomColors.warn("Invalid lightmap width: " + customcolormap.getWidth() + ", path: " + s4);
                } else {
                    int i1 = (Integer)integer - j1;
                    acustomcolormap[i1] = customcolormap;
                }
            }
            ++l2;
        }
        LightMapPack[] alightmappack = new LightMapPack[acustomcolormap.length];
        int l1 = 0;
        while (l1 < acustomcolormap.length) {
            CustomColormap customcolormap3 = acustomcolormap[l1];
            if (customcolormap3 != null) {
                LightMapPack lightmappack;
                String s5 = customcolormap3.name;
                String s6 = customcolormap3.basePath;
                CustomColormap customcolormap1 = CustomColors.getCustomColors(String.valueOf(s6) + "/" + s5 + "_rain.png", -1, -1);
                CustomColormap customcolormap2 = CustomColors.getCustomColors(String.valueOf(s6) + "/" + s5 + "_thunder.png", -1, -1);
                LightMap lightmap = new LightMap(customcolormap3);
                LightMap lightmap1 = customcolormap1 != null ? new LightMap(customcolormap1) : null;
                LightMap lightmap2 = customcolormap2 != null ? new LightMap(customcolormap2) : null;
                alightmappack[l1] = lightmappack = new LightMapPack(lightmap, lightmap1, lightmap2);
            }
            ++l1;
        }
        return new ImmutablePair<LightMapPack[], Integer>(alightmappack, j1);
    }

    private static int getTextureHeight(String path, int defHeight) {
        try {
            InputStream inputstream = Config.getResourceStream(new ResourceLocation(path));
            if (inputstream == null) {
                return defHeight;
            }
            BufferedImage bufferedimage = ImageIO.read(inputstream);
            inputstream.close();
            return bufferedimage == null ? defHeight : bufferedimage.getHeight();
        }
        catch (IOException var4) {
            return defHeight;
        }
    }

    private static void readColorProperties(String fileName) {
        try {
            ResourceLocation resourcelocation = new ResourceLocation(fileName);
            InputStream inputstream = Config.getResourceStream(resourcelocation);
            if (inputstream == null) {
                return;
            }
            CustomColors.dbg("Loading " + fileName);
            PropertiesOrdered properties = new PropertiesOrdered();
            properties.load(inputstream);
            inputstream.close();
            particleWaterColor = CustomColors.readColor((Properties)properties, new String[]{"particle.water", "drop.water"});
            particlePortalColor = CustomColors.readColor((Properties)properties, "particle.portal");
            lilyPadColor = CustomColors.readColor((Properties)properties, "lilypad");
            expBarTextColor = CustomColors.readColor((Properties)properties, "text.xpbar");
            bossTextColor = CustomColors.readColor((Properties)properties, "text.boss");
            signTextColor = CustomColors.readColor((Properties)properties, "text.sign");
            fogColorNether = CustomColors.readColorVec3(properties, "fog.nether");
            fogColorEnd = CustomColors.readColorVec3(properties, "fog.end");
            skyColorEnd = CustomColors.readColorVec3(properties, "sky.end");
            colorsBlockColormaps = CustomColors.readCustomColormaps(properties, fileName);
            spawnEggPrimaryColors = CustomColors.readSpawnEggColors(properties, fileName, "egg.shell.", "Spawn egg shell");
            spawnEggSecondaryColors = CustomColors.readSpawnEggColors(properties, fileName, "egg.spots.", "Spawn egg spot");
            wolfCollarColors = CustomColors.readDyeColors(properties, fileName, "collar.", "Wolf collar");
            sheepColors = CustomColors.readDyeColors(properties, fileName, "sheep.", "Sheep");
            textColors = CustomColors.readTextColors(properties, fileName, "text.code.", "Text");
            int[] aint = CustomColors.readMapColors(properties, fileName, "map.", "Map");
            if (aint != null) {
                if (mapColorsOriginal == null) {
                    mapColorsOriginal = CustomColors.getMapColors();
                }
                CustomColors.setMapColors(aint);
            }
            potionColors = CustomColors.readPotionColors(properties, fileName, "potion.", "Potion");
            xpOrbTime = Config.parseInt(properties.getProperty("xporb.time"), -1);
        }
        catch (FileNotFoundException var5) {
            return;
        }
        catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
    }

    private static CustomColormap[] readCustomColormaps(Properties props, String fileName) {
        ArrayList<CustomColormap> list = new ArrayList<CustomColormap>();
        String s2 = "palette.block.";
        HashMap<String, String> map = new HashMap<String, String>();
        for (Object o2 : props.keySet()) {
            String s1 = (String)o2;
            String s22 = props.getProperty(s1);
            if (!s1.startsWith(s2)) continue;
            map.put(s1, s22);
        }
        String[] astring = map.keySet().toArray(new String[map.size()]);
        int j2 = 0;
        while (j2 < astring.length) {
            String s6 = astring[j2];
            String s3 = props.getProperty(s6);
            CustomColors.dbg("Block palette: " + s6 + " = " + s3);
            String s4 = s6.substring(s2.length());
            String s5 = TextureUtils.getBasePath(fileName);
            s4 = TextureUtils.fixResourcePath(s4, s5);
            CustomColormap customcolormap = CustomColors.getCustomColors(s4, 256, 256);
            if (customcolormap == null) {
                CustomColors.warn("Colormap not found: " + s4);
            } else {
                ConnectedParser connectedparser = new ConnectedParser("CustomColors");
                MatchBlock[] amatchblock = connectedparser.parseMatchBlocks(s3);
                if (amatchblock != null && amatchblock.length > 0) {
                    int i2 = 0;
                    while (i2 < amatchblock.length) {
                        MatchBlock matchblock = amatchblock[i2];
                        customcolormap.addMatchBlock(matchblock);
                        ++i2;
                    }
                    list.add(customcolormap);
                } else {
                    CustomColors.warn("Invalid match blocks: " + s3);
                }
            }
            ++j2;
        }
        if (list.size() <= 0) {
            return null;
        }
        CustomColormap[] acustomcolormap = list.toArray(new CustomColormap[list.size()]);
        return acustomcolormap;
    }

    private static CustomColormap[][] readBlockColormaps(String[] basePaths, CustomColormap[] basePalettes, int width, int height) {
        Object[] astring = ResUtils.collectFiles(basePaths, new String[]{".properties"});
        Arrays.sort(astring);
        ArrayList list = new ArrayList();
        int i2 = 0;
        while (i2 < astring.length) {
            Object s2 = astring[i2];
            CustomColors.dbg("Block colormap: " + (String)s2);
            try {
                ResourceLocation resourcelocation = new ResourceLocation("minecraft", (String)s2);
                InputStream inputstream = Config.getResourceStream(resourcelocation);
                if (inputstream == null) {
                    CustomColors.warn("File not found: " + (String)s2);
                } else {
                    PropertiesOrdered properties = new PropertiesOrdered();
                    properties.load(inputstream);
                    inputstream.close();
                    CustomColormap customcolormap = new CustomColormap(properties, (String)s2, width, height, paletteFormatDefault);
                    if (customcolormap.isValid((String)s2) && customcolormap.isValidMatchBlocks((String)s2)) {
                        CustomColors.addToBlockList(customcolormap, list);
                    }
                }
            }
            catch (FileNotFoundException var12) {
                CustomColors.warn("File not found: " + (String)s2);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
            ++i2;
        }
        if (basePalettes != null) {
            int j2 = 0;
            while (j2 < basePalettes.length) {
                CustomColormap customcolormap1 = basePalettes[j2];
                CustomColors.addToBlockList(customcolormap1, list);
                ++j2;
            }
        }
        if (list.size() <= 0) {
            return null;
        }
        CustomColormap[][] acustomcolormap = CustomColors.blockListToArray(list);
        return acustomcolormap;
    }

    private static void addToBlockList(CustomColormap cm2, List blockList) {
        int[] aint = cm2.getMatchBlockIds();
        if (aint != null && aint.length > 0) {
            int i2 = 0;
            while (i2 < aint.length) {
                int j2 = aint[i2];
                if (j2 < 0) {
                    CustomColors.warn("Invalid block ID: " + j2);
                } else {
                    CustomColors.addToList(cm2, blockList, j2);
                }
                ++i2;
            }
        } else {
            CustomColors.warn("No match blocks: " + Config.arrayToString(aint));
        }
    }

    private static void addToList(CustomColormap cm2, List lists, int id2) {
        while (id2 >= lists.size()) {
            lists.add(null);
        }
        ArrayList<CustomColormap> list = (ArrayList<CustomColormap>)lists.get(id2);
        if (list == null) {
            list = new ArrayList<CustomColormap>();
            list.set(id2, (CustomColormap)((Object)list));
        }
        list.add(cm2);
    }

    private static CustomColormap[][] blockListToArray(List lists) {
        CustomColormap[][] acustomcolormap = new CustomColormap[lists.size()][];
        int i2 = 0;
        while (i2 < lists.size()) {
            List list = (List)lists.get(i2);
            if (list != null) {
                CustomColormap[] acustomcolormap1 = list.toArray(new CustomColormap[list.size()]);
                acustomcolormap[i2] = acustomcolormap1;
            }
            ++i2;
        }
        return acustomcolormap;
    }

    private static int readColor(Properties props, String[] names) {
        int i2 = 0;
        while (i2 < names.length) {
            String s2 = names[i2];
            int j2 = CustomColors.readColor(props, s2);
            if (j2 >= 0) {
                return j2;
            }
            ++i2;
        }
        return -1;
    }

    private static int readColor(Properties props, String name) {
        String s2 = props.getProperty(name);
        if (s2 == null) {
            return -1;
        }
        int i2 = CustomColors.parseColor(s2 = s2.trim());
        if (i2 < 0) {
            CustomColors.warn("Invalid color: " + name + " = " + s2);
            return i2;
        }
        CustomColors.dbg(String.valueOf(name) + " = " + s2);
        return i2;
    }

    private static int parseColor(String str) {
        if (str == null) {
            return -1;
        }
        str = str.trim();
        try {
            int i2 = Integer.parseInt(str, 16) & 0xFFFFFF;
            return i2;
        }
        catch (NumberFormatException var2) {
            return -1;
        }
    }

    private static Vec3 readColorVec3(Properties props, String name) {
        int i2 = CustomColors.readColor(props, name);
        if (i2 < 0) {
            return null;
        }
        int j2 = i2 >> 16 & 0xFF;
        int k2 = i2 >> 8 & 0xFF;
        int l2 = i2 & 0xFF;
        float f2 = (float)j2 / 255.0f;
        float f1 = (float)k2 / 255.0f;
        float f22 = (float)l2 / 255.0f;
        return new Vec3(f2, f1, f22);
    }

    private static CustomColormap getCustomColors(String basePath, String[] paths, int width, int height) {
        int i2 = 0;
        while (i2 < paths.length) {
            String s2 = paths[i2];
            s2 = String.valueOf(basePath) + s2;
            CustomColormap customcolormap = CustomColors.getCustomColors(s2, width, height);
            if (customcolormap != null) {
                return customcolormap;
            }
            ++i2;
        }
        return null;
    }

    public static CustomColormap getCustomColors(String pathImage, int width, int height) {
        block5: {
            try {
                ResourceLocation resourcelocation = new ResourceLocation(pathImage);
                if (Config.hasResource(resourcelocation)) break block5;
                return null;
            }
            catch (Exception exception) {
                exception.printStackTrace();
                return null;
            }
        }
        CustomColors.dbg("Colormap " + pathImage);
        PropertiesOrdered properties = new PropertiesOrdered();
        String s2 = StrUtils.replaceSuffix(pathImage, ".png", ".properties");
        ResourceLocation resourcelocation1 = new ResourceLocation(s2);
        if (Config.hasResource(resourcelocation1)) {
            InputStream inputstream = Config.getResourceStream(resourcelocation1);
            properties.load(inputstream);
            inputstream.close();
            CustomColors.dbg("Colormap properties: " + s2);
        } else {
            ((Properties)properties).put("format", paletteFormatDefault);
            ((Properties)properties).put("source", pathImage);
            s2 = pathImage;
        }
        CustomColormap customcolormap = new CustomColormap(properties, s2, width, height, paletteFormatDefault);
        return !customcolormap.isValid(s2) ? null : customcolormap;
    }

    public static void updateUseDefaultGrassFoliageColors() {
        useDefaultGrassFoliageColors = foliageBirchColors == null && foliagePineColors == null && swampGrassColors == null && swampFoliageColors == null && Config.isSwampColors() && Config.isSmoothBiomes();
    }

    public static int getColorMultiplier(BakedQuad quad, IBlockState blockState, IBlockAccess blockAccess, BlockPos blockPos, RenderEnv renderEnv) {
        IColorizer customcolors$icolorizer;
        Block block = blockState.getBlock();
        IBlockState iblockstate = renderEnv.getBlockState();
        if (blockColormaps != null) {
            CustomColormap customcolormap;
            if (!quad.hasTintIndex()) {
                if (block == Blocks.grass) {
                    iblockstate = BLOCK_STATE_DIRT;
                }
                if (block == Blocks.redstone_wire) {
                    return -1;
                }
            }
            if (block == Blocks.double_plant && renderEnv.getMetadata() >= 8) {
                blockPos = blockPos.down();
                iblockstate = blockAccess.getBlockState(blockPos);
            }
            if ((customcolormap = CustomColors.getBlockColormap(iblockstate)) != null) {
                if (Config.isSmoothBiomes() && !customcolormap.isColorConstant()) {
                    return CustomColors.getSmoothColorMultiplier(blockState, blockAccess, blockPos, customcolormap, renderEnv.getColorizerBlockPosM());
                }
                return customcolormap.getColor(blockAccess, blockPos);
            }
        }
        if (!quad.hasTintIndex()) {
            return -1;
        }
        if (block == Blocks.waterlily) {
            return CustomColors.getLilypadColorMultiplier(blockAccess, blockPos);
        }
        if (block == Blocks.redstone_wire) {
            return CustomColors.getRedstoneColor(renderEnv.getBlockState());
        }
        if (block instanceof BlockStem) {
            return CustomColors.getStemColorMultiplier(block, blockAccess, blockPos, renderEnv);
        }
        if (useDefaultGrassFoliageColors) {
            return -1;
        }
        int i2 = renderEnv.getMetadata();
        if (block != Blocks.grass && block != Blocks.tallgrass && block != Blocks.double_plant) {
            if (block == Blocks.double_plant) {
                customcolors$icolorizer = COLORIZER_GRASS;
                if (i2 >= 8) {
                    blockPos = blockPos.down();
                }
            } else if (block == Blocks.leaves) {
                switch (i2 & 3) {
                    case 0: {
                        customcolors$icolorizer = COLORIZER_FOLIAGE;
                        break;
                    }
                    case 1: {
                        customcolors$icolorizer = COLORIZER_FOLIAGE_PINE;
                        break;
                    }
                    case 2: {
                        customcolors$icolorizer = COLORIZER_FOLIAGE_BIRCH;
                        break;
                    }
                    default: {
                        customcolors$icolorizer = COLORIZER_FOLIAGE;
                        break;
                    }
                }
            } else if (block == Blocks.leaves2) {
                customcolors$icolorizer = COLORIZER_FOLIAGE;
            } else {
                if (block != Blocks.vine) {
                    return -1;
                }
                customcolors$icolorizer = COLORIZER_FOLIAGE;
            }
        } else {
            customcolors$icolorizer = COLORIZER_GRASS;
        }
        return Config.isSmoothBiomes() && !customcolors$icolorizer.isColorConstant() ? CustomColors.getSmoothColorMultiplier(blockState, blockAccess, blockPos, customcolors$icolorizer, renderEnv.getColorizerBlockPosM()) : customcolors$icolorizer.getColor(iblockstate, blockAccess, blockPos);
    }

    protected static BiomeGenBase getColorBiome(IBlockAccess blockAccess, BlockPos blockPos) {
        BiomeGenBase biomegenbase = blockAccess.getBiomeGenForCoords(blockPos);
        if (biomegenbase == BiomeGenBase.swampland && !Config.isSwampColors()) {
            biomegenbase = BiomeGenBase.plains;
        }
        return biomegenbase;
    }

    private static CustomColormap getBlockColormap(IBlockState blockState) {
        if (blockColormaps == null) {
            return null;
        }
        if (!(blockState instanceof BlockStateBase)) {
            return null;
        }
        BlockStateBase blockstatebase = (BlockStateBase)blockState;
        int i2 = blockstatebase.getBlockId();
        if (i2 >= 0 && i2 < blockColormaps.length) {
            CustomColormap[] acustomcolormap = blockColormaps[i2];
            if (acustomcolormap == null) {
                return null;
            }
            int j2 = 0;
            while (j2 < acustomcolormap.length) {
                CustomColormap customcolormap = acustomcolormap[j2];
                if (customcolormap.matchesBlock(blockstatebase)) {
                    return customcolormap;
                }
                ++j2;
            }
            return null;
        }
        return null;
    }

    private static int getSmoothColorMultiplier(IBlockState blockState, IBlockAccess blockAccess, BlockPos blockPos, IColorizer colorizer, BlockPosM blockPosM) {
        int i2 = 0;
        int j2 = 0;
        int k2 = 0;
        int l2 = blockPos.getX();
        int i1 = blockPos.getY();
        int j1 = blockPos.getZ();
        BlockPosM blockposm = blockPosM;
        int k1 = l2 - 1;
        while (k1 <= l2 + 1) {
            int l1 = j1 - 1;
            while (l1 <= j1 + 1) {
                blockposm.setXyz(k1, i1, l1);
                int i22 = colorizer.getColor(blockState, blockAccess, blockposm);
                i2 += i22 >> 16 & 0xFF;
                j2 += i22 >> 8 & 0xFF;
                k2 += i22 & 0xFF;
                ++l1;
            }
            ++k1;
        }
        int j22 = i2 / 9;
        int k22 = j2 / 9;
        int l22 = k2 / 9;
        return j22 << 16 | k22 << 8 | l22;
    }

    public static int getFluidColor(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, RenderEnv renderEnv) {
        Block block = blockState.getBlock();
        IColorizer customcolors$icolorizer = CustomColors.getBlockColormap(blockState);
        if (customcolors$icolorizer == null && blockState.getBlock().getMaterial() == Material.water) {
            customcolors$icolorizer = COLORIZER_WATER;
        }
        return customcolors$icolorizer == null ? block.colorMultiplier(blockAccess, blockPos, 0) : (Config.isSmoothBiomes() && !customcolors$icolorizer.isColorConstant() ? CustomColors.getSmoothColorMultiplier(blockState, blockAccess, blockPos, customcolors$icolorizer, renderEnv.getColorizerBlockPosM()) : customcolors$icolorizer.getColor(blockState, blockAccess, blockPos));
    }

    public static void updatePortalFX(EntityFX fx2) {
        if (particlePortalColor >= 0) {
            int i2 = particlePortalColor;
            int j2 = i2 >> 16 & 0xFF;
            int k2 = i2 >> 8 & 0xFF;
            int l2 = i2 & 0xFF;
            float f2 = (float)j2 / 255.0f;
            float f1 = (float)k2 / 255.0f;
            float f22 = (float)l2 / 255.0f;
            fx2.setRBGColorF(f2, f1, f22);
        }
    }

    public static void updateMyceliumFX(EntityFX fx2) {
        if (myceliumParticleColors != null) {
            int i2 = myceliumParticleColors.getColorRandom();
            int j2 = i2 >> 16 & 0xFF;
            int k2 = i2 >> 8 & 0xFF;
            int l2 = i2 & 0xFF;
            float f2 = (float)j2 / 255.0f;
            float f1 = (float)k2 / 255.0f;
            float f22 = (float)l2 / 255.0f;
            fx2.setRBGColorF(f2, f1, f22);
        }
    }

    private static int getRedstoneColor(IBlockState blockState) {
        if (redstoneColors == null) {
            return -1;
        }
        int i2 = CustomColors.getRedstoneLevel(blockState, 15);
        int j2 = redstoneColors.getColor(i2);
        return j2;
    }

    public static void updateReddustFX(EntityFX fx2, IBlockAccess blockAccess, double x2, double y2, double z2) {
        if (redstoneColors != null) {
            IBlockState iblockstate = blockAccess.getBlockState(new BlockPos(x2, y2, z2));
            int i2 = CustomColors.getRedstoneLevel(iblockstate, 15);
            int j2 = redstoneColors.getColor(i2);
            int k2 = j2 >> 16 & 0xFF;
            int l2 = j2 >> 8 & 0xFF;
            int i1 = j2 & 0xFF;
            float f2 = (float)k2 / 255.0f;
            float f1 = (float)l2 / 255.0f;
            float f22 = (float)i1 / 255.0f;
            fx2.setRBGColorF(f2, f1, f22);
        }
    }

    private static int getRedstoneLevel(IBlockState state, int def) {
        Block block = state.getBlock();
        if (!(block instanceof BlockRedstoneWire)) {
            return def;
        }
        Integer object = state.getValue(BlockRedstoneWire.POWER);
        if (!(object instanceof Integer)) {
            return def;
        }
        Integer integer = object;
        return integer;
    }

    public static float getXpOrbTimer(float timer) {
        if (xpOrbTime <= 0) {
            return timer;
        }
        float f2 = 628.0f / (float)xpOrbTime;
        return timer * f2;
    }

    public static int getXpOrbColor(float timer) {
        if (xpOrbColors == null) {
            return -1;
        }
        int i2 = (int)Math.round((double)((MathHelper.sin(timer) + 1.0f) * (float)(xpOrbColors.getLength() - 1)) / 2.0);
        int j2 = xpOrbColors.getColor(i2);
        return j2;
    }

    public static int getDurabilityColor(int dur255) {
        if (durabilityColors == null) {
            return -1;
        }
        int i2 = dur255 * durabilityColors.getLength() / 255;
        int j2 = durabilityColors.getColor(i2);
        return j2;
    }

    public static void updateWaterFX(EntityFX fx2, IBlockAccess blockAccess, double x2, double y2, double z2, RenderEnv renderEnv) {
        if (waterColors != null || blockColormaps != null || particleWaterColor >= 0) {
            BlockPos blockpos = new BlockPos(x2, y2, z2);
            renderEnv.reset(BLOCK_STATE_WATER, blockpos);
            int i2 = CustomColors.getFluidColor(blockAccess, BLOCK_STATE_WATER, blockpos, renderEnv);
            int j2 = i2 >> 16 & 0xFF;
            int k2 = i2 >> 8 & 0xFF;
            int l2 = i2 & 0xFF;
            float f2 = (float)j2 / 255.0f;
            float f1 = (float)k2 / 255.0f;
            float f22 = (float)l2 / 255.0f;
            if (particleWaterColor >= 0) {
                int i1 = particleWaterColor >> 16 & 0xFF;
                int j1 = particleWaterColor >> 8 & 0xFF;
                int k1 = particleWaterColor & 0xFF;
                f2 *= (float)i1 / 255.0f;
                f1 *= (float)j1 / 255.0f;
                f22 *= (float)k1 / 255.0f;
            }
            fx2.setRBGColorF(f2, f1, f22);
        }
    }

    private static int getLilypadColorMultiplier(IBlockAccess blockAccess, BlockPos blockPos) {
        return lilyPadColor < 0 ? Blocks.waterlily.colorMultiplier(blockAccess, blockPos) : lilyPadColor;
    }

    private static Vec3 getFogColorNether(Vec3 col) {
        return fogColorNether == null ? col : fogColorNether;
    }

    private static Vec3 getFogColorEnd(Vec3 col) {
        return fogColorEnd == null ? col : fogColorEnd;
    }

    private static Vec3 getSkyColorEnd(Vec3 col) {
        return skyColorEnd == null ? col : skyColorEnd;
    }

    public static Vec3 getSkyColor(Vec3 skyColor3d, IBlockAccess blockAccess, double x2, double y2, double z2) {
        if (skyColors == null) {
            return skyColor3d;
        }
        int i2 = skyColors.getColorSmooth(blockAccess, x2, y2, z2, 3);
        int j2 = i2 >> 16 & 0xFF;
        int k2 = i2 >> 8 & 0xFF;
        int l2 = i2 & 0xFF;
        float f2 = (float)j2 / 255.0f;
        float f1 = (float)k2 / 255.0f;
        float f22 = (float)l2 / 255.0f;
        float f3 = (float)skyColor3d.xCoord / 0.5f;
        float f4 = (float)skyColor3d.yCoord / 0.66275f;
        float f5 = (float)skyColor3d.zCoord;
        Vec3 vec3 = skyColorFader.getColor(f2 *= f3, f1 *= f4, f22 *= f5);
        return vec3;
    }

    private static Vec3 getFogColor(Vec3 fogColor3d, IBlockAccess blockAccess, double x2, double y2, double z2) {
        if (fogColors == null) {
            return fogColor3d;
        }
        int i2 = fogColors.getColorSmooth(blockAccess, x2, y2, z2, 3);
        int j2 = i2 >> 16 & 0xFF;
        int k2 = i2 >> 8 & 0xFF;
        int l2 = i2 & 0xFF;
        float f2 = (float)j2 / 255.0f;
        float f1 = (float)k2 / 255.0f;
        float f22 = (float)l2 / 255.0f;
        float f3 = (float)fogColor3d.xCoord / 0.753f;
        float f4 = (float)fogColor3d.yCoord / 0.8471f;
        float f5 = (float)fogColor3d.zCoord;
        Vec3 vec3 = fogColorFader.getColor(f2 *= f3, f1 *= f4, f22 *= f5);
        return vec3;
    }

    public static Vec3 getUnderwaterColor(IBlockAccess blockAccess, double x2, double y2, double z2) {
        return CustomColors.getUnderFluidColor(blockAccess, x2, y2, z2, underwaterColors, underwaterColorFader);
    }

    public static Vec3 getUnderlavaColor(IBlockAccess blockAccess, double x2, double y2, double z2) {
        return CustomColors.getUnderFluidColor(blockAccess, x2, y2, z2, underlavaColors, underlavaColorFader);
    }

    public static Vec3 getUnderFluidColor(IBlockAccess blockAccess, double x2, double y2, double z2, CustomColormap underFluidColors, CustomColorFader underFluidColorFader) {
        if (underFluidColors == null) {
            return null;
        }
        int i2 = underFluidColors.getColorSmooth(blockAccess, x2, y2, z2, 3);
        int j2 = i2 >> 16 & 0xFF;
        int k2 = i2 >> 8 & 0xFF;
        int l2 = i2 & 0xFF;
        float f2 = (float)j2 / 255.0f;
        float f1 = (float)k2 / 255.0f;
        float f22 = (float)l2 / 255.0f;
        Vec3 vec3 = underFluidColorFader.getColor(f2, f1, f22);
        return vec3;
    }

    private static int getStemColorMultiplier(Block blockStem, IBlockAccess blockAccess, BlockPos blockPos, RenderEnv renderEnv) {
        CustomColormap customcolormap = stemColors;
        if (blockStem == Blocks.pumpkin_stem && stemPumpkinColors != null) {
            customcolormap = stemPumpkinColors;
        }
        if (blockStem == Blocks.melon_stem && stemMelonColors != null) {
            customcolormap = stemMelonColors;
        }
        if (customcolormap == null) {
            return -1;
        }
        int i2 = renderEnv.getMetadata();
        return customcolormap.getColor(i2);
    }

    public static boolean updateLightmap(World world, float torchFlickerX, int[] lmColors, boolean nightvision, float partialTicks) {
        if (world == null) {
            return false;
        }
        if (lightMapPacks == null) {
            return false;
        }
        int i2 = world.provider.getDimensionId();
        int j2 = i2 - lightmapMinDimensionId;
        if (j2 >= 0 && j2 < lightMapPacks.length) {
            LightMapPack lightmappack = lightMapPacks[j2];
            return lightmappack == null ? false : lightmappack.updateLightmap(world, torchFlickerX, lmColors, nightvision, partialTicks);
        }
        return false;
    }

    public static Vec3 getWorldFogColor(Vec3 fogVec, World world, Entity renderViewEntity, float partialTicks) {
        int i2 = world.provider.getDimensionId();
        switch (i2) {
            case -1: {
                fogVec = CustomColors.getFogColorNether(fogVec);
                break;
            }
            case 0: {
                Minecraft minecraft = Minecraft.getMinecraft();
                fogVec = CustomColors.getFogColor(fogVec, minecraft.theWorld, renderViewEntity.posX, renderViewEntity.posY + 1.0, renderViewEntity.posZ);
                break;
            }
            case 1: {
                fogVec = CustomColors.getFogColorEnd(fogVec);
            }
        }
        return fogVec;
    }

    public static Vec3 getWorldSkyColor(Vec3 skyVec, World world, Entity renderViewEntity, float partialTicks) {
        int i2 = world.provider.getDimensionId();
        switch (i2) {
            case 0: {
                Minecraft minecraft = Minecraft.getMinecraft();
                skyVec = CustomColors.getSkyColor(skyVec, minecraft.theWorld, renderViewEntity.posX, renderViewEntity.posY + 1.0, renderViewEntity.posZ);
                break;
            }
            case 1: {
                skyVec = CustomColors.getSkyColorEnd(skyVec);
            }
        }
        return skyVec;
    }

    /*
     * Unable to fully structure code
     */
    private static int[] readSpawnEggColors(Properties props, String fileName, String prefix, String logName) {
        list = new ArrayList<Integer>();
        set = props.keySet();
        i = 0;
        for (Object o : set) {
            s = (String)o;
            s1 = props.getProperty(s);
            if (!s.startsWith(prefix)) continue;
            s2 = StrUtils.removePrefix(s, prefix);
            j = EntityUtils.getEntityIdByName(s2);
            if (j < 0) {
                CustomColors.warn("Invalid spawn egg name: " + s);
                continue;
            }
            k = CustomColors.parseColor(s1);
            if (k >= 0) ** GOTO lbl19
            CustomColors.warn("Invalid spawn egg color: " + s + " = " + s1);
            continue;
lbl-1000:
            // 1 sources

            {
                list.add(-1);
lbl19:
                // 2 sources

                ** while (list.size() <= j)
            }
lbl20:
            // 1 sources

            list.set(j, k);
            ++i;
        }
        if (i <= 0) {
            return null;
        }
        CustomColors.dbg(String.valueOf(logName) + " colors: " + i);
        aint = new int[list.size()];
        l = 0;
        while (l < aint.length) {
            aint[l] = (Integer)list.get(l);
            ++l;
        }
        return aint;
    }

    private static int getSpawnEggColor(ItemMonsterPlacer item, ItemStack itemStack, int layer, int color) {
        int[] aint;
        int i2 = itemStack.getMetadata();
        int[] nArray = aint = layer == 0 ? spawnEggPrimaryColors : spawnEggSecondaryColors;
        if (aint == null) {
            return color;
        }
        if (i2 >= 0 && i2 < aint.length) {
            int j2 = aint[i2];
            return j2 < 0 ? color : j2;
        }
        return color;
    }

    public static int getColorFromItemStack(ItemStack itemStack, int layer, int color) {
        if (itemStack == null) {
            return color;
        }
        Item item = itemStack.getItem();
        return item == null ? color : (item instanceof ItemMonsterPlacer ? CustomColors.getSpawnEggColor((ItemMonsterPlacer)item, itemStack, layer, color) : color);
    }

    private static float[][] readDyeColors(Properties props, String fileName, String prefix, String logName) {
        EnumDyeColor[] aenumdyecolor = EnumDyeColor.values();
        HashMap<String, EnumDyeColor> map = new HashMap<String, EnumDyeColor>();
        int i2 = 0;
        while (i2 < aenumdyecolor.length) {
            EnumDyeColor enumdyecolor = aenumdyecolor[i2];
            map.put(enumdyecolor.getName(), enumdyecolor);
            ++i2;
        }
        float[][] afloat1 = new float[aenumdyecolor.length][];
        int k2 = 0;
        for (Object o2 : props.keySet()) {
            String s2 = (String)o2;
            String s1 = props.getProperty(s2);
            if (!s2.startsWith(prefix)) continue;
            String s22 = StrUtils.removePrefix(s2, prefix);
            if (s22.equals("lightBlue")) {
                s22 = "light_blue";
            }
            EnumDyeColor enumdyecolor1 = (EnumDyeColor)map.get(s22);
            int j2 = CustomColors.parseColor(s1);
            if (enumdyecolor1 != null && j2 >= 0) {
                float[] afloat = new float[]{(float)(j2 >> 16 & 0xFF) / 255.0f, (float)(j2 >> 8 & 0xFF) / 255.0f, (float)(j2 & 0xFF) / 255.0f};
                afloat1[enumdyecolor1.ordinal()] = afloat;
                ++k2;
                continue;
            }
            CustomColors.warn("Invalid color: " + s2 + " = " + s1);
        }
        if (k2 <= 0) {
            return null;
        }
        CustomColors.dbg(String.valueOf(logName) + " colors: " + k2);
        return afloat1;
    }

    private static float[] getDyeColors(EnumDyeColor dye, float[][] dyeColors, float[] colors) {
        if (dyeColors == null) {
            return colors;
        }
        if (dye == null) {
            return colors;
        }
        float[] afloat = dyeColors[dye.ordinal()];
        return afloat == null ? colors : afloat;
    }

    public static float[] getWolfCollarColors(EnumDyeColor dye, float[] colors) {
        return CustomColors.getDyeColors(dye, wolfCollarColors, colors);
    }

    public static float[] getSheepColors(EnumDyeColor dye, float[] colors) {
        return CustomColors.getDyeColors(dye, sheepColors, colors);
    }

    private static int[] readTextColors(Properties props, String fileName, String prefix, String logName) {
        int[] aint = new int[32];
        Arrays.fill(aint, -1);
        int i2 = 0;
        for (Object o2 : props.keySet()) {
            String s2 = (String)o2;
            String s1 = props.getProperty(s2);
            if (!s2.startsWith(prefix)) continue;
            String s22 = StrUtils.removePrefix(s2, prefix);
            int j2 = Config.parseInt(s22, -1);
            int k2 = CustomColors.parseColor(s1);
            if (j2 >= 0 && j2 < aint.length && k2 >= 0) {
                aint[j2] = k2;
                ++i2;
                continue;
            }
            CustomColors.warn("Invalid color: " + s2 + " = " + s1);
        }
        if (i2 <= 0) {
            return null;
        }
        CustomColors.dbg(String.valueOf(logName) + " colors: " + i2);
        return aint;
    }

    public static int getTextColor(int index, int color) {
        if (textColors == null) {
            return color;
        }
        if (index >= 0 && index < textColors.length) {
            int i2 = textColors[index];
            return i2 < 0 ? color : i2;
        }
        return color;
    }

    private static int[] readMapColors(Properties props, String fileName, String prefix, String logName) {
        int[] aint = new int[MapColor.mapColorArray.length];
        Arrays.fill(aint, -1);
        int i2 = 0;
        for (Object o2 : props.keySet()) {
            String s2 = (String)o2;
            String s1 = props.getProperty(s2);
            if (!s2.startsWith(prefix)) continue;
            String s22 = StrUtils.removePrefix(s2, prefix);
            int j2 = CustomColors.getMapColorIndex(s22);
            int k2 = CustomColors.parseColor(s1);
            if (j2 >= 0 && j2 < aint.length && k2 >= 0) {
                aint[j2] = k2;
                ++i2;
                continue;
            }
            CustomColors.warn("Invalid color: " + s2 + " = " + s1);
        }
        if (i2 <= 0) {
            return null;
        }
        CustomColors.dbg(String.valueOf(logName) + " colors: " + i2);
        return aint;
    }

    private static int[] readPotionColors(Properties props, String fileName, String prefix, String logName) {
        int[] aint = new int[Potion.potionTypes.length];
        Arrays.fill(aint, -1);
        int i2 = 0;
        for (Object o2 : props.keySet()) {
            String s2 = (String)o2;
            String s1 = props.getProperty(s2);
            if (!s2.startsWith(prefix)) continue;
            int j2 = CustomColors.getPotionId(s2);
            int k2 = CustomColors.parseColor(s1);
            if (j2 >= 0 && j2 < aint.length && k2 >= 0) {
                aint[j2] = k2;
                ++i2;
                continue;
            }
            CustomColors.warn("Invalid color: " + s2 + " = " + s1);
        }
        if (i2 <= 0) {
            return null;
        }
        CustomColors.dbg(String.valueOf(logName) + " colors: " + i2);
        return aint;
    }

    private static int getPotionId(String name) {
        if (name.equals("potion.water")) {
            return 0;
        }
        Potion[] apotion = Potion.potionTypes;
        int i2 = 0;
        while (i2 < apotion.length) {
            Potion potion = apotion[i2];
            if (potion != null && potion.getName().equals(name)) {
                return potion.getId();
            }
            ++i2;
        }
        return -1;
    }

    public static int getPotionColor(int potionId, int color) {
        if (potionColors == null) {
            return color;
        }
        if (potionId >= 0 && potionId < potionColors.length) {
            int i2 = potionColors[potionId];
            return i2 < 0 ? color : i2;
        }
        return color;
    }

    private static int getMapColorIndex(String name) {
        return name == null ? -1 : (name.equals("air") ? MapColor.airColor.colorIndex : (name.equals("grass") ? MapColor.grassColor.colorIndex : (name.equals("sand") ? MapColor.sandColor.colorIndex : (name.equals("cloth") ? MapColor.clothColor.colorIndex : (name.equals("tnt") ? MapColor.tntColor.colorIndex : (name.equals("ice") ? MapColor.iceColor.colorIndex : (name.equals("iron") ? MapColor.ironColor.colorIndex : (name.equals("foliage") ? MapColor.foliageColor.colorIndex : (name.equals("clay") ? MapColor.clayColor.colorIndex : (name.equals("dirt") ? MapColor.dirtColor.colorIndex : (name.equals("stone") ? MapColor.stoneColor.colorIndex : (name.equals("water") ? MapColor.waterColor.colorIndex : (name.equals("wood") ? MapColor.woodColor.colorIndex : (name.equals("quartz") ? MapColor.quartzColor.colorIndex : (name.equals("gold") ? MapColor.goldColor.colorIndex : (name.equals("diamond") ? MapColor.diamondColor.colorIndex : (name.equals("lapis") ? MapColor.lapisColor.colorIndex : (name.equals("emerald") ? MapColor.emeraldColor.colorIndex : (name.equals("podzol") ? MapColor.obsidianColor.colorIndex : (name.equals("netherrack") ? MapColor.netherrackColor.colorIndex : (!name.equals("snow") && !name.equals("white") ? (!name.equals("adobe") && !name.equals("orange") ? (name.equals("magenta") ? MapColor.magentaColor.colorIndex : (!name.equals("light_blue") && !name.equals("lightBlue") ? (name.equals("yellow") ? MapColor.yellowColor.colorIndex : (name.equals("lime") ? MapColor.limeColor.colorIndex : (name.equals("pink") ? MapColor.pinkColor.colorIndex : (name.equals("gray") ? MapColor.grayColor.colorIndex : (name.equals("silver") ? MapColor.silverColor.colorIndex : (name.equals("cyan") ? MapColor.cyanColor.colorIndex : (name.equals("purple") ? MapColor.purpleColor.colorIndex : (name.equals("blue") ? MapColor.blueColor.colorIndex : (name.equals("brown") ? MapColor.brownColor.colorIndex : (name.equals("green") ? MapColor.greenColor.colorIndex : (name.equals("red") ? MapColor.redColor.colorIndex : (name.equals("black") ? MapColor.blackColor.colorIndex : -1)))))))))))) : MapColor.lightBlueColor.colorIndex)) : MapColor.adobeColor.colorIndex) : MapColor.snowColor.colorIndex)))))))))))))))))))));
    }

    private static int[] getMapColors() {
        MapColor[] amapcolor = MapColor.mapColorArray;
        int[] aint = new int[amapcolor.length];
        Arrays.fill(aint, -1);
        int i2 = 0;
        while (i2 < amapcolor.length && i2 < aint.length) {
            MapColor mapcolor = amapcolor[i2];
            if (mapcolor != null) {
                aint[i2] = mapcolor.colorValue;
            }
            ++i2;
        }
        return aint;
    }

    private static void setMapColors(int[] colors) {
        if (colors != null) {
            MapColor[] amapcolor = MapColor.mapColorArray;
            boolean flag = false;
            int i2 = 0;
            while (i2 < amapcolor.length && i2 < colors.length) {
                int j2;
                MapColor mapcolor = amapcolor[i2];
                if (mapcolor != null && (j2 = colors[i2]) >= 0 && mapcolor.colorValue != j2) {
                    mapcolor.colorValue = j2;
                    flag = true;
                }
                ++i2;
            }
            if (flag) {
                Minecraft.getMinecraft().getTextureManager().reloadBannerTextures();
            }
        }
    }

    private static void dbg(String str) {
        Config.dbg("CustomColors: " + str);
    }

    private static void warn(String str) {
        Config.warn("CustomColors: " + str);
    }

    public static int getExpBarTextColor(int color) {
        return expBarTextColor < 0 ? color : expBarTextColor;
    }

    public static int getBossTextColor(int color) {
        return bossTextColor < 0 ? color : bossTextColor;
    }

    public static int getSignTextColor(int color) {
        return signTextColor < 0 ? color : signTextColor;
    }

    public static interface IColorizer {
        public int getColor(IBlockState var1, IBlockAccess var2, BlockPos var3);

        public boolean isColorConstant();
    }
}

