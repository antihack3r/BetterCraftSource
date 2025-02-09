/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.optifine.IRandomEntity;
import net.optifine.RandomEntity;
import net.optifine.RandomEntityProperties;
import net.optifine.RandomTileEntity;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorRaw;
import net.optifine.util.IntegratedServerUtils;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.ResUtils;
import net.optifine.util.StrUtils;

public class RandomEntities {
    private static Map<String, RandomEntityProperties> mapProperties = new HashMap<String, RandomEntityProperties>();
    private static boolean active = false;
    private static RenderGlobal renderGlobal;
    private static RandomEntity randomEntity;
    private static TileEntityRendererDispatcher tileEntityRendererDispatcher;
    private static RandomTileEntity randomTileEntity;
    private static boolean working;
    public static final String SUFFIX_PNG = ".png";
    public static final String SUFFIX_PROPERTIES = ".properties";
    public static final String PREFIX_TEXTURES_ENTITY = "textures/entity/";
    public static final String PREFIX_TEXTURES_PAINTING = "textures/painting/";
    public static final String PREFIX_TEXTURES = "textures/";
    public static final String PREFIX_OPTIFINE_RANDOM = "optifine/random/";
    public static final String PREFIX_MCPATCHER_MOB = "mcpatcher/mob/";
    private static final String[] DEPENDANT_SUFFIXES;
    private static final String PREFIX_DYNAMIC_TEXTURE_HORSE = "horse/";
    private static final String[] HORSE_TEXTURES;
    private static final String[] HORSE_TEXTURES_ABBR;

    static {
        randomEntity = new RandomEntity();
        randomTileEntity = new RandomTileEntity();
        working = false;
        DEPENDANT_SUFFIXES = new String[]{"_armor", "_eyes", "_exploding", "_shooting", "_fur", "_eyes", "_invulnerable", "_angry", "_tame", "_collar"};
        HORSE_TEXTURES = (String[])ReflectorRaw.getFieldValue(null, EntityHorse.class, String[].class, 2);
        HORSE_TEXTURES_ABBR = (String[])ReflectorRaw.getFieldValue(null, EntityHorse.class, String[].class, 3);
    }

    public static void entityLoaded(Entity entity, World world) {
        if (world != null) {
            DataWatcher datawatcher = entity.getDataWatcher();
            datawatcher.spawnPosition = entity.getPosition();
            datawatcher.spawnBiome = world.getBiomeGenForCoords(datawatcher.spawnPosition);
            UUID uuid = entity.getUniqueID();
            if (entity instanceof EntityVillager) {
                RandomEntities.updateEntityVillager(uuid, (EntityVillager)entity);
            }
        }
    }

    public static void entityUnloaded(Entity entity, World world) {
    }

    private static void updateEntityVillager(UUID uuid, EntityVillager ev2) {
        Entity entity = IntegratedServerUtils.getEntity(uuid);
        if (entity instanceof EntityVillager) {
            EntityVillager entityvillager = (EntityVillager)entity;
            int i2 = entityvillager.getProfession();
            ev2.setProfession(i2);
            int j2 = Reflector.getFieldValueInt(entityvillager, Reflector.EntityVillager_careerId, 0);
            Reflector.setFieldValueInt(ev2, Reflector.EntityVillager_careerId, j2);
            int k2 = Reflector.getFieldValueInt(entityvillager, Reflector.EntityVillager_careerLevel, 0);
            Reflector.setFieldValueInt(ev2, Reflector.EntityVillager_careerLevel, k2);
        }
    }

    public static void worldChanged(World oldWorld, World newWorld) {
        if (newWorld != null) {
            List<Entity> list = newWorld.getLoadedEntityList();
            int i2 = 0;
            while (i2 < list.size()) {
                Entity entity = list.get(i2);
                RandomEntities.entityLoaded(entity, newWorld);
                ++i2;
            }
        }
        randomEntity.setEntity(null);
        randomTileEntity.setTileEntity(null);
    }

    public static ResourceLocation getTextureLocation(ResourceLocation loc) {
        ResourceLocation name;
        if (!active) {
            return loc;
        }
        if (working) {
            return loc;
        }
        try {
            working = true;
            IRandomEntity irandomentity = RandomEntities.getRandomEntityRendered();
            if (irandomentity != null) {
                ResourceLocation resourcelocation1;
                String s2 = loc.getResourcePath();
                if (s2.startsWith(PREFIX_DYNAMIC_TEXTURE_HORSE)) {
                    s2 = RandomEntities.getHorseTexturePath(s2, PREFIX_DYNAMIC_TEXTURE_HORSE.length());
                }
                if (!s2.startsWith(PREFIX_TEXTURES_ENTITY) && !s2.startsWith(PREFIX_TEXTURES_PAINTING)) {
                    ResourceLocation resourcelocation2;
                    ResourceLocation resourceLocation = resourcelocation2 = loc;
                    return resourceLocation;
                }
                RandomEntityProperties randomentityproperties = mapProperties.get(s2);
                if (randomentityproperties == null) {
                    ResourceLocation resourcelocation3;
                    ResourceLocation resourceLocation = resourcelocation3 = loc;
                    return resourceLocation;
                }
                ResourceLocation resourceLocation = resourcelocation1 = randomentityproperties.getTextureLocation(loc, irandomentity);
                return resourceLocation;
            }
            name = loc;
        }
        finally {
            working = false;
        }
        return name;
    }

    private static String getHorseTexturePath(String path, int pos) {
        if (HORSE_TEXTURES != null && HORSE_TEXTURES_ABBR != null) {
            int i2 = 0;
            while (i2 < HORSE_TEXTURES_ABBR.length) {
                String s2 = HORSE_TEXTURES_ABBR[i2];
                if (path.startsWith(s2, pos)) {
                    return HORSE_TEXTURES[i2];
                }
                ++i2;
            }
            return path;
        }
        return path;
    }

    private static IRandomEntity getRandomEntityRendered() {
        TileEntity tileentity;
        if (RandomEntities.renderGlobal.renderedEntity != null) {
            randomEntity.setEntity(RandomEntities.renderGlobal.renderedEntity);
            return randomEntity;
        }
        if (RandomEntities.tileEntityRendererDispatcher.tileEntityRendered != null && (tileentity = RandomEntities.tileEntityRendererDispatcher.tileEntityRendered).getWorld() != null) {
            randomTileEntity.setTileEntity(tileentity);
            return randomTileEntity;
        }
        return null;
    }

    private static RandomEntityProperties makeProperties(ResourceLocation loc, boolean mcpatcher) {
        RandomEntityProperties randomentityproperties;
        String s2 = loc.getResourcePath();
        ResourceLocation resourcelocation = RandomEntities.getLocationProperties(loc, mcpatcher);
        if (resourcelocation != null && (randomentityproperties = RandomEntities.parseProperties(resourcelocation, loc)) != null) {
            return randomentityproperties;
        }
        ResourceLocation[] aresourcelocation = RandomEntities.getLocationsVariants(loc, mcpatcher);
        return aresourcelocation == null ? null : new RandomEntityProperties(s2, aresourcelocation);
    }

    private static RandomEntityProperties parseProperties(ResourceLocation propLoc, ResourceLocation resLoc) {
        InputStream inputstream;
        String s2;
        block4: {
            s2 = propLoc.getResourcePath();
            RandomEntities.dbg(String.valueOf(resLoc.getResourcePath()) + ", properties: " + s2);
            inputstream = Config.getResourceStream(propLoc);
            if (inputstream != null) break block4;
            RandomEntities.warn("Properties not found: " + s2);
            return null;
        }
        try {
            PropertiesOrdered properties = new PropertiesOrdered();
            properties.load(inputstream);
            inputstream.close();
            RandomEntityProperties randomentityproperties = new RandomEntityProperties(properties, s2, resLoc);
            return !randomentityproperties.isValid(s2) ? null : randomentityproperties;
        }
        catch (FileNotFoundException var6) {
            RandomEntities.warn("File not found: " + resLoc.getResourcePath());
            return null;
        }
        catch (IOException ioexception) {
            ioexception.printStackTrace();
            return null;
        }
    }

    private static ResourceLocation getLocationProperties(ResourceLocation loc, boolean mcpatcher) {
        String s1;
        String s2;
        String s3;
        ResourceLocation resourcelocation = RandomEntities.getLocationRandom(loc, mcpatcher);
        if (resourcelocation == null) {
            return null;
        }
        String s4 = resourcelocation.getResourceDomain();
        ResourceLocation resourcelocation1 = new ResourceLocation(s4, s3 = String.valueOf(s2 = StrUtils.removeSuffix(s1 = resourcelocation.getResourcePath(), SUFFIX_PNG)) + SUFFIX_PROPERTIES);
        if (Config.hasResource(resourcelocation1)) {
            return resourcelocation1;
        }
        String s42 = RandomEntities.getParentTexturePath(s2);
        if (s42 == null) {
            return null;
        }
        ResourceLocation resourcelocation2 = new ResourceLocation(s4, String.valueOf(s42) + SUFFIX_PROPERTIES);
        return Config.hasResource(resourcelocation2) ? resourcelocation2 : null;
    }

    protected static ResourceLocation getLocationRandom(ResourceLocation loc, boolean mcpatcher) {
        String s2 = loc.getResourceDomain();
        String s1 = loc.getResourcePath();
        String s22 = PREFIX_TEXTURES;
        String s3 = PREFIX_OPTIFINE_RANDOM;
        if (mcpatcher) {
            s22 = PREFIX_TEXTURES_ENTITY;
            s3 = PREFIX_MCPATCHER_MOB;
        }
        if (!s1.startsWith(s22)) {
            return null;
        }
        String s4 = StrUtils.replacePrefix(s1, s22, s3);
        return new ResourceLocation(s2, s4);
    }

    private static String getPathBase(String pathRandom) {
        return pathRandom.startsWith(PREFIX_OPTIFINE_RANDOM) ? StrUtils.replacePrefix(pathRandom, PREFIX_OPTIFINE_RANDOM, PREFIX_TEXTURES) : (pathRandom.startsWith(PREFIX_MCPATCHER_MOB) ? StrUtils.replacePrefix(pathRandom, PREFIX_MCPATCHER_MOB, PREFIX_TEXTURES_ENTITY) : null);
    }

    protected static ResourceLocation getLocationIndexed(ResourceLocation loc, int index) {
        if (loc == null) {
            return null;
        }
        String s2 = loc.getResourcePath();
        int i2 = s2.lastIndexOf(46);
        if (i2 < 0) {
            return null;
        }
        String s1 = s2.substring(0, i2);
        String s22 = s2.substring(i2);
        String s3 = String.valueOf(s1) + index + s22;
        ResourceLocation resourcelocation = new ResourceLocation(loc.getResourceDomain(), s3);
        return resourcelocation;
    }

    private static String getParentTexturePath(String path) {
        int i2 = 0;
        while (i2 < DEPENDANT_SUFFIXES.length) {
            String s2 = DEPENDANT_SUFFIXES[i2];
            if (path.endsWith(s2)) {
                String s1 = StrUtils.removeSuffix(path, s2);
                return s1;
            }
            ++i2;
        }
        return null;
    }

    private static ResourceLocation[] getLocationsVariants(ResourceLocation loc, boolean mcpatcher) {
        ArrayList<ResourceLocation> list = new ArrayList<ResourceLocation>();
        list.add(loc);
        ResourceLocation resourcelocation = RandomEntities.getLocationRandom(loc, mcpatcher);
        if (resourcelocation == null) {
            return null;
        }
        int i2 = 1;
        while (i2 < list.size() + 10) {
            int j2 = i2 + 1;
            ResourceLocation resourcelocation1 = RandomEntities.getLocationIndexed(resourcelocation, j2);
            if (Config.hasResource(resourcelocation1)) {
                list.add(resourcelocation1);
            }
            ++i2;
        }
        if (list.size() <= 1) {
            return null;
        }
        ResourceLocation[] aresourcelocation = list.toArray(new ResourceLocation[list.size()]);
        RandomEntities.dbg(String.valueOf(loc.getResourcePath()) + ", variants: " + aresourcelocation.length);
        return aresourcelocation;
    }

    public static void update() {
        mapProperties.clear();
        active = false;
        if (Config.isRandomEntities()) {
            RandomEntities.initialize();
        }
    }

    private static void initialize() {
        renderGlobal = Config.getRenderGlobal();
        tileEntityRendererDispatcher = TileEntityRendererDispatcher.instance;
        String[] astring = new String[]{PREFIX_OPTIFINE_RANDOM, PREFIX_MCPATCHER_MOB};
        String[] astring1 = new String[]{SUFFIX_PNG, SUFFIX_PROPERTIES};
        String[] astring2 = ResUtils.collectFiles(astring, astring1);
        HashSet<String> set = new HashSet<String>();
        int i2 = 0;
        while (i2 < astring2.length) {
            String s2 = astring2[i2];
            s2 = StrUtils.removeSuffix(s2, astring1);
            s2 = StrUtils.trimTrailing(s2, "0123456789");
            String s1 = RandomEntities.getPathBase(s2 = String.valueOf(s2) + SUFFIX_PNG);
            if (!set.contains(s1)) {
                RandomEntityProperties randomentityproperties;
                set.add(s1);
                ResourceLocation resourcelocation = new ResourceLocation(s1);
                if (Config.hasResource(resourcelocation) && (randomentityproperties = mapProperties.get(s1)) == null) {
                    randomentityproperties = RandomEntities.makeProperties(resourcelocation, false);
                    if (randomentityproperties == null) {
                        randomentityproperties = RandomEntities.makeProperties(resourcelocation, true);
                    }
                    if (randomentityproperties != null) {
                        mapProperties.put(s1, randomentityproperties);
                    }
                }
            }
            ++i2;
        }
        active = !mapProperties.isEmpty();
    }

    public static void dbg(String str) {
        Config.dbg("RandomEntities: " + str);
    }

    public static void warn(String str) {
        Config.warn("RandomEntities: " + str);
    }
}

