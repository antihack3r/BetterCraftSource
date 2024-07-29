/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.optifine.DynamicLight;
import net.optifine.DynamicLightsMap;
import net.optifine.config.ConnectedParser;
import net.optifine.config.EntityClassLocator;
import net.optifine.config.IObjectLocator;
import net.optifine.config.ItemLocator;
import net.optifine.reflect.ReflectorForge;
import net.optifine.util.PropertiesOrdered;

public class DynamicLights {
    private static DynamicLightsMap mapDynamicLights = new DynamicLightsMap();
    private static Map<Class, Integer> mapEntityLightLevels = new HashMap<Class, Integer>();
    private static Map<Item, Integer> mapItemLightLevels = new HashMap<Item, Integer>();
    private static long timeUpdateMs = 0L;
    private static final double MAX_DIST = 7.5;
    private static final double MAX_DIST_SQ = 56.25;
    private static final int LIGHT_LEVEL_MAX = 15;
    private static final int LIGHT_LEVEL_FIRE = 15;
    private static final int LIGHT_LEVEL_BLAZE = 10;
    private static final int LIGHT_LEVEL_MAGMA_CUBE = 8;
    private static final int LIGHT_LEVEL_MAGMA_CUBE_CORE = 13;
    private static final int LIGHT_LEVEL_GLOWSTONE_DUST = 8;
    private static final int LIGHT_LEVEL_PRISMARINE_CRYSTALS = 8;
    private static boolean initialized;

    public static void entityAdded(Entity entityIn, RenderGlobal renderGlobal) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void entityRemoved(Entity entityIn, RenderGlobal renderGlobal) {
        DynamicLightsMap dynamicLightsMap = mapDynamicLights;
        synchronized (dynamicLightsMap) {
            DynamicLight dynamiclight = mapDynamicLights.remove(entityIn.getEntityId());
            if (dynamiclight != null) {
                dynamiclight.updateLitChunks(renderGlobal);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void update(RenderGlobal renderGlobal) {
        long i2 = System.currentTimeMillis();
        if (i2 >= timeUpdateMs + 50L) {
            timeUpdateMs = i2;
            if (!initialized) {
                DynamicLights.initialize();
            }
            DynamicLightsMap dynamicLightsMap = mapDynamicLights;
            synchronized (dynamicLightsMap) {
                DynamicLights.updateMapDynamicLights(renderGlobal);
                if (mapDynamicLights.size() > 0) {
                    List<DynamicLight> list = mapDynamicLights.valueList();
                    int j2 = 0;
                    while (j2 < list.size()) {
                        DynamicLight dynamiclight = list.get(j2);
                        dynamiclight.update(renderGlobal);
                        ++j2;
                    }
                }
            }
        }
    }

    private static void initialize() {
        initialized = true;
        mapEntityLightLevels.clear();
        mapItemLightLevels.clear();
        String[] astring = ReflectorForge.getForgeModIds();
        int i2 = 0;
        while (i2 < astring.length) {
            String s2 = astring[i2];
            try {
                ResourceLocation resourcelocation = new ResourceLocation(s2, "optifine/dynamic_lights.properties");
                InputStream inputstream = Config.getResourceStream(resourcelocation);
                DynamicLights.loadModConfiguration(inputstream, resourcelocation.toString(), s2);
            }
            catch (IOException iOException) {
                // empty catch block
            }
            ++i2;
        }
        if (mapEntityLightLevels.size() > 0) {
            Config.dbg("DynamicLights entities: " + mapEntityLightLevels.size());
        }
        if (mapItemLightLevels.size() > 0) {
            Config.dbg("DynamicLights items: " + mapItemLightLevels.size());
        }
    }

    private static void loadModConfiguration(InputStream in2, String path, String modId) {
        if (in2 != null) {
            try {
                PropertiesOrdered properties = new PropertiesOrdered();
                properties.load(in2);
                in2.close();
                Config.dbg("DynamicLights: Parsing " + path);
                ConnectedParser connectedparser = new ConnectedParser("DynamicLights");
                DynamicLights.loadModLightLevels(properties.getProperty("entities"), mapEntityLightLevels, new EntityClassLocator(), connectedparser, path, modId);
                DynamicLights.loadModLightLevels(properties.getProperty("items"), mapItemLightLevels, new ItemLocator(), connectedparser, path, modId);
            }
            catch (IOException var5) {
                Config.warn("DynamicLights: Error reading " + path);
            }
        }
    }

    private static void loadModLightLevels(String prop, Map mapLightLevels, IObjectLocator ol2, ConnectedParser cp2, String path, String modId) {
        if (prop != null) {
            String[] astring = Config.tokenize(prop, " ");
            int i2 = 0;
            while (i2 < astring.length) {
                String s2 = astring[i2];
                String[] astring1 = Config.tokenize(s2, ":");
                if (astring1.length != 2) {
                    cp2.warn("Invalid entry: " + s2 + ", in:" + path);
                } else {
                    String s1 = astring1[0];
                    String s22 = astring1[1];
                    String s3 = String.valueOf(modId) + ":" + s1;
                    ResourceLocation resourcelocation = new ResourceLocation(s3);
                    Object object = ol2.getObject(resourcelocation);
                    if (object == null) {
                        cp2.warn("Object not found: " + s3);
                    } else {
                        int j2 = cp2.parseInt(s22, -1);
                        if (j2 >= 0 && j2 <= 15) {
                            mapLightLevels.put(object, new Integer(j2));
                        } else {
                            cp2.warn("Invalid light level: " + s2);
                        }
                    }
                }
                ++i2;
            }
        }
    }

    private static void updateMapDynamicLights(RenderGlobal renderGlobal) {
        WorldClient world = renderGlobal.getWorld();
        if (world != null) {
            for (Entity entity : world.getLoadedEntityList()) {
                int i2 = DynamicLights.getLightLevel(entity);
                if (i2 > 0) {
                    int j2 = entity.getEntityId();
                    DynamicLight dynamiclight = mapDynamicLights.get(j2);
                    if (dynamiclight != null) continue;
                    dynamiclight = new DynamicLight(entity);
                    mapDynamicLights.put(j2, dynamiclight);
                    continue;
                }
                int k2 = entity.getEntityId();
                DynamicLight dynamiclight1 = mapDynamicLights.remove(k2);
                if (dynamiclight1 == null) continue;
                dynamiclight1.updateLitChunks(renderGlobal);
            }
        }
    }

    public static int getCombinedLight(BlockPos pos, int combinedLight) {
        double d0 = DynamicLights.getLightLevel(pos);
        combinedLight = DynamicLights.getCombinedLight(d0, combinedLight);
        return combinedLight;
    }

    public static int getCombinedLight(Entity entity, int combinedLight) {
        double d0 = DynamicLights.getLightLevel(entity);
        combinedLight = DynamicLights.getCombinedLight(d0, combinedLight);
        return combinedLight;
    }

    public static int getCombinedLight(double lightPlayer, int combinedLight) {
        int j2;
        int i2;
        if (lightPlayer > 0.0 && (i2 = (int)(lightPlayer * 16.0)) > (j2 = combinedLight & 0xFF)) {
            combinedLight &= 0xFFFFFF00;
            combinedLight |= i2;
        }
        return combinedLight;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static double getLightLevel(BlockPos pos) {
        double d0 = 0.0;
        DynamicLightsMap dynamicLightsMap = mapDynamicLights;
        synchronized (dynamicLightsMap) {
            List<DynamicLight> list = mapDynamicLights.valueList();
            int i2 = list.size();
            int j2 = 0;
            while (j2 < i2) {
                DynamicLight dynamiclight = list.get(j2);
                int k2 = dynamiclight.getLastLightLevel();
                if (k2 > 0) {
                    double d8;
                    double d9;
                    double d10;
                    double d1 = dynamiclight.getLastPosX();
                    double d2 = dynamiclight.getLastPosY();
                    double d3 = dynamiclight.getLastPosZ();
                    double d4 = (double)pos.getX() - d1;
                    double d5 = (double)pos.getY() - d2;
                    double d6 = (double)pos.getZ() - d3;
                    double d7 = d4 * d4 + d5 * d5 + d6 * d6;
                    if (dynamiclight.isUnderwater() && !Config.isClearWater()) {
                        k2 = Config.limit(k2 - 2, 0, 15);
                        d7 *= 2.0;
                    }
                    if (d7 <= 56.25 && (d10 = (d9 = 1.0 - (d8 = Math.sqrt(d7)) / 7.5) * (double)k2) > d0) {
                        d0 = d10;
                    }
                }
                ++j2;
            }
        }
        double d11 = Config.limit(d0, 0.0, 15.0);
        return d11;
    }

    public static int getLightLevel(ItemStack itemStack) {
        ItemBlock itemblock;
        Block block;
        if (itemStack == null) {
            return 0;
        }
        Item item = itemStack.getItem();
        if (item instanceof ItemBlock && (block = (itemblock = (ItemBlock)item).getBlock()) != null) {
            return block.getLightValue();
        }
        if (item == Items.lava_bucket) {
            return Blocks.lava.getLightValue();
        }
        if (item != Items.blaze_rod && item != Items.blaze_powder) {
            Integer integer;
            if (item == Items.glowstone_dust) {
                return 8;
            }
            if (item == Items.prismarine_crystals) {
                return 8;
            }
            if (item == Items.magma_cream) {
                return 8;
            }
            if (item == Items.nether_star) {
                return Blocks.beacon.getLightValue() / 2;
            }
            if (!mapItemLightLevels.isEmpty() && (integer = mapItemLightLevels.get(item)) != null) {
                return integer;
            }
            return 0;
        }
        return 10;
    }

    public static int getLightLevel(Entity entity) {
        EntityCreeper entitycreeper;
        Integer integer;
        EntityPlayer entityplayer;
        if (entity == Config.getMinecraft().getRenderViewEntity() && !Config.isDynamicHandLight()) {
            return 0;
        }
        if (entity instanceof EntityPlayer && (entityplayer = (EntityPlayer)entity).isSpectator()) {
            return 0;
        }
        if (entity.isBurning()) {
            return 15;
        }
        if (!mapEntityLightLevels.isEmpty() && (integer = mapEntityLightLevels.get(entity.getClass())) != null) {
            return integer;
        }
        if (entity instanceof EntityFireball) {
            return 15;
        }
        if (entity instanceof EntityTNTPrimed) {
            return 15;
        }
        if (entity instanceof EntityBlaze) {
            EntityBlaze entityblaze = (EntityBlaze)entity;
            return entityblaze.func_70845_n() ? 15 : 10;
        }
        if (entity instanceof EntityMagmaCube) {
            EntityMagmaCube entitymagmacube = (EntityMagmaCube)entity;
            return (double)entitymagmacube.squishFactor > 0.6 ? 13 : 8;
        }
        if (entity instanceof EntityCreeper && (double)(entitycreeper = (EntityCreeper)entity).getCreeperFlashIntensity(0.0f) > 0.001) {
            return 15;
        }
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase entitylivingbase = (EntityLivingBase)entity;
            ItemStack itemstack2 = entitylivingbase.getHeldItem();
            int i2 = DynamicLights.getLightLevel(itemstack2);
            ItemStack itemstack1 = entitylivingbase.getEquipmentInSlot(4);
            int j2 = DynamicLights.getLightLevel(itemstack1);
            return Math.max(i2, j2);
        }
        if (entity instanceof EntityItem) {
            EntityItem entityitem = (EntityItem)entity;
            ItemStack itemstack = DynamicLights.getItemStack(entityitem);
            return DynamicLights.getLightLevel(itemstack);
        }
        return 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void removeLights(RenderGlobal renderGlobal) {
        DynamicLightsMap dynamicLightsMap = mapDynamicLights;
        synchronized (dynamicLightsMap) {
            List<DynamicLight> list = mapDynamicLights.valueList();
            int i2 = 0;
            while (i2 < list.size()) {
                DynamicLight dynamiclight = list.get(i2);
                dynamiclight.updateLitChunks(renderGlobal);
                ++i2;
            }
            mapDynamicLights.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void clear() {
        DynamicLightsMap dynamicLightsMap = mapDynamicLights;
        synchronized (dynamicLightsMap) {
            mapDynamicLights.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static int getCount() {
        DynamicLightsMap dynamicLightsMap = mapDynamicLights;
        synchronized (dynamicLightsMap) {
            return mapDynamicLights.size();
        }
    }

    public static ItemStack getItemStack(EntityItem entityItem) {
        ItemStack itemstack = entityItem.getDataWatcher().getWatchableObjectItemStack(10);
        return itemstack;
    }
}

