/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.CustomItemProperties;
import net.optifine.CustomItemsComparator;
import net.optifine.config.NbtTagValue;
import net.optifine.render.Blender;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.ShadersRender;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.ResUtils;
import net.optifine.util.StrUtils;

public class CustomItems {
    private static CustomItemProperties[][] itemProperties = null;
    private static CustomItemProperties[][] enchantmentProperties = null;
    private static Map mapPotionIds = null;
    private static ItemModelGenerator itemModelGenerator = new ItemModelGenerator();
    private static boolean useGlint = true;
    private static boolean renderOffHand = false;
    public static final int MASK_POTION_SPLASH = 16384;
    public static final int MASK_POTION_NAME = 63;
    public static final int MASK_POTION_EXTENDED = 64;
    public static final String KEY_TEXTURE_OVERLAY = "texture.potion_overlay";
    public static final String KEY_TEXTURE_SPLASH = "texture.potion_bottle_splash";
    public static final String KEY_TEXTURE_DRINKABLE = "texture.potion_bottle_drinkable";
    public static final String DEFAULT_TEXTURE_OVERLAY = "items/potion_overlay";
    public static final String DEFAULT_TEXTURE_SPLASH = "items/potion_bottle_splash";
    public static final String DEFAULT_TEXTURE_DRINKABLE = "items/potion_bottle_drinkable";
    private static final int[][] EMPTY_INT2_ARRAY = new int[0][];
    private static final String TYPE_POTION_NORMAL = "normal";
    private static final String TYPE_POTION_SPLASH = "splash";
    private static final String TYPE_POTION_LINGER = "linger";

    public static void update() {
        itemProperties = null;
        enchantmentProperties = null;
        useGlint = true;
        if (Config.isCustomItems()) {
            CustomItems.readCitProperties("mcpatcher/cit.properties");
            IResourcePack[] airesourcepack = Config.getResourcePacks();
            int i2 = airesourcepack.length - 1;
            while (i2 >= 0) {
                IResourcePack iresourcepack = airesourcepack[i2];
                CustomItems.update(iresourcepack);
                --i2;
            }
            CustomItems.update(Config.getDefaultResourcePack());
            if (itemProperties.length <= 0) {
                itemProperties = null;
            }
            if (enchantmentProperties.length <= 0) {
                enchantmentProperties = null;
            }
        }
    }

    private static void readCitProperties(String fileName) {
        try {
            ResourceLocation resourcelocation = new ResourceLocation(fileName);
            InputStream inputstream = Config.getResourceStream(resourcelocation);
            if (inputstream == null) {
                return;
            }
            Config.dbg("CustomItems: Loading " + fileName);
            PropertiesOrdered properties = new PropertiesOrdered();
            properties.load(inputstream);
            inputstream.close();
            useGlint = Config.parseBoolean(properties.getProperty("useGlint"), true);
        }
        catch (FileNotFoundException var4) {
            return;
        }
        catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static void update(IResourcePack rp2) {
        Object[] astring = ResUtils.collectFiles(rp2, "mcpatcher/cit/", ".properties", null);
        Map map = CustomItems.makeAutoImageProperties(rp2);
        if (map.size() > 0) {
            Set set = map.keySet();
            Object[] astring1 = set.toArray(new String[set.size()]);
            astring = (String[])Config.addObjectsToArray(astring, astring1);
        }
        Arrays.sort(astring);
        List list = CustomItems.makePropertyList(itemProperties);
        List list1 = CustomItems.makePropertyList(enchantmentProperties);
        int i2 = 0;
        while (i2 < astring.length) {
            block13: {
                Object s2 = astring[i2];
                Config.dbg("CustomItems: " + (String)s2);
                try {
                    CustomItemProperties customitemproperties = null;
                    if (map.containsKey(s2)) {
                        customitemproperties = (CustomItemProperties)map.get(s2);
                    }
                    if (customitemproperties == null) {
                        ResourceLocation resourcelocation = new ResourceLocation((String)s2);
                        InputStream inputstream = rp2.getInputStream(resourcelocation);
                        if (inputstream == null) {
                            Config.warn("CustomItems file not found: " + (String)s2);
                            break block13;
                        }
                        PropertiesOrdered properties = new PropertiesOrdered();
                        properties.load(inputstream);
                        inputstream.close();
                        customitemproperties = new CustomItemProperties(properties, (String)s2);
                    }
                    if (customitemproperties.isValid((String)s2)) {
                        CustomItems.addToItemList(customitemproperties, list);
                        CustomItems.addToEnchantmentList(customitemproperties, list1);
                    }
                }
                catch (FileNotFoundException var11) {
                    Config.warn("CustomItems file not found: " + (String)s2);
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            ++i2;
        }
        itemProperties = CustomItems.propertyListToArray(list);
        enchantmentProperties = CustomItems.propertyListToArray(list1);
        Comparator comparator = CustomItems.getPropertiesComparator();
        int j2 = 0;
        while (j2 < itemProperties.length) {
            CustomItemProperties[] acustomitemproperties = itemProperties[j2];
            if (acustomitemproperties != null) {
                Arrays.sort(acustomitemproperties, comparator);
            }
            ++j2;
        }
        int k2 = 0;
        while (k2 < enchantmentProperties.length) {
            CustomItemProperties[] acustomitemproperties1 = enchantmentProperties[k2];
            if (acustomitemproperties1 != null) {
                Arrays.sort(acustomitemproperties1, comparator);
            }
            ++k2;
        }
    }

    private static Comparator getPropertiesComparator() {
        Comparator comparator = new Comparator(){

            public int compare(Object o1, Object o2) {
                CustomItemProperties customitemproperties = (CustomItemProperties)o1;
                CustomItemProperties customitemproperties1 = (CustomItemProperties)o2;
                return customitemproperties.layer != customitemproperties1.layer ? customitemproperties.layer - customitemproperties1.layer : (customitemproperties.weight != customitemproperties1.weight ? customitemproperties1.weight - customitemproperties.weight : (!customitemproperties.basePath.equals(customitemproperties1.basePath) ? customitemproperties.basePath.compareTo(customitemproperties1.basePath) : customitemproperties.name.compareTo(customitemproperties1.name)));
            }
        };
        return comparator;
    }

    public static void updateIcons(TextureMap textureMap) {
        for (CustomItemProperties customitemproperties : CustomItems.getAllProperties()) {
            customitemproperties.updateIcons(textureMap);
        }
    }

    public static void loadModels(ModelBakery modelBakery) {
        for (CustomItemProperties customitemproperties : CustomItems.getAllProperties()) {
            customitemproperties.loadModels(modelBakery);
        }
    }

    public static void updateModels() {
        for (CustomItemProperties customitemproperties : CustomItems.getAllProperties()) {
            if (customitemproperties.type != 1) continue;
            TextureMap texturemap = Minecraft.getMinecraft().getTextureMapBlocks();
            customitemproperties.updateModelTexture(texturemap, itemModelGenerator);
            customitemproperties.updateModelsFull();
        }
    }

    private static List<CustomItemProperties> getAllProperties() {
        ArrayList<CustomItemProperties> list = new ArrayList<CustomItemProperties>();
        CustomItems.addAll(itemProperties, list);
        CustomItems.addAll(enchantmentProperties, list);
        return list;
    }

    private static void addAll(CustomItemProperties[][] cipsArr, List<CustomItemProperties> list) {
        if (cipsArr != null) {
            int i2 = 0;
            while (i2 < cipsArr.length) {
                CustomItemProperties[] acustomitemproperties = cipsArr[i2];
                if (acustomitemproperties != null) {
                    int j2 = 0;
                    while (j2 < acustomitemproperties.length) {
                        CustomItemProperties customitemproperties = acustomitemproperties[j2];
                        if (customitemproperties != null) {
                            list.add(customitemproperties);
                        }
                        ++j2;
                    }
                }
                ++i2;
            }
        }
    }

    private static Map makeAutoImageProperties(IResourcePack rp2) {
        HashMap map = new HashMap();
        map.putAll(CustomItems.makePotionImageProperties(rp2, TYPE_POTION_NORMAL, Item.getIdFromItem(Items.potionitem)));
        map.putAll(CustomItems.makePotionImageProperties(rp2, TYPE_POTION_SPLASH, Item.getIdFromItem(Items.potionitem)));
        map.putAll(CustomItems.makePotionImageProperties(rp2, TYPE_POTION_LINGER, Item.getIdFromItem(Items.potionitem)));
        return map;
    }

    private static Map makePotionImageProperties(IResourcePack rp2, String type, int itemId) {
        HashMap<String, CustomItemProperties> map = new HashMap<String, CustomItemProperties>();
        String s2 = String.valueOf(type) + "/";
        String[] astring = new String[]{"mcpatcher/cit/potion/" + s2, "mcpatcher/cit/Potion/" + s2};
        String[] astring1 = new String[]{".png"};
        String[] astring2 = ResUtils.collectFiles(rp2, astring, astring1);
        int i2 = 0;
        while (i2 < astring2.length) {
            String s1 = astring2[i2];
            String name = StrUtils.removePrefixSuffix(s1, astring, astring1);
            Properties properties = CustomItems.makePotionProperties(name, type, itemId, s1);
            if (properties != null) {
                String s3 = String.valueOf(StrUtils.removeSuffix(s1, astring1)) + ".properties";
                CustomItemProperties customitemproperties = new CustomItemProperties(properties, s3);
                map.put(s3, customitemproperties);
            }
            ++i2;
        }
        return map;
    }

    private static Properties makePotionProperties(String name, String type, int itemId, String path) {
        if (StrUtils.endsWith(name, new String[]{"_n", "_s"})) {
            return null;
        }
        if (name.equals("empty") && type.equals(TYPE_POTION_NORMAL)) {
            itemId = Item.getIdFromItem(Items.glass_bottle);
            PropertiesOrdered properties = new PropertiesOrdered();
            ((Properties)properties).put("type", "item");
            ((Properties)properties).put("items", "" + itemId);
            return properties;
        }
        int[] aint = (int[])CustomItems.getMapPotionIds().get(name);
        if (aint == null) {
            Config.warn("Potion not found for image: " + path);
            return null;
        }
        StringBuffer stringbuffer = new StringBuffer();
        int i2 = 0;
        while (i2 < aint.length) {
            int j2 = aint[i2];
            if (type.equals(TYPE_POTION_SPLASH)) {
                j2 |= 0x4000;
            }
            if (i2 > 0) {
                stringbuffer.append(" ");
            }
            stringbuffer.append(j2);
            ++i2;
        }
        int k2 = 16447;
        if (name.equals("water") || name.equals("mundane")) {
            k2 |= 0x40;
        }
        PropertiesOrdered properties1 = new PropertiesOrdered();
        ((Properties)properties1).put("type", "item");
        ((Properties)properties1).put("items", "" + itemId);
        ((Properties)properties1).put("damage", stringbuffer.toString());
        ((Properties)properties1).put("damageMask", "" + k2);
        if (type.equals(TYPE_POTION_SPLASH)) {
            ((Properties)properties1).put(KEY_TEXTURE_SPLASH, name);
        } else {
            ((Properties)properties1).put(KEY_TEXTURE_DRINKABLE, name);
        }
        return properties1;
    }

    private static Map getMapPotionIds() {
        if (mapPotionIds == null) {
            mapPotionIds = new LinkedHashMap();
            mapPotionIds.put("water", CustomItems.getPotionId(0, 0));
            mapPotionIds.put("awkward", CustomItems.getPotionId(0, 1));
            mapPotionIds.put("thick", CustomItems.getPotionId(0, 2));
            mapPotionIds.put("potent", CustomItems.getPotionId(0, 3));
            mapPotionIds.put("regeneration", CustomItems.getPotionIds(1));
            mapPotionIds.put("movespeed", CustomItems.getPotionIds(2));
            mapPotionIds.put("fireresistance", CustomItems.getPotionIds(3));
            mapPotionIds.put("poison", CustomItems.getPotionIds(4));
            mapPotionIds.put("heal", CustomItems.getPotionIds(5));
            mapPotionIds.put("nightvision", CustomItems.getPotionIds(6));
            mapPotionIds.put("clear", CustomItems.getPotionId(7, 0));
            mapPotionIds.put("bungling", CustomItems.getPotionId(7, 1));
            mapPotionIds.put("charming", CustomItems.getPotionId(7, 2));
            mapPotionIds.put("rank", CustomItems.getPotionId(7, 3));
            mapPotionIds.put("weakness", CustomItems.getPotionIds(8));
            mapPotionIds.put("damageboost", CustomItems.getPotionIds(9));
            mapPotionIds.put("moveslowdown", CustomItems.getPotionIds(10));
            mapPotionIds.put("leaping", CustomItems.getPotionIds(11));
            mapPotionIds.put("harm", CustomItems.getPotionIds(12));
            mapPotionIds.put("waterbreathing", CustomItems.getPotionIds(13));
            mapPotionIds.put("invisibility", CustomItems.getPotionIds(14));
            mapPotionIds.put("thin", CustomItems.getPotionId(15, 0));
            mapPotionIds.put("debonair", CustomItems.getPotionId(15, 1));
            mapPotionIds.put("sparkling", CustomItems.getPotionId(15, 2));
            mapPotionIds.put("stinky", CustomItems.getPotionId(15, 3));
            mapPotionIds.put("mundane", CustomItems.getPotionId(0, 4));
            mapPotionIds.put("speed", mapPotionIds.get("movespeed"));
            mapPotionIds.put("fire_resistance", mapPotionIds.get("fireresistance"));
            mapPotionIds.put("instant_health", mapPotionIds.get("heal"));
            mapPotionIds.put("night_vision", mapPotionIds.get("nightvision"));
            mapPotionIds.put("strength", mapPotionIds.get("damageboost"));
            mapPotionIds.put("slowness", mapPotionIds.get("moveslowdown"));
            mapPotionIds.put("instant_damage", mapPotionIds.get("harm"));
            mapPotionIds.put("water_breathing", mapPotionIds.get("waterbreathing"));
        }
        return mapPotionIds;
    }

    private static int[] getPotionIds(int baseId) {
        return new int[]{baseId, baseId + 16, baseId + 32, baseId + 48};
    }

    private static int[] getPotionId(int baseId, int subId) {
        return new int[]{baseId + subId * 16};
    }

    private static int getPotionNameDamage(String name) {
        String s2 = "potion." + name;
        Potion[] apotion = Potion.potionTypes;
        int i2 = 0;
        while (i2 < apotion.length) {
            String s1;
            Potion potion = apotion[i2];
            if (potion != null && s2.equals(s1 = potion.getName())) {
                return potion.getId();
            }
            ++i2;
        }
        return -1;
    }

    private static List makePropertyList(CustomItemProperties[][] propsArr) {
        ArrayList<ArrayList<CustomItemProperties>> list = new ArrayList<ArrayList<CustomItemProperties>>();
        if (propsArr != null) {
            int i2 = 0;
            while (i2 < propsArr.length) {
                CustomItemProperties[] acustomitemproperties = propsArr[i2];
                ArrayList<CustomItemProperties> list1 = null;
                if (acustomitemproperties != null) {
                    list1 = new ArrayList<CustomItemProperties>(Arrays.asList(acustomitemproperties));
                }
                list.add(list1);
                ++i2;
            }
        }
        return list;
    }

    private static CustomItemProperties[][] propertyListToArray(List lists) {
        CustomItemProperties[][] acustomitemproperties = new CustomItemProperties[lists.size()][];
        int i2 = 0;
        while (i2 < lists.size()) {
            List list = (List)lists.get(i2);
            if (list != null) {
                CustomItemProperties[] acustomitemproperties1 = list.toArray(new CustomItemProperties[list.size()]);
                Arrays.sort(acustomitemproperties1, new CustomItemsComparator());
                acustomitemproperties[i2] = acustomitemproperties1;
            }
            ++i2;
        }
        return acustomitemproperties;
    }

    private static void addToItemList(CustomItemProperties cp2, List itemList) {
        if (cp2.items != null) {
            int i2 = 0;
            while (i2 < cp2.items.length) {
                int j2 = cp2.items[i2];
                if (j2 <= 0) {
                    Config.warn("Invalid item ID: " + j2);
                } else {
                    CustomItems.addToList(cp2, itemList, j2);
                }
                ++i2;
            }
        }
    }

    private static void addToEnchantmentList(CustomItemProperties cp2, List enchantmentList) {
        if (cp2.type == 2 && cp2.enchantmentIds != null) {
            int i2 = 0;
            while (i2 < 256) {
                if (cp2.enchantmentIds.isInRange(i2)) {
                    CustomItems.addToList(cp2, enchantmentList, i2);
                }
                ++i2;
            }
        }
    }

    private static void addToList(CustomItemProperties cp2, List lists, int id2) {
        while (id2 >= lists.size()) {
            lists.add(null);
        }
        ArrayList<CustomItemProperties> list = (ArrayList<CustomItemProperties>)lists.get(id2);
        if (list == null) {
            list = new ArrayList<CustomItemProperties>();
            list.set(id2, (CustomItemProperties)((Object)list));
        }
        list.add(cp2);
    }

    public static IBakedModel getCustomItemModel(ItemStack itemStack, IBakedModel model, ResourceLocation modelLocation, boolean fullModel) {
        if (!fullModel && model.isGui3d()) {
            return model;
        }
        if (itemProperties == null) {
            return model;
        }
        CustomItemProperties customitemproperties = CustomItems.getCustomItemProperties(itemStack, 1);
        if (customitemproperties == null) {
            return model;
        }
        IBakedModel ibakedmodel = customitemproperties.getBakedModel(modelLocation, fullModel);
        return ibakedmodel != null ? ibakedmodel : model;
    }

    public static boolean bindCustomArmorTexture(ItemStack itemStack, int layer, String overlay) {
        if (itemProperties == null) {
            return false;
        }
        ResourceLocation resourcelocation = CustomItems.getCustomArmorLocation(itemStack, layer, overlay);
        if (resourcelocation == null) {
            return false;
        }
        Config.getTextureManager().bindTexture(resourcelocation);
        return true;
    }

    private static ResourceLocation getCustomArmorLocation(ItemStack itemStack, int layer, String overlay) {
        String s1;
        ResourceLocation resourcelocation;
        CustomItemProperties customitemproperties = CustomItems.getCustomItemProperties(itemStack, 3);
        if (customitemproperties == null) {
            return null;
        }
        if (customitemproperties.mapTextureLocations == null) {
            return customitemproperties.textureLocation;
        }
        Item item = itemStack.getItem();
        if (!(item instanceof ItemArmor)) {
            return null;
        }
        ItemArmor itemarmor = (ItemArmor)item;
        String s2 = itemarmor.getArmorMaterial().getName();
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("texture.");
        stringbuffer.append(s2);
        stringbuffer.append("_layer_");
        stringbuffer.append(layer);
        if (overlay != null) {
            stringbuffer.append("_");
            stringbuffer.append(overlay);
        }
        return (resourcelocation = (ResourceLocation)customitemproperties.mapTextureLocations.get(s1 = stringbuffer.toString())) == null ? customitemproperties.textureLocation : resourcelocation;
    }

    private static CustomItemProperties getCustomItemProperties(ItemStack itemStack, int type) {
        CustomItemProperties[] acustomitemproperties;
        if (itemProperties == null) {
            return null;
        }
        if (itemStack == null) {
            return null;
        }
        Item item = itemStack.getItem();
        int i2 = Item.getIdFromItem(item);
        if (i2 >= 0 && i2 < itemProperties.length && (acustomitemproperties = itemProperties[i2]) != null) {
            int j2 = 0;
            while (j2 < acustomitemproperties.length) {
                CustomItemProperties customitemproperties = acustomitemproperties[j2];
                if (customitemproperties.type == type && CustomItems.matchesProperties(customitemproperties, itemStack, null)) {
                    return customitemproperties;
                }
                ++j2;
            }
        }
        return null;
    }

    private static boolean matchesProperties(CustomItemProperties cip, ItemStack itemStack, int[][] enchantmentIdLevels) {
        Item item = itemStack.getItem();
        if (cip.damage != null) {
            int i2 = itemStack.getItemDamage();
            if (cip.damageMask != 0) {
                i2 &= cip.damageMask;
            }
            if (cip.damagePercent) {
                int j2 = item.getMaxDamage();
                i2 = (int)((double)(i2 * 100) / (double)j2);
            }
            if (!cip.damage.isInRange(i2)) {
                return false;
            }
        }
        if (cip.stackSize != null && !cip.stackSize.isInRange(itemStack.stackSize)) {
            return false;
        }
        int[][] aint = enchantmentIdLevels;
        if (cip.enchantmentIds != null) {
            if (enchantmentIdLevels == null) {
                aint = CustomItems.getEnchantmentIdLevels(itemStack);
            }
            boolean flag = false;
            int k2 = 0;
            while (k2 < aint.length) {
                int l2 = aint[k2][0];
                if (cip.enchantmentIds.isInRange(l2)) {
                    flag = true;
                    break;
                }
                ++k2;
            }
            if (!flag) {
                return false;
            }
        }
        if (cip.enchantmentLevels != null) {
            if (aint == null) {
                aint = CustomItems.getEnchantmentIdLevels(itemStack);
            }
            boolean flag1 = false;
            int i1 = 0;
            while (i1 < aint.length) {
                int k1 = aint[i1][1];
                if (cip.enchantmentLevels.isInRange(k1)) {
                    flag1 = true;
                    break;
                }
                ++i1;
            }
            if (!flag1) {
                return false;
            }
        }
        if (cip.nbtTagValues != null) {
            NBTTagCompound nbttagcompound = itemStack.getTagCompound();
            int j1 = 0;
            while (j1 < cip.nbtTagValues.length) {
                NbtTagValue nbttagvalue = cip.nbtTagValues[j1];
                if (!nbttagvalue.matches(nbttagcompound)) {
                    return false;
                }
                ++j1;
            }
        }
        if (cip.hand != 0) {
            if (cip.hand == 1 && renderOffHand) {
                return false;
            }
            if (cip.hand == 2 && !renderOffHand) {
                return false;
            }
        }
        return true;
    }

    private static int[][] getEnchantmentIdLevels(ItemStack itemStack) {
        NBTTagList nbttaglist;
        Item item = itemStack.getItem();
        NBTTagList nBTTagList = nbttaglist = item == Items.enchanted_book ? Items.enchanted_book.getEnchantments(itemStack) : itemStack.getEnchantmentTagList();
        if (nbttaglist != null && nbttaglist.tagCount() > 0) {
            int[][] aint = new int[nbttaglist.tagCount()][2];
            int i2 = 0;
            while (i2 < nbttaglist.tagCount()) {
                NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i2);
                short j2 = nbttagcompound.getShort("id");
                short k2 = nbttagcompound.getShort("lvl");
                aint[i2][0] = j2;
                aint[i2][1] = k2;
                ++i2;
            }
            return aint;
        }
        return EMPTY_INT2_ARRAY;
    }

    public static boolean renderCustomEffect(RenderItem renderItem, ItemStack itemStack, IBakedModel model) {
        if (enchantmentProperties == null) {
            return false;
        }
        if (itemStack == null) {
            return false;
        }
        int[][] aint = CustomItems.getEnchantmentIdLevels(itemStack);
        if (aint.length <= 0) {
            return false;
        }
        HashSet<Integer> set = null;
        boolean flag = false;
        TextureManager texturemanager = Config.getTextureManager();
        int i2 = 0;
        while (i2 < aint.length) {
            CustomItemProperties[] acustomitemproperties;
            int j2 = aint[i2][0];
            if (j2 >= 0 && j2 < enchantmentProperties.length && (acustomitemproperties = enchantmentProperties[j2]) != null) {
                int k2 = 0;
                while (k2 < acustomitemproperties.length) {
                    CustomItemProperties customitemproperties = acustomitemproperties[k2];
                    if (set == null) {
                        set = new HashSet<Integer>();
                    }
                    if (set.add(j2) && CustomItems.matchesProperties(customitemproperties, itemStack, aint) && customitemproperties.textureLocation != null) {
                        texturemanager.bindTexture(customitemproperties.textureLocation);
                        float f2 = customitemproperties.getTextureWidth(texturemanager);
                        if (!flag) {
                            flag = true;
                            GlStateManager.depthMask(false);
                            GlStateManager.depthFunc(514);
                            GlStateManager.disableLighting();
                            GlStateManager.matrixMode(5890);
                        }
                        Blender.setupBlend(customitemproperties.blend, 1.0f);
                        GlStateManager.pushMatrix();
                        GlStateManager.scale(f2 / 2.0f, f2 / 2.0f, f2 / 2.0f);
                        float f1 = customitemproperties.speed * (float)(Minecraft.getSystemTime() % 3000L) / 3000.0f / 8.0f;
                        GlStateManager.translate(f1, 0.0f, 0.0f);
                        GlStateManager.rotate(customitemproperties.rotation, 0.0f, 0.0f, 1.0f);
                        renderItem.renderModel(model, -1);
                        GlStateManager.popMatrix();
                    }
                    ++k2;
                }
            }
            ++i2;
        }
        if (flag) {
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.matrixMode(5888);
            GlStateManager.enableLighting();
            GlStateManager.depthFunc(515);
            GlStateManager.depthMask(true);
            texturemanager.bindTexture(TextureMap.locationBlocksTexture);
        }
        return flag;
    }

    public static boolean renderCustomArmorEffect(EntityLivingBase entity, ItemStack itemStack, ModelBase model, float limbSwing, float prevLimbSwing, float partialTicks, float timeLimbSwing, float yaw, float pitch, float scale) {
        if (enchantmentProperties == null) {
            return false;
        }
        if (Config.isShaders() && Shaders.isShadowPass) {
            return false;
        }
        if (itemStack == null) {
            return false;
        }
        int[][] aint = CustomItems.getEnchantmentIdLevels(itemStack);
        if (aint.length <= 0) {
            return false;
        }
        HashSet<Integer> set = null;
        boolean flag = false;
        TextureManager texturemanager = Config.getTextureManager();
        int i2 = 0;
        while (i2 < aint.length) {
            CustomItemProperties[] acustomitemproperties;
            int j2 = aint[i2][0];
            if (j2 >= 0 && j2 < enchantmentProperties.length && (acustomitemproperties = enchantmentProperties[j2]) != null) {
                int k2 = 0;
                while (k2 < acustomitemproperties.length) {
                    CustomItemProperties customitemproperties = acustomitemproperties[k2];
                    if (set == null) {
                        set = new HashSet<Integer>();
                    }
                    if (set.add(j2) && CustomItems.matchesProperties(customitemproperties, itemStack, aint) && customitemproperties.textureLocation != null) {
                        texturemanager.bindTexture(customitemproperties.textureLocation);
                        float f2 = customitemproperties.getTextureWidth(texturemanager);
                        if (!flag) {
                            flag = true;
                            if (Config.isShaders()) {
                                ShadersRender.renderEnchantedGlintBegin();
                            }
                            GlStateManager.enableBlend();
                            GlStateManager.depthFunc(514);
                            GlStateManager.depthMask(false);
                        }
                        Blender.setupBlend(customitemproperties.blend, 1.0f);
                        GlStateManager.disableLighting();
                        GlStateManager.matrixMode(5890);
                        GlStateManager.loadIdentity();
                        GlStateManager.rotate(customitemproperties.rotation, 0.0f, 0.0f, 1.0f);
                        float f1 = f2 / 8.0f;
                        GlStateManager.scale(f1, f1 / 2.0f, f1);
                        float f22 = customitemproperties.speed * (float)(Minecraft.getSystemTime() % 3000L) / 3000.0f / 8.0f;
                        GlStateManager.translate(0.0f, f22, 0.0f);
                        GlStateManager.matrixMode(5888);
                        model.render(entity, limbSwing, prevLimbSwing, timeLimbSwing, yaw, pitch, scale);
                    }
                    ++k2;
                }
            }
            ++i2;
        }
        if (flag) {
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(5888);
            GlStateManager.enableLighting();
            GlStateManager.depthMask(true);
            GlStateManager.depthFunc(515);
            GlStateManager.disableBlend();
            if (Config.isShaders()) {
                ShadersRender.renderEnchantedGlintEnd();
            }
        }
        return flag;
    }

    public static boolean isUseGlint() {
        return useGlint;
    }
}

