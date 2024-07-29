/*
 * Decompiled with CFR 0.152.
 */
package wdl;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wdl.WDL;
import wdl.WDLPluginChannels;
import wdl.api.IEntityAdder;
import wdl.api.ISpecialEntityHandler;
import wdl.api.IWDLMod;
import wdl.api.WDLApi;

public class EntityUtils {
    private static final Logger logger = LogManager.getLogger();
    public static final Map<String, Class<?>> stringToClassMapping;
    public static final Map<Class<?>, String> classToStringMapping;

    static {
        try {
            Map mappingSTC = null;
            Map mappingCTS = null;
            Field[] fieldArray = EntityList.class.getDeclaredFields();
            int n2 = fieldArray.length;
            int n3 = 0;
            while (n3 < n2) {
                Field field = fieldArray[n3];
                if (field.getType().equals(Map.class)) {
                    Map temp;
                    field.setAccessible(true);
                    Map m2 = (Map)field.get(null);
                    Map.Entry e2 = (Map.Entry)m2.entrySet().toArray()[0];
                    if (e2.getKey() instanceof String && e2.getValue() instanceof Class) {
                        mappingSTC = temp = m2;
                    }
                    if (e2.getKey() instanceof Class && e2.getValue() instanceof String) {
                        mappingCTS = temp = m2;
                    }
                }
                ++n3;
            }
            if (mappingSTC == null) {
                throw new Exception("WDL: Failed to find stringToClassMapping!");
            }
            if (mappingCTS == null) {
                throw new Exception("WDL: Failed to find classToStringMapping!");
            }
            stringToClassMapping = mappingSTC;
            classToStringMapping = mappingCTS;
        }
        catch (Exception e3) {
            Minecraft.getMinecraft().crashed(new CrashReport("World Downloader Mod: failed to set up entity ranges!", e3));
            throw new Error("World Downloader Mod: failed to set up entity ranges!", e3);
        }
    }

    public static Set<String> getEntityTypes() {
        HashSet<String> returned = new HashSet<String>();
        for (Map.Entry<String, Class<?>> entry : stringToClassMapping.entrySet()) {
            if (Modifier.isAbstract(entry.getValue().getModifiers())) continue;
            returned.add(entry.getKey());
        }
        for (WDLApi.ModInfo modInfo : WDLApi.getImplementingExtensions(ISpecialEntityHandler.class)) {
            returned.addAll(((ISpecialEntityHandler)modInfo.mod).getSpecialEntities().values());
        }
        return returned;
    }

    public static Multimap<String, String> getEntitiesByGroup() {
        HashMultimap<String, String> returned = HashMultimap.create();
        Set<String> types = EntityUtils.getEntityTypes();
        for (String type : types) {
            returned.put(EntityUtils.getEntityGroup(type), type);
        }
        return returned;
    }

    public static int getDefaultEntityRange(String type) {
        if (type == null) {
            return -1;
        }
        for (WDLApi.ModInfo<IEntityAdder> modInfo : WDLApi.getImplementingExtensions(IEntityAdder.class)) {
            List<String> names = ((IEntityAdder)modInfo.mod).getModEntities();
            if (names == null) {
                logger.warn(String.valueOf(modInfo.toString()) + " returned null for getModEntities()!");
                continue;
            }
            if (!names.contains(type)) continue;
            return ((IEntityAdder)modInfo.mod).getDefaultEntityTrackDistance(type);
        }
        for (WDLApi.ModInfo<IWDLMod> modInfo : WDLApi.getImplementingExtensions(ISpecialEntityHandler.class)) {
            Multimap<String, String> specialEntities = ((ISpecialEntityHandler)modInfo.mod).getSpecialEntities();
            if (specialEntities == null) {
                logger.warn(String.valueOf(modInfo.toString()) + " returned null for getSpecialEntities()!");
                continue;
            }
            for (Map.Entry<String, String> e2 : specialEntities.entries()) {
                if (!e2.getValue().equals(type)) continue;
                int trackDistance = ((ISpecialEntityHandler)modInfo.mod).getSpecialEntityTrackDistance(e2.getValue());
                if (trackDistance < 0) {
                    trackDistance = EntityUtils.getMostLikelyEntityTrackDistance(e2.getKey());
                }
                return trackDistance;
            }
        }
        return EntityUtils.getVanillaEntityRange(stringToClassMapping.get(type));
    }

    public static int getEntityTrackDistance(Entity e2) {
        return EntityUtils.getEntityTrackDistance(EntityUtils.getTrackDistanceMode(), e2);
    }

    public static int getEntityTrackDistance(String mode, Entity e2) {
        if ("default".equals(mode)) {
            return EntityUtils.getMostLikelyEntityTrackDistance(e2);
        }
        if ("server".equals(mode)) {
            int serverDistance = WDLPluginChannels.getEntityRange(EntityUtils.getEntityType(e2));
            if (serverDistance < 0) {
                int mostLikelyRange = EntityUtils.getMostLikelyEntityTrackDistance(e2);
                if (mostLikelyRange < 0) {
                    return WDLPluginChannels.getEntityRange(EntityList.getEntityString(e2));
                }
                return mostLikelyRange;
            }
            return serverDistance;
        }
        if ("user".equals(mode)) {
            String prop = WDL.worldProps.getProperty("Entity." + EntityUtils.getEntityType(e2) + ".TrackDistance", "-1");
            int value = Integer.valueOf(prop);
            if (value == -1) {
                return EntityUtils.getEntityTrackDistance("server", e2);
            }
            return value;
        }
        throw new IllegalArgumentException("Mode is not a valid mode: " + mode);
    }

    public static int getEntityTrackDistance(String type) {
        return EntityUtils.getEntityTrackDistance(EntityUtils.getTrackDistanceMode(), type);
    }

    public static int getEntityTrackDistance(String mode, String type) {
        if ("default".equals(mode)) {
            int mostLikelyDistance = EntityUtils.getMostLikelyEntityTrackDistance(type);
            if (mostLikelyDistance < 0) {
                for (WDLApi.ModInfo<ISpecialEntityHandler> info : WDLApi.getImplementingExtensions(ISpecialEntityHandler.class)) {
                    Multimap<String, String> specialEntities = ((ISpecialEntityHandler)info.mod).getSpecialEntities();
                    for (Map.Entry<String, String> mapping : specialEntities.entries()) {
                        if (!type.equals(mapping.getValue())) continue;
                        return EntityUtils.getEntityTrackDistance(mode, mapping.getKey());
                    }
                }
            }
            return mostLikelyDistance;
        }
        if ("server".equals(mode)) {
            int serverDistance = WDLPluginChannels.getEntityRange(type);
            if (serverDistance < 0) {
                int mostLikelyDistance = EntityUtils.getMostLikelyEntityTrackDistance(type);
                if (mostLikelyDistance < 0) {
                    for (WDLApi.ModInfo<ISpecialEntityHandler> info : WDLApi.getImplementingExtensions(ISpecialEntityHandler.class)) {
                        Multimap<String, String> specialEntities = ((ISpecialEntityHandler)info.mod).getSpecialEntities();
                        for (Map.Entry<String, String> mapping : specialEntities.entries()) {
                            if (!type.equals(mapping.getValue())) continue;
                            return EntityUtils.getEntityTrackDistance(mode, mapping.getKey());
                        }
                    }
                } else {
                    return mostLikelyDistance;
                }
            }
            return serverDistance;
        }
        if ("user".equals(mode)) {
            String prop = WDL.worldProps.getProperty("Entity." + type + ".TrackDistance", "-1");
            int value = Integer.valueOf(prop);
            if (value == -1) {
                return EntityUtils.getEntityTrackDistance("server", type);
            }
            return value;
        }
        throw new IllegalArgumentException("Mode is not a valid mode: " + mode);
    }

    public static String getEntityGroup(String type) {
        if (type == null) {
            return null;
        }
        for (WDLApi.ModInfo<IEntityAdder> modInfo : WDLApi.getImplementingExtensions(IEntityAdder.class)) {
            List<String> names = ((IEntityAdder)modInfo.mod).getModEntities();
            if (names == null) {
                logger.warn(String.valueOf(modInfo.toString()) + " returned null for getModEntities()!");
                continue;
            }
            if (!names.contains(type)) continue;
            return ((IEntityAdder)modInfo.mod).getEntityCategory(type);
        }
        for (WDLApi.ModInfo<IWDLMod> modInfo : WDLApi.getImplementingExtensions(ISpecialEntityHandler.class)) {
            Multimap<String, String> specialEntities = ((ISpecialEntityHandler)modInfo.mod).getSpecialEntities();
            if (specialEntities == null) {
                logger.warn(String.valueOf(modInfo.toString()) + " returned null for getSpecialEntities()!");
                continue;
            }
            if (!specialEntities.containsValue(type)) continue;
            return ((ISpecialEntityHandler)modInfo.mod).getSpecialEntityCategory(type);
        }
        if (stringToClassMapping.containsKey(type)) {
            Class<?> clazz = stringToClassMapping.get(type);
            if (IMob.class.isAssignableFrom(clazz)) {
                return "Hostile";
            }
            if (IAnimals.class.isAssignableFrom(clazz)) {
                return "Passive";
            }
            return "Other";
        }
        return null;
    }

    public static boolean isEntityEnabled(Entity e2) {
        return EntityUtils.isEntityEnabled(EntityUtils.getEntityType(e2));
    }

    public static boolean isEntityEnabled(String type) {
        boolean groupEnabled = WDL.worldProps.getProperty("EntityGroup." + EntityUtils.getEntityGroup(type) + ".Enabled", "true").equals("true");
        boolean singleEnabled = WDL.worldProps.getProperty("Entity." + type + ".Enabled", "true").equals("true");
        return groupEnabled && singleEnabled;
    }

    public static String getEntityType(Entity e2) {
        String vanillaName = EntityList.getEntityString(e2);
        for (WDLApi.ModInfo<ISpecialEntityHandler> info : WDLApi.getImplementingExtensions(ISpecialEntityHandler.class)) {
            String specialName;
            if (!((ISpecialEntityHandler)info.mod).getSpecialEntities().containsKey(vanillaName) || (specialName = ((ISpecialEntityHandler)info.mod).getSpecialEntityName(e2)) == null) continue;
            return specialName;
        }
        return vanillaName;
    }

    public static int getMostLikelyEntityTrackDistance(Entity e2) {
        if (WDL.isSpigot()) {
            return EntityUtils.getDefaultSpigotEntityRange(e2.getClass());
        }
        return EntityUtils.getDefaultEntityRange(EntityUtils.getEntityType(e2));
    }

    public static int getMostLikelyEntityTrackDistance(String type) {
        if (WDL.isSpigot()) {
            Class<?> c2 = stringToClassMapping.get(type);
            if (c2 != null) {
                return EntityUtils.getDefaultSpigotEntityRange(c2);
            }
            return EntityUtils.getDefaultEntityRange(type);
        }
        return EntityUtils.getDefaultEntityRange(type);
    }

    public static int getVanillaEntityRange(String type) {
        return EntityUtils.getVanillaEntityRange(classToStringMapping.get(type));
    }

    public static String getTrackDistanceMode() {
        return WDL.worldProps.getProperty("Entity.TrackDistanceMode", "server");
    }

    public static int getVanillaEntityRange(Class<?> c2) {
        if (c2 == null) {
            return -1;
        }
        if (EntityFishHook.class.isAssignableFrom(c2) || EntityArrow.class.isAssignableFrom(c2) || EntitySmallFireball.class.isAssignableFrom(c2) || EntityFireball.class.isAssignableFrom(c2) || EntitySnowball.class.isAssignableFrom(c2) || EntityEnderPearl.class.isAssignableFrom(c2) || EntityEnderEye.class.isAssignableFrom(c2) || EntityEgg.class.isAssignableFrom(c2) || EntityPotion.class.isAssignableFrom(c2) || EntityExpBottle.class.isAssignableFrom(c2) || EntityFireworkRocket.class.isAssignableFrom(c2) || EntityItem.class.isAssignableFrom(c2) || EntitySquid.class.isAssignableFrom(c2)) {
            return 64;
        }
        if (EntityMinecart.class.isAssignableFrom(c2) || EntityBoat.class.isAssignableFrom(c2) || EntityWither.class.isAssignableFrom(c2) || EntityBat.class.isAssignableFrom(c2) || IAnimals.class.isAssignableFrom(c2)) {
            return 80;
        }
        if (EntityDragon.class.isAssignableFrom(c2) || EntityTNTPrimed.class.isAssignableFrom(c2) || EntityFallingBlock.class.isAssignableFrom(c2) || EntityHanging.class.isAssignableFrom(c2) || EntityArmorStand.class.isAssignableFrom(c2) || EntityXPOrb.class.isAssignableFrom(c2)) {
            return 160;
        }
        if (EntityEnderCrystal.class.isAssignableFrom(c2)) {
            return 256;
        }
        return -1;
    }

    public static int getDefaultSpigotEntityRange(Class<?> c2) {
        int monsterRange = 48;
        int animalRange = 48;
        int miscRange = 32;
        int otherRange = 64;
        if (EntityMob.class.isAssignableFrom(c2) || EntitySlime.class.isAssignableFrom(c2)) {
            return 48;
        }
        if (EntityCreature.class.isAssignableFrom(c2) || EntityAmbientCreature.class.isAssignableFrom(c2)) {
            return 48;
        }
        if (EntityItemFrame.class.isAssignableFrom(c2) || EntityPainting.class.isAssignableFrom(c2) || EntityItem.class.isAssignableFrom(c2) || EntityXPOrb.class.isAssignableFrom(c2)) {
            return 32;
        }
        return 64;
    }
}

