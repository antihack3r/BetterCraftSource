/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.BlockPosM;
import net.optifine.CustomColors;
import net.optifine.config.ConnectedParser;
import net.optifine.config.MatchBlock;
import net.optifine.config.Matches;
import net.optifine.util.TextureUtils;

public class CustomColormap
implements CustomColors.IColorizer {
    public String name = null;
    public String basePath = null;
    private int format = -1;
    private MatchBlock[] matchBlocks = null;
    private String source = null;
    private int color = -1;
    private int yVariance = 0;
    private int yOffset = 0;
    private int width = 0;
    private int height = 0;
    private int[] colors = null;
    private float[][] colorsRgb = null;
    private static final int FORMAT_UNKNOWN = -1;
    private static final int FORMAT_VANILLA = 0;
    private static final int FORMAT_GRID = 1;
    private static final int FORMAT_FIXED = 2;
    public static final String FORMAT_VANILLA_STRING = "vanilla";
    public static final String FORMAT_GRID_STRING = "grid";
    public static final String FORMAT_FIXED_STRING = "fixed";
    public static final String[] FORMAT_STRINGS = new String[]{"vanilla", "grid", "fixed"};
    public static final String KEY_FORMAT = "format";
    public static final String KEY_BLOCKS = "blocks";
    public static final String KEY_SOURCE = "source";
    public static final String KEY_COLOR = "color";
    public static final String KEY_Y_VARIANCE = "yVariance";
    public static final String KEY_Y_OFFSET = "yOffset";

    public CustomColormap(Properties props, String path, int width, int height, String formatDefault) {
        ConnectedParser connectedparser = new ConnectedParser("Colormap");
        this.name = connectedparser.parseName(path);
        this.basePath = connectedparser.parseBasePath(path);
        this.format = this.parseFormat(props.getProperty(KEY_FORMAT, formatDefault));
        this.matchBlocks = connectedparser.parseMatchBlocks(props.getProperty(KEY_BLOCKS));
        this.source = CustomColormap.parseTexture(props.getProperty(KEY_SOURCE), path, this.basePath);
        this.color = ConnectedParser.parseColor(props.getProperty(KEY_COLOR), -1);
        this.yVariance = connectedparser.parseInt(props.getProperty(KEY_Y_VARIANCE), 0);
        this.yOffset = connectedparser.parseInt(props.getProperty(KEY_Y_OFFSET), 0);
        this.width = width;
        this.height = height;
    }

    private int parseFormat(String str) {
        if (str == null) {
            return 0;
        }
        if ((str = str.trim()).equals(FORMAT_VANILLA_STRING)) {
            return 0;
        }
        if (str.equals(FORMAT_GRID_STRING)) {
            return 1;
        }
        if (str.equals(FORMAT_FIXED_STRING)) {
            return 2;
        }
        CustomColormap.warn("Unknown format: " + str);
        return -1;
    }

    public boolean isValid(String path) {
        if (this.format != 0 && this.format != 1) {
            if (this.format != 2) {
                return false;
            }
            if (this.color < 0) {
                this.color = 0xFFFFFF;
            }
        } else {
            if (this.source == null) {
                CustomColormap.warn("Source not defined: " + path);
                return false;
            }
            this.readColors();
            if (this.colors == null) {
                return false;
            }
            if (this.color < 0) {
                if (this.format == 0) {
                    this.color = this.getColor(127, 127);
                }
                if (this.format == 1) {
                    this.color = this.getColorGrid(BiomeGenBase.plains, new BlockPos(0, 64, 0));
                }
            }
        }
        return true;
    }

    public boolean isValidMatchBlocks(String path) {
        if (this.matchBlocks == null) {
            this.matchBlocks = this.detectMatchBlocks();
            if (this.matchBlocks == null) {
                CustomColormap.warn("Match blocks not defined: " + path);
                return false;
            }
        }
        return true;
    }

    private MatchBlock[] detectMatchBlocks() {
        String s2;
        int i2;
        Block block = Block.getBlockFromName(this.name);
        if (block != null) {
            return new MatchBlock[]{new MatchBlock(Block.getIdFromBlock(block))};
        }
        Pattern pattern = Pattern.compile("^block([0-9]+).*$");
        Matcher matcher = pattern.matcher(this.name);
        if (matcher.matches() && (i2 = Config.parseInt(s2 = matcher.group(1), -1)) >= 0) {
            return new MatchBlock[]{new MatchBlock(i2)};
        }
        ConnectedParser connectedparser = new ConnectedParser("Colormap");
        MatchBlock[] amatchblock = connectedparser.parseMatchBlock(this.name);
        return amatchblock != null ? amatchblock : null;
    }

    private void readColors() {
        try {
            boolean flag1;
            this.colors = null;
            if (this.source == null) {
                return;
            }
            String s2 = String.valueOf(this.source) + ".png";
            ResourceLocation resourcelocation = new ResourceLocation(s2);
            InputStream inputstream = Config.getResourceStream(resourcelocation);
            if (inputstream == null) {
                return;
            }
            BufferedImage bufferedimage = TextureUtil.readBufferedImage(inputstream);
            if (bufferedimage == null) {
                return;
            }
            int i2 = bufferedimage.getWidth();
            int j2 = bufferedimage.getHeight();
            boolean flag = this.width < 0 || this.width == i2;
            boolean bl2 = flag1 = this.height < 0 || this.height == j2;
            if (!flag || !flag1) {
                CustomColormap.dbg("Non-standard palette size: " + i2 + "x" + j2 + ", should be: " + this.width + "x" + this.height + ", path: " + s2);
            }
            this.width = i2;
            this.height = j2;
            if (this.width <= 0 || this.height <= 0) {
                CustomColormap.warn("Invalid palette size: " + i2 + "x" + j2 + ", path: " + s2);
                return;
            }
            this.colors = new int[i2 * j2];
            bufferedimage.getRGB(0, 0, i2, j2, this.colors, 0, i2);
        }
        catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
    }

    private static void dbg(String str) {
        Config.dbg("CustomColors: " + str);
    }

    private static void warn(String str) {
        Config.warn("CustomColors: " + str);
    }

    private static String parseTexture(String texStr, String path, String basePath) {
        int j2;
        if (texStr != null) {
            String s1;
            if ((texStr = texStr.trim()).endsWith(s1 = ".png")) {
                texStr = texStr.substring(0, texStr.length() - s1.length());
            }
            texStr = CustomColormap.fixTextureName(texStr, basePath);
            return texStr;
        }
        String s2 = path;
        int i2 = path.lastIndexOf(47);
        if (i2 >= 0) {
            s2 = path.substring(i2 + 1);
        }
        if ((j2 = s2.lastIndexOf(46)) >= 0) {
            s2 = s2.substring(0, j2);
        }
        s2 = CustomColormap.fixTextureName(s2, basePath);
        return s2;
    }

    private static String fixTextureName(String iconName, String basePath) {
        String s2;
        if (!((iconName = TextureUtils.fixResourcePath(iconName, basePath)).startsWith(basePath) || iconName.startsWith("textures/") || iconName.startsWith("mcpatcher/"))) {
            iconName = String.valueOf(basePath) + "/" + iconName;
        }
        if (iconName.endsWith(".png")) {
            iconName = iconName.substring(0, iconName.length() - 4);
        }
        if (iconName.startsWith(s2 = "textures/blocks/")) {
            iconName = iconName.substring(s2.length());
        }
        if (iconName.startsWith("/")) {
            iconName = iconName.substring(1);
        }
        return iconName;
    }

    public boolean matchesBlock(BlockStateBase blockState) {
        return Matches.block(blockState, this.matchBlocks);
    }

    public int getColorRandom() {
        if (this.format == 2) {
            return this.color;
        }
        int i2 = CustomColors.random.nextInt(this.colors.length);
        return this.colors[i2];
    }

    public int getColor(int index) {
        index = Config.limit(index, 0, this.colors.length - 1);
        return this.colors[index] & 0xFFFFFF;
    }

    public int getColor(int cx2, int cy2) {
        cx2 = Config.limit(cx2, 0, this.width - 1);
        cy2 = Config.limit(cy2, 0, this.height - 1);
        return this.colors[cy2 * this.width + cx2] & 0xFFFFFF;
    }

    public float[][] getColorsRgb() {
        if (this.colorsRgb == null) {
            this.colorsRgb = CustomColormap.toRgb(this.colors);
        }
        return this.colorsRgb;
    }

    @Override
    public int getColor(IBlockState blockState, IBlockAccess blockAccess, BlockPos blockPos) {
        return this.getColor(blockAccess, blockPos);
    }

    public int getColor(IBlockAccess blockAccess, BlockPos blockPos) {
        BiomeGenBase biomegenbase = CustomColors.getColorBiome(blockAccess, blockPos);
        return this.getColor(biomegenbase, blockPos);
    }

    @Override
    public boolean isColorConstant() {
        return this.format == 2;
    }

    public int getColor(BiomeGenBase biome, BlockPos blockPos) {
        return this.format == 0 ? this.getColorVanilla(biome, blockPos) : (this.format == 1 ? this.getColorGrid(biome, blockPos) : this.color);
    }

    public int getColorSmooth(IBlockAccess blockAccess, double x2, double y2, double z2, int radius) {
        if (this.format == 2) {
            return this.color;
        }
        int i2 = MathHelper.floor_double(x2);
        int j2 = MathHelper.floor_double(y2);
        int k2 = MathHelper.floor_double(z2);
        int l2 = 0;
        int i1 = 0;
        int j1 = 0;
        int k1 = 0;
        BlockPosM blockposm = new BlockPosM(0, 0, 0);
        int l1 = i2 - radius;
        while (l1 <= i2 + radius) {
            int i22 = k2 - radius;
            while (i22 <= k2 + radius) {
                blockposm.setXyz(l1, j2, i22);
                int j22 = this.getColor(blockAccess, (BlockPos)blockposm);
                l2 += j22 >> 16 & 0xFF;
                i1 += j22 >> 8 & 0xFF;
                j1 += j22 & 0xFF;
                ++k1;
                ++i22;
            }
            ++l1;
        }
        int k22 = l2 / k1;
        int l22 = i1 / k1;
        int i3 = j1 / k1;
        return k22 << 16 | l22 << 8 | i3;
    }

    private int getColorVanilla(BiomeGenBase biome, BlockPos blockPos) {
        double d0 = MathHelper.clamp_float(biome.getFloatTemperature(blockPos), 0.0f, 1.0f);
        double d1 = MathHelper.clamp_float(biome.getFloatRainfall(), 0.0f, 1.0f);
        int i2 = (int)((1.0 - d0) * (double)(this.width - 1));
        int j2 = (int)((1.0 - (d1 *= d0)) * (double)(this.height - 1));
        return this.getColor(i2, j2);
    }

    private int getColorGrid(BiomeGenBase biome, BlockPos blockPos) {
        int i2 = biome.biomeID;
        int j2 = blockPos.getY() - this.yOffset;
        if (this.yVariance > 0) {
            int k2 = blockPos.getX() << 16 + blockPos.getZ();
            int l2 = Config.intHash(k2);
            int i1 = this.yVariance * 2 + 1;
            int j1 = (l2 & 0xFF) % i1 - this.yVariance;
            j2 += j1;
        }
        return this.getColor(i2, j2);
    }

    public int getLength() {
        return this.format == 2 ? 1 : this.colors.length;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    private static float[][] toRgb(int[] cols) {
        float[][] afloat = new float[cols.length][3];
        int i2 = 0;
        while (i2 < cols.length) {
            int j2 = cols[i2];
            float f2 = (float)(j2 >> 16 & 0xFF) / 255.0f;
            float f1 = (float)(j2 >> 8 & 0xFF) / 255.0f;
            float f22 = (float)(j2 & 0xFF) / 255.0f;
            float[] afloat1 = afloat[i2];
            afloat1[0] = f2;
            afloat1[1] = f1;
            afloat1[2] = f22;
            ++i2;
        }
        return afloat;
    }

    public void addMatchBlock(MatchBlock mb2) {
        if (this.matchBlocks == null) {
            this.matchBlocks = new MatchBlock[0];
        }
        this.matchBlocks = (MatchBlock[])Config.addObjectToArray(this.matchBlocks, mb2);
    }

    public void addMatchBlock(int blockId, int metadata) {
        MatchBlock matchblock = this.getMatchBlock(blockId);
        if (matchblock != null) {
            if (metadata >= 0) {
                matchblock.addMetadata(metadata);
            }
        } else {
            this.addMatchBlock(new MatchBlock(blockId, metadata));
        }
    }

    private MatchBlock getMatchBlock(int blockId) {
        if (this.matchBlocks == null) {
            return null;
        }
        int i2 = 0;
        while (i2 < this.matchBlocks.length) {
            MatchBlock matchblock = this.matchBlocks[i2];
            if (matchblock.getBlockId() == blockId) {
                return matchblock;
            }
            ++i2;
        }
        return null;
    }

    public int[] getMatchBlockIds() {
        if (this.matchBlocks == null) {
            return null;
        }
        HashSet<Integer> set = new HashSet<Integer>();
        int i2 = 0;
        while (i2 < this.matchBlocks.length) {
            MatchBlock matchblock = this.matchBlocks[i2];
            if (matchblock.getBlockId() >= 0) {
                set.add(matchblock.getBlockId());
            }
            ++i2;
        }
        Integer[] ainteger = set.toArray(new Integer[set.size()]);
        int[] aint = new int[ainteger.length];
        int j2 = 0;
        while (j2 < ainteger.length) {
            aint[j2] = ainteger[j2];
            ++j2;
        }
        return aint;
    }

    public String toString() {
        return this.basePath + "/" + this.name + ", blocks: " + Config.arrayToString(this.matchBlocks) + ", source: " + this.source;
    }
}

