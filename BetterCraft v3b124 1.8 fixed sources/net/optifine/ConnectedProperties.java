/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.src.Config;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.ConnectedTextures;
import net.optifine.config.ConnectedParser;
import net.optifine.config.MatchBlock;
import net.optifine.config.Matches;
import net.optifine.config.NbtTagValue;
import net.optifine.config.RangeInt;
import net.optifine.config.RangeListInt;
import net.optifine.util.MathUtils;
import net.optifine.util.TextureUtils;

public class ConnectedProperties {
    public String name = null;
    public String basePath = null;
    public MatchBlock[] matchBlocks = null;
    public int[] metadatas = null;
    public String[] matchTiles = null;
    public int method = 0;
    public String[] tiles = null;
    public int connect = 0;
    public int faces = 63;
    public BiomeGenBase[] biomes = null;
    public RangeListInt heights = null;
    public int renderPass = 0;
    public boolean innerSeams = false;
    public int[] ctmTileIndexes = null;
    public int width = 0;
    public int height = 0;
    public int[] weights = null;
    public int randomLoops = 0;
    public int symmetry = 1;
    public boolean linked = false;
    public NbtTagValue nbtName = null;
    public int[] sumWeights = null;
    public int sumAllWeights = 1;
    public TextureAtlasSprite[] matchTileIcons = null;
    public TextureAtlasSprite[] tileIcons = null;
    public MatchBlock[] connectBlocks = null;
    public String[] connectTiles = null;
    public TextureAtlasSprite[] connectTileIcons = null;
    public int tintIndex = -1;
    public IBlockState tintBlockState = Blocks.air.getDefaultState();
    public EnumWorldBlockLayer layer = null;
    public static final int METHOD_NONE = 0;
    public static final int METHOD_CTM = 1;
    public static final int METHOD_HORIZONTAL = 2;
    public static final int METHOD_TOP = 3;
    public static final int METHOD_RANDOM = 4;
    public static final int METHOD_REPEAT = 5;
    public static final int METHOD_VERTICAL = 6;
    public static final int METHOD_FIXED = 7;
    public static final int METHOD_HORIZONTAL_VERTICAL = 8;
    public static final int METHOD_VERTICAL_HORIZONTAL = 9;
    public static final int METHOD_CTM_COMPACT = 10;
    public static final int METHOD_OVERLAY = 11;
    public static final int METHOD_OVERLAY_FIXED = 12;
    public static final int METHOD_OVERLAY_RANDOM = 13;
    public static final int METHOD_OVERLAY_REPEAT = 14;
    public static final int METHOD_OVERLAY_CTM = 15;
    public static final int CONNECT_NONE = 0;
    public static final int CONNECT_BLOCK = 1;
    public static final int CONNECT_TILE = 2;
    public static final int CONNECT_MATERIAL = 3;
    public static final int CONNECT_UNKNOWN = 128;
    public static final int FACE_BOTTOM = 1;
    public static final int FACE_TOP = 2;
    public static final int FACE_NORTH = 4;
    public static final int FACE_SOUTH = 8;
    public static final int FACE_WEST = 16;
    public static final int FACE_EAST = 32;
    public static final int FACE_SIDES = 60;
    public static final int FACE_ALL = 63;
    public static final int FACE_UNKNOWN = 128;
    public static final int SYMMETRY_NONE = 1;
    public static final int SYMMETRY_OPPOSITE = 2;
    public static final int SYMMETRY_ALL = 6;
    public static final int SYMMETRY_UNKNOWN = 128;
    public static final String TILE_SKIP_PNG = "<skip>.png";
    public static final String TILE_DEFAULT_PNG = "<default>.png";

    public ConnectedProperties(Properties props, String path) {
        ConnectedParser connectedparser = new ConnectedParser("ConnectedTextures");
        this.name = connectedparser.parseName(path);
        this.basePath = connectedparser.parseBasePath(path);
        this.matchBlocks = connectedparser.parseMatchBlocks(props.getProperty("matchBlocks"));
        this.metadatas = connectedparser.parseIntList(props.getProperty("metadata"));
        this.matchTiles = this.parseMatchTiles(props.getProperty("matchTiles"));
        this.method = ConnectedProperties.parseMethod(props.getProperty("method"));
        this.tiles = this.parseTileNames(props.getProperty("tiles"));
        this.connect = ConnectedProperties.parseConnect(props.getProperty("connect"));
        this.faces = ConnectedProperties.parseFaces(props.getProperty("faces"));
        this.biomes = connectedparser.parseBiomes(props.getProperty("biomes"));
        this.heights = connectedparser.parseRangeListInt(props.getProperty("heights"));
        if (this.heights == null) {
            int i2 = connectedparser.parseInt(props.getProperty("minHeight"), -1);
            int j2 = connectedparser.parseInt(props.getProperty("maxHeight"), 1024);
            if (i2 != -1 || j2 != 1024) {
                this.heights = new RangeListInt(new RangeInt(i2, j2));
            }
        }
        this.renderPass = connectedparser.parseInt(props.getProperty("renderPass"), -1);
        this.innerSeams = connectedparser.parseBoolean(props.getProperty("innerSeams"), false);
        this.ctmTileIndexes = this.parseCtmTileIndexes(props);
        this.width = connectedparser.parseInt(props.getProperty("width"), -1);
        this.height = connectedparser.parseInt(props.getProperty("height"), -1);
        this.weights = connectedparser.parseIntList(props.getProperty("weights"));
        this.randomLoops = connectedparser.parseInt(props.getProperty("randomLoops"), 0);
        this.symmetry = ConnectedProperties.parseSymmetry(props.getProperty("symmetry"));
        this.linked = connectedparser.parseBoolean(props.getProperty("linked"), false);
        this.nbtName = connectedparser.parseNbtTagValue("name", props.getProperty("name"));
        this.connectBlocks = connectedparser.parseMatchBlocks(props.getProperty("connectBlocks"));
        this.connectTiles = this.parseMatchTiles(props.getProperty("connectTiles"));
        this.tintIndex = connectedparser.parseInt(props.getProperty("tintIndex"), -1);
        this.tintBlockState = connectedparser.parseBlockState(props.getProperty("tintBlock"), Blocks.air.getDefaultState());
        this.layer = connectedparser.parseBlockRenderLayer(props.getProperty("layer"), EnumWorldBlockLayer.CUTOUT_MIPPED);
    }

    private int[] parseCtmTileIndexes(Properties props) {
        if (this.tiles == null) {
            return null;
        }
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (Object object : props.keySet()) {
            String s1;
            String s2;
            if (!(object instanceof String) || !(s2 = (String)object).startsWith(s1 = "ctm.")) continue;
            String s22 = s2.substring(s1.length());
            String s3 = props.getProperty(s2);
            if (s3 == null) continue;
            s3 = s3.trim();
            int i2 = Config.parseInt(s22, -1);
            if (i2 >= 0 && i2 <= 46) {
                int j2 = Config.parseInt(s3, -1);
                if (j2 >= 0 && j2 < this.tiles.length) {
                    map.put(i2, j2);
                    continue;
                }
                Config.warn("Invalid CTM tile index: " + s3);
                continue;
            }
            Config.warn("Invalid CTM index: " + s22);
        }
        if (map.isEmpty()) {
            return null;
        }
        int[] aint = new int[47];
        int k2 = 0;
        while (k2 < aint.length) {
            aint[k2] = -1;
            if (map.containsKey(k2)) {
                aint[k2] = (Integer)map.get(k2);
            }
            ++k2;
        }
        return aint;
    }

    private String[] parseMatchTiles(String str) {
        if (str == null) {
            return null;
        }
        String[] astring = Config.tokenize(str, " ");
        int i2 = 0;
        while (i2 < astring.length) {
            String s2 = astring[i2];
            if (s2.endsWith(".png")) {
                s2 = s2.substring(0, s2.length() - 4);
            }
            astring[i2] = s2 = TextureUtils.fixResourcePath(s2, this.basePath);
            ++i2;
        }
        return astring;
    }

    private static String parseName(String path) {
        int j2;
        String s2 = path;
        int i2 = path.lastIndexOf(47);
        if (i2 >= 0) {
            s2 = path.substring(i2 + 1);
        }
        if ((j2 = s2.lastIndexOf(46)) >= 0) {
            s2 = s2.substring(0, j2);
        }
        return s2;
    }

    private static String parseBasePath(String path) {
        int i2 = path.lastIndexOf(47);
        return i2 < 0 ? "" : path.substring(0, i2);
    }

    /*
     * Enabled aggressive block sorting
     */
    private String[] parseTileNames(String str) {
        if (str == null) {
            return null;
        }
        ArrayList<String> list = new ArrayList<String>();
        String[] astring = Config.tokenize(str, " ,");
        int i2 = 0;
        while (i2 < astring.length) {
            block9: {
                int k2;
                block8: {
                    String s2;
                    block7: {
                        String[] astring1;
                        s2 = astring[i2];
                        if (!s2.contains("-") || (astring1 = Config.tokenize(s2, "-")).length != 2) break block7;
                        int j2 = Config.parseInt(astring1[0], -1);
                        k2 = Config.parseInt(astring1[1], -1);
                        if (j2 < 0 || k2 < 0) break block7;
                        if (j2 <= k2) break block8;
                        Config.warn("Invalid interval: " + s2 + ", when parsing: " + str);
                        break block9;
                    }
                    list.add(s2);
                    break block9;
                }
                for (int l2 = j2; l2 <= k2; ++l2) {
                    list.add(String.valueOf(l2));
                }
            }
            ++i2;
        }
        String[] astring2 = list.toArray(new String[list.size()]);
        int i1 = 0;
        while (i1 < astring2.length) {
            String s1 = astring2[i1];
            if (!((s1 = TextureUtils.fixResourcePath(s1, this.basePath)).startsWith(this.basePath) || s1.startsWith("textures/") || s1.startsWith("mcpatcher/"))) {
                s1 = String.valueOf(this.basePath) + "/" + s1;
            }
            if (s1.endsWith(".png")) {
                s1 = s1.substring(0, s1.length() - 4);
            }
            if (s1.startsWith("/")) {
                s1 = s1.substring(1);
            }
            astring2[i1] = s1;
            ++i1;
        }
        return astring2;
    }

    private static int parseSymmetry(String str) {
        if (str == null) {
            return 1;
        }
        if ((str = str.trim()).equals("opposite")) {
            return 2;
        }
        if (str.equals("all")) {
            return 6;
        }
        Config.warn("Unknown symmetry: " + str);
        return 1;
    }

    private static int parseFaces(String str) {
        if (str == null) {
            return 63;
        }
        String[] astring = Config.tokenize(str, " ,");
        int i2 = 0;
        int j2 = 0;
        while (j2 < astring.length) {
            String s2 = astring[j2];
            int k2 = ConnectedProperties.parseFace(s2);
            i2 |= k2;
            ++j2;
        }
        return i2;
    }

    private static int parseFace(String str) {
        if (!(str = str.toLowerCase()).equals("bottom") && !str.equals("down")) {
            if (!str.equals("top") && !str.equals("up")) {
                if (str.equals("north")) {
                    return 4;
                }
                if (str.equals("south")) {
                    return 8;
                }
                if (str.equals("east")) {
                    return 32;
                }
                if (str.equals("west")) {
                    return 16;
                }
                if (str.equals("sides")) {
                    return 60;
                }
                if (str.equals("all")) {
                    return 63;
                }
                Config.warn("Unknown face: " + str);
                return 128;
            }
            return 2;
        }
        return 1;
    }

    private static int parseConnect(String str) {
        if (str == null) {
            return 0;
        }
        if ((str = str.trim()).equals("block")) {
            return 1;
        }
        if (str.equals("tile")) {
            return 2;
        }
        if (str.equals("material")) {
            return 3;
        }
        Config.warn("Unknown connect: " + str);
        return 128;
    }

    public static IProperty getProperty(String key, Collection properties) {
        for (Object o2 : properties) {
            IProperty iproperty = (IProperty)o2;
            if (!key.equals(iproperty.getName())) continue;
            return iproperty;
        }
        return null;
    }

    private static int parseMethod(String str) {
        if (str == null) {
            return 1;
        }
        if (!(str = str.trim()).equals("ctm") && !str.equals("glass")) {
            if (str.equals("ctm_compact")) {
                return 10;
            }
            if (!str.equals("horizontal") && !str.equals("bookshelf")) {
                if (str.equals("vertical")) {
                    return 6;
                }
                if (str.equals("top")) {
                    return 3;
                }
                if (str.equals("random")) {
                    return 4;
                }
                if (str.equals("repeat")) {
                    return 5;
                }
                if (str.equals("fixed")) {
                    return 7;
                }
                if (!str.equals("horizontal+vertical") && !str.equals("h+v")) {
                    if (!str.equals("vertical+horizontal") && !str.equals("v+h")) {
                        if (str.equals("overlay")) {
                            return 11;
                        }
                        if (str.equals("overlay_fixed")) {
                            return 12;
                        }
                        if (str.equals("overlay_random")) {
                            return 13;
                        }
                        if (str.equals("overlay_repeat")) {
                            return 14;
                        }
                        if (str.equals("overlay_ctm")) {
                            return 15;
                        }
                        Config.warn("Unknown method: " + str);
                        return 0;
                    }
                    return 9;
                }
                return 8;
            }
            return 2;
        }
        return 1;
    }

    public boolean isValid(String path) {
        if (this.name != null && this.name.length() > 0) {
            if (this.basePath == null) {
                Config.warn("No base path found: " + path);
                return false;
            }
            if (this.matchBlocks == null) {
                this.matchBlocks = this.detectMatchBlocks();
            }
            if (this.matchTiles == null && this.matchBlocks == null) {
                this.matchTiles = this.detectMatchTiles();
            }
            if (this.matchBlocks == null && this.matchTiles == null) {
                Config.warn("No matchBlocks or matchTiles specified: " + path);
                return false;
            }
            if (this.method == 0) {
                Config.warn("No method: " + path);
                return false;
            }
            if (this.tiles != null && this.tiles.length > 0) {
                if (this.connect == 0) {
                    this.connect = this.detectConnect();
                }
                if (this.connect == 128) {
                    Config.warn("Invalid connect in: " + path);
                    return false;
                }
                if (this.renderPass > 0) {
                    Config.warn("Render pass not supported: " + this.renderPass);
                    return false;
                }
                if ((this.faces & 0x80) != 0) {
                    Config.warn("Invalid faces in: " + path);
                    return false;
                }
                if ((this.symmetry & 0x80) != 0) {
                    Config.warn("Invalid symmetry in: " + path);
                    return false;
                }
                switch (this.method) {
                    case 1: {
                        return this.isValidCtm(path);
                    }
                    case 2: {
                        return this.isValidHorizontal(path);
                    }
                    case 3: {
                        return this.isValidTop(path);
                    }
                    case 4: {
                        return this.isValidRandom(path);
                    }
                    case 5: {
                        return this.isValidRepeat(path);
                    }
                    case 6: {
                        return this.isValidVertical(path);
                    }
                    case 7: {
                        return this.isValidFixed(path);
                    }
                    case 8: {
                        return this.isValidHorizontalVertical(path);
                    }
                    case 9: {
                        return this.isValidVerticalHorizontal(path);
                    }
                    case 10: {
                        return this.isValidCtmCompact(path);
                    }
                    case 11: {
                        return this.isValidOverlay(path);
                    }
                    case 12: {
                        return this.isValidOverlayFixed(path);
                    }
                    case 13: {
                        return this.isValidOverlayRandom(path);
                    }
                    case 14: {
                        return this.isValidOverlayRepeat(path);
                    }
                    case 15: {
                        return this.isValidOverlayCtm(path);
                    }
                }
                Config.warn("Unknown method: " + path);
                return false;
            }
            Config.warn("No tiles specified: " + path);
            return false;
        }
        Config.warn("No name found: " + path);
        return false;
    }

    private int detectConnect() {
        return this.matchBlocks != null ? 1 : (this.matchTiles != null ? 2 : 128);
    }

    private MatchBlock[] detectMatchBlocks() {
        int[] aint = this.detectMatchBlockIds();
        if (aint == null) {
            return null;
        }
        MatchBlock[] amatchblock = new MatchBlock[aint.length];
        int i2 = 0;
        while (i2 < amatchblock.length) {
            amatchblock[i2] = new MatchBlock(aint[i2]);
            ++i2;
        }
        return amatchblock;
    }

    private int[] detectMatchBlockIds() {
        int[] nArray;
        int i2;
        if (!this.name.startsWith("block")) {
            return null;
        }
        int j2 = i2 = "block".length();
        while (j2 < this.name.length()) {
            char c0 = this.name.charAt(j2);
            if (c0 < '0' || c0 > '9') break;
            ++j2;
        }
        if (j2 == i2) {
            return null;
        }
        String s2 = this.name.substring(i2, j2);
        int k2 = Config.parseInt(s2, -1);
        if (k2 < 0) {
            nArray = null;
        } else {
            int[] nArray2 = new int[1];
            nArray = nArray2;
            nArray2[0] = k2;
        }
        return nArray;
    }

    private String[] detectMatchTiles() {
        String[] stringArray;
        TextureAtlasSprite textureatlassprite = ConnectedProperties.getIcon(this.name);
        if (textureatlassprite == null) {
            stringArray = null;
        } else {
            String[] stringArray2 = new String[1];
            stringArray = stringArray2;
            stringArray2[0] = this.name;
        }
        return stringArray;
    }

    private static TextureAtlasSprite getIcon(String iconName) {
        TextureMap texturemap = Minecraft.getMinecraft().getTextureMapBlocks();
        TextureAtlasSprite textureatlassprite = texturemap.getSpriteSafe(iconName);
        if (textureatlassprite != null) {
            return textureatlassprite;
        }
        textureatlassprite = texturemap.getSpriteSafe("blocks/" + iconName);
        return textureatlassprite;
    }

    private boolean isValidCtm(String path) {
        if (this.tiles == null) {
            this.tiles = this.parseTileNames("0-11 16-27 32-43 48-58");
        }
        if (this.tiles.length < 47) {
            Config.warn("Invalid tiles, must be at least 47: " + path);
            return false;
        }
        return true;
    }

    private boolean isValidCtmCompact(String path) {
        if (this.tiles == null) {
            this.tiles = this.parseTileNames("0-4");
        }
        if (this.tiles.length < 5) {
            Config.warn("Invalid tiles, must be at least 5: " + path);
            return false;
        }
        return true;
    }

    private boolean isValidOverlay(String path) {
        if (this.tiles == null) {
            this.tiles = this.parseTileNames("0-16");
        }
        if (this.tiles.length < 17) {
            Config.warn("Invalid tiles, must be at least 17: " + path);
            return false;
        }
        if (this.layer != null && this.layer != EnumWorldBlockLayer.SOLID) {
            return true;
        }
        Config.warn("Invalid overlay layer: " + (Object)((Object)this.layer));
        return false;
    }

    private boolean isValidOverlayFixed(String path) {
        if (!this.isValidFixed(path)) {
            return false;
        }
        if (this.layer != null && this.layer != EnumWorldBlockLayer.SOLID) {
            return true;
        }
        Config.warn("Invalid overlay layer: " + (Object)((Object)this.layer));
        return false;
    }

    private boolean isValidOverlayRandom(String path) {
        if (!this.isValidRandom(path)) {
            return false;
        }
        if (this.layer != null && this.layer != EnumWorldBlockLayer.SOLID) {
            return true;
        }
        Config.warn("Invalid overlay layer: " + (Object)((Object)this.layer));
        return false;
    }

    private boolean isValidOverlayRepeat(String path) {
        if (!this.isValidRepeat(path)) {
            return false;
        }
        if (this.layer != null && this.layer != EnumWorldBlockLayer.SOLID) {
            return true;
        }
        Config.warn("Invalid overlay layer: " + (Object)((Object)this.layer));
        return false;
    }

    private boolean isValidOverlayCtm(String path) {
        if (!this.isValidCtm(path)) {
            return false;
        }
        if (this.layer != null && this.layer != EnumWorldBlockLayer.SOLID) {
            return true;
        }
        Config.warn("Invalid overlay layer: " + (Object)((Object)this.layer));
        return false;
    }

    private boolean isValidHorizontal(String path) {
        if (this.tiles == null) {
            this.tiles = this.parseTileNames("12-15");
        }
        if (this.tiles.length != 4) {
            Config.warn("Invalid tiles, must be exactly 4: " + path);
            return false;
        }
        return true;
    }

    private boolean isValidVertical(String path) {
        if (this.tiles == null) {
            Config.warn("No tiles defined for vertical: " + path);
            return false;
        }
        if (this.tiles.length != 4) {
            Config.warn("Invalid tiles, must be exactly 4: " + path);
            return false;
        }
        return true;
    }

    private boolean isValidHorizontalVertical(String path) {
        if (this.tiles == null) {
            Config.warn("No tiles defined for horizontal+vertical: " + path);
            return false;
        }
        if (this.tiles.length != 7) {
            Config.warn("Invalid tiles, must be exactly 7: " + path);
            return false;
        }
        return true;
    }

    private boolean isValidVerticalHorizontal(String path) {
        if (this.tiles == null) {
            Config.warn("No tiles defined for vertical+horizontal: " + path);
            return false;
        }
        if (this.tiles.length != 7) {
            Config.warn("Invalid tiles, must be exactly 7: " + path);
            return false;
        }
        return true;
    }

    private boolean isValidRandom(String path) {
        if (this.tiles != null && this.tiles.length > 0) {
            if (this.weights != null) {
                if (this.weights.length > this.tiles.length) {
                    Config.warn("More weights defined than tiles, trimming weights: " + path);
                    int[] aint = new int[this.tiles.length];
                    System.arraycopy(this.weights, 0, aint, 0, aint.length);
                    this.weights = aint;
                }
                if (this.weights.length < this.tiles.length) {
                    Config.warn("Less weights defined than tiles, expanding weights: " + path);
                    int[] aint1 = new int[this.tiles.length];
                    System.arraycopy(this.weights, 0, aint1, 0, this.weights.length);
                    int i2 = MathUtils.getAverage(this.weights);
                    int j2 = this.weights.length;
                    while (j2 < aint1.length) {
                        aint1[j2] = i2;
                        ++j2;
                    }
                    this.weights = aint1;
                }
                this.sumWeights = new int[this.weights.length];
                int k2 = 0;
                int l2 = 0;
                while (l2 < this.weights.length) {
                    this.sumWeights[l2] = k2 += this.weights[l2];
                    ++l2;
                }
                this.sumAllWeights = k2;
                if (this.sumAllWeights <= 0) {
                    Config.warn("Invalid sum of all weights: " + k2);
                    this.sumAllWeights = 1;
                }
            }
            if (this.randomLoops >= 0 && this.randomLoops <= 9) {
                return true;
            }
            Config.warn("Invalid randomLoops: " + this.randomLoops);
            return false;
        }
        Config.warn("Tiles not defined: " + path);
        return false;
    }

    private boolean isValidRepeat(String path) {
        if (this.tiles == null) {
            Config.warn("Tiles not defined: " + path);
            return false;
        }
        if (this.width <= 0) {
            Config.warn("Invalid width: " + path);
            return false;
        }
        if (this.height <= 0) {
            Config.warn("Invalid height: " + path);
            return false;
        }
        if (this.tiles.length != this.width * this.height) {
            Config.warn("Number of tiles does not equal width x height: " + path);
            return false;
        }
        return true;
    }

    private boolean isValidFixed(String path) {
        if (this.tiles == null) {
            Config.warn("Tiles not defined: " + path);
            return false;
        }
        if (this.tiles.length != 1) {
            Config.warn("Number of tiles should be 1 for method: fixed.");
            return false;
        }
        return true;
    }

    private boolean isValidTop(String path) {
        if (this.tiles == null) {
            this.tiles = this.parseTileNames("66");
        }
        if (this.tiles.length != 1) {
            Config.warn("Invalid tiles, must be exactly 1: " + path);
            return false;
        }
        return true;
    }

    public void updateIcons(TextureMap textureMap) {
        if (this.matchTiles != null) {
            this.matchTileIcons = ConnectedProperties.registerIcons(this.matchTiles, textureMap, false, false);
        }
        if (this.connectTiles != null) {
            this.connectTileIcons = ConnectedProperties.registerIcons(this.connectTiles, textureMap, false, false);
        }
        if (this.tiles != null) {
            this.tileIcons = ConnectedProperties.registerIcons(this.tiles, textureMap, true, !ConnectedProperties.isMethodOverlay(this.method));
        }
    }

    private static boolean isMethodOverlay(int method) {
        switch (method) {
            case 11: 
            case 12: 
            case 13: 
            case 14: 
            case 15: {
                return true;
            }
        }
        return false;
    }

    private static TextureAtlasSprite[] registerIcons(String[] tileNames, TextureMap textureMap, boolean skipTiles, boolean defaultTiles) {
        if (tileNames == null) {
            return null;
        }
        ArrayList<TextureAtlasSprite> list = new ArrayList<TextureAtlasSprite>();
        int i2 = 0;
        while (i2 < tileNames.length) {
            String s2 = tileNames[i2];
            ResourceLocation resourcelocation = new ResourceLocation(s2);
            String s1 = resourcelocation.getResourceDomain();
            String s22 = resourcelocation.getResourcePath();
            if (!s22.contains("/")) {
                s22 = "textures/blocks/" + s22;
            }
            String s3 = String.valueOf(s22) + ".png";
            if (skipTiles && s3.endsWith(TILE_SKIP_PNG)) {
                list.add(null);
            } else if (defaultTiles && s3.endsWith(TILE_DEFAULT_PNG)) {
                list.add(ConnectedTextures.SPRITE_DEFAULT);
            } else {
                ResourceLocation resourcelocation1 = new ResourceLocation(s1, s3);
                boolean flag = Config.hasResource(resourcelocation1);
                if (!flag) {
                    Config.warn("File not found: " + s3);
                }
                String s4 = "textures/";
                String s5 = s22;
                if (s22.startsWith(s4)) {
                    s5 = s22.substring(s4.length());
                }
                ResourceLocation resourcelocation2 = new ResourceLocation(s1, s5);
                TextureAtlasSprite textureatlassprite = textureMap.registerSprite(resourcelocation2);
                list.add(textureatlassprite);
            }
            ++i2;
        }
        TextureAtlasSprite[] atextureatlassprite = list.toArray(new TextureAtlasSprite[list.size()]);
        return atextureatlassprite;
    }

    public boolean matchesBlockId(int blockId) {
        return Matches.blockId(blockId, this.matchBlocks);
    }

    public boolean matchesBlock(int blockId, int metadata) {
        return !Matches.block(blockId, metadata, this.matchBlocks) ? false : Matches.metadata(metadata, this.metadatas);
    }

    public boolean matchesIcon(TextureAtlasSprite icon) {
        return Matches.sprite(icon, this.matchTileIcons);
    }

    public String toString() {
        return "CTM name: " + this.name + ", basePath: " + this.basePath + ", matchBlocks: " + Config.arrayToString(this.matchBlocks) + ", matchTiles: " + Config.arrayToString(this.matchTiles);
    }

    public boolean matchesBiome(BiomeGenBase biome) {
        return Matches.biome(biome, this.biomes);
    }

    public int getMetadataMax() {
        int i2 = -1;
        i2 = this.getMax(this.metadatas, i2);
        if (this.matchBlocks != null) {
            int j2 = 0;
            while (j2 < this.matchBlocks.length) {
                MatchBlock matchblock = this.matchBlocks[j2];
                i2 = this.getMax(matchblock.getMetadatas(), i2);
                ++j2;
            }
        }
        return i2;
    }

    private int getMax(int[] mds, int max) {
        if (mds == null) {
            return max;
        }
        int i2 = 0;
        while (i2 < mds.length) {
            int j2 = mds[i2];
            if (j2 > max) {
                max = j2;
            }
            ++i2;
        }
        return max;
    }
}

