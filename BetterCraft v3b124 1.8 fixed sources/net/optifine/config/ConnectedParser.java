/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.config;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.src.Config;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.ConnectedProperties;
import net.optifine.config.INameGetter;
import net.optifine.config.MatchBlock;
import net.optifine.config.NbtTagValue;
import net.optifine.config.RangeInt;
import net.optifine.config.RangeListInt;
import net.optifine.config.VillagerProfession;
import net.optifine.config.Weather;
import net.optifine.util.EntityUtils;

public class ConnectedParser {
    private String context = null;
    public static final VillagerProfession[] PROFESSIONS_INVALID = new VillagerProfession[0];
    public static final EnumDyeColor[] DYE_COLORS_INVALID = new EnumDyeColor[0];
    private static final INameGetter<Enum> NAME_GETTER_ENUM = new INameGetter<Enum>(){

        @Override
        public String getName(Enum en2) {
            return en2.name();
        }
    };
    private static final INameGetter<EnumDyeColor> NAME_GETTER_DYE_COLOR = new INameGetter<EnumDyeColor>(){

        @Override
        public String getName(EnumDyeColor col) {
            return col.getName();
        }
    };

    public ConnectedParser(String context) {
        this.context = context;
    }

    public String parseName(String path) {
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

    public String parseBasePath(String path) {
        int i2 = path.lastIndexOf(47);
        return i2 < 0 ? "" : path.substring(0, i2);
    }

    public MatchBlock[] parseMatchBlocks(String propMatchBlocks) {
        if (propMatchBlocks == null) {
            return null;
        }
        ArrayList<MatchBlock> list = new ArrayList<MatchBlock>();
        String[] astring = Config.tokenize(propMatchBlocks, " ");
        int i2 = 0;
        while (i2 < astring.length) {
            String s2 = astring[i2];
            MatchBlock[] amatchblock = this.parseMatchBlock(s2);
            if (amatchblock != null) {
                list.addAll(Arrays.asList(amatchblock));
            }
            ++i2;
        }
        MatchBlock[] amatchblock1 = list.toArray(new MatchBlock[list.size()]);
        return amatchblock1;
    }

    public IBlockState parseBlockState(String str, IBlockState def) {
        MatchBlock[] amatchblock = this.parseMatchBlock(str);
        if (amatchblock == null) {
            return def;
        }
        if (amatchblock.length != 1) {
            return def;
        }
        MatchBlock matchblock = amatchblock[0];
        int i2 = matchblock.getBlockId();
        Block block = Block.getBlockById(i2);
        return block.getDefaultState();
    }

    public MatchBlock[] parseMatchBlock(String blockStr) {
        if (blockStr == null) {
            return null;
        }
        if ((blockStr = blockStr.trim()).length() <= 0) {
            return null;
        }
        String[] astring = Config.tokenize(blockStr, ":");
        String s2 = "minecraft";
        int i2 = 0;
        if (astring.length > 1 && this.isFullBlockName(astring)) {
            s2 = astring[0];
            i2 = 1;
        } else {
            s2 = "minecraft";
            i2 = 0;
        }
        String s1 = astring[i2];
        String[] astring1 = Arrays.copyOfRange(astring, i2 + 1, astring.length);
        Block[] ablock = this.parseBlockPart(s2, s1);
        if (ablock == null) {
            return null;
        }
        MatchBlock[] amatchblock = new MatchBlock[ablock.length];
        int j2 = 0;
        while (j2 < ablock.length) {
            MatchBlock matchblock;
            Block block = ablock[j2];
            int k2 = Block.getIdFromBlock(block);
            int[] aint = null;
            if (astring1.length > 0 && (aint = this.parseBlockMetadatas(block, astring1)) == null) {
                return null;
            }
            amatchblock[j2] = matchblock = new MatchBlock(k2, aint);
            ++j2;
        }
        return amatchblock;
    }

    public boolean isFullBlockName(String[] parts) {
        if (parts.length < 2) {
            return false;
        }
        String s2 = parts[1];
        return s2.length() < 1 ? false : (this.startsWithDigit(s2) ? false : !s2.contains("="));
    }

    public boolean startsWithDigit(String str) {
        if (str == null) {
            return false;
        }
        if (str.length() < 1) {
            return false;
        }
        char c0 = str.charAt(0);
        return Character.isDigit(c0);
    }

    public Block[] parseBlockPart(String domain, String blockPart) {
        if (this.startsWithDigit(blockPart)) {
            int[] aint = this.parseIntList(blockPart);
            if (aint == null) {
                return null;
            }
            Block[] ablock1 = new Block[aint.length];
            int j2 = 0;
            while (j2 < aint.length) {
                int i2 = aint[j2];
                Block block1 = Block.getBlockById(i2);
                if (block1 == null) {
                    this.warn("Block not found for id: " + i2);
                    return null;
                }
                ablock1[j2] = block1;
                ++j2;
            }
            return ablock1;
        }
        String s2 = String.valueOf(domain) + ":" + blockPart;
        Block block = Block.getBlockFromName(s2);
        if (block == null) {
            this.warn("Block not found for name: " + s2);
            return null;
        }
        Block[] ablock = new Block[]{block};
        return ablock;
    }

    public int[] parseBlockMetadatas(Block block, String[] params) {
        if (params.length <= 0) {
            return null;
        }
        String s2 = params[0];
        if (this.startsWithDigit(s2)) {
            int[] aint = this.parseIntList(s2);
            return aint;
        }
        IBlockState iblockstate = block.getDefaultState();
        Collection<IProperty> collection = iblockstate.getPropertyNames();
        HashMap<IProperty, List<Comparable>> map = new HashMap<IProperty, List<Comparable>>();
        int i2 = 0;
        while (i2 < params.length) {
            String s1 = params[i2];
            if (s1.length() > 0) {
                String[] astring = Config.tokenize(s1, "=");
                if (astring.length != 2) {
                    this.warn("Invalid block property: " + s1);
                    return null;
                }
                String s22 = astring[0];
                String s3 = astring[1];
                IProperty iproperty = ConnectedProperties.getProperty(s22, collection);
                if (iproperty == null) {
                    this.warn("Property not found: " + s22 + ", block: " + block);
                    return null;
                }
                ArrayList<Comparable> list = (ArrayList<Comparable>)map.get(s22);
                if (list == null) {
                    list = new ArrayList<Comparable>();
                    map.put(iproperty, list);
                }
                String[] astring1 = Config.tokenize(s3, ",");
                int j2 = 0;
                while (j2 < astring1.length) {
                    String s4 = astring1[j2];
                    Comparable comparable = ConnectedParser.parsePropertyValue(iproperty, s4);
                    if (comparable == null) {
                        this.warn("Property value not found: " + s4 + ", property: " + s22 + ", block: " + block);
                        return null;
                    }
                    list.add(comparable);
                    ++j2;
                }
            }
            ++i2;
        }
        if (map.isEmpty()) {
            return null;
        }
        ArrayList<Integer> list1 = new ArrayList<Integer>();
        int k2 = 0;
        while (k2 < 16) {
            int l2 = k2;
            try {
                IBlockState iblockstate1 = this.getStateFromMeta(block, l2);
                if (this.matchState(iblockstate1, map)) {
                    list1.add(l2);
                }
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
            ++k2;
        }
        if (list1.size() == 16) {
            return null;
        }
        int[] aint1 = new int[list1.size()];
        int i1 = 0;
        while (i1 < aint1.length) {
            aint1[i1] = (Integer)list1.get(i1);
            ++i1;
        }
        return aint1;
    }

    private IBlockState getStateFromMeta(Block block, int md2) {
        try {
            IBlockState iblockstate = block.getStateFromMeta(md2);
            if (block == Blocks.double_plant && md2 > 7) {
                IBlockState iblockstate1 = block.getStateFromMeta(md2 & 7);
                iblockstate = iblockstate.withProperty(BlockDoublePlant.VARIANT, iblockstate1.getValue(BlockDoublePlant.VARIANT));
            }
            return iblockstate;
        }
        catch (IllegalArgumentException var5) {
            return block.getDefaultState();
        }
    }

    public static Comparable parsePropertyValue(IProperty prop, String valStr) {
        Class oclass = prop.getValueClass();
        Comparable comparable = ConnectedParser.parseValue(valStr, oclass);
        if (comparable == null) {
            Collection collection = prop.getAllowedValues();
            comparable = ConnectedParser.getPropertyValue(valStr, collection);
        }
        return comparable;
    }

    public static Comparable getPropertyValue(String value, Collection propertyValues) {
        for (Object o2 : propertyValues) {
            Comparable comparable = (Comparable)o2;
            if (!ConnectedParser.getValueName(comparable).equals(value)) continue;
            return comparable;
        }
        return null;
    }

    private static Object getValueName(Comparable obj) {
        if (obj instanceof IStringSerializable) {
            IStringSerializable istringserializable = (IStringSerializable)((Object)obj);
            return istringserializable.getName();
        }
        return obj.toString();
    }

    public static Comparable parseValue(String str, Class<?> cls) {
        if (cls == String.class) {
            return str;
        }
        if (cls == Boolean.class) {
            return Boolean.valueOf(str);
        }
        if (cls == Float.class) {
            return Float.valueOf(str);
        }
        if (cls == Double.class) {
            return Double.valueOf(str);
        }
        if (cls == Integer.class) {
            return Integer.valueOf(str);
        }
        if (cls == Long.class) {
            return Long.valueOf(str);
        }
        return null;
    }

    public boolean matchState(IBlockState bs2, Map<IProperty, List<Comparable>> mapPropValues) {
        for (IProperty iproperty : mapPropValues.keySet()) {
            List<Comparable> list = mapPropValues.get(iproperty);
            Object comparable = bs2.getValue(iproperty);
            if (comparable == null) {
                return false;
            }
            if (list.contains(comparable)) continue;
            return false;
        }
        return true;
    }

    public BiomeGenBase[] parseBiomes(String str) {
        if (str == null) {
            return null;
        }
        str = str.trim();
        boolean flag = false;
        if (str.startsWith("!")) {
            flag = true;
            str = str.substring(1);
        }
        String[] astring = Config.tokenize(str, " ");
        ArrayList<BiomeGenBase> list = new ArrayList<BiomeGenBase>();
        int i2 = 0;
        while (i2 < astring.length) {
            String s2 = astring[i2];
            BiomeGenBase biomegenbase = this.findBiome(s2);
            if (biomegenbase == null) {
                this.warn("Biome not found: " + s2);
            } else {
                list.add(biomegenbase);
            }
            ++i2;
        }
        if (flag) {
            ArrayList<BiomeGenBase> list1 = new ArrayList<BiomeGenBase>(Arrays.asList(BiomeGenBase.getBiomeGenArray()));
            list1.removeAll(list);
            list = list1;
        }
        BiomeGenBase[] abiomegenbase = list.toArray(new BiomeGenBase[list.size()]);
        return abiomegenbase;
    }

    public BiomeGenBase findBiome(String biomeName) {
        if ((biomeName = biomeName.toLowerCase()).equals("nether")) {
            return BiomeGenBase.hell;
        }
        BiomeGenBase[] abiomegenbase = BiomeGenBase.getBiomeGenArray();
        int i2 = 0;
        while (i2 < abiomegenbase.length) {
            String s2;
            BiomeGenBase biomegenbase = abiomegenbase[i2];
            if (biomegenbase != null && (s2 = biomegenbase.biomeName.replace(" ", "").toLowerCase()).equals(biomeName)) {
                return biomegenbase;
            }
            ++i2;
        }
        return null;
    }

    public int parseInt(String str, int defVal) {
        if (str == null) {
            return defVal;
        }
        int i2 = Config.parseInt(str = str.trim(), -1);
        if (i2 < 0) {
            this.warn("Invalid number: " + str);
            return defVal;
        }
        return i2;
    }

    public int[] parseIntList(String str) {
        if (str == null) {
            return null;
        }
        ArrayList<Integer> list = new ArrayList<Integer>();
        String[] astring = Config.tokenize(str, " ,");
        int i2 = 0;
        while (i2 < astring.length) {
            String s2 = astring[i2];
            if (s2.contains("-")) {
                String[] astring1 = Config.tokenize(s2, "-");
                if (astring1.length != 2) {
                    this.warn("Invalid interval: " + s2 + ", when parsing: " + str);
                } else {
                    int k2 = Config.parseInt(astring1[0], -1);
                    int l2 = Config.parseInt(astring1[1], -1);
                    if (k2 >= 0 && l2 >= 0 && k2 <= l2) {
                        int i1 = k2;
                        while (i1 <= l2) {
                            list.add(i1);
                            ++i1;
                        }
                    } else {
                        this.warn("Invalid interval: " + s2 + ", when parsing: " + str);
                    }
                }
            } else {
                int j2 = Config.parseInt(s2, -1);
                if (j2 < 0) {
                    this.warn("Invalid number: " + s2 + ", when parsing: " + str);
                } else {
                    list.add(j2);
                }
            }
            ++i2;
        }
        int[] aint = new int[list.size()];
        int j1 = 0;
        while (j1 < aint.length) {
            aint[j1] = (Integer)list.get(j1);
            ++j1;
        }
        return aint;
    }

    public boolean[] parseFaces(String str, boolean[] defVal) {
        if (str == null) {
            return defVal;
        }
        EnumSet<EnumFacing> enumset = EnumSet.allOf(EnumFacing.class);
        String[] astring = Config.tokenize(str, " ,");
        int i2 = 0;
        while (i2 < astring.length) {
            String s2 = astring[i2];
            if (s2.equals("sides")) {
                enumset.add(EnumFacing.NORTH);
                enumset.add(EnumFacing.SOUTH);
                enumset.add(EnumFacing.WEST);
                enumset.add(EnumFacing.EAST);
            } else if (s2.equals("all")) {
                enumset.addAll(Arrays.asList(EnumFacing.VALUES));
            } else {
                EnumFacing enumfacing = this.parseFace(s2);
                if (enumfacing != null) {
                    enumset.add(enumfacing);
                }
            }
            ++i2;
        }
        boolean[] aboolean = new boolean[EnumFacing.VALUES.length];
        int j2 = 0;
        while (j2 < aboolean.length) {
            aboolean[j2] = enumset.contains(EnumFacing.VALUES[j2]);
            ++j2;
        }
        return aboolean;
    }

    public EnumFacing parseFace(String str) {
        if (!(str = str.toLowerCase()).equals("bottom") && !str.equals("down")) {
            if (!str.equals("top") && !str.equals("up")) {
                if (str.equals("north")) {
                    return EnumFacing.NORTH;
                }
                if (str.equals("south")) {
                    return EnumFacing.SOUTH;
                }
                if (str.equals("east")) {
                    return EnumFacing.EAST;
                }
                if (str.equals("west")) {
                    return EnumFacing.WEST;
                }
                Config.warn("Unknown face: " + str);
                return null;
            }
            return EnumFacing.UP;
        }
        return EnumFacing.DOWN;
    }

    public void dbg(String str) {
        Config.dbg(this.context + ": " + str);
    }

    public void warn(String str) {
        Config.warn(this.context + ": " + str);
    }

    public RangeListInt parseRangeListInt(String str) {
        if (str == null) {
            return null;
        }
        RangeListInt rangelistint = new RangeListInt();
        String[] astring = Config.tokenize(str, " ,");
        int i2 = 0;
        while (i2 < astring.length) {
            String s2 = astring[i2];
            RangeInt rangeint = this.parseRangeInt(s2);
            if (rangeint == null) {
                return null;
            }
            rangelistint.addRange(rangeint);
            ++i2;
        }
        return rangelistint;
    }

    private RangeInt parseRangeInt(String str) {
        if (str == null) {
            return null;
        }
        if (str.indexOf(45) >= 0) {
            String[] astring = Config.tokenize(str, "-");
            if (astring.length != 2) {
                this.warn("Invalid range: " + str);
                return null;
            }
            int j2 = Config.parseInt(astring[0], -1);
            int k2 = Config.parseInt(astring[1], -1);
            if (j2 >= 0 && k2 >= 0) {
                return new RangeInt(j2, k2);
            }
            this.warn("Invalid range: " + str);
            return null;
        }
        int i2 = Config.parseInt(str, -1);
        if (i2 < 0) {
            this.warn("Invalid integer: " + str);
            return null;
        }
        return new RangeInt(i2, i2);
    }

    public boolean parseBoolean(String str, boolean defVal) {
        if (str == null) {
            return defVal;
        }
        String s2 = str.toLowerCase().trim();
        if (s2.equals("true")) {
            return true;
        }
        if (s2.equals("false")) {
            return false;
        }
        this.warn("Invalid boolean: " + str);
        return defVal;
    }

    public Boolean parseBooleanObject(String str) {
        if (str == null) {
            return null;
        }
        String s2 = str.toLowerCase().trim();
        if (s2.equals("true")) {
            return Boolean.TRUE;
        }
        if (s2.equals("false")) {
            return Boolean.FALSE;
        }
        this.warn("Invalid boolean: " + str);
        return null;
    }

    public static int parseColor(String str, int defVal) {
        if (str == null) {
            return defVal;
        }
        str = str.trim();
        try {
            int i2 = Integer.parseInt(str, 16) & 0xFFFFFF;
            return i2;
        }
        catch (NumberFormatException var3) {
            return defVal;
        }
    }

    public static int parseColor4(String str, int defVal) {
        if (str == null) {
            return defVal;
        }
        str = str.trim();
        try {
            int i2 = (int)(Long.parseLong(str, 16) & 0xFFFFFFFFFFFFFFFFL);
            return i2;
        }
        catch (NumberFormatException var3) {
            return defVal;
        }
    }

    public EnumWorldBlockLayer parseBlockRenderLayer(String str, EnumWorldBlockLayer def) {
        if (str == null) {
            return def;
        }
        str = str.toLowerCase().trim();
        EnumWorldBlockLayer[] aenumworldblocklayer = EnumWorldBlockLayer.values();
        int i2 = 0;
        while (i2 < aenumworldblocklayer.length) {
            EnumWorldBlockLayer enumworldblocklayer = aenumworldblocklayer[i2];
            if (str.equals(enumworldblocklayer.name().toLowerCase())) {
                return enumworldblocklayer;
            }
            ++i2;
        }
        return def;
    }

    public <T> T parseObject(String str, T[] objs, INameGetter nameGetter, String property) {
        if (str == null) {
            return null;
        }
        String s2 = str.toLowerCase().trim();
        int i2 = 0;
        while (i2 < objs.length) {
            T t2 = objs[i2];
            String s1 = nameGetter.getName(t2);
            if (s1 != null && s1.toLowerCase().equals(s2)) {
                return t2;
            }
            ++i2;
        }
        this.warn("Invalid " + property + ": " + str);
        return null;
    }

    public <T> T[] parseObjects(String str, T[] objs, INameGetter nameGetter, String property, T[] errValue) {
        if (str == null) {
            return null;
        }
        str = str.toLowerCase().trim();
        String[] astring = Config.tokenize(str, " ");
        Object[] at2 = (Object[])Array.newInstance(objs.getClass().getComponentType(), astring.length);
        int i2 = 0;
        while (i2 < astring.length) {
            String s2 = astring[i2];
            T t2 = this.parseObject(s2, objs, nameGetter, property);
            if (t2 == null) {
                return errValue;
            }
            at2[i2] = t2;
            ++i2;
        }
        return at2;
    }

    public Enum parseEnum(String str, Enum[] enums, String property) {
        return this.parseObject(str, enums, NAME_GETTER_ENUM, property);
    }

    public Enum[] parseEnums(String str, Enum[] enums, String property, Enum[] errValue) {
        return this.parseObjects(str, enums, NAME_GETTER_ENUM, property, errValue);
    }

    public EnumDyeColor[] parseDyeColors(String str, String property, EnumDyeColor[] errValue) {
        return this.parseObjects(str, EnumDyeColor.values(), NAME_GETTER_DYE_COLOR, property, errValue);
    }

    public Weather[] parseWeather(String str, String property, Weather[] errValue) {
        return this.parseObjects(str, Weather.values(), NAME_GETTER_ENUM, property, errValue);
    }

    public NbtTagValue parseNbtTagValue(String path, String value) {
        return path != null && value != null ? new NbtTagValue(path, value) : null;
    }

    public VillagerProfession[] parseProfessions(String profStr) {
        if (profStr == null) {
            return null;
        }
        ArrayList<VillagerProfession> list = new ArrayList<VillagerProfession>();
        String[] astring = Config.tokenize(profStr, " ");
        int i2 = 0;
        while (i2 < astring.length) {
            String s2 = astring[i2];
            VillagerProfession villagerprofession = this.parseProfession(s2);
            if (villagerprofession == null) {
                this.warn("Invalid profession: " + s2);
                return PROFESSIONS_INVALID;
            }
            list.add(villagerprofession);
            ++i2;
        }
        if (list.isEmpty()) {
            return null;
        }
        VillagerProfession[] avillagerprofession = list.toArray(new VillagerProfession[list.size()]);
        return avillagerprofession;
    }

    private VillagerProfession parseProfession(String str) {
        int i2;
        String[] astring = Config.tokenize(str = str.toLowerCase(), ":");
        if (astring.length > 2) {
            return null;
        }
        String s2 = astring[0];
        String s1 = null;
        if (astring.length > 1) {
            s1 = astring[1];
        }
        if ((i2 = ConnectedParser.parseProfessionId(s2)) < 0) {
            return null;
        }
        int[] aint = null;
        if (s1 != null && (aint = ConnectedParser.parseCareerIds(i2, s1)) == null) {
            return null;
        }
        return new VillagerProfession(i2, aint);
    }

    private static int parseProfessionId(String str) {
        int i2 = Config.parseInt(str, -1);
        return i2 >= 0 ? i2 : (str.equals("farmer") ? 0 : (str.equals("librarian") ? 1 : (str.equals("priest") ? 2 : (str.equals("blacksmith") ? 3 : (str.equals("butcher") ? 4 : (str.equals("nitwit") ? 5 : -1))))));
    }

    private static int[] parseCareerIds(int prof, String str) {
        HashSet<Integer> set = new HashSet<Integer>();
        String[] astring = Config.tokenize(str, ",");
        int i2 = 0;
        while (i2 < astring.length) {
            String s2 = astring[i2];
            int j2 = ConnectedParser.parseCareerId(prof, s2);
            if (j2 < 0) {
                return null;
            }
            set.add(j2);
            ++i2;
        }
        Integer[] ainteger = set.toArray(new Integer[set.size()]);
        int[] aint = new int[ainteger.length];
        int k2 = 0;
        while (k2 < aint.length) {
            aint[k2] = ainteger[k2];
            ++k2;
        }
        return aint;
    }

    private static int parseCareerId(int prof, String str) {
        int i2 = Config.parseInt(str, -1);
        if (i2 >= 0) {
            return i2;
        }
        if (prof == 0) {
            if (str.equals("farmer")) {
                return 1;
            }
            if (str.equals("fisherman")) {
                return 2;
            }
            if (str.equals("shepherd")) {
                return 3;
            }
            if (str.equals("fletcher")) {
                return 4;
            }
        }
        if (prof == 1) {
            if (str.equals("librarian")) {
                return 1;
            }
            if (str.equals("cartographer")) {
                return 2;
            }
        }
        if (prof == 2 && str.equals("cleric")) {
            return 1;
        }
        if (prof == 3) {
            if (str.equals("armor")) {
                return 1;
            }
            if (str.equals("weapon")) {
                return 2;
            }
            if (str.equals("tool")) {
                return 3;
            }
        }
        if (prof == 4) {
            if (str.equals("butcher")) {
                return 1;
            }
            if (str.equals("leather")) {
                return 2;
            }
        }
        return prof == 5 && str.equals("nitwit") ? 1 : -1;
    }

    public int[] parseItems(String str) {
        str = str.trim();
        TreeSet<Integer> set = new TreeSet<Integer>();
        String[] astring = Config.tokenize(str, " ");
        int i2 = 0;
        while (i2 < astring.length) {
            String s2 = astring[i2];
            ResourceLocation resourcelocation = new ResourceLocation(s2);
            Item item = Item.itemRegistry.getObject(resourcelocation);
            if (item == null) {
                this.warn("Item not found: " + s2);
            } else {
                int j2 = Item.getIdFromItem(item);
                if (j2 < 0) {
                    this.warn("Item has no ID: " + item + ", name: " + s2);
                } else {
                    set.add(new Integer(j2));
                }
            }
            ++i2;
        }
        Integer[] ainteger = set.toArray(new Integer[set.size()]);
        int[] aint = Config.toPrimitive(ainteger);
        return aint;
    }

    public int[] parseEntities(String str) {
        str = str.trim();
        TreeSet<Integer> set = new TreeSet<Integer>();
        String[] astring = Config.tokenize(str, " ");
        int i2 = 0;
        while (i2 < astring.length) {
            String s2 = astring[i2];
            int j2 = EntityUtils.getEntityIdByName(s2);
            if (j2 < 0) {
                this.warn("Entity not found: " + s2);
            } else {
                set.add(new Integer(j2));
            }
            ++i2;
        }
        Integer[] ainteger = set.toArray(new Integer[set.size()]);
        int[] aint = Config.toPrimitive(ainteger);
        return aint;
    }
}

